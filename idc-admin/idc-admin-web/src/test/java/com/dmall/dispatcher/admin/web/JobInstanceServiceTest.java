package com.dmall.dispatcher.admin.web;

import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.IDCApplication;
import com.iwellmass.idc.model.JobQuery;
import com.iwellmass.idc.service.JobInstanceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

@SpringBootTest(classes = IDCApplication.class)
@RunWith(SpringRunner.class)
public class JobInstanceServiceTest {

    private final static Logger logger= LoggerFactory.getLogger(JobInstanceServiceTest.class);

    @Inject
    private JobInstanceService jobInstanceService;

    @Test
    public void findJobHistoryByCondition(){
        JobQuery jobQuery=new JobQuery();
        jobQuery.setName("简单任务");
        Pager pager=new Pager();
        pager.setPage(0);
        pager.setLimit(3);
        logger.info("========={}",jobInstanceService.findTaskInstanceByCondition(jobQuery,pager));
    }
}
