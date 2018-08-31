package com.iwellmass.idc.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.model.JobAlarm;
import com.iwellmass.idc.model.JobAlarmQuery;
import com.iwellmass.idc.service.JobAlarmService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job-alarm")
public class JobAlarmController {

	@Inject
	private JobAlarmService jobAlarmService;

	@ApiOperation("查询告警信息")
	@PostMapping("/query")
	public ServiceResult<PageData<List<JobAlarm>>> query(@RequestBody(required = false) JobAlarmQuery alarm, Pager pager) {
		PageData<List<JobAlarm>> jobAlarm = jobAlarmService.findJobAlarmByCondition(alarm, pager);
		return ServiceResult.success(jobAlarm);
	}
}
