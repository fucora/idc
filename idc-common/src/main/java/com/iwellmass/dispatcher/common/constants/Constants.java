package com.iwellmass.dispatcher.common.constants;

public class Constants {
	
	public static final int TASK_CATEGORY_BASIC = 1;
	
	public static final int TASK_CATEGORY_WORKFLOW = 2;
	
	public static final int TASK_TYPE_CRON = 11;
	
	public static final int TASK_TYPE_SUBTASK = 12;
	
	public static final int TASK_TYPE_SIMPLE = 13;
	
	public static final int TASK_TYPE_FLOW = 21;
	
	public static final int TASK_STATUS_DISABLED = 0;
	
	public static final int TASK_STATUS_ENABLED = 1;
	
	public static final int TASK_TRIGGER_TYPE_SYSTEM = 0;
	
	public static final int TASK_TRIGGER_TYPE_MAN = 1;
	
	public static final int THREE_SERVER_HEARTBEAT = 3 * 5 * 1000;
	
	public static final int THREE_NODE_HEARTBEAT = 3 * 15 * 1000;
	
	public static final int WORKFOW_OOD = 0;
	
	public static final int WORKFLOW_INUSE = 1;

	public static final int RETRY_THRIFT = 3;

	public static final int RETRY_BEAN = 2;
	
	public static final int TIMEOUT_RETRY = 3;
	
	public static final int AN_HOUR = 60 * 60 * 1000;
	
	public static final int SEVEN_DAY = 7 * 24 * 60 * 60 * 1000;
	
	public static final int TASK_EXECUTE_TYPE_SYSTEM = 0;
	
	public static final int TASK_EXECUTE_TYPE_MANUAL = 1;
	
	public static final String DDC_SCHEDULER_GROUP = "ddc_scheduler_group";
	
	public static final String JOB_SYN_CACHE = "job_syn_cache";
	
	public static final String JOB_NO_DISPOSE_TASK = "job_no_dispose_task";
	
	public static final String JOB_NO_RESPONSE_TASK = "job_no_response_task";
	
	public static final String JOB_NO_DISPOSE_SUBTASK = "job_no_dispose_subtask";
	
	public static final String JOB_NO_RESPONSE_SUBTASK = "job_no_response_subtask";
	
	public static final String JOB_CLEAR_TASK = "job_clear_task";
	
	public static final String JOB_CLEAR_SUBTASK = "job_clear_subtask";
	
	public static final String JOB_CLEAR_STATUS = "job_clear_status";

	public static final String TASK_FIVE_MINUTE_STATISTIC = "task_five_minute_statistic";

	public static final String TASK_HOUR_STATISTIC = "task_hour_statistic";

	public static final String TRIGGER_SYN_CACHE = "trigger_syn_cache";

	public static final String TRIGGER_NO_DISPOSE_TASK = "trigger_no_dispose_task";
	
	public static final String TRIGGER_NO_RESPONSE_TASK = "trigger_no_response_task";
	
	public static final String TRIGGER_NO_DISPOSE_SUBTASK = "trigger_no_dispose_subtask";
	
	public static final String TRIGGER_NO_RESPONSE_SUBTASK = "trigger_no_response_subtask";
	
	public static final String TRIGGER_CLEAR_TASK = "trigger_clear_task";
	
	public static final String TRIGGER_CLEAR_SUBTASK = "trigger_clear_subtask";
	
	public static final String TRIGGER_CLEAR_STATUS = "trigger_clear_status";
	
	public static final String SUBTASK_PREFIX = "ddc_subtask_";
	
	public static final String STRATEGY_TYPE_RANDOM = "random";
	
	public static final String JOB_PREFIX = "job_";

	public static final String TRIGGER_PREFIX = "trigger_";
	
	public static final String ERROR_TYPE = "系统错误";
	
	public static final int TIME_OUT = 3000;

	public static final int DISABLED = 0;
	
	public static final int ENABLED = 1;
	
	public static final int WORKFLOW_START_TASK_ID = -1;
	
	public static final int WORKFLOW_END_TASK_ID = -2;
	
	public static final String ALARM_KEY_TIMEOUT = "timeout";
	
	public static final String ALARM_KEY_OVERERROR = "overerror";
	
	public static final String ALARM_KEY_NOTASK = "notask";
	
	public static final String ALARM_KEY_ERRORKEY = "errorkey";
	
	public static final String ALARM_KEY_NOCLIENT = "noclient";
}
