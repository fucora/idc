package com.iwellmass.idc.app.mapper;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.iwellmass.idc.app.IDCApplication;
import com.iwellmass.idc.app.vo.JobBarrierVO;
import com.iwellmass.idc.app.vo.JobRuntimeListVO;
import com.iwellmass.idc.model.JobKey;
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

		
		lst.stream().filter(vo -> vo.getJobId().equals("w1") && vo.getJobGroup().equals("idc-demo")).forEach(v -> {
			System.out.println(v.getScheduleStatus());
			v.getBarriers().forEach(b -> {
				System.out.println(b.getBarrierStatus());
			});
		});
		
//		List<JobBarrierVO> lst = jrm.selectJobBarrierVO(new JobKey("w1", "idc-demo"));
//		
//		lst.forEach(l -> {
//			System.out.println(l.getBarrierStatus());
//		});
//		
//		System.out.println(lst);
		
		// System.out.println(mapper.selectBatch(Arrays.asList(new TaskKey("11", "idc-demo"))));
	}

}
