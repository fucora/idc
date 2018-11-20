package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_PLUGIN;

import org.quartz.JobExecutionContext;
import org.quartz.JobPersistenceException;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.IDCUtils;
import com.iwellmass.idc.TwoTuple;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

/**
 * 同步生成 JobInstance 记录
 */
public class IDCTriggerListener extends TriggerListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCTriggerListener.class);

	private final IDCJobStore store;
	
	public IDCTriggerListener(IDCJobStore store) {
		this.store = store;
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		
		IDCPlugin idcPlugin = IDC_PLUGIN.applyGet(context.getScheduler());
		
		TwoTuple<JobKey, Long> jobInsKey = IDCUtils.parseJobInstanceKey(trigger);
		
		Job job = null;
		JobInstance ins = null;
		try {
			ins = store.retrieveIDCJobInstance(jobInsKey._1, jobInsKey._2);
			job = store.retrieveIDCJob(ins.getJobKey());
		} catch (JobPersistenceException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("触发任务 {}, TaskType: {}, DispatchType {}", ins.getJobKey(), job.getTaskType(), ins.getInstanceType());
		}
		
		idcPlugin.getLogger().clearLog(ins.getInstanceId())
			.log(ins.getInstanceId(), "创建任务实例 {}, 实例类型 {} ", ins.getInstanceId(), ins.getInstanceType())
			.log(ins.getInstanceId(), "运行参数: {}", Utils.isNullOrEmpty(ins.getParameter()) ? "--" : ins.getParameter());
		
		IDCContextKey.CONTEXT_JOB.applyPut(context, job);
		IDCContextKey.CONTEXT_INSTANCE.applyPut(context, ins);
	}
	
	@Override
	public String getName() {
		return IDCTriggerListener.class.getName();
	}
}
