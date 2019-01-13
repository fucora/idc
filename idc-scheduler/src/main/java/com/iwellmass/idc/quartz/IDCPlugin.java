package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_ENV;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_TRIGGER_INSTRUCTION;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobPersistenceException;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.listeners.JobListenerSupport;
import org.quartz.listeners.SchedulerListenerSupport;
import org.quartz.listeners.TriggerListenerSupport;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.iwellmass.common.util.Assert;
import com.iwellmass.idc.IDCLogger;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.SimpleIDCLogger;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.model.WorkflowEdge;

import lombok.Getter;
import lombok.Setter;

public abstract class IDCPlugin implements SchedulerPlugin, IDCConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCPlugin.class);

	// ~~ init from factory ~~
	private Scheduler scheduler;
	private IDCJobStore idcJobStore;
	
	// ~~ internal component ~~
	@Getter
	private IDCStatusService statusService;
	
	@Getter
	private IDCPluginService pluginService;
	
	@Setter
	@Getter
	private IDCLogger logger = new SimpleIDCLogger();
	
	public IDCPlugin(IDCPluginService pluginService) {
		this.pluginService = pluginService;
	}
	
	public void initialize(IDCJobStore store) {
		this.idcJobStore = store;
	}
	
	@Override
	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
		LOGGER.info("加载 IDCPlugin...");
		
		Objects.requireNonNull(idcJobStore, "IDCJobStore cannot be null");
		Objects.requireNonNull(pluginService, "IDCSchedulerService cannot be null");
		
		// 验证
		IDCPluginConfig config = pluginService.getConfig();
		LOGGER.info("IDC数据库版本 {}", config.getVersion());
		
		// set up context
		IDC_PLUGIN.applyPut(scheduler.getContext(), this);
		
		// add listeners
		scheduler.getListenerManager().addJobListener(new IDCJobListener());
		scheduler.getListenerManager().addTriggerListener(new IDCTriggerListener());
		scheduler.getListenerManager().addSchedulerListener(new IDCSchedulerListener());
		
		// ~~ 系统任务 ~~
		scheduler.addJob(JobBuilder.newJob(IDCWorkflowGuardJob.class)
			.withIdentity(WorkflowEdge.END.getTaskId(), WorkflowEdge.END.getTaskGroup()).requestRecovery()
			.storeDurably().build(), true);
		
		this.scheduler = scheduler;
		this.statusService = (IDCStatusService) Proxy.newProxyInstance(IDCPlugin.class.getClassLoader(), new Class[] {IDCStatusService.class}, new InvocationHandler() {
			private StdStatusService ss = new StdStatusService();
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				try {
					LOGGER.info("Get Event {}", args);
					return method.invoke(ss, args);
				} catch(Throwable e) {
					LOGGER.error("通知失败," + e.getMessage(), e);
				}
				return null;
			}
		});
	}

	@Override
	public void start() {
		LOGGER.info("启动 IDCPlugin");
	}

	@Override
	public void shutdown() {
		LOGGER.info("停止 IDCPlugin");
	}
	
	/** 
	 * 刷新任务
	 */
	public void refresh(Task task) throws SchedulerException {
		if (task.getTaskType() == TaskType.WORKFLOW) {
			JobDetail jobDetail = JobBuilder
					.newJob(IDCWorkflowJob.class)
					//.usingJobData(jobData)
					.withIdentity(task.getTaskId(), task.getTaskGroup())
					.storeDurably()
					.build();
			scheduler.addJob(jobDetail, true);
		} else {
			JobDetail jobDetail = JobBuilder
					.newJob(getJobClass(task))
					//.usingJobData(jobData)
					.withIdentity(task.getTaskId(), task.getTaskGroup())
					.storeDurably()
					.build();
			scheduler.addJob(jobDetail, true);
		}
	}
	
	public void schedule(Job job) throws SchedulerException {
		
		Task task = pluginService.getTask(job.getTaskKey());
		
		if (task == null) {
			throw new SchedulerException("任务不存在");
		}
		
		JobKey newJobKey = pluginService.acquireJobKey(task);
		
		job.setJobKey(newJobKey);
		
//		Job job = new Job();
		// 任务信息
//		job.setJobKey(newJobKey);
		job.setCreateTime(LocalDateTime.now());
		job.setUpdateTime(LocalDateTime.now());
//		job.setTaskKey(task.getTaskKey());
		job.setTaskType(task.getTaskType());
		job.setContentType(task.getContentType());
		job.setWorkflowId(task.getWorkflowId());
//		
//		// 调度信息
//		job.setDispatchType(sp.getDispatchType());
//		job.setJobName(sp.getJobName());
//		job.setAssignee(sp.getAssignee());
//		job.setScheduleType(sp.getScheduleType());
//		job.setIsRetry(sp.getIsRetry());
//		job.setBlockOnError(sp.getBlockOnError());
//		if (sp.getStartTime() != null) {
//			job.setStartTime(sp.getStartTime().atTime(LocalTime.MIN));
//		}
//		if (sp.getEndTime() != null) {
//			job.setEndTime(sp.getEndTime().atTime(LocalTime.MAX));
//		}
//		job.setParameter(sp.getParameter());
//		// cron-expr
//		if (job.getScheduleType() != ScheduleType.CUSTOMER) {
//			job.setCronExpr(sp.toCronExpression());
//		}
//		// ~~ 前端用 ~~
//		job.setScheduleConfig(JSON.toJSONString(sp));
		
		// TODO 保存 job 的依赖关系
		pluginService.saveJob(job);
		
		// do scheduler
		if (job.getDispatchType() == DispatchType.AUTO) {
			scheduler.scheduleJob(buildAutoTrigger(job));
		}
	}
	
	/**
	 * 重新调度
	 */
	public void reschedule(JobKey jobKey, Job sp) throws SchedulerException {
		// 清空所有实例
		idcJobStore.cleanupIDCJob(jobKey);
		
		schedule(sp);
		
//		Job job = pluginService.findJob(jobKey);
//		if (job == null) {
//			throw new SchedulerException("调度计划 " + jobKey + " 不存在");
//		}
//		Task task = pluginService.findTask(job.getTaskKey());
//		
//		if (task == null) {
//			throw new SchedulerException("任务 " + jobKey + " 不存在");
//		}
//		
//		// 调度信息
//		if (sp != null) {
//			job.setJobName(sp.getJobName());
//			job.setAssignee(sp.getAssignee());
//			job.setScheduleType(sp.getScheduleType());
//			job.setIsRetry(sp.getIsRetry());
//			job.setBlockOnError(sp.getBlockOnError());
//			if (sp.getStartTime() != null) {
//				job.setStartTime(sp.getStartTime());
//			}
//			if (sp.getEndTime() != null) {
//				job.setEndTime(sp.getEndTime());
//			}
//			job.setParameter(sp.getParameter());
//			job.setCronExpr(sp.getCronExpr());
//			job.setScheduleConfig(sp.getScheduleConfig());
//			job.setUpdateTime(LocalDateTime.now());
//		}
//		job.setWorkflowId(task.getWorkflowId());
//		pluginService.saveJob(job);
//		
//		// do schedule
//		if (job.getDispatchType() == DispatchType.AUTO) {
//			scheduler.scheduleJob(buildAutoTrigger(job));
//		}
//		return null;
	}
	
	void scheduleSubTask(TaskKey subTaskKey, Integer mainInstanceId) throws SchedulerException {
		JobInstance mainJobIns = idcJobStore.retrieveIDCJobInstance(mainInstanceId);
		scheduleSubTask(subTaskKey, mainJobIns);
	}
	
	void scheduleSubTask(TaskKey subTaskKey, JobInstance mainJobIns) throws SchedulerException {
		
		String wfId = mainJobIns.getWorkflowId();
		Task mainTask = pluginService.getTask(mainJobIns.getTaskKey());
		
		// 如果快照已经改变
		if (!wfId.equals(mainTask.getWorkflowId())) {
			statusService.fireCompleteEvent(CompleteEvent.failureEvent(mainJobIns.getInstanceId())
				.setMessage("工作流程已经改变，结束此任务"));
			return;
		}
		
		JobKey subJobKey = IDCUtils.getSubJobKey(mainJobIns.getJobKey(), subTaskKey);
		Trigger trigger = null;
		
		if (WorkflowEdge.CTRL_JOIN_GROUP.equals(subTaskKey.getTaskGroup())) {
			
			List<JobKey> barrierKeys = pluginService.getPredecessors(mainJobIns.getTaskKey(), subTaskKey)
					.stream().map(tk -> {
						return IDCUtils.getSubJobKey(mainJobIns.getJobKey(), tk);
					}).collect(Collectors.toList());
			
			JoinEnv joinEnv = new JoinEnv();
			joinEnv.setInstanceId(mainJobIns.getInstanceId());
			joinEnv.setShouldFireTime(mainJobIns.getShouldFireTime());
			joinEnv.setBarrierKeys(barrierKeys);
			joinEnv.setJoinKey(subTaskKey);
			joinEnv.setMainTaskKey(mainJobIns.getTaskKey());

			JobDataMap jobData = new JobDataMap();
			JOB_TRIGGER_INSTRUCTION.applyPut(jobData, IDCTriggerInstruction.GUARD);
			JOB_ENV.applyPut(jobData, JSON.toJSONString(joinEnv));
			trigger = buildSimpleTrigger(subJobKey, WorkflowEdge.CTRL_JOIN, jobData);
			
		} else if (subTaskKey.equals(WorkflowEdge.END)) {
			// 添加一个 guard trigger
			List<JobKey> barrierKeys = pluginService.getPredecessors(mainJobIns.getTaskKey(), WorkflowEdge.END)
				.stream().map(tk -> {
					return IDCUtils.getSubJobKey(mainJobIns.getJobKey(), tk);
				}).collect(Collectors.toList());

			GuardEnv guardEnv = new GuardEnv();
			guardEnv.setInstanceId(mainJobIns.getInstanceId());
			guardEnv.setShouldFireTime(mainJobIns.getShouldFireTime());
			guardEnv.setBarrierKeys(barrierKeys);

			JobDataMap jobData = new JobDataMap();
			JOB_TRIGGER_INSTRUCTION.applyPut(jobData, IDCTriggerInstruction.GUARD);
			JOB_ENV.applyPut(jobData, JSON.toJSONString(guardEnv));
			trigger = buildSimpleTrigger(subJobKey, WorkflowEdge.END, jobData);
		} else {
			Task subTask = pluginService.getTask(subTaskKey);
			if (subTask == null) {
				throw new JobExecutionException("子任务不存在");
			}
			
			SubEnv subEnv = new SubEnv();
			// mark
			subEnv.setMainInstanceId(mainJobIns.getInstanceId());
			
			// build Simple
			JobDataMap jobData = new JobDataMap();
			JOB_ENV.applyPut(jobData, JSON.toJSONString(subEnv));
			JOB_TRIGGER_INSTRUCTION.applyPut(jobData, IDCTriggerInstruction.SUB);
			
			//子任务Key
			trigger = buildSimpleTrigger(subJobKey, subTaskKey, jobData);
		}
		// just schedule
		try {
			scheduler.scheduleJob(trigger);
		} catch (ObjectAlreadyExistsException e) {
			LOGGER.debug("任务" + subJobKey + "已经触发过了，忽略...");
		}
	}
	
	private Trigger buildAutoTrigger(Job job) {
		// 构建 TriggerBuilder
		TriggerBuilder<CronTrigger> builder = TriggerBuilder.newTrigger()
			.withIdentity(IDCUtils.toTriggerKey(job.getJobKey()))
			.forJob(job.getTaskId(), job.getTaskGroup())
			.withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpr()).withMisfireHandlingInstructionIgnoreMisfires());
		// 设置开始时间
		Optional.ofNullable(job.getStartTime()).map(IDCUtils::toDate).ifPresent(builder::startAt);
		// 设置结束时间
		Optional.ofNullable(job.getEndTime()).map(IDCUtils::toDate).ifPresent(builder::endAt);
		
		return builder.build();
	}
	
	private Trigger buildSimpleTrigger(JobKey jobKey, TaskKey taskKey, JobDataMap jobData) {	// build Trigger
		// 构建常量
		TriggerBuilder<SimpleTrigger> builder = TriggerBuilder.newTrigger()
			.withIdentity(jobKey.getJobId(), jobKey.getJobGroup())
			.forJob(taskKey.getTaskId(), taskKey.getTaskGroup())
			.withSchedule(SimpleScheduleBuilder.simpleSchedule());
		
		if (jobData != null) {
			builder.usingJobData(jobData);
		}
		return builder.build();
	}
	
	public void unschedule(JobKey jobKey) throws SchedulerException {
		Job job = pluginService.getJob(jobKey);
		Assert.isTrue(job != null, "调度计划 " + jobKey + "不存在");
		scheduler.unscheduleJob(IDCUtils.toTriggerKey(jobKey));
	}
	
	public void pause(JobKey jobKey) throws SchedulerException {
		scheduler.pauseTrigger(IDCUtils.toTriggerKey(jobKey));
	}
	
	public void resume(JobKey jobKey) throws SchedulerException {
		scheduler.resumeTrigger(IDCUtils.toTriggerKey(jobKey));
	}
	
	public void redo(Integer instanceId) throws SchedulerException {
		JobInstance ins;
		try {
			ins = idcJobStore.retrieveIDCJobInstance(instanceId);
			
			RedoEnv redoEnv = new RedoEnv();
			redoEnv.setInstanceId(ins.getInstanceId());
			
			JobDataMap jobData = new JobDataMap();
			JOB_ENV.applyPut(jobData, JSON.toJSONString(redoEnv));
			JOB_TRIGGER_INSTRUCTION.applyPut(jobData, IDCTriggerInstruction.REDO);
			
			JobKey jobKey = IDCUtils.getRedoJobKey(ins.getInstanceId());
			
			Trigger redoTrigger = buildSimpleTrigger(jobKey, ins.getTaskKey(), jobData);
			scheduler.scheduleJob(redoTrigger);
		} catch (JobPersistenceException e) {
			throw new SchedulerException(e.getMessage(), e);
		}
	}
	
	protected abstract Class<? extends org.quartz.Job> getJobClass(Task task);
	
	// ~~ Scheduler Listener ~~
	private class IDCSchedulerListener extends SchedulerListenerSupport {
		@Override
		public void schedulerError(String msg, SchedulerException cause) {
			// TODO 添加到报警信息中
		}
	}

	// ~~ Trigger trace ~~
	private class IDCTriggerListener extends TriggerListenerSupport {
	
		@Override
		public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
			String fid = context.getFireInstanceId();
			try {
				int instanceId = Integer.parseInt(fid);
				IDCTriggerInstruction instruction = getTriggerInstruction(context);
				// 如果 IDCJob 被触发，这存在状态为 NEW 的 instance，如果不存在则否决执行
				if (instruction.isIDCJobTriggered()) {
					JobInstance instance = idcJobStore.retrieveIDCJobInstance(instanceId);
					if (instance != null && instance.getStatus() == JobInstanceStatus.NEW) {
						// pre-execute
						CONTEXT_INSTANCE.applyPut(context, instance);
					}
				}
			} catch (Exception e) {
				// 其他任务不处理
			}
			return false;
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
			IDCTriggerInstruction instruction = getTriggerInstruction(context);
			if (instruction.isIDCJobTriggered()) {
				JobInstance ins = getJobInstance(context);
				logger.clearLog(ins.getInstanceId())
					.log(ins.getInstanceId(), "执行任务 {}, taskId: {}, taskGroup: {}", ins.getJobName(), ins.getTaskId(), ins.getTaskGroup())
					.log(ins.getInstanceId(), "创建任务实例 {}, 执行方式 {}, 周期类型 {}", ins.getInstanceId(), ins.getDispatchType(), ins.getScheduleType())
					.log(ins.getInstanceId(), "执行批次 {}, 业务日期 {}, ", IDCConstants.FULL_DF.format(new Date(ins.getShouldFireTime())), ins.getLoadDate());
				if (ins.getTaskType() != TaskType.WORKFLOW) {
					statusService.fireProgressEvent(ProgressEvent.newEvent(ins.getInstanceId())
						.setStatus(JobInstanceStatus.NEW).setMessage("派发任务..."));
				}
			}
		}
		
		@Override
		public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
			
			IDCTriggerInstruction instruction = getTriggerInstruction(context);
			
			// IDCJob running, update JobInstance
			if (instruction.isIDCJobTriggered()) {
				JobInstance instance = getJobInstance(context);
				if (jobException != null) {
					CompleteEvent event = CompleteEvent.failureEvent(instance.getInstanceId());
					if (instance.getTaskType() == TaskType.WORKFLOW) {
						event.setMessage("任务异常: {}", jobException.getMessage());
					} else {
						event.setMessage("派发失败: {}", jobException.getMessage());
					}
					statusService.fireCompleteEvent(event);
				} else {
					ProgressEvent event = ProgressEvent.newEvent(instance.getInstanceId());
					if (instance.getTaskType() == TaskType.WORKFLOW) {
						event.setStatus(JobInstanceStatus.RUNNING);
						event.setMessage("派发子任务...");
					} else {
						event.setStatus(JobInstanceStatus.ACCEPTED);
						event.setMessage("派发成功, 等待执行结果...");
					}
					statusService.fireProgressEvent(event);
				}
			}
		}
		
		@Override
		public String getName() {
			return IDCJobListener.class.getSimpleName();
		}
	}
	
	private JobInstance getJobInstance(JobExecutionContext context) {
		return CONTEXT_INSTANCE.applyGet(context);
	}
	
	private IDCTriggerInstruction getTriggerInstruction(JobExecutionContext context) {
		return JOB_TRIGGER_INSTRUCTION.applyGet(context.getTrigger().getJobDataMap());
	}

	
	// ~~ 事件服务~~
	private class StdStatusService implements IDCStatusService {
		@Override
		public void fireStartEvent(StartEvent event) {
			try {
				ProgressEvent pe = ProgressEvent.newEvent(event.getInstanceId())
					.setMessage(event.getMessage())
					.setStatus(JobInstanceStatus.RUNNING);
				pe.setTime(event.getStartTime());
				idcJobStore.jobInstanceProgressing(pe);
			} catch (JobPersistenceException e) {
				throw new RuntimeException(e);
			}
		}

		public void fireProgressEvent(ProgressEvent event) {
			try {
				JobInstance instance = idcJobStore.jobInstanceProgressing(event);
				if(instance != null) {
					// 本任务日志
					logger.log(event.getInstanceId(), event.getMessage());
					
					// 主任务日志
					if (instance.getTaskType() == TaskType.WORKFLOW) {
						logger.log(instance.getMainInstanceId(), "[{}] {}", instance.getJobName(), event.getMessage());
					}
				}
			} catch (JobPersistenceException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public void fireCompleteEvent(CompleteEvent event) {
			try {
				JobInstance ins = idcJobStore.jobInstanceCompleted(event);
				
				// 本任务日志
				logger.log(event.getInstanceId(), event.getMessage());
				logger.log(event.getInstanceId(), "任务结束, 执行结果: {}", event.getFinalStatus());

				// 主任务日志
				if (ins.getTaskType() == TaskType.SUB_TASK) {
					logger.log(ins.getMainInstanceId(), "[{}] 执行结束, 执行结果: {}", ins.getJobName(), event.getFinalStatus());
					// 期望的完成状态
					List<JobInstanceStatus> expectStatus = Arrays.asList(JobInstanceStatus.SKIPPED, JobInstanceStatus.FINISHED);
					if(expectStatus.contains(ins.getStatus())) {
						JobInstance mainIns = idcJobStore.retrieveIDCJobInstance(ins.getMainInstanceId());
						List<TaskKey> subTaskKeys = pluginService.getSuccessors(mainIns.getTaskKey(), ins.getTaskKey());
						for (TaskKey tk : subTaskKeys) {
							try {
								scheduleSubTask(tk, ins.getMainInstanceId());
							} catch (SchedulerException e) {
								logger.log(ins.getMainInstanceId(), "调度子任务 {} 时出错: {}", tk, e.getMessage());
							}
						}
					}
				}
			} catch (JobPersistenceException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
