package com.iwellmass.dispatcher.common.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DdcApplicationExample {
    /**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	protected String orderByClause;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	protected boolean distinct;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	protected List<Criteria> oredCriteria;

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	public DdcApplicationExample() {
		oredCriteria = new ArrayList<Criteria>();
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	public String getOrderByClause() {
		return orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	public Criteria createCriteria() {
		Criteria criteria = createCriteriaInternal();
		if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
	 */
	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
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

		public Criteria andAppKeyIsNull() {
			addCriterion("APP_KEY is null");
			return (Criteria) this;
		}

		public Criteria andAppKeyIsNotNull() {
			addCriterion("APP_KEY is not null");
			return (Criteria) this;
		}

		public Criteria andAppKeyEqualTo(String value) {
			addCriterion("APP_KEY =", value, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppKeyNotEqualTo(String value) {
			addCriterion("APP_KEY <>", value, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppKeyGreaterThan(String value) {
			addCriterion("APP_KEY >", value, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppKeyGreaterThanOrEqualTo(String value) {
			addCriterion("APP_KEY >=", value, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppKeyLessThan(String value) {
			addCriterion("APP_KEY <", value, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppKeyLessThanOrEqualTo(String value) {
			addCriterion("APP_KEY <=", value, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppKeyLike(String value) {
			addCriterion("APP_KEY like", value, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppKeyNotLike(String value) {
			addCriterion("APP_KEY not like", value, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppKeyIn(List<String> values) {
			addCriterion("APP_KEY in", values, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppKeyNotIn(List<String> values) {
			addCriterion("APP_KEY not in", values, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppKeyBetween(String value1, String value2) {
			addCriterion("APP_KEY between", value1, value2, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppKeyNotBetween(String value1, String value2) {
			addCriterion("APP_KEY not between", value1, value2, "appKey");
			return (Criteria) this;
		}

		public Criteria andAppNameIsNull() {
			addCriterion("APP_NAME is null");
			return (Criteria) this;
		}

		public Criteria andAppNameIsNotNull() {
			addCriterion("APP_NAME is not null");
			return (Criteria) this;
		}

		public Criteria andAppNameEqualTo(String value) {
			addCriterion("APP_NAME =", value, "appName");
			return (Criteria) this;
		}

		public Criteria andAppNameNotEqualTo(String value) {
			addCriterion("APP_NAME <>", value, "appName");
			return (Criteria) this;
		}

		public Criteria andAppNameGreaterThan(String value) {
			addCriterion("APP_NAME >", value, "appName");
			return (Criteria) this;
		}

		public Criteria andAppNameGreaterThanOrEqualTo(String value) {
			addCriterion("APP_NAME >=", value, "appName");
			return (Criteria) this;
		}

		public Criteria andAppNameLessThan(String value) {
			addCriterion("APP_NAME <", value, "appName");
			return (Criteria) this;
		}

		public Criteria andAppNameLessThanOrEqualTo(String value) {
			addCriterion("APP_NAME <=", value, "appName");
			return (Criteria) this;
		}

		public Criteria andAppNameLike(String value) {
			addCriterion("APP_NAME like", value, "appName");
			return (Criteria) this;
		}

		public Criteria andAppNameNotLike(String value) {
			addCriterion("APP_NAME not like", value, "appName");
			return (Criteria) this;
		}

		public Criteria andAppNameIn(List<String> values) {
			addCriterion("APP_NAME in", values, "appName");
			return (Criteria) this;
		}

		public Criteria andAppNameNotIn(List<String> values) {
			addCriterion("APP_NAME not in", values, "appName");
			return (Criteria) this;
		}

		public Criteria andAppNameBetween(String value1, String value2) {
			addCriterion("APP_NAME between", value1, value2, "appName");
			return (Criteria) this;
		}

		public Criteria andAppNameNotBetween(String value1, String value2) {
			addCriterion("APP_NAME not between", value1, value2, "appName");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionIsNull() {
			addCriterion("APP_DESCRIPTION is null");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionIsNotNull() {
			addCriterion("APP_DESCRIPTION is not null");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionEqualTo(String value) {
			addCriterion("APP_DESCRIPTION =", value, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionNotEqualTo(String value) {
			addCriterion("APP_DESCRIPTION <>", value, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionGreaterThan(String value) {
			addCriterion("APP_DESCRIPTION >", value, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionGreaterThanOrEqualTo(String value) {
			addCriterion("APP_DESCRIPTION >=", value, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionLessThan(String value) {
			addCriterion("APP_DESCRIPTION <", value, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionLessThanOrEqualTo(String value) {
			addCriterion("APP_DESCRIPTION <=", value, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionLike(String value) {
			addCriterion("APP_DESCRIPTION like", value, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionNotLike(String value) {
			addCriterion("APP_DESCRIPTION not like", value, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionIn(List<String> values) {
			addCriterion("APP_DESCRIPTION in", values, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionNotIn(List<String> values) {
			addCriterion("APP_DESCRIPTION not in", values, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionBetween(String value1, String value2) {
			addCriterion("APP_DESCRIPTION between", value1, value2, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppDescriptionNotBetween(String value1, String value2) {
			addCriterion("APP_DESCRIPTION not between", value1, value2, "appDescription");
			return (Criteria) this;
		}

		public Criteria andAppStatusIsNull() {
			addCriterion("APP_STATUS is null");
			return (Criteria) this;
		}

		public Criteria andAppStatusIsNotNull() {
			addCriterion("APP_STATUS is not null");
			return (Criteria) this;
		}

		public Criteria andAppStatusEqualTo(Integer value) {
			addCriterion("APP_STATUS =", value, "appStatus");
			return (Criteria) this;
		}

		public Criteria andAppStatusNotEqualTo(Integer value) {
			addCriterion("APP_STATUS <>", value, "appStatus");
			return (Criteria) this;
		}

		public Criteria andAppStatusGreaterThan(Integer value) {
			addCriterion("APP_STATUS >", value, "appStatus");
			return (Criteria) this;
		}

		public Criteria andAppStatusGreaterThanOrEqualTo(Integer value) {
			addCriterion("APP_STATUS >=", value, "appStatus");
			return (Criteria) this;
		}

		public Criteria andAppStatusLessThan(Integer value) {
			addCriterion("APP_STATUS <", value, "appStatus");
			return (Criteria) this;
		}

		public Criteria andAppStatusLessThanOrEqualTo(Integer value) {
			addCriterion("APP_STATUS <=", value, "appStatus");
			return (Criteria) this;
		}

		public Criteria andAppStatusIn(List<Integer> values) {
			addCriterion("APP_STATUS in", values, "appStatus");
			return (Criteria) this;
		}

		public Criteria andAppStatusNotIn(List<Integer> values) {
			addCriterion("APP_STATUS not in", values, "appStatus");
			return (Criteria) this;
		}

		public Criteria andAppStatusBetween(Integer value1, Integer value2) {
			addCriterion("APP_STATUS between", value1, value2, "appStatus");
			return (Criteria) this;
		}

		public Criteria andAppStatusNotBetween(Integer value1, Integer value2) {
			addCriterion("APP_STATUS not between", value1, value2, "appStatus");
			return (Criteria) this;
		}

		public Criteria andCreateUserIsNull() {
			addCriterion("CREATE_USER is null");
			return (Criteria) this;
		}

		public Criteria andCreateUserIsNotNull() {
			addCriterion("CREATE_USER is not null");
			return (Criteria) this;
		}

		public Criteria andCreateUserEqualTo(String value) {
			addCriterion("CREATE_USER =", value, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateUserNotEqualTo(String value) {
			addCriterion("CREATE_USER <>", value, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateUserGreaterThan(String value) {
			addCriterion("CREATE_USER >", value, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateUserGreaterThanOrEqualTo(String value) {
			addCriterion("CREATE_USER >=", value, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateUserLessThan(String value) {
			addCriterion("CREATE_USER <", value, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateUserLessThanOrEqualTo(String value) {
			addCriterion("CREATE_USER <=", value, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateUserLike(String value) {
			addCriterion("CREATE_USER like", value, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateUserNotLike(String value) {
			addCriterion("CREATE_USER not like", value, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateUserIn(List<String> values) {
			addCriterion("CREATE_USER in", values, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateUserNotIn(List<String> values) {
			addCriterion("CREATE_USER not in", values, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateUserBetween(String value1, String value2) {
			addCriterion("CREATE_USER between", value1, value2, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateUserNotBetween(String value1, String value2) {
			addCriterion("CREATE_USER not between", value1, value2, "createUser");
			return (Criteria) this;
		}

		public Criteria andCreateTimeIsNull() {
			addCriterion("CREATE_TIME is null");
			return (Criteria) this;
		}

		public Criteria andCreateTimeIsNotNull() {
			addCriterion("CREATE_TIME is not null");
			return (Criteria) this;
		}

		public Criteria andCreateTimeEqualTo(Date value) {
			addCriterion("CREATE_TIME =", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeNotEqualTo(Date value) {
			addCriterion("CREATE_TIME <>", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeGreaterThan(Date value) {
			addCriterion("CREATE_TIME >", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
			addCriterion("CREATE_TIME >=", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeLessThan(Date value) {
			addCriterion("CREATE_TIME <", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
			addCriterion("CREATE_TIME <=", value, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeIn(List<Date> values) {
			addCriterion("CREATE_TIME in", values, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeNotIn(List<Date> values) {
			addCriterion("CREATE_TIME not in", values, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeBetween(Date value1, Date value2) {
			addCriterion("CREATE_TIME between", value1, value2, "createTime");
			return (Criteria) this;
		}

		public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
			addCriterion("CREATE_TIME not between", value1, value2, "createTime");
			return (Criteria) this;
		}

		public Criteria andUpdateUserIsNull() {
			addCriterion("UPDATE_USER is null");
			return (Criteria) this;
		}

		public Criteria andUpdateUserIsNotNull() {
			addCriterion("UPDATE_USER is not null");
			return (Criteria) this;
		}

		public Criteria andUpdateUserEqualTo(String value) {
			addCriterion("UPDATE_USER =", value, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateUserNotEqualTo(String value) {
			addCriterion("UPDATE_USER <>", value, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateUserGreaterThan(String value) {
			addCriterion("UPDATE_USER >", value, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateUserGreaterThanOrEqualTo(String value) {
			addCriterion("UPDATE_USER >=", value, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateUserLessThan(String value) {
			addCriterion("UPDATE_USER <", value, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateUserLessThanOrEqualTo(String value) {
			addCriterion("UPDATE_USER <=", value, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateUserLike(String value) {
			addCriterion("UPDATE_USER like", value, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateUserNotLike(String value) {
			addCriterion("UPDATE_USER not like", value, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateUserIn(List<String> values) {
			addCriterion("UPDATE_USER in", values, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateUserNotIn(List<String> values) {
			addCriterion("UPDATE_USER not in", values, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateUserBetween(String value1, String value2) {
			addCriterion("UPDATE_USER between", value1, value2, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateUserNotBetween(String value1, String value2) {
			addCriterion("UPDATE_USER not between", value1, value2, "updateUser");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeIsNull() {
			addCriterion("UPDATE_TIME is null");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeIsNotNull() {
			addCriterion("UPDATE_TIME is not null");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeEqualTo(Date value) {
			addCriterion("UPDATE_TIME =", value, "updateTime");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeNotEqualTo(Date value) {
			addCriterion("UPDATE_TIME <>", value, "updateTime");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeGreaterThan(Date value) {
			addCriterion("UPDATE_TIME >", value, "updateTime");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeGreaterThanOrEqualTo(Date value) {
			addCriterion("UPDATE_TIME >=", value, "updateTime");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeLessThan(Date value) {
			addCriterion("UPDATE_TIME <", value, "updateTime");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeLessThanOrEqualTo(Date value) {
			addCriterion("UPDATE_TIME <=", value, "updateTime");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeIn(List<Date> values) {
			addCriterion("UPDATE_TIME in", values, "updateTime");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeNotIn(List<Date> values) {
			addCriterion("UPDATE_TIME not in", values, "updateTime");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeBetween(Date value1, Date value2) {
			addCriterion("UPDATE_TIME between", value1, value2, "updateTime");
			return (Criteria) this;
		}

		public Criteria andUpdateTimeNotBetween(Date value1, Date value2) {
			addCriterion("UPDATE_TIME not between", value1, value2, "updateTime");
			return (Criteria) this;
		}
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table DDC_APPLICATION
	 * @mbggenerated  Wed Jun 08 15:30:29 CST 2016
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

	/**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table DDC_APPLICATION
     *
     * @mbggenerated do_not_delete_during_merge Fri May 20 10:52:20 CST 2016
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }
}