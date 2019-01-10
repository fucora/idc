package com.iwellmass.idc.app.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.app.service.InitService;
import com.iwellmass.idc.model.Task;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/init")
public class InitController {

    public static final String DATA_FACTORY = "data-factory";

    @Inject
    private InitService initService;
    
    @PostMapping()
    @ApiOperation("先使用工具,将df中的task任务信息导入到idctask中,再执行该接口." +
            "从t-df-task里面导数据到t-idc-task中")
    public ServiceResult<String> derivative(@RequestBody List<Task> tasks) {
        return initService.init(tasks);
    }

}
