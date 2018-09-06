package com.iwellmass.idc.app.controller;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.app.service.JobService;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.service.ComplementRequest;
import com.iwellmass.idc.service.ExecutionRequest;

import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping("/job")
public class JobController2 {

	@Inject
	private JobService jobService;
	
	@PostMapping(value = "/{id}/lock")
	@ApiOperation("冻结 Job")
	public ServiceResult<String> lock(@PathVariable("id") int jobId) {
		jobService.lock(new JobPK(jobId + "", "data-factory"));
		return ServiceResult.success("success");
	}
	
	@PostMapping(value = "/{id}/unlock")
	@ApiOperation("恢复 Job")
	public ServiceResult<String> unlock(@PathVariable("id") int jobId) {
		jobService.unlock(new JobPK(jobId + "", "data-factory"));
		return ServiceResult.success("success");
	}

    @ApiOperation("补数")
    @PostMapping("/{id}/complement")
    public ServiceResult<String> complement(@PathVariable("id") Integer id, @RequestBody ComplementRequest request){
    	request.setGroupId("datafactory");
    	jobService.complement(request);
        return ServiceResult.success("success");
    }
    
    @ApiOperation("手动执行任务")
    @PostMapping("/{id}/execution")
    public ServiceResult<String> execution(@PathVariable("id") Integer id, @RequestBody(required = false) ExecutionRequest request){
    	request.setGroupId("datafactory");
    	jobService.execute(request);
    	return ServiceResult.success("success");
    }

}
