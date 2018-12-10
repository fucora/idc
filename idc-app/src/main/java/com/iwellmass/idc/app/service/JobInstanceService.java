package com.iwellmass.idc.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.CancleRequest;
import com.iwellmass.idc.app.model.JobInstanceQuery;
import com.iwellmass.idc.app.model.RedoRequest;
import com.iwellmass.idc.app.repo.ExecutionLogRepository;
import com.iwellmass.idc.app.repo.JobInstanceRepository;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.ExecutionLog;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.quartz.IDCContextKey;
import com.iwellmass.idc.quartz.IDCPlugin;

@Service
public class JobInstanceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobInstanceService.class);
	
	@Inject
	private JobInstanceRepository jobInstanceRepository;
	
	@Inject
	private ExecutionLogRepository logRepository;

	@Inject
	private IDCPlugin idcPlugin;
	
	private Scheduler scheduler;

	public void redo(RedoRequest request) {
		
		int instanceId = request.getInstanceId();
		
		JobInstance instance = jobInstanceRepository.findOne(instanceId);
		
		Assert.isTrue(instance != null, "找不到此实例");
		
		try {
			idcPlugin.redo(request.getInstanceId());
		} catch (SchedulerException e) {
			throw new AppException("执行失败: " + e.getMessage());
		}
	}

	
	public void forceComplete(int instanceId) {
		
		JobInstance instance = jobInstanceRepository.findOne(instanceId);
		
		Assert.isTrue(instance != null, "找不到此实例");
		
		CompleteEvent event = CompleteEvent
			.successEvent("强制结束")
			.setInstanceId(instanceId)
			.setFinalStatus(JobInstanceStatus.SKIPPED)
			.setEndTime(LocalDateTime.now());
		idcPlugin.getStatusService().fireCompleteEvent(event);
	}
	
	public void cancle(CancleRequest req) {
		int instanceId = req.getInstanceId();
		JobInstance instance = jobInstanceRepository.findOne(instanceId);
		Assert.isTrue(instance != null, "不存在此实例 %s", instanceId);
		
		try {
			IDCPlugin plugin = IDCContextKey.IDC_PLUGIN.applyGet(scheduler.getContext());
			// plugin.cancleJob(instance.getJobId(), instance.getJobGroup());
			
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
			logRepository.log(instanceId, "无法取消任务: {}", e.getMessage());
			if (req.isForce()) {
				instance.setStatus(JobInstanceStatus.CANCLED);
				logRepository.log(instanceId, "强制取消任务", e.getMessage());
			} else {
				throw new AppException("无法取消任务: " + e.getMessage(), e);
			}
		}
	}
	
	public PageData<ExecutionLog> getJobInstanceLog(Integer id, Pager pager) {
		Pageable page = new PageRequest(pager.getPage(), pager.getLimit(), new Sort(Direction.ASC, "id"));
		Page<ExecutionLog> data = logRepository.findByInstanceId(id, page);
		return new PageData<>((int) data.getTotalElements(), data.getContent());
	}
	
	


	@Inject
	private JobInstanceRepository repository;

	public PageData<JobInstance> findJobInstance(JobInstanceQuery queryObject, Pager pager) {
		Pageable pgr = new PageRequest(pager.getPage(), pager.getLimit(), new Sort(Direction.DESC, "startTime"));
		Page<JobInstance> result = queryObject == null ? repository.findAll(pgr)
				: repository.findAll(queryObject.toSpecification(), pgr);
		return new PageData<>(result.getNumberOfElements(), result.getContent());
	}

	
	public JobInstance getJobInstance(JobKey jobKey, long shouldFireTime) {
		return repository.findOne(jobKey.getJobId(), jobKey.getJobGroup(), shouldFireTime);
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
