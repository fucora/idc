package com.iwellmass.idc.quartz;

import com.iwellmass.idc.model.Task;

public class SimpleIDCPlugin extends IDCPlugin {

	protected  Class<? extends org.quartz.Job> getJobClass(Task task) {
		return SimpleJob.class;
	}

}
