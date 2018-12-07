package com.iwellmass.idc.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.iwellmass.idc.app.model.JobQuery;
import com.iwellmass.idc.app.vo.JobBarrierVO;
import com.iwellmass.idc.app.vo.JobRuntimeListVO;
import com.iwellmass.idc.app.vo.JobRuntime;
import com.iwellmass.idc.model.JobKey;

@Mapper
public interface JobRuntimeMapper {

	public List<JobBarrierVO> selectJobBarrierVO(JobKey jobKey);

	
	public List<JobRuntimeListVO> selectJobRuntimeList(@Param("q") JobQuery query);


	public JobRuntime selectJobRuntime(JobKey jobKey);
}
