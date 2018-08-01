package com.iwellmass.dispatcher.server;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.iwellmass.dispatcher.common.DDCContext;
import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.context.SpringContext;
import com.iwellmass.dispatcher.common.dao.DdcTaskMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskUpdateHistoryMapper;
import com.iwellmass.dispatcher.common.model.DdcTask;
import com.iwellmass.dispatcher.common.model.DdcTaskExample;
import com.iwellmass.dispatcher.common.model.DdcTaskUpdateHistory;
import com.iwellmass.idc.lookup.LookupContextImpl;
import com.iwellmass.idc.lookup.SourceEvent;
import com.iwellmass.idc.lookup.SourceLookup;
import com.iwellmass.idc.lookup.SourceLookupManager;

public class SourceLookupManagerImpl implements SourceLookupManager{

	private static final Logger LOGGER = LoggerFactory.getLogger(SourceLookupManagerImpl.class);

	private ScheduledExecutorService schExecutor = Executors.newSingleThreadScheduledExecutor();
	
	
	public void start() {
		
		
		
	}

	public void schedule(SourceLookup sourceLookup) {
		LookupTask task = new LookupTask();
		task.lookup = sourceLookup;
		schedule0(task);
	}
	
	private void schedule0(LookupTask task) {
		schExecutor.schedule(task, task.lookup.getInterval(), TimeUnit.MILLISECONDS);
	}

	class LookupTask implements Runnable {

		private SourceLookup lookup;

		@Override
		public void run() {
			// 停止检测
			if( lookup.isHalt()) {
				LOGGER.info("停止检测进程 {} ", lookup);
			}
			try {
				
				LookupContextImpl ctx = new LookupContextImpl();
				
				// TODO 初始化 context
				lookup.lookup(ctx);
				SourceEvent event = null;
				fireSourceEvent(event);
			} catch (Throwable e) {
				LOGGER.error("检测失败, ERROR: {}", e.getMessage(), e);
			} finally {
				if (!lookup.isHalt()) {
					schedule0(this);
				}
			}
		}
	}

	@Override
	public boolean isSourceReady(int checkTaskId) {
		return false;
	}

	public void fireSourceEvent(SourceEvent event) {
		
		ApplicationContext ctx = SpringContext.getApplicationContext();
		
		Scheduler scheduler = ctx.getBean(Scheduler.class);
		DdcTaskUpdateHistoryMapper taskUpdateMapper = ctx.getBean(DdcTaskUpdateHistoryMapper.class);

		int taskId = Integer.parseInt(event.getJobId());
		
        DdcTask task = selectByTaskIdAndAppId(DDCContext.DEFAULT_APP, taskId);

        JobDataMap map = new JobDataMap();
        JobKey jobKey = buildJobKey(task);
        map.put("triggerType", Constants.TASK_EXECUTE_TYPE_MANUAL);
        map.put("user", "admin");

        try {
            scheduler.triggerJob(jobKey, map);
        } catch (SchedulerException e) {
            LOGGER.error("任务失败，任务名称{}，错误信息{}", task.getTaskName(), e);
        }
        
        try{
        	DdcTaskUpdateHistory record = new DdcTaskUpdateHistory();
            String user = "admin";
        	record.setTaskId(taskId);
        	record.setUpdateUser(user);
        	record.setUpdateTime(new Date());
        	record.setUpdateDetail(String.format("{%s} 执行任务！", "SourceLookup"));
        	taskUpdateMapper.insertSelective(record);
        } catch(Throwable e) {
        	LOGGER.error("插入任务执行记录失败，错误信息：{}", e);
        }
	}
	

    private JobKey buildJobKey(DdcTask task) {

        JobKey jobKey = new JobKey(Constants.JOB_PREFIX + task.getTaskId(), task.getAppKey());
        return jobKey;
    }
    private DdcTask selectByTaskIdAndAppId(int appId, int taskId) {
    	
    	DdcTaskMapper taskMapper = SpringContext.getApplicationContext().getBean(DdcTaskMapper.class);
    	
        DdcTaskExample taskExample = new DdcTaskExample();
        DdcTaskExample.Criteria taskCriteria = taskExample.createCriteria();
        taskCriteria.andAppIdEqualTo(appId);
        taskCriteria.andTaskIdEqualTo(taskId);
        List<DdcTask> ddcTaskList = taskMapper.selectByExample(taskExample);
        if (ddcTaskList == null || ddcTaskList.size() != 1) {
            throw new RuntimeException("查询task数据异常");
        }
        return ddcTaskList.get(0);
    }
}
