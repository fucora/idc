package com.iwellmass.idc.app.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

@Repository
public interface TaskRepository extends CrudRepository<Task, TaskKey>, JpaSpecificationExecutor<Task> {

	Optional<Task> findByTaskId(String taskId);

	List<Task> findByTaskType(TaskType taskType);

	@Query(value = "select * from t_idc_workflow_edge edge where edge.task_id = :#{#taskKey.getTaskId()} and edge.task_group = :#{#taskKey.getTaskGroup()} and edge.workflowId = ?1}",nativeQuery = true)
	List<Task> findPredecessors(String workflowId,TaskKey taskKey);

    @Query(value = "select * from t_idc_workflow_edge edge where edge.src_task_id = :#{#taskKey.getTaskId()} and edge.src_task_group = :#{#taskKey.getTaskGroup()} and edge.workflowId = ?1}",nativeQuery = true)
    List<Task> findSuccessors(String workflowId,TaskKey taskKey);
}
