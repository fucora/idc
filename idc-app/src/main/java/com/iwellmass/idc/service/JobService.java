package com.iwellmass.idc.service;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_PARAMETER;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.app.model.ComplementRequest;
import com.iwellmass.idc.app.model.ExecutionRequest;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleStatus;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.quartz.JobKeyGenerator;
import com.iwellmass.idc.repo.JobDependencyRepository;
import com.iwellmass.idc.repo.JobRepository;
import com.iwellmass.idc.scheduler.StdJobKeyGenerator;

@Service
public class JobService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

	@Inject
	private JobRepository jobRepository;

	@Inject
	private JobDependencyRepository dependencyRepo;
	
	@Inject
	private IDCPlugin idcPlugin;

	@Inject
	private Scheduler scheduler;

	@Inject
	private TaskFactory taskFactory;

	@Inject
	private JobKeyGenerator jobPKGenerator;

	public void schedule(Job job) throws AppException {
		
		Task task = taskFactory.getOrCreateTask(job.getTaskKey());
		Assert.isTrue(task != null, "无法获取 Task 信息");
		
		JobKey jobKey = jobPKGenerator.generate(job);
		Assert.isTrue(jobRepository.findOne(jobKey) == null, "不可重复调度任务");
		
		validate(jobKey, job.getDependencies());

		LOGGER.info("创建调度任务 {}", jobKey);

		job.setJobKey(jobKey);
		job.setCreateTime(LocalDateTime.now());
		job.setUpdateTime(null);
		
		try {
			if (job.getDispatchType() == DispatchType.MANUAL) {
				idcPlugin.addJob(job);
			} else {
				Trigger trigger = idcPlugin.buildTrigger(job, true);
				
				TriggerState state = scheduler.getTriggerState(trigger.getKey());
				if (state != TriggerState.NONE) {
					throw new AppException("不可重复创建调度任务");
				}
				scheduler.scheduleJob(trigger);
			}
		} catch(AppException e) {
			throw e;
		} catch (SchedulerException e) {
			throw new AppException("无法调度任务: " + e.getMessage(), e);
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			throw new AppException("生成 Cron 表达式时错误, " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new AppException("调度失败: " + e.getMessage());
		}
	}

	@Transactional
	public void reschedule(Job job) {

		JobKey jobPk = jobPKGenerator.generate(job);
		
		validate(jobPk, job.getDependencies());

		// 没有正在执行的任务计划便可以重新调度计划任务
		Job pj = jobRepository.findOne(jobPk);
		if (pj != null && pj.getStatus() != ScheduleStatus.NONE) {
			Assert.isTrue(pj.getStatus() == ScheduleStatus.PAUSED, "任务未冻结");
		}
		
		job.setJobKey(jobPk);
		job.setUpdateTime(LocalDateTime.now());

		LOGGER.info("重新调度任务 {}", jobPk);
		
		try {
			Trigger trigger = idcPlugin.buildTrigger(job, true);
			if (pj.getStatus() == ScheduleStatus.NONE) {
				scheduler.scheduleJob(trigger);
			} else {
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			}
		} catch (ParseException e) {
			throw new AppException("生成 Cron 表达式时错误, " + e.getMessage());
		} catch (SchedulerException e) {
			throw new AppException("无法调度任务: " + e.getMessage(), e);
		}
		
	}
	
	@Transactional
	public void reschedule(JobKey jobKey) {
		// 没有正在执行的任务计划便可以重新调度计划任务
		Job job = jobRepository.findOne(jobKey);
		if (job != null && job.getStatus() != ScheduleStatus.NONE) {
			Assert.isTrue(job.getStatus() == ScheduleStatus.PAUSED, "任务未冻结");
		}

		LOGGER.info("重新调度任务 {}", jobKey);
		
		try {
			Trigger trigger = idcPlugin.buildTrigger(job, false);

			if (job.getStatus() == ScheduleStatus.NONE) {
				scheduler.scheduleJob(trigger);
			} else {
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			}
		} catch (ParseException e) {
			throw new AppException("生成 Cron 表达式时错误, " + e.getMessage());
		} catch (SchedulerException e) {
			throw new AppException("无法调度任务: " + e.getMessage(), e);
		}
	}

	private void validate(JobKey jobPk, List<JobDependency> deps) {

		if (Utils.isNullOrEmpty(deps)) {
			return;
		}

		TriggerKey triggerKey = new TriggerKey(jobPk.getJobId(), jobPk.getJobGroup());
		// 初始化
		DirectedAcyclicGraph<TriggerKey, Dependency> depGraph = new DirectedAcyclicGraph<>(Dependency.class);
		List<JobDependency> existingDeps = dependencyRepo.findAll();
		if (existingDeps != null) {
			for (JobDependency dep : existingDeps) {
				TriggerKey srcPk = new TriggerKey(dep.getSrcJobId(), dep.getSrcJobGroup());
				TriggerKey targetPk = new TriggerKey(dep.getJobId(), dep.getJobGroup());
				depGraph.addVertex(srcPk);
				depGraph.addVertex(targetPk);
				depGraph.addEdge(srcPk, targetPk);
			}
		}

		// 检查依赖
		depGraph.addVertex(triggerKey);
		for (JobDependency dep : deps) {
			TriggerKey target = new TriggerKey(dep.getJobId(), dep.getJobGroup());
			try {
				depGraph.addVertex(target);
				depGraph.addEdge(triggerKey, target);
			} catch (IllegalArgumentException e) {
				throw new AppException("无法添加 " + triggerKey + " -> " + target + " 依赖: " + e.getMessage());
			}
			dep.setSrcJobId(triggerKey.getName());
			dep.setSrcJobGroup(triggerKey.getGroup());
		}
	}

	@Transactional
	public void unschedule(JobKey jobKey) throws AppException {
		try {
			LOGGER.info("撤销调度任务 {}", jobKey);
			boolean result = scheduler.unscheduleJob(new TriggerKey(jobKey.getJobId(), jobKey.getJobGroup()));
			if (!result) {
				LOGGER.warn("调度任务 {} 不存在", jobKey);
			}
		} catch (SchedulerException e) {
			throw new AppException(e);
		}
	}

	@Transactional
	public void pause(JobKey jobKey, boolean forcePause) {
		LOGGER.info("冻结调度任务 {}", jobKey);
		TriggerKey triggerKey = new TriggerKey(jobKey.getJobId(), jobKey.getJobGroup());
		try {
			if (!forcePause) {
				TriggerState state = scheduler.getTriggerState(triggerKey);
				Assert.isTrue(state != TriggerState.BLOCKED, "等待任务执行完毕");
			} else {
				// TODO 强制取消子任务
			}
			scheduler.pauseTrigger(triggerKey);
		} catch (SchedulerException e) {
			throw new AppException("无法冻结此任务");
		}
	}

	@Transactional
	public void resume(JobKey jobKey) {
		TriggerKey tk = new TriggerKey(jobKey.getJobId(), jobKey.getJobGroup());
		try {
			Job job = jobRepository.findOne(jobKey);
			Assert.isTrue(job != null, "调度任务 %s 不存在", jobKey);
			scheduler.resumeTrigger(tk);
			TriggerState state = scheduler.getTriggerState(tk);
			job.setStatus(ScheduleStatus.values()[state.ordinal()]);
			jobRepository.save(job);
		} catch (SchedulerException e) {
			throw new AppException("无法恢复此任务");
		}
	}

	public void execute(ExecutionRequest request) {
		
		JobKey jobPk = StdJobKeyGenerator.valueOf(request);
		
		TriggerKey tk = new TriggerKey(jobPk.getJobId(), jobPk.getJobGroup());
		
		Job job = jobRepository.findOne(jobPk);

		Assert.isTrue(job != null, "调度任务 %s 不存在", jobPk);

		ScheduleStatus status = job.getStatus();

		Assert.isTrue(status != ScheduleStatus.PAUSED, "执行失败, 任务已冻结");
		Assert.isTrue(status != ScheduleStatus.BLOCKED, "执行失败, 存在正在执行的任务实例");

		
		LocalDateTime loadDate = job.getScheduleType().parse(request.getLoadDate());

		try {

			TriggerState state = scheduler.getTriggerState(tk);

			// ~~ 调度参数 ~~
			JobDataMap jdm = new JobDataMap();
			CONTEXT_PARAMETER.applyPut(jdm, request.getJobParameter());
			CONTEXT_LOAD_DATE.applyPut(jdm, loadDate);
			Trigger trigger = TriggerBuilder.newTrigger()
				.usingJobData(jdm)
				.withIdentity(tk)
				.forJob(request.getTaskId(), request.getGroupId()).build();

			if (state == TriggerState.NONE) {
				scheduler.scheduleJob(trigger);
			} else {
				scheduler.rescheduleJob(tk, trigger);
			}
		} catch (SchedulerException e) {
			throw new AppException("执行失败: " + e.getMessage());
		}
		
		
	}

	public static String toCronExpression(ScheduleProperties scheduleProperties) {
		LocalTime duetime = LocalTime.parse(scheduleProperties.getDuetime(), DateTimeFormatter.ISO_TIME);
		switch (scheduleProperties.getScheduleType()) {
		case MONTHLY: {
			List<Integer> days = scheduleProperties.getDays();
			Assert.isFalse(Utils.isNullOrEmpty(days), "月调度配置不能为空");
			
			boolean isLast = days.stream().filter(i -> i < 0).count() == 1;
			if(isLast && days.size() > 1) {
				throw new AppException("最后 T 天不能使用组合配置模式");
			};
			
			return String.format("%s %s %s %s * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(),
				isLast ? days.get(0) == -1 ? "L" : "L" + (days.get(0) + 1)
					: String.join(",", days.stream().map(String::valueOf).collect(Collectors.toList())));
		}
		case WEEKLY: {
			throw new UnsupportedOperationException("not supported yet");
		}
		case DAILY:
			return String.format("%s %s %s * * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour());
		default:
			throw new AppException("未指定周期调度类型, 接收的周期调度类型" + Arrays.asList(ScheduleType.values()));
		}
	}

	public void complement(ComplementRequest request) {
		/*
		 * try { String taskId = request.getTaskId(); String groupId =
		 * request.getGroupId();
		 *
		 * Trigger mainTrigger =
		 * scheduler.getTrigger(buildTriggerKey(JobInstanceType.CRON, taskId, groupId));
		 * Assert.isTrue(mainTrigger != null, "任务未提交");
		 *
		 * ScheduleBuilder<? extends Trigger> sbt = mainTrigger.getScheduleBuilder();
		 *
		 * TriggerKey triggerKey = buildTriggerKey(JobInstanceType.COMPLEMENT, taskId,
		 * groupId);
		 *
		 * Trigger trigger = scheduler.getTrigger(triggerKey);
		 *
		 * Assert.isTrue(trigger == null, "存在正在执行的补数任务");
		 *
		 * TriggerBuilder<?> complementTriggerBuilder =
		 * TriggerBuilder.newTrigger().withIdentity(triggerKey)
		 * .forJob(mainTrigger.getJobKey()).withSchedule(sbt)
		 * .startAt(toDate(LocalDateTime.of(request.getStartTime(), LocalTime.MIN)))
		 * .endAt(toDate(LocalDateTime.of(request.getEndTime(), LocalTime.MAX)));
		 *
		 * if (trigger == null) {
		 * scheduler.scheduleJob(complementTriggerBuilder.build()); } else {
		 * scheduler.rescheduleJob(triggerKey, complementTriggerBuilder.build()); }
		 *
		 * } catch (SchedulerException e) { throw new AppException("补数异常: " +
		 * e.getMessage()); }
		 */
	}
}
