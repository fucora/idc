package com.iwellmass.idc.app.service;

import com.google.common.collect.Lists;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.common.param.ParamParser;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.ExecuteRequest;
import com.iwellmass.idc.app.util.IDCUtils;
import com.iwellmass.idc.app.vo.execParam.ReferParam;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.repository.JobRepository;
import com.iwellmass.idc.scheduler.repository.TaskRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

/**
 * @author nobita chen
 * @email nobita0522@qq.com
 * @date 2019/8/14 15:42
 * @description handle execution param,contain expr and simple param.
 */
@Component
public class ExecParamHelper {

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
        request.setParams(Lists.newArrayList());
        request.setLoadDate("");
        // build req url
        request.setDomain(nodeTask.getDomain());
        request.setContentType(nodeTask.getContentType());
        // redundant
        request.setNodeJobId(nodeJob.getId());
        request.setJobId(job.getId());
        request.setTaskName(task.getTaskName());
        request.setScheduleType(task.getScheduleType());
        request.setShouldFireTime(job.getShouldFireTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return request;
    }

    public static void parse(NodeJob nodeJob) {
        ReferParam referParam = new ReferParam();
//        referParam.setShouldFireTime(nodeJob.get);


    }

//    public static void main(String[] args) {
//        // 计算参数
//        List<ExecParam> params = job.getParameter();
//        if (!Utils.isNullOrEmpty(params)) {
//            IDCDefaultParam dp = new IDCDefaultParam();
//            dp.setShouldFireTime(IDCUtils.toLocalDateTime(ins.getShouldFireTime()));
//            ParamParser parser = new ParamParser(Collections.singletonMap("idc", dp));
//            parser.parse(params);
//            ins.setParameter(params);
//            for (ExecParam param : params) {
//                if ("loadDate".equals(param.getName())) {
//                    ins.setLoadDate(param.getValue());
//                }
//            }
//        }
//        // loadDate
//        if (ins.getLoadDate() == null) {
//            ins.setLoadDate(ins.getScheduleType().format(IDCUtils.toLocalDateTime(shouldFireTime)));
//        }
//        // clear first
//        idcDriverDelegate.deleteJobInstance(conn, jobKey, ins.getShouldFireTime());
//        // insert it
//        return idcDriverDelegate.insertJobInstance(conn, ins).getInstanceId() + "";
//    }

}
