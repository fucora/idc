package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.JobInstanceQuery;
import com.iwellmass.idc.app.repo.JobInstanceRepository;
import com.iwellmass.idc.model.JobInstance;

@Service
public class JobInstanceQueryService {

	@Inject
	private JobInstanceRepository repository;

	public PageData<JobInstance> findJobInstance(JobInstanceQuery queryObject, Pager pager) {
		Pageable pgr = new PageRequest(pager.getPage(), pager.getLimit(), new Sort(Direction.DESC, "startTime"));
		Page<JobInstance> result = queryObject == null ? repository.findAll(pgr)
				: repository.findAll(queryObject.toSpecification(), pgr);
		return new PageData<>(result.getNumberOfElements(), result.getContent());
	}

	public JobInstance getJobInstance(Integer id) {
		JobInstance instance = repository.findOne(id);
		Assert.isTrue(instance != null, "任务实例 %s 不存在", id);
		return instance;
	}

	public List<JobInstance> getWorkflowSubInstance(Integer id) {
		throw new UnsupportedOperationException("not supported yet.");
	}

	public List<Assignee> getAllAssignee() {
		return repository.findAllAssignee().stream().map(id -> {
			Assignee assignee = new Assignee();
			assignee.setAssignee(id);
			return assignee;
		}).collect(Collectors.toList());
		
	}
}
