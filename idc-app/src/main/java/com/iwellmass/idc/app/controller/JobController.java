package com.iwellmass.idc.app.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.JobQuery;
import com.iwellmass.idc.app.service.JobQueryService;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.ScheduleStatus;
import com.iwellmass.idc.service.ComplementRequest;
import com.iwellmass.idc.service.ExecutionRequest;
import com.iwellmass.idc.service.JobService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job")
public class JobController {

	@Inject
	private JobService jobService;
	
	@Inject
	private JobQueryService jobQueryService;

	@ApiOperation("查询任务")
	@PostMapping("/query")
	public ServiceResult<PageData<Job>> query(@RequestBody(required = false) JobQuery jobQuery, Pager pager) {
		PageData<Job> data = jobQueryService.findJob(jobQuery, pager);
		return ServiceResult.success(data);
	}

	@ApiOperation("查询负责人信息")
	@GetMapping("/assignee")
	public ServiceResult<List<Assignee>> getAssignee() {
		List<Assignee> data = jobQueryService.getAllAssignee();
		return ServiceResult.success(data);
	}
	
	@ApiOperation("查询调度状态")
	@GetMapping("/status")
	public ServiceResult<ScheduleStatus> getScheduleStatus(JobPK jobKey){
		Job job = jobQueryService.findJob(jobKey);
		return ServiceResult.success(job == null ? ScheduleStatus.NONE : job.getStatus());
	}

	@ApiOperation("调度任务")
	@PostMapping(path = "/schedule")
	public ServiceResult<String> schedule(@RequestBody Job job) {
		jobService.schedule(job);
		return ServiceResult.success("提交成功");
	}

	@ApiOperation("取消调度任务")
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
}
