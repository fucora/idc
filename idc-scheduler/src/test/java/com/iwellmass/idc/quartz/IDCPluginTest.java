package com.iwellmass.idc.quartz;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.iwellmass.idc.AllSimpleService;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.model.WorkflowEdge;

public class IDCPluginTest {

	private static final String lqd_02 = "lqd_02";
	private static final String lqd_01 = "lqd_01";
	private static final String group = "test";

	private static final LocalDateTime _1_1 = LocalDateTime.of(LocalDate.of(2018, 1, 1), LocalTime.MIN);
	private static final LocalDateTime _9_1 = LocalDateTime.of(LocalDate.of(2018, 9, 1), LocalTime.MIN);

	@Test
	public void testWorkflowSchedule() throws Exception {
		
		IDCPlugin plugin = new SimpleIDCPlugin();
		
		AllSimpleService allService = new AllSimpleService();
		
		// 主任务
		Task task = new Task();
		task.setTaskKey(new TaskKey(lqd_01, group));
		task.setTaskName("工作流的任务");
		task.setDispatchType(DispatchType.AUTO);
		task.setTaskType(TaskType.WORKFLOW_TASK);
		task.setContentType("simple-test");
		task.setWorkflowId("1");
		
		// 子任务
		Task sub1 = new Task();
		sub1.setTaskKey(new TaskKey("sub1", group));
		sub1.setTaskName("sub1");
		sub1.setDispatchType(DispatchType.AUTO);
		sub1.setTaskType(TaskType.NODE_TASK);
		sub1.setContentType("simple-test");
		
		Task sub2 = new Task();
		sub2.setTaskKey(new TaskKey("sub2", group));
		sub2.setTaskName("sub2");
		sub2.setDispatchType(DispatchType.AUTO);
		sub2.setTaskType(TaskType.NODE_TASK);
		sub2.setContentType("simple-test");
		
		allService.saveTask(task);
		allService.saveTask(sub1);
		allService.saveTask(sub2);
		
		allService.addTaskDependency("1", WorkflowEdge.START, sub1.getTaskKey(), sub2.getTaskKey(), WorkflowEdge.END);
		
		IDCSchedulerFactory factory = new IDCSchedulerFactory();
		factory.setPlugin(plugin);
		factory.setTaskService(allService);
		factory.setJobService(allService);
		factory.setDependencyService(allService);
		factory.setDriverDelegate(new SimpleIDCDriverDelegate());
		factory.setDataSource(dataSource());
		
		Scheduler scheduler = factory.getScheduler();
		scheduler.clear();
		scheduler.start();
		
		ScheduleProperties sp = new ScheduleProperties();
		sp.setScheduleType(ScheduleType.MONTHLY);
		sp.setStartTime(_1_1);
		sp.setEndTime(_9_1);
		sp.setDays(Arrays.asList(-1));
		plugin.schedule(task, sp);
		scheduler.getTriggerState(new TriggerKey(lqd_02, group));
		
//		plugin.getStatusService().fireCompleteEvent(CompleteEvent.successEvent()
//			.setInstanceId(1001));
		
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
