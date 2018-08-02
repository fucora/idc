package com.iwellmass.dispatcher.admin.dao.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.iwellmass.dispatcher.admin.dao.model.DdcUserAlarm;
import com.iwellmass.dispatcher.admin.dao.model.DdcUserAlarmExample;

public interface DdcUserAlarmMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddc_user_alarm
     *
     * @mbggenerated Wed Jun 22 14:41:10 CST 2016
     */
    int countByExample(DdcUserAlarmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddc_user_alarm
     *
     * @mbggenerated Wed Jun 22 14:41:10 CST 2016
     */
    int deleteByExample(DdcUserAlarmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddc_user_alarm
     *
     * @mbggenerated Wed Jun 22 14:41:10 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddc_user_alarm
     *
     * @mbggenerated Wed Jun 22 14:41:10 CST 2016
     */
    int insert(DdcUserAlarm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddc_user_alarm
     *
     * @mbggenerated Wed Jun 22 14:41:10 CST 2016
     */
    int insertSelective(DdcUserAlarm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddc_user_alarm
     *
     * @mbggenerated Wed Jun 22 14:41:10 CST 2016
     */
    List<DdcUserAlarm> selectByExample(DdcUserAlarmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddc_user_alarm
     *
     * @mbggenerated Wed Jun 22 14:41:10 CST 2016
     */
    DdcUserAlarm selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddc_user_alarm
     *
     * @mbggenerated Wed Jun 22 14:41:10 CST 2016
     */
    int updateByExampleSelective(@Param("record") DdcUserAlarm record, @Param("example") DdcUserAlarmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddc_user_alarm
     *
     * @mbggenerated Wed Jun 22 14:41:10 CST 2016
     */
    int updateByExample(@Param("record") DdcUserAlarm record, @Param("example") DdcUserAlarmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddc_user_alarm
     *
     * @mbggenerated Wed Jun 22 14:41:10 CST 2016
     */
    int updateByPrimaryKeySelective(DdcUserAlarm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ddc_user_alarm
     *
     * @mbggenerated Wed Jun 22 14:41:10 CST 2016
     */
    int updateByPrimaryKey(DdcUserAlarm record);
}