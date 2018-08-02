package com.iwellmass.dispatcher.admin.dao.mapper;

import org.apache.ibatis.annotations.Param;

import com.iwellmass.dispatcher.admin.dao.model.DdcRunningTask;
import com.iwellmass.dispatcher.admin.dao.model.DdcRunningTaskExample;

import java.util.List;

public interface DdcRunningTaskMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated Tue Nov 01 19:58:08 CST 2016
     */
    int countByExample(DdcRunningTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated Tue Nov 01 19:58:08 CST 2016
     */
    int deleteByExample(DdcRunningTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated Tue Nov 01 19:58:08 CST 2016
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated Tue Nov 01 19:58:08 CST 2016
     */
    int insert(DdcRunningTask record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated Tue Nov 01 19:58:08 CST 2016
     */
    int insertSelective(DdcRunningTask record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated Tue Nov 01 19:58:08 CST 2016
     */
    List<DdcRunningTask> selectByExample(DdcRunningTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated Tue Nov 01 19:58:08 CST 2016
     */
    DdcRunningTask selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated Tue Nov 01 19:58:08 CST 2016
     */
    int updateByExampleSelective(@Param("record") DdcRunningTask record, @Param("example") DdcRunningTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated Tue Nov 01 19:58:08 CST 2016
     */
    int updateByExample(@Param("record") DdcRunningTask record, @Param("example") DdcRunningTaskExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated Tue Nov 01 19:58:08 CST 2016
     */
    int updateByPrimaryKeySelective(DdcRunningTask record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated Tue Nov 01 19:58:08 CST 2016
     */
    int updateByPrimaryKey(DdcRunningTask record);
}