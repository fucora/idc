package com.dmall.dispatcher.admin.web;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.IDCApplication;
import com.iwellmass.idc.model.JobQuery;
import com.iwellmass.idc.service.JobInstanceService;

@SpringBootTest(classes = IDCApplication.class)
@RunWith(SpringRunner.class)
public class JobInstanceServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(JobInstanceServiceTest.class);

    @Inject
    private JobInstanceService jobInstanceService;

    @Test
    public void findJobHistoryByCondition() {
        JobQuery jobQuery = new JobQuery();
        Pager pager = new Pager();
        pager.setPage(0);
        pager.setLimit(3);
        logger.info("========={}", jobInstanceService.findTaskInstanceByCondition(jobQuery, pager).getData().toString());
    }

    @Test
    public void getAllTypes() {
        logger.info("========={}", jobInstanceService.getAllTypes().toString());
    }

    @Test
    public void getAllAssignee(){
        logger.info("========={}", jobInstanceService.getAllAssignee());
    }
}