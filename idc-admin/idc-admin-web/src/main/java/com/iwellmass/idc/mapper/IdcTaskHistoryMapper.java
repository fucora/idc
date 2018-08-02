package com.iwellmass.idc.mapper;

import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface IdcTaskHistoryMapper {
    List<JobInstance> findTaskInstanceByCondition(@Param(value = "job") JobQuery job, @Param(value = "pager") Pager pager);
    List<JobInstance> findAllTaskInstanceByCondition(JobQuery job);
}
