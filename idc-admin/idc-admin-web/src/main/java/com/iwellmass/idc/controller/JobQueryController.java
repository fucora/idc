package com.iwellmass.idc.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobQuery;
import com.iwellmass.idc.service.JobQueryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
@RestController
@RequestMapping("/job-query")
public class JobQueryController {

    @Inject
    private JobQueryService jobQueryService;

    @ApiOperation("通过条件检索任务（分页显示）")
    @GetMapping(path = "/findTasksByCondition")
    public ServiceResult<List<Job>> findTasksByCondition(@RequestBody JobQuery query,
                                         @RequestParam(name = "page",defaultValue = "1") int page,
                                         @RequestParam(name = "pageSize",defaultValue = "10") int pageSize){
        Pager pager=new Pager();
        pager.setPage(page);
        pager.setLimit(pageSize);
        List<Job> jobs = jobQueryService.findTasksByCondition(query,pager);
        return ServiceResult.success(jobs);
    }

}
