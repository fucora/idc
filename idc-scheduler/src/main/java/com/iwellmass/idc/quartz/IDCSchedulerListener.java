package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_LOAD_DATE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_ASYNC;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_DISPATCH_TYPE;
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
		
		Boolean isAsync = JOB_ASYNC.applyGet(trigger.getJobDataMap());
		DispatchType dispatchType = JOB_DISPATCH_TYPE.applyGet(trigger.getJobDataMap());
		
		TriggerKey triggerKey = trigger.getKey();
		LOGGER.info("创建调度 {}, 调度模式 {}, 执行模式 {}", triggerKey, dispatchType, isAsync ? "异步" : "同步");
		
		Boolean isRedo = false;
		if (isRedo) {
			LOGGER.info("重跑调度 {}", triggerKey);
		} else {
			pluginContext.updateJob(toJobPK(triggerKey), (job) -> {
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
		}
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
		TriggerKey tk = trigger.getKey();
		LOGGER.info("调度任务 {} 已完结", tk);
		
		pluginContext.updateJob(toJobPK(tk), (job) -> {
			job.setUpdateTime(LocalDateTime.now());
			job.setStatus(ScheduleStatus.COMPLETE);
		});
	}
	
	@Override
	public void schedulerError(String msg, SchedulerException cause) {
		LOGGER.error("IDCScheduler ERROR: " + msg, cause);
	}
	
	private JobPK toJobPK(TriggerKey tk) {
		return new JobPK(tk.getName(), tk.getGroup());
	}
}
