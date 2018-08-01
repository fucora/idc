package com.iwellmass.dispatcher.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.iwellmass.dispatcher.admin.dao.mapper")
@ComponentScan("com.iwellmass.dispatcher.admin")
public class DDCConfiguration {
}
