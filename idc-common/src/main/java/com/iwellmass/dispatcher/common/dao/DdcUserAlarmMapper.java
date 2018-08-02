package com.iwellmass.dispatcher.common.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.iwellmass.dispatcher.common.model.DdcUserAlarm;
import com.iwellmass.dispatcher.common.model.DdcUserAlarmExample;

public interface DdcUserAlarmMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_USER_ALARM
     *
     * @mbggenerated Mon Jun 20 15:28:18 CST 2016
     */
    int countByExample(DdcUserAlarmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_USER_ALARM
     *
     * @mbggenerated Mon Jun 20 15:28:18 CST 2016
     */
    int deleteByExample(DdcUserAlarmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_USER_ALARM
     *
     * @mbggenerated Mon Jun 20 15:28:18 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_USER_ALARM
     *
     * @mbggenerated Mon Jun 20 15:28:18 CST 2016
     */
    int insert(DdcUserAlarm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_USER_ALARM
     *
     * @mbggenerated Mon Jun 20 15:28:18 CST 2016
     */
    int insertSelective(DdcUserAlarm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_USER_ALARM
     *
     * @mbggenerated Mon Jun 20 15:28:18 CST 2016
     */
    List<DdcUserAlarm> selectByExample(DdcUserAlarmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_USER_ALARM
     *
     * @mbggenerated Mon Jun 20 15:28:18 CST 2016
     */
    DdcUserAlarm selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_USER_ALARM
     *
     * @mbggenerated Mon Jun 20 15:28:18 CST 2016
     */
    int updateByExampleSelective(@Param("record") DdcUserAlarm record, @Param("example") DdcUserAlarmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_USER_ALARM
     *
     * @mbggenerated Mon Jun 20 15:28:18 CST 2016
     */
    int updateByExample(@Param("record") DdcUserAlarm record, @Param("example") DdcUserAlarmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_USER_ALARM
     *
     * @mbggenerated Mon Jun 20 15:28:18 CST 2016
     */
    int updateByPrimaryKeySelective(DdcUserAlarm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_USER_ALARM
     *
     * @mbggenerated Mon Jun 20 15:28:18 CST 2016
     */
    int updateByPrimaryKey(DdcUserAlarm record);
}