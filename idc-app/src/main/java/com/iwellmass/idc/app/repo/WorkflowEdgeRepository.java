package com.iwellmass.idc.app.repo;

import java.util.List;

import com.iwellmass.idc.model.TaskKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.WorkflowEdge;

@Repository
public interface WorkflowEdgeRepository extends CrudRepository<WorkflowEdge, Integer> {

    List<WorkflowEdge> findByParentTaskIdAndParentTaskGroup(String parentTaskId, String parentTaskGroup);
    
	@Query(value = "select W from WorkflowEdge W where W.parentTaskId = ?1 and W.parentTaskGroup = ?2 and W.taskId = ?3 and W.taskGroup = ?4")
	List<WorkflowEdge> findPredecessors(String parentTaskId,String parentTaskGroup, String taskId, String taskGroup);

    
    @Query(value = "select W from WorkflowEdge W where W.parentTaskId = ?1 and W.parentTaskGroup = ?2 and W.srcTaskId = ?3 and W.srcTaskGroup = ?4")
    List<WorkflowEdge> findSuccessors(String parentTaskId,String parentTaskGroup, String taskId, String taskGroup);

    @Modifying
	void deleteByParentTaskIdAndParentTaskGroup(String parentTaskId, String parentTaskGroup);
}
