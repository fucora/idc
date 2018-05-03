package com.iwellmass.dispatcher.server.task;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.time.FastDateFormat;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.common.context.SpringContext;
import com.iwellmass.dispatcher.common.dao.DdcFiveMinuteExecuteStatisticMapper;
import com.iwellmass.dispatcher.common.model.DdcFiveMinuteExecuteStatistic;
import com.iwellmass.dispatcher.server.util.PropertyHolder;

/**
 * Created by xkwu on 2016/6/29.
 */
public class FiveMinuteStatusStatisticTask implements Job {
	
	private static final int STEP = 200;

    private Logger logger = LoggerFactory.getLogger(FiveMinuteStatusStatisticTask.class);
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

    	DdcFiveMinuteExecuteStatisticMapper fiveMinuteExecuteStatisticMapper = SpringContext.getApplicationContext().getBean(DdcFiveMinuteExecuteStatisticMapper.class);
    	PropertyHolder props = SpringContext.getApplicationContext().getBean(PropertyHolder.class);
        int delay = props.getFiveMinutesDelay();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(context.getScheduledFireTime());
        //延迟汇聚操作
        cal.add(Calendar.MINUTE,-delay);

        FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
        String endTime = dateFormat.format(cal);
        cal.add(Calendar.MINUTE, -5);
        String startTime = dateFormat.format(cal);
        logger.info("5分钟任务执行情况汇总,时间段[{}-{}],begin.", startTime, endTime);
        List<DdcFiveMinuteExecuteStatistic> historyList = fiveMinuteExecuteStatisticMapper.aggregateHistory(startTime, endTime);
        for (int i = 0; i < historyList.size(); i += STEP) {
            fiveMinuteExecuteStatisticMapper.insertList(historyList.subList(i, i + STEP > historyList.size() ? historyList.size() : i + STEP));
        }

        List<DdcFiveMinuteExecuteStatistic> subHistoryList = fiveMinuteExecuteStatisticMapper.aggregateSubHistory(startTime, endTime);
        for (int i = 0; i < subHistoryList.size(); i += STEP) {
            fiveMinuteExecuteStatisticMapper.insertList(subHistoryList.subList(i, i + STEP > subHistoryList.size() ? subHistoryList.size() : i + STEP));
        }

        logger.info("5分钟任务执行情况汇总,时间段[{}-{}],end.", startTime, endTime);
    }


    public static final void main(String[] args){
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream("classpath:server.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Properties p = new Properties();
        try {
            p.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
