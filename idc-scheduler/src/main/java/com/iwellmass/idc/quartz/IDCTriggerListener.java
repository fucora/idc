package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_PARAMETER;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_DISPATCH_TYPE;
import static com.iwellmass.idc.quartz.IDCPlugin.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.listeners.TriggerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobInstanceType;
import com.iwellmass.idc.model.JobPK;

public class IDCTriggerListener extends TriggerListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCTriggerListener.class);

	private final IDCPluginContext pluginContext;

	public IDCTriggerListener(IDCPluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}

	/* 初始化执行信息 */
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		
		JobDataMap data = context.getMergedJobDataMap();
		
		DispatchType type = JOB_DISPATCH_TYPE.applyGet(data);

		JobPK jobKey = new JobPK(trigger.getKey().getName(), trigger.getKey().getGroup());
		
		if (type != DispatchType.AUTO && type != DispatchType.MANUAL) {
			// should never
			context.setResult(CompletedExecutionInstruction.SET_TRIGGER_ERROR);
			throw new UnsupportedOperationException("not supported " + type + " dispatch type");
		}
		
		JobInstance jobInstance = pluginContext.createJobInstance(jobKey, (job) -> {
			
			JobInstance newIns = createJobInstance(job);
			
			// TODO 强制覆盖所有参数？
			String parameter = CONTEXT_PARAMETER.applyGet(data);
			if (parameter != null) {
				newIns.setParameter(parameter);
			}
			
			LocalDateTime loadDate = null;
			
			// 其他参数
			if (type == DispatchType.MANUAL) {
				loadDate = CONTEXT_LOAD_DATE.applyGet(data);
				newIns.setInstanceType(JobInstanceType.MANUAL);
				newIns.setLoadDate(loadDate);
				newIns.setNextLoadDate(null);
				newIns.setShouldFireTime(IDCPlugin.toEpochMilli(loadDate));
			} else {
				Date shouldFireTime = context.getScheduledFireTime();
				loadDate = toLocalDateTime(shouldFireTime);
				newIns.setInstanceType(JobInstanceType.AUTO);
				newIns.setLoadDate(loadDate);
				newIns.setNextLoadDate(toLocalDateTime(context.getNextFireTime()));
				newIns.setShouldFireTime(shouldFireTime == null ? -1 : shouldFireTime.getTime());
			}
			LOGGER.info("执行 {} 调度任务 {}, Full loadDate {}", type, jobKey, newIns.getLoadDate().format(IDCPlugin.DEFAULT_LOAD_DATE_DTF));
			return newIns;
		});
		
		// 初始化执行环境
		CONTEXT_INSTANCE.applyPut(context.getMergedJobDataMap(), jobInstance);
	}
	
	private JobInstance createJobInstance(Job job) {
		JobInstance jobInstance = new JobInstance();
		jobInstance.setJobId(job.getJobId());
		jobInstance.setJobGroup(job.getJobGroup());
		jobInstance.setTaskId(job.getTaskId());
		jobInstance.setGroupId(job.getGroupId());
		jobInstance.setTaskName(job.getTaskName());
		jobInstance.setDescription(job.getDescription());
		jobInstance.setContentType(job.getContentType());
		jobInstance.setTaskType(job.getTaskType());
		jobInstance.setAssignee(job.getAssignee());
		jobInstance.setParameter(job.getParameter());
		jobInstance.setStartTime(LocalDateTime.now());
		jobInstance.setEndTime(null);
		jobInstance.setScheduleType(job.getScheduleType());
		jobInstance.setDispatchType(job.getDispatchType());
		jobInstance.setStatus(JobInstanceStatus.NEW);
		jobInstance.setDispatchType(job.getDispatchType());
		return jobInstance;
	}


	@Override
	public String getName() {
		return IDCTriggerListener.class.getName();
	}
}
