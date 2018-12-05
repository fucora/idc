package com.iwellmass.idc.app.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;

@Repository
public interface TaskRepository extends CrudRepository<Task, TaskKey>, JpaSpecificationExecutor<Task> {

	Optional<Task> findByTaskId(String taskId);

	List<Task> findByTaskType(TaskType taskType);

}
