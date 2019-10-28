package com.iwellmass.idc.app.vo.graph;

import lombok.*;

@Getter
@Setter
@ToString(of = {"id", "source", "target"})
@NoArgsConstructor
@AllArgsConstructor
public class EdgeVO {

	private String id;
	
	private SourceVO source;
	
	private TargetVO target;
	
}
