package com.iwellmass.idc.quartz;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public interface IDCConstants {

	SimpleDateFormat DEFAULT_LOAD_DATE_DF = new SimpleDateFormat("yyyyMMddHHmmss");
	DateTimeFormatter DEFAULT_LOAD_DATE_DTF = DateTimeFormatter.ofPattern(DEFAULT_LOAD_DATE_DF.toPattern());
	
	// ~~ IDC quartz plugin ~~
	String VERSION = "2.0";

	String COL_IDC_JOB_NAME = "job_id";
	String COL_IDC_JOB_GROUP = "job_group";
	
	String TABLE_DEPENDENCY = "t_idc_dependency";
	String COL_DEPENDENCY_SRC_JOB_NAME = "src_job_id";
	String COL_DEPENDENCY_SRC_JOB_GROUP = "src_job_group";
	
	String TABLE_JOB_INSTANCE = "t_idc_job_instance";
	String COL_JOB_INSTANCE_SHOULD_FIRE_TIME = "should_fire_time";
	String COL_JOB_INSTANCE_STATUS = "status";
	
	
	// ~~ 依赖表 ~~
	String TABLE_BARRIER = "BARRIER";
	String COL_BARRIER_NAME = "BARRIER_NAME";
	String COL_BARRIER_GROUP = "BARRIER_GROUP";
	String COL_BARRIER_SHOULD_FIRE_TIME = "BARRIER_SHOULD_FIRE_TIME";
	String COL_BARRIER_STATE = "STATE";
	
	
}
