package com.iwellmass.dispatcher.sdk.service;

import com.iwellmass.dispatcher.sdk.util.IBasicExecuteContext;

/**
 * 业务系统添加任务时，只需要实现ITaskService接口即可。
 */
public interface ITaskService {

	/**
	 * 
	 * @param params 任务的执行参数
	 * @param ctx 任务执行上下文，主要用于缓存任务执行状态
	 */	
    public void execute(String params, IBasicExecuteContext ctx);
}
