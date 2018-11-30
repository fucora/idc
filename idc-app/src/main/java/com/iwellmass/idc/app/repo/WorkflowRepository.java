package com.iwellmass.idc.app.repo;

import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.Workflow;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkflowRepository extends CrudRepository<Workflow, TaskKey>,JpaSpecificationExecutor {
    Optional<Workflow> findByWorkflowId(String workflowId);

    Optional<Workflow> findByTaskIdAndTaskGroup(String taskId,String taskGroup);
}
