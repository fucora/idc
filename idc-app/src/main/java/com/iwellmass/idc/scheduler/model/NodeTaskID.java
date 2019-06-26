package com.iwellmass.idc.scheduler.model;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class NodeTaskID implements Serializable{

	private static final long serialVersionUID = 1300186826817591465L;

	private String pid;
	
	private String id;

}
