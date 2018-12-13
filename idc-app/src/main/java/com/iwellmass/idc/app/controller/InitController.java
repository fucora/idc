package com.iwellmass.idc.app.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.app.service.InitService;
import com.iwellmass.idc.app.service.TaskService;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskType;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/init")
public class InitController {

    public static final String DATA_FACTORY = "data-factory";

    @Inject
    private InitService initService;
    @Inject
    private TaskRepository taskRepository;
    @Inject
    private TaskService taskService;

    @PostMapping()
    @ApiOperation("先使用工具,将df中的task任务信息导入到idctask中,再执行该接口." +
            "从t-df-task里面导数据到t-idc-task中")
    public ServiceResult derivative(@RequestBody List<Task> tasks) {
        return initService.init(tasks);
    }

}
