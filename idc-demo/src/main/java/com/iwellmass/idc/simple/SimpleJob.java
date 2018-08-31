package com.iwellmass.idc.simple;

import com.iwellmass.idc.executor.IDCExecutionContext;
import com.iwellmass.idc.executor.IDCJob;

public class SimpleJob implements IDCJob{

	@Override
	public void execute(IDCExecutionContext context) {
		System.out.println("SimpleJob worked.");
		
	}
}
