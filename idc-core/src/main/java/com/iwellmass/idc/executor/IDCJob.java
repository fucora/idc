package com.iwellmass.idc.executor;

import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.model.JobLog;

/**
 * 处理 {@link IDCJob#getContentType()} 类型的任务
 */
public interface IDCJob {
	
	// ~~ META DATA ~~
	String getContentType();
	
	// ~~ 业务逻辑 ~~
	void execute(IDCJobExecutionContext context);

	// ~~ 获取实例运行日志 ~~
    PageData<JobLog> getRuntimeLog(Integer instanceId, Pager pager);
}