package com.iwellmass.idc.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.inject.Inject;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.ExecutionLog;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobInstanceType;
import com.iwellmass.idc.model.JobStatusEvent;
import com.iwellmass.idc.model.ScheduleStatus;
import com.iwellmass.idc.model.StartEvent;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.repo.ExecutionLogRepository;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.repo.JobRepository;

@Component
public class StdStatusManager implements JobStatusManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StdStatusManager.class);

	@Inject
	private JobRepository jobRepository;

	@Inject
	private JobInstanceRepository jobInstanceRepository;

	@Inject
	private ExecutionLogRepository jobLogRepository;
	
	private Scheduler scheduler;

	public StdStatusManager withScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
		return this;
	}

	public void fireJobComplete(CompleteEvent event) {
		// 更新实例状态
		JobInstance jobInstance = jobInstanceRepository.findOne(event.getInstanceId());
		jobInstance.setEndTime(event.getEndTime());
		jobInstance.setStatus(event.getFinalStatus());
		jobInstanceRepository.save(jobInstance);
		
		// 更新任务状态
		Job job = jobRepository.findOne(jobInstance.getTaskId(), jobInstance.getGroupId());
		job.setStatus(ScheduleStatus.ERROR);
		jobRepository.save(job);

		// 添加执行记录
		ExecutionLog log = new ExecutionLog();
		log.setInstanceId(event.getInstanceId());
		log.setMessage(event.getMessage());
		log.setTime(LocalDateTime.now());
		
		jobLogRepository.save(log);

		// 任务执行成功
		if (event.getFinalStatus() == JobInstanceStatus.FINISHED) {
			updateJobExecutionBarrier(jobInstance);
		} else {
			// 任务执行失败，更新任务状态
		}
	}

	private void updateJobExecutionBarrier(JobInstance instance) {
		// 删除依赖条目
		//jobExecutionBarrierRepository.deleteDependency(instance.getTaskId(), instance.getGroupId(), instance.getLoadDate());
	}
	
	
	private void resumeJob(JobInstance instance) {
		TriggerKey triggerKey = IDCPlugin.buildTriggerKey(instance.getType(), instance.getTaskId(), instance.getGroupId());
		try {
			
			scheduler.resetTriggerFromErrorState(triggerKey);
			
			scheduler.resumeTrigger(triggerKey);
		} catch (SchedulerException e) {
			LOGGER.error("恢复任务 {} 失败", instance, e);
		}
	}

	public void fireJobStart(StartEvent event) {
		// 更新实例状态
		JobInstance jobInstance = jobInstanceRepository.findOne(event.getInstanceId());
		jobInstance.setStartTime(event.getStartTime());
		jobInstance.setEndTime(null);
		jobInstance.setStatus(JobInstanceStatus.RUNNING);
		jobInstanceRepository.save(jobInstance);
		
		// 更新任务状态
		Job job = jobRepository.findOne(jobInstance.getTaskId(), jobInstance.getGroupId());
		job.setStatus(ScheduleStatus.NORMAL);
		jobRepository.save(job);

		// 添加执行记录
		ExecutionLog log = new ExecutionLog();
		log.setInstanceId(event.getInstanceId());
		log.setMessage(event.getMessage());
		log.setTime(LocalDateTime.now());
		jobLogRepository.save(log);
	}

	public void fireJobActived(JobStatusEvent event) {
		
		JobInstance jobInstance = jobInstanceRepository.findOne(event.getInstanceId());
		
		String taskId = jobInstance.getTaskId();
		String groupId = jobInstance.getGroupId();
		
		TriggerKey mainTriggerKey = IDCPlugin.buildTriggerKey(JobInstanceType.CRON, taskId, groupId);
		
		Instant loadDateInstant = jobInstance.getLoadDate().atZone(ZoneId.systemDefault()).toInstant();
		
		// 是否已被调度
		try {
			Trigger mainTrigger = scheduler.getTrigger(mainTriggerKey);
			Instant nextInstant = mainTrigger.getNextFireTime().toInstant();
			
			// 调度器已经调度过此任务, 补偿执行一次
			if (nextInstant.isAfter(loadDateInstant)) {
				Trigger trigger = TriggerBuilder.newTrigger()
						.withIdentity("", "idc.rescovery")
						.startNow()
						.withSchedule(SimpleScheduleBuilder.simpleSchedule())
						.forJob(mainTrigger.getJobKey())
						.build();
				scheduler.scheduleJob(trigger);
			} else {
				// TODO 调度器尚未调度到此任务，我们检查trigger 状态，然后复原他
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
		
		// 更新实例状态
		jobInstance.setStatus(JobInstanceStatus.RUNNING);
		jobInstanceRepository.save(jobInstance);
		
		// 更新任务状态
		Job job = jobRepository.findOne(jobInstance.getTaskId(), jobInstance.getGroupId());
		job.setStatus(ScheduleStatus.NORMAL);
		jobRepository.save(job);

		// 添加执行记录
		ExecutionLog log = new ExecutionLog();
		log.setInstanceId(event.getInstanceId());
		log.setMessage("激活任务");
		log.setTime(LocalDateTime.now());
		jobLogRepository.save(log);
		resumeJob(jobInstance);
	}

	public void fireJobBlocked(JobStatusEvent event) {
		// 重新计算 barrier 信息
		fireJobActived(event);
	}

//	 class MockLookup  {
//			
//			
//			private Scheduler scheduler;
//			private ScheduledExecutorService schExecutor = Executors.newSingleThreadScheduledExecutor();
//
//			public Map<String, LookupTask> lookupMap = new ConcurrentHashMap<>();
//			
//			
//			public void test() {
//			}
//			
//			
//
//			// 前置以来满足时，调用这个任务
//			public void scheduleOnSourceReady(JobKey jobKey, LocalDateTime loadDate) {
//				
//				
//				
//				DdcTaskMapper mapper = SpringContext.getApplicationContext().getBean(DdcTaskMapper.class);
//				DdcTask ddcTask = mapper.selectByPrimaryKey(jobId);
//				
//				LookupTask lookupTask = lookupMap.computeIfAbsent(ddcTask.getClassName(), key -> {
//					LookupTask task = new LookupTask();
//					task.jobId = jobId + "";
//					task.loadDate = loadDate;
//					task.ddcTask = ddcTask;
//					return task;
//				});
//				// 循环调度，给个延时让事务提交 
//				schExecutor.schedule(() -> {
//					schedule0(lookupTask);
//				}, 1000 * 10, TimeUnit.MILLISECONDS);
//			}
//
//			public void scheduleOnSourceReady(TriggerKey tk, LookupTask task) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			private void schedule0(LookupTask task) {
//				schExecutor.schedule(task, task.getInterval(), TimeUnit.MILLISECONDS);
//			}
//			
//			public void fireSourceEvent(SourceEvent event) {
//				ApplicationContext ctx = SpringContext.getApplicationContext();
//				Scheduler scheduler = ctx.getBean(Scheduler.class);
//				DdcTaskUpdateHistoryMapper taskUpdateMapper = ctx.getBean(DdcTaskUpdateHistoryMapper.class);
//
//				int taskId = Integer.parseInt(event.getJobId());
//
//				DdcTask task = selectByTaskIdAndAppId(DDCContext.DEFAULT_APP, taskId);
//
//				JobDataMap map = new JobDataMap();
//				JobKey jobKey = buildJobKey(task);
//				map.put("triggerType", Constants.TASK_EXECUTE_TYPE_MANUAL);
//				map.put("user", "admin");
//
//				try {
//					scheduler.triggerJob(jobKey, map);
//					DdcTaskUpdateHistory record = new DdcTaskUpdateHistory();
//					String user = "admin";
//					record.setTaskId(taskId);
//					record.setUpdateUser(user);
//					record.setUpdateTime(new Date());
//					record.setUpdateDetail(String.format("{%s} 执行任务！", "SourceLookup"));
//					taskUpdateMapper.insertSelective(record);
//				} catch (SchedulerException e) {
//					LOGGER.error("任务失败，任务名称{}，错误信息{}", task.getTaskName(), e);
//				} catch (Throwable e) {
//					LOGGER.error("插入任务执行记录失败，错误信息：{}", e);
//				}
//			}
//
//			private JobKey buildJobKey(DdcTask task) {
//
//				JobKey jobKey = new JobKey(Constants.JOB_PREFIX + task.getTaskId(), task.getAppKey());
//				return jobKey;
//			}
//
//			private DdcTask selectByTaskIdAndAppId(int appId, int taskId) {
//
//				DdcTaskMapper taskMapper = SpringContext.getApplicationContext().getBean(DdcTaskMapper.class);
//
//				DdcTaskExample taskExample = new DdcTaskExample();
//				DdcTaskExample.Criteria taskCriteria = taskExample.createCriteria();
//				taskCriteria.andAppIdEqualTo(appId);
//				taskCriteria.andTaskIdEqualTo(taskId);
//				List<DdcTask> ddcTaskList = taskMapper.selectByExample(taskExample);
//				if (ddcTaskList == null || ddcTaskList.size() != 1) {
//					throw new RuntimeException("查询task数据异常");
//				}
//				return ddcTaskList.get(0);
//			}
//
//			class SourceLookupRPC implements  SourceLookup {
//				
//				private DdcNode node;
//				private String taskClassName;
//				
//				@Override
//				public boolean lookup(String jobId, LocalDateTime loadDate) {
//					
//					JSONObject jo = new JSONObject();
//					jo.put("jobId", jobId);
//					jo.put("loadDate", loadDate.format(DateTimeFormatter.ISO_DATE_TIME));
//					
//					TaskEntity taskEntity = new TaskEntity();
//					// public int appId; // required
//					taskEntity.setAppId(1234567);
//					// public String className; // required
//					taskEntity.setClassName(taskClassName);
//					// public int taskId; // required
//					taskEntity.setTaskId(Integer.parseInt(jobId));
//					// public long executeId; // required
//					taskEntity.setExecuteId(1L);
//					// public int dispatchCount; // required
//					taskEntity.setDispatchCount(1);
//					// public String executeBatchId; // required
//					taskEntity.setExecuteBatchId(loadDate.format(DateTimeFormatter.BASIC_ISO_DATE));
//					// public int threadCount; // required
//					taskEntity.setThreadCount(1);
//					// public boolean needRetry; // required
//					taskEntity.setNeedRetry(false);
//					taskEntity.setParameters(jo.toJSONString());
//					
//					try (TFramedTransport transport = new TFramedTransport(new TSocket(node.getNodeIp(), node.getNodePort(), Constants.TIME_OUT));) {
//						TProtocol protocol = new TBinaryProtocol(transport);
//						transport.open();
//						AgentService.Client client = new AgentService.Client(protocol);
//						// 每次派发更新派发时间
//
//						CommandResult result = client.executeTask(taskEntity);
//						if (result.succeed) {
//							LOGGER.debug("执行成功");
//							String flag = result.getMessage().trim();
//							return Boolean.parseBoolean(flag);
//						}
//					} catch (Exception e) {
//						LOGGER.error("RPC Exceptoion, but what ever :)");
//					}
//					return false;
//				}
//			}
//			
//			
//			/**
//			 * 记录任务历史
//			 * @param status
//			 * @param message
//			 */
//			private void insertDdcTaskExecuteStatus(String status, String message) {
//				/*DdcTaskExecuteStatus ddcTaskExecuteStatus = new DdcTaskExecuteStatus();
//				ddcTaskExecuteStatus.setTaskId(taskId);
//				ddcTaskExecuteStatus.setExecuteId(executeId);
//				ddcTaskExecuteStatus.setExecuteBatchId(executeBatchId);
//				ddcTaskExecuteStatus.setWorkflowId(ddcTaskExecuteHistory != null ? ddcTaskExecuteHistory.getWorkflowId() : ddcSubtaskExecuteHistory.getWorkflowId());
//				ddcTaskExecuteStatus.setWorkflowExecuteId(workflowExecuteId);
//				ddcTaskExecuteStatus.setStatus(status);
//				ddcTaskExecuteStatus.setMessage(message);
//				ddcTaskExecuteStatus.setTimestamp(new Date());
//				ddcTaskExecuteStatusMapper.insertSelective(ddcTaskExecuteStatus);*/
//			}
//		}
//
//
//		class LookupTask implements Runnable {
//
//			private DdcTask ddcTask;
//			private String jobId;
//			private LocalDateTime loadDate;
//			private SourceLookup lookup;
//			
//			@Override
//			public void run() {
//				// 检测是否停止
//				if (this.isHalt()) {
//					LOGGER.info("停止检测: {}", ddcTask.getClassName());
//					return;
//				}
//				// 触发调度标识
//				boolean fired = false;
//				try {
//					// A: 跨周期依赖 和  B:数据源依赖都要满足才能执行
//					// check A
//					if (!isPrevSuccessed()) {
//						LOGGER.debug("前置任务未完成，等待调度 {} - {} ", ddcTask.getTaskId(), loadDate);
//						return;
//					} 
//					// 停止检测
//					if (lookup != null) {
//						boolean test = lookup.lookup(jobId, loadDate);
//						if (test) {
//							SourceEvent event = new SourceEvent();
//							event.setJobId(jobId);
//							event.setLoadDate(loadDate);
//							fireSourceEvent(event);
//						}
//					}
//				} catch (Throwable e) {
//					LOGGER.error("检测失败, ERROR: {}", e.getMessage(), e);
//				} finally {
//					if (!this.isHalt()) {
//						if (fired) {
//							LOGGER.info("任务 {} - {} 已触发", ddcTask.getTaskId(), loadDate);
//						} else {
//							// 继续调度
//							schedule0(this);
//						}
//					} else {
//						LOGGER.info("停止检测: {}", ddcTask.getClassName());
//					}
//				}
//			}
//			
//			private boolean isPrevSuccessed() {
//				// 不支持并发
//				Instant instant = loadDate.atZone(ZoneId.systemDefault()).toInstant();
//				Date prev = new Date(instant.toEpochMilli());
//				DdcTaskExecuteHistoryMapper historyMapper = SpringContext.getApplicationContext().getBean(DdcTaskExecuteHistoryMapper.class);
//				DdcTaskExecuteHistoryExample example = new DdcTaskExecuteHistoryExample();
//				example.createCriteria()
//					.andTaskIdEqualTo(ddcTask.getTaskId())
//					.andNextFireTimeEqualTo(prev);
//				List<DdcTaskExecuteHistory> list = historyMapper.selectByExample(example);
//				
//				if (list == null || list.size() == 0) {
//					return true;
//				}
//				if (list.size() == 1) {
//					DdcTaskExecuteHistory prevTask = list.get(0);
//					return prevTask.getExecuteResult() == "";
//				} else {
//					throw new AppException("不允许相同周期的任务出现两个");
//				}
//			}
//
//			public long getInterval() {
//				return 5000;
//			}
//
//			public boolean isHalt() {
//				return false;
//			}

}
