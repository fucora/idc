package com.iwellmass.idc.model;

import io.swagger.annotations.ApiModelProperty;

public enum ContentType {
	
	// 数据工厂 ---> data-factory

	@ApiModelProperty("NONE")
    NONE("default"),
    
    @ApiModelProperty("SCALA")
    SCALA("data-factory"),
    
    @ApiModelProperty("SPARK SQL")
    SPARK_SQL("data-factory"),
    
    @ApiModelProperty("SPARK APP")
    SPARK_APP("data-factory"),
    
    @ApiModelProperty("数据同步任务")
    DATA_SYNC("data-factory"),
    
    @ApiModelProperty("数据清洗")
	DATA_CLEAN("data-factory");
	
	private String domain;

	private ContentType(String domain) {
		this.domain = domain;
	}
	
	public String getDomain() {
		return this.domain;
	}
}
