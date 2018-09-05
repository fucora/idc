package com.iwellmass.idc.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.model.JobAlarmQuery;
import com.iwellmass.idc.model.JobAlarm;

@Service
public class JobAlarmService {

    public PageData<List<JobAlarm>> findJobAlarmByCondition(JobAlarmQuery query, Pager pager){
    	throw new UnsupportedOperationException("not supported yet.");
    }
}
