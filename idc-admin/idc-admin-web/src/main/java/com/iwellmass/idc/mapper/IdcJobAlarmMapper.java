package com.iwellmass.idc.mapper;

import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.model.JobAlarm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IdcJobAlarmMapper {
     List<JobAlarm> findJobAlarmByCondition(@Param(value = "alarm") JobAlarm alarm,@Param(value = "pager") Pager pager);
     List<JobAlarm> findAllJobAlarmByCondition(JobAlarm alarm);
}
