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

    @ApiOperation("通过条件检索实例（分页显示）")
    @PostMapping("/findTaskInstanceByCondition")
    public ServiceResult<PageData<List<JobInstance>>> findTaskInstanceByCondition(@RequestBody JobQuery query,
                                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize){
        Pager pager = new Pager();
        pager.setPage(page);
        pager.setLimit(pageSize);
        PageData<List<JobInstance>> taskInstance = jobInstanceService.findTaskInstanceByCondition(query, pager);
        return ServiceResult.success(taskInstance);
    }

    @ApiOperation("获取所有实例类型")
    @GetMapping(path = "/getAllTypes")
    public ServiceResult<List<JobQuery>> getAllTypes(){
        List<JobQuery> allTypes = jobInstanceService.getAllTypes();
        return ServiceResult.success(allTypes);
    }

    @ApiOperation("获取所有负责人")
    @GetMapping(path ="/getAllAssignee" )
    public ServiceResult<List<JobQuery>> getAllAssignee(){
        List<JobQuery> allAssignee = jobInstanceService.getAllAssignee();
        return ServiceResult.success(allAssignee);
    }
}
