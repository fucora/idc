package com.iwellmass.idc.app.service;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.app.controller.InitController;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskType;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InitService {

    @Inject
    private TaskService taskService;

    public ServiceResult init(List<Task> tasks) {
        // 过滤掉 contentType 是NONE的情况
        try {
            tasks.stream().filter(c -> !c.getContentType().equals("NONE")).collect(Collectors.toList()).forEach((task) -> {
                if (task.getParameter().equalsIgnoreCase("null")) {
                    task.setParameter(null);
                }
                taskService.saveTask(task);
            });
            return ServiceResult.success("添加成功");
        } catch (AppException e) {
            return ServiceResult.failure("添加失败");
        }

    }

}
