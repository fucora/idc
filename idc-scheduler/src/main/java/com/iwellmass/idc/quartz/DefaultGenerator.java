package com.iwellmass.idc.quartz;

import java.util.UUID;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobPK;

/**
 * 随机 UUID 码作为 jobName, {@link JobPKGenerator#DEFAULT_GROUP} 作为 jobGroup
 */
public class DefaultGenerator implements JobPKGenerator {

	public static final DefaultGenerator INSTANCE = new DefaultGenerator();

	private DefaultGenerator() {
	}

	@Override
	public JobPK generate(Job job) {
		String jobName = UUID.randomUUID().toString();
		return new JobPK(jobName, DEFAULT_GROUP);
	}

}
