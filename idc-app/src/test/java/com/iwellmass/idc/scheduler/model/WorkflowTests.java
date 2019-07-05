package com.iwellmass.idc.scheduler.model;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import com.iwellmass.idc.scheduler.repository.WorkflowRepository;


@RunWith(SpringRunner.class)
@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class WorkflowTests {

	@Resource
	WorkflowRepository repository;
	
	@Test
	@Rollback(false)
	public void test() {
		repository.deleteById("my-test");
	}
	
	
}
