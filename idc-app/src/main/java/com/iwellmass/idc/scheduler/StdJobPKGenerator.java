package com.iwellmass.idc.scheduler;

import org.springframework.stereotype.Component;

import com.iwellmass.common.util.Assert;
import com.iwellmass.idc.app.model.TaskKey;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.quartz.JobPKGenerator;

/**
 * 单例模式，一个任务只能生成一个调度，使用 taskId, groupId 作为 JobName & JobGroup;
 */
@Component
public class StdJobPKGenerator implements JobPKGenerator {

	@Override
	public JobPK generate(Job job) {
		return valueOf(job.getTaskId(), job.getGroupId());
	}

	public static JobPK valueOf(TaskKey taskKey) {
		return valueOf(taskKey.getTaskId(), taskKey.getGroupId());
	}

	private static JobPK valueOf(String taskId, String groupId) {

		Assert.isTrue(taskId != null, "taskId 不能为空");
		Assert.isTrue(groupId != null, "groupId 不能为空");

		return new JobPK(taskId, groupId);
	}

}