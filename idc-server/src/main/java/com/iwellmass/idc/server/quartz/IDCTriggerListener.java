package com.iwellmass.idc.server.quartz;

import static com.iwellmass.idc.server.quartz.IDCPlugin.buildInstanceId;
import static com.iwellmass.idc.server.quartz.IDCPlugin.getTriggerType;
import static com.iwellmass.idc.server.quartz.IDCPlugin.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.iwellmass.idc.model.BarrierFlag;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobExecutionBarrier;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobStatusEvent;
import com.iwellmass.idc.model.ScheduleStatus;
import com.iwellmass.idc.repo.JobBarrierRepository;
import com.iwellmass.idc.repo.JobDependencyRepository;
import com.iwellmass.idc.repo.JobExecuteLogRepository;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.repo.JobRepository;

import ch.qos.logback.core.status.StatusManager;

@Component
public class IDCTriggerListener extends TriggerListenerSupport implements ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCTriggerListener.class);

	private ApplicationContext applicationContext;

	@Inject
	private JobRepository jobRepository;

	@Inject
	private JobInstanceRepository jobInstanceRepository;

	@Inject
	private JobDependencyRepository jobDependencyRepository;

	@Inject
	private JobBarrierRepository jobExecutionBarrierRepository;

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {

		// 更新任务状态
		JobKey jobKey = context.getJobDetail().getKey();
		Job job = jobRepository.findOne(jobKey.getName(), jobKey.getGroup());
		Optional.ofNullable(trigger.getPreviousFireTime()).map(IDCPlugin::toLocalDateTime)
				.ifPresent(job::setPrevLoadDate);
		Optional.ofNullable(trigger.getNextFireTime()).map(IDCPlugin::toLocalDateTime).ifPresent(job::setNextLoadDate);
		job.setStatus(ScheduleStatus.NORMAL);
		jobRepository.save(job);

		// 实例信息
		String taskId = job.getTaskId();
		String groupId = job.getGroupId();
		LocalDateTime loadDate = toLocalDateTime(context.getScheduledFireTime());
		String instanceId = buildInstanceId(taskId, groupId, loadDate);

		LOGGER.info("创建/更新 {} 实例", instanceId);

		// 生成实例
		JobInstance jobInstance = new JobInstance();
		jobInstance.setId(instanceId);
		jobInstance.setTaskId(job.getTaskId());
		jobInstance.setGroupId(job.getGroupId());
		jobInstance.setTaskType(job.getTaskType());
		jobInstance.setAssignee(job.getAssignee());
		jobInstance.setLoadDate(loadDate); // 业务日期
		jobInstance.setStatus(JobInstanceStatus.NEW); // 任务状态
		jobInstance.setType(getTriggerType(trigger));// 实例类型
		jobInstanceRepository.save(jobInstance);

		// 解析依赖关系
		List<JobDependency> dependencies = jobDependencyRepository.findDependencies(taskId, groupId);
		if (!dependencies.isEmpty()) {
			suspendOnBarriers(jobInstance, dependencies, trigger, context);
		} else {
			// 初始化 context，然后派发任务
			initJobExecutionContext(job, jobInstance, context);
		}
	}

	@Override
	public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
		return IDCContextKey.EXECUTION_BLOCK.applyGet(context);
	}

	@Override
	public String getName() {
		return IDCTriggerListener.class.getSimpleName();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private void initJobExecutionContext(Job job, JobInstance jobInstance, JobExecutionContext context) {
		// 初始化组件
		IDCContextKey.CONTEXT_JOB.applyPut(context, job);
		IDCContextKey.CONTEXT_INSTANCE.applyPut(context, jobInstance);
		IDCContextKey.CONTEXT_LOGGER.applyPut(context,
				new IDCJobLogger(jobInstance.getId(), applicationContext.getBean(JobExecuteLogRepository.class)));
		IDCContextKey.keys().stream().filter(k -> {
			return IDCContextKey.GROUP_AUTO_INJECT.equals(k.group());
		}).forEach(k -> {
			try {
				context.put(k.key(), applicationContext.getBean(k.valueType()));
			} catch (Throwable e) {
				LOGGER.warn("初始化 {} 出错: {}", e.getMessage(), e);
			}
		});
	}

	private void suspendOnBarriers(JobInstance instance, List<JobDependency> dependencies, Trigger trigger, JobExecutionContext context) {
		// sentinel barrier
		JobExecutionBarrier sentinel = new JobExecutionBarrier();
		sentinel.setInstanceId(instance.getId());
		sentinel.setTaskId(instance.getTaskId());
		sentinel.setGorupId(instance.getGroupId());
		sentinel.setLoadDate(instance.getLoadDate());
		sentinel.setFlag(BarrierFlag.SENTINEL);
		jobExecutionBarrierRepository.save(sentinel);
		// dependence barriers
		List<JobExecutionBarrier> barriers = dependencies.stream().map(jd -> {
			JobExecutionBarrier barrier = new JobExecutionBarrier();
			barrier.setInstanceId(instance.getId());
			barrier.setTaskId(jd.getDependencyTaskId());
			barrier.setGorupId(jd.getDependencyGroupId());
			barrier.setLoadDate(instance.getLoadDate());
			barrier.setFlag(BarrierFlag.DEPENDENCY);
			return barrier;
		}).collect(Collectors.toList());
		jobExecutionBarrierRepository.save(barriers);
		IDCContextKey.EXECUTION_BLOCK.applyPut(context, true);

		try {
			context.getScheduler().pauseTrigger(trigger.getKey());
			JobStatusEvent event = new JobStatusEvent();
			event.setInstanceId(instance.getId());
			IDCPlugin.getStatusManager().fireJobBlocked(event);
		} catch (SchedulerException e) {
			// TODO
			LOGGER.error("", e);
		}
	}
}
