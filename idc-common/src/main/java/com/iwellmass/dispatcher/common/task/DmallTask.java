package com.iwellmass.dispatcher.common.task;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.support.json.JSONUtils;
import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.context.JVMContext;
import com.iwellmass.dispatcher.common.context.QuartzContext;
import com.iwellmass.dispatcher.common.context.SpringContext;
import com.iwellmass.dispatcher.common.dag.SchedulingEngine;
import com.iwellmass.dispatcher.common.dao.DdcApplicationMapper;
import com.iwellmass.dispatcher.common.dao.DdcNodeMapper;
import com.iwellmass.dispatcher.common.dao.DdcRunningTaskMapper;
import com.iwellmass.dispatcher.common.dao.DdcSubtaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskExecuteStatusMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskMapper;
import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.dispatcher.common.model.DdcApplication;
import com.iwellmass.dispatcher.common.model.DdcNode;
import com.iwellmass.dispatcher.common.model.DdcRunningTask;
import com.iwellmass.dispatcher.common.model.DdcRunningTaskExample;
import com.iwellmass.dispatcher.common.model.DdcSubtaskExecuteHistory;
import com.iwellmass.dispatcher.common.model.DdcTask;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteHistory;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteStatus;
import com.iwellmass.dispatcher.common.strategy.AbstractDispatchStrategy;
import com.iwellmass.dispatcher.common.strategy.DispatchStrategyFactory;
import com.iwellmass.dispatcher.common.utils.AlarmUtils;
import com.iwellmass.dispatcher.common.utils.DateUtils;
import com.iwellmass.dispatcher.common.utils.ExceptionUtils;
import com.iwellmass.dispatcher.thrift.bvo.TaskStatus;
import com.iwellmass.dispatcher.thrift.bvo.TaskTypeHelper;
import com.iwellmass.dispatcher.thrift.model.CommandResult;
import com.iwellmass.dispatcher.thrift.model.TaskEntity;
import com.iwellmass.dispatcher.thrift.sdk.AgentService;
import com.iwellmass.idc.lookup.EventDriveScheduler;

/**
 * 定时任务实现
 * @author duheng
 *
 */
public class DmallTask implements Job {
	
	private Logger logger = LoggerFactory.getLogger(DmallTask.class);
	
	private static ThreadLocal<Logger> loggers = new ThreadLocal<Logger>();

	private Integer taskId;
	
	// 业务日期
	private Long loadDate;
	
	private Long executeId;
	
	private String executeBatchId;
	
	//当前错误重试次数
	private int currentRetryTime;
	
	//手工or自动触发
	private int triggerType;
	
	private String user;
	
	private Long workflowExecuteId;
	
	private Integer workflowId;
	
	private DdcTaskMapper ddcTaskMapper;
	private DdcNodeMapper ddcNodeMapper;
	private DdcSubtaskExecuteHistoryMapper ddcSubtaskExecuteHistoryMapper;
	private DdcTaskExecuteHistoryMapper ddcTaskExecuteHistoryMapper;
	private DdcTaskExecuteStatusMapper ddcTaskExecuteStatusMapper;
	private DdcTaskExecuteHistory ddcTaskExecuteHistory;
	private DdcSubtaskExecuteHistory ddcSubtaskExecuteHistory;
	private SchedulingEngine dagEngine;
	private DdcRunningTaskMapper ddcRunningTaskMapper;
	private DdcApplicationMapper ddcApplicationMapper;
	private boolean isContinue;
	
	private JobExecutionContext context;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		this.context = context;
		logger.info(context.getJobDetail().getKey().getName() + "  开始运行...");
		
		ddcTaskMapper = SpringContext.getApplicationContext().getBean(DdcTaskMapper.class);
		ddcNodeMapper = SpringContext.getApplicationContext().getBean(DdcNodeMapper.class);
		ddcSubtaskExecuteHistoryMapper = SpringContext.getApplicationContext().getBean(DdcSubtaskExecuteHistoryMapper.class);
		ddcTaskExecuteHistoryMapper = SpringContext.getApplicationContext().getBean(DdcTaskExecuteHistoryMapper.class);
		ddcTaskExecuteStatusMapper = SpringContext.getApplicationContext().getBean(DdcTaskExecuteStatusMapper.class);
		dagEngine = SpringContext.getApplicationContext().getBean(SchedulingEngine.class);
		ddcRunningTaskMapper = SpringContext.getApplicationContext().getBean(DdcRunningTaskMapper.class);
		ddcApplicationMapper = SpringContext.getApplicationContext().getBean(DdcApplicationMapper.class);
		
		//将logger放入线程对象，保证后续打印的日志都所属同一线程
		loggers.set(logger);
		isContinue = false;
		
		// 保存到数据库
		doTask();
		
		if(isContinue) {			
			try {
				if(workflowId == null) {
					ExceptionUtils.dealErrorInfo("流程任务对应的流程定义为空，不能正确下发子任务，流程任务编号：{%d}", taskId);
				}
				List<Integer> subTaskIds = dagEngine.findStartTaskIds(workflowId);
				Scheduler scheduler = QuartzContext.getScheduler();
				for(Integer subTaskId : subTaskIds) {
					DdcTask subTask = ddcTaskMapper.selectByPrimaryKey(subTaskId);
					if(subTask == null) {
						ExceptionUtils.dealErrorInfo("子任务不存在，不能进行派发，子任务编号：{%d}", subTaskId);
					}

					JobDataMap map = new JobDataMap();
					map.put("taskId", subTaskId);
					map.put("executeBatchId", executeBatchId);
					map.put("workflowExecuteId", workflowExecuteId);
					map.put("workflowId", workflowId);
					map.put("loadDate", loadDate);

					try {
						JobKey jobKey = buildJobKey(subTask, executeId);

						if(!scheduler.checkExists(jobKey)) {
							TriggerKey triggerKey = buildTriggerKey(subTask, executeId);

							JobDetail job = buildJob(subTask, jobKey, map);
							Trigger trigger = buildTrigger(subTask, triggerKey, subTask.getWaitTime());

							scheduler.scheduleJob(job, trigger);
						}
					} catch(SchedulerException e) {
						ExceptionUtils.dealErrorInfo("Schedule流程子任务失败！当前任务编号：{%d}，下一步任务编号{%d}，执行编号：{%d}，执行批次编号：{%s}，错误信息：{%s}", taskId, subTaskId, executeId, executeBatchId, e.getMessage());						
					}
				}
			} catch(Throwable e) {
				if(ddcTaskExecuteHistory != null) {
					ddcTaskExecuteHistory.setCompleteTime(new Date());
					ddcTaskExecuteHistory.setExecuteResult(TaskStatus.FAILED);
					ddcTaskExecuteHistory.setExecuteResultDesc(e.getMessage());
					ddcTaskExecuteHistoryMapper.updateByPrimaryKeySelective(ddcTaskExecuteHistory);
				} 
			}
		}
	}
	
	/**
	 * 开始单个任务执行
	 * @return
	 */
	private void doTask() {

		try {
			
			DdcTask ddcTask = ddcTaskMapper.selectByPrimaryKey(taskId);
			
			// 非流程子任务
			if(ddcTask.getTaskType() != Constants.TASK_TYPE_SUBTASK) {
				// CRON 调度，计算业务日期
				if (triggerType == Constants.TASK_TRIGGER_TYPE_SYSTEM) {
					Date sdf = context.getScheduledFireTime();
					this.executeBatchId =  formatSimple(sdf == null ? new Date() : sdf);
					this.loadDate = sdf.getTime();
				} 
			}
			
			if (this.loadDate == null) {
				throw new JobExecutionException("任务" + taskId + "未指定业务日期");
			}
			
			if (this.executeBatchId == null) {
				this.executeBatchId = formatSimple(new Date());
			}

			if(ddcTask == null || ddcTask.getTaskStatus() == null || ddcTask.getTaskStatus() != Constants.TASK_STATUS_ENABLED) {
				AlarmUtils.sendAndRecordAlarm(Constants.ALARM_KEY_NOTASK, taskId, "任务编号" + taskId + "对应的任务不存在或者未启用，不能进行任务调度！");
				
				ExceptionUtils.dealErrorInfo("任务编号{%d}对应的任务不存在或者未启用，不能进行任务调度！", taskId);
			}

			//更新下一次执行时间
			if(context.getNextFireTime() != null) {
				DdcTask record = new DdcTask();
				record.setTaskId(taskId);
				record.setNextFireTime(context.getNextFireTime());
				ddcTaskMapper.updateByPrimaryKeySelective(record);				
			}
			
			DdcTaskExecuteHistory executeHistory = null;
			
			if(ddcTask.getTaskType() == Constants.TASK_TYPE_CRON || ddcTask.getTaskType() == Constants.TASK_TYPE_SIMPLE) { //定时任务或简单任务
				executeHistory = createDdcTaskExecuteHistory(ddcTask);
			} else if(ddcTask.getTaskType() == Constants.TASK_TYPE_SUBTASK) { //流程子任务
				createDdcSubtaskExecuteHistory(ddcTask);
			} else if(ddcTask.getTaskType() == Constants.TASK_TYPE_FLOW) { //流程任务
				executeHistory = createDdcTaskExecuteHistory(ddcTask);
				workflowExecuteId = executeId;
				workflowId = ddcTask.getWorkflowId();
				isContinue = true;
			}
			
			// TODO 这里检查是否需要等待触发执行
			if (executeHistory != null && isWaitable(ddcTask)) {
				EventDriveScheduler sourceLookupManager = SpringContext.getApplicationContext().getBean(EventDriveScheduler.class);
				// 更新DDC 任务为等待状态
				insertDdcTaskExecuteStatus(TaskStatus.WAITING, "任务等待");
				LocalDateTime loadDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(executeHistory.getShouldFireTime()), ZoneId.systemDefault());
				sourceLookupManager.scheduleOnSourceReady(ddcTask.getTaskId(), loadDate);
				return;
			}
			
			//是否允许并行执行
			if(ddcTask.getConcurrency() != null && !ddcTask.getConcurrency()) {
				DdcRunningTaskExample example = new DdcRunningTaskExample();
				DdcRunningTaskExample.Criteria criteria = example.createCriteria();
				criteria.andTaskIdEqualTo(taskId);
				List<DdcRunningTask> ddcRunningTasks = ddcRunningTaskMapper.selectByExample(example);
				if(ddcRunningTasks != null && ddcRunningTasks.size() > 0) {
					ExceptionUtils.dealErrorInfo("任务编号{%d}，有相同任务正在执行且任务不允许并行运行，本次调度无效！", taskId);	
				}
			}
			//生成当前任务
			DdcRunningTask ddcRunningTask = new DdcRunningTask();
			ddcRunningTask.setTaskId(taskId);
			ddcRunningTask.setExecuteId(executeId);
			ddcRunningTask.setExecuteBatchId(executeBatchId);
			ddcRunningTask.setWorkflowId(ddcTask.getTaskType() != Constants.TASK_TYPE_FLOW ? (workflowId != null ? workflowId : 0) : -1);
			ddcRunningTask.setWorkflowExecuteId(workflowExecuteId);
			ddcRunningTask.setDispatchTime(new Date());
			ddcRunningTask.setDispatchCount(ddcTaskExecuteHistory != null ? ddcTaskExecuteHistory.getDispatchCount() : ddcSubtaskExecuteHistory.getDispatchCount());
			ddcRunningTask.setTimeout(ddcTask.getTimeout());
			if(ddcTask.getTimeout() != null && ddcTask.getTimeout() > 0) {
				ddcRunningTask.setTimeoutTime(new Date(System.currentTimeMillis() + ddcTask.getTimeout() * 1000));				
			} else {
				ddcRunningTask.setTimeoutTime(new Date());
			}
			ddcRunningTask.setTimeoutRetry(ddcTask.getTimeoutRetry() ? 1 : 0);
			ddcRunningTask.setTimeoutRetryTimes(ddcTask.getTimeoutRetryTimes());
			ddcRunningTaskMapper.insertSelective(ddcRunningTask);
			//流程任务不需要派发
			if(ddcTask.getTaskType() != Constants.TASK_TYPE_FLOW) { 
				//派发任务
				dispatchTask(ddcTask);
			}
		} catch(Exception e) {
			logger.error("doTask出错，错误信息：{}", e);
			DdcRunningTaskExample example = new DdcRunningTaskExample();
			DdcRunningTaskExample.Criteria criteria = example.createCriteria();
			if(ddcTaskExecuteHistory != null) {
				ddcTaskExecuteHistory.setCompleteTime(new Date());
				ddcTaskExecuteHistory.setExecuteResult(TaskStatus.FAILED);
				ddcTaskExecuteHistory.setExecuteResultDesc(e.getMessage());
				ddcTaskExecuteHistoryMapper.updateByPrimaryKeySelective(ddcTaskExecuteHistory);		
				criteria.andExecuteIdEqualTo(ddcTaskExecuteHistory.getExecuteId()).andWorkflowIdEqualTo(0);
			} else if(ddcSubtaskExecuteHistory != null) {
				ddcSubtaskExecuteHistory.setCompleteTime(new Date());
				ddcSubtaskExecuteHistory.setExecuteResult(TaskStatus.FAILED);
				ddcSubtaskExecuteHistory.setExecuteResultDesc(e.getMessage());
				ddcSubtaskExecuteHistoryMapper.updateByPrimaryKeySelective(ddcSubtaskExecuteHistory);	
				//子任务失败，同时结束流程任务
				DdcTaskExecuteHistory finishOpt = new DdcTaskExecuteHistory();
				finishOpt.setExecuteId(ddcSubtaskExecuteHistory.getWorkflowExecuteId());
				finishOpt.setCompleteTime(new Date());
				finishOpt.setExecuteResult(TaskStatus.FAILED);
				finishOpt.setExecuteResultDesc(e.getMessage());
				ddcTaskExecuteHistoryMapper.updateByPrimaryKeySelective(finishOpt);
				criteria.andExecuteIdEqualTo(ddcSubtaskExecuteHistory.getExecuteId()).andWorkflowIdGreaterThan(0);
			}
			//操作详情
			if(executeId != null) {
				insertDdcTaskExecuteStatus(TaskStatus.FAILED, e.getMessage());	
				ddcRunningTaskMapper.deleteByExample(example);
				//删除关联的流程任务
				if(ddcSubtaskExecuteHistory != null) {
					example = new DdcRunningTaskExample();
					criteria = example.createCriteria();
					criteria.andExecuteIdEqualTo(ddcSubtaskExecuteHistory.getWorkflowExecuteId()).andWorkflowIdLessThan(0);
					ddcRunningTaskMapper.deleteByExample(example);
				}
			}
			//当前任务异常，后续不执行
			isContinue = false;
		}
	}

	/**
	 * 带有重试功能的任务派发
	 * @param ddcTask
	 * @throws JobExecutionException
	 */
	private void dispatchTask(DdcTask ddcTask) throws DDCException {
		
		String strategyType = "random";
		TaskEntity taskEntity = createTaskEntity(ddcTask, LocalDateTime.ofInstant(Instant.ofEpochMilli(this.loadDate), ZoneId.systemDefault()));
		List<DdcNode> nodes = getAvailableNodes(ddcTask);
		if(nodes==null || nodes.size()==0) {
			DdcApplication ddcApplication = ddcApplicationMapper.selectByPrimaryKey(ddcTask.getAppId());
			AlarmUtils.sendAndRecordAlarm(Constants.ALARM_KEY_NOCLIENT, taskId, "当前任务无可用执行实例，无法进行任务分派！任务名称：" + ddcTask.getTaskName() + "，应用名称：" + (ddcApplication != null ? ddcApplication.getAppName() : ddcTask.getAppId()));
			ExceptionUtils.dealErrorInfo("当前任务无可用执行实例，无法进行任务分派！任务编号：{%d}，任务名称：{%s}，应用编号：{%d}，应用名称：{%s}", taskId, ddcTask.getTaskName(), ddcTask.getAppId(), (ddcApplication != null ? ddcApplication.getAppName() : ddcTask.getAppId()));
		}
		
		List<DdcNode> selectedNodes = new ArrayList<DdcNode>();
		//获取配置的派发策略
		AbstractDispatchStrategy abstractDispatchStrategy = DispatchStrategyFactory.getDispatchStrategy(strategyType);
		if(abstractDispatchStrategy != null) {
			int nodesSize = nodes.size();
			for(int i = 0; i < nodesSize; i++) {
				//根据负载策略计算可执行节点
				DdcNode node = abstractDispatchStrategy.select(nodes, selectedNodes);
				if(node == null) {
					DdcApplication ddcApplication = ddcApplicationMapper.selectByPrimaryKey(ddcTask.getAppId());
					AlarmUtils.sendAndRecordAlarm(Constants.ALARM_KEY_NOCLIENT, taskId, "当前任务无可用执行实例，无法进行任务分派！任务名称：" + ddcTask.getTaskName() + "，应用名称：" + (ddcApplication != null ? ddcApplication.getAppName() : ddcTask.getAppId()));
					ExceptionUtils.dealErrorInfo("当前任务无可用执行实例，无法进行任务分派！任务编号：{%d}，任务名称：{%s}，应用编号：{%d}，应用名称：{%s}", taskId, ddcTask.getTaskName(), ddcTask.getAppId(), (ddcApplication != null ? ddcApplication.getAppName() : ddcTask.getAppId()));
				}
				for(int j = 0; j < Constants.RETRY_THRIFT; j++) {
					TTransport transport = null;
					//调用接口通知客户端具体的任务执行
					try {
						TSocket socket = new TSocket(node.getNodeIp(), node.getNodePort(), Constants.TIME_OUT);
				        transport = new TFramedTransport(socket);
						TProtocol protocol = new TBinaryProtocol(transport); 
						transport.open();
						AgentService.Client client = new AgentService.Client(protocol);
						//每次派发更新派发时间
						if(ddcTaskExecuteHistory != null) {
							ddcTaskExecuteHistory.setDispatchTime(new Date());								
						} else {
							ddcSubtaskExecuteHistory.setDispatchTime(new Date());
						}
						CommandResult result = client.executeTask(taskEntity);
						if(result.succeed) {
							//操作详情
							insertDdcTaskExecuteStatus(TaskStatus.DISPATCHED, "派发成功");
							return;
						} else if(!result.succeed && result.needRetry) {
							continue;
						} else {
							ExceptionUtils.dealErrorInfo("下发任务失败，且不需要重试！" + result.getMessage());
						}
					} catch (TException e) {
						logger.error("任务下发出错，任务编号：{}，执行IP：{}，执行端口：{}，错误信息：{}", taskId, node.getNodeIp(), node.getNodePort(), e.getMessage());
						insertDdcTaskExecuteStatus(TaskStatus.FAILED, String.format("任务下发出错，任务编号：%d，执行IP：%s，执行端口：%d，错误信息：%s", taskId, node.getNodeIp(), node.getNodePort(), e.getMessage()));
					} finally {
						if(transport != null) {
							transport.close();
						}
					}
				}
				selectedNodes.add(node);
			}
			
			StringBuffer nsb = new StringBuffer();
			for(DdcNode n : selectedNodes) {
				nsb.append(n.getNodeIp()).append(":").append(n.getNodePort()).append(" ");
			}
			ExceptionUtils.dealErrorInfo("任务下发失败，每个实例已重试{%d}次，下发实例列表：{%s}", Constants.RETRY_THRIFT, nsb.toString());
		} else {
			ExceptionUtils.dealErrorInfo("任务的分配策略无效，任务编号：{%d}，分配策略：{%s}", taskId, strategyType);
		}
	}

	private boolean isWaitable(DdcTask task) {
		return TaskTypeHelper.isDataSyncJob(TaskTypeHelper.contentTypeOf(task.getClassName()));
	}

	/**
	 * 获取可用的实例列表（3个心跳内有上报）
	 * @param ddcTask
	 * @return
	 */
	private List<DdcNode> getAvailableNodes(DdcTask ddcTask) {

			Date now = new Date(System.currentTimeMillis() - Constants.THREE_NODE_HEARTBEAT);
			DdcNode ddcNode = new DdcNode();
			ddcNode.setAppId(ddcTask.getAppId());
			ddcNode.setNodeStatus(Constants.ENABLED);
			ddcNode.setLastHbTime(now);
			ddcNode.setClassNames(ddcTask.getClassName());
			return ddcNodeMapper.selectAvailableNodes(ddcNode);
	}
	
	/**
	 * 创建ddcTaskExecuteHistory的结构
	 * @param ddcTask
	 * @return
	 * @throws JobExecutionException
	 */
	private DdcTaskExecuteHistory createDdcTaskExecuteHistory(DdcTask ddcTask) throws DDCException {
		//记录操作历史
		//验证当前编号的任务是否存在
		if(executeId != null) {
			ddcTaskExecuteHistory = ddcTaskExecuteHistoryMapper.selectByPrimaryKey(executeId);
			if(ddcTaskExecuteHistory != null) {
				//重置相关时间
				ddcTaskExecuteHistory.setFireTime(context.getFireTime());
				ddcTaskExecuteHistory.setNextFireTime(context.getNextFireTime());
				ddcTaskExecuteHistory.setShouldFireTime(context.getScheduledFireTime().getTime());
				ddcTaskExecuteHistory.setActualFireTime(context.getFireTime().getTime());
				ddcTaskExecuteHistory.setDispatchTime(new Date());	
				ddcTaskExecuteHistory.setReceiveTime(null);
				ddcTaskExecuteHistory.setStartTime(null);
				ddcTaskExecuteHistory.setDispatchCount(ddcTaskExecuteHistory.getDispatchCount() + 1);
				ddcTaskExecuteHistory.setDispatcherIp(JVMContext.getIp());
				ddcTaskExecuteHistory.setDispatcherPort(JVMContext.getPort());
				ddcTaskExecuteHistoryMapper.updateByPrimaryKey(ddcTaskExecuteHistory);
				return ddcTaskExecuteHistory;
			} else {
				ExceptionUtils.dealErrorInfo("系统错误：未找到执行编号{%d}在DDC_TASK_EXECUTE_HISTORY中的记录！", executeId);
				return null;
			}
		} else {
			//生成执行历史记录
			ddcTaskExecuteHistory = new DdcTaskExecuteHistory();
			ddcTaskExecuteHistory.setTaskId(taskId);
			ddcTaskExecuteHistory.setExecuteBatchId(executeBatchId);
			ddcTaskExecuteHistory.setWorkflowId(ddcTask.getWorkflowId());
			ddcTaskExecuteHistory.setTriggerType(triggerType);
			ddcTaskExecuteHistory.setFireTime(context.getFireTime());
			ddcTaskExecuteHistory.setNextFireTime(context.getNextFireTime());
			ddcTaskExecuteHistory.setShouldFireTime(context.getScheduledFireTime().getTime());
			ddcTaskExecuteHistory.setActualFireTime(context.getFireTime().getTime());
			ddcTaskExecuteHistory.setDispatchTime(new Date());	
			ddcTaskExecuteHistory.setDispatchCount(1);
			ddcTaskExecuteHistory.setDispatcherIp(JVMContext.getIp());
			ddcTaskExecuteHistory.setDispatcherPort(JVMContext.getPort());
			ddcTaskExecuteHistory.setExecuteUser(StringUtils.isNotEmpty(user) ? user : null);
			ddcTaskExecuteHistory.setTimeout(ddcTask.getTimeout());
			if(ddcTask.getTimeout() != null && ddcTask.getTimeout() > 0) {
				ddcTaskExecuteHistory.setTimeoutTime(new Date(System.currentTimeMillis() + ddcTask.getTimeout() * 1000));				
			} else {
				ddcTaskExecuteHistory.setTimeoutTime(new Date());
			}
			ddcTaskExecuteHistory.setTimeoutRetry(ddcTask.getTimeoutRetry() ? 1 : 0);
			ddcTaskExecuteHistory.setTimeoutRetryTimes(ddcTask.getTimeoutRetryTimes());
			ddcTaskExecuteHistoryMapper.insertSelective(ddcTaskExecuteHistory);
			executeId = ddcTaskExecuteHistory.getExecuteId();
			return ddcTaskExecuteHistory;
		}
	}
	
	/**
	 * 创建ddcSubtaskExecuteHistory的结构
	 * @param ddcTask
	 * @return
	 * @throws JobExecutionException
	 */
	private void createDdcSubtaskExecuteHistory(DdcTask ddcTask) throws DDCException {
		//记录操作历史
		//验证当前编号的任务是否存在
		if(executeId != null) {
			ddcSubtaskExecuteHistory = ddcSubtaskExecuteHistoryMapper.selectByPrimaryKey(executeId);
			if(ddcSubtaskExecuteHistory != null) {
				ddcSubtaskExecuteHistory.setFireTime(context.getFireTime());
				ddcSubtaskExecuteHistory.setShouldFireTime(context.getScheduledFireTime().getTime());
				ddcSubtaskExecuteHistory.setActualFireTime(context.getFireTime().getTime());
				//重置相关时间
				ddcSubtaskExecuteHistory.setDispatchTime(new Date());	
				ddcSubtaskExecuteHistory.setReceiveTime(null);
				ddcSubtaskExecuteHistory.setStartTime(null);
				ddcSubtaskExecuteHistory.setDispatchCount(ddcSubtaskExecuteHistory.getDispatchCount() + 1);
				ddcSubtaskExecuteHistory.setDispatcherIp(JVMContext.getIp());
				ddcSubtaskExecuteHistory.setDispatcherPort(JVMContext.getPort());
				ddcSubtaskExecuteHistoryMapper.updateByPrimaryKey(ddcSubtaskExecuteHistory);
			} else {
				ExceptionUtils.dealErrorInfo("系统错误：未找到执行编号{%d}在DDC_SUBTASK_EXECUTE_HISTORY中的记录！", executeId);
			}
		} else {
			//生成执行历史记录
			ddcSubtaskExecuteHistory = new DdcSubtaskExecuteHistory();
			ddcSubtaskExecuteHistory.setWorkflowExecuteId(workflowExecuteId);
			ddcSubtaskExecuteHistory.setTaskId(taskId);
			ddcSubtaskExecuteHistory.setExecuteBatchId(executeBatchId);
			ddcSubtaskExecuteHistory.setWorkflowId(workflowId);
			ddcSubtaskExecuteHistory.setTriggerType(triggerType);
			ddcSubtaskExecuteHistory.setFireTime(context.getFireTime());
			ddcSubtaskExecuteHistory.setShouldFireTime(context.getScheduledFireTime().getTime());
			ddcSubtaskExecuteHistory.setActualFireTime(context.getFireTime().getTime());
			ddcSubtaskExecuteHistory.setDispatchTime(new Date());	
			ddcSubtaskExecuteHistory.setDispatchCount(1);
			ddcSubtaskExecuteHistory.setDispatcherIp(JVMContext.getIp());
			ddcSubtaskExecuteHistory.setDispatcherPort(JVMContext.getPort());
			ddcSubtaskExecuteHistory.setTimeout(ddcTask.getTimeout());
			if(ddcTask.getTimeout() != null && ddcTask.getTimeout() > 0) {
				ddcSubtaskExecuteHistory.setTimeoutTime(new Date(System.currentTimeMillis() + ddcTask.getTimeout() * 1000));				
			} else {
				ddcSubtaskExecuteHistory.setTimeoutTime(new Date());
			}
			ddcSubtaskExecuteHistory.setTimeoutRetry(ddcTask.getTimeoutRetry() ? 1 : 0);
			ddcSubtaskExecuteHistory.setTimeoutRetryTimes(ddcTask.getTimeoutRetryTimes());
			ddcSubtaskExecuteHistoryMapper.insertSelective(ddcSubtaskExecuteHistory);
			executeId = ddcSubtaskExecuteHistory.getExecuteId();
		}
	}
	
	/**
	 * 记录任务历史
	 * @param status
	 * @param message
	 */
	private void insertDdcTaskExecuteStatus(String status, String message) {
		DdcTaskExecuteStatus ddcTaskExecuteStatus = new DdcTaskExecuteStatus();
		ddcTaskExecuteStatus.setTaskId(taskId);
		ddcTaskExecuteStatus.setExecuteId(executeId);
		ddcTaskExecuteStatus.setExecuteBatchId(executeBatchId);
		ddcTaskExecuteStatus.setWorkflowId(ddcTaskExecuteHistory != null ? ddcTaskExecuteHistory.getWorkflowId() : ddcSubtaskExecuteHistory.getWorkflowId());
		ddcTaskExecuteStatus.setWorkflowExecuteId(workflowExecuteId);
		ddcTaskExecuteStatus.setStatus(status);
		ddcTaskExecuteStatus.setMessage(message);
		ddcTaskExecuteStatus.setTimestamp(new Date());
		ddcTaskExecuteStatusMapper.insertSelective(ddcTaskExecuteStatus);
	}
	
	/**
	 * 创建TaskEntity实例
	 * @param ddcTask
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private TaskEntity createTaskEntity(DdcTask ddcTask, LocalDateTime loadDate) {
		//构造thrift传输对象
		TaskEntity taskEntity = new TaskEntity();
		taskEntity.setExecuteId(executeId);
		taskEntity.setExecuteBatchId(executeBatchId);
		taskEntity.setWorkflowExecuteId(workflowExecuteId != null ? workflowExecuteId : 0);
		taskEntity.setWorkflowId(workflowId != null ? workflowId : 0);
		taskEntity.setAppId(ddcTask.getAppId());
		taskEntity.setClassName(ddcTask.getClassName());
		taskEntity.setParameters(ddcTask.getParameters());
		taskEntity.setDataLimit(ddcTask.getDataLimit());
		taskEntity.setTaskId(ddcTask.getTaskId());
		taskEntity.setNeedRetry(ddcTask.getFailedRetry());
		taskEntity.setRetryTimes(ddcTask.getFailedRetryTimes());
		taskEntity.setCurrentRetryTime(currentRetryTime);
		taskEntity.setRetryInterval(ddcTask.getFailedRetryInterval());
		taskEntity.setThreadCount(ddcTask.getThreads());
		taskEntity.setDispatchCount(ddcTaskExecuteHistory != null ? ddcTaskExecuteHistory.getDispatchCount() : ddcSubtaskExecuteHistory.getDispatchCount());
		taskEntity.setFireTime(context.getScheduledFireTime().getTime());
		
		// idc 参数设置
		Map<String, Object> idcParameters = new HashMap<>();
		if (taskEntity.getParameters() != null) {
			try {
				Map<String, Object> args = (Map<String, Object>) JSONUtils.parse(taskEntity.getParameters());
				idcParameters.putAll(args);
			} catch (Throwable e) {
				logger.warn("解析任务参数时出错 jobId {}, batchId {}", ddcTask.getTaskId(), executeBatchId);
			}
		}
		// 任务 ID
		idcParameters.put("jobId", taskId);
		// 任务批次
		idcParameters.put("loadDate", loadDate.format(DateTimeFormatter.BASIC_ISO_DATE));
		taskEntity.setParameters(JSONUtils.toJSONString(idcParameters));
		return taskEntity;
	}
	
	/**
	 * 构造JobKey
	 * @param task
	 * @param executeBatchId
	 * @return
	 */
	private JobKey buildJobKey(DdcTask task, long executeId) {

		return new JobKey(Constants.JOB_PREFIX + task.getTaskId() + "_" + executeId, task.getAppKey());
	}
	
	/**
	 * 构造TriggerKey
	 * @param task
	 * @param executeBatchId
	 * @return
	 */
	private TriggerKey buildTriggerKey(DdcTask task, long executeId) {

		return new TriggerKey(Constants.TRIGGER_PREFIX + task.getTaskId() + "_" + executeId, task.getAppKey());
	}
	
	/**
     * 装配task
     *
     * @param task
     * @return
     */
    private JobDetail buildJob(DdcTask task, JobKey jobKey, JobDataMap map) {

    	Class<? extends DmallTask> jobClass = task.getConcurrency() ? DmallTask.class : DmallTaskDisallowConcurrent.class;

        return newJob(jobClass)
                .withIdentity(jobKey)
                .storeDurably(false).usingJobData(map)
                .build();
    }
    
    /**
     * 为task配置触发器
     *
     * @param task
     * @return
     */
    @SuppressWarnings("rawtypes")
	private Trigger buildTrigger(DdcTask task, TriggerKey triggerKey, long interval) {

        TriggerBuilder builder = newTrigger().withIdentity(triggerKey).withSchedule(simpleSchedule().withRepeatCount(0)); 
        if(interval > 0) {
        	Date now = new Date();
        	now.setTime(now.getTime() + interval * 1000);
        	builder = builder.startAt(now);
        } else {
        	builder = builder.startNow();
        }
        return builder.build();
    }

	public static void setLogger(Logger logger) {
		loggers.set(logger);
	}
	
	public static Logger getLogger() {
		return loggers.get();
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Long getExecuteId() {
		return executeId;
	}

	public void setExecuteId(Long executeId) {
		this.executeId = executeId;
	}

	public String getExecuteBatchId() {
		return executeBatchId;
	}

	public void setExecuteBatchId(String executeBatchId) {
		this.executeBatchId = executeBatchId;
	}

	public int getCurrentRetryTime() {
		return currentRetryTime;
	}

	public void setCurrentRetryTime(int currentRetryTime) {
		this.currentRetryTime = currentRetryTime;
	}

	public int getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Long getWorkflowExecuteId() {
		return workflowExecuteId;
	}

	public void setWorkflowExecuteId(Long workflowExecuteId) {
		this.workflowExecuteId = workflowExecuteId;
	}

	public Integer getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Integer workflowId) {
		this.workflowId = workflowId;
	}
	
	
	public Long getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(Long loadDate) {
		this.loadDate = loadDate;
	}

	private static final String formatSimple(Date date) {
		try {
			return DateUtils.formatDate(date, DateUtils.FORMAT_DATETIME_SIMPLE);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
}
