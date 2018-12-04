package com.iwellmass.idc.app.controller;
 
import java.util.List;

import javax.inject.Inject;

import org.quartz.SchedulerException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.TaskService;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.JobQuery;
import com.iwellmass.idc.app.model.JobRuntime;
import com.iwellmass.idc.app.model.PauseRequest;
import com.iwellmass.idc.app.service.JobServiceImpl;
import com.iwellmass.idc.app.vo.JobRuntimeListVO;
import com.iwellmass.idc.app.vo.JobRuntimeVO;
import com.iwellmass.idc.app.vo.ScheduleRequest;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.quartz.IDCPlugin;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job")
public class JobController {
	
	@Inject
	private JobServiceImpl jobService;
	
	@Inject
	private TaskService taskService;
	
	@Inject
	private IDCPlugin idcPlugin;
	
	@ApiOperation("查询调度列表")
	@PostMapping("/query")
	public ServiceResult<PageData<JobRuntimeListVO>> query(@RequestBody(required = false) JobQuery jobQuery, Pager pager) {
		PageData<JobRuntimeListVO> data = jobService.getJobRuntime(jobQuery, pager);
		return ServiceResult.success(data);
	}
	
	@ApiOperation("获取调度信息")
	@GetMapping
	public ServiceResult<Job> getJob(JobKey jobKey) {
		Job job = jobService.findJob(jobKey);
		if (job == null) {
			return ServiceResult.failure("任务不存在");
		}
		return ServiceResult.success(job);
	}
	
	@ApiOperation("获取调度运行时信息")
	@GetMapping("/runtime")
	public ServiceResult<JobRuntimeVO> getJobRuntime(JobKey jobKey) {

		JobRuntime jr = jobService.getJobRuntime(jobKey);
		
		Job job = jobService.findJob(jobKey);
		Task task = taskService.getTask(job.getTaskKey());
		task.setGraphId(job.getWorkflowId());
		task.setGraph(job.getWorkflowGraph());
		
		JobRuntimeVO vo = new JobRuntimeVO();
		vo.setScheduleConfig(JSON.parseObject(job.getScheduleConfig(), ScheduleProperties.class));
		vo.setTask(task);
		vo.setJobRuntime(jr);
		
		jr.setInstanceId(1);
		
		return ServiceResult.success(vo);
	}
	
	@ApiOperation("查询负责人信息")
	@GetMapping("/assignee")
	public ServiceResult<List<Assignee>> getAssignee() {
		List<Assignee> data = jobService.getAllAssignee();
		return ServiceResult.success(data);
	}
	
	// ~~~~~~~~~~~~~ 调度器接口 should be called by rpc  ~~~~~~~~~~~~~
	@ApiOperation("调度任务")
	@PostMapping(path = "/schedule")
	public ServiceResult<String> schedule(@RequestBody ScheduleRequest sr) {
		try {
			Task task = sr.getTask();
			ScheduleProperties sp = sr.getScheduleConfig();
			idcPlugin.schedule(task, sp);
			return ServiceResult.success("提交成功");
		} catch (SchedulerException e) {
			return ServiceResult.failure(e.getMessage());
		}
	}
	
	@ApiOperation("重新调度任务")
	@PostMapping(path = "/reschedule")
	public ServiceResult<String> reschedule(@RequestBody JobKey jobKey, @RequestBody ScheduleProperties scheduleConfig) {
		try {
			idcPlugin.reschedule(jobKey, scheduleConfig);
			return ServiceResult.success("提交成功");
		} catch (SchedulerException e) {
			return ServiceResult.failure(e.getMessage());
		}
	}
	
	@ApiOperation("取消调度任务")
	@PostMapping(path = "/unschedule")
	public ServiceResult<String> unschedule(@RequestBody JobKey jobKey) {
		try {
			idcPlugin.unschedule(jobKey);
			return ServiceResult.success("提交成功");
		} catch (SchedulerException e) {
			return ServiceResult.failure(e.getMessage());
		}
	}

	@PostMapping(value = "/pause")
	@ApiOperation("冻结调度")
	public ServiceResult<String> pause(@RequestBody PauseRequest request) {
		try {
			idcPlugin.pause(request);
			return ServiceResult.success("任务已冻结");
		} catch (SchedulerException e) {
			return ServiceResult.failure(e.getMessage());
		}
	}

	@PostMapping(value = "/resume")
	@ApiOperation("恢复调度")
	public ServiceResult<String> resume(@RequestBody JobKey jobKey) {
		try {
			idcPlugin.resume(jobKey);
			return ServiceResult.success("任务已冻结");
		} catch (SchedulerException e) {
			return ServiceResult.failure(e.getMessage());
		}
	}
}
