package com.dmall.ddc.server.dag.test;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.iwellmass.dispatcher.common.entry.TaskInfoTuple;
import com.iwellmass.dispatcher.common.model.DdcSubtaskExecuteHistory;
import com.iwellmass.dispatcher.common.model.DdcSubtaskExecuteHistoryExample;

/**
 * Description goes here.
 *
 * @author Lu Gan
 * @email lu.gan@dmall.com
 * @date 5/12/16
 */
@Test
public class DAGEngineTest extends AbstractTester {

    private static final Logger LOGGER = LoggerFactory.getLogger(DAGEngineTest.class);

    @Test
    private void testDag1() {

        DdcSubtaskExecuteHistoryExample query = new DdcSubtaskExecuteHistoryExample();
        query.createCriteria().andWorkflowIdEqualTo(1);
        List<DdcSubtaskExecuteHistory> historyList = subtaskExecuteHistoryMapper.selectByExample(query);

        if(historyList.size() > 0) {
            DdcSubtaskExecuteHistory history = historyList.get(0);

            TaskInfoTuple taskInfoTuple = new TaskInfoTuple(history.getWorkflowExecuteId(), history.getTaskId(), history.getExecuteBatchId(), history.getWorkflowId());

            List<Integer> subsequentTaskIds = engine.findSubsequentTasks(taskInfoTuple);
            LOGGER.info("Found: {}",Arrays.toString(subsequentTaskIds.toArray()));
        }

    }
    
    @Test
    private void testWorkflow() {
    	List<Integer> subsequentTaskIds = engine.findStartTaskIds(1);
    	System.out.println(subsequentTaskIds);
    }

}
