package com.iwellmass.idc.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import com.iwellmass.idc.model.JobInstance;

@FeignClient(name = "test")
public interface RestExecutorService {

	@PostMapping(path = "/{groupId}/{taskId}/execution", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void execute(JobInstance request);

}
