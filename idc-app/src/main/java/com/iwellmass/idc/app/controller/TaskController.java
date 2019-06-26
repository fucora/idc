package com.iwellmass.idc.app.controller;
 
import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.app.service.TaskService;
import com.iwellmass.idc.app.vo.Assignee;
import com.iwellmass.idc.app.vo.TaskQueryParam;
import com.iwellmass.idc.app.vo.TaskRuntimeVO;
import com.iwellmass.idc.app.vo.TaskVO;
import com.iwellmass.idc.app.vo.ReTaskVO;
import com.iwellmass.idc.scheduler.model.IDCScheduler;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/task")
public class TaskController {
	
	@Resource
	private IDCScheduler idcs;
	
	@Resource
	private TaskService taskService;
	
	@ApiOperation("获取调度运行时信息")
	@GetMapping("/runtime")
	public ServiceResult<PageData<TaskRuntimeVO>> getJobRuntime(@RequestParam(required = false) TaskQueryParam jqm) {
		PageData<TaskRuntimeVO> ret = taskService.query(jqm);
		return ServiceResult.success(ret);
	}
	
	@ApiOperation("新增调度计划")
	@PostMapping
	public ServiceResult<String> schedule(@RequestBody TaskVO vo) {
		idcs.schedule(vo);
		return ServiceResult.success("提交成功");
	}
	
	@ApiOperation("获取调度计划详情")
	@GetMapping("/{name}")
	public ServiceResult<TaskVO> getTask(@PathVariable("name") String name) {
		TaskVO vo = taskService.getTask(name);
		return ServiceResult.success(vo);
	}
	
	
	@ApiOperation("查询负责人信息")
	@GetMapping("/assignee")
	public ServiceResult<List<Assignee>> getAssignee() {
		List<Assignee> data = taskService.getAllAssignee();
		return ServiceResult.success(data);
	}
	
	@ApiOperation("重新调度")
	@PostMapping(path = "/{name}/reschedule")
	public ServiceResult<String> reschedule(@PathVariable("name") String name, @RequestBody ReTaskVO jobVO) {
		idcs.reschedule(name, jobVO);
		return ServiceResult.success("提交成功");
	}
	
	@ApiOperation("取消调度")
	@PostMapping(path = "/{name}/unschedule")
	public ServiceResult<String> unschedule(@PathVariable("name") String name) {
		idcs.unschedule(name);
		return ServiceResult.success("提交成功");
	}

	@PostMapping(value = "/{name}/pause")
	@ApiOperation("暂停调度")
	public ServiceResult<String> pause(@PathVariable("name") String name) {
		idcs.pause(name);
		return ServiceResult.success("任务已冻结");
	}

	@PostMapping(value = "/{name}/resume")
	@ApiOperation("恢复调度")
	public ServiceResult<String> resume(@PathVariable("name") String name) {
		idcs.resume(name);
		return ServiceResult.success("任务已恢复");
	}


}
