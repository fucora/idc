package com.iwellmass.idc.app.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.idc.app.service.TaskService;
import com.iwellmass.idc.app.vo.TaskQueryVO;
import com.iwellmass.idc.model.Task;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController
@RequestMapping("/task")
public class TaskController {
	
	@Inject
    private TaskService taskService;

    @PostMapping("/query")
    @ApiOperation("根据taskType,taskName,查询指定要求的task")
    public ServiceResult<PageData<Task>> query(@RequestBody(required = false) TaskQueryVO taskQueryVO){
        return ServiceResult.success(taskService.query(taskQueryVO));
    }

	@GetMapping("/item")
    @ApiOperation("根据taskId查询指定task")
    public ServiceResult<PageData<Task>> item(String taskId){
        try {
            return ServiceResult.success(taskService.item(taskId));
        } catch (Exception e) {
            return ServiceResult.failure(e.getMessage());
        }
    }



	
	
}
