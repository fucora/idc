package com.iwellmass.idc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.model.JobInstance;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job-instance")
public class JobInstanceController {


	@ApiOperation("查询任务实例")
	public ServiceResult<PageData<JobInstance>> queryJobInstance() {
		return null;
	}
}
