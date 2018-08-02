package com.iwellmass.dispatcher.admin.dao.mapper;

import org.apache.ibatis.annotations.Param;

import com.iwellmass.dispatcher.admin.dao.model.DdcFiveMinuteExecuteStatistic;
import com.iwellmass.dispatcher.admin.dao.model.DdcFiveMinuteExecuteStatisticExample;
import com.iwellmass.dispatcher.admin.dao.model.DdcFiveMinuteExecuteStatisticKey;

import java.util.List;

public interface DdcFiveMinuteExecuteStatisticMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_FIVE_MINUTE_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    int countByExample(DdcFiveMinuteExecuteStatisticExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_FIVE_MINUTE_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    int deleteByExample(DdcFiveMinuteExecuteStatisticExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_FIVE_MINUTE_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    int deleteByPrimaryKey(DdcFiveMinuteExecuteStatisticKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_FIVE_MINUTE_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    int insert(DdcFiveMinuteExecuteStatistic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_FIVE_MINUTE_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    int insertSelective(DdcFiveMinuteExecuteStatistic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_FIVE_MINUTE_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    List<DdcFiveMinuteExecuteStatistic> selectByExample(DdcFiveMinuteExecuteStatisticExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_FIVE_MINUTE_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    DdcFiveMinuteExecuteStatistic selectByPrimaryKey(DdcFiveMinuteExecuteStatisticKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_FIVE_MINUTE_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    int updateByExampleSelective(@Param("record") DdcFiveMinuteExecuteStatistic record, @Param("example") DdcFiveMinuteExecuteStatisticExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_FIVE_MINUTE_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    int updateByExample(@Param("record") DdcFiveMinuteExecuteStatistic record, @Param("example") DdcFiveMinuteExecuteStatisticExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_FIVE_MINUTE_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    int updateByPrimaryKeySelective(DdcFiveMinuteExecuteStatistic record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_FIVE_MINUTE_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    int updateByPrimaryKey(DdcFiveMinuteExecuteStatistic record);

    /**
     * Sum by app id list.
     * 按appId对任务执行成功、失败次数进行统计
     * @param appId the app id
     * @return the list
     */
    List<DdcFiveMinuteExecuteStatistic> aggregateByAppId(@Param("appId") int appId, @Param("startTime") String startTime);
}