package com.dmall.ddc.server.dag.test;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.iwellmass.dispatcher.common.dag.SchedulingEngine;
import com.iwellmass.dispatcher.common.dao.DdcSubtaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskWorkflowMapper;

/**
 * Description goes here.
 *
 * @author Lu Gan
 * @email lu.gan@dmall.com
 * @date 5/17/16
 */
@Test
public abstract class AbstractTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTester.class);

    private static final String BEAN_CONTEXT = "classpath:applicationContext-bean.xml";
    private static final String DAO_CONTEXT = "classpath:applicationContext-dao.xml";

    protected ApplicationContext applicationContext;

    protected SchedulingEngine engine;

    protected DdcTaskWorkflowMapper workflowMapper;

    protected DdcSubtaskExecuteHistoryMapper subtaskExecuteHistoryMapper;

    @BeforeTest
    protected void init(){

        String[] contexts = {BEAN_CONTEXT, DAO_CONTEXT};

        LOGGER.info("Load ApplicationContext {}", Arrays.toString(contexts));
        applicationContext = new ClassPathXmlApplicationContext(contexts);
        workflowMapper = applicationContext.getBean(DdcTaskWorkflowMapper.class);
        subtaskExecuteHistoryMapper = applicationContext.getBean(DdcSubtaskExecuteHistoryMapper.class);

        engine = applicationContext.getBean(SchedulingEngine.class);
        engine.start();
    }


}
