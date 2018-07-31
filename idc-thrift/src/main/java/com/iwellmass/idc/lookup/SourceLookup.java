package com.iwellmass.idc.lookup;

/**
 * 数据源 通知 IDC 调度器是否可以开始调度任务
 */
public interface SourceLookup {

	/**
	 * 是否需要停止检测
	 */
	public boolean isHalt();

	/**
	 * 检测间隔，单位毫秒
	 */
	public long getInterval();

	/**
	 * 执行检测动作
	 */
	public void lookup(LookupContext context);
	
}
