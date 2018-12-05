package com.iwellmass.idc.quartz;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public interface IDCConstants  {

	SimpleDateFormat DEFAULT_LOAD_DATE_DF = new SimpleDateFormat("yyyyMMddHHmmss");
	DateTimeFormatter DEFAULT_LOAD_DATE_DTF = DateTimeFormatter.ofPattern(DEFAULT_LOAD_DATE_DF.toPattern());

	// ~~ IDC quartz plugin ~~
	String VERSION = "2.1.0";

	// ~~ barrier 表 ~~
	String TABLE_BARRIER = "t_idc_barrier";
	String COL_IDC_JOB_NAME = "job_id";
	String COL_IDC_JOB_GROUP = "job_group";
	String COL_BARRIER_NAME = "barrier_id";
	String COL_BARRIER_GROUP = "barrier_group";
	String COL_BARRIER_SHOULD_FIRE_TIME = "barrier_should_fire_time";
	String COL_BARRIER_STATE = "state";
}
