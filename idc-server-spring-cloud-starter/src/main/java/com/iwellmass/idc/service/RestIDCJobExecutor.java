package com.iwellmass.idc.service;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.iwellmass.idc.executor.IDCJobExecutorService;
import com.iwellmass.idc.model.JobInstance;

public interface RestIDCJobExecutor extends IDCJobExecutorService {
	
	@PostMapping
	public void execute(@RequestBody JobInstance jobInstance);

}
