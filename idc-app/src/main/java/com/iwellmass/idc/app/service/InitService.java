package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.model.Task;

@Service
public class InitService {

    @Inject
    private TaskService taskService;

    public ServiceResult<String> init(List<Task> tasks) {
        // 过滤掉 contentType 是NONE的情况
        try {
            tasks.stream().filter(c -> !c.getContentType().equals("NONE")).collect(Collectors.toList()).forEach((task) -> {
                taskService.update(task);
            });
            return ServiceResult.success("添加成功");
        } catch (AppException e) {
            return ServiceResult.failure("添加失败");
        }

    }

}
