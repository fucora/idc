package com.iwellmass.dispatcher.sdk.util;

/**
 * 基本状态操作接口
 * 提供给用户供增加自定义状态
 * @author Ming.Li
 *
 */
public interface IExecuteContext {

	/**
	 * 添加任务自定义状态，业务方自定义执行状态通过此方法添加
	 * 
	 * 	<p>代码示例:
	 *  <pre>
	 *  public void execute(String params, ExecuteContext ctx) {
	 *  	..........
	 *  	ctx.addCustomStatus("FETCH_DATA_FINISHED", "获取数据完成");
	 *  	..........
	 *  }
	 * @param status
	 * @param message
	 * 
	 */
	public void addCustomStatus(String status, String message);
	
	/**
	 * 获取本次执行编号
	 * 任务每次运行都对应独立的执行编号，若因调度端出超时等异常时会再次下发任务，此参数可用于任务去重判断
	 * @return 执行编号
	 */
	public long getExecuteId();
	
	/**
	 * 获取任务执行批次编号，格式为yyyyMMddHHmmss
	 * 批次编号对应cron的一次触发，若发生失败重试等操作后批次编号依然不变
	 * @return 执行批次编号
	 */
	
	public String getExecuteBatchId();
	
	/**
	 * 获取任务并发执行的线程数
	 * @return
	 */
	public int getThreadCount();
	
	/**
	 * 获取任务的应触发时间
	 * @return
	 */
	public long getFireTime();
	
	/**
	 * 获取任务执行参数
	 * @return
	 */
	public String getParameters();
	
	/**
	 * 添加自定义参数
	 * @param key
	 * @param value
	 */
	public void putCustomParam(Object key, Object value);
	
	/**
	 * 获取自定义参数
	 * @param key
	 * @return
	 */
	public Object getCustomParam(Object key);

}
