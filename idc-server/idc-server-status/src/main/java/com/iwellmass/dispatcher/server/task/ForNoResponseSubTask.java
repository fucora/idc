package com.iwellmass.dispatcher.server.task;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import java.util.Date;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.context.QuartzContext;
import com.iwellmass.dispatcher.common.context.SpringContext;
import com.iwellmass.dispatcher.common.dao.DdcRunningTaskMapper;
import com.iwellmass.dispatcher.common.dao.DdcSubtaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskExecuteStatusMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskMapper;
import com.iwellmass.dispatcher.common.model.DdcRunningTask;
import com.iwellmass.dispatcher.common.model.DdcRunningTaskExample;
import com.iwellmass.dispatcher.common.model.DdcSubtaskExecuteHistory;
import com.iwellmass.dispatcher.common.model.DdcTask;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteHistory;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteStatus;
import com.iwellmass.dispatcher.common.task.DmallTask;
import com.iwellmass.dispatcher.common.utils.AlarmUtils;
import com.iwellmass.dispatcher.thrift.bvo.TaskStatus;

/**
 * 未响应子任务重新执行
 * @author duheng
 *
 */
@DisallowConcurrentExecution
public class ForNoResponseSubTask implements Job {
	
	private Logger logger = LoggerFactory.getLogger(ForNoResponseSubTask.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		DdcTaskExecuteHistoryMapper ddcTaskExecuteHistoryMapper = SpringContext.getApplicationContext().getBean(DdcTaskExecuteHistoryMapper.class);
		DdcSubtaskExecuteHistoryMapper ddcSubtaskExecuteHistoryMapper = SpringContext.getApplicationContext().getBean(DdcSubtaskExecuteHistoryMapper.class);
		DdcRunningTaskMapper ddcRunningTaskMapper = SpringContext.getApplicationContext().getBean(DdcRunningTaskMapper.class);
		DdcTaskExecuteStatusMapper ddcTaskExecuteStatusMapper = SpringContext.getApplicationContext().getBean(DdcTaskExecuteStatusMapper.class);
		DdcTaskMapper ddcTaskMapper = SpringContext.getApplicationContext().getBean(DdcTaskMapper.class);
		
		DdcRunningTaskExample example = new DdcRunningTaskExample();
		DdcRunningTaskExample.Criteria criteria = example.createCriteria();
		criteria.andTimeoutGreaterThan(0L).andTimeoutTimeLessThan(new Date()).andStartTimeIsNotNull()
			.andCompleteTimeIsNull().andWorkflowExecuteIdGreaterThan(0L).andWorkflowIdGreaterThan(0);;

		List<DdcRunningTask> results = ddcRunningTaskMapper.selectByExample(example);
		logger.info("检查执行超时流程子任务开始运行，发现执行超时流程子任务个数：{}", results.size());
		
		if(results != null && !results.isEmpty()) {
			Scheduler scheduler = QuartzContext.getScheduler();
			for(DdcRunningTask result : results) {
				try {
					String remarks = null;
					if(result.getTimeoutRetry() == Constants.ENABLED && result.getDispatchCount() <= result.getTimeoutRetryTimes()) {
						DdcTaskExecuteHistory ddcTaskExecuteHistory = ddcTaskExecuteHistoryMapper.selectByPrimaryKey(result.getWorkflowExecuteId());
						if(StringUtils.isEmpty(ddcTaskExecuteHistory.getCompleteTime())) {
							JobDetail jobDetail = JobBuilder.newJob(DmallTask.class)
									.withIdentity(Constants.JOB_NO_RESPONSE_SUBTASK + "_" + result.getExecuteId(), Constants.DDC_SCHEDULER_GROUP)
									.usingJobData("taskId", result.getTaskId())
									.usingJobData("executeId", result.getExecuteId())
									.usingJobData("executeBatchId", result.getExecuteBatchId())
									.usingJobData("workflowId", result.getWorkflowId())
									.usingJobData("workflowExecuteId", result.getWorkflowExecuteId())
									.usingJobData("currentRetryTime", 0)
									.usingJobData("triggerType", Constants.TASK_TRIGGER_TYPE_SYSTEM)
									.build();
							
							Trigger trigger = TriggerBuilder.newTrigger()
									.forJob(jobDetail)
									.withIdentity(Constants.TRIGGER_NO_RESPONSE_SUBTASK + "_" + + result.getExecuteId(), Constants.DDC_SCHEDULER_GROUP)
									.startNow()
									.withSchedule(simpleSchedule().withRepeatCount(0))
									.build();
							
							scheduler.scheduleJob(jobDetail, trigger);
							
							if(result.getDispatchCount() == 1) {
								remarks = "执行超时，将进行重试！";
							} else {
								remarks = "重试执行超时，当前重试次数：" + result.getDispatchCount() + "，最大重试次数：" + result.getTimeoutRetryTimes() + "！";
							}
						} else {
							remarks = String.format("子任务{%s}执行超时，流程任务已经结束，本次执行被系统结束！", result.getTaskId());
						}
					} else {	
						DdcSubtaskExecuteHistory ddcSubtaskExecuteHistory = new DdcSubtaskExecuteHistory();
						ddcSubtaskExecuteHistory.setExecuteId(result.getExecuteId());
						ddcSubtaskExecuteHistory.setCompleteTime(new Date());
						ddcSubtaskExecuteHistory.setExecuteResult(TaskStatus.EXECUTE_TIMEOUT);
						ddcSubtaskExecuteHistory.setExecuteResultDesc(remarks);
						ddcSubtaskExecuteHistoryMapper.updateByPrimaryKeySelective(ddcSubtaskExecuteHistory);
						
						DdcTaskExecuteHistory ddcTaskExecuteHistory = new DdcTaskExecuteHistory();
						ddcTaskExecuteHistory.setCompleteTime(new Date());
						ddcTaskExecuteHistory.setExecuteId(result.getWorkflowExecuteId());
						ddcTaskExecuteHistory.setExecuteResult(TaskStatus.FAILED);
						ddcTaskExecuteHistory.setExecuteResultDesc(remarks);
						ddcTaskExecuteHistoryMapper.updateByPrimaryKeySelective(ddcTaskExecuteHistory);
						
						DdcTask ddcTask = ddcTaskMapper.selectByPrimaryKey(result.getTaskId());
						if(result.getTimeoutRetry() == Constants.ENABLED) {
							logger.info("流程子任务执行超时，重试达到最大重试次数，本次执行被系统结束！任务编号：{}，执行编号：{}，流程任务执行编号：{}，执行批次编号：{}，最大重试次数：{}", result.getTaskId(), result.getExecuteId(), result.getWorkflowExecuteId(), result.getExecuteBatchId(), result.getTimeoutRetryTimes());
							remarks = "流程子任务：" + (ddcTask != null ? ddcTask.getTaskName() : result.getTaskId()) + "执行超时，已达到最大重试次数：" + result.getTimeoutRetryTimes() + "！";
						} else {
							logger.info("流程子任务执行超时，未配置超时重试，本次执行被系统结束！任务编号：{}，执行编号：{}，流程任务执行编号：{}，执行批次编号：{}", result.getTaskId(), result.getExecuteId(), result.getWorkflowExecuteId(), result.getExecuteBatchId());
							remarks = "流程子任务：" + (ddcTask != null ? ddcTask.getTaskName() : result.getTaskId()) + "执行超时，未配置超时重试，本次执行被系统结束！";
						}

						AlarmUtils.sendAndRecordAlarm(Constants.ALARM_KEY_TIMEOUT, result.getTaskId(), remarks);
						
						//删除关联的流程任务
						example = new DdcRunningTaskExample();
						criteria = example.createCriteria();
						criteria.andExecuteIdEqualTo(result.getWorkflowExecuteId()).andWorkflowIdLessThan(0);
						ddcRunningTaskMapper.deleteByExample(example);
					}
					ddcRunningTaskMapper.deleteByPrimaryKey(result.getId());
					//增加历史操作记录
					DdcTaskExecuteStatus ddcTaskExecuteStatus = new DdcTaskExecuteStatus();
					ddcTaskExecuteStatus.setTaskId(result.getTaskId());
					ddcTaskExecuteStatus.setExecuteId(result.getExecuteId());
					ddcTaskExecuteStatus.setExecuteBatchId(result.getExecuteBatchId());
					ddcTaskExecuteStatus.setWorkflowId(result.getWorkflowId());
					ddcTaskExecuteStatus.setWorkflowExecuteId(result.getWorkflowExecuteId());
					ddcTaskExecuteStatus.setStatus(TaskStatus.EXECUTE_TIMEOUT);
					ddcTaskExecuteStatus.setMessage(remarks);
					ddcTaskExecuteStatus.setTimestamp(new Date());
					ddcTaskExecuteStatusMapper.insertSelective(ddcTaskExecuteStatus);
				} catch(Exception e) {
					logger.info("任务执行超时，重试出错！任务编号：{}，执行编号：{}，流程任务执行编号：{}，执行批次编号：{}，错误信息：{}", result.getTaskId(), result.getExecuteId(), result.getWorkflowExecuteId(), result.getExecuteBatchId(), e);
				}
			}
		}
	}
}
