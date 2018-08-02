package com.iwellmass.dispatcher.server.task;

import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.context.SpringContext;
import com.iwellmass.dispatcher.common.dao.DdcTaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteHistoryExample;

/**
 * 清理n天前的任务执行记录
 * @author duheng
 *
 */
@DisallowConcurrentExecution
public class ClearTaskHistory implements Job {

	private Logger logger = LoggerFactory.getLogger(ClearTaskHistory.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		DdcTaskExecuteHistoryMapper ddcTaskExecuteHistoryMapper = SpringContext.getApplicationContext().getBean(DdcTaskExecuteHistoryMapper.class);
		
		Date sevenDays = new Date(System.currentTimeMillis() - Constants.SEVEN_DAY);
		DdcTaskExecuteHistoryExample example = new DdcTaskExecuteHistoryExample();
		example.createCriteria().andCompleteTimeIsNotNull().andCompleteTimeLessThan(sevenDays);
		
		int results = ddcTaskExecuteHistoryMapper.deleteByExample(example);		
		logger.info(String.format("删除%s表%tF %tT之前的%d条记录", "DDC_TASK_EXECUTE_HISTORY", sevenDays, sevenDays, results));
	}
}
