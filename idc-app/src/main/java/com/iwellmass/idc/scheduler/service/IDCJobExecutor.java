package com.iwellmass.idc.scheduler.service;

import com.iwellmass.idc.JobEnv;
import com.iwellmass.idc.app.scheduler.ExecuteRequest;

public interface IDCJobExecutor {

	void execute(ExecuteRequest request);

	void execute(JobEnv jobEnv);
}
