package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

public enum ContentType {

	@ApiModelProperty("NONE")
    NONE,
    
    @ApiModelProperty("SCALA")
    SCALA,
    
    @ApiModelProperty("SPARK SQL")
    SPARK_SQL,
    
    @ApiModelProperty("SPARK APP")
    SPARK_APP,
    @ApiModelProperty("数据同步任务")
    DATA_SYNC
}
