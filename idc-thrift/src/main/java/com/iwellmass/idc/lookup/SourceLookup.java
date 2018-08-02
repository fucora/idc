package com.iwellmass.idc.lookup;

import java.time.LocalDateTime;

/**
 * 数据源 通知 IDC 调度器是否可以开始调度任务
 */
public interface SourceLookup {

	/**
	 * 执行检测动作
	 */
	public boolean lookup(String jobId, LocalDateTime loadDate);
	
}
