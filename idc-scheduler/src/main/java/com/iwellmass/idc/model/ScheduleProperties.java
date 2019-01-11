package com.iwellmass.idc.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 调度配置
 */
@Getter
@Setter
public class ScheduleProperties {
	
	@ApiModelProperty("业务ID")
	private String taskId;
	
	@ApiModelProperty("业务域")
	private String taskGroup;

	@ApiModelProperty("任务名称")
	private String jobName;
	
	@ApiModelProperty("描述")
	private String description;
	
	@ApiModelProperty("负责人")
	private String assignee;
	
	@ApiModelProperty("哪一天")
	private List<Integer> days;

	@ApiModelProperty("具体时间")
	@JsonFormat(timezone = "GMT+8", pattern="HH:mm:ss")
	private LocalTime duetime = LocalTime.MIN;

	@ApiModelProperty("周期类型")
	private ScheduleType scheduleType;
	
	@ApiModelProperty("失败重试")
	private Boolean isRetry = true;
	
	@ApiModelProperty("出错时阻塞")
	private Boolean blockOnError = true;
	
	@ApiModelProperty("生效日期 yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private LocalDate startTime;

	@ApiModelProperty("失效日期, yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private LocalDate endTime;
	
	@ApiModelProperty("执行方式")
	private DispatchType dispatchType;
	
	@ApiModelProperty("运行参数")
	private List<ExecParam> parameter;

	@ApiModelProperty("job依赖关系")
    private List<JobDependency> jobDependencies;

	public String toCronExpression() {
		switch (scheduleType) {
		case MONTHLY: {
			Assert.isFalse(Utils.isNullOrEmpty(days), "月调度配置不能为空");
			boolean isLast = days.stream().filter(i -> i < 0).count() == 1;
			if(isLast && days.size() > 1) {
				throw new AppException("最后 N 天不能使用组合配置模式");
			};
			return String.format("%s %s %s %s * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(),
				isLast ? days.get(0) == -1 ? "L" : "L" + (days.get(0) + 1)
					: String.join(",", days.stream().map(String::valueOf).collect(Collectors.toList())));
		}
		case WEEKLY: {
			throw new UnsupportedOperationException("not supported yet");
		}
		case DAILY:
			return String.format("%s %s %s * * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour());
		default:
			throw new AppException("接收的调度类型" + Arrays.asList(ScheduleType.values()));
		}
	}
}
