package com.iwellmass.idc.scheduler;

import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.repo.JobRepository;

public class IDCContext {

	private static JobRepository jobRepository;
	
	private static JobInstanceRepository jobInstanceRepository;

	public static JobRepository getJobRepository() {
		return jobRepository;
	}

	public static void setJobRepository(JobRepository jobRepository) {
		IDCContext.jobRepository = jobRepository;
	}

	public static JobInstanceRepository getJobInstanceRepository() {
		return jobInstanceRepository;
	}

	public static void setJobInstanceRepository(JobInstanceRepository jobInstanceRepository) {
		IDCContext.jobInstanceRepository = jobInstanceRepository;
	}
	
}
