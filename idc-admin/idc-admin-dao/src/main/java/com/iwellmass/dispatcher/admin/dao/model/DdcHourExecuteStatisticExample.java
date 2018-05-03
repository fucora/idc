package com.iwellmass.dispatcher.admin.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.iwellmass.dispatcher.admin.dao.Page;

public class DdcHourExecuteStatisticExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    protected List<Criteria> oredCriteria;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    protected Page page;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public DdcHourExecuteStatisticExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public void setPage(Page page) {
        this.page=page;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public Page getPage() {
        return page;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("ID is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("ID is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("ID =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("ID <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("ID >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("ID >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("ID <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("ID <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("ID in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("ID not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("ID between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("ID not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNull() {
            addCriterion("APP_ID is null");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNotNull() {
            addCriterion("APP_ID is not null");
            return (Criteria) this;
        }

        public Criteria andAppIdEqualTo(Integer value) {
            addCriterion("APP_ID =", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotEqualTo(Integer value) {
            addCriterion("APP_ID <>", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThan(Integer value) {
            addCriterion("APP_ID >", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("APP_ID >=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThan(Integer value) {
            addCriterion("APP_ID <", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThanOrEqualTo(Integer value) {
            addCriterion("APP_ID <=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdIn(List<Integer> values) {
            addCriterion("APP_ID in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotIn(List<Integer> values) {
            addCriterion("APP_ID not in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdBetween(Integer value1, Integer value2) {
            addCriterion("APP_ID between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotBetween(Integer value1, Integer value2) {
            addCriterion("APP_ID not between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andDataTimeIsNull() {
            addCriterion("DATA_TIME is null");
            return (Criteria) this;
        }

        public Criteria andDataTimeIsNotNull() {
            addCriterion("DATA_TIME is not null");
            return (Criteria) this;
        }

        public Criteria andDataTimeEqualTo(Date value) {
            addCriterion("DATA_TIME =", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeNotEqualTo(Date value) {
            addCriterion("DATA_TIME <>", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeGreaterThan(Date value) {
            addCriterion("DATA_TIME >", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("DATA_TIME >=", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeLessThan(Date value) {
            addCriterion("DATA_TIME <", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeLessThanOrEqualTo(Date value) {
            addCriterion("DATA_TIME <=", value, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeIn(List<Date> values) {
            addCriterion("DATA_TIME in", values, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeNotIn(List<Date> values) {
            addCriterion("DATA_TIME not in", values, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeBetween(Date value1, Date value2) {
            addCriterion("DATA_TIME between", value1, value2, "dataTime");
            return (Criteria) this;
        }

        public Criteria andDataTimeNotBetween(Date value1, Date value2) {
            addCriterion("DATA_TIME not between", value1, value2, "dataTime");
            return (Criteria) this;
        }

        public Criteria andTaskIdIsNull() {
            addCriterion("TASK_ID is null");
            return (Criteria) this;
        }

        public Criteria andTaskIdIsNotNull() {
            addCriterion("TASK_ID is not null");
            return (Criteria) this;
        }

        public Criteria andTaskIdEqualTo(Integer value) {
            addCriterion("TASK_ID =", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotEqualTo(Integer value) {
            addCriterion("TASK_ID <>", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdGreaterThan(Integer value) {
            addCriterion("TASK_ID >", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("TASK_ID >=", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdLessThan(Integer value) {
            addCriterion("TASK_ID <", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdLessThanOrEqualTo(Integer value) {
            addCriterion("TASK_ID <=", value, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdIn(List<Integer> values) {
            addCriterion("TASK_ID in", values, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotIn(List<Integer> values) {
            addCriterion("TASK_ID not in", values, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdBetween(Integer value1, Integer value2) {
            addCriterion("TASK_ID between", value1, value2, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskIdNotBetween(Integer value1, Integer value2) {
            addCriterion("TASK_ID not between", value1, value2, "taskId");
            return (Criteria) this;
        }

        public Criteria andTaskTypeIsNull() {
            addCriterion("TASK_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andTaskTypeIsNotNull() {
            addCriterion("TASK_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andTaskTypeEqualTo(Integer value) {
            addCriterion("TASK_TYPE =", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeNotEqualTo(Integer value) {
            addCriterion("TASK_TYPE <>", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeGreaterThan(Integer value) {
            addCriterion("TASK_TYPE >", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("TASK_TYPE >=", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeLessThan(Integer value) {
            addCriterion("TASK_TYPE <", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeLessThanOrEqualTo(Integer value) {
            addCriterion("TASK_TYPE <=", value, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeIn(List<Integer> values) {
            addCriterion("TASK_TYPE in", values, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeNotIn(List<Integer> values) {
            addCriterion("TASK_TYPE not in", values, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeBetween(Integer value1, Integer value2) {
            addCriterion("TASK_TYPE between", value1, value2, "taskType");
            return (Criteria) this;
        }

        public Criteria andTaskTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("TASK_TYPE not between", value1, value2, "taskType");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedIsNull() {
            addCriterion("EXECUTE_SUCCEED is null");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedIsNotNull() {
            addCriterion("EXECUTE_SUCCEED is not null");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedEqualTo(Integer value) {
            addCriterion("EXECUTE_SUCCEED =", value, "executeSucceed");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedNotEqualTo(Integer value) {
            addCriterion("EXECUTE_SUCCEED <>", value, "executeSucceed");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedGreaterThan(Integer value) {
            addCriterion("EXECUTE_SUCCEED >", value, "executeSucceed");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedGreaterThanOrEqualTo(Integer value) {
            addCriterion("EXECUTE_SUCCEED >=", value, "executeSucceed");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedLessThan(Integer value) {
            addCriterion("EXECUTE_SUCCEED <", value, "executeSucceed");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedLessThanOrEqualTo(Integer value) {
            addCriterion("EXECUTE_SUCCEED <=", value, "executeSucceed");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedIn(List<Integer> values) {
            addCriterion("EXECUTE_SUCCEED in", values, "executeSucceed");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedNotIn(List<Integer> values) {
            addCriterion("EXECUTE_SUCCEED not in", values, "executeSucceed");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedBetween(Integer value1, Integer value2) {
            addCriterion("EXECUTE_SUCCEED between", value1, value2, "executeSucceed");
            return (Criteria) this;
        }

        public Criteria andExecuteSucceedNotBetween(Integer value1, Integer value2) {
            addCriterion("EXECUTE_SUCCEED not between", value1, value2, "executeSucceed");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedIsNull() {
            addCriterion("EXECUTE_FAILED is null");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedIsNotNull() {
            addCriterion("EXECUTE_FAILED is not null");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedEqualTo(Integer value) {
            addCriterion("EXECUTE_FAILED =", value, "executeFailed");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedNotEqualTo(Integer value) {
            addCriterion("EXECUTE_FAILED <>", value, "executeFailed");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedGreaterThan(Integer value) {
            addCriterion("EXECUTE_FAILED >", value, "executeFailed");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedGreaterThanOrEqualTo(Integer value) {
            addCriterion("EXECUTE_FAILED >=", value, "executeFailed");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedLessThan(Integer value) {
            addCriterion("EXECUTE_FAILED <", value, "executeFailed");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedLessThanOrEqualTo(Integer value) {
            addCriterion("EXECUTE_FAILED <=", value, "executeFailed");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedIn(List<Integer> values) {
            addCriterion("EXECUTE_FAILED in", values, "executeFailed");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedNotIn(List<Integer> values) {
            addCriterion("EXECUTE_FAILED not in", values, "executeFailed");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedBetween(Integer value1, Integer value2) {
            addCriterion("EXECUTE_FAILED between", value1, value2, "executeFailed");
            return (Criteria) this;
        }

        public Criteria andExecuteFailedNotBetween(Integer value1, Integer value2) {
            addCriterion("EXECUTE_FAILED not between", value1, value2, "executeFailed");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutIsNull() {
            addCriterion("EXECUTE_TIME_OUT is null");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutIsNotNull() {
            addCriterion("EXECUTE_TIME_OUT is not null");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutEqualTo(Integer value) {
            addCriterion("EXECUTE_TIME_OUT =", value, "executeTimeOut");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutNotEqualTo(Integer value) {
            addCriterion("EXECUTE_TIME_OUT <>", value, "executeTimeOut");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutGreaterThan(Integer value) {
            addCriterion("EXECUTE_TIME_OUT >", value, "executeTimeOut");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutGreaterThanOrEqualTo(Integer value) {
            addCriterion("EXECUTE_TIME_OUT >=", value, "executeTimeOut");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutLessThan(Integer value) {
            addCriterion("EXECUTE_TIME_OUT <", value, "executeTimeOut");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutLessThanOrEqualTo(Integer value) {
            addCriterion("EXECUTE_TIME_OUT <=", value, "executeTimeOut");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutIn(List<Integer> values) {
            addCriterion("EXECUTE_TIME_OUT in", values, "executeTimeOut");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutNotIn(List<Integer> values) {
            addCriterion("EXECUTE_TIME_OUT not in", values, "executeTimeOut");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutBetween(Integer value1, Integer value2) {
            addCriterion("EXECUTE_TIME_OUT between", value1, value2, "executeTimeOut");
            return (Criteria) this;
        }

        public Criteria andExecuteTimeOutNotBetween(Integer value1, Integer value2) {
            addCriterion("EXECUTE_TIME_OUT not between", value1, value2, "executeTimeOut");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationIsNull() {
            addCriterion("EXECUTE_DURATION is null");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationIsNotNull() {
            addCriterion("EXECUTE_DURATION is not null");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationEqualTo(Double value) {
            addCriterion("EXECUTE_DURATION =", value, "executeDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationNotEqualTo(Double value) {
            addCriterion("EXECUTE_DURATION <>", value, "executeDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationGreaterThan(Double value) {
            addCriterion("EXECUTE_DURATION >", value, "executeDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationGreaterThanOrEqualTo(Double value) {
            addCriterion("EXECUTE_DURATION >=", value, "executeDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationLessThan(Double value) {
            addCriterion("EXECUTE_DURATION <", value, "executeDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationLessThanOrEqualTo(Double value) {
            addCriterion("EXECUTE_DURATION <=", value, "executeDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationIn(List<Double> values) {
            addCriterion("EXECUTE_DURATION in", values, "executeDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationNotIn(List<Double> values) {
            addCriterion("EXECUTE_DURATION not in", values, "executeDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationBetween(Double value1, Double value2) {
            addCriterion("EXECUTE_DURATION between", value1, value2, "executeDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteDurationNotBetween(Double value1, Double value2) {
            addCriterion("EXECUTE_DURATION not between", value1, value2, "executeDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationIsNull() {
            addCriterion("EXECUTE_MAX_DURATION is null");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationIsNotNull() {
            addCriterion("EXECUTE_MAX_DURATION is not null");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationEqualTo(Double value) {
            addCriterion("EXECUTE_MAX_DURATION =", value, "executeMaxDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationNotEqualTo(Double value) {
            addCriterion("EXECUTE_MAX_DURATION <>", value, "executeMaxDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationGreaterThan(Double value) {
            addCriterion("EXECUTE_MAX_DURATION >", value, "executeMaxDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationGreaterThanOrEqualTo(Double value) {
            addCriterion("EXECUTE_MAX_DURATION >=", value, "executeMaxDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationLessThan(Double value) {
            addCriterion("EXECUTE_MAX_DURATION <", value, "executeMaxDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationLessThanOrEqualTo(Double value) {
            addCriterion("EXECUTE_MAX_DURATION <=", value, "executeMaxDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationIn(List<Double> values) {
            addCriterion("EXECUTE_MAX_DURATION in", values, "executeMaxDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationNotIn(List<Double> values) {
            addCriterion("EXECUTE_MAX_DURATION not in", values, "executeMaxDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationBetween(Double value1, Double value2) {
            addCriterion("EXECUTE_MAX_DURATION between", value1, value2, "executeMaxDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMaxDurationNotBetween(Double value1, Double value2) {
            addCriterion("EXECUTE_MAX_DURATION not between", value1, value2, "executeMaxDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationIsNull() {
            addCriterion("EXECUTE_MIN_DURATION is null");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationIsNotNull() {
            addCriterion("EXECUTE_MIN_DURATION is not null");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationEqualTo(Double value) {
            addCriterion("EXECUTE_MIN_DURATION =", value, "executeMinDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationNotEqualTo(Double value) {
            addCriterion("EXECUTE_MIN_DURATION <>", value, "executeMinDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationGreaterThan(Double value) {
            addCriterion("EXECUTE_MIN_DURATION >", value, "executeMinDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationGreaterThanOrEqualTo(Double value) {
            addCriterion("EXECUTE_MIN_DURATION >=", value, "executeMinDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationLessThan(Double value) {
            addCriterion("EXECUTE_MIN_DURATION <", value, "executeMinDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationLessThanOrEqualTo(Double value) {
            addCriterion("EXECUTE_MIN_DURATION <=", value, "executeMinDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationIn(List<Double> values) {
            addCriterion("EXECUTE_MIN_DURATION in", values, "executeMinDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationNotIn(List<Double> values) {
            addCriterion("EXECUTE_MIN_DURATION not in", values, "executeMinDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationBetween(Double value1, Double value2) {
            addCriterion("EXECUTE_MIN_DURATION between", value1, value2, "executeMinDuration");
            return (Criteria) this;
        }

        public Criteria andExecuteMinDurationNotBetween(Double value1, Double value2) {
            addCriterion("EXECUTE_MIN_DURATION not between", value1, value2, "executeMinDuration");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated do_not_delete_during_merge Wed Jul 06 11:37:17 CST 2016
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table DDC_HOUR_EXECUTE_STATISTIC
     *
     * @mbggenerated Wed Jul 06 11:37:17 CST 2016
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}