package com.iwellmass.dispatcher.admin.dao.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.iwellmass.dispatcher.admin.dao.model.DdcNode;
import com.iwellmass.dispatcher.admin.dao.model.DdcNodeExample;

public interface DdcNodeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    int countByExample(DdcNodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    int deleteByExample(DdcNodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    int insert(DdcNode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    int insertSelective(DdcNode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    List<DdcNode> selectByExampleWithBLOBs(DdcNodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    List<DdcNode> selectByExample(DdcNodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    DdcNode selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    int updateByExampleSelective(@Param("record") DdcNode record, @Param("example") DdcNodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    int updateByExampleWithBLOBs(@Param("record") DdcNode record, @Param("example") DdcNodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    int updateByExample(@Param("record") DdcNode record, @Param("example") DdcNodeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    int updateByPrimaryKeySelective(DdcNode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    int updateByPrimaryKeyWithBLOBs(DdcNode record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_NODE
     *
     * @mbggenerated Wed May 25 10:50:51 CST 2016
     */
    int updateByPrimaryKey(DdcNode record);
}