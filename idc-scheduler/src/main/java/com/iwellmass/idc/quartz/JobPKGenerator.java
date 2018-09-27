package com.iwellmass.idc.quartz;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobPK;

public interface JobPKGenerator {

	String DEFAULT_GROUP = "default";

	public JobPK generate(Job job);

}
