package com.iwellmass.idc.app.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.Workflow;

@Repository
public interface WorkflowRepository extends CrudRepository<Workflow, TaskKey>, JpaSpecificationExecutor<Workflow> {

	Optional<Workflow> findByGraphId(String workflowId);

	Optional<Workflow> findByTaskIdAndTaskGroup(String taskId, String taskGroup);
}
