package com.iwellmass.idc.scheduler;

import java.util.Objects;

import com.iwellmass.idc.scheduler.service.IDCJobExecutor;

public final class IDCJobExecutors {
	
	private static IDCJobExecutor GLOBAL_EXECUTOR = null;
	
	public static final IDCJobExecutor getExecutor() {
		return GLOBAL_EXECUTOR;
	}
	
	public static final void setGlobalExecutor(IDCJobExecutor executor) {
		Objects.requireNonNull(executor, "Executor cannot be null");
		GLOBAL_EXECUTOR = executor;
	}
}
