package com.iwellmass.idc.rpc;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.StartEvent;

@RestController
@RequestMapping("/job")
public class IDCStatusController {

	@Inject
	private IDCStatusService statusService;

	@PutMapping("/complete")
	public void fireCompleteEvent(@RequestBody CompleteEvent event) {
		statusService.fireCompleteEvent(event);
	}

	@PutMapping("/start")
	public void fireStartEvent(@RequestBody StartEvent event) {
		statusService.fireStartEvent(event);
	}

	@ExceptionHandler({ Throwable.class })
	public ResponseEntity<String> exception(Exception e) {
		ResponseEntity<String> resp = new ResponseEntity<>("无法完成操作，服务器异常: " + e.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR);
		return resp;
	}

}
