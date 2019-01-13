package com.iwellmass.idc.app.rpc;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.ProgressEvent;
import com.iwellmass.idc.executor.StartEvent;
import com.iwellmass.idc.quartz.IDCPlugin;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/job")
public class IDCStatusService {

	@Inject
	private IDCPlugin idcPlugin;

	@ApiOperation("任务开始")
	@PutMapping("/start")
	public void fireStartEvent(@RequestBody StartEvent event) {
		idcPlugin.getStatusService().fireStartEvent(event);
	}

	@ApiOperation("发送过程信息")
	@PutMapping(path = "/progress")
	public void saveRuntimeUrlLog(@RequestBody ProgressEvent progressEvent) {
		idcPlugin.getStatusService().fireProgressEvent(progressEvent);
	}

	@PutMapping("/complete")
	public void fireCompleteEvent(@RequestBody CompleteEvent event) {
		idcPlugin.getStatusService().fireCompleteEvent(event);
	}

	@ExceptionHandler({ Throwable.class })
	public ResponseEntity<String> exception(Exception e) {
		ResponseEntity<String> resp = new ResponseEntity<>("无法完成操作，服务器异常: " + e.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
		return resp;
	}
}
