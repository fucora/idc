package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.iwellmass.idc.app.repo.JobBarrierRepo;
import com.iwellmass.idc.model.*;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.mapper.JobRuntimeMapper;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.ExecutionRequest;
import com.iwellmass.idc.app.model.JobQuery;
import com.iwellmass.idc.app.model.JobRuntime;
import com.iwellmass.idc.app.model.PauseRequest;
import com.iwellmass.idc.app.repo.JobRepository;
import com.iwellmass.idc.app.vo.JobBarrierVO;
import com.iwellmass.idc.app.vo.JobRuntimeListVO;
import com.iwellmass.idc.quartz.IDCPlugin;

@Service
public class JobService {

	static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

	@Inject
	private JobRepository jobRepository;

	@Inject
	private JobRuntimeMapper jobRuntimeMapper;
	
	@Inject
	private IDCPlugin idcPlugin;

	@Inject
	private JobBarrierRepo jobBarrierRepo;
	
	public JobRuntime getJobRuntime(JobKey jobKey) {
		List<JobBarrierVO> barriers = jobRuntimeMapper.selectJobBarrierVO(jobKey);
		JobRuntime jr = new JobRuntime();
		jr.setBarriers(barriers);
		jr.setStatus(ScheduleStatus.ERROR);
		return  jr;
	}

	public void execute(ExecutionRequest request) {
		
	}
	
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
					root.get("taskType").in(TaskType.WORKFLOW, TaskType.NODE_TASK)
			);
		};
		return jobRepository.findAll(spec);
	}

	public List<Job> getWorkflowJob() {
		return jobRepository.findByTaskType(TaskType.WORKFLOW);
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

	public PageData<JobRuntimeListVO> getJobRuntime(JobQuery jobQuery, Pager pager) {
		PageInfo<JobRuntimeListVO> pageInfo = PageHelper.startPage(pager.getPage()+1,pager.getLimit()).doSelectPageInfo(()->jobRuntimeMapper.selectJobRuntimeList(jobQuery));
		List<JobRuntimeListVO> jobRuntimeListVOS = pageInfo.getList();
		jobRuntimeListVOS.forEach(bean->{
			JobKey jobKey = new JobKey(bean.getJobId(),bean.getJobGroup());
			List<JobBarrier> jobBarriers = jobBarrierRepo.findByJobIdAndJobGroup(jobKey.getJobId(),jobKey.getJobGroup());
			Integer sumTask = 0;
			Integer finishedTask = 0;
			if (jobBarriers.size()>0){
				finishedTask = (int)(jobBarriers.stream().filter(item->item.getState().equals(BarrierState.INVALID)).count());
				sumTask = jobBarriers.size();
			}
			bean.setSumTask(sumTask);
			bean.setFinishedTask(finishedTask);
		});
		return new PageData<JobRuntimeListVO>((int)pageInfo.getTotal(), pageInfo.getList());
	}

	public void schedule(ScheduleProperties sp) {
		try {
			// TODO 检查 task 完整性
			idcPlugin.schedule(sp);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	public void reschedule(JobKey jobKey, ScheduleProperties scheduleConfig) {
		try {
			idcPlugin.reschedule(jobKey, scheduleConfig);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	public void unschedule(JobKey jobKey) {
		try {
			idcPlugin.unschedule(jobKey);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
		
	}

	public void pause(PauseRequest request) {
		try {
			idcPlugin.pause(request);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	public void resume(JobKey jobKey) {
		try {
			idcPlugin.resume(jobKey);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}
}
