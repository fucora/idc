package com.dmall.ddc.server.dag.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.dao.DdcTaskWorkflowMapper;
import com.iwellmass.dispatcher.common.model.DdcSubtaskExecuteHistory;
import com.iwellmass.dispatcher.common.model.DdcTaskWorkflowWithBLOBs;
import com.iwellmass.dispatcher.thrift.bvo.WorkflowTask;

/**
 * Description goes here.
 *
 * @author Lu Gan
 * @email lu.gan@dmall.com
 * @date 5/12/16
 */
@Test
public class TaskCURDTest extends AbstractTester {

    @Test
    private void addWorkflow1() {


        DdcTaskWorkflowWithBLOBs workflow = new DdcTaskWorkflowWithBLOBs();
        workflow.setCreateTime(new Date());
        workflow.setCreateUser("lu.gan");
        workflow.setStatus(Constants.WORKFLOW_INUSE);
        WorkflowTask root = new WorkflowTask();
        root.setTaskId(1);
        List<WorkflowTask> level1 = new ArrayList<>();
        WorkflowTask w2 = new WorkflowTask();
        w2.setTaskId(2);
        level1.add(w2);
        WorkflowTask w3 = new WorkflowTask();
        w3.setTaskId(3);
        level1.add(w3);
        WorkflowTask w4 = new WorkflowTask();
        w4.setTaskId(4);
        w2.setChildren(Arrays.asList(w4));
        w3.setChildren(Arrays.asList(w4));
        root.setChildren(level1);

        //   / 2\
        // 1     4
        //  \ 3 /

        workflow.setWorkflow(JSON.toJSONBytes(root));
        workflowMapper.insert(workflow);

    }


    @Test
    private void addWorkflow2() {

        DdcTaskWorkflowMapper workflowMapper = applicationContext.getBean(DdcTaskWorkflowMapper.class);

        DdcTaskWorkflowWithBLOBs workflow = new DdcTaskWorkflowWithBLOBs();
        workflow.setCreateTime(new Date());
        workflow.setCreateUser("lu.gan");
        workflow.setStatus(Constants.WORKFLOW_INUSE);
        WorkflowTask root = new WorkflowTask();
        root.setTaskId(1);
        List<WorkflowTask> level1 = new ArrayList<>();
        WorkflowTask w2 = new WorkflowTask();
        w2.setTaskId(2);
        level1.add(w2);
        WorkflowTask w3 = new WorkflowTask();
        w3.setTaskId(3);
        level1.add(w3);
        WorkflowTask w4 = new WorkflowTask();
        w4.setTaskId(4);
        WorkflowTask w5 = new WorkflowTask();
        w5.setTaskId(5);
        w2.setChildren(Arrays.asList(w4));
        w3.setChildren(Arrays.asList(w5));
        w4.setChildren(Arrays.asList(w5));
        root.setChildren(level1);

        //   / 2 - 4\
        // 1         5
        //  \ 3   /

        workflow.setWorkflow(JSON.toJSONBytes(root));
        workflowMapper.insert(workflow);

    }


    @Test
    private void addSubtaskExecution1() {
        DdcSubtaskExecuteHistory execution = new DdcSubtaskExecuteHistory();
        execution.setTaskId(1);
        execution.setExecuteBatchId(UUID.randomUUID().toString());
        execution.setWorkflowId(4);
        execution.setWorkflowExecuteId(1L);

        subtaskExecuteHistoryMapper.insert(execution);
    }

}
