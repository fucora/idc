package com.iwellmass.idc.controller;

import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.model.JobQuery;
import com.iwellmass.idc.service.JobInstanceService;
import org.springframework.web.bind.annotation.*;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.model.JobInstance;

import io.swagger.annotations.ApiOperation;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/job-instance")
public class JobInstanceController {

    @Inject
    private JobInstanceService jobInstanceService;

    @ApiOperation("补数")
    @PostMapping("/complement")
    public ServiceResult save(@RequestBody JobInstance instance){
        JobInstance jobInstance = jobInstanceService.save(instance);
        return ServiceResult.success(jobInstance);
    }

    @ApiOperation("通过条件检索实例（分页显示）")
    @PostMapping("/query")
    public ServiceResult<PageData<List<JobInstance>>> findTaskInstanceByCondition(@RequestBody(required = false) JobQuery query,Pager pager){
        PageData<List<JobInstance>> taskInstance = jobInstanceService.findTaskInstanceByCondition(query, pager);
        return ServiceResult.success(taskInstance);
    }

    @ApiOperation("获取所有负责人")
    @GetMapping(path ="/assignee" )
    public ServiceResult<List<JobQuery>> getAllAssignee(){
        List<JobQuery> allAssignee = jobInstanceService.getAllAssignee();
        return ServiceResult.success(allAssignee);
    }
}
