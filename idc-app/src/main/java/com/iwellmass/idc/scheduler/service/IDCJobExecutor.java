package com.iwellmass.idc.scheduler.service;

import com.iwellmass.idc.scheduler.model.AbstractJob;

public interface IDCJobExecutor {

	void execute(AbstractJob job);
}
