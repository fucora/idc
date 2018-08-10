package com.iwellmass.idc.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobQuery;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.service.JobService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job")
public class JobController {

	@Autowired
	private JobService jobService;

	@PostMapping
	@ApiOperation("新增调度任务")
	public ServiceResult<String> addJob(@RequestBody Job job) {
		jobService.schedule(job);
		return ServiceResult.success("success");
	}

	@ApiOperation("通过条件检索任务（分页显示）")
	@PostMapping(path = "/query")
	public ServiceResult<PageData<Job>> findTasksByCondition(@RequestBody(required = false) JobQuery query,
			Pager pager) {
		
		if (query == null) {
			query = new JobQuery();
		}
		if (query.getTaskTypes() == null) {
			query.setTaskTypes(Arrays.asList(TaskType.NODE_TASK, TaskType.WORKFLOW));
		}
		
		PageData<Job> tasks = jobService.findTasksByCondition(query, pager);
		return ServiceResult.success(tasks);
	}

	@ApiOperation("获取所有 workflow job")
	@GetMapping(path = "/workflow-job")
	public ServiceResult<List<Job>> getWorkflowJob() {
		return ServiceResult.success(jobService.getWorkflowJob());
	}

	@ApiOperation("获取工作流子任务")
	@GetMapping(path = "/workflow-job/{workflowId}")
	public ServiceResult<List<Job>> findTaskByGroupId(@PathVariable("workflowId") Integer workflowId) {
		List<Job> taskByGroupId = jobService.getWorkflowJob(workflowId);
		return ServiceResult.success(taskByGroupId);
	}

	@ApiOperation("获取任务所有负责人")
	@GetMapping(path = "/assignee")
	public ServiceResult<List<JobQuery>> getAllAssignee() {
		List<JobQuery> allAssignee = jobService.getAllAssignee();
		return ServiceResult.success(allAssignee);
	}

	@PostMapping(value = "/{id}/lock")
	@ApiOperation("冻结 Job")
	public ServiceResult<String> lock(@PathVariable("id") int jobId) {
		try {
			jobService.lockStatus(jobId);
		} catch (DDCException e) {
			return ServiceResult.failure(e.getMessage());
		}
		return ServiceResult.success("success");
	}
	
	@PostMapping(value = "/{id}/unlock")
	@ApiOperation("恢复 Job")
	public ServiceResult<String> unlock(@PathVariable("id") int jobId) {
		try {
			jobService.unlockStatus(jobId);
		} catch (DDCException e) {
			return ServiceResult.failure(e.getMessage());
		}
		return ServiceResult.success("success");
	}

    @ApiOperation("补数")
    @PostMapping("/{id}/complement")
    public ServiceResult<String> complement(@PathVariable("id") Integer id, @RequestBody ComplementRequest request){
    	request.setJobId(id);
    	jobService.complement(request);
        return ServiceResult.success("success");
    }
    
    @ApiOperation("手动执行任务")
    @PostMapping("/{id}/execution")
    public ServiceResult<String> execution(@PathVariable("id") Integer id, @RequestBody(required = false) ExecutionRequest request){
    	request.setJobId(id);
    	jobService.execute(request);
    	return ServiceResult.success("success");
    }

}
