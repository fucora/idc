package com.iwellmass.idc.message;

/**
 * 实例事件，用于更新事件状态
 */
public enum JobEvent {

	/**
	 * 开始
	 */
	START,

	/**
	 * 完成
	 */
	FINISH,
	
	/**
	 * 失败
	 */
	FAIL,

	/**
	 * 重跑
	 */
	REDO,

	/**
	 * 取消
	 */
	CANCEL,

	/**
	 * 跳过
	 */
	SKIP,

	// =====================  nodeJob 回调
	/**
	 * 准备真正运行
	 */
	READY,

	/**
	 * 正在运行
	 */
	RUNNING
}
