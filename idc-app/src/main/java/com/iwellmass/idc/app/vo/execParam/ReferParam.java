package com.iwellmass.idc.app.vo.execParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author nobita chen
 * @email nobita0522@qq.com
 * @date 2019/8/14 17:26
 * @description when param is expr, this class contain the params that user can refer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferParam {

    @ApiModelProperty("当前运行批次时间")
    private LocalDateTime shouldFireTime;

    @ApiModelProperty("实际运行时间")
    private LocalDateTime realRunTime;

    @ApiModelProperty("调度计划提交或者重新调度的时间更新时间,不是task.createTime")
    private LocalDateTime taskUpdateTime;

}
