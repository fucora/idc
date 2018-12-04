package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_RUNTIME;
import static com.iwellmass.idc.quartz.IDCContextKey.TASK_JSON;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
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
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.listeners.JobListenerSupport;
import org.quartz.listeners.SchedulerListenerSupport;
import org.quartz.listeners.TriggerListenerSupport;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.IDCLogger;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.JobService;
import com.iwellmass.idc.SimpleIDCLogger;
import com.iwellmass.idc.TaskService;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobEnv;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskType;

import lombok.Getter;
import lombok.Setter;

public abstract class IDCPlugin implements SchedulerPlugin, IDCConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCPlugin.class);

	// ~~ init from factory ~~
	private Scheduler scheduler;
	private IDCJobStore idcJobStore;
	
	@Getter
	private TaskService taskService;
	
	@Getter
	private JobService jobService;
	
	@Getter
	private DependencyService dependencyService;
	
	// ~~ internal component ~~
	@Getter
	private IDCStatusService statusService;
	
	@Setter
	@Getter
	private IDCLogger logger = new SimpleIDCLogger();
	
	public void initialize(IDCJobStore store, TaskService taskService, JobService jobService, DependencyService workflowService) {
		this.idcJobStore = store;
		this.dependencyService = workflowService;
		this.taskService = taskService;
		this.jobService = jobService;
		store.clearAllBarrier();
	}
	
	@Override
	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
		LOGGER.info("加载 IDCPlugin...");
		
		Objects.requireNonNull(idcJobStore, "IDCJobStore cannot be null");
		Objects.requireNonNull(taskService, "TaskService cannot be null");
		Objects.requireNonNull(jobService, "TaskService cannot be null");
		Objects.requireNonNull(dependencyService, "WorkflowService cannot be null");

		this.scheduler = scheduler;
		// new status service
		this.statusService = new StdStatusService();
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
		// 任务信息
		job.setJobKey(aquireJobKey(task));
		job.setJobName(task.getTaskName());
		job.setCreateTime(LocalDateTime.now());
		job.setUpdateTime(null);
		job.setTaskKey(task.getTaskKey());
		job.setTaskType(task.getTaskType());
		job.setContentType(task.getContentType());
		job.setDispatchType(task.getDispatchType());
		// 调度信息
		job.setAssignee(sp.getAssignee());
		job.setScheduleType(sp.getScheduleType());
		job.setIsRetry(sp.getIsRetry());
		job.setBlockOnError(sp.getBlockOnError());
		job.setStartTime(sp.getStartTime());
		job.setEndTime(sp.getEndTime());
		job.setParameter(sp.getParameter());
		job.setCronExpr(sp.toCronExpression());
		// ~~ 前端用 ~~
		job.setScheduleConfig(JSON.toJSONString(sp));
		jobService.saveJob(job);
		
		// 调度
		JobDetail jobDetail = buildJobDetail(task);
		Trigger trigger = buildMainTrigger(job);
		scheduler.scheduleJob(jobDetail, trigger);
	}
	
	public Date reschedule(JobKey jobKey, ScheduleProperties sp) throws SchedulerException {
		Job job = jobService.getJob(jobKey);
		if (job == null) {
			throw new SchedulerException("调度计划 " + jobKey + " 不存在");
		}
		// 调度信息
		job.setAssignee(sp.getAssignee());
		job.setScheduleType(sp.getScheduleType());
		job.setIsRetry(sp.getIsRetry());
		job.setBlockOnError(sp.getBlockOnError());
		job.setStartTime(sp.getStartTime());
		job.setEndTime(sp.getEndTime());
		job.setParameter(sp.getParameter());
		job.setCronExpr(sp.toCronExpression());
		// ~~ 前端用 ~~
		job.setScheduleConfig(JSON.toJSONString(sp));
		jobService.saveJob(job);
		
		// 调度
		Trigger trigger = buildMainTrigger(job);
		Date ret = scheduler.rescheduleJob(trigger.getKey(), trigger);
		
		// 创建一个新的调度
		if (ret == null) {
			Task task = taskService.getTask(job.getTaskKey());
			JobDetail jdt = buildJobDetail(task);
			ret = scheduler.scheduleJob(jdt, trigger);
		}
		return ret;
	}
	
	private Trigger buildMainTrigger(Job job) {
		JobEnv jobEnv = new JobEnv();
		jobEnv.setAssignee(job.getAssignee());
		jobEnv.setJobKey(job.getJobKey());
		jobEnv.setScheduleType(job.getScheduleType());
		jobEnv.setParameter(job.getParameter());
		JobDataMap jobData = new JobDataMap();
		JOB_RUNTIME.applyPut(jobData, JSON.toJSONString(jobEnv));
		// 构建 TriggerBuilder
		TriggerBuilder<CronTrigger> builder = TriggerBuilder.newTrigger()
			.withIdentity(IDCUtils.toTriggerKey(jobEnv.getJobKey()))
			.forJob(job.getTaskId(), job.getTaskGroup())
			.withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpr()).withMisfireHandlingInstructionIgnoreMisfires())
			.usingJobData(jobData);
		// 设置开始时间
		Optional.ofNullable(job.getStartTime()).map(IDCUtils::toDate).ifPresent(builder::startAt);
		// 设置结束时间
		Optional.ofNullable(job.getEndTime()).map(IDCUtils::toDate).ifPresent(builder::endAt);
		
		return builder.build();
	}
	
	private JobKey aquireJobKey(Task tk) {
		return new JobKey(tk.getTaskId(), tk.getTaskGroup());
	}
	
	/** 调度子任务 */
	void scheduleSubTask(Task task, Integer mainJobInsId) throws SchedulerException {
		
		JobInstance sfIns = idcJobStore.retrieveIDCJobInstance(mainJobInsId);
		
		// 取出主任务
		Job mainJob = jobService.getJob(sfIns.getJobKey());
		
		JobEnv jobEnv = new JobEnv();
		jobEnv.setJobKey(aquireSubJobKey(task, mainJob));
		jobEnv.setTaskKey(task.getTaskKey());
		
		// mark
		jobEnv.setMainInstanceId(mainJobInsId);
		jobEnv.setTaskType(TaskType.SUB_TASK);
		
		// seam as main job
		jobEnv.setAssignee(mainJob.getAssignee());
		jobEnv.setParameter(mainJob.getParameter());
		jobEnv.setScheduleType(mainJob.getScheduleType());
		
		jobEnv.setPrevFireTime(sfIns.getPrevFireTime());
		jobEnv.setShouldFireTime(sfIns.getShouldFireTime());
		

		// build Task
		JobDetail jobDetail = buildJobDetail(task);
				
		// build Trigger
		JobDataMap jobData = new JobDataMap();
		JOB_RUNTIME.applyPut(jobData, JSON.toJSONString(jobEnv));
		// 构建常量
		TriggerBuilder<SimpleTrigger> builder = TriggerBuilder.newTrigger()
			.withIdentity(IDCUtils.toTriggerKey(jobEnv.getJobKey()))
			.forJob(task.getTaskId(), task.getTaskGroup())
			.usingJobData(jobData)
			.withSchedule(SimpleScheduleBuilder.simpleSchedule());
		
		// just schedule
		scheduler.scheduleJob(jobDetail, builder.build());
	}
	
	private JobKey aquireSubJobKey(Task tk, Job mainJob) {
		return new JobKey(tk.getTaskId(), tk.getTaskGroup());
	}
	
	
	private JobDetail buildJobDetail(Task task) {
		JobDataMap jobData = new JobDataMap();
		TASK_JSON.applyPut(jobData, JSON.toJSONString(task));
		
		JobDetail jobDetail = null;
		if (task.getTaskType() == TaskType.WORKFLOW) {
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
	
	public void unschedule(JobKey jobKey) throws SchedulerException {
		Job job = jobService.getJob(jobKey);
		
		Assert.isTrue(job != null, "调度计划 " + jobKey + "不存在");
		
		scheduler.unscheduleJob(IDCUtils.toTriggerKey(jobKey));
		
	}
	
	public void pause(JobKey jobKey) throws SchedulerException {
		scheduler.pauseTrigger(IDCUtils.toTriggerKey(jobKey));
	}
	
	public void resume(JobKey jobKey) throws SchedulerException {
		scheduler.resumeTrigger(IDCUtils.toTriggerKey(jobKey));
	}
	
	protected abstract Class<? extends org.quartz.Job> getJobClass(Task task);
	
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
				if (ins.getTaskType() == TaskType.SUB_TASK) {
					logger.log(ins.getMainInstanceId(), "子任务 {} 结束, 执行结果: {}", ins.getInstanceId(), event.getFinalStatus());
				}
				
				// 工作流任务，执行子任务
				if (ins.getTaskType() == TaskType.SUB_TASK) {
					// TODO 
					/*List<TaskKey> nextTasks = dependencyService.getSuccessors(ins.getWorkflowId(), ins.getTaskKey());
					if (!Utils.isNullOrEmpty(nextTasks)) {
						for (Task subTask : taskService.getTasks(nextTasks)) {
							JobEnv env = new JobEnv();
							env.setMainInstanceId(ins.getMainInstanceId());
							// scheduleSubTask(subTask, env);
						}
					}*/
				}
			} catch (Exception e) {
				LOGGER.error("更新任务状态出错: " + e.getMessage());
			}
		}
	}
	
	// ~~ Scheduler Listener ~~
	public class IDCSchedulerListener extends SchedulerListenerSupport {

		/* 保存任务到数据库 */
		public void jobScheduled(Trigger trigger) {}

		/* 撤销调度 */
		public void jobUnscheduled(TriggerKey triggerKey) {
//			LOGGER.info("调度任务 {} 已撤销", triggerKey);
		}

		/* 调度冻结 */
		public void triggerPaused(TriggerKey triggerKey) {
//			LOGGER.info("调度任务 {} 已冻结", triggerKey);
			throw new UnsupportedOperationException("Not supported yet.");
		}
		
		/* 调度恢复 */
		public void triggerResumed(TriggerKey triggerKey) {
//			LOGGER.info("调度任务 {} 已恢复", triggerKey);
			throw new UnsupportedOperationException("Not supported yet.");
		}
		
		/* 调度完结 */
		public void triggerFinalized(Trigger trigger) {
//			JobKey JobKey = parseJobKey(trigger);
//			LOGGER.info("调度任务 {} 已完结", JobKey);
		}
		
		@Override
		public void schedulerError(String msg, SchedulerException cause) {
			LOGGER.error("IDCScheduler ERROR: " + msg, cause);
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
					LOGGER.debug("触发任务 {}, TaskType: {}, DispatchType {}", ins.getJobKey(), ins.getTaskType(), ins.getDispatchType());
				}
				
				logger.clearLog(ins.getInstanceId())
				.log(ins.getInstanceId(), "创建任务实例 {}, 执行方式 {}, 任务类型 {}", ins.getInstanceId(), ins.getDispatchType(), ins.getTaskType())
				.log(ins.getInstanceId(), "周期类型 {}, 业务日期 {}, 批次 {}", ins.getScheduleType(), ins.getLoadDate(), SDF.format(new Date(ins.getShouldFireTime())))
				.log(ins.getInstanceId(), "运行参数: {}", Utils.isNullOrEmpty(ins.getParameter()) ? "--" : ins.getParameter());
				
				IDCContextKey.CONTEXT_INSTANCE.applyPut(context, ins);
			} catch (JobPersistenceException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		
		@Override
		public void triggerComplete(Trigger trigger, JobExecutionContext context, CompletedExecutionInstruction triggerInstructionCode) {
			super.triggerComplete(trigger, context, triggerInstructionCode);
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
			if (instance.getTaskType() == TaskType.WORKFLOW) {
				statusService.fireProgressEvent(ProgressEvent.newEvent(instance.getInstanceId())
					.setStatus(JobInstanceStatus.NEW)
					.setMessage("执行工作流，业务日期 {}", instance.getLoadDate()));
			} else {
				statusService.fireProgressEvent(ProgressEvent.newEvent(instance.getInstanceId())
					.setStatus(JobInstanceStatus.NEW)
					.setMessage("派发任务，业务日期 {}", instance.getLoadDate()));
			}

			if (instance.getTaskType() == TaskType.NODE_TASK) {
				statusService.fireProgressEvent(ProgressEvent.newEvent(instance.getMainInstanceId())
						.setStatus(JobInstanceStatus.RUNNING)
						.setMessage("派发子任务 {}...", instance.getInstanceId()));
			}
			
		}
		
		@Override
		public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
			JobInstance instance = CONTEXT_INSTANCE.applyGet(context);
			if (jobException != null) {
				// 通知任务已经完成
				if (instance.getTaskType() == TaskType.WORKFLOW) {
					statusService.fireCompleteEvent(CompleteEvent.failureEvent()
						.setInstanceId(instance.getInstanceId())
						.setMessage("执行失败: {}", jobException.getMessage()));
				} else {
					statusService.fireCompleteEvent(CompleteEvent.failureEvent()
						.setInstanceId(instance.getInstanceId())
						.setMessage("派发任务失败: {}", jobException.getMessage()));
				}
			} else {
				// 本任务日志
				if (instance.getTaskType() != TaskType.WORKFLOW) {
					statusService.fireProgressEvent(ProgressEvent.newEvent(instance.getInstanceId())
							.setStatus(JobInstanceStatus.ACCEPTED)	
							.setMessage("等待执行结果...", instance.getInstanceId()));
				}
				if (instance.getTaskType() == TaskType.NODE_TASK) {
					statusService.fireProgressEvent(ProgressEvent.newEvent(instance.getMainInstanceId())
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
