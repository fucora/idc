package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.JobQuery;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.repo.JobRepository;

@Service
public class JobQueryService {

	@Inject
	private JobRepository jobRepository;

	public PageData<Job> findJob(JobQuery jobQuery, Pager pager) {

		Specification<Job> spec = jobQuery.toSpecification();
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
