package com.iwellmass.idc.model;

import com.alibaba.fastjson.JSON;

import io.swagger.annotations.ApiModelProperty;

public interface JobEnv {

	@ApiModelProperty("执行ID")
	public Integer getInstanceId();

	@ApiModelProperty("计划ID")
	public String getJobId();

	@ApiModelProperty("计划Group")
	public String getJobGroup();

	@ApiModelProperty("计划名称")
	public String getJobName();

	@ApiModelProperty("业务日期")
	public String getLoadDate();

	@ApiModelProperty("周期类型")
	public ScheduleType getScheduleType();

	@ApiModelProperty("参数设置")
	public String getParameter();

	@ApiModelProperty("执行方式")
	public DispatchType getDispatchType();

	@ApiModelProperty("本次调度日期")
	public Long getShouldFireTime();

	@ApiModelProperty("上次调度日期")
	public Long getPrevFireTime();

	@ApiModelProperty("Task ID")
	public String getTaskId();

	public default <T> T getParameterObject(Class<T> type) {
		if (this.getParameter() == null || this.getParameter().isEmpty()) {
			return null;
		}
		return JSON.parseObject(this.getParameter(), type);
	}

}
