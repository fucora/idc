package com.iwellmass.idc.scheduler.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.app.vo.TaskRuntimeVO;
import com.iwellmass.idc.app.vo.task.TaskVO;
import com.iwellmass.idc.model.ScheduleType;
import org.quartz.TriggerKey;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.scheduler.util.ExecParamConverter;
import com.iwellmass.idc.scheduler.util.LocalLongConverter;
import com.iwellmass.idc.scheduler.util.MapConverter;
import com.iwellmass.idc.scheduler.util.TaskStateConverter;

import lombok.Getter;
import lombok.Setter;

/**
 * 调度
 */
@Getter
@Setter
@Entity
@IdClass(TaskID.class)
@Table(name = "idc_task")
@SecondaryTable(name = "QRTZ_TRIGGERS", pkJoinColumns = {
        @PrimaryKeyJoinColumn(name = "TRIGGER_NAME", referencedColumnName = "task_name"),
        @PrimaryKeyJoinColumn(name = "TRIGGER_GROUP", referencedColumnName = "task_group"),
})
public class Task extends AbstractTask {

    public static final String GROUP_PRIMARY = "primary";

    // ~~ constructor ~~
    /**
     * 调度组
     */
    @Id
    @Column(name = "task_group")
    private String taskGroup = GROUP_PRIMARY;

    /**
     * 责任人
     */
    @Column(name = "assignee", length = 20)
    private String assignee;

    /**
     * 运行参数(含有表达式)
     */
    @Column(name = "param", columnDefinition = "TEXT")
    @Convert(converter = ExecParamConverter.class)
    private List<ExecParam> params;


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
     * 出错时阻塞
     */
    @Column(name = "block_on_error")
    private Boolean blockOnError;

    /**
     * 生效时间
     */
    @Column(name = "start_datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startDateTime;

    /**
     * 失效时间
     */
    @Column(name = "end_datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endDateTime;

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
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;

    /**
     * 更新时间
     */
    @Column(name = "updatetime")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatetime;

    /**
     * 具体时间
     */
    @Column(name = "duetime")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private LocalTime duetime;

    @Column(table = "QRTZ_TRIGGERS", name = "PREV_FIRE_TIME", insertable = false, updatable = false)
    @Convert(converter = LocalLongConverter.class)
    private LocalDateTime prevFireTime;

    @Column(table = "QRTZ_TRIGGERS", name = "NEXT_FIRE_TIME", insertable = false, updatable = false)
    @Convert(converter = LocalLongConverter.class)
    private LocalDateTime nextFireTime;

    /**
     * attention : the state field only stand for trigger's state can't mean task state.
     * then state is complete the task's the last job may well is running.so we need to twice validate state in taskRunTimeVO
     * see {@link TaskRuntimeVO#state}
     */
    @Column(table = "QRTZ_TRIGGERS", name = "TRIGGER_STATE", insertable = false, updatable = false)
    @Convert(converter = TaskStateConverter.class)
    private TaskState state;

    public Task() {
    }

    public Task(TaskVO vo) {
        this.taskName = vo.getTaskName();
        this.workflowId = vo.getWorkflowId();
        this.taskGroup = Task.GROUP_PRIMARY;
        this.setCreatetime(LocalDateTime.now());
        this.setUpdatetime(this.createtime);
    }

    public TriggerKey getTriggerKey() {
        Objects.requireNonNull(taskName);
        Objects.requireNonNull(taskGroup);
        return new TriggerKey(taskName, taskGroup);
    }
}
