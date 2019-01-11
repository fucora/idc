package com.iwellmass.idc.jpa;

import java.util.List;

import javax.persistence.AttributeConverter;

import com.alibaba.fastjson.JSON;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.common.util.Utils;

public class ExecParamConverter implements AttributeConverter<List<ExecParam>, String> {

	@Override
	public String convertToDatabaseColumn(List<ExecParam> attribute) {
		if (Utils.isNullOrEmpty(attribute)) {
			return null;
		}
		return JSON.toJSONString(attribute);
	}

	@Override
	public List<ExecParam> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.isEmpty() || dbData.equalsIgnoreCase("null")) {
			return null;
		}
		return JSON.parseArray(dbData, ExecParam.class);
	}

}
