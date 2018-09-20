package com.iwellmass.idc.service;


import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_DISPATCH_TYPE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_PARAMETER;
import static com.iwellmass.idc.quartz.IDCPlugin.buildCronTriggerKey;
import static com.iwellmass.idc.quartz.IDCPlugin.buildManualTriggerKey;
import static com.iwellmass.idc.quartz.IDCPlugin.toDate;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
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

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.quartz.IDCPluginContext.Dependency;
import com.iwellmass.idc.repo.JobDependencyRepository;
import com.iwellmass.idc.repo.JobRepository;
import com.iwellmass.idc.scheduler.IDCDispatcherJob;

@Service
public class JobService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

	@Inject
	private JobRepository jobRepository;
	
	@Inject
	private JobDependencyRepository dependencyRepo;
	
	@Inject
	private Scheduler scheduler;
	
	@Transactional
	public void schedule(Job job) throws AppException {
		
		LOGGER.info("创建调度任务 {}", job);
		
		LocalDateTime now = LocalDateTime.now();
		// 默认值
		job.setCreateTime(now);
		if (job.getGroupId() == null) {
			job.setGroupId(Job.DEFAULT_GROUP);
		}
		if (job.getContentType() == null) {
			job.setContentType(Job.DEFAULT_CONTENT_TYPE);
		}
		ScheduleProperties sp = job.getScheduleProperties();
		job.setScheduleType(sp.getScheduleType());
		JobKey jobKey = new JobKey(job.getTaskId(), job.getGroupId());
		
		boolean success = false;
		try {
			if(scheduler.checkExists(jobKey)) {
				throw new AppException("不可重复调度任务");
			}
		
			// 计算依赖
			Set<JobDependency> deps = job.getDependencies();
			if (!Utils.isNullOrEmpty(deps)) {
				DirectedAcyclicGraph<JobKey, Dependency> depGraph = loadDependencyGraph();
				depGraph.addVertex(jobKey);
				for (JobDependency dep : deps) {
					JobKey target = new JobKey(dep.getTaskId(), dep.getGroupId());
					try {
						depGraph.addVertex(target);
						depGraph.addEdge(jobKey, target);
					} catch (IllegalArgumentException e) {
						throw new AppException("无法添加 "  + jobKey + " -> " + target + " 依赖: " + e.getMessage(), jobKey);
					}
					dep.setSrcTaskId(jobKey.getName());
					dep.setSrcGroupId(jobKey.getGroup());
				}
			}
			
			// save idc job
			jobRepository.save(job);
			
			// save dependencies
			dependencyRepo.cleanJobDependencies(job.getTaskId(), job.getGroupId());
			dependencyRepo.save(job.getDependencies());
			
			TriggerKey triggerKey = buildCronTriggerKey(sp.getScheduleType(), job.getTaskId(), job.getGroupId());
			
			JobDetail jobDetail = JobBuilder.newJob(IDCDispatcherJob.class)
					.withIdentity(jobKey)
					.requestRecovery()
					.storeDurably()
					.build();
			
			// save scheduler job
			scheduler.addJob(jobDetail, false);
			
			if (job.getDispatchType() == DispatchType.AUTO) {
				// 让 QZ 可以知道调度类型
				Trigger trigger = TriggerBuilder.newTrigger()
						.withIdentity(triggerKey)
						.forJob(jobKey)
						.withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(toCronExpression(sp)))
								.withMisfireHandlingInstructionIgnoreMisfires())
						.startAt(toDate(job.getStartTime()))
						.endAt(toDate(job.getEndTime()))
						.build();
				CONTEXT_DISPATCH_TYPE.applyPut(trigger.getJobDataMap(), job.getDispatchType());
				// 保存到 quartz
				scheduler.scheduleJob(trigger);
			}
			success = true;
		} catch (AppException e) {
			throw e;
		} catch (ObjectAlreadyExistsException e) {
			LOGGER.error(e.getMessage());
			throw new AppException("不可重复调度任务");
		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			throw new AppException("生成 Cron 表达式时错误, " + e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new AppException("调度失败: " + e.getMessage());
		} finally {
			if (!success) {
				try {
					scheduler.deleteJob(jobKey);
				} catch (Throwable e) {
					// ignore
				}
			}
		}
	}
	
	private DirectedAcyclicGraph<JobKey, Dependency> loadDependencyGraph() {
		
		List<JobDependency> deps = dependencyRepo.findAll();
		DirectedAcyclicGraph<JobKey, Dependency> depGraph = new DirectedAcyclicGraph<>(Dependency.class);
		
		if (deps != null) {
			for (JobDependency dep : deps) {
				JobKey sourceVertex = new JobKey(dep.getSrcTaskId(), dep.getSrcGroupId());
				JobKey targetVertex = new JobKey(dep.getTaskId(), dep.getGroupId());
				depGraph.addVertex(sourceVertex);
				depGraph.addVertex(targetVertex);
				depGraph.addEdge(sourceVertex, targetVertex);
			}
		}
		return depGraph;
	}
	
	public void unschedule(JobPK jobKey) throws AppException {
		try {
			LOGGER.info("取消任务 {} 所有调度计划", jobKey);
			boolean result = scheduler.deleteJob(new JobKey(jobKey.getTaskId(), jobKey.getGroupId()));
			if (!result) {
				LOGGER.warn("{} 不存在的调度任务", jobKey);
			}
		} catch (SchedulerException e) {
			throw new AppException(e);
		}
		
	}

	public void complement(ComplementRequest request) {
		/*try {
			String taskId = request.getTaskId();
			String groupId = request.getGroupId();

			Trigger mainTrigger = scheduler.getTrigger(buildTriggerKey(JobInstanceType.CRON, taskId, groupId));
			Assert.isTrue(mainTrigger != null, "任务未提交");

			ScheduleBuilder<? extends Trigger> sbt = mainTrigger.getScheduleBuilder();

			TriggerKey triggerKey = buildTriggerKey(JobInstanceType.COMPLEMENT, taskId, groupId);

			Trigger trigger = scheduler.getTrigger(triggerKey);

			Assert.isTrue(trigger == null, "存在正在执行的补数任务");

			TriggerBuilder<?> complementTriggerBuilder = TriggerBuilder.newTrigger().withIdentity(triggerKey)
					.forJob(mainTrigger.getJobKey()).withSchedule(sbt)
					.startAt(toDate(LocalDateTime.of(request.getStartTime(), LocalTime.MIN)))
					.endAt(toDate(LocalDateTime.of(request.getEndTime(), LocalTime.MAX)));

			if (trigger == null) {
				scheduler.scheduleJob(complementTriggerBuilder.build());
			} else {
				scheduler.rescheduleJob(triggerKey, complementTriggerBuilder.build());
			}

		} catch (SchedulerException e) {
			throw new AppException("补数异常: " + e.getMessage());
		}*/
	}

	public void lock(JobPK jobKey) {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public void unlock(JobPK jobKey) {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public void execute(ExecutionRequest request) {
		String taskId = request.getTaskId();
		String groupId = request.getGroupId();
		
		JobKey jobKey = new JobKey(taskId, groupId);
		
		try {
			JobDetail jdt = scheduler.getJobDetail(jobKey);
		
			Assert.isTrue(jdt != null, "任务 %s.%s 不存在", groupId, taskId);
			
			TriggerKey tk = buildManualTriggerKey(LocalDateTime.of(request.getLoadDate(), LocalTime.MIN), taskId, groupId);
			
			TriggerState state = scheduler.getTriggerState(tk);
			
			// ~~ 调度参数 ~~
			JobDataMap jdm = new JobDataMap();
			CONTEXT_PARAMETER.applyPut(jdm, request.getJobParameter());
			CONTEXT_LOAD_DATE.applyPut(jdm, LocalDateTime.of(request.getLoadDate(), LocalTime.MIN));
			Trigger trigger = TriggerBuilder.newTrigger()
					.usingJobData(jdm)
					.withIdentity(tk)
					.forJob(taskId, groupId)
					.build();
			
			if (state == TriggerState.COMPLETE) {
				scheduler.rescheduleJob(tk, trigger);
			} else if (state == TriggerState.NONE) {
				scheduler.scheduleJob(trigger);
			} else {
				throw new AppException("不可重复调度任务，当前调度状态 {}", state);
			}
		} catch (SchedulerException e) {
			throw new AppException("执行失败: " + e.getMessage());
		}
	}
	
	public String toCronExpression(ScheduleProperties scheduleProperties) {
		LocalTime duetime = LocalTime.parse(scheduleProperties.getDuetime(), DateTimeFormatter.ISO_TIME);
		switch (scheduleProperties.getScheduleType()) {
		case MONTHLY: {
			List<Integer> days = scheduleProperties.getDaysOfMonth();
			Assert.isFalse(Utils.isNullOrEmpty(days), "月调度配置不能为空");
			return String.format("%s %s %s %s * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(),
					String.join(",", days.stream().map(i -> i + "").collect(Collectors.toList())));
		}
		case WEEKLY: {
			List<Integer> days = scheduleProperties.getDaysOfMonth();
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

}
