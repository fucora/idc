package com.iwellmass.idc;

import org.mybatis.spring.annotation.MapperScan;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.iwellmass.dispatcher.admin.DDCConfiguration;

@SpringBootApplication
@Import(DDCConfiguration.class)
@MapperScan("com.iwellmass.idc.mapper")
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
