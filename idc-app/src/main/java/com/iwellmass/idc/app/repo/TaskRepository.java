package com.iwellmass.idc.app.repo;

import org.springframework.data.repository.CrudRepository;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;

public interface TaskRepository extends CrudRepository<Task, TaskKey>{

}
