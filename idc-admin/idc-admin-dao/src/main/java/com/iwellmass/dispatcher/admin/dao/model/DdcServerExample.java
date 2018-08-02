package com.iwellmass.dispatcher.admin.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.iwellmass.dispatcher.admin.dao.IDCPager;

public class DdcServerExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    protected List<Criteria> oredCriteria;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    protected IDCPager page;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    public DdcServerExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
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
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    public void setPage(IDCPager page) {
        this.page=page;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
     */
    public IDCPager getPage() {
        return page;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
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

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("ID =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("ID <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("ID >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("ID >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("ID <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("ID <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("ID in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("ID not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("ID between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("ID not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIpIsNull() {
            addCriterion("IP is null");
            return (Criteria) this;
        }

        public Criteria andIpIsNotNull() {
            addCriterion("IP is not null");
            return (Criteria) this;
        }

        public Criteria andIpEqualTo(String value) {
            addCriterion("IP =", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotEqualTo(String value) {
            addCriterion("IP <>", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpGreaterThan(String value) {
            addCriterion("IP >", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpGreaterThanOrEqualTo(String value) {
            addCriterion("IP >=", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLessThan(String value) {
            addCriterion("IP <", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLessThanOrEqualTo(String value) {
            addCriterion("IP <=", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpLike(String value) {
            addCriterion("IP like", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotLike(String value) {
            addCriterion("IP not like", value, "ip");
            return (Criteria) this;
        }

        public Criteria andIpIn(List<String> values) {
            addCriterion("IP in", values, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotIn(List<String> values) {
            addCriterion("IP not in", values, "ip");
            return (Criteria) this;
        }

        public Criteria andIpBetween(String value1, String value2) {
            addCriterion("IP between", value1, value2, "ip");
            return (Criteria) this;
        }

        public Criteria andIpNotBetween(String value1, String value2) {
            addCriterion("IP not between", value1, value2, "ip");
            return (Criteria) this;
        }

        public Criteria andPortIsNull() {
            addCriterion("PORT is null");
            return (Criteria) this;
        }

        public Criteria andPortIsNotNull() {
            addCriterion("PORT is not null");
            return (Criteria) this;
        }

        public Criteria andPortEqualTo(Integer value) {
            addCriterion("PORT =", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortNotEqualTo(Integer value) {
            addCriterion("PORT <>", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortGreaterThan(Integer value) {
            addCriterion("PORT >", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortGreaterThanOrEqualTo(Integer value) {
            addCriterion("PORT >=", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortLessThan(Integer value) {
            addCriterion("PORT <", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortLessThanOrEqualTo(Integer value) {
            addCriterion("PORT <=", value, "port");
            return (Criteria) this;
        }

        public Criteria andPortIn(List<Integer> values) {
            addCriterion("PORT in", values, "port");
            return (Criteria) this;
        }

        public Criteria andPortNotIn(List<Integer> values) {
            addCriterion("PORT not in", values, "port");
            return (Criteria) this;
        }

        public Criteria andPortBetween(Integer value1, Integer value2) {
            addCriterion("PORT between", value1, value2, "port");
            return (Criteria) this;
        }

        public Criteria andPortNotBetween(Integer value1, Integer value2) {
            addCriterion("PORT not between", value1, value2, "port");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("STATUS is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Integer value) {
            addCriterion("STATUS =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Integer value) {
            addCriterion("STATUS <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Integer value) {
            addCriterion("STATUS >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("STATUS >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Integer value) {
            addCriterion("STATUS <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Integer value) {
            addCriterion("STATUS <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Integer> values) {
            addCriterion("STATUS in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Integer> values) {
            addCriterion("STATUS not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Integer value1, Integer value2) {
            addCriterion("STATUS between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Integer value1, Integer value2) {
            addCriterion("STATUS not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeIsNull() {
            addCriterion("LAST_START_TIME is null");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeIsNotNull() {
            addCriterion("LAST_START_TIME is not null");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeEqualTo(Date value) {
            addCriterion("LAST_START_TIME =", value, "lastStartTime");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeNotEqualTo(Date value) {
            addCriterion("LAST_START_TIME <>", value, "lastStartTime");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeGreaterThan(Date value) {
            addCriterion("LAST_START_TIME >", value, "lastStartTime");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("LAST_START_TIME >=", value, "lastStartTime");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeLessThan(Date value) {
            addCriterion("LAST_START_TIME <", value, "lastStartTime");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeLessThanOrEqualTo(Date value) {
            addCriterion("LAST_START_TIME <=", value, "lastStartTime");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeIn(List<Date> values) {
            addCriterion("LAST_START_TIME in", values, "lastStartTime");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeNotIn(List<Date> values) {
            addCriterion("LAST_START_TIME not in", values, "lastStartTime");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeBetween(Date value1, Date value2) {
            addCriterion("LAST_START_TIME between", value1, value2, "lastStartTime");
            return (Criteria) this;
        }

        public Criteria andLastStartTimeNotBetween(Date value1, Date value2) {
            addCriterion("LAST_START_TIME not between", value1, value2, "lastStartTime");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeIsNull() {
            addCriterion("LAST_HB_TIME is null");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeIsNotNull() {
            addCriterion("LAST_HB_TIME is not null");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeEqualTo(Date value) {
            addCriterion("LAST_HB_TIME =", value, "lastHbTime");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeNotEqualTo(Date value) {
            addCriterion("LAST_HB_TIME <>", value, "lastHbTime");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeGreaterThan(Date value) {
            addCriterion("LAST_HB_TIME >", value, "lastHbTime");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("LAST_HB_TIME >=", value, "lastHbTime");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeLessThan(Date value) {
            addCriterion("LAST_HB_TIME <", value, "lastHbTime");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeLessThanOrEqualTo(Date value) {
            addCriterion("LAST_HB_TIME <=", value, "lastHbTime");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeIn(List<Date> values) {
            addCriterion("LAST_HB_TIME in", values, "lastHbTime");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeNotIn(List<Date> values) {
            addCriterion("LAST_HB_TIME not in", values, "lastHbTime");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeBetween(Date value1, Date value2) {
            addCriterion("LAST_HB_TIME between", value1, value2, "lastHbTime");
            return (Criteria) this;
        }

        public Criteria andLastHbTimeNotBetween(Date value1, Date value2) {
            addCriterion("LAST_HB_TIME not between", value1, value2, "lastHbTime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table DDC_SERVER
     *
     * @mbggenerated do_not_delete_during_merge Mon Jun 20 16:41:20 CST 2016
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table DDC_SERVER
     *
     * @mbggenerated Mon Jun 20 16:41:20 CST 2016
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