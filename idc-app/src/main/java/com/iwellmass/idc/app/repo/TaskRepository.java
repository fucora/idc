package com.iwellmass.idc.app.repo;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

@Repository
public interface TaskRepository extends CrudRepository<Task, TaskKey>, JpaSpecificationExecutor<Task> {

	List<Task> findByTaskType(TaskType taskType, Sort sort);

	@Query(value = "select count(*) from t_idc_task where task_group = 'data-factory'",nativeQuery = true)
	Integer countAll();

	@Query(value = "select e.src_task_id,e.src_task_group from t_idc_workflow_edge e " +
			"where e.parent_task_id = :#{#ptk.getTaskId()} and e.parent_task_group = :#{#ptk.getTaskGroup()}",nativeQuery = true)
	List<Object[]> findSrcTaskKeyByParentTaskKey(@Param("ptk") TaskKey taskKey);
}
