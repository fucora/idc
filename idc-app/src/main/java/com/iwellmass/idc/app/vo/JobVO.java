package com.iwellmass.idc.app.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.app.vo.graph.GraphVO;
import com.iwellmass.idc.app.vo.task.MergeTaskParamVO;
import com.iwellmass.idc.app.vo.task.TaskVO;
import com.iwellmass.idc.scheduler.model.Job;
import com.iwellmass.idc.scheduler.model.JobState;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobVO {

    @ApiModelProperty("详细node实例")
    private List<NodeJobVO> nodeJobVOS;

    @ApiModelProperty("图形依赖关系")
    private GraphVO graphVO;

    @ApiModelProperty("调度计划基础信息")
    private TaskVO taskVO;

    @ApiModelProperty("运行的实例参数信息")
    private List<MergeTaskParamVO> mergeTaskParamVOS;

    @ApiModelProperty("执行批次")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime shouldFireTime;

    private JobState state;
}
