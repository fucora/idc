package com.iwellmass.idc.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.model.JobAlarm;
import com.iwellmass.idc.model.JobAlarmQuery;

@Service
public class JobAlarmService {

    public PageData<List<JobAlarm>> findJobAlarmByCondition(JobAlarmQuery query, Pager pager){
    	throw new UnsupportedOperationException("not supported yet.");
    }
    
    
}
