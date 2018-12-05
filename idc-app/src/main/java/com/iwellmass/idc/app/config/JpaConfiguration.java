package com.iwellmass.idc.app.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("com.iwellmass.idc.app.repo")
@EntityScan("com.iwellmass.idc.model")
public class JpaConfiguration {

}
