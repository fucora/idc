package com.iwellmass.idc.app.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.ComplementRequest;
import com.iwellmass.idc.app.model.ExecutionRequest;
import com.iwellmass.idc.app.model.JobQuery;
import com.iwellmass.idc.app.model.LockRequest;
import com.iwellmass.idc.app.service.JobQueryService;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.service.JobService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job")
public class JobController {

	@Inject
	private JobService jobService;
	
	@Inject
	private JobQueryService jobQueryService;
	
	@ApiOperation("获取任务信息")
	@GetMapping
	public ServiceResult<Job> getJob(JobPK jobKey) {
		Job job = jobQueryService.findJob(jobKey);
		if (job == null) {
			return ServiceResult.failure("任务不存在");
		}
		return ServiceResult.success(job);
	}
	

	@ApiOperation("查询任务，分页")
	@PostMapping("/query")
	public ServiceResult<PageData<Job>> query(@RequestBody(required = false) JobQuery jobQuery, Pager pager) {
		PageData<Job> data = jobQueryService.findJob(jobQuery, pager);
		return ServiceResult.success(data);
	}
	
	@ApiOperation("查询依赖列表")
	@GetMapping("/dependency-list")
	public ServiceResult<List<Job>> query(@RequestParam("scheduleType") ScheduleType scheduleType) {
		List<Job> deps = jobQueryService.findAvailableDependency(scheduleType);
		return ServiceResult.success(deps);
	}
	
	@ApiOperation("查询负责人信息")
	@GetMapping("/assignee")
	public ServiceResult<List<Assignee>> getAssignee() {
		List<Assignee> data = jobQueryService.getAllAssignee();
		return ServiceResult.success(data);
	}

	@ApiOperation("调度任务")
	@PostMapping(path = "/schedule")
	public ServiceResult<String> schedule(@RequestBody Job job) {
		jobService.schedule(job);
		return ServiceResult.success("提交成功");
	}
	
	@ApiOperation("重新调度任务")
	@PostMapping(path = "/reschedule")
	public ServiceResult<String> reschedule(@RequestBody Job job) {
		jobService.reschedule(job);
		return ServiceResult.success("提交成功");
	}

	@ApiOperation("取消调度任务")
	@PostMapping(path = "/unschedule")
	public ServiceResult<String> unschedule(@RequestBody JobPK jobKey) {
		jobService.unschedule(jobKey);
		return ServiceResult.success("提交成功");
	}

	@PostMapping(value = "/pause")
	@ApiOperation("冻结 Job")
	public ServiceResult<String> pause(@RequestBody LockRequest request) {
		jobService.pause(request);
		return ServiceResult.success("任务已冻结");
	}

	@PostMapping(value = "/resume")
	@ApiOperation("恢复 Job")
	public ServiceResult<String> resume(@RequestBody JobPK jobKey) {
		jobService.resume(jobKey);
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
