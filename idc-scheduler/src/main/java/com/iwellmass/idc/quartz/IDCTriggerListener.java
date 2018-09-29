package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE_ID;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_PARAMETER;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_DISPATCH_TYPE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_GROUP;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_ID;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_REOD;
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
import com.iwellmass.idc.model.ScheduleStatus;

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
		DispatchType type = JOB_DISPATCH_TYPE.applyGet(context.getMergedJobDataMap());
		
		LOGGER.debug("任务 {} 已触发, DispatchType {}", trigger.getKey(), type);
		
		if (type != DispatchType.AUTO && type != DispatchType.MANUAL) {
			// should never
			context.setResult(CompletedExecutionInstruction.SET_TRIGGER_ERROR);
			throw new UnsupportedOperationException("not supported " + type + " dispatch type");
		}
		
		JobInstance jobInstance = null;
		if (isRedo) {
			jobInstance = triggerRedo(trigger, context);
		} else {
			jobInstance = triggerNormal(trigger, context);
		}
		
		LOGGER.info("执行 {}, JobPK {}, loadDate {}", jobInstance.getTaskName(),  jobInstance.getJobPK(),
				jobInstance.getLoadDate().format(IDCPlugin.DEFAULT_LOAD_DATE_DTF));
		
		// 清空日志
		pluginContext.clearLog(jobInstance.getInstanceId());
		pluginContext.batchLogger(jobInstance.getInstanceId())
			.log("创建任务实例 {}, 执行类型 {} ", jobInstance.getInstanceId(), type)
			.log("运行参数: ", jobInstance.getParameter() == null ? "--" : jobInstance.getParameter())
			.end();
		
		// 初始化执行环境
		jobInstance.setDispatchType(type);
		CONTEXT_INSTANCE.applyPut(context.getMergedJobDataMap(), jobInstance);
	}
	
	
	private JobInstance triggerRedo(Trigger trigger, JobExecutionContext context) {
		
		String jobId = JOB_ID.applyGet(trigger.getJobDataMap());
		String jobGroup = JOB_GROUP.applyGet(trigger.getJobDataMap());
		int instanceId = CONTEXT_INSTANCE_ID.applyGet(trigger.getJobDataMap());
		
		JobPK jobKey = new JobPK(jobId, jobGroup);
		
		pluginContext.updateJob(jobKey, (job) -> {
			job.setUpdateTime(LocalDateTime.now());
			job.setStatus(ScheduleStatus.NORMAL);
		});
		
		return pluginContext.updateJobInstance(instanceId, (ins -> {
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
