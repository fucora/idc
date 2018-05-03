package com.iwellmass.ddc.server.dag;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.iwellmass.dispatcher.common.dao.DdcTaskWorkflowMapper;
import com.iwellmass.dispatcher.thrift.bvo.WorkflowTask;

/**
 * Description goes here.
 *
 * @author Lu Gan
 * @email lu.gan@dmall.com
 * @date 5/5/16
 */
@Component
public class WorkflowManager {

    @Autowired
    private DdcTaskWorkflowMapper workflowMapper;

    // LRU
    private Map<Integer, TaskDAGBuilder> workflowTemplates;

    public WorkflowManager() {
        // init
        workflowTemplates = new LinkedHashMap(){

            private static final int MAX_ENTRIES = 100000;

            @Override
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > MAX_ENTRIES;
            }
        };


    }

    public Optional<TaskDAGBuilder> get(Integer workflowId) {
        if(workflowId == null) {
            return Optional.empty();
        }
        TaskDAGBuilder taskDAGBuilder = workflowTemplates.get(workflowId);
        if(taskDAGBuilder == null) {
            return Optional.ofNullable(workflowMapper.selectByPrimaryKey(workflowId)).
                    map(ddcTaskWorkflow -> JSON.parseObject(ddcTaskWorkflow.getWorkflow(), WorkflowTask.class)).
                    map(workflowTask -> {
                        TaskDAGBuilder temp = new TaskDAGBuilder((WorkflowTask) workflowTask);
                        workflowTemplates.put(workflowId, temp);
                        return temp;
                    });
        } else {
            return Optional.of(taskDAGBuilder);
        }
    }
}
