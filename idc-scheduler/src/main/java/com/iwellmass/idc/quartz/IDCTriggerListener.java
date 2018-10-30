package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCPlugin.getContext;
import static com.iwellmass.idc.quartz.IDCUtils.parseJobKey;
import static com.iwellmass.idc.quartz.IDCUtils.parseLoadDate;

import java.time.LocalDateTime;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobKey;

/**
 * 同步生成 JobInstance 记录
 */
public class IDCTriggerListener extends TriggerListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCTriggerListener.class);

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		
		JobKey jobKey = parseJobKey(trigger);
		LocalDateTime loadDate = parseLoadDate(trigger, context);
		
		JobInstance ins = getContext().getJobInstance(jobKey, loadDate);
		
		LOGGER.debug("任务 {} 已触发, DispatchType {}", jobKey, ins.getInstanceType());
		
		getContext().clearLog(ins.getInstanceId());
		getContext().batchLogger(ins.getInstanceId())
			.log("创建任务实例 {}, 实例类型 {} ", ins.getInstanceId(), ins.getInstanceType())
			.log("运行参数:  {}", Utils.isNullOrEmpty(ins.getParameter()) ? "--" : ins.getParameter())
			.end();
		
		CONTEXT_INSTANCE.applyPut(context.getMergedJobDataMap(), ins);
		
	}

	@Override
	public String getName() {
		return IDCTriggerListener.class.getName();
	}
}
