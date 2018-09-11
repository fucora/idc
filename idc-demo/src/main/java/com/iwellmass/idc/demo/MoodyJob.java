package com.iwellmass.idc.demo;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.executor.IDCJobExecutionContext;
import com.iwellmass.idc.executor.IDCJob;

public class MoodyJob implements IDCJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(MoodyJob.class);

	private static final int UNCATCHED_EXCEPTION = 0;
	private static final int SUCCESSFULLY_FAST = 1;
	private static final int SUCCESSFULLY_NORMAL = 2;

	private final String contentType;
	
	public MoodyJob(String contentType) {
		this.contentType = contentType;
	}
	
	@Override
	public void execute(IDCJobExecutionContext context) {
		int op = new Random().nextInt(3);
		switch (op) {
		case UNCATCHED_EXCEPTION:
			uncatchedException();
			break;
		case SUCCESSFULLY_FAST:
			successfullyFast();
			break;
		case SUCCESSFULLY_NORMAL:
			successfullyNormal();
			break;
		default:
			successfullyNormal();
			break;
		}
	}

	private void uncatchedException() {
		LOGGER.info("fast-fail.");
	}

	private void successfullyFast() {
		LOGGER.info("fast-success");
	}

	private void successfullyNormal() {
		long l = new Random().nextInt(10000) + 10 * 1000;
		LOGGER.info("long {} running...", l);
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			LOGGER.info("long {} interrupted", l);
		}
		LOGGER.info("long {} successed", l);
	};
	
	@Override
	public String getContentType() {
		return contentType;
	}

}
