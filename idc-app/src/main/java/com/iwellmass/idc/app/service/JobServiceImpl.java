package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.JobService;
import com.iwellmass.idc.app.mapper.JobRuntimeMapper;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.ComplementRequest;
import com.iwellmass.idc.app.model.ExecutionRequest;
import com.iwellmass.idc.app.model.JobQuery;
import com.iwellmass.idc.app.model.JobRuntime;
import com.iwellmass.idc.app.repo.JobDependencyRepository;
import com.iwellmass.idc.app.repo.JobRepository;
import com.iwellmass.idc.app.vo.JobBarrierVO;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleStatus;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.quartz.IDCPlugin;

@Service
public class JobServiceImpl implements JobService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

	@Inject
	private JobRepository jobRepository;

	@Inject
	private JobRuntimeMapper jobRuntimeMapper;
	
//	@Inject
	private IDCPlugin idcPlugin;

//	@Inject
	private Scheduler scheduler;
	
	
	@Override
	public Job getJob(JobKey jobKey) {
		return jobRepository.findOne(jobKey);
	}
	
	@Override
	public void saveJob(Job job) {
		jobRepository.save(job);
	}
	

	public JobRuntime getJobRuntime(JobKey jobKey) {
		List<JobBarrierVO> barriers = jobRuntimeMapper.selectJobBarrierVO(jobKey);
		JobRuntime jr = new JobRuntime();
		jr.setBarriers(barriers);
		jr.setStatus(ScheduleStatus.ERROR);
		return  jr;
	}

	public void schedule(Task task, ScheduleProperties schdProps) {
		try {
			idcPlugin.schedule(task, schdProps);
		} catch (SchedulerException e) {
			throw new AppException("调度失败: " + e.getMessage(), e);
		}
	}

	@Transactional
	public void reschedule(Job job) {
	}
	
	@Transactional
	public void reschedule(JobKey jobKey) {
		// 没有正在执行的任务计划便可以重新调度计划任务
		/*Job job = jobRepository.findOne(jobKey);
		if (job != null && job.getStatus() != ScheduleStatus.NONE) {
			Assert.isTrue(job.getStatus() == ScheduleStatus.PAUSED, "任务未冻结");
		}

		LOGGER.info("重新调度任务 {}", jobKey);
		
		try {
			
			idcPlugin.reschedule(job);
			
			Trigger trigger = idcPlugin.buildTrigger(job, false);

			if (job.getStatus() == ScheduleStatus.NONE) {
				scheduler.scheduleJob(trigger);
			} else {
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			}
		} catch (reschedu e) {
			throw new AppException("生成 Cron 表达式时错误, " + e.getMessage());
		} catch (SchedulerException e) {
			throw new AppException("无法调度任务: " + e.getMessage(), e);
		}*/
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
			jobRepository.save(job);
		} catch (SchedulerException e) {
			throw new AppException("无法恢复此任务");
		}
	}

	public void execute(ExecutionRequest request) {
		/*JobKey jobPk = StdJobKeyGeneratorImpl.valueOf(request);
		
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
		}*/
		
		
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
	
	


	
	@Inject
	private JobDependencyRepository dependencyRepository;

	public PageData<Job> findJob(JobQuery jobQuery, Pager pager) {
		Specification<Job> spec = jobQuery == null ? null : jobQuery.toSpecification();
		Page<Job> job = jobRepository.findAll(spec, new PageRequest(pager.getPage(), pager.getLimit()));
		return new PageData<Job>((int)job.getTotalElements(), job.getContent());
	}
	
	// available 
	public List<Job> findAvailableDependency(ScheduleType scheduleType) {
		Specification<Job> spec = (root, cq, cb) -> {
			return cb.and(
					cb.equal(root.get("scheduleType"), scheduleType),
					root.get("taskType").in(TaskType.WORKFLOW_TASK, TaskType.NODE_TASK)
			);
		};
		return jobRepository.findAll(spec);
	}

	public List<Job> getWorkflowJob() {
		return jobRepository.findByTaskType(TaskType.WORKFLOW_TASK);
	}

	public List<Job> getWorkflowJob(JobKey jobKey) {
		return jobRepository.findSubJobs(jobKey.getJobId(), jobKey.getJobGroup());
	}

	public List<Assignee> getAllAssignee() {
		return jobRepository.findAllAssignee().stream().map(id -> {
			Assignee asg = new Assignee();
			asg.setAssignee(id);
			return asg;
		}).collect(Collectors.toList());
	}

	public static <T> Specifications<T> empty() {
		return Specifications.where((root, query, cb) -> {
			return cb.equal(cb.literal(1), 1);
		});
	}

	public Job findJob(JobKey jobKey) {
		return jobRepository.findOne(jobKey);
	}


}
