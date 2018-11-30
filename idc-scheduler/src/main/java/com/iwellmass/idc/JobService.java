package com.iwellmass.idc;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;

public interface JobService {
	
	public void saveJob(Job job);
	
	public Job getJob(JobKey jobKey);

}
