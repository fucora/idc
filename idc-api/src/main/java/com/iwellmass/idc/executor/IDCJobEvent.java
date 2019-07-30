package com.iwellmass.idc.executor;

import java.io.Serializable;

public interface IDCJobEvent extends Serializable{

	String getInstanceId();
	
	String getMessage();
	
}
