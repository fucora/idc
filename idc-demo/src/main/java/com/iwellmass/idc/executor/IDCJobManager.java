package com.iwellmass.idc.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class IDCJobManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCJobManager.class);
	
	private MoodyIDCJob moodyIDCJob = new MoodyIDCJob();
	
	public IDCJob getJob(String taskType) {
		return moodyIDCJob;
	}
	
	static class MoodyIDCJob implements IDCJob {
		@Override
		public void execute(IDCExecutionContext context) {
			LOGGER.info("moody job running");
			
			LOGGER.info("moody job finished");
		}
	}
}
