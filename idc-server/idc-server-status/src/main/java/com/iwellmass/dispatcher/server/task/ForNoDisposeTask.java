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

import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.context.QuartzContext;
import com.iwellmass.dispatcher.common.context.SpringContext;
import com.iwellmass.dispatcher.common.dao.DdcRunningTaskMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskExecuteStatusMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskMapper;
import com.iwellmass.dispatcher.common.model.DdcRunningTask;
import com.iwellmass.dispatcher.common.model.DdcRunningTaskExample;
import com.iwellmass.dispatcher.common.model.DdcTask;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteHistory;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteStatus;
import com.iwellmass.dispatcher.common.task.DmallTask;
import com.iwellmass.dispatcher.common.utils.AlarmUtils;
import com.iwellmass.dispatcher.thrift.bvo.TaskStatus;

/**
 * 未开始任务重新执行
 * 
 * 条件：调度中心任务派发成功，但在3个heartbeat（一个heartbeat5秒钟）后都未收到任务接收时间
 * 在任务开始执行时会上报receive_time和start_time，5秒钟一次的heartbeat也会上报状态，3个heartbeat未收到receive_time证明任务因为重启等问题被丢失
 * 
 * @author duheng
 *
 */
@DisallowConcurrentExecution
public class ForNoDisposeTask implements Job {
	
	private Logger logger = LoggerFactory.getLogger(ForNoDisposeTask.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		DdcTaskExecuteHistoryMapper ddcTaskExecuteHistoryMapper = SpringContext.getApplicationContext().getBean(DdcTaskExecuteHistoryMapper.class);
		DdcRunningTaskMapper ddcRunningTaskMapper = SpringContext.getApplicationContext().getBean(DdcRunningTaskMapper.class);
		DdcTaskExecuteStatusMapper ddcTaskExecuteStatusMapper = SpringContext.getApplicationContext().getBean(DdcTaskExecuteStatusMapper.class);
		DdcTaskMapper ddcTaskMapper = SpringContext.getApplicationContext().getBean(DdcTaskMapper.class);
		
		Date threeHbAgo = new Date(System.currentTimeMillis() - Constants.THREE_NODE_HEARTBEAT);
		DdcRunningTaskExample example = new DdcRunningTaskExample();
		DdcRunningTaskExample.Criteria criteria = example.createCriteria();
		criteria.andDispatchTimeIsNotNull().andDispatchTimeLessThan(threeHbAgo).andReceiveTimeIsNull().andCompleteTimeIsNull()
			.andWorkflowExecuteIdEqualTo(0L).andWorkflowIdEqualTo(0);
		
		List<DdcRunningTask> results = ddcRunningTaskMapper.selectByExample(example);
		logger.info("检查应用未成功接收定时任务开始运行，发现下发成功但接收失败的定时任务个数：{}", results.size());
		
		if(results != null && !results.isEmpty()) {
			Scheduler scheduler = QuartzContext.getScheduler();
			for(DdcRunningTask result : results) {
				try {
					String remarks = "";
					if(result.getDispatchCount() <= Constants.TIMEOUT_RETRY) {
						JobDetail jobDetail = JobBuilder.newJob(DmallTask.class)
								.withIdentity(Constants.JOB_NO_DISPOSE_TASK + "_" + result.getExecuteId(), Constants.DDC_SCHEDULER_GROUP)
								.usingJobData("taskId", result.getTaskId())
								.usingJobData("executeId", result.getExecuteId())
								.usingJobData("executeBatchId", result.getExecuteBatchId())
								.usingJobData("currentRetryTime", 0)
								.usingJobData("triggerType", Constants.TASK_TRIGGER_TYPE_SYSTEM)
								.build();
						
						Trigger trigger = TriggerBuilder.newTrigger()
								.forJob(jobDetail)
								.withIdentity(Constants.TRIGGER_NO_DISPOSE_TASK + "_" + + result.getExecuteId(), Constants.DDC_SCHEDULER_GROUP)
								.startNow()
								.withSchedule(simpleSchedule().withRepeatCount(0))
								.build();
						
						scheduler.scheduleJob(jobDetail, trigger);
						
						if(result.getDispatchCount() == 1) {
							remarks = "任务下发成功，但应用未成功接收，将再次下发！";
						} else {
							remarks = "任务下发成功，但应用未成功接收，当前下发次数：" + result.getDispatchCount() + "，系统最大下发次数：" + Constants.TIMEOUT_RETRY + "！";
						}
					} else {
						DdcTaskExecuteHistory ddcTaskExecuteHistory = new DdcTaskExecuteHistory();
						ddcTaskExecuteHistory.setExecuteId(result.getExecuteId());
						ddcTaskExecuteHistory.setCompleteTime(new Date());
						ddcTaskExecuteHistory.setExecuteResult(TaskStatus.EXECUTE_TIMEOUT);
						ddcTaskExecuteHistory.setExecuteResultDesc("任务下发成功但未开始执行，重试达到系统最大重试次数，本次执行被系统结束！");
						ddcTaskExecuteHistoryMapper.updateByPrimaryKeySelective(ddcTaskExecuteHistory);
						
						logger.error("重试【已下发未开始执行定时任务】达到系统最大重试次数，该执行记录将被系统结束！任务编号：{},执行编号：{}, 执行批次编号：{}, 系统重试次数：{}",result.getTaskId(), result.getExecuteId(), result.getExecuteBatchId(), Constants.TIMEOUT_RETRY);
						
						DdcTask ddcTask = ddcTaskMapper.selectByPrimaryKey(result.getTaskId());
						remarks = "任务：" + (ddcTask != null ? ddcTask.getTaskName() : result.getTaskId()) + "下发成功，但应用未成功接收，达到系统最大下发次数：" + Constants.TIMEOUT_RETRY + "！";
						
						AlarmUtils.sendAndRecordAlarm(Constants.ALARM_KEY_TIMEOUT, result.getTaskId(), remarks);
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
					logger.error("重试【已下发未开始执行定时任务】出错！任务编号：{}, 执行编号：{}, 执行批次编号：{}, 错误信息：{}",result.getTaskId(), result.getExecuteId(), result.getExecuteBatchId(), e);
				}
			}
		}
	}
}
