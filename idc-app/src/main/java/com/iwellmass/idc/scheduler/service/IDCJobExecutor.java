package com.iwellmass.idc.scheduler.service;

import com.iwellmass.idc.ExecuteRequest;

public interface IDCJobExecutor {

	void execute(ExecuteRequest request);
}
