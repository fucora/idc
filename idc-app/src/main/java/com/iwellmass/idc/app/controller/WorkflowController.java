package com.iwellmass.idc.app.controller;

import javax.inject.Inject;
import javax.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.app.service.WorkflowService;
import com.iwellmass.idc.app.vo.WorkflowQueryParam;
import com.iwellmass.idc.app.vo.WorkflowVO;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Inject
    private WorkflowService workflowService;
    
    @GetMapping
    @ApiOperation("查询工作流列表")
    public ServiceResult<PageData<WorkflowVO>> query(@RequestParam WorkflowQueryParam param) {
    	PageData<WorkflowVO> data = workflowService.query(param);
    	return ServiceResult.success(data);
    }
    
    @PostMapping
    @ApiOperation("保存工作流列表")
    public ServiceResult<String> save(WorkflowVO vo) {
    	workflowService.save(vo);
		return ServiceResult.success("操作成功");
    }
    
    @GetMapping("/{id}")
    @ApiOperation("查询指定工作流")
    public ServiceResult<WorkflowVO> get(@PathParam("id") String id){
    	WorkflowVO vo = workflowService.getWorkflow(id);
        return ServiceResult.success(vo);
    }
}
