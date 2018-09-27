package com.iwellmass.idc.app.model;

import com.iwellmass.idc.model.JobPK;

public class LockRequest extends JobPK{
	
	private static final long serialVersionUID = 2505317470899394851L;
	
	private boolean forceLock;

	public boolean isForceLock() {
		return forceLock;
	}

	public void setForceLock(boolean forceLock) {
		this.forceLock = forceLock;
	}
	
}
