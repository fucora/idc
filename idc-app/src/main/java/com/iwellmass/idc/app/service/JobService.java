package com.iwellmass.idc.app.service;



import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.JobQuery;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.repo.JobRepository;
import com.iwellmass.idc.service.ComplementRequest;
import com.iwellmass.idc.service.ExecutionRequest;
import com.iwellmass.idc.service.SchedulerService;

@Service
public class JobService {

	@Inject
	private JobRepository jobRepository;

	@Inject
	private SchedulerService schedulerService;

	public void schedule(Job job) throws AppException {
		schedulerService.schedule(job);
	}

	public void unschedule(JobPK jobKey) {
		schedulerService.unschedule(jobKey);
	}
	
	public void complement(ComplementRequest request) {
		schedulerService.complement(request);
	}

	public void execute(ExecutionRequest request) {
		schedulerService.execute(request);
	}

	public void lock(JobPK jobKey) {
		schedulerService.lock(jobKey);
	}

	public void unlock(JobPK jobKey) {
		schedulerService.unlock(jobKey);
	}

	public PageData<Job> findJob(JobQuery jobQuery, Pager pager) {

		Specifications<Job> spec = empty();

		Optional.ofNullable(jobQuery.getAssignee()).map(JobQuery::assigneeEq).ifPresent(spec::and);
		Optional.ofNullable(jobQuery.getContentType()).map(JobQuery::contentTypeEq).ifPresent(spec::and);
		Optional.ofNullable(jobQuery.getScheduleType()).map(JobQuery::scheduleTypeEq).ifPresent(spec::and);
		Optional.ofNullable(jobQuery.getTaskName()).map(JobQuery::taskNameLike).ifPresent(spec::and);
		Optional.ofNullable(jobQuery.getTaskTypes()).map(JobQuery::taskTypeIn).ifPresent(spec::and);

		Page<Job> job = jobRepository.findAll(spec, new PageRequest(pager.getPage(), pager.getLimit()));

		return new PageData<Job>(job.getNumberOfElements(), job.getContent());
	}

	public List<Job> getWorkflowJob() {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public List<Job> getWorkflowJob(Integer jobId) {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public List<Assignee> getAllAssignee() {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public static <T> Specifications<T> empty() {
		return Specifications.where((root, query, cb) -> {
			return cb.equal(cb.literal(1), 1);
		});
	}

}
