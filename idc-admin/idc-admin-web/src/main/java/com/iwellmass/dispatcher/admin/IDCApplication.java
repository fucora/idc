package com.iwellmass.dispatcher.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringCloudApplication
@EnableEurekaClient
@MapperScan("com.iwellmass.dispatcher.admin.dao.mapper")
public class IDCApplication {
	
    public static void main(String[] args) {
        new SpringApplicationBuilder(IDCApplication.class).web(true).run(args);
    }
    
    
    @Bean
    public SchedulerFactory schedulerFactory() {
    	return new StdSchedulerFactory();
    }
    
    @Bean
    public FactoryBean<Scheduler> scheduler(SchedulerFactory schedulerFactory) {
    	return new FactoryBean<Scheduler>() {

			@Override
			public Scheduler getObject() throws Exception {
				return schedulerFactory.getScheduler();
			}

			@Override
			public Class<?> getObjectType() {
				return Scheduler.class;
			}

			@Override
			public boolean isSingleton() {
				return false;
			}
		};
    }
}
