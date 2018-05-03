package com.iwellmass.dispatcher.sdk.task;

import com.iwellmass.dispatcher.sdk.util.IExecuteContext;

/**
 * 简单任务接口
 * @author Ming.Li
 *
 */
interface ISimpleTaskService {

	/**
	 * 任务处理具体方法
	 * @param params
	 * @param ctx
	 * @return
	 */
    public boolean executeTask(String params, IExecuteContext ctx);
}
