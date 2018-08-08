package com.iwellmass.dispatcher.common.task;

import org.quartz.DisallowConcurrentExecution;

@DisallowConcurrentExecution
public class DmallTaskDisallowConcurrent extends DmallTask {
	
	public DmallTaskDisallowConcurrent() {
		System.out.println("debug");
	}
}
