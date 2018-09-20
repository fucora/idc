package com.iwellmass.idc.service;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.iwellmass.idc.executor.IDCJobExecutorService;
import com.iwellmass.idc.model.JobInstance;

public interface RestIDCJobExecutor extends IDCJobExecutorService {
	
	@PostMapping(path="/execution", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void execute(@RequestBody JobInstance jobInstance);

}
