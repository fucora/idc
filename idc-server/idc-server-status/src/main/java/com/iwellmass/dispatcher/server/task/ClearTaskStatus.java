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
import com.iwellmass.dispatcher.common.dao.DdcTaskExecuteStatusMapper;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteStatusExample;

/**
 * 清理n天前的任务状态记录
 * @author duheng
 *
 */
@DisallowConcurrentExecution
public class ClearTaskStatus implements Job {

	private Logger logger = LoggerFactory.getLogger(ClearTaskStatus.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		DdcTaskExecuteStatusMapper ddcTaskExecuteStatusMapper = SpringContext.getApplicationContext().getBean(DdcTaskExecuteStatusMapper.class);
		
		Date sevenDays = new Date(System.currentTimeMillis() - Constants.SEVEN_DAY);
		DdcTaskExecuteStatusExample example = new DdcTaskExecuteStatusExample();
		DdcTaskExecuteStatusExample.Criteria criteria = example.createCriteria();
		criteria.andTimestampLessThan(sevenDays);
		
		int results = ddcTaskExecuteStatusMapper.deleteByExample(example);
		logger.info(String.format("删除%s表%tF %tT之前的%d条记录", "DDC_TASK_EXECUTE_STATUS", sevenDays, sevenDays, results));	}
}
