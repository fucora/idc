package com.iwellmass.idc.app.vo.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(of = {"id", "source", "target"})
public class EdgeVO {

	private String id;
	
	private SourceVO source;
	
	private TargetVO target;
	
}
