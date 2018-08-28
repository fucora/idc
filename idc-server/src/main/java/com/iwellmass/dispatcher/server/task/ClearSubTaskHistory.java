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
import com.iwellmass.dispatcher.common.dao.DdcSubtaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.common.model.DdcSubtaskExecuteHistoryExample;

/**
 * 清理n天前的子任务执行记录
 * @author duheng
 *
 */
@DisallowConcurrentExecution
public class ClearSubTaskHistory implements Job {

	private Logger logger = LoggerFactory.getLogger(ClearSubTaskHistory.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		DdcSubtaskExecuteHistoryMapper ddcSubtaskExecuteHistoryMapper = SpringContext.getApplicationContext().getBean(DdcSubtaskExecuteHistoryMapper.class);
		
		Date sevenDays = new Date(System.currentTimeMillis() - Constants.SEVEN_DAY);
		DdcSubtaskExecuteHistoryExample example = new DdcSubtaskExecuteHistoryExample();
		example.createCriteria().andCompleteTimeIsNotNull().andCompleteTimeLessThan(sevenDays);

		int results = ddcSubtaskExecuteHistoryMapper.deleteByExample(example);
		logger.info(String.format("删除%s表%tF %tT之前的%d条记录", "DDC_SUBTASK_EXECUTE_HISTORY", sevenDays, sevenDays, results));
	}
}
