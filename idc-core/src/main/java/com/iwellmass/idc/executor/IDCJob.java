package com.iwellmass.idc.executor;

/**
 * 处理 {@link IDCJob#getContentType()} 类型的任务
 */
public interface IDCJob {
	
	// ~~ META DATA ~~
	public String getContentType();
	
	// ~~ 业务逻辑 ~~
	public void execute(IDCJobExecutionContext context);
	
}