package com.iwellmass.idc.app.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.JobInstanceQuery;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.service.SchedulerService;

@Service
public class JobInstanceService {

	@Inject
	private JobInstanceRepository repository;

	@Inject
	private SchedulerService schedulerService;

	public void redo(Integer id) {
		schedulerService.redo(id);
	}

	public PageData<JobInstance> findJobInstance(JobInstanceQuery params, Pager pager) {
		// TODO dynamic query
		Page<JobInstance> result = repository.findAll(null, new PageRequest(pager.getPage(), pager.getLimit()));
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
}
