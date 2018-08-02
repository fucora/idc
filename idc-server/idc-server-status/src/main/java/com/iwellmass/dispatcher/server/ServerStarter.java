package com.iwellmass.dispatcher.server;

import static org.quartz.CronScheduleBuilder.cronSchedule;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.server.TServer;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.context.JVMContext;
import com.iwellmass.dispatcher.common.context.QuartzContext;
import com.iwellmass.dispatcher.common.dao.DdcServerMapper;
import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.dispatcher.common.model.DdcServer;
import com.iwellmass.dispatcher.common.model.DdcServerExample;
import com.iwellmass.dispatcher.server.task.ClearSubTaskHistory;
import com.iwellmass.dispatcher.server.task.ClearTaskHistory;
import com.iwellmass.dispatcher.server.task.ClearTaskStatus;
import com.iwellmass.dispatcher.server.task.FiveMinuteStatusStatisticTask;
import com.iwellmass.dispatcher.server.task.ForNoDisposeSubTask;
import com.iwellmass.dispatcher.server.task.ForNoDisposeTask;
import com.iwellmass.dispatcher.server.task.ForNoResponseSubTask;
import com.iwellmass.dispatcher.server.task.ForNoResponseTask;
import com.iwellmass.dispatcher.server.task.HourStatusStatisticTask;
import com.iwellmass.dispatcher.server.thread.ServerHeartBeatThread;
import com.iwellmass.dispatcher.server.thread.ServerShutdownHook;
import com.iwellmass.dispatcher.server.thread.ThriftServerThread;
import com.iwellmass.dispatcher.server.thrift.StatusThriftServer;
import com.iwellmass.dispatcher.server.thrift.impl.StatusServerServiceImpl;
import com.iwellmass.dispatcher.server.util.NetUtils;
import com.iwellmass.dispatcher.server.util.PropertyHolder;

public class ServerStarter {
	
	@Autowired
	private DdcServerMapper serverMapper;
	
	@Autowired
	private PropertyHolder propertyHolder;
	
	@Autowired
	private StatusThriftServer thriftService;
	
	@Autowired
	private StatusServerServiceImpl statusServerService;

	private static final String retry_cron = "*/30 * * * * ?";
	
	private static final String clear_cron = "0 */5 * * * ?";

	private static final String statistic_five_minute_cron = "0 0/5 * * * ?";

	private final static Logger logger = LoggerFactory.getLogger(ServerStarter.class);
	
	/**
	 * 状态服务器启动入口函数
	 * @throws DDCException
	 */
	public void start() throws DDCException {
		
		logger.info("初始化基础数据开始......");
		JVMContext.setIp(NetUtils.CURRENT_HOST_IP);
		JVMContext.setPort(thriftService.getThriftPort());
		logger.info("初始化基础数据完成......");
		
		logger.info("初始化Scheduler开始......");
		SchedulerFactory schedulerFactory = null;
		Scheduler scheduler = null;
		try {
			schedulerFactory = new StdSchedulerFactory("quartz.properties");
			scheduler = schedulerFactory.getScheduler();
			QuartzContext.setScheduler(scheduler);
		} catch (SchedulerException e) {
			logger.error("初始化Scheduler出错，错误信息{}", e);
			throw new DDCException("初始化Scheduler出错，错误信息{%s}", e.getMessage());
		} 
		logger.info("初始化Scheduler完成......");
		
		//启动调度服务
		startSchedulerServer(scheduler);
		
		//启动状态服务
		startStatusServer();
		
		try {
			logger.info("启动Scheduler开始......");
			scheduler.start();
			logger.info("启动Scheduler完成......");
		} catch (SchedulerException e) {
			logger.error("启动Scheduler完成出错，错误信息{}", e);
			throw new DDCException("启动Scheduler完成出错，错误信息{%s}", e);
		}

		Runtime.getRuntime().addShutdownHook(new Thread(new ServerShutdownHook(scheduler, statusServerService), "ddc-server-ShutdownHook"));
		logger.info("服务启动完成......");
	}
	
	/**
	 * 启动调度服务
	 * @param scheduler
	 * @throws DDCException
	 */
	private void startSchedulerServer(final Scheduler scheduler) throws DDCException {

	    logger.info("初始化调度服务开始......");
		try {							
			//检查是否存在派发后指定时间未开始执行的任务
			JobKey jobKey = new JobKey(Constants.JOB_NO_DISPOSE_TASK, Constants.DDC_SCHEDULER_GROUP);
			JobDetail jobDetail = JobBuilder.newJob(ForNoDisposeTask.class)
					.withIdentity(jobKey)
					.storeDurably()
					.build();
			//创建Trigger
			Trigger trigger = TriggerBuilder.newTrigger()
					.forJob(jobDetail)
					.withIdentity(Constants.TRIGGER_NO_DISPOSE_TASK, Constants.DDC_SCHEDULER_GROUP)
					.withSchedule(cronSchedule(retry_cron))
					.withPriority(10)
					.build();
			
			if(scheduler.checkExists(jobKey)) {
				scheduler.addJob(jobDetail, true);
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			} else {
				scheduler.scheduleJob(jobDetail, trigger);
			}
			
			//检查是否存在执行后指定时间未回复的任务
			jobKey = new JobKey(Constants.JOB_NO_RESPONSE_TASK, Constants.DDC_SCHEDULER_GROUP);
			jobDetail = JobBuilder.newJob(ForNoResponseTask.class)
						.withIdentity(jobKey)
						.storeDurably()
						.build();
			//创建Trigger
			trigger = TriggerBuilder.newTrigger()
					.forJob(jobDetail)
					.withIdentity(Constants.TRIGGER_NO_RESPONSE_TASK, Constants.DDC_SCHEDULER_GROUP)
					.withSchedule(cronSchedule(retry_cron))
					.withPriority(10)
					.build();
			if(scheduler.checkExists(jobKey)) {
				scheduler.addJob(jobDetail, true);
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			} else {
				scheduler.scheduleJob(jobDetail, trigger);
			}
			
			//检查是否存在派发后指定时间未开始执行的子任务
			jobKey = new JobKey(Constants.JOB_NO_DISPOSE_SUBTASK, Constants.DDC_SCHEDULER_GROUP);
			jobDetail = JobBuilder.newJob(ForNoDisposeSubTask.class)
					.withIdentity(jobKey)
					.storeDurably()
					.build();

			//创建Trigger
			trigger = TriggerBuilder.newTrigger()
					.forJob(jobDetail)
					.withIdentity(Constants.TRIGGER_NO_DISPOSE_SUBTASK, Constants.DDC_SCHEDULER_GROUP)
					.withSchedule(cronSchedule(retry_cron))
					.withPriority(10)
					.build();
			if(scheduler.checkExists(jobKey)) {
				scheduler.addJob(jobDetail, true);
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			} else {
				scheduler.scheduleJob(jobDetail, trigger);
			}
			
			//检查是否存在执行后指定时间未回复的子任务
			jobKey = new JobKey(Constants.JOB_NO_RESPONSE_SUBTASK, Constants.DDC_SCHEDULER_GROUP);
			jobDetail = JobBuilder.newJob(ForNoResponseSubTask.class)
					.withIdentity(jobKey)
					.storeDurably()
					.build();
			//创建Trigger
			trigger = TriggerBuilder.newTrigger()
					.forJob(jobDetail)
					.withIdentity(Constants.TRIGGER_NO_RESPONSE_SUBTASK, Constants.DDC_SCHEDULER_GROUP)
					.withSchedule(cronSchedule(retry_cron))
					.withPriority(10)
					.build();
			if(scheduler.checkExists(jobKey)) {
				scheduler.addJob(jobDetail, true);
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			} else {
				scheduler.scheduleJob(jobDetail, trigger);
			}
			
			//清理N天前的任务执行历史记录
			jobKey = new JobKey(Constants.JOB_CLEAR_TASK, Constants.DDC_SCHEDULER_GROUP);
			jobDetail = JobBuilder.newJob(ClearTaskHistory.class)
					.withIdentity(jobKey)
					.storeDurably()
					.build();
			//创建Trigger
			trigger = TriggerBuilder.newTrigger()
					.forJob(jobDetail)
					.withIdentity(Constants.TRIGGER_CLEAR_TASK, Constants.DDC_SCHEDULER_GROUP)
					.withSchedule(cronSchedule(clear_cron))
					.withPriority(1)
					.build();
			if(scheduler.checkExists(jobKey)) {
				scheduler.addJob(jobDetail, true);
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			} else {
				scheduler.scheduleJob(jobDetail, trigger);
			}
			
			//清理N天前的子任务执行历史记录
			jobKey = new JobKey(Constants.JOB_CLEAR_SUBTASK, Constants.DDC_SCHEDULER_GROUP);
			jobDetail = JobBuilder.newJob(ClearSubTaskHistory.class)
					.withIdentity(jobKey)
					.storeDurably()
					.build();
			//创建Trigger
			trigger = TriggerBuilder.newTrigger()
					.forJob(jobDetail)
					.withIdentity(Constants.TRIGGER_CLEAR_SUBTASK, Constants.DDC_SCHEDULER_GROUP)
					.withSchedule(cronSchedule(clear_cron))
					.withPriority(1)
					.build();
			if(scheduler.checkExists(jobKey)) {
				scheduler.addJob(jobDetail, true);
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			} else {
				scheduler.scheduleJob(jobDetail, trigger);
			}
			
			//清理N天前的任务执行状态记录
			jobKey = new JobKey(Constants.JOB_CLEAR_STATUS, Constants.DDC_SCHEDULER_GROUP);
			jobDetail = JobBuilder.newJob(ClearTaskStatus.class)
					.withIdentity(jobKey)
					.storeDurably()
					.build();
			//创建Trigger
			trigger = TriggerBuilder.newTrigger()
					.forJob(jobDetail)
					.withIdentity(Constants.TRIGGER_CLEAR_STATUS, Constants.DDC_SCHEDULER_GROUP)
					.withSchedule(cronSchedule(clear_cron))
					.withPriority(1)
					.build();
			if(scheduler.checkExists(jobKey)) {
				scheduler.addJob(jobDetail, true);
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			} else {
				scheduler.scheduleJob(jobDetail, trigger);
			}

			//5分钟任务执行情况汇总
			jobKey = new JobKey(Constants.TASK_FIVE_MINUTE_STATISTIC, Constants.DDC_SCHEDULER_GROUP);
			jobDetail = JobBuilder.newJob(FiveMinuteStatusStatisticTask.class)
					.withIdentity(jobKey)
					.storeDurably()
					.build();
			//创建Trigger
			trigger = TriggerBuilder.newTrigger()
					.forJob(jobDetail)
					.withIdentity(Constants.TASK_FIVE_MINUTE_STATISTIC, Constants.DDC_SCHEDULER_GROUP)
					.withSchedule(cronSchedule(statistic_five_minute_cron))
					.withPriority(1)
					.build();
			if(scheduler.checkExists(jobKey)) {
				scheduler.addJob(jobDetail, true);
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			} else {
				scheduler.scheduleJob(jobDetail, trigger);
			}

			//1小时任务执行情况汇总
			jobKey = new JobKey(Constants.TASK_HOUR_STATISTIC, Constants.DDC_SCHEDULER_GROUP);
			jobDetail = JobBuilder.newJob(HourStatusStatisticTask.class)
						.withIdentity(jobKey)
						.storeDurably()
						.build();
			//创建Trigger
			trigger = TriggerBuilder.newTrigger()
					.forJob(jobDetail)
					.withIdentity(Constants.TASK_HOUR_STATISTIC, Constants.DDC_SCHEDULER_GROUP)
					.withSchedule(cronSchedule(propertyHolder.getHourlyStatisticCron()))
					.withPriority(1)
					.build();
			if(scheduler.checkExists(jobKey)) {
				scheduler.addJob(jobDetail, true);
				scheduler.rescheduleJob(trigger.getKey(), trigger);
			} else {
				scheduler.scheduleJob(jobDetail, trigger);
			}

			logger.info("初始化调度服务完成......");
		} catch(Throwable e) {
			logger.error("初始化调度服务出错，错误信息{}", e);
			throw new DDCException("初始化调度服务出错，错误信息{%s}", e.getMessage());
		}
	
	}
	
	/**
	 * 启动状态服务
	 * @throws DDCException
	 */
	private void startStatusServer() throws DDCException {
		
		logger.info("启动状态服务器开始......");
		int thriftPort = thriftService.getThriftPort();
		final TServer server = thriftService.initThriftServer();
		if(server == null) {
			logger.error("DDC-注册状态服务器Thrift服务失败！");
			throw new DDCException("DDC-注册状态服务器Thrift服务失败！");
		}	
		
		final ThriftServerThread thriftServiceThread = new ThriftServerThread(server);
		new Thread(thriftServiceThread, "DDC-ThriftServerThread").start();
		
		if(!thriftServiceThread.isServerServing()) {
			logger.error("DDC-启动Thrift服务失败！");
			throw new DDCException("DDC-启动Thrift服务失败！");
		}
		logger.info("Thrift服务启动完成，端口{}", thriftPort);
		
		DdcServerExample example = new DdcServerExample();
		DdcServerExample.Criteria criteria = example.createCriteria();
		criteria.andIpEqualTo(NetUtils.CURRENT_HOST_IP).andPortEqualTo(thriftPort);
		
		int serverId = 0;
		List<DdcServer> serverList = serverMapper.selectByExample(example);
		Date now = new Date();
		if(serverList == null || serverList.isEmpty()) {			
			DdcServer record = new DdcServer();
			record.setIp(NetUtils.CURRENT_HOST_IP);
			record.setPort(thriftPort);
			record.setLastStartTime(now);
			record.setLastHbTime(now);
			record.setStatus(Constants.ENABLED);
			serverMapper.insertSelective(record);
			serverId = record.getId();
		} else {
			serverId = serverList.get(0).getId();
			DdcServer record = new DdcServer();
			record.setId(serverId);
			record.setLastStartTime(now);
			record.setLastHbTime(now);
			serverMapper.updateByPrimaryKeySelective(record);
		}
				
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, new ThreadFactory(){

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "DDC-ScheduledThreadPool");
				return t;
			}});
		
		ServerHeartBeatThread serverHeartBeatThread = new ServerHeartBeatThread(serverId, serverMapper);
		scheduledExecutorService.scheduleAtFixedRate(serverHeartBeatThread, 5, 5, TimeUnit.SECONDS);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){

			@Override
			public void run() {
				scheduledExecutorService.shutdown();
			}
		}, "ddc-server-ShutdownHook"));
		
		logger.info("启动状态服务器完成......");
	}
}
