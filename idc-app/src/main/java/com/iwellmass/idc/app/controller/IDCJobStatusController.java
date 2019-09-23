package com.iwellmass.idc.app.controller;

import com.google.common.collect.Lists;
import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.app.service.JobHelper;
import com.iwellmass.idc.app.service.JobService;
import com.iwellmass.idc.app.service.TaskService;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.message.FinishMessage;
import com.iwellmass.idc.message.*;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.scheduler.quartz.IDCJobStore;
import io.swagger.annotations.ApiOperation;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.inject.Inject;

@RestController
@RequestMapping("/job")
public class IDCJobStatusController {

    @Resource
    Scheduler qs;

    final Logger logger = LoggerFactory.getLogger(getClass());

    @ApiOperation("任务开始")
    @PutMapping("/start")
    public void fireStartEvent(@RequestBody StartEvent event) {
        ReadyMessage message = ReadyMessage.newMessage(event.getNodeJobId());
        message.setMessage("任务准备执行" + event.getNodeJobId());
        TaskEventPlugin.eventService(qs).send(message);
    }

    @ApiOperation("发送过程信息")
    @PutMapping(path = "/progress")
    public void saveRuntimeUrlLog(@RequestBody ProgressEvent event) {
        RunningMessage message = RunningMessage.newMessage(event.getNodeJobId());
        message.setMessage("任务正在执行" + event.getNodeJobId());
        TaskEventPlugin.eventService(qs).send(message);
    }

    @PutMapping("/complete")
    public void fireCompleteEvent(@RequestBody CompleteEvent event) {
        JobInstanceStatus jobInstanceStatus = event.getFinalStatus();
        JobMessage message;
        if (jobInstanceStatus == JobInstanceStatus.FINISHED) {
            message = FinishMessage.newMessage(event.getNodeJobId());
        } else if (jobInstanceStatus == JobInstanceStatus.FAILED) {
            message = FailMessage.newMessage(event.getNodeJobId());
            message.setThrowable(event.getThrowable());
        } else if (jobInstanceStatus == JobInstanceStatus.CANCLED) {
            message = CancelMessage.newMessage(event.getNodeJobId());
        } else {
            throw new RuntimeException("illegal message type:" + jobInstanceStatus + ",instanceId:" + event.getNodeJobId());
        }
        message.setMessage(event.getMessage());
        TaskEventPlugin.eventService(qs).send(message);
    }

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<String> exception(Exception e) {
        ResponseEntity<String> resp = new ResponseEntity<>("无法完成操作，服务器异常: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        return resp;
    }
}
