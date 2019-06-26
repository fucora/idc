package com.iwellmass.idc.scheduler.model;

import java.util.Optional;

import javax.annotation.Resource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.iwellmass.idc.scheduler.SchedulerConfig;
import com.iwellmass.idc.scheduler.repository.TaskRepository;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ModelTests {
	
	@BeforeClass
	public static void before() {
		SchedulerConfig.clear = true;
	}

	@Resource
	TaskRepository taskRepo;

	@Test
	public void test() {
		Optional<Task> aa = taskRepo.findById(new TaskID("111"));
		Task task = aa.get();
		System.out.println(task);
	}

}
