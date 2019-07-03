package com.iwellmass.idc.scheduler.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

import org.quartz.TriggerKey;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.scheduler.util.ExecParamConverter;
import com.iwellmass.idc.scheduler.util.MapConverter;
import com.iwellmass.idc.scheduler.util.TaskStateConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(TaskID.class)
@Table(name = "idc_task")
@SecondaryTable(name = "QRTZ_TRIGGERS", pkJoinColumns = {
	@PrimaryKeyJoinColumn(name = "TRIGGER_NAME", referencedColumnName = "name"),
	@PrimaryKeyJoinColumn(name = "TRIGGER_GROUP", referencedColumnName = "group"),
})
public class Task extends AbstractTask {

	public static final String GROUP_PRIMARY = "primary";
	
	// ~~ constructor ~~
	/**
	 * 计划名称
	 */
	@Id
	@Column(name = "name")
	private String name;
	
	/**
	 * 计划组
	 */
	@Id
	@Column(name = "group")
	private String group;

	/**
	 * 业务类型
	 */
	@Column(name = "content_type")
	private String contentType;
	
	/**
	 * 责任人
	 */
	@Column(name = "assignee", length = 20)
	private String assignee;

	/**
	 * 运行参数
	 */
	@Column(name = "param", columnDefinition = "TEXT")
	@Convert(converter = ExecParamConverter.class)
	private List<ExecParam> param;
	
	
	// ~~ others ~~
	
	/**
	 * 调度方式
	 */
	@Column(name = "schedule_type")
	@Enumerated(EnumType.STRING)
	private ScheduleType scheduleType;
	
	/**
	 * 出错重试
	 */
	@Column(name = "is_retry")
	private Boolean isRetry;

	/**
	 * 生效时间
	 */
	@Column(name = "starttime")
	private LocalDateTime starttime;

	/**
	 * 失效时间
	 */
	@Column(name = "endtime")
	private LocalDateTime endtime;


	/**
	 * 其他参数（反显前端用）
	 */
	@Column(name = "props", columnDefinition = "TEXT")
	@Convert(converter = MapConverter.class)
	private Map<String, Object> props;

	/**
	 * 创建时间
	 */
	@Column(name = "createtime")
	private LocalDateTime createtime;

	/**
	 * 更新时间
	 */
	@Column(name = "updatetime")
	private LocalDateTime updatetime;

	@Column(table="QRTZ_TRIGGERS", name = "PREV_FIRE_TIME", insertable = false, updatable = false)
	private LocalDateTime prevFireTime;

	@Column(table="QRTZ_TRIGGERS", name = "NEXT_FIRE_TIME", insertable = false, updatable = false)
	private LocalDateTime nextFireTime;

	@Column(table="QRTZ_TRIGGERS", name = "TRIGGER_STATE", insertable = false, updatable = false)
	@Convert(converter = TaskStateConverter.class)
	private TaskState state;
	
	public Task() {
		this.group = GROUP_PRIMARY;
	}

	public Task(String name, String taskId, String domain) {
		this();
		this.name = name;
		this.taskId = taskId;
		this.domain = domain;
		this.setCreatetime(LocalDateTime.now());
		this.setUpdatetime(this.createtime);
	}

	public TriggerKey getTriggerKey() {
		return new TriggerKey(name, "idc-workflow");
	}

	public void clear() {
	}
}
