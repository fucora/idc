package com.iwellmass.idc.app.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.JobQuery;
import com.iwellmass.idc.app.service.JobService;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.service.ComplementRequest;
import com.iwellmass.idc.service.ExecutionRequest;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job")
public class JobController {

	@Inject
	private JobService jobService;

	@Inject
	private IDCStatusService statusService;

	@PostMapping("/query")
	public ServiceResult<PageData<Job>> query(@RequestBody JobQuery jobQuery, Pager pager) {
		PageData<Job> data = jobService.findJob(jobQuery, pager);
		return ServiceResult.success(data);
	}

	@GetMapping("/assignee")
	public ServiceResult<List<Assignee>> getAssignee() {
		List<Assignee> data = jobService.getAllAssignee();
		return ServiceResult.success(data);
	}

	@PostMapping(path = "/schedule")
	public ServiceResult<String> schedule(@RequestBody Job job) {
		jobService.schedule(job);
		return ServiceResult.success("提交成功");
	}

	@PostMapping(path = "/unschedule")
	public ServiceResult<String> unschedule(@RequestBody JobPK jobKey) {
		jobService.unschedule(jobKey);
		return ServiceResult.success("提交成功");
	}

	@PostMapping(value = "/lock")
	@ApiOperation("冻结 Job")
	public ServiceResult<String> lock(@RequestBody JobPK jobKey) {
		jobService.lock(jobKey);
		return ServiceResult.success("任务已冻结");
	}

	@PostMapping(value = "/unlock")
	@ApiOperation("恢复 Job")
	public ServiceResult<String> unlock(@RequestBody JobPK jobKey) {
		jobService.unlock(jobKey);
		return ServiceResult.success("任务已恢复");
	}

	@ApiOperation("补数")
	@PostMapping("/complement")
	public ServiceResult<String> complement(@RequestBody ComplementRequest request) {
		jobService.complement(request);
		return ServiceResult.success("提交成功");
	}

	@ApiOperation("手动执行任务")
	@PostMapping("/execution")
	public ServiceResult<String> execution(@RequestBody ExecutionRequest request) {
		jobService.execute(request);
		return ServiceResult.success("提交成功");
	}

	@PutMapping("/complete")
	public void fireCompleteEvent(@RequestBody CompleteEvent event) {
		statusService.fireCompleteEvent(event);
	}

	@PutMapping("/start")
	public void fireStartEvent(@RequestBody StartEvent event) {
		statusService.fireStartEvent(event);
	}
}
