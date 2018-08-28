package com.iwellmass.idc.simple;

import com.iwellmass.idc.IDCExecutionContext;
import com.iwellmass.idc.IDCJob;

public class SimpleJob implements IDCJob{

	public void execute(IDCExecutionContext context) {
		
		String loadDate = context.getLoadDate();
		
		String taskId = context.getTaskId();
		
		
	}

}
