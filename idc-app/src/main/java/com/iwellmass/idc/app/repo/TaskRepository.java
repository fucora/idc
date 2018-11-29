package com.iwellmass.idc.app.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;

@Repository
public interface TaskRepository extends CrudRepository<Task, TaskKey>{

}
