package com.iwellmass.idc.quartz;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

public class IDCPluginTest {

	private static final String lqd_01 = "lqd_01";
	private static final String group = "test";

	private static final LocalDateTime _1_1 = LocalDateTime.of(LocalDate.of(2018, 1, 1), LocalTime.MIN);
	private static final LocalDateTime _9_1 = LocalDateTime.of(LocalDate.of(2018, 9, 1), LocalTime.MIN);

	Scheduler scheduler;
	IDCPlugin plugin;
	
	@Before
	public void setup() throws SchedulerException {
		plugin = new SimpleIDCPlugin();
		
		IDCSchedulerFactory factory = new IDCSchedulerFactory();
		factory.setIDCPlugin(plugin);
		factory.setIdcDriverDelegate(new SimpleIDCDriverDelegate());
		factory.setDataSource(dataSource());

		scheduler = factory.getScheduler();
		scheduler.clear();
		scheduler.start();
	}
	
	@Test
	public void testSimpleSchedule() throws Exception {
		
		ScheduleProperties sp = new ScheduleProperties();
		sp.setScheduleType(ScheduleType.MONTHLY);
		sp.setStartTime(_1_1);
		sp.setEndTime(_9_1);
		sp.setDays(Arrays.asList(-1));
		sp.setDuetime("00:00:00");

		// Job = JobDetails + Trigger
		Job job = new Job();
		job.setJobKey(new JobKey(lqd_01, group));
		job.setTaskKey(new TaskKey(lqd_01, group));
		job.setScheduleType(ScheduleType.MONTHLY);
		job.setDispatchType(DispatchType.AUTO);
		job.setWorkflowId(1);
		job.setScheduleProperties(sp);
		job.setTaskType(TaskType.NODE_TASK);

		plugin.schedule(job);

		scheduler.getTriggerState(new TriggerKey(lqd_01, group));
		
		
		plugin.getStatusService().fireCompleteEvent(CompleteEvent.successEvent().setInstanceId(123));
		
		Thread.sleep(10 * 60 * 1000);

	}

	private DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://austin.realhyx.com:3306/test?characterEncoding=utf8&useSSL=false");
		dataSource.setUsername("root");
		dataSource.setPassword("000000oO)");
		return dataSource;
	}
}
