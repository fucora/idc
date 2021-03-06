package com.iwellmass.idc.scheduler.repository;

import com.iwellmass.idc.scheduler.model.NodeTask;
import com.iwellmass.idc.scheduler.model.TaskID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeTaskRepository extends CrudRepository<NodeTask, TaskID>, JpaSpecificationExecutor<NodeTask>{
	
}
