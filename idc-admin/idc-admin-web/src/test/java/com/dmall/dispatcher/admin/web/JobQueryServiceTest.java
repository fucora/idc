package com.dmall.dispatcher.admin.web;

import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.IDCApplication;
import com.iwellmass.idc.model.JobQuery;
import com.iwellmass.idc.service.JobQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

@SpringBootTest(classes = IDCApplication.class)
@RunWith(SpringRunner.class)
public class JobQueryServiceTest {

    private final static Logger logger= LoggerFactory.getLogger(JobQueryServiceTest.class);

    @Inject
    private JobQueryService jobQueryService;

    @Test
    public void findTasksByCondition(){
        JobQuery jobQuery=new JobQuery();
        Pager pager=new Pager();
        pager.setPage(2);
        pager.setLimit(3);
        logger.info("========={}",jobQueryService.findTasksByCondition(jobQuery,pager).getRawData().toString());
    }

    @Test
    public void getAllTypes(){
        logger.info("========={}",jobQueryService.getAllTypes().toString());
    }
}
