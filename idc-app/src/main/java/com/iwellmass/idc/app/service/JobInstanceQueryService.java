package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.JobInstanceQuery;
import com.iwellmass.idc.app.repo.JobInstanceRepository;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.TaskType;

@Service
public class JobInstanceQueryService {

	@Inject
	private JobInstanceRepository repository;

	public PageData<JobInstance> findJobInstance(JobInstanceQuery queryObject, Pager pager) {
		Pageable pgr = new PageRequest(pager.getPage(), pager.getLimit(), new Sort(Direction.DESC, "shouldFireTime"));
		
		Specification<JobInstance> specs = Specifications.<JobInstance>where((a,b,c) -> {
			return c.notEqual(a.get("taskType"), TaskType.SUB_TASK);
		}).and(queryObject.<JobInstance>toSpecification());
		
		Page<JobInstance> result = repository.findAll(specs, pgr);
		return new PageData<>((int)result.getTotalElements(), result.getContent());
	}

	public JobInstance getJobInstance(Integer id) {
		JobInstance instance = repository.findOne(id);
		Assert.isTrue(instance != null, "任务实例 %s 不存在", id);
		return instance;
	}

	public List<JobInstance> getWorkflowSubInstance(Integer id) {
		return repository.findByMainInstanceId(id);
	}

	public List<Assignee> getAllAssignee() {
		return repository.findAllAssignee().stream().map(id -> {
			Assignee assignee = new Assignee();
			assignee.setAssignee(id);
			return assignee;
		}).collect(Collectors.toList());
	}
}
