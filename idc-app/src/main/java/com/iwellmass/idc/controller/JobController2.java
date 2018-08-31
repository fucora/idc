package com.iwellmass.idc.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.model.ExecutionRequest;

import io.swagger.annotations.ApiOperation;

/**
 *
 * @deprecated 版本稳定后删除，使用 {@link JobController}
 */
@RestController
@RequestMapping("/job")
@Deprecated
public class JobController2 {

	@PostMapping(value = "/{id}/lock")
	@ApiOperation("冻结 Job")
	public ServiceResult<String> lock(@PathVariable("id") String jobKey) {
		return ServiceResult.failure("not supported yet.");
	}

	@PostMapping(value = "/{id}/unlock")
	@ApiOperation("恢复 Job")
	public ServiceResult<String> unlock(@PathVariable("id") String jobKey) {
		return ServiceResult.failure("not supported yet.");
	}

	@ApiOperation("补数")
	@PostMapping("/{id}/complement")
	public ServiceResult<String> complement(@PathVariable("id") Integer id, @RequestBody ComplementRequest request) {
		return ServiceResult.failure("not supported yet.");
	}

	@ApiOperation("手动执行任务")
	@PostMapping("/{id}/execution")
	public ServiceResult<String> execution(@PathVariable("id") String jobKey,
			@RequestBody(required = false) ExecutionRequest request) {
		return ServiceResult.failure("not supported yet.");
	}

}
