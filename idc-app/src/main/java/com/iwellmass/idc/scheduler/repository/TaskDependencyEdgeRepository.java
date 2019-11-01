package com.iwellmass.idc.scheduler.repository;

import com.iwellmass.idc.scheduler.model.TaskDependencyEdge;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDependencyEdgeRepository extends CrudRepository<TaskDependencyEdge, Long>, JpaSpecificationExecutor<TaskDependencyEdge> {

    void deleteByTarget(String target);

    void deleteBySourceOrTarget(String source,String target);

    List<TaskDependencyEdge> findAllByTarget(String target);

    List<TaskDependencyEdge> findAllBySource(String string);

    void deleteByTaskDependencyId(Long dependencyId);
}
