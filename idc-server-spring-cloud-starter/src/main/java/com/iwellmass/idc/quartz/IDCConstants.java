package com.iwellmass.idc.quartz;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.iwellmass.idc.model.ScheduleType;

public interface IDCConstants {
	
	public static final SimpleDateFormat DEFAULT_LOAD_DATE_DF = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final DateTimeFormatter DEFAULT_LOAD_DATE_DTF = DateTimeFormatter.ofPattern(DEFAULT_LOAD_DATE_DF.toPattern());

	// ~~ IDC quartz plugin ~~
	public static final String VERSION = "2.0";
	public static final String TABLE_SENTINEL = "t_idc_sentinel";
	public static final String COL_SENTINEL_TRIGGER_NAME = "trigger_name";
	public static final String COL_SENTINEL_TRIGGER_GROUP = "trigger_group";
	public static final String COL_SENTINEL_SHOULD_FIRE_TIME = "should_fire_time";
	public static final String COL_SENTINEL_STATUS = "status";
	
	// ~~ 创建任务 ~~
	public static final IDCContextKey<String> IDC_JOB_VALUE = IDCContextKey.def("idc.job.value", String.class);
	
	// ~~ 调度信息 ~~
	/** ScheduleType */
	public static final IDCContextKey<ScheduleType> IDC_SCHEDULE_TYPE = IDCContextKey.def("idc.job.scheduleType", ScheduleType.class);
	/** TaskId */
	public static final IDCContextKey<String> IDC_TASK_ID = IDCContextKey.def("idc.job.taskId", String.class);
	/** GroupId */
	public static final IDCContextKey<String> IDC_GROUP_ID = IDCContextKey.def("idc.job.groupId", String.class);
	/** Parameter */
	public static final IDCContextKey<String> IDC_PARAMETER = IDCContextKey.def("job.context.parameter", String.class);
	
	// ~~ runtime values ~~
	/** 实例 ID */
	public static final IDCContextKey<Integer> CONTEXT_INSTANCE_ID = IDCContextKey.def("job.context.instanceId", Integer.class);
	/** 获取业务日期 */
	public static final IDCContextKey<LocalDateTime> CONTEXT_LOAD_DATE = IDCContextKey.def("job.context.loadDate", LocalDateTime.class);
	/** 是否跳过本次执行 */
	public static final IDCContextKey<Boolean> CONTEXT_SKIP = IDCContextKey.def("job.context.skip", Boolean.class).defaultValue(false);
	/** 是否重跑实例 */
	public static final IDCContextKey<Boolean> CONTEXT_REDO = IDCContextKey.def("job.context.redo", Boolean.class).defaultValue(false);

	///** JobInstanceType */
	//public static final IDCContextKey<JobInstanceType> CONTEXT_JOB_INSTANCE_TYPE = IDCContextKey.def("job.context.jobInstanceType", JobInstanceType.class);
	
}
