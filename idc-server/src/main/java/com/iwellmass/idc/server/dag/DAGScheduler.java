package com.iwellmass.idc.server.dag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.CompleteEvent;

public class DAGScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DAGScheduler.class);

//	public void execute() {
//
//		this.jobLogger = IDCContextKey.CONTEXT_LOGGER.applyGet(context);
//		this.executorService = IDCContextKey.CONTEXT_EXECUTOR.applyGet(context);
//
//		LOGGER.info("执行 {} ", context.getJobDetail().getKey());
//		JobInstance jobInstance = IDCContextKey.CONTEXT_INSTANCE.applyGet(context);
//
//		// 工作流任务
//		if (jobInstance.getTaskType() == TaskType.WORKFLOW) {
//			// TODO获取所有子任务 // 执行它们
//			executeSubJobs(jobInstance);
//		} // 独立任务
//		else {
//			dispatcher(jobInstance);
//		}
//	}
//
//	// 执行子任务 private void executeSubJobs(JobInstance instance) {
//
//	DAGSchedulingEngine dagEngine = null;
//
//	// TODO 获取可用的子任务
//
//	List<Job> subJobs = null;
//
//	if(Utils.isNullOrEmpty(subJobs))
//	{ // TODO 通知已经执行完毕 JobExecutionLog log =
//		new JobExecutionLog();
//		log.setMessage("执行完毕");
//	}else
//	{
//		Scheduler scheduler = QuartzContext.getScheduler();
//
//		JobDetail jobDetail = null;
//
//		Trigger trigger = newTrigger().withSchedule(simpleSchedule()).forJob(jobDetail).startNow().build();
//		try {
//			scheduler.scheduleJob(jobDetail, trigger);
//		} catch (SchedulerException e) {
//			LOGGER.error("hahaha");
//		}
//	}}
//
//	// 使用 eureka 作为我们的执行器 private void dispatcher(JobInstance jobInstance) {
//	ExecutionRequest request = new ExecutionRequest();request.setInstanceId(jobInstance.getId());request.setTaskId(jobInstance.getTaskId());request.setGroup(jobInstance.getGroupId());request.setParameters(jobInstance.getParameters());try
//	{
//		executorService.execute(request);
//		jobLogger.info("派发任务成功");
//	}catch(
//	Throwable e)
//	{
//		CompleteEvent event = new CompleteEvent();
//		event.setEndTime(LocalDateTime.now());
//		event.setInstanceId(jobInstance.getId());
//		event.setFinalStatus(JobInstanceStatus.FAILED);
//		event.setMessage("派发任务失败:" + e.getMessage());
//		IDCPlugin.getStatusManager().fireJobComplete(event);
//	}
//}

}
