package com.iwellmass.idc;

import java.util.List;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.model.CronType;

import io.swagger.annotations.ApiModelProperty;

public interface JobEnv {

	@ApiModelProperty("执行ID")
	public String getInstanceId();

	@ApiModelProperty("计划ID")
	public String getJobId();

	@ApiModelProperty("计划名称")
	public String getJobName();

	@ApiModelProperty("业务日期")
	public String getLoadDate();

	@ApiModelProperty("周期类型")
	public CronType getScheduleType();

	@ApiModelProperty("参数设置")
	public List<ExecParam> getParameter();

	@ApiModelProperty("执行方式")
	public String getDispatchType();

	@ApiModelProperty("本次调度日期")
	public Long getShouldFireTime();

	@ApiModelProperty("上次调度日期")
	public Long getPrevFireTime();

	@ApiModelProperty("Task ID")
	public String getTaskId();
	
	public String getContentType();


}
