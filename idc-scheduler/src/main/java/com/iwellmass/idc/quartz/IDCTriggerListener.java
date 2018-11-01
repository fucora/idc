package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.IDC_LOGGER;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.model.JobInstance;

/**
 * 同步生成 JobInstance 记录
 */
public class IDCTriggerListener extends TriggerListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCTriggerListener.class);

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		
		IDCLogger idcLogger = IDC_LOGGER.applyGet(context.getScheduler());
		
		JobInstance ins = IDCContextKey.JOB_INSTANCE.applyGet(trigger.getJobDataMap());
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("任务 {} 已触发, DispatchType {}", ins.getJobKey(), ins.getInstanceType());
		}
		
		idcLogger.clearLog(ins.getInstanceId())
			.log(ins.getInstanceId(), "创建任务实例 {}, 实例类型 {} ", ins.getInstanceId(), ins.getInstanceType())
			.log(ins.getInstanceId(), "运行参数:  {}", Utils.isNullOrEmpty(ins.getParameter()) ? "--" : ins.getParameter());
		
	}

	@Override
	public String getName() {
		return IDCTriggerListener.class.getName();
	}
}
