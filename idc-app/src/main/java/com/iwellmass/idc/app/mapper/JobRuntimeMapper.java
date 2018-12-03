package com.iwellmass.idc.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.iwellmass.idc.app.vo.JobBarrierVO;
import com.iwellmass.idc.model.JobKey;

@Mapper
public interface JobRuntimeMapper {

	public List<JobBarrierVO> selectJobBarrierVO(@Param("jobKey") JobKey jobKey);

}
