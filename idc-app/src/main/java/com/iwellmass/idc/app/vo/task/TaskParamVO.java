package com.iwellmass.idc.app.vo.task;

import com.google.common.collect.Lists;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.datafactory.common.vo.TaskDetailVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nobita chen
 * @email nobita0522@qq.com
 * @date 2019/8/16 10:50
 * @description show necessary param when schedule. it contain paramName, the source of param ,domain .etc
 */
@Getter
@Setter
public class TaskParamVO {

    @ApiModelProperty("nodeTask的taskId")
    private String nodeTaskTaskId;

    @ApiModelProperty("nodeTask的任务名称")
    private String nodeTaskName;

    @ApiModelProperty("nodeTask的业务域")
    private String nodeTaskDomain;

    @ApiModelProperty("nodeTask运行参数信息")
    private List<ExecParam> params;

    public TaskParamVO(TaskDetailVO taskDetailVO) {
        this.nodeTaskTaskId = taskDetailVO.getId().toString();
        this.nodeTaskName = taskDetailVO.getName();
        this.params = taskDetailVO.getParams() == null ? Lists.newArrayList() : taskDetailVO.getParams().stream().map(p -> new ExecParam(p.getName(),p.getDefaultExpr(),p.getParamType())).collect(Collectors.toList());
    }
}
