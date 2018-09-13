package com.iwellmass.idc.app.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.JobInstanceQuery;
import com.iwellmass.idc.app.service.JobInstanceQueryService;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.service.JobInstanceService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job-instance")
public class JobInstanceController {
	
	@Inject
	private JobInstanceService jobInstanceService;

	@Inject
	private JobInstanceQueryService queryService;

	@ApiOperation("通过条件检索实例（分页显示）")
	@PostMapping("/query")
	public ServiceResult<PageData<JobInstance>> findTaskInstanceByCondition(
			@RequestBody(required = false) JobInstanceQuery query, Pager pager) {
		PageData<JobInstance> taskInstance = queryService.findJobInstance(query, pager);
		return ServiceResult.success(taskInstance);
	}
	
	@ApiOperation("通过条件检索实例（分页显示）")
	@GetMapping("/assignee")
	public ServiceResult<List<Assignee>> assignee() {
		return ServiceResult.success(queryService.getAllAssignee());
	}

	@ApiOperation("获取工作流实例")
	@GetMapping("/{id}/workflow-job")
	public ServiceResult<List<JobInstance>> getWorkflowTask(@PathVariable("id") Integer id) {
		List<JobInstance> result = queryService.getWorkflowSubInstance(id);
		return ServiceResult.success(result);
	}

	@ApiOperation("重跑任务")
	@PostMapping("/{id}/redo")
	public ServiceResult<String> restart(@PathVariable(name = "id") Integer id) {
		jobInstanceService.redo(id);
		return ServiceResult.success("success");
	}
}
