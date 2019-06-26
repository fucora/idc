//package com.iwellmass.idc.app.scheduler;
//
//import static com.iwellmass.idc.app.scheduler.IDCContextKey.CONTEXT_INSTANCE;
//import static com.iwellmass.idc.app.scheduler.IDCContextKey.IDC_PLUGIN;
//import static com.iwellmass.idc.app.scheduler.IDCContextKey.JOB_ENV;
//import static com.iwellmass.idc.app.scheduler.IDCContextKey.JOB_TRIGGER_INSTRUCTION;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import org.quartz.CronScheduleBuilder;
//import org.quartz.CronTrigger;
//import org.quartz.JobBuilder;
//import org.quartz.JobDataMap;
//import org.quartz.JobDetail;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.quartz.JobKey;
//import org.quartz.ObjectAlreadyExistsException;
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.quartz.SimpleScheduleBuilder;
//import org.quartz.SimpleTrigger;
//import org.quartz.Trigger;
//import org.quartz.TriggerBuilder;
//import org.quartz.listeners.JobListenerSupport;
//import org.quartz.listeners.SchedulerListenerSupport;
//import org.quartz.listeners.TriggerListenerSupport;
//import org.quartz.spi.ClassLoadHelper;
//import org.quartz.spi.SchedulerPlugin;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.alibaba.fastjson.JSON;
//import com.iwellmass.idc.app.model.Job;
//import com.iwellmass.idc.app.model.JobInstance;
//import com.iwellmass.idc.app.model.Task;
//import com.iwellmass.idc.app.model.TaskKey;
//import com.iwellmass.idc.app.model.WorkflowEdge;
//import com.iwellmass.idc.app.service.StdStatusService;
//import com.iwellmass.idc.app.util.IDCUtils;
//import com.iwellmass.idc.executor.CompleteEvent;
//import com.iwellmass.idc.executor.IDCStatusService;
//import com.iwellmass.idc.executor.ProgressEvent;
//import com.iwellmass.idc.executor.StartEvent;
//import com.iwellmass.idc.model.JobInstanceStatus;
//import com.iwellmass.idc.model.TaskType;
//
//import lombok.Getter;
//import lombok.Setter;
//
//public class IDCPlugin implements SchedulerPlugin, IDCConstants {
//
//	private static final Logger LOGGER = LoggerFactory.getLogger(IDCPlugin.class);
//
//	// ~~ init from factory ~~
//	private Scheduler scheduler;
//	private IDCJobStore idcJobStore;
//	
//	// ~~ internal component ~~
//	@Getter
//	private IDCStatusService statusService;
//	
//	@Getter
//	private IDCPluginService pluginService;
//	
//	@Setter
//	@Getter
//	private IDCLogger logger = new SimpleIDCLogger();
//	
//	public IDCPlugin(IDCPluginService pluginService) {
//		this.pluginService = pluginService;
//	}
//	
//	public void initialize(IDCJobStore store) {
//		this.idcJobStore = store;
//	}
//	
//	@Override
//	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
//		LOGGER.info("加载 IDCPlugin...");
//		
//		Objects.requireNonNull(idcJobStore, "IDCJobStore cannot be null");
//		Objects.requireNonNull(pluginService, "IDCSchedulerService cannot be null");
//		
//		// 验证
//		IDCPluginConfig config = pluginService.getConfig();
//		LOGGER.info("IDC数据库版本 {}", config.getVersion());
//		
//		// set up context
//		IDC_PLUGIN.applyPut(scheduler.getContext(), this);
//		
//		// add listeners
//		scheduler.getListenerManager().addJobListener(new IDCJobListener());
//		scheduler.getListenerManager().addTriggerListener(new IDCTriggerListener());
//		scheduler.getListenerManager().addSchedulerListener(new IDCSchedulerListener());
//		
//		// ~~ 系统任务 ~~
//		scheduler.addJob(JobBuilder.newJob(IDCWorkflowGuardJob.class)
//			.withIdentity(WorkflowEdge.END.getTaskId(), WorkflowEdge.END.getTaskGroup()).requestRecovery()
//			.storeDurably().build(), true);
//		
//		this.scheduler = scheduler;
//		this.statusService = (IDCStatusService) Proxy.newProxyInstance(IDCPlugin.class.getClassLoader(), new Class[] {IDCStatusService.class}, new InvocationHandler() {
//			private StdStatusService ss = new StdStatusService();
//			@Override
//			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//				try {
//					LOGGER.info("Get Event {}", args);
//					return method.invoke(ss, args);
//				} catch(Throwable e) {
//					LOGGER.error("通知失败," + e.getMessage(), e);
//				}
//				return null;
//			}
//		});
//	}
//
//	@Override
//	public void start() {
//		LOGGER.info("启动 IDCPlugin");
//	}
//
//	@Override
//	public void shutdown() {
//		LOGGER.info("停止 IDCPlugin");
//	}
//	
//	/** 
//	 * 刷新任务
//	 */
//	public void refresh(Task task) throws SchedulerException {
//		if (task.getTaskType() == TaskType.WORKFLOW) {
//			JobDetail jobDetail = JobBuilder
//					.newJob(IDCWorkflowJob.class)
//					//.usingJobData(jobData)
//					.withIdentity(task.getTaskId(), task.getTaskGroup())
//					.storeDurably()
//					.build();
//			scheduler.addJob(jobDetail, true);
//		} else {
//			JobDetail jobDetail = JobBuilder
//					.newJob(IDCDispatcherJob.class)
//					//.usingJobData(jobData)
//					.withIdentity(task.getTaskId(), task.getTaskGroup())
//					.storeDurably()
//					.build();
//			scheduler.addJob(jobDetail, true);
//		}
//	}
//	
//	public void scheduleSubTask(TaskKey subTaskKey, Integer mainInstanceId) throws SchedulerException {
//		JobInstance mainJobIns = idcJobStore.retrieveIDCJobInstance(mainInstanceId);
//		scheduleSubTask(subTaskKey, mainJobIns);
//	}
//	
//	public void scheduleSubTask(TaskKey subTaskKey, JobInstance mainJobIns) throws SchedulerException {
//		
//		String wfId = mainJobIns.getWorkflowId();
//		Task mainTask = pluginService.getTask(mainJobIns.getTaskKey());
//		
//		// 如果快照已经改变
//		if (!wfId.equals(mainTask.getWorkflowId())) {
//			statusService.complete(CompleteEvent.failureEvent(mainJobIns.getInstanceId())
//				.setMessage("工作流程已经改变，结束此任务"));
//			return;
//		}
//		
//		JobKey subJobKey = IDCUtils.getSubJobKey(mainJobIns.getJobKey(), subTaskKey);
//		Trigger trigger = null;
//		
//		if (WorkflowEdge.CTRL_JOIN_GROUP.equals(subTaskKey.getTaskGroup())) {
//			
//			List<JobKey> barrierKeys = pluginService.getPredecessors(mainJobIns.getTaskKey(), subTaskKey)
//					.stream().map(tk -> {
//						return IDCUtils.getSubJobKey(mainJobIns.getJobKey(), tk);
//					}).collect(Collectors.toList());
//			
//			JoinEnv joinEnv = new JoinEnv();
//			joinEnv.setInstanceId(mainJobIns.getInstanceId());
//			joinEnv.setShouldFireTime(mainJobIns.getShouldFireTime());
//			joinEnv.setBarrierKeys(barrierKeys);
//			joinEnv.setJoinKey(subTaskKey);
//			joinEnv.setMainTaskKey(mainJobIns.getTaskKey());
//
//			JobDataMap jobData = new JobDataMap();
//			JOB_TRIGGER_INSTRUCTION.applyPut(jobData, IDCTriggerInstruction.GUARD);
//			JOB_ENV.applyPut(jobData, JSON.toJSONString(joinEnv));
//			trigger = buildSimpleTrigger(subJobKey, WorkflowEdge.CTRL_JOIN, jobData);
//			
//		} else if (subTaskKey.equals(WorkflowEdge.END)) {
//			// 添加一个 guard trigger
//			List<JobKey> barrierKeys = pluginService.getPredecessors(mainJobIns.getTaskKey(), WorkflowEdge.END)
//				.stream().map(tk -> {
//					return IDCUtils.getSubJobKey(mainJobIns.getJobKey(), tk);
//				}).collect(Collectors.toList());
//
//			GuardEnv guardEnv = new GuardEnv();
//			guardEnv.setInstanceId(mainJobIns.getInstanceId());
//			guardEnv.setShouldFireTime(mainJobIns.getShouldFireTime());
//			guardEnv.setBarrierKeys(barrierKeys);
//
//			JobDataMap jobData = new JobDataMap();
//			JOB_TRIGGER_INSTRUCTION.applyPut(jobData, IDCTriggerInstruction.GUARD);
//			JOB_ENV.applyPut(jobData, JSON.toJSONString(guardEnv));
//			trigger = buildSimpleTrigger(subJobKey, WorkflowEdge.END, jobData);
//		} else {
//			Task subTask = pluginService.getTask(subTaskKey);
//			if (subTask == null) {
//				throw new JobExecutionException("子任务不存在");
//			}
//			
//			SubEnv subEnv = new SubEnv();
//			// mark
//			subEnv.setMainInstanceId(mainJobIns.getInstanceId());
//			
//			// build Simple
//			JobDataMap jobData = new JobDataMap();
//			JOB_ENV.applyPut(jobData, JSON.toJSONString(subEnv));
//			JOB_TRIGGER_INSTRUCTION.applyPut(jobData, IDCTriggerInstruction.SUB);
//			
//			//子任务Key
//			trigger = buildSimpleTrigger(subJobKey, subTaskKey, jobData);
//		}
//		// just schedule
//		try {
//			scheduler.scheduleJob(trigger);
//		} catch (ObjectAlreadyExistsException e) {
//			LOGGER.debug("任务" + subJobKey + "已经触发过了，忽略...");
//		}
//	}
//	
//	private Trigger buildAutoTrigger(Job job) {
//		// 构建 TriggerBuilder
//		TriggerBuilder<CronTrigger> builder = TriggerBuilder.newTrigger()
//			.withIdentity(IDCUtils.toTriggerKey(job.getJobKey()))
//			.forJob(job.getTaskId(), job.getTaskGroup())
//			.withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpr()).withMisfireHandlingInstructionIgnoreMisfires());
//		// 设置开始时间
//		Optional.ofNullable(job.getStartTime()).map(IDCUtils::toDate).ifPresent(builder::startAt);
//		// 设置结束时间
//		Optional.ofNullable(job.getEndTime()).map(IDCUtils::toDate).ifPresent(builder::endAt);
//		
//		return builder.build();
//	}
//	
//	private Trigger buildSimpleTrigger(JobKey jobKey, TaskKey taskKey, JobDataMap jobData) {	// build Trigger
//		// 构建常量
//		TriggerBuilder<SimpleTrigger> builder = TriggerBuilder.newTrigger()
//			.withIdentity(jobKey.getJobId(), jobKey.getJobGroup())
//			.forJob(taskKey.getTaskId(), taskKey.getTaskGroup())
//			.withSchedule(SimpleScheduleBuilder.simpleSchedule());
//		
//		if (jobData != null) {
//			builder.usingJobData(jobData);
//		}
//		return builder.build();
//	}
//	
//	
//	
//	
//	// ~~ Scheduler Listener ~~
//	private class IDCSchedulerListener extends SchedulerListenerSupport {
//		
//		@Override
//		public void schedulerError(String msg, SchedulerException cause) {
//			// TODO 添加到报警信息中
//		}
//	}
//
//	
//	private JobInstance getJobInstance(JobExecutionContext context) {
//		return CONTEXT_INSTANCE.applyGet(context);
//	}
//	
//	private IDCTriggerInstruction getTriggerInstruction(Trigger trigger) {
//		return JOB_TRIGGER_INSTRUCTION.applyGet(trigger.getJobDataMap());
//	}
//
//}
