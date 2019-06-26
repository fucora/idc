package com.iwellmass.idc.client.autoconfig;

import com.iwellmass.idc.executor.ProgressEvent;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.executor.StartEvent;

@FeignClient("idc")
public interface RestIDCStatusService extends IDCStatusService {

	@PutMapping(path = "/job/complete", consumes = MediaType.APPLICATION_JSON_VALUE)
	void complete(@RequestBody CompleteEvent event);
	
	
	@PutMapping(path = "/job/start", consumes = MediaType.APPLICATION_JSON_VALUE)
	void start(@RequestBody StartEvent event);

    @PutMapping(path = "/job/progress", consumes = MediaType.APPLICATION_JSON_VALUE)
	void progress(ProgressEvent setMessage);
}
