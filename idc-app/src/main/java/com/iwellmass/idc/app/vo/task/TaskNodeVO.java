package com.iwellmass.idc.app.vo.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.scheduler.model.Job;
import com.iwellmass.idc.scheduler.model.JobState;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskNodeVO {

    @ApiModelProperty("调度计划名，用于前端绘图")
    private String id;

    @ApiModelProperty("调度计划名")
    private String taskName;

    @ApiModelProperty("最新调度实例批次时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private LocalDate batchTime;

    @ApiModelProperty("最新调度实例状态")
    private JobState state;

    @ApiModelProperty("业务日期")
    private String loadDate;

    /**
     * when latestJob is null. we only need to init task'info
     * @param latestJob
     * @param taskName
     */
    public TaskNodeVO(Job latestJob,String taskName) {
        if(latestJob == null) {
            this.id = taskName;
            this.taskName = taskName;
        } else {
            this.id = latestJob.getTaskName();
            this.taskName = latestJob.getTaskName();
            this.batchTime = latestJob.getBatchTime();
            this.state = latestJob.getState();
            this.loadDate = latestJob.getLoadDate();
        }
    }
}
