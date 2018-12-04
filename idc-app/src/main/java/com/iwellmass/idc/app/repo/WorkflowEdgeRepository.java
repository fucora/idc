package com.iwellmass.idc.app.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.WorkflowEdge;

@Repository
public interface WorkflowEdgeRepository extends CrudRepository<WorkflowEdge, Integer> {

    Optional<WorkflowEdge> findByWorkflowId(Integer workflowId);
    
	@Query(value = "select W from WorkflowEdge W where W.workflowId = ?1 and W.taskId = ?2 and W.taskGroup = ?3")
	List<WorkflowEdge> findPredecessors(String workflowId, String taskId, String taskGroup);

    
    @Query(value = "select W from WorkflowEdge W where W.workflowId = ?1 and W.srcTaskId = ?2 and W.srcTaskGroup = ?3")
    List<WorkflowEdge> findSuccessors(String workflowId, String taskId, String taskGroup);
}
