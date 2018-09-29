package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_DISPATCH_TYPE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_REOD;
import static com.iwellmass.idc.quartz.IDCPlugin.toJobPK;
import static com.iwellmass.idc.quartz.IDCPlugin.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.Date;

import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.listeners.SchedulerListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.ScheduleStatus;

public class IDCSchedulerListener extends SchedulerListenerSupport {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCSchedulerListener.class);

	private final IDCPluginContext pluginContext;

	public IDCSchedulerListener(IDCPluginContext pluginContext) {
		this.pluginContext = pluginContext;
	}

	/* 创建调度 */
	public void jobScheduled(Trigger trigger) {
		
		Boolean isRedo = JOB_REOD.applyGet(trigger.getJobDataMap());
		JobPK jobPK = toJobPK(trigger.getKey());
		
		Boolean isAsync = true;
		if (!isRedo) {
			DispatchType dispatchType = JOB_DISPATCH_TYPE.applyGet(trigger.getJobDataMap());
			
			LOGGER.info("创建 {} 任务 {}, 执行模式: {}", dispatchType, jobPK, isAsync ? "异步" : "同步");

			pluginContext.updateJob(jobPK, (job) -> {
				// 更新调度信息
				job.setStatus(ScheduleStatus.NORMAL);
				if (dispatchType == DispatchType.MANUAL) {
					job.setPrevLoadDate(CONTEXT_LOAD_DATE.applyGet(trigger.getJobDataMap()));
					job.setNextLoadDate(null);
				} else {
					// prev fire time
					job.setPrevLoadDate(toLocalDateTime(trigger.getPreviousFireTime()));
					Date nextFireTime = trigger.getNextFireTime();
					// next fire time
					job.setNextLoadDate(toLocalDateTime(nextFireTime));
				}
			});
		} else {
			LOGGER.info("创建 REDO 任务 {},  执行模式: 异步", jobPK, isAsync ? "异步" : "同步") ;
		}
	}

	/* 撤销调度 */
	public void jobUnscheduled(TriggerKey triggerKey) {
		JobPK jobPk = new JobPK(triggerKey.getName(), triggerKey.getGroup());
		pluginContext.remove(jobPk);
	}

	/* 调度冻结 */
	public void triggerPaused(TriggerKey triggerKey) {
		LOGGER.info("调度任务 {} 已冻结", triggerKey);
		pluginContext.updateJob(toJobPK(triggerKey), (job) -> {
			job.setUpdateTime(LocalDateTime.now());
			job.setStatus(ScheduleStatus.PAUSED);
		});
	}
	
	/* 调度恢复 */
	public void triggerResumed(TriggerKey triggerKey) {
		LOGGER.info("调度任务 {} 已恢复", triggerKey);
		pluginContext.updateJob(toJobPK(triggerKey), (job) -> {
			job.setUpdateTime(LocalDateTime.now());
			job.setStatus(ScheduleStatus.NORMAL);
		});
	}

	
	/* 调度完结 */
	public void triggerFinalized(Trigger trigger) {
		
		JobPK jobPK = toJobPK(trigger);
		LOGGER.info("调度任务 {} 已完结", jobPK);
		
		pluginContext.updateJob(jobPK, (job) -> {
			job.setUpdateTime(LocalDateTime.now());
			job.setStatus(ScheduleStatus.COMPLETE);
		});
	}
	
	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		LOGGER.error("IDCScheduler ERROR: " + msg, cause);
	}
	
}
