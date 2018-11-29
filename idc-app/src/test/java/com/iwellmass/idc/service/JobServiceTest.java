package com.iwellmass.idc.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iwellmass.idc.app.config.IDCSchedulerConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = IDCSchedulerConfiguration.class)
public class JobServiceTest {


	LocalDateTime _2017_12_31 = LocalDateTime.of(LocalDate.of(2017, 12, 31), LocalTime.MIN);

	LocalDateTime _2018_01_01 = LocalDateTime.of(LocalDate.of(2018, 1, 1), LocalTime.MIN);
	LocalDateTime lastDay = LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MIN);

	LocalDateTime last3Day = LocalDateTime.of(LocalDate.now().plusDays(-3), LocalTime.MIN);
	
}
