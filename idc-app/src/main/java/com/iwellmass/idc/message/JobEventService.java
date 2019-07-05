package com.iwellmass.idc.message;

/**
 * Job 事件处理服务，一般情况下串行处理
 */
public interface JobEventService {

	public void send(JobMessage message);

}