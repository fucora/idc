package com.iwellmass.idc.scheduler.util;

import java.util.List;

import com.iwellmass.common.param.ExecParam;

public class ExecParamConverter extends JsonConverter<List<ExecParam>> {

	public ExecParamConverter() {
		super(mapper.getTypeFactory().constructCollectionLikeType(List.class, ExecParam.class));
	}
}
