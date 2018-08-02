package com.iwellmass.dispatcher.common.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.iwellmass.dispatcher.common.model.DdcTaskExecuteStatus;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteStatusExample;

public interface DdcTaskExecuteStatusMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_TASK_EXECUTE_STATUS
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int countByExample(DdcTaskExecuteStatusExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_TASK_EXECUTE_STATUS
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int deleteByExample(DdcTaskExecuteStatusExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_TASK_EXECUTE_STATUS
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int deleteByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_TASK_EXECUTE_STATUS
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int insert(DdcTaskExecuteStatus record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_TASK_EXECUTE_STATUS
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int insertSelective(DdcTaskExecuteStatus record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_TASK_EXECUTE_STATUS
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	List<DdcTaskExecuteStatus> selectByExample(DdcTaskExecuteStatusExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_TASK_EXECUTE_STATUS
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	DdcTaskExecuteStatus selectByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_TASK_EXECUTE_STATUS
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int updateByExampleSelective(@Param("record") DdcTaskExecuteStatus record,
			@Param("example") DdcTaskExecuteStatusExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_TASK_EXECUTE_STATUS
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int updateByExample(@Param("record") DdcTaskExecuteStatus record,
			@Param("example") DdcTaskExecuteStatusExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_TASK_EXECUTE_STATUS
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int updateByPrimaryKeySelective(DdcTaskExecuteStatus record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_TASK_EXECUTE_STATUS
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int updateByPrimaryKey(DdcTaskExecuteStatus record);
}