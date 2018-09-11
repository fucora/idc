package com.iwellmass.idc.executor;

import java.io.Serializable;

public interface IDCJobEvent extends Serializable{

	public Integer getInstanceId();
	
	public String getMessage();
	
}
