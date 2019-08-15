package com.iwellmass.idc.app.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.scheduler.model.JobState;
import com.iwellmass.idc.scheduler.model.TaskType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author nobita
 * @email nobita0522@qq.com
 * @date 2019/7/30 10:51
 * @description
 */
@Getter
@Setter
public class NodeJobVO {

    private String id;

    private String taskId;

    private String nodeId;

    private String mainId;

    private String container;

    private TaskType taskType;

    private String contentType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime starttime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatetime;

    private JobState state;

    private String taskName;
}
