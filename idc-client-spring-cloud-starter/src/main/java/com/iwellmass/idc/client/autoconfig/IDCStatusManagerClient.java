package com.iwellmass.idc.client.autoconfig;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.StartEvent;

@FeignClient("idc")
public interface IDCStatusManagerClient extends IDCStatusService {

	@PutMapping("/job/complete")
	public void fireCompleteEvent(@RequestBody CompleteEvent event);
	
	
	@PutMapping("/job/start")
	public void fireStartEvent(@RequestBody StartEvent event);
}
