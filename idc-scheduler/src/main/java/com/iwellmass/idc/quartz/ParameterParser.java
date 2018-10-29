package com.iwellmass.idc.quartz;

public class ParameterParser {

	public String parse(String... jobParam) {
		if (jobParam == null || jobParam.length == 0) {
			return null;
		}
		for (int i = jobParam.length - 1; i >= 0; i--) {
			String ret = jobParam[i];
			if (ret != null && !ret.isEmpty()) {
				return ret;
			}
		}
		return null;
	}

}
