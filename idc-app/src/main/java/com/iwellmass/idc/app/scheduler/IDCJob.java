package com.iwellmass.idc.app.scheduler;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.iwellmass.idc.JobEnv;

public interface IDCJob {

	@PostMapping(path = "/execution", consumes = MediaType.APPLICATION_JSON_VALUE)
	void execute(@RequestBody JobEnv jobEnv);

}
