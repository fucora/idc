package com.iwellmass.idc.app.service;

import com.google.common.collect.Lists;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.common.param.ParamParser;
import com.iwellmass.common.param.ParamType;
import com.iwellmass.idc.ExecuteRequest;
import com.iwellmass.idc.app.vo.execParam.ReferParam;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.repository.JobRepository;
import com.iwellmass.idc.scheduler.repository.TaskRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author nobita chen
 * @email nobita0522@qq.com
 * @date 2019/8/14 15:42
 * @description handle execution param,contain expr and simple param.
 */
@Component
public class ExecParamHelper {

    static final String LOAD_DATE = "loadDate";

    @Inject
    JobRepository jobRepository;
    @Inject
    TaskRepository taskRepository;

    public ExecuteRequest buildExecReq(NodeJob nodeJob, NodeTask nodeTask) {
        Job job = jobRepository.findById(nodeJob.getContainer()).orElseThrow(() -> new AppException("未发现id:" + nodeJob.getContainer() + "的job信息"));
        Task task = taskRepository.findById(new TaskID(job.getTaskName())).orElseThrow(() -> new AppException("未发现taskName:" + job.getTaskName() + "的调度计划信息"));
        ExecuteRequest request = new ExecuteRequest();
        // task running
        request.setTaskId(nodeTask.getTaskId());
        request.setParams(job.getParams());
        request.setLoadDate(getLoadDate(job.getParams()));
        // build req url
        request.setDomain(nodeTask.getDomain());
        request.setContentType(nodeTask.getContentType());
        // redundant
        request.setNodeJobId(nodeJob.getId());
        request.setJobId(job.getId());
        request.setTaskName(task.getTaskName());
        request.setScheduleType(task.getScheduleType());
        if (job.getShouldFireTime() != null) {
            request.setShouldFireTime(job.getShouldFireTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        return request;
    }

    public List<ExecParam> parse(Task task) {
        ReferParam referParam = new ReferParam(task.getPrevFireTime(), LocalDateTime.now(), task.getUpdatetime());
        ParamParser parser = new ParamParser(Collections.singletonMap("idc", referParam));
        List<ExecParam> execParams = deepCopyExecParam(task.getParams());
        // modify the simple loadDate param to ognl expression
        for (ExecParam execParam : execParams) {
            if (TaskService.loadDateParams.containsKey(execParam.getDefaultExpr().trim())) {
                execParam.setDefaultExpr(TaskService.loadDateParams.get(execParam.getDefaultExpr().trim()));
            }
        }
        parser.parse(execParams);
        return execParams;
    }

//    public String getLoadDate(List<ExecParam> execParams) {
//        this
//    }

    public static String getLoadDate(List<ExecParam> execParams) {
        String loadDate = "";
        for (ExecParam p : execParams) {
            if (p.getName().equalsIgnoreCase(LOAD_DATE)) {
                loadDate = p.getValue();
                break;
            }
        }
        return loadDate;
    }

    // there must adapt a new List<ExecParam> copied on task'params,otherwise the task's params will be modified to the value after firstly parse
    public List<ExecParam> deepCopyExecParam(List<ExecParam> source) {
        List<ExecParam> result = Lists.newArrayList();
        source.forEach(e -> {
            ExecParam r = new ExecParam();
            r.setName(e.getName());
            r.setDefaultExpr(e.getDefaultExpr());
            r.setParamType(e.getParamType());
            r.setValue(e.getValue());
            result.add(r);
        });
        return result;
    }

    public static void main(String[] args) {
        // 计算参数
        Task task = new Task();
        task.setPrevFireTime(LocalDateTime.now());
        java.time.LocalDateTime.now();
        task.setParams(Lists.newArrayList(new ExecParam("loadDate", null, ParamType.VARCHAR)));
        new ExecParamHelper().parse(task).forEach(execParam -> {
            System.out.println(execParam.getValue());
        });

    }

}
