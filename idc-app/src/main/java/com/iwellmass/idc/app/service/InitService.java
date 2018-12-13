package com.iwellmass.idc.app.service;

import com.iwellmass.common.ServiceResult;
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
    private TaskRepository repository;
    @Inject
    private TaskService taskService;

    public ServiceResult init(List<Task> tasks) {
        // 过滤掉 contentType 是NONE的情况
        List<Task> tasks1 = tasks.stream().filter(c -> !c.getContentType().equals("NONE")).collect(Collectors.toList());
        repository.save((Iterable<Task>) () -> tasks1.iterator());
        if (tasks.size() == repository.countAll()) {
            // success
            for (int i = 0;i < tasks.size();i++) {
                System.out.println("InitController init task ->job : " + i + "  total : " + tasks.size());
                taskService.saveTask(tasks.get(i));
            }
            return ServiceResult.success("添加成功");
        } else {
            //fail
            repository.delete(() -> tasks1.iterator());
            return ServiceResult.failure("添加失败");
        }
    }

}
