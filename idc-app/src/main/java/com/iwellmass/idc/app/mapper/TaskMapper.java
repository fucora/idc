package com.iwellmass.idc.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;

@Mapper
public interface TaskMapper {
	public List<Task> selectBatch(@Param("taskKeys") List<TaskKey> taskKeys);
}
