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

    @ApiOperation("通过条件检索实例（分页显示）")
    @GetMapping("/findJobAlarmByCondition")
    public ServiceResult<PageData<List<JobAlarm>>> findJobAlarmByCondition(JobAlarm alarm,
                                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                                        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize){
        Pager pager = new Pager();
        pager.setPage(page);
        pager.setLimit(pageSize);
        PageData<List<JobAlarm>> jobAlarm = jobAlarmService.findJobAlarmByCondition(alarm, pager);
        return ServiceResult.success(jobAlarm);
    }
}
