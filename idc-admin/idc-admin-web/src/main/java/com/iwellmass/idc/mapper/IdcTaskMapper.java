package com.iwellmass.idc.mapper;


import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IdcTaskMapper {
    List<Job> findTasksByCondition(@Param(value = "job") JobQuery job, @Param(value = "pager") Pager pager);
    List<Job> findAllTasksByCondition(JobQuery job);
    List<Job> findTaskByGroupId(Integer id);
    List<Job> findAllTasks();
}
