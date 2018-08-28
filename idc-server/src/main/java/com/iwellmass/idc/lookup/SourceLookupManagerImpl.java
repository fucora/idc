package com.iwellmass.idc.lookup;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.dispatcher.common.DDCContext;
import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.context.SpringContext;
import com.iwellmass.dispatcher.common.dao.DdcTaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskUpdateHistoryMapper;
import com.iwellmass.dispatcher.common.model.DdcNode;
import com.iwellmass.dispatcher.common.model.DdcTask;
import com.iwellmass.dispatcher.common.model.DdcTaskExample;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteHistory;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteHistoryExample;
import com.iwellmass.dispatcher.common.model.DdcTaskUpdateHistory;
import com.iwellmass.dispatcher.thrift.model.CommandResult;
import com.iwellmass.dispatcher.thrift.model.TaskEntity;
import com.iwellmass.dispatcher.thrift.sdk.AgentService;
import com.iwellmass.idc.lookup.EventDriveScheduler;
import com.iwellmass.idc.lookup.SourceEvent;
import com.iwellmass.idc.lookup.SourceLookup;

public class SourceLookupManagerImpl implements EventDriveScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SourceLookupManagerImpl.class);

	private ScheduledExecutorService schExecutor = Executors.newSingleThreadScheduledExecutor();

	public Map<String, LookupTask> lookupMap = new ConcurrentHashMap<>();
	
	@Override
	public void register(DdcNode node) {
		LOGGER.info("注册 SourceLookup {}", node);
		for ( String className : node.getClassNames().split(",")) {
			SourceLookupRPC lookup = new SourceLookupRPC();
			lookup.node = node;
			lookup.taskClassName = className;
			lookupMap.computeIfAbsent(className, key -> {
				LookupTask task = new LookupTask();
				task.lookup = lookup;
				return task;
			});
		}
	}

	@Override
	public void scheduleOnSourceReady(Integer jobId, LocalDateTime loadDate) {
		DdcTaskMapper mapper = SpringContext.getApplicationContext().getBean(DdcTaskMapper.class);
		DdcTask ddcTask = mapper.selectByPrimaryKey(jobId);
		
		LookupTask lookupTask = lookupMap.computeIfAbsent(ddcTask.getClassName(), key -> {
			LookupTask task = new LookupTask();
			task.jobId = jobId + "";
			task.loadDate = loadDate;
			task.ddcTask = ddcTask;
			return task;
		});
		// 循环调度，给个延时让事务提交 
		schExecutor.schedule(() -> {
			schedule0(lookupTask);
		}, 1000 * 10, TimeUnit.MILLISECONDS);
	}

	private void schedule0(LookupTask task) {
		schExecutor.schedule(task, task.getInterval(), TimeUnit.MILLISECONDS);
	}
	
	public void fireSourceEvent(SourceEvent event) {
		ApplicationContext ctx = SpringContext.getApplicationContext();
		Scheduler scheduler = ctx.getBean(Scheduler.class);
		DdcTaskUpdateHistoryMapper taskUpdateMapper = ctx.getBean(DdcTaskUpdateHistoryMapper.class);

		int taskId = Integer.parseInt(event.getJobId());

		DdcTask task = selectByTaskIdAndAppId(DDCContext.DEFAULT_APP, taskId);

		JobDataMap map = new JobDataMap();
		JobKey jobKey = buildJobKey(task);
		map.put("triggerType", Constants.TASK_EXECUTE_TYPE_MANUAL);
		map.put("user", "admin");

		try {
			scheduler.triggerJob(jobKey, map);
			DdcTaskUpdateHistory record = new DdcTaskUpdateHistory();
			String user = "admin";
			record.setTaskId(taskId);
			record.setUpdateUser(user);
			record.setUpdateTime(new Date());
			record.setUpdateDetail(String.format("{%s} 执行任务！", "SourceLookup"));
			taskUpdateMapper.insertSelective(record);
		} catch (SchedulerException e) {
			LOGGER.error("任务失败，任务名称{}，错误信息{}", task.getTaskName(), e);
		} catch (Throwable e) {
			LOGGER.error("插入任务执行记录失败，错误信息：{}", e);
		}
	}

	private JobKey buildJobKey(DdcTask task) {

		JobKey jobKey = new JobKey(Constants.JOB_PREFIX + task.getTaskId(), task.getAppKey());
		return jobKey;
	}

	private DdcTask selectByTaskIdAndAppId(int appId, int taskId) {

		DdcTaskMapper taskMapper = SpringContext.getApplicationContext().getBean(DdcTaskMapper.class);

		DdcTaskExample taskExample = new DdcTaskExample();
		DdcTaskExample.Criteria taskCriteria = taskExample.createCriteria();
		taskCriteria.andAppIdEqualTo(appId);
		taskCriteria.andTaskIdEqualTo(taskId);
		List<DdcTask> ddcTaskList = taskMapper.selectByExample(taskExample);
		if (ddcTaskList == null || ddcTaskList.size() != 1) {
			throw new RuntimeException("查询task数据异常");
		}
		return ddcTaskList.get(0);
	}

	class LookupTask implements Runnable {

		private DdcTask ddcTask;
		private String jobId;
		private LocalDateTime loadDate;
		private SourceLookup lookup;
		
		@Override
		public void run() {
			// 检测是否停止
			if (this.isHalt()) {
				LOGGER.info("停止检测: {}", ddcTask.getClassName());
				return;
			}
			// 触发调度标识
			boolean fired = false;
			try {
				// A: 跨周期依赖 和  B:数据源依赖都要满足才能执行
				// check A
				if (!isPrevSuccessed()) {
					LOGGER.debug("前置任务未完成，等待调度 {} - {} ", ddcTask.getTaskId(), loadDate);
					return;
				} 
				// 停止检测
				if (lookup != null) {
					boolean test = lookup.lookup(jobId, loadDate);
					if (test) {
						SourceEvent event = new SourceEvent();
						event.setJobId(jobId);
						event.setLoadDate(loadDate);
						fireSourceEvent(event);
					}
				}
			} catch (Throwable e) {
				LOGGER.error("检测失败, ERROR: {}", e.getMessage(), e);
			} finally {
				if (!this.isHalt()) {
					if (fired) {
						LOGGER.info("任务 {} - {} 已触发", ddcTask.getTaskId(), loadDate);
					} else {
						// 继续调度
						schedule0(this);
					}
				} else {
					LOGGER.info("停止检测: {}", ddcTask.getClassName());
				}
			}
		}
		
		private boolean isPrevSuccessed() {
			// 不支持并发
			Instant instant = loadDate.atZone(ZoneId.systemDefault()).toInstant();
			Date prev = new Date(instant.toEpochMilli());
			DdcTaskExecuteHistoryMapper historyMapper = SpringContext.getApplicationContext().getBean(DdcTaskExecuteHistoryMapper.class);
			DdcTaskExecuteHistoryExample example = new DdcTaskExecuteHistoryExample();
			example.createCriteria()
				.andTaskIdEqualTo(ddcTask.getTaskId())
				.andNextFireTimeEqualTo(prev);
			List<DdcTaskExecuteHistory> list = historyMapper.selectByExample(example);
			
			if (list == null || list.size() == 0) {
				return true;
			}
			if (list.size() == 1) {
				DdcTaskExecuteHistory prevTask = list.get(0);
				return prevTask.getExecuteResult() == "";
			} else {
				throw new AppException("不允许相同周期的任务出现两个");
			}
		}

		public long getInterval() {
			return 5000;
		}

		public boolean isHalt() {
			return false;
		}
	}
	
	class SourceLookupRPC implements  SourceLookup {
		
		private DdcNode node;
		private String taskClassName;
		
		@Override
		public boolean lookup(String jobId, LocalDateTime loadDate) {
			
			JSONObject jo = new JSONObject();
			jo.put("jobId", jobId);
			jo.put("loadDate", loadDate.format(DateTimeFormatter.ISO_DATE_TIME));
			
			TaskEntity taskEntity = new TaskEntity();
			// public int appId; // required
			taskEntity.setAppId(1234567);
			// public String className; // required
			taskEntity.setClassName(taskClassName);
			// public int taskId; // required
			taskEntity.setTaskId(Integer.parseInt(jobId));
			// public long executeId; // required
			taskEntity.setExecuteId(1L);
			// public int dispatchCount; // required
			taskEntity.setDispatchCount(1);
			// public String executeBatchId; // required
			taskEntity.setExecuteBatchId(loadDate.format(DateTimeFormatter.BASIC_ISO_DATE));
			// public int threadCount; // required
			taskEntity.setThreadCount(1);
			// public boolean needRetry; // required
			taskEntity.setNeedRetry(false);
			taskEntity.setParameters(jo.toJSONString());
			
			try (TFramedTransport transport = new TFramedTransport(new TSocket(node.getNodeIp(), node.getNodePort(), Constants.TIME_OUT));) {
				TProtocol protocol = new TBinaryProtocol(transport);
				transport.open();
				AgentService.Client client = new AgentService.Client(protocol);
				// 每次派发更新派发时间

				CommandResult result = client.executeTask(taskEntity);
				if (result.succeed) {
					LOGGER.debug("执行成功");
					String flag = result.getMessage().trim();
					return Boolean.parseBoolean(flag);
				}
			} catch (Exception e) {
				LOGGER.error("RPC Exceptoion, but what ever :)");
			}
			return false;
		}
	}
	
	
	/**
	 * 记录任务历史
	 * @param status
	 * @param message
	 */
	private void insertDdcTaskExecuteStatus(String status, String message) {
		/*DdcTaskExecuteStatus ddcTaskExecuteStatus = new DdcTaskExecuteStatus();
		ddcTaskExecuteStatus.setTaskId(taskId);
		ddcTaskExecuteStatus.setExecuteId(executeId);
		ddcTaskExecuteStatus.setExecuteBatchId(executeBatchId);
		ddcTaskExecuteStatus.setWorkflowId(ddcTaskExecuteHistory != null ? ddcTaskExecuteHistory.getWorkflowId() : ddcSubtaskExecuteHistory.getWorkflowId());
		ddcTaskExecuteStatus.setWorkflowExecuteId(workflowExecuteId);
		ddcTaskExecuteStatus.setStatus(status);
		ddcTaskExecuteStatus.setMessage(message);
		ddcTaskExecuteStatus.setTimestamp(new Date());
		ddcTaskExecuteStatusMapper.insertSelective(ddcTaskExecuteStatus);*/
	}
}
