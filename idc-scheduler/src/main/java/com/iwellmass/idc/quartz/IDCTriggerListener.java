package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE_ID;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_PARAMETER;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_DISPATCH_TYPE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_REOD;
import static com.iwellmass.idc.quartz.IDCPlugin.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobInstanceType;
import com.iwellmass.idc.model.JobPK;

/**
 * 同步生成 JobInstance 记录
 */
public class IDCTriggerListener extends TriggerListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCTriggerListener.class);

	private final IDCPluginContext pluginContext;
	
	public IDCTriggerListener(IDCPluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}

	/* 初始化执行信息 */
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		
		Boolean isRedo = JOB_REOD.applyGet(trigger.getJobDataMap());
		
		JobInstance jobInstance = null;
		if (isRedo) {
			jobInstance = triggerRedo(trigger, context);
		} else {
			jobInstance = triggerNormal(trigger, context);
		}
		
		LOGGER.debug("任务 {} 已触发, DispatchType {}", trigger.getKey(), jobInstance.getInstanceType());
		
		String loadDate = jobInstance.getScheduleType().format(jobInstance.getLoadDate());
		
		LOGGER.info("执行任务 {}, JobPK {}_{}", jobInstance.getTaskName(), jobInstance.getJobPK(), loadDate);
		
		// 清空日志
		pluginContext.clearLog(jobInstance.getInstanceId());
		pluginContext.batchLogger(jobInstance.getInstanceId())
			.log("创建任务实例 {}, 实例类型 {} ", jobInstance.getInstanceId(), jobInstance.getInstanceType())
			.log("运行参数: ", Utils.isNullOrEmpty(jobInstance.getParameter()) ? "--" : jobInstance.getParameter())
			.end();
		
		// 初始化执行环境
		CONTEXT_INSTANCE.applyPut(context.getMergedJobDataMap(), jobInstance);
	}
	
	private JobInstance triggerRedo(Trigger trigger, JobExecutionContext context) {
		
		int instanceId = CONTEXT_INSTANCE_ID.applyGet(trigger.getJobDataMap());
		
		return pluginContext.updateJobInstance(instanceId, (ins -> {
			ins.setStartTime(LocalDateTime.now());
			ins.setEndTime(null);
			ins.setStatus(JobInstanceStatus.NEW);
			ins.setParameter(mergeParameter(ins.getParameter(), context));
		}));
	}
	private JobInstance triggerNormal(Trigger trigger, JobExecutionContext context) {
		
		JobPK jobKey = new JobPK(trigger.getKey().getName(), trigger.getKey().getGroup());
		
		JobDataMap data = context.getMergedJobDataMap();
		
		DispatchType type = JOB_DISPATCH_TYPE.applyGet(data);
		
		return pluginContext.createJobInstance(jobKey, (job) -> {
			
			JobInstance newIns = createJobInstance(job);
			newIns.setParameter(mergeParameter(job.getParameter(), context));
			
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
			return newIns;
		});
	}
	
	private String mergeParameter(String defaultParameter, JobExecutionContext context) {
		// TODO 强制覆盖所有参数？
		String parameter = CONTEXT_PARAMETER.applyGet(context.getTrigger().getJobDataMap());
		if (parameter != null) {
			return parameter;
		}
		return defaultParameter;
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
		jobInstance.setScheduleType(job.getScheduleType());
		jobInstance.setStartTime(LocalDateTime.now());
		jobInstance.setEndTime(null);
		jobInstance.setStatus(JobInstanceStatus.NEW);
		return jobInstance;
	}


	@Override
	public String getName() {
		return IDCTriggerListener.class.getName();
	}
}
