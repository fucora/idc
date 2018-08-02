package com.iwellmass.dispatcher.admin.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.iwellmass.dispatcher.admin.dao.IDCPager;

public class DdcAlarmHistoryExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    protected List<Criteria> oredCriteria;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    protected IDCPager page;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    public DdcAlarmHistoryExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
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
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    public void setPage(IDCPager page) {
        this.page=page;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
     */
    public IDCPager getPage() {
        return page;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
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

        public Criteria andAlarmKeyIsNull() {
            addCriterion("ALARM_KEY is null");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyIsNotNull() {
            addCriterion("ALARM_KEY is not null");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyEqualTo(String value) {
            addCriterion("ALARM_KEY =", value, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyNotEqualTo(String value) {
            addCriterion("ALARM_KEY <>", value, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyGreaterThan(String value) {
            addCriterion("ALARM_KEY >", value, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyGreaterThanOrEqualTo(String value) {
            addCriterion("ALARM_KEY >=", value, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyLessThan(String value) {
            addCriterion("ALARM_KEY <", value, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyLessThanOrEqualTo(String value) {
            addCriterion("ALARM_KEY <=", value, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyLike(String value) {
            addCriterion("ALARM_KEY like", value, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyNotLike(String value) {
            addCriterion("ALARM_KEY not like", value, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyIn(List<String> values) {
            addCriterion("ALARM_KEY in", values, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyNotIn(List<String> values) {
            addCriterion("ALARM_KEY not in", values, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyBetween(String value1, String value2) {
            addCriterion("ALARM_KEY between", value1, value2, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andAlarmKeyNotBetween(String value1, String value2) {
            addCriterion("ALARM_KEY not between", value1, value2, "alarmKey");
            return (Criteria) this;
        }

        public Criteria andContentIsNull() {
            addCriterion("CONTENT is null");
            return (Criteria) this;
        }

        public Criteria andContentIsNotNull() {
            addCriterion("CONTENT is not null");
            return (Criteria) this;
        }

        public Criteria andContentEqualTo(String value) {
            addCriterion("CONTENT =", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotEqualTo(String value) {
            addCriterion("CONTENT <>", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentGreaterThan(String value) {
            addCriterion("CONTENT >", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentGreaterThanOrEqualTo(String value) {
            addCriterion("CONTENT >=", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLessThan(String value) {
            addCriterion("CONTENT <", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLessThanOrEqualTo(String value) {
            addCriterion("CONTENT <=", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentLike(String value) {
            addCriterion("CONTENT like", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotLike(String value) {
            addCriterion("CONTENT not like", value, "content");
            return (Criteria) this;
        }

        public Criteria andContentIn(List<String> values) {
            addCriterion("CONTENT in", values, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotIn(List<String> values) {
            addCriterion("CONTENT not in", values, "content");
            return (Criteria) this;
        }

        public Criteria andContentBetween(String value1, String value2) {
            addCriterion("CONTENT between", value1, value2, "content");
            return (Criteria) this;
        }

        public Criteria andContentNotBetween(String value1, String value2) {
            addCriterion("CONTENT not between", value1, value2, "content");
            return (Criteria) this;
        }

        public Criteria andReceiversIsNull() {
            addCriterion("RECEIVERS is null");
            return (Criteria) this;
        }

        public Criteria andReceiversIsNotNull() {
            addCriterion("RECEIVERS is not null");
            return (Criteria) this;
        }

        public Criteria andReceiversEqualTo(String value) {
            addCriterion("RECEIVERS =", value, "receivers");
            return (Criteria) this;
        }

        public Criteria andReceiversNotEqualTo(String value) {
            addCriterion("RECEIVERS <>", value, "receivers");
            return (Criteria) this;
        }

        public Criteria andReceiversGreaterThan(String value) {
            addCriterion("RECEIVERS >", value, "receivers");
            return (Criteria) this;
        }

        public Criteria andReceiversGreaterThanOrEqualTo(String value) {
            addCriterion("RECEIVERS >=", value, "receivers");
            return (Criteria) this;
        }

        public Criteria andReceiversLessThan(String value) {
            addCriterion("RECEIVERS <", value, "receivers");
            return (Criteria) this;
        }

        public Criteria andReceiversLessThanOrEqualTo(String value) {
            addCriterion("RECEIVERS <=", value, "receivers");
            return (Criteria) this;
        }

        public Criteria andReceiversLike(String value) {
            addCriterion("RECEIVERS like", value, "receivers");
            return (Criteria) this;
        }

        public Criteria andReceiversNotLike(String value) {
            addCriterion("RECEIVERS not like", value, "receivers");
            return (Criteria) this;
        }

        public Criteria andReceiversIn(List<String> values) {
            addCriterion("RECEIVERS in", values, "receivers");
            return (Criteria) this;
        }

        public Criteria andReceiversNotIn(List<String> values) {
            addCriterion("RECEIVERS not in", values, "receivers");
            return (Criteria) this;
        }

        public Criteria andReceiversBetween(String value1, String value2) {
            addCriterion("RECEIVERS between", value1, value2, "receivers");
            return (Criteria) this;
        }

        public Criteria andReceiversNotBetween(String value1, String value2) {
            addCriterion("RECEIVERS not between", value1, value2, "receivers");
            return (Criteria) this;
        }

        public Criteria andAlarmDateIsNull() {
            addCriterion("ALARM_DATE is null");
            return (Criteria) this;
        }

        public Criteria andAlarmDateIsNotNull() {
            addCriterion("ALARM_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andAlarmDateEqualTo(Date value) {
            addCriterion("ALARM_DATE =", value, "alarmDate");
            return (Criteria) this;
        }

        public Criteria andAlarmDateNotEqualTo(Date value) {
            addCriterion("ALARM_DATE <>", value, "alarmDate");
            return (Criteria) this;
        }

        public Criteria andAlarmDateGreaterThan(Date value) {
            addCriterion("ALARM_DATE >", value, "alarmDate");
            return (Criteria) this;
        }

        public Criteria andAlarmDateGreaterThanOrEqualTo(Date value) {
            addCriterion("ALARM_DATE >=", value, "alarmDate");
            return (Criteria) this;
        }

        public Criteria andAlarmDateLessThan(Date value) {
            addCriterion("ALARM_DATE <", value, "alarmDate");
            return (Criteria) this;
        }

        public Criteria andAlarmDateLessThanOrEqualTo(Date value) {
            addCriterion("ALARM_DATE <=", value, "alarmDate");
            return (Criteria) this;
        }

        public Criteria andAlarmDateIn(List<Date> values) {
            addCriterion("ALARM_DATE in", values, "alarmDate");
            return (Criteria) this;
        }

        public Criteria andAlarmDateNotIn(List<Date> values) {
            addCriterion("ALARM_DATE not in", values, "alarmDate");
            return (Criteria) this;
        }

        public Criteria andAlarmDateBetween(Date value1, Date value2) {
            addCriterion("ALARM_DATE between", value1, value2, "alarmDate");
            return (Criteria) this;
        }

        public Criteria andAlarmDateNotBetween(Date value1, Date value2) {
            addCriterion("ALARM_DATE not between", value1, value2, "alarmDate");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated do_not_delete_during_merge Mon Jul 11 16:23:08 CST 2016
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table DDC_ALARM_HISTORY
     *
     * @mbggenerated Mon Jul 11 16:23:08 CST 2016
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