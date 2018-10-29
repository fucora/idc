package com.iwellmass.idc.quartz;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;

public interface JobKeyGenerator {

	String DEFAULT_GROUP = "default";

	public JobKey generate(Job job);

}
