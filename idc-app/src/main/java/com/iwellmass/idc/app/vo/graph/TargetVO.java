package com.iwellmass.idc.app.vo.graph;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetVO {

	private String id;

	public TargetVO() {
	}

	public TargetVO(String id) {
		super();
		this.id = id;
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}
}
