package com.iwellmass.idc.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iwellmass.idc.IDCServerConfiguration;
import com.iwellmass.idc.model.ContentType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstanceType;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.quartz.IDCPlugin;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = IDCServerConfiguration.class)
public class JobServiceTest {

	@Inject
	JobService jobService;

	LocalDateTime _2017_12_31 = LocalDateTime.of(LocalDate.of(2017, 12, 31), LocalTime.MIN);

	LocalDateTime _2018_01_01 = LocalDateTime.of(LocalDate.of(2018, 1, 1), LocalTime.MIN);
	LocalDateTime lastDay = LocalDateTime.of(LocalDate.now().plusDays(-1), LocalTime.MIN);

	LocalDateTime last3Day = LocalDateTime.of(LocalDate.now().plusDays(-3), LocalTime.MIN);

	@Test
	public void scheduleJob() throws InterruptedException {
		ScheduleProperties sp = new ScheduleProperties();
		sp.setScheduleType(ScheduleType.DAILY);
		sp.setDuetime("10:00:00");

		Job job1 = new Job();
		job1.setTaskId("1");
		job1.setTaskName("lqd_test");
		job1.setTaskType(TaskType.NODE_TASK);
		job1.setGroupId("datafactory");
		job1.setScheduleType(ScheduleType.DAILY);
		job1.setScheduleProperties(sp);
		job1.setAssignee("lqd");
		job1.setContentType(ContentType.SPARK_SQL);
		job1.setStartTime(last3Day);

		jobService.schedule(job1);

		Job job2 = new Job();
		job2.setTaskId("2");
		job2.setTaskName("lqd_test_2");
		job2.setTaskType(TaskType.NODE_TASK);
		job2.setGroupId("datafactory");
		job2.setScheduleType(ScheduleType.DAILY);
		job2.setScheduleProperties(sp);
		job2.setAssignee("lqd");
		job2.setContentType(ContentType.SPARK_SQL);
		job2.setStartTime(last3Day);

		jobService.schedule(job2);

		Thread.sleep(60 * 10 * 1000);
	}

	@Inject
	private Scheduler scheduler;

	@Test
	public void mockResume() throws SchedulerException, InterruptedException {
		scheduler.resumeTrigger(IDCPlugin.buildTriggerKey(JobInstanceType.CRON, "1"));
		Thread.sleep(60 * 1000);
	}

	@Test
	public void startOnly() throws InterruptedException {
		Thread.sleep(10 * 60 * 1000);
	}

	@Test
	public void lock() throws InterruptedException {
		jobService.lock("1", "datafactory");
	}

	@Test
	public void complement() throws InterruptedException {

	}

	@Test
	public void getAllJob() {
		jobService.getJob("1", "datafactory");
	}

}
