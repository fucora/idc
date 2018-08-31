package com.iwellmass.idc.quartz;

import java.time.LocalDateTime;

import com.iwellmass.idc.model.JobInstanceType;

public interface IDCConstants {

	// ~~ IDC quartz plugin ~~
	public static final String VERSION = "2.0";
	public static final String TABLE_SENTINEL = "t_idc_sentinel";
	public static final String COL_SENTINEL_TRIGGER_NAME = "trigger_name";
	public static final String COL_SENTINEL_TRIGGER_GROUP = "trigger_group";
	public static final String COL_SENTINEL_SHOULD_FIRE_TIME = "should_fire_time";
	public static final String COL_SENTINEL_STATUS = "status";
	
	// ~~ context value keys ~~
	/** 实例 ID */
	public static final IDCContextKey<Integer> CONTEXT_INSTANCE_ID = IDCContextKey.def("job.context.instanceId", Integer.class);
	/** 获取业务日期 */
	public static final IDCContextKey<LocalDateTime> CONTEXT_LOAD_DATE = IDCContextKey.def("job.context.loadDate", LocalDateTime.class);
	/** TaskId */
	public static final IDCContextKey<String> CONTEXT_TASK_ID = IDCContextKey.def("job.context.taskId", String.class);
	/** GroupId */
	public static final IDCContextKey<String> CONTEXT_GROUP_ID = IDCContextKey.def("job.context.groupId", String.class);
	/** JobInstanceType */
	public static final IDCContextKey<JobInstanceType> CONTEXT_JOB_INSTANCE_TYPE = IDCContextKey.def("job.context.jobInstanceType", JobInstanceType.class);
	/** 是否跳过本次执行 */
	public static final IDCContextKey<Boolean> JOB_EXECUTION_SKIP = IDCContextKey.def("job.execution.skip", Boolean.class).defaultValue(false);
	
}
