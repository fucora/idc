package com.iwellmass.idc.app.service;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.quartz.IDCPlugin;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job")
public class IDCStatusController {

	@Inject
	private JobService jobService;
	
	@Inject
	private IDCPlugin idcPlugin;
	
	
	@PutMapping("/complete")
	public void fireCompleteEvent(@RequestBody CompleteEvent event) {
		idcPlugin.getStatusService().fireCompleteEvent(event);
	}

	@PutMapping("/start")
	public void fireStartEvent(@RequestBody StartEvent event) {
		idcPlugin.getStatusService().fireStartEvent(event);
	}
	
	@ApiOperation("获取任务信息")
	@GetMapping("/schedule-properties")
	public ScheduleProperties getScheduleProperties(TaskKey taskKey) {
		JobKey jobPK = new JobKey(taskKey.getTaskId(), taskKey.getTaskGroup());
		Job job = jobService.findJob(jobPK);
		if (job == null) {
			return null;
		}
		return null;
	}
	
	@ExceptionHandler({ Throwable.class })
	public ResponseEntity<String> exception(Exception e) {
		ResponseEntity<String> resp = new ResponseEntity<>("无法完成操作，服务器异常: " + e.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
		return resp;
	}

}
