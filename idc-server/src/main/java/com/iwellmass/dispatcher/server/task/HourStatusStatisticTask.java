package com.iwellmass.dispatcher.server.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.common.context.SpringContext;
import com.iwellmass.dispatcher.common.dao.DdcHourExecuteStatisticMapper;
import com.iwellmass.dispatcher.common.model.DdcHourExecuteStatistic;

/**
 * Created by xkwu on 2016/7/6.
 */
public class HourStatusStatisticTask implements Job {
	
	private static final int STEP = 200;
	
    private Logger logger = LoggerFactory.getLogger(HourStatusStatisticTask.class);
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        
    	DdcHourExecuteStatisticMapper hourExecuteStatisticMapper = SpringContext.getApplicationContext().getBean(DdcHourExecuteStatisticMapper.class);
    	
        Calendar cal = Calendar.getInstance();
        cal.setTime(context.getScheduledFireTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String endTime = dateFormat.format(cal);
        cal.set(Calendar.MINUTE, 0);
        cal.add(Calendar.HOUR, -1);
        String startTime = dateFormat.format(cal);
        
        logger.info("1小时任务执行情况汇总,时间段[{}-{}],begin.", startTime, endTime);
        List<DdcHourExecuteStatistic> historyList = hourExecuteStatisticMapper.aggregateHistory(startTime,endTime);
        for (int i = 0; i < historyList.size(); i += STEP) {
            hourExecuteStatisticMapper.insertList(historyList.subList(i, i + STEP > historyList.size() ? historyList.size() : i + STEP));
        }
        
        logger.info("1小时任务执行情况汇总,时间段[{}-{}],end.", startTime, endTime);
    }
}
