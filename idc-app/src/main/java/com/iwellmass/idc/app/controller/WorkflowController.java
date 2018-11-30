package com.iwellmass.idc.app.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.TaskService;
import com.iwellmass.idc.app.service.WorkflowService;
import com.iwellmass.idc.app.vo.WorkflowEnableVO;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.Workflow;
import com.iwellmass.idc.model.WorkflowEdge;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Inject
    private WorkflowService workflowService;

    @PostMapping("")
    @ApiOperation("保存workflow画图信息,以及最新workflowId")
    public ServiceResult<Workflow> save(@RequestBody Workflow workflow) {
        return ServiceResult.success(workflowService.saveWorkflow(workflow));
    }

    @GetMapping("")
    @ApiOperation("查询指定工作流")
    public ServiceResult<Workflow> item(TaskKey taskKey){
        try {
            return ServiceResult.success(workflowService.item(taskKey));
        } catch (Exception e) {
            return ServiceResult.failure(e.getMessage());
        }
    }

    @PostMapping("enable")
    @ApiOperation("/启用指定工作流")
    public ServiceResult<String> enable(@RequestBody WorkflowEnableVO workflowEnableVO) {
        try {
            return ServiceResult.success(workflowService.enable(workflowEnableVO));
        } catch (Exception e) {
            return ServiceResult.failure(e.getMessage());
        }
    }



}
