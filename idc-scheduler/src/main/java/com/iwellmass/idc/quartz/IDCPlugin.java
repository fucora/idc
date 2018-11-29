package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_JSON;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobPersistenceException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.listeners.JobListenerSupport;
import org.quartz.listeners.TriggerListenerSupport;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.IDCLogger;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.SimpleIDCLogger;
import com.iwellmass.idc.TaskService;
import com.iwellmass.idc.WorkflowService;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.JobRuntime;
import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

import lombok.Getter;
import lombok.Setter;

public abstract class IDCPlugin implements SchedulerPlugin, IDCConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCPlugin.class);

	// ~~ init from factory ~~
	private Scheduler scheduler;
	private IDCJobStore idcJobStore;
	private TaskService taskService;
	private WorkflowService workflowService;
	
	// ~~ internal component ~~
	private IDCStatusService idcStatusService;
	
	@Setter
	@Getter
	private IDCLogger logger = new SimpleIDCLogger();
	
	public void initialize(IDCJobStore store, WorkflowService workflowService, TaskService taskService) {
		this.idcJobStore = store;
		this.workflowService = workflowService;
		this.taskService = taskService;
		store.clearAllBarrier();
	}
	
	@Override
	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
		LOGGER.info("加载 IDCPlugin...");
		
		Objects.requireNonNull(idcJobStore, "IDCJobStore cannot be null");
		Objects.requireNonNull(taskService, "TaskService cannot be null");
		Objects.requireNonNull(workflowService, "WorkflowService cannot be null");

		this.scheduler = scheduler;
		// new status service
		this.idcStatusService = new StdStatusService();
		// set up context
		IDC_PLUGIN.applyPut(scheduler.getContext(), this);
		// add listeners
		scheduler.getListenerManager().addJobListener(new IDCJobListener());
		scheduler.getListenerManager().addTriggerListener(new IDCTriggerListener());
		scheduler.getListenerManager().addSchedulerListener(new IDCSchedulerListener());
		
		PluginVersion version = new PluginVersion();
		LOGGER.info("IDCPlugin 已加载, VERSION: {}", version.getVersion());
	}

	@Override
	public void start() {
		LOGGER.info("启动 IDCPlugin");
	}

	@Override
	public void shutdown() {
		LOGGER.info("停止 IDCPlugin");
	}
	
	/** 调度主任务 */
	public void schedule(Task task, ScheduleProperties sp) throws SchedulerException {
		
		Job job = new Job();
		
		job.setJobKey(aquireJobKey(task));
		job.setJobName(task.getTaskName());
		job.setCreateTime(LocalDateTime.now());
		job.setUpdateTime(null);
		job.setTaskKey(task.getTaskKey());
		job.setTaskType(task.getTaskType());
		job.setContentType(task.getContentType());
		job.setDispatchType(task.getDispatchType());
		
		job.setAssignee(sp.getAssignee());
		job.setScheduleType(sp.getScheduleType());
		job.setIsRetry(sp.getIsRetry());
		job.setBlockOnError(sp.getBlockOnError());
		job.setStartTime(sp.getStartTime());
		job.setEndTime(sp.getEndTime());
		job.setParameter(sp.getParameter());
		job.setCronExpr(sp.toCronExpression());
		
		schedule(task, job, null);
	}
	
	private JobKey aquireJobKey(Task tk) {
		return new JobKey(tk.getTaskId(), tk.getTaskGroup());
	}
	
	/** 调度子任务 */
	public void scheduleSubTask(Task task, Job mainJob, JobRuntime jrt) throws SchedulerException {
		Job subJob = new Job();
		subJob.setJobKey(aquireSubJobKey(task, mainJob));
		subJob.setJobName(task.getTaskName());
		subJob.setCreateTime(LocalDateTime.now());
		subJob.setUpdateTime(null);
		subJob.setTaskKey(task.getTaskKey());
		subJob.setTaskType(task.getTaskType());
		subJob.setContentType(task.getContentType());
		subJob.setDispatchType(task.getDispatchType());
		
		jrt.setJobKey(subJob.getJobKey());
		jrt.setParameter(mainJob.getParameter());
		jrt.setScheduleType(mainJob.getScheduleType());
		
		schedule(task, subJob, jrt);
	}
	
	private JobKey aquireSubJobKey(Task tk, Job mainJob) {
		return new JobKey(tk.getTaskId(), tk.getTaskGroup());
	}
	
	void schedule(Task task, Job job, JobRuntime jrt) throws SchedulerException {
		// build Task
		JobDetail jobDetail = buildJobDetail(task);
				
		// build Trigger
		Trigger trigger = buildTrigger(job);
		if (jrt != null) {
			IDCContextKey.JOB_RUNTIME.applyPut(trigger.getJobDataMap(), JSON.toJSONString(jrt));
		}
		
		// just schedule
		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	/** 重新调度任务 */
	public void reschedule(Job job) throws SchedulerException {
		
		/*JobKey jobPk = idcPlugin.buildJobKey(job);
		
		validate(jobPk, job.getDependencies());

		// 没有正在执行的任务计划便可以重新调度计划任务
		Job pj = jobRepository.findOne(jobPk);
		if (pj != null && pj.getStatus() != ScheduleStatus.NONE) {
			Assert.isTrue(pj.getStatus() == ScheduleStatus.PAUSED, "任务未冻结");
		}
		
		job.setJobKey(jobPk);
		job.setUpdateTime(LocalDateTime.now());

		LOGGER.info("重新调度任务 {}", jobPk);*/
		throw new SchedulerException("Not supported yet.");
	}
	
	/** 重新调度任务 */
	public void reschedule(Job job, JobDataMap jobData) throws SchedulerException {
		
		/*JobKey jobPk = idcPlugin.buildJobKey(job);
		
		validate(jobPk, job.getDependencies());

		// 没有正在执行的任务计划便可以重新调度计划任务
		Job pj = jobRepository.findOne(jobPk);
		if (pj != null && pj.getStatus() != ScheduleStatus.NONE) {
			Assert.isTrue(pj.getStatus() == ScheduleStatus.PAUSED, "任务未冻结");
		}
		
		job.setJobKey(jobPk);
		job.setUpdateTime(LocalDateTime.now());

		LOGGER.info("重新调度任务 {}", jobPk);*/
		throw new SchedulerException("Not supported yet.");
	}
	
	protected JobDetail buildJobDetail(Task task) {
		JobDataMap jobData = new JobDataMap();
		JobDetail jobDetail = null;
		if (task.getTaskType() == TaskType.WORKFLOW_TASK) {
			 jobDetail = JobBuilder.newJob(IDCWorkflowJob.class)
				 .withIdentity(task.getTaskId(), task.getTaskGroup())
				 .usingJobData(jobData)
				 .storeDurably()
				 .requestRecovery()
				 .build();
		} else {
			jobDetail = JobBuilder
				.newJob(getJobClass(task))
				.usingJobData(jobData)
				.withIdentity(task.getTaskId(), task.getTaskGroup())
				.requestRecovery()
				.build();
		}
		return jobDetail;
	}
	
	protected Trigger buildTrigger(Job job) {
		
		if (job.getTaskType() == TaskType.WORKFLOW_SUB_TASK) {
			JobKey jobKey = new JobKey(job.getTaskId(), job.getTaskGroup());
			// 构建常量
			TriggerBuilder<SimpleTrigger> builder = TriggerBuilder.newTrigger()
				.withIdentity(IDCUtils.toTriggerKey(jobKey))
				.forJob(job.getTaskId(), job.getTaskGroup())
				// .usingJobData(jobData)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule());
			return builder.build();
		} else {
			
			JobDataMap jobData = new JobDataMap();
			JOB_JSON.applyPut(jobData, JSON.toJSONString(job));
			
			JobKey jobKey = new JobKey(job.getTaskId(), job.getTaskGroup());
			// 构建 CRON 表达式
			CronScheduleBuilder cronBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpr())
					.withMisfireHandlingInstructionIgnoreMisfires();
			// 构建 TriggerBuilder
			TriggerBuilder<CronTrigger> builder = TriggerBuilder.newTrigger()
					.withIdentity(IDCUtils.toTriggerKey(jobKey))
					.forJob(job.getTaskId(), job.getTaskGroup())
					.usingJobData(jobData)
					.withSchedule(cronBuilder);
			// 设置开始时间
			Optional.ofNullable(job.getStartTime()).map(IDCUtils::toDate).ifPresent(builder::startAt);
			// 设置结束时间
			Optional.ofNullable(job.getEndTime()).map(IDCUtils::toDate).ifPresent(builder::endAt);
			return builder.build();
		}
	}
	
	protected abstract Class<? extends org.quartz.Job> getJobClass(Task task);
	
	public IDCStatusService getStatusService() {
		return this.idcStatusService;
	}
	
	public WorkflowService getWorkflowService() {
		return workflowService;
	}
	
	public TaskService getTaskService() {
		return taskService;
	}
	
	// ~~ 事件服务~~
	private class StdStatusService implements IDCStatusService {
		@Override
		public void fireStartEvent(StartEvent event) {
			LOGGER.info("Get event {}", event);
			logger.log(event.getInstanceId(), Optional.ofNullable(event.getMessage()).orElse("开始执行"));
			// 更新实例状态
			try {
				// 更新实例状态
				idcJobStore.storeIDCJobInstance(event.getInstanceId(), (jobInstance)->{
					jobInstance.setStartTime(event.getStartTime());
					jobInstance.setStatus(JobInstanceStatus.RUNNING);
				});
			} catch (Exception e) {
				String error = "更新任务状态出错" + e.getMessage();
				logger.log(event.getInstanceId(), error);
				throw new AppException(error, e);
			}
		}

		public void fireProgressEvent(ProgressEvent event) {
			LOGGER.info("Get event {}", event);
			logger.log(event.getInstanceId(), event.getMessage());
			// 更新实例状态
			try {
				idcJobStore.storeIDCJobInstance(event.getInstanceId(), (jobInstance)->{
					jobInstance.setStatus(JobInstanceStatus.RUNNING);
				});
			} catch (Exception e) {
				String error = "更新任务状态出错" + e.getMessage();
				logger.log(event.getInstanceId(), error);
				throw new AppException(error, e);
			}
		}
		
		@Override
		public void fireCompleteEvent(CompleteEvent event) {
			LOGGER.info("Get event {}", event);
			try {
				// 完成这个实例
				JobInstance ins = idcJobStore.completeIDCJobInstance(event);
				if (ins == null) {
					return;
				}
				// log this
				logger.log(event.getInstanceId(), event.getMessage())
					.log(event.getInstanceId(), "任务结束, 执行结果: {}", event.getFinalStatus());
				if (ins.getTaskType() == TaskType.WORKFLOW_SUB_TASK) {
					logger.log(ins.getWorkflowInstanceId(), "子任务 {} 结束, 执行结果: {}", ins.getInstanceId(), event.getFinalStatus());
				}
				// 工作流任务，执行子任务
				if (ins.getTaskType() == TaskType.WORKFLOW_SUB_TASK) {
					List<TaskKey> nextTasks = workflowService.getSuccessors(ins.getWorkflowId(), ins.getTaskKey());
					
					
				}
			} catch (Exception e) {
				String error = "更新任务状态出错: " + e.getMessage();
				logger.log(event.getInstanceId(), error);
				throw new AppException(error, e);
			}
		}
	}
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	// ~~ Trigger trace ~~
	private class IDCTriggerListener extends TriggerListenerSupport {
	
		@Override
		public void triggerFired(Trigger trigger, JobExecutionContext context) {
			try {
				JobInstance ins = idcJobStore.retrieveIDCJobInstance(Integer.parseInt(context.getFireInstanceId()));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("触发任务 {}, TaskType: {}, DispatchType {}", ins.getJobKey(), ins.getTaskType(), ins.getInstanceType());
				}
				
				logger.clearLog(ins.getInstanceId())
				.log(ins.getInstanceId(), "创建任务实例 {}, 执行方式 {}, 任务类型 {}", ins.getInstanceId(), ins.getInstanceType(), ins.getTaskType())
				.log(ins.getInstanceId(), "业务日期 {}, 批次 {}", ins.getLoadDate(), SDF.format(new Date(ins.getShouldFireTime())))
				.log(ins.getInstanceId(), "运行参数: {}", Utils.isNullOrEmpty(ins.getParameter()) ? "--" : ins.getParameter());
				
				IDCContextKey.CONTEXT_INSTANCE.applyPut(context, ins);
			} catch (JobPersistenceException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		
		@Override
		public String getName() {
			return IDCTriggerListener.class.getName();
		}
	}


	// ~~ Job trace ~~
	private class IDCJobListener extends JobListenerSupport {
		
		@Override
		public void jobToBeExecuted(JobExecutionContext context) {
			JobInstance instance = CONTEXT_INSTANCE.applyGet(context);
			
			// 记录任务
			if (instance.getTaskType() == TaskType.WORKFLOW_TASK) {
				idcStatusService.fireProgressEvent(ProgressEvent.newEvent(instance.getInstanceId())
					.setStatus(JobInstanceStatus.NEW)
					.setMessage("执行工作流，业务日期 {}", instance.getLoadDate()));
			} else {
				idcStatusService.fireProgressEvent(ProgressEvent.newEvent(instance.getInstanceId())
					.setStatus(JobInstanceStatus.NEW)
					.setMessage("派发任务，业务日期 {}", instance.getLoadDate()));
			}

			if (instance.getTaskType() == TaskType.WORKFLOW_SUB_TASK) {
				idcStatusService.fireProgressEvent(ProgressEvent.newEvent(instance.getWorkflowInstanceId())
						.setStatus(JobInstanceStatus.RUNNING)
						.setMessage("派发子任务 {}...", instance.getInstanceId()));
			}
			
		}
		
		@Override
		public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
			JobInstance instance = CONTEXT_INSTANCE.applyGet(context);
			if (jobException != null) {
				// 通知任务已经完成
				if (instance.getTaskType() == TaskType.WORKFLOW_TASK) {
					idcStatusService.fireCompleteEvent(CompleteEvent.failureEvent()
						.setInstanceId(instance.getInstanceId())
						.setMessage("执行失败: {}", jobException.getMessage()));
				} else {
					idcStatusService.fireCompleteEvent(CompleteEvent.failureEvent()
						.setInstanceId(instance.getInstanceId())
						.setMessage("派发任务失败: {}", jobException.getMessage()));
				}
			} else {
				// 本任务日志
				if (instance.getTaskType() != TaskType.WORKFLOW_TASK) {
					idcStatusService.fireProgressEvent(ProgressEvent.newEvent(instance.getInstanceId())
							.setStatus(JobInstanceStatus.ACCEPTED)	
							.setMessage("等待执行结果...", instance.getInstanceId()));
				}
				if (instance.getTaskType() == TaskType.WORKFLOW_SUB_TASK) {
					idcStatusService.fireProgressEvent(ProgressEvent.newEvent(instance.getWorkflowInstanceId())
							.setStatus(JobInstanceStatus.ACCEPTED)	
							.setMessage("等待 {} 执行结果...", instance.getInstanceId()));
				}
			}
		}
		@Override
		public String getName() {
			return IDCJobListener.class.getSimpleName();
		}
	}
}
