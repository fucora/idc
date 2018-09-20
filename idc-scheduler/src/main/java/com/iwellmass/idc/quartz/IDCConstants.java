package com.iwellmass.idc.quartz;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public interface IDCConstants {

	public static final SimpleDateFormat DEFAULT_LOAD_DATE_DF = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final DateTimeFormatter DEFAULT_LOAD_DATE_DTF = DateTimeFormatter.ofPattern(DEFAULT_LOAD_DATE_DF.toPattern());

	// ~~ IDC quartz plugin ~~
	public static final String VERSION = "2.0";

	// ~~ IDC 依赖表
	/*public static final String COL_IDC_JOB_NAME = "JOB_NAME";
	public static final String COL_IDC_JOB_GROUP = "JOB_GROUP";
	
	public static final String TABLE_DEPENDENCY = "QRTZ_DEPENDENCY";
	public static final String COL_DEPENDENCY_SRC_JOB_NAME = "SRC_JOB_NAME";
	public static final String COL_DEPENDENCY_SRC_JOB_GROUP = "SRC_JOB_GROUP";
	
	public static final String TABLE_JOB_INSTANCE = "QRTZ_SIGNAL";
	public static final String COL_JOB_INSTANCE_SHOULD_FIRE_TIME = "SHOULD_FIRE_TIME";
	public static final String COL_JOB_INSTANCE_STATUS = "STATUS";*/
	
	public static final String COL_IDC_JOB_NAME = "task_id";
	public static final String COL_IDC_JOB_GROUP = "group_id";
	
	public static final String TABLE_DEPENDENCY = "t_idc_dependency";
	public static final String COL_DEPENDENCY_SRC_JOB_NAME = "src_task_id";
	public static final String COL_DEPENDENCY_SRC_JOB_GROUP = "src_group_id";
	
	public static final String TABLE_JOB_INSTANCE = "t_idc_job_instance";
	public static final String COL_JOB_INSTANCE_SHOULD_FIRE_TIME = "shouldFireTime";
	public static final String COL_JOB_INSTANCE_STATUS = "status";
}
