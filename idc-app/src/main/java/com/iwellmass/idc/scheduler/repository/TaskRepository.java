package com.iwellmass.idc.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.scheduler.model.Task;
import com.iwellmass.idc.scheduler.model.TaskID;

@Repository
public interface TaskRepository extends CrudRepository<Task, TaskID>, JpaSpecificationExecutor<Task>{
	
	@Query("SELECT DISTINCT assignee FROM Task WHERE assignee IS NOT NULL")
	List<String> findAllAssignee();
}
