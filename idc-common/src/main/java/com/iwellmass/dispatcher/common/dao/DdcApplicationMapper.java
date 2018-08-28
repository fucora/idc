package com.iwellmass.dispatcher.common.dao;

import java.util.List;

import com.iwellmass.dispatcher.common.model.DdcApplication;
import com.iwellmass.dispatcher.common.model.DdcApplicationExample;

public interface DdcApplicationMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int countByExample(DdcApplicationExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int deleteByExample(DdcApplicationExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int deleteByPrimaryKey(Integer appId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int insert(DdcApplication record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int insertSelective(DdcApplication record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	List<DdcApplication> selectByExample(DdcApplicationExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	DdcApplication selectByPrimaryKey(Integer appId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int updateByExampleSelective(@Param("record") DdcApplication record,
			@Param("example") DdcApplicationExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int updateByExample(@Param("record") DdcApplication record, @Param("example") DdcApplicationExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int updateByPrimaryKeySelective(DdcApplication record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	int updateByPrimaryKey(DdcApplication record);
}