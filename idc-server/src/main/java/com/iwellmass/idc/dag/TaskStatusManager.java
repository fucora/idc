//package com.iwellmass.idc.dag;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.iwellmass.dispatcher.common.constants.Constants;
//import com.iwellmass.dispatcher.common.dao.DdcSubtaskExecuteHistoryMapper;
//import com.iwellmass.dispatcher.common.model.DdcSubtaskExecuteHistory;
//import com.iwellmass.dispatcher.common.model.DdcSubtaskExecuteHistoryExample;
//import com.iwellmass.dispatcher.thrift.bvo.TaskStatus;
//
///**
// * Description goes here.
// *
// * @author Lu Gan
// * @email lu.gan@dmall.com
// * @date 5/5/16
// */
////@Component
//public class TaskStatusManager {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusManager.class);
//
//    @Autowired
//    private DdcSubtaskExecuteHistoryMapper subtaskExecuteHistoryMapper;
//
//
//    boolean isJobsCompleted(Long workflowExecuteId, String executeBatchId, Collection<Integer> taskIds) {
//        if(taskIds == null) {
//            return true;
//        }
//        
//        taskIds.remove(Constants.WORKFLOW_START_TASK_ID); //移除开始节点
//
//        return taskIds.stream().allMatch(taskId -> {
//            DdcSubtaskExecuteHistoryExample query = new DdcSubtaskExecuteHistoryExample();
//            query.createCriteria().andWorkflowExecuteIdEqualTo(workflowExecuteId).andExecuteBatchIdEqualTo(executeBatchId).
//                    andTaskIdEqualTo(taskId).andExecuteResultEqualTo(TaskStatus.SUCCEED);
//            List<DdcSubtaskExecuteHistory> found = subtaskExecuteHistoryMapper.selectByExample(query);
//
//            // Shouldn't have more than one
//            if(found != null && found.size() > 1) {
//                LOGGER.error("Found {} records {}!", found.size(), Arrays.toString(found.toArray()));
//            }
//
//            return found != null && found.size() >= 1;
//        });
//
//
//    }
//
//}
