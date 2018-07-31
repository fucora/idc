package com.iwellmass.idc.lookup;

import java.time.LocalDateTime;

public interface LookupContext {

	public String jobId();
	
	public String jobParameter();
	
	public LocalDateTime loadDate();
	
	public void fireSourceEvent(SourceEvent event);
	
	
}
