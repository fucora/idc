package com.iwellmass.idc.scheduler.model;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.iwellmass.idc.scheduler.model")
@EnableJpaRepositories("com.iwellmass.idc.scheduler.repository")
public class SchedulerSetup {

}
