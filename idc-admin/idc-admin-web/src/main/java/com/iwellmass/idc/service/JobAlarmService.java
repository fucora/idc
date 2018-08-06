package com.iwellmass.idc.service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.mapper.IdcJobAlarmMapper;
import com.iwellmass.idc.model.JobAlarm;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class JobAlarmService {

    @Inject
    private IdcJobAlarmMapper idcJobAlarmMapper;

    public PageData<List<JobAlarm>> findJobAlarmByCondition(JobAlarm alarm, Pager pager){
        Pager pager1=new Pager();
        pager1.setPage(pager.getTo());
        pager1.setLimit(pager.getLimit());
        List<JobAlarm> allJobAlarm = idcJobAlarmMapper.findAllJobAlarmByCondition(alarm);
        List<JobAlarm> jobAlarm = idcJobAlarmMapper.findJobAlarmByCondition(alarm, pager1);
        return new PageData(allJobAlarm.size(),jobAlarm);
    }
}
