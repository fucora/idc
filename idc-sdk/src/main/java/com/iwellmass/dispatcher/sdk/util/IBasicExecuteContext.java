package com.iwellmass.dispatcher.sdk.util;

public interface IBasicExecuteContext extends IExecuteContext {
	
	/**
	 * 添加任务执行成功状态，任务执行成功时调用此方法
	 * 	<p>代码示例:
	 *  <pre>
	 *  public void execute(String params, ExecuteContext ctx) {
	 *  	try{
	 *  		..............
	 *  		ctx.executeSucceed();
	 *  	} catch(Exception) {
	 *  	}
	 *  }
	 *  
	 *  </pre>
	 */
	public void executeSucceed();
	
	/**
	 * 添加任务执行成功状态，任务执行成功时调用此方法
	 * 	<p>代码示例:
	 *  <pre>
	 *  public void execute(String params, ExecuteContext ctx) {
	 *  	try{
	 *  		..............
	 *  		ctx.executeSucceed("执行成功，处理数据100条！");
	 *  	} catch(Exception) {
	 *  	}
	 *  }
	 *  
	 *  </pre>
	 */
	public void executeSucceed(String message);
	
	/**
	 * 添加任务执行失败状态，任务执行失败时调用此方法
	 * 	<p>代码示例:
	 *  <pre>
	 *  public void execute(String params, ExecuteContext ctx) {
	 *  	try{
	 *  		............
	 *  	} catch(Exception e) {
	 *  		ctx.executeFailed();
	 *  	}
	 *  }
	 *  </pre>
	 */
	public void executeFailed();
	
	/**
	 * 添加任务执行失败状态，任务执行失败时调用此方法
	 
	 * 	<p>代码示例:
	 *  <pre>
	 *  ctx.executeFailed("获取数据失败！");
	 *  </pre>
	 *  
	 *  @param message
	 */
	public void executeFailed(String message);

}
