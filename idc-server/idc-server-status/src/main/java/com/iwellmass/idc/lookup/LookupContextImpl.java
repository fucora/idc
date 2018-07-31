package com.iwellmass.idc.lookup;

import java.time.LocalDateTime;

public class LookupContextImpl implements LookupContext {

	@Override
	public String jobId() {
		return null;
	}

	@Override
	public String jobParameter() {
		return null;
	}

	@Override
	public LocalDateTime loadDate() {
		return null;
	}

	@Override
	public void fireSourceEvent(SourceEvent event) {
		System.out.println(event);
	}

}
