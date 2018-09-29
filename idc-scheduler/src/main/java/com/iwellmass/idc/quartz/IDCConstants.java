package com.iwellmass.idc.quartz;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public interface IDCConstants {

	SimpleDateFormat DEFAULT_LOAD_DATE_DF = new SimpleDateFormat("yyyyMMddHHmmss");
	DateTimeFormatter DEFAULT_LOAD_DATE_DTF = DateTimeFormatter.ofPattern(DEFAULT_LOAD_DATE_DF.toPattern());

	// ~~ IDC quartz plugin ~~
	String VERSION = "2.0";

	// ~~ IDC 依赖表
	String COL_IDC_JOB_NAME = "JOB_NAME";
	String COL_IDC_JOB_GROUP = "JOB_GROUP";
	
	String TABLE_DEPENDENCY = "QRTZ_DEPENDENCY";
	String COL_DEPENDENCY_SRC_JOB_NAME = "SRC_JOB_NAME";
	String COL_DEPENDENCY_SRC_JOB_GROUP = "SRC_JOB_GROUP";
	
	String TABLE_JOB_INSTANCE = "QRTZ_SIGNAL";
	String COL_JOB_INSTANCE_SHOULD_FIRE_TIME = "SHOULD_FIRE_TIME";
	String COL_JOB_INSTANCE_STATUS = "STATUS";
	
	/*String COL_IDC_JOB_NAME = "job_id";
	String COL_IDC_JOB_GROUP = "job_group";
	
	String TABLE_DEPENDENCY = "t_idc_dependency";
	String COL_DEPENDENCY_SRC_JOB_NAME = "src_job_id";
	String COL_DEPENDENCY_SRC_JOB_GROUP = "src_job_group";
	
	String TABLE_JOB_INSTANCE = "t_idc_job_instance";
	String COL_JOB_INSTANCE_SHOULD_FIRE_TIME = "should_fire_time";
	String COL_JOB_INSTANCE_STATUS = "status";*/
}
