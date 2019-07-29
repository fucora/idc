package com.iwellmass.idc.app.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.common.criteria.BetweenPair;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author chenxiong
 * @email nobita0522@qq.com
 * @date 2019/7/29 10:32
 * @description 用于时间区间的筛选
 */
@Getter
@Setter
public class YMDHMSBetweenPair extends BetweenPair<LocalDateTime> {

    @ApiModelProperty("开始 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime from;

    @ApiModelProperty("截止 yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime to;

}
