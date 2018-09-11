package com.iwellmass.idc.app.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.JobQuery;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.repo.JobRepository;

@Service
public class JobQueryService {

	@Inject
	private JobRepository jobRepository;

	public PageData<Job> findJob(JobQuery jobQuery, Pager pager) {

		Specifications<Job> spec = empty();

		Optional.ofNullable(jobQuery.getAssignee()).map(JobQuery::assigneeEq).ifPresent(spec::and);
		Optional.ofNullable(jobQuery.getContentType()).map(JobQuery::contentTypeEq).ifPresent(spec::and);
		
		if (jobQuery.getScheduleType() == ScheduleType.CRON) {
			spec.and(JobQuery.scheduleTypeIn(Arrays.asList(ScheduleType.MONTHLY, ScheduleType.WEEKLY, ScheduleType.DAILY, ScheduleType.HOURLY)));
		} else {
			Optional.ofNullable(jobQuery.getScheduleType()).map(JobQuery::scheduleTypeEq).ifPresent(spec::and);
		}
		
		Optional.ofNullable(jobQuery.getTaskName()).map(JobQuery::taskNameLike).ifPresent(spec::and);
		Optional.ofNullable(jobQuery.getTaskTypes()).map(JobQuery::taskTypeIn).ifPresent(spec::and);

		Page<Job> job = jobRepository.findAll(spec, new PageRequest(pager.getPage(), pager.getLimit()));

		return new PageData<Job>(job.getNumberOfElements(), job.getContent());
	}

	public List<Job> getWorkflowJob() {
		return jobRepository.findByTaskType(TaskType.WORKFLOW);
	}

	public List<Job> getWorkflowJob(JobPK jobKey) {
		return jobRepository.findSubJobs(jobKey.getTaskId(), jobKey.getGroupId());
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

}
