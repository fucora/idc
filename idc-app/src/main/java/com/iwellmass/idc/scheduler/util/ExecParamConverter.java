package com.iwellmass.idc.scheduler.util;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.iwellmass.common.param.ExecParam;

public class ExecParamConverter extends JsonConverter<List<ExecParam>> {

	public ExecParamConverter() {
		super(new TypeReference<List<ExecParam>>() {
		});
	}
}
