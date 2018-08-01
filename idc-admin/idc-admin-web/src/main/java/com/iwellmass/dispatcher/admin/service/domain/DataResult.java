package com.iwellmass.dispatcher.admin.service.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xkwu on 2016/5/10.
 */
public class DataResult {
    private STATUS_CODE statusCode = STATUS_CODE.SUCCESS;
    private Map<String, Object> dataMap = new HashMap<>();
    private String msg;

    public STATUS_CODE getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(STATUS_CODE statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public enum STATUS_CODE {
        SUCCESS, FAILURE
    }

    public DataResult addAttribute(String attributeName, Object attributeValue) {
        dataMap.put(attributeName, attributeValue);
        return this;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

}
