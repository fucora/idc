package com.iwellmass.idc.mapper;

import com.iwellmass.idc.model.JobInstance;

public interface JobInstanceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(JobInstance record);

    int insertSelective(JobInstance record);

    JobInstance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(JobInstance record);

    int updateByPrimaryKey(JobInstance record);
}