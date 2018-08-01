package com.iwellmass.dispatcher.admin.dao.mapper;

import org.apache.ibatis.annotations.Param;

import com.iwellmass.dispatcher.admin.dao.model.DdcServer;
import com.iwellmass.dispatcher.admin.dao.model.DdcServerExample;

import java.util.List;

public interface DdcServerMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    int countByExample(DdcServerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    int deleteByExample(DdcServerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    int insert(DdcServer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    int insertSelective(DdcServer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    List<DdcServer> selectByExample(DdcServerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    DdcServer selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    int updateByExampleSelective(@Param("record") DdcServer record, @Param("example") DdcServerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    int updateByExample(@Param("record") DdcServer record, @Param("example") DdcServerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    int updateByPrimaryKeySelective(DdcServer record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    int updateByPrimaryKey(DdcServer record);
}