package com.iwellmass.idc.service;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iwellmass.idc.server.IDCServerConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = IDCServerConfiguration.class)
public class JobInstanceServiceTest {


	@Inject
	JobInstanceService jobInstanceService;
	
	
	@Test
	public void redo() {
		
	}
	
	
}
