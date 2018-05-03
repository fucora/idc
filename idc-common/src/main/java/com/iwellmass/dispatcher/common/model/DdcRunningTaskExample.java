package com.iwellmass.dispatcher.common.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DdcRunningTaskExample {
    /**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	protected String orderByClause;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	protected boolean distinct;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	protected List<Criteria> oredCriteria;

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	public DdcRunningTaskExample() {
		oredCriteria = new ArrayList<Criteria>();
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	public String getOrderByClause() {
		return orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	public Criteria createCriteria() {
		Criteria criteria = createCriteriaInternal();
		if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
	 */
	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
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

		public Criteria andExecuteIdIsNull() {
			addCriterion("EXECUTE_ID is null");
			return (Criteria) this;
		}

		public Criteria andExecuteIdIsNotNull() {
			addCriterion("EXECUTE_ID is not null");
			return (Criteria) this;
		}

		public Criteria andExecuteIdEqualTo(Long value) {
			addCriterion("EXECUTE_ID =", value, "executeId");
			return (Criteria) this;
		}

		public Criteria andExecuteIdNotEqualTo(Long value) {
			addCriterion("EXECUTE_ID <>", value, "executeId");
			return (Criteria) this;
		}

		public Criteria andExecuteIdGreaterThan(Long value) {
			addCriterion("EXECUTE_ID >", value, "executeId");
			return (Criteria) this;
		}

		public Criteria andExecuteIdGreaterThanOrEqualTo(Long value) {
			addCriterion("EXECUTE_ID >=", value, "executeId");
			return (Criteria) this;
		}

		public Criteria andExecuteIdLessThan(Long value) {
			addCriterion("EXECUTE_ID <", value, "executeId");
			return (Criteria) this;
		}

		public Criteria andExecuteIdLessThanOrEqualTo(Long value) {
			addCriterion("EXECUTE_ID <=", value, "executeId");
			return (Criteria) this;
		}

		public Criteria andExecuteIdIn(List<Long> values) {
			addCriterion("EXECUTE_ID in", values, "executeId");
			return (Criteria) this;
		}

		public Criteria andExecuteIdNotIn(List<Long> values) {
			addCriterion("EXECUTE_ID not in", values, "executeId");
			return (Criteria) this;
		}

		public Criteria andExecuteIdBetween(Long value1, Long value2) {
			addCriterion("EXECUTE_ID between", value1, value2, "executeId");
			return (Criteria) this;
		}

		public Criteria andExecuteIdNotBetween(Long value1, Long value2) {
			addCriterion("EXECUTE_ID not between", value1, value2, "executeId");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdIsNull() {
			addCriterion("WORKFLOW_EXECUTE_ID is null");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdIsNotNull() {
			addCriterion("WORKFLOW_EXECUTE_ID is not null");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdEqualTo(Long value) {
			addCriterion("WORKFLOW_EXECUTE_ID =", value, "workflowExecuteId");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdNotEqualTo(Long value) {
			addCriterion("WORKFLOW_EXECUTE_ID <>", value, "workflowExecuteId");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdGreaterThan(Long value) {
			addCriterion("WORKFLOW_EXECUTE_ID >", value, "workflowExecuteId");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdGreaterThanOrEqualTo(Long value) {
			addCriterion("WORKFLOW_EXECUTE_ID >=", value, "workflowExecuteId");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdLessThan(Long value) {
			addCriterion("WORKFLOW_EXECUTE_ID <", value, "workflowExecuteId");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdLessThanOrEqualTo(Long value) {
			addCriterion("WORKFLOW_EXECUTE_ID <=", value, "workflowExecuteId");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdIn(List<Long> values) {
			addCriterion("WORKFLOW_EXECUTE_ID in", values, "workflowExecuteId");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdNotIn(List<Long> values) {
			addCriterion("WORKFLOW_EXECUTE_ID not in", values, "workflowExecuteId");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdBetween(Long value1, Long value2) {
			addCriterion("WORKFLOW_EXECUTE_ID between", value1, value2, "workflowExecuteId");
			return (Criteria) this;
		}

		public Criteria andWorkflowExecuteIdNotBetween(Long value1, Long value2) {
			addCriterion("WORKFLOW_EXECUTE_ID not between", value1, value2, "workflowExecuteId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdIsNull() {
			addCriterion("EXECUTE_BATCH_ID is null");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdIsNotNull() {
			addCriterion("EXECUTE_BATCH_ID is not null");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdEqualTo(String value) {
			addCriterion("EXECUTE_BATCH_ID =", value, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdNotEqualTo(String value) {
			addCriterion("EXECUTE_BATCH_ID <>", value, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdGreaterThan(String value) {
			addCriterion("EXECUTE_BATCH_ID >", value, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdGreaterThanOrEqualTo(String value) {
			addCriterion("EXECUTE_BATCH_ID >=", value, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdLessThan(String value) {
			addCriterion("EXECUTE_BATCH_ID <", value, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdLessThanOrEqualTo(String value) {
			addCriterion("EXECUTE_BATCH_ID <=", value, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdLike(String value) {
			addCriterion("EXECUTE_BATCH_ID like", value, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdNotLike(String value) {
			addCriterion("EXECUTE_BATCH_ID not like", value, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdIn(List<String> values) {
			addCriterion("EXECUTE_BATCH_ID in", values, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdNotIn(List<String> values) {
			addCriterion("EXECUTE_BATCH_ID not in", values, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdBetween(String value1, String value2) {
			addCriterion("EXECUTE_BATCH_ID between", value1, value2, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andExecuteBatchIdNotBetween(String value1, String value2) {
			addCriterion("EXECUTE_BATCH_ID not between", value1, value2, "executeBatchId");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdIsNull() {
			addCriterion("WORKFLOW_ID is null");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdIsNotNull() {
			addCriterion("WORKFLOW_ID is not null");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdEqualTo(Integer value) {
			addCriterion("WORKFLOW_ID =", value, "workflowId");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdNotEqualTo(Integer value) {
			addCriterion("WORKFLOW_ID <>", value, "workflowId");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdGreaterThan(Integer value) {
			addCriterion("WORKFLOW_ID >", value, "workflowId");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdGreaterThanOrEqualTo(Integer value) {
			addCriterion("WORKFLOW_ID >=", value, "workflowId");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdLessThan(Integer value) {
			addCriterion("WORKFLOW_ID <", value, "workflowId");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdLessThanOrEqualTo(Integer value) {
			addCriterion("WORKFLOW_ID <=", value, "workflowId");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdIn(List<Integer> values) {
			addCriterion("WORKFLOW_ID in", values, "workflowId");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdNotIn(List<Integer> values) {
			addCriterion("WORKFLOW_ID not in", values, "workflowId");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdBetween(Integer value1, Integer value2) {
			addCriterion("WORKFLOW_ID between", value1, value2, "workflowId");
			return (Criteria) this;
		}

		public Criteria andWorkflowIdNotBetween(Integer value1, Integer value2) {
			addCriterion("WORKFLOW_ID not between", value1, value2, "workflowId");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeIsNull() {
			addCriterion("DISPATCH_TIME is null");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeIsNotNull() {
			addCriterion("DISPATCH_TIME is not null");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeEqualTo(Date value) {
			addCriterion("DISPATCH_TIME =", value, "dispatchTime");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeNotEqualTo(Date value) {
			addCriterion("DISPATCH_TIME <>", value, "dispatchTime");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeGreaterThan(Date value) {
			addCriterion("DISPATCH_TIME >", value, "dispatchTime");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeGreaterThanOrEqualTo(Date value) {
			addCriterion("DISPATCH_TIME >=", value, "dispatchTime");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeLessThan(Date value) {
			addCriterion("DISPATCH_TIME <", value, "dispatchTime");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeLessThanOrEqualTo(Date value) {
			addCriterion("DISPATCH_TIME <=", value, "dispatchTime");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeIn(List<Date> values) {
			addCriterion("DISPATCH_TIME in", values, "dispatchTime");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeNotIn(List<Date> values) {
			addCriterion("DISPATCH_TIME not in", values, "dispatchTime");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeBetween(Date value1, Date value2) {
			addCriterion("DISPATCH_TIME between", value1, value2, "dispatchTime");
			return (Criteria) this;
		}

		public Criteria andDispatchTimeNotBetween(Date value1, Date value2) {
			addCriterion("DISPATCH_TIME not between", value1, value2, "dispatchTime");
			return (Criteria) this;
		}

		public Criteria andDispatchCountIsNull() {
			addCriterion("DISPATCH_COUNT is null");
			return (Criteria) this;
		}

		public Criteria andDispatchCountIsNotNull() {
			addCriterion("DISPATCH_COUNT is not null");
			return (Criteria) this;
		}

		public Criteria andDispatchCountEqualTo(Integer value) {
			addCriterion("DISPATCH_COUNT =", value, "dispatchCount");
			return (Criteria) this;
		}

		public Criteria andDispatchCountNotEqualTo(Integer value) {
			addCriterion("DISPATCH_COUNT <>", value, "dispatchCount");
			return (Criteria) this;
		}

		public Criteria andDispatchCountGreaterThan(Integer value) {
			addCriterion("DISPATCH_COUNT >", value, "dispatchCount");
			return (Criteria) this;
		}

		public Criteria andDispatchCountGreaterThanOrEqualTo(Integer value) {
			addCriterion("DISPATCH_COUNT >=", value, "dispatchCount");
			return (Criteria) this;
		}

		public Criteria andDispatchCountLessThan(Integer value) {
			addCriterion("DISPATCH_COUNT <", value, "dispatchCount");
			return (Criteria) this;
		}

		public Criteria andDispatchCountLessThanOrEqualTo(Integer value) {
			addCriterion("DISPATCH_COUNT <=", value, "dispatchCount");
			return (Criteria) this;
		}

		public Criteria andDispatchCountIn(List<Integer> values) {
			addCriterion("DISPATCH_COUNT in", values, "dispatchCount");
			return (Criteria) this;
		}

		public Criteria andDispatchCountNotIn(List<Integer> values) {
			addCriterion("DISPATCH_COUNT not in", values, "dispatchCount");
			return (Criteria) this;
		}

		public Criteria andDispatchCountBetween(Integer value1, Integer value2) {
			addCriterion("DISPATCH_COUNT between", value1, value2, "dispatchCount");
			return (Criteria) this;
		}

		public Criteria andDispatchCountNotBetween(Integer value1, Integer value2) {
			addCriterion("DISPATCH_COUNT not between", value1, value2, "dispatchCount");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeIsNull() {
			addCriterion("RECEIVE_TIME is null");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeIsNotNull() {
			addCriterion("RECEIVE_TIME is not null");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeEqualTo(Date value) {
			addCriterion("RECEIVE_TIME =", value, "receiveTime");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeNotEqualTo(Date value) {
			addCriterion("RECEIVE_TIME <>", value, "receiveTime");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeGreaterThan(Date value) {
			addCriterion("RECEIVE_TIME >", value, "receiveTime");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeGreaterThanOrEqualTo(Date value) {
			addCriterion("RECEIVE_TIME >=", value, "receiveTime");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeLessThan(Date value) {
			addCriterion("RECEIVE_TIME <", value, "receiveTime");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeLessThanOrEqualTo(Date value) {
			addCriterion("RECEIVE_TIME <=", value, "receiveTime");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeIn(List<Date> values) {
			addCriterion("RECEIVE_TIME in", values, "receiveTime");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeNotIn(List<Date> values) {
			addCriterion("RECEIVE_TIME not in", values, "receiveTime");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeBetween(Date value1, Date value2) {
			addCriterion("RECEIVE_TIME between", value1, value2, "receiveTime");
			return (Criteria) this;
		}

		public Criteria andReceiveTimeNotBetween(Date value1, Date value2) {
			addCriterion("RECEIVE_TIME not between", value1, value2, "receiveTime");
			return (Criteria) this;
		}

		public Criteria andStartTimeIsNull() {
			addCriterion("START_TIME is null");
			return (Criteria) this;
		}

		public Criteria andStartTimeIsNotNull() {
			addCriterion("START_TIME is not null");
			return (Criteria) this;
		}

		public Criteria andStartTimeEqualTo(Date value) {
			addCriterion("START_TIME =", value, "startTime");
			return (Criteria) this;
		}

		public Criteria andStartTimeNotEqualTo(Date value) {
			addCriterion("START_TIME <>", value, "startTime");
			return (Criteria) this;
		}

		public Criteria andStartTimeGreaterThan(Date value) {
			addCriterion("START_TIME >", value, "startTime");
			return (Criteria) this;
		}

		public Criteria andStartTimeGreaterThanOrEqualTo(Date value) {
			addCriterion("START_TIME >=", value, "startTime");
			return (Criteria) this;
		}

		public Criteria andStartTimeLessThan(Date value) {
			addCriterion("START_TIME <", value, "startTime");
			return (Criteria) this;
		}

		public Criteria andStartTimeLessThanOrEqualTo(Date value) {
			addCriterion("START_TIME <=", value, "startTime");
			return (Criteria) this;
		}

		public Criteria andStartTimeIn(List<Date> values) {
			addCriterion("START_TIME in", values, "startTime");
			return (Criteria) this;
		}

		public Criteria andStartTimeNotIn(List<Date> values) {
			addCriterion("START_TIME not in", values, "startTime");
			return (Criteria) this;
		}

		public Criteria andStartTimeBetween(Date value1, Date value2) {
			addCriterion("START_TIME between", value1, value2, "startTime");
			return (Criteria) this;
		}

		public Criteria andStartTimeNotBetween(Date value1, Date value2) {
			addCriterion("START_TIME not between", value1, value2, "startTime");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeIsNull() {
			addCriterion("COMPLETE_TIME is null");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeIsNotNull() {
			addCriterion("COMPLETE_TIME is not null");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeEqualTo(Date value) {
			addCriterion("COMPLETE_TIME =", value, "completeTime");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeNotEqualTo(Date value) {
			addCriterion("COMPLETE_TIME <>", value, "completeTime");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeGreaterThan(Date value) {
			addCriterion("COMPLETE_TIME >", value, "completeTime");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeGreaterThanOrEqualTo(Date value) {
			addCriterion("COMPLETE_TIME >=", value, "completeTime");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeLessThan(Date value) {
			addCriterion("COMPLETE_TIME <", value, "completeTime");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeLessThanOrEqualTo(Date value) {
			addCriterion("COMPLETE_TIME <=", value, "completeTime");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeIn(List<Date> values) {
			addCriterion("COMPLETE_TIME in", values, "completeTime");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeNotIn(List<Date> values) {
			addCriterion("COMPLETE_TIME not in", values, "completeTime");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeBetween(Date value1, Date value2) {
			addCriterion("COMPLETE_TIME between", value1, value2, "completeTime");
			return (Criteria) this;
		}

		public Criteria andCompleteTimeNotBetween(Date value1, Date value2) {
			addCriterion("COMPLETE_TIME not between", value1, value2, "completeTime");
			return (Criteria) this;
		}

		public Criteria andTimeoutIsNull() {
			addCriterion("TIMEOUT is null");
			return (Criteria) this;
		}

		public Criteria andTimeoutIsNotNull() {
			addCriterion("TIMEOUT is not null");
			return (Criteria) this;
		}

		public Criteria andTimeoutEqualTo(Long value) {
			addCriterion("TIMEOUT =", value, "timeout");
			return (Criteria) this;
		}

		public Criteria andTimeoutNotEqualTo(Long value) {
			addCriterion("TIMEOUT <>", value, "timeout");
			return (Criteria) this;
		}

		public Criteria andTimeoutGreaterThan(Long value) {
			addCriterion("TIMEOUT >", value, "timeout");
			return (Criteria) this;
		}

		public Criteria andTimeoutGreaterThanOrEqualTo(Long value) {
			addCriterion("TIMEOUT >=", value, "timeout");
			return (Criteria) this;
		}

		public Criteria andTimeoutLessThan(Long value) {
			addCriterion("TIMEOUT <", value, "timeout");
			return (Criteria) this;
		}

		public Criteria andTimeoutLessThanOrEqualTo(Long value) {
			addCriterion("TIMEOUT <=", value, "timeout");
			return (Criteria) this;
		}

		public Criteria andTimeoutIn(List<Long> values) {
			addCriterion("TIMEOUT in", values, "timeout");
			return (Criteria) this;
		}

		public Criteria andTimeoutNotIn(List<Long> values) {
			addCriterion("TIMEOUT not in", values, "timeout");
			return (Criteria) this;
		}

		public Criteria andTimeoutBetween(Long value1, Long value2) {
			addCriterion("TIMEOUT between", value1, value2, "timeout");
			return (Criteria) this;
		}

		public Criteria andTimeoutNotBetween(Long value1, Long value2) {
			addCriterion("TIMEOUT not between", value1, value2, "timeout");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeIsNull() {
			addCriterion("TIMEOUT_TIME is null");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeIsNotNull() {
			addCriterion("TIMEOUT_TIME is not null");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeEqualTo(Date value) {
			addCriterion("TIMEOUT_TIME =", value, "timeoutTime");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeNotEqualTo(Date value) {
			addCriterion("TIMEOUT_TIME <>", value, "timeoutTime");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeGreaterThan(Date value) {
			addCriterion("TIMEOUT_TIME >", value, "timeoutTime");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeGreaterThanOrEqualTo(Date value) {
			addCriterion("TIMEOUT_TIME >=", value, "timeoutTime");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeLessThan(Date value) {
			addCriterion("TIMEOUT_TIME <", value, "timeoutTime");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeLessThanOrEqualTo(Date value) {
			addCriterion("TIMEOUT_TIME <=", value, "timeoutTime");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeIn(List<Date> values) {
			addCriterion("TIMEOUT_TIME in", values, "timeoutTime");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeNotIn(List<Date> values) {
			addCriterion("TIMEOUT_TIME not in", values, "timeoutTime");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeBetween(Date value1, Date value2) {
			addCriterion("TIMEOUT_TIME between", value1, value2, "timeoutTime");
			return (Criteria) this;
		}

		public Criteria andTimeoutTimeNotBetween(Date value1, Date value2) {
			addCriterion("TIMEOUT_TIME not between", value1, value2, "timeoutTime");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryIsNull() {
			addCriterion("TIMEOUT_RETRY is null");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryIsNotNull() {
			addCriterion("TIMEOUT_RETRY is not null");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryEqualTo(Integer value) {
			addCriterion("TIMEOUT_RETRY =", value, "timeoutRetry");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryNotEqualTo(Integer value) {
			addCriterion("TIMEOUT_RETRY <>", value, "timeoutRetry");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryGreaterThan(Integer value) {
			addCriterion("TIMEOUT_RETRY >", value, "timeoutRetry");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryGreaterThanOrEqualTo(Integer value) {
			addCriterion("TIMEOUT_RETRY >=", value, "timeoutRetry");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryLessThan(Integer value) {
			addCriterion("TIMEOUT_RETRY <", value, "timeoutRetry");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryLessThanOrEqualTo(Integer value) {
			addCriterion("TIMEOUT_RETRY <=", value, "timeoutRetry");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryIn(List<Integer> values) {
			addCriterion("TIMEOUT_RETRY in", values, "timeoutRetry");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryNotIn(List<Integer> values) {
			addCriterion("TIMEOUT_RETRY not in", values, "timeoutRetry");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryBetween(Integer value1, Integer value2) {
			addCriterion("TIMEOUT_RETRY between", value1, value2, "timeoutRetry");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryNotBetween(Integer value1, Integer value2) {
			addCriterion("TIMEOUT_RETRY not between", value1, value2, "timeoutRetry");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesIsNull() {
			addCriterion("TIMEOUT_RETRY_TIMES is null");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesIsNotNull() {
			addCriterion("TIMEOUT_RETRY_TIMES is not null");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesEqualTo(Integer value) {
			addCriterion("TIMEOUT_RETRY_TIMES =", value, "timeoutRetryTimes");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesNotEqualTo(Integer value) {
			addCriterion("TIMEOUT_RETRY_TIMES <>", value, "timeoutRetryTimes");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesGreaterThan(Integer value) {
			addCriterion("TIMEOUT_RETRY_TIMES >", value, "timeoutRetryTimes");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesGreaterThanOrEqualTo(Integer value) {
			addCriterion("TIMEOUT_RETRY_TIMES >=", value, "timeoutRetryTimes");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesLessThan(Integer value) {
			addCriterion("TIMEOUT_RETRY_TIMES <", value, "timeoutRetryTimes");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesLessThanOrEqualTo(Integer value) {
			addCriterion("TIMEOUT_RETRY_TIMES <=", value, "timeoutRetryTimes");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesIn(List<Integer> values) {
			addCriterion("TIMEOUT_RETRY_TIMES in", values, "timeoutRetryTimes");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesNotIn(List<Integer> values) {
			addCriterion("TIMEOUT_RETRY_TIMES not in", values, "timeoutRetryTimes");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesBetween(Integer value1, Integer value2) {
			addCriterion("TIMEOUT_RETRY_TIMES between", value1, value2, "timeoutRetryTimes");
			return (Criteria) this;
		}

		public Criteria andTimeoutRetryTimesNotBetween(Integer value1, Integer value2) {
			addCriterion("TIMEOUT_RETRY_TIMES not between", value1, value2, "timeoutRetryTimes");
			return (Criteria) this;
		}
	}

	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table DDC_RUNNING_TASK
	 * @mbggenerated  Sun Jun 12 15:16:43 CST 2016
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
     * This class corresponds to the database table DDC_RUNNING_TASK
     *
     * @mbggenerated do_not_delete_during_merge Sun Jun 12 14:24:50 CST 2016
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }
}