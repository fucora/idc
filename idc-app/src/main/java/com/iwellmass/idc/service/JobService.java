package com.iwellmass.idc.service;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_PARAMETER;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_DISPATCH_TYPE;
import static com.iwellmass.idc.quartz.IDCPlugin.toDate;

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
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.app.model.ComplementRequest;
import com.iwellmass.idc.app.model.ExecutionRequest;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.JobScript;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleStatus;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.quartz.IDCPluginContext.Dependency;
import com.iwellmass.idc.quartz.JobPKGenerator;
import com.iwellmass.idc.repo.JobDependencyRepository;
import com.iwellmass.idc.repo.JobRepository;
import com.iwellmass.idc.scheduler.StdJobPKGenerator;

@Service
public class JobService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

	@Inject
	private JobRepository jobRepository;

	@Inject
	private JobDependencyRepository dependencyRepo;

	@Inject
	private Scheduler scheduler;

	@Inject
	private JobScriptFactory jobScriptFactory;

	@Inject
	private JobPKGenerator jobPKGenerator;

	@Inject
	private PlatformTransactionManager transactionManager;
	
	public void schedule(Job job) throws AppException {


		JobPK jobPK = jobPKGenerator.generate(job);
		Assert.isTrue(jobRepository.findOne(jobPK) == null, "不可重复调度任务");
		
		validate(jobPK, job.getDependencies());

		LOGGER.info("创建调度任务 {}", jobPK);

		job.setJobPK(jobPK);
		job.setCreateTime(LocalDateTime.now());
		job.setUpdateTime(null);
		
		
		doScheduleJob(job, false);
	}

	@Transactional
	public void reschedule(Job job) {

		JobPK jobPk = jobPKGenerator.generate(job);
		
		validate(jobPk, job.getDependencies());

		// 没有正在执行的任务计划便可以重新调度计划任务
		Job pj = jobRepository.findOne(jobPk);
		if (pj != null) {
			Assert.isTrue(pj.getStatus() == ScheduleStatus.PAUSED, "任务未冻结");
		}
		
		job.setJobPK(jobPk);
		job.setUpdateTime(LocalDateTime.now());

		LOGGER.info("重新调度任务 {}", jobPk);
		
		doScheduleJob(job, true);
	}

	private void doScheduleJob(Job job, boolean replace) {

		// TODO 以后扩展
		JobScript script = jobScriptFactory.getJobScript(job);
		if (script == null) {
			throw new AppException("未找到对应的 JobScript");
		}

		JobPK jobPK = jobPKGenerator.generate(job);
		TriggerKey triggerKey = new TriggerKey(jobPK.getJobId(), jobPK.getJobGroup());

		// 默认值
		ScheduleProperties sp = job.getScheduleProperties();
		job.setScheduleType(sp.getScheduleType());

		
		TransactionTemplate template = new TransactionTemplate(transactionManager);
		template.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				// save idc job
				jobRepository.save(job);
				// save dependencies
				dependencyRepo.cleanJobDependencies(jobPK.getJobId(), jobPK.getJobGroup());
				dependencyRepo.save(job.getDependencies());
			}
		});
		
		
		boolean success = false;
		try {
			if (job.getDispatchType() == DispatchType.AUTO) {
				
				
				JobDataMap newJobDataMap = new JobDataMap();
				
				JOB_DISPATCH_TYPE.applyPut(newJobDataMap, job.getDispatchType());
				
				// 让 QZ 可以知道调度类型
				TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
						.withIdentity(jobPK.getJobId(), jobPK.getJobGroup())
						.forJob(script.getScriptId(), script.getScriptGroup())
						.usingJobData(newJobDataMap)
						.withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(toCronExpression(sp)))
								.withMisfireHandlingInstructionIgnoreMisfires());

				
				if (job.getStartTime() != null) {
					triggerBuilder.startAt(toDate(job.getStartTime()));
				}
				if (job.getEndTime() != null) {
					triggerBuilder.endAt(toDate(job.getEndTime()));
				}
				
				// 保存到 quartz
				if (!scheduler.checkExists(triggerKey)) {
					scheduler.scheduleJob(triggerBuilder.build());
				} else {
					if (replace) {
						scheduler.rescheduleJob(triggerKey, triggerBuilder.build());
					} else {
						throw new AppException("不可重复创建调度任务");
					}
				}
			}
			success = true;
		} catch (AppException e) {
			throw e;
		} catch (ObjectAlreadyExistsException e) {
			LOGGER.error(e.getMessage());
			throw new AppException("不可重复创建调度任务");
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			throw new AppException("生成 Cron 表达式时错误, " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new AppException("调度失败: " + e.getMessage());
		} finally {
			if (!success) {
				try {
					scheduler.unscheduleJob(triggerKey);
				} catch (Throwable e) {
					// ignore
				}
			}
		}
	}

	private void validate(JobPK jobPk, List<JobDependency> deps) {

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
	public void unschedule(JobPK jobKey) throws AppException {
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
	public void pause(JobPK jobKey, boolean forcePause) {
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
	public void resume(JobPK jobKey) {
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
		
		JobPK jobPk = StdJobPKGenerator.valueOf(request);
		
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
			JOB_DISPATCH_TYPE.applyPut(jdm, job.getDispatchType());
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

	public String toCronExpression(ScheduleProperties scheduleProperties) {
		LocalTime duetime = LocalTime.parse(scheduleProperties.getDuetime(), DateTimeFormatter.ISO_TIME);
		switch (scheduleProperties.getScheduleType()) {
		case MONTHLY: {
			List<Integer> days = scheduleProperties.getDays();
			Assert.isFalse(Utils.isNullOrEmpty(days), "月调度配置不能为空");
			return String.format("%s %s %s %s * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(),
					String.join(",", days.stream().map(i -> i + "").collect(Collectors.toList())));
		}
		case WEEKLY: {
			List<Integer> days = scheduleProperties.getDays();
			Assert.isFalse(Utils.isNullOrEmpty(days), "周调度配置不能为空");
			return String.format("%s %s %s ? * %s *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(),
					String.join(",", days.stream().map(i -> i + "").collect(Collectors.toList())));
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
