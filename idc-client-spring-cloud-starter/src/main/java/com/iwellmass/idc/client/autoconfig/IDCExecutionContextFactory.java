package com.iwellmass.idc.client.autoconfig;

import javax.inject.Inject;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.executor.IDCJob;
import com.iwellmass.idc.executor.IDCJobExecutionContext;
import com.iwellmass.idc.executor.IDCStatusService;
import com.iwellmass.idc.model.JobInstance;

public class IDCExecutionContextFactory  {
	
	@Inject
	private IDCStatusService statusService;
	
	
	public IDCJobExecutionContext newContext(JobInstance instance, IDCJob job) {
		ExecutionContextImpl context = new ExecutionContextImpl();
		context.idcJob = job;
		context.instance = instance;
		return context;
	}
	
	class ExecutionContextImpl implements IDCJobExecutionContext {
		
		private IDCJob idcJob;
		private JobInstance instance;
		
		@Override
		public IDCJob getIDCJob() {
			return this.idcJob;
		}

		@Override
		public Integer getInstanceId() {
			return instance.getInstanceId();
		}

		@Override
		public void complete(CompleteEvent event) {
			statusService.fireCompleteEvent(event);
		}
	}
	
	
}
