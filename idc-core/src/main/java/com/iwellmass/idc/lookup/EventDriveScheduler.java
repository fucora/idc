package com.iwellmass.idc.lookup;

import java.time.LocalDateTime;

public interface EventDriveScheduler {
	

	/**
	 * @param checkTaskId
	 * @return
	 */
	void scheduleOnSourceReady(Integer jobId, LocalDateTime loadDate);


}
