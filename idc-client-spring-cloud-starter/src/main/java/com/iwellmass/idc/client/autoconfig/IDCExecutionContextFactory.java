package com.iwellmass.idc.client.autoconfig;

import javax.inject.Inject;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCJobExecutionContext;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.model.JobInstance;

public class IDCExecutionContextFactory  {
	
	@Inject
	private IDCStatusService statusService;
	
	public IDCJobExecutionContext newContext(JobInstance instance) {
		ExecutionContextImpl context = new ExecutionContextImpl();
		context.instance = instance;
		return context;
	}
	
	class ExecutionContextImpl implements IDCJobExecutionContext {
		
		private JobInstance instance;

		@Override
		public JobInstance getInstance() {
			return instance;
		}

		@Override
		public void complete(CompleteEvent event) {
			statusService.fireCompleteEvent(event);
		}
	}
	
	
}
