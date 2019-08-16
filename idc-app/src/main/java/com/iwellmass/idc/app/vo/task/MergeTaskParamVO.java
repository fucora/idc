package com.iwellmass.idc.app.vo.task;

import com.iwellmass.common.param.ExecParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author nobita chen
 * @email nobita0522@qq.com
 * @date 2019/8/16 11:14
 * @description show param that has been merged: one param to n nodeTaskId ,and connect nodeTaskName with ','
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MergeTaskParamVO {

    @ApiModelProperty("该参数对应的所有nodeTask合并后的nodeTask的TaskName")
    private String mergedNodeTaskName;

    @ApiModelProperty("实际参数")
    private ExecParam execParam;
}
