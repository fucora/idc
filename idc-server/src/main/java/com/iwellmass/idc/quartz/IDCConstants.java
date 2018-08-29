package com.iwellmass.idc.quartz;

import java.time.LocalDateTime;

public interface IDCConstants {

	// ~~ IDC quartz plugin ~~
	public static final String TABLE_SENTINEL = "t_idc_sentinel";
	public static final String COL_SENTINEL_TASK_ID = "task_id";
	public static final String COL_SENTINEL_GROUP_ID = "groud_id";
	public static final String COL_SENTINEL_LOAD_DATE = "load_date";
	public static final String COL_SENTINEL_STATUS = "status";
	
	// ~~ context value keys ~~
	/** 实例 ID */
	public static final IDCContextKey<String> CONTEXT_INSTANCE_ID = IDCContextKey.def("job.context.instanceId", String.class);
	/** 获取业务日期 */
	public static final IDCContextKey<LocalDateTime> CONTEXT_LOAD_DATE = IDCContextKey.def("job.context.loadDate", LocalDateTime.class);
	/** 是否跳过本次执行 */
	public static final IDCContextKey<Boolean> JOB_EXECUTION_SKIP = IDCContextKey.def("job.execution.skip", Boolean.class).defaultValue(false);
	
}
