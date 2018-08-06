package com.iwellmass.idc.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.model.JobAlarm;
import com.iwellmass.idc.service.JobAlarmService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/job-alarm")
public class JobAlarmController {

    @Inject

    private JobAlarmService jobAlarmService;

    @ApiOperation("通过条件检索实例警告（分页显示）")
    @PostMapping("/query")
    public ServiceResult<PageData<List<JobAlarm>>> findJobAlarmByCondition(@RequestBody(required = false) JobAlarm alarm,
                                                                           Pager pager){
        PageData<List<JobAlarm>> jobAlarm = jobAlarmService.findJobAlarmByCondition(alarm, pager);
        return ServiceResult.success(jobAlarm);
    }
}
