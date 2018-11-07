package com.iwellmass.idc.dag;

import java.util.List;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;

public interface Workflow {

	public Job getMainJob();
	
	public List<Job> getAllSubJob();
	
	public List<Job> computeFirstJob();
	
	public List<Job> getNextJob(JobKey jobKey);
	
}
