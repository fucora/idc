package com.iwellmass.idc.app.mapper;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iwellmass.idc.app.IDCApplication;
import com.iwellmass.idc.app.vo.JobRuntimeListVO;
import com.iwellmass.idc.model.TaskKey;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = IDCApplication.class)
public class JobRuntimeMapperTest {
	
	@Inject
	TaskMapper mapper;
	
	@Inject
	JobRuntimeMapper jrm;
	
	@Test
	public void test() {
		
		List<JobRuntimeListVO> lst = jrm.selectJobRuntimeList(null);

		lst.forEach(l -> {
			System.out.println(l.getScheduleStatus());
		});
		
		System.out.println(lst);
		
		// System.out.println(mapper.selectBatch(Arrays.asList(new TaskKey("11", "idc-demo"))));
	}

}
