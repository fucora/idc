package com.iwellmass.idc.app.vo.graph;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GraphVO {

	private List<NodeVO> nodes;
	
	private List<EdgeVO> edges;
	
}
