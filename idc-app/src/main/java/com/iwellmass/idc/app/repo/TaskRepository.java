package com.iwellmass.idc.app.repo;

import com.iwellmass.idc.model.Task;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends CrudRepository<Task, TaskKey>,JpaSpecificationExecutor {
	
    Optional<Task> findByTaskId(String taskId);

	List<Task> findByTaskType(TaskType taskType);
}
