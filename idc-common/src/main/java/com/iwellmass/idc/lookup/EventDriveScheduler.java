package com.iwellmass.idc.lookup;

import java.time.LocalDateTime;

import com.iwellmass.dispatcher.common.model.DdcNode;

public interface EventDriveScheduler {
	
	public void register(DdcNode nodeInfo);

	/**
	 * @param checkTaskId
	 * @return
	 */
	void scheduleOnSourceReady(Integer jobId, LocalDateTime loadDate);


}
