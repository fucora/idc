package com.iwellmass.idc.scheduler.repository;

import com.iwellmass.idc.scheduler.model.TaskDependency;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDependencyRepository extends CrudRepository<TaskDependency, Long>, JpaSpecificationExecutor<TaskDependency> {
}
