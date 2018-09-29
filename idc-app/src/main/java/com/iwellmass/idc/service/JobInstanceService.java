package com.iwellmass.idc.service;

import static com.iwellmass.idc.quartz.IDCContextKey.JOB_ID;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_GROUP;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_REOD;
import static com.iwellmass.idc.quartz.IDCContextKey.CONTEXT_INSTANCE_ID;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_DISPATCH_TYPE;
import static com.iwellmass.idc.quartz.IDCContextKey.JOB_SCHEDULE_TYPE;

import javax.inject.Inject;

import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.CancleRequest;
import com.iwellmass.idc.app.model.RedoRequest;
import com.iwellmass.idc.model.ExecutionLog;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.JobScript;
import com.iwellmass.idc.quartz.IDCContextKey;
import com.iwellmass.idc.quartz.IDCPlugin;
import com.iwellmass.idc.repo.ExecutionLogRepository;
import com.iwellmass.idc.repo.JobInstanceRepository;
import com.iwellmass.idc.repo.JobRepository;

@Service
public class JobInstanceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobInstanceService.class);
	
	@Inject
	private JobInstanceRepository jobInstanceRepository;
	
	@Inject
	private ExecutionLogRepository logRepository;

	@Inject
	private Scheduler scheduler;
	
	@Inject
	private JobScriptFactory jobScriptFactory;
	
	@Inject
	private JobRepository jobRepository;

	public void redo(RedoRequest request) {
		int instanceId = request.getInstanceId();
		JobInstance instance = jobInstanceRepository.findOne(instanceId);
		
		Assert.isTrue(instance != null, "找不到此实例");
		Assert.isTrue(instance.getStatus().isComplete(), "实例正在运行，无法重跑");
		
		JobPK id = instance.getJobPK();
		
		Job job = jobRepository.findOne(id);
		
		
		JobScript script = jobScriptFactory.getJobScript(job);
		
		Assert.isTrue(script != null, "找不到业务对象");
		
		
		TriggerKey triggerKey = new TriggerKey("REDO_" + instanceId, id.getJobGroup());
		
		Trigger trigger = TriggerBuilder.newTrigger()
			.withIdentity(triggerKey)
			.withSchedule(SimpleScheduleBuilder.simpleSchedule())
			.forJob(script.getScriptId(), script.getScriptGroup())
			.startNow().build();
		
		JobDataMap jdm = trigger.getJobDataMap();
		JOB_REOD.applyPut(jdm, true);
		JOB_ID.applyPut(jdm, job.getJobId());
		JOB_GROUP.applyPut(jdm, job.getJobGroup());
		JOB_SCHEDULE_TYPE.applyPut(jdm, job.getScheduleType());
		JOB_DISPATCH_TYPE.applyPut(jdm, job.getDispatchType());
		CONTEXT_INSTANCE_ID.applyPut(jdm, instanceId);

		try {
			scheduler.scheduleJob(trigger);
		} catch (SchedulerException e) {
			throw new AppException("重跑失败: " + e.getMessage(), e);
		}
	}

	public void cancle(CancleRequest req) {
		int instanceId = req.getInstanceId();
		JobInstance instance = jobInstanceRepository.findOne(instanceId);
		Assert.isTrue(instance != null, "不存在此实例 %s", instanceId);
		
		try {
			IDCPlugin plugin = IDCContextKey.IDC_PLUGIN.applyGet(scheduler.getContext());
			plugin.cancleJob(instance.getJobId(), instance.getJobGroup());
			
		} catch (SchedulerException e) {
			LOGGER.error(e.getMessage(), e);
			logRepository.log(instanceId, "无法取消任务: {}", e.getMessage());
			if (req.isForce()) {
				instance.setStatus(JobInstanceStatus.CANCLED);
				logRepository.log(instanceId, "强制取消任务", e.getMessage());
			} else {
				throw new AppException("无法取消任务: " + e.getMessage(), e);
			}
		}
	}
	
	public PageData<ExecutionLog> getJobInstanceLog(Integer id, Pager pager) {
		Pageable page = new PageRequest(pager.getPage(), pager.getLimit());
		Page<ExecutionLog> data = logRepository.findByInstanceId(id, page);
		return new PageData<>((int) data.getTotalElements(), data.getContent());
	}

}
