package com.iwellmass.dispatcher.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.iwellmass.dispatcher.admin.dao.mapper")
@ComponentScan("com.iwellmass.dispatcher.admin")
public class DDCConfiguration {

	public static final int DEFAULT_APP = 3;
	public static final String DEFAULT_APP_KEY = "default";
}
