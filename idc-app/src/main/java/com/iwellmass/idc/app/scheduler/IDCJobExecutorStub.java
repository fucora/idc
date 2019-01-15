package com.iwellmass.idc.app.scheduler;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.iwellmass.idc.executor.IDCJobExecutorService;
import com.iwellmass.idc.model.JobEnv;

public interface IDCJobExecutorStub extends IDCJobExecutorService {
	
	@PostMapping(path="/execution", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void execute(@RequestBody JobEnv jobEnv);

}