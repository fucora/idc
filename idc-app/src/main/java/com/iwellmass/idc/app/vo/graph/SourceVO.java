package com.iwellmass.idc.app.vo.graph;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceVO {

	private String id;
	
	public SourceVO() {
	}
	
	public SourceVO(String id) {
		super();
		this.id = id;
	}
	
	@Override
	public String toString() {
		return String.valueOf(id);
	}

}
