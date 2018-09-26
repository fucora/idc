package com.iwellmass.idc.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.scheduler.IDCSchedulerConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = IDCSchedulerConfiguration.class)
public class JobServiceTest {


	LocalDateTime _2017_12_31 = LocalDateTime.of(LocalDate.of(2017, 12, 31), LocalTime.MIN);

	LocalDateTime _2018_01_01 = LocalDateTime.of(LocalDate.of(2018, 1, 1), LocalTime.MIN);
	LocalDateTime lastDay = LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MIN);

	LocalDateTime last3Day = LocalDateTime.of(LocalDate.now().plusDays(-3), LocalTime.MIN);
	
	@Inject
	private JobService jobService;

	@Test
	public void scheduleCronJob() throws InterruptedException {
		
		ScheduleProperties sp = new ScheduleProperties();
		sp.setScheduleType(ScheduleType.DAILY);
		sp.setDuetime("10:00:00");

		Job job1 = new Job();
		job1.setTaskId("1");
		job1.setTaskName("LQD_周期调度测试_01");
		job1.setTaskType(TaskType.NODE_TASK);
		job1.setGroupId("idc-demo");
		job1.setScheduleProperties(sp);
		job1.setAssignee("lqd");
		job1.setContentType("moody");
		job1.setStartTime(last3Day);

		jobService.schedule(job1);

		Thread.sleep(60 * 10 * 1000);
	}
	@Test
	public void scheduleManualJob() throws InterruptedException {
		
		ScheduleProperties sp = new ScheduleProperties();
		
		Job job1 = new Job();
		job1.setTaskId("2");
		job1.setTaskName("LQD_手动调度测试_01");
		job1.setTaskType(TaskType.NODE_TASK);
		job1.setGroupId("idc-demo");
		job1.setScheduleProperties(sp);
		job1.setAssignee("lqd");
		job1.setContentType("moody");
		job1.setStartTime(last3Day);
		
		jobService.schedule(job1);
		
		Thread.sleep(60 * 10 * 1000);
	}
	@Test
	public void executeJob() throws InterruptedException {
		ExecutionRequest request = new ExecutionRequest();
		request.setTaskId("2");
		request.setGroupId("idc-demo");
		request.setJobParameter("");
		jobService.execute(request);
		Thread.sleep(60 * 10 * 1000);
	}



}
