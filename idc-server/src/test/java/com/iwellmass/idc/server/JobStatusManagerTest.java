package com.iwellmass.idc.server;

import java.time.LocalDateTime;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iwellmass.idc.IDCServerConfiguration;
import com.iwellmass.idc.model.CompleteEvent;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@SpringBootTest(classes = IDCServerConfiguration.class)
public class JobStatusManagerTest {

	@Inject
	public JobStatusManager statusManager;
	
	public void test() {
		CompleteEvent event = new CompleteEvent();
		event.setEndTime(LocalDateTime.now());
		statusManager.fireJobComplete(event);
	}
}
