package com.iwellmass.idc.app.repo;

import com.iwellmass.idc.model.Workflow;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends CrudRepository<Workflow, Integer>,JpaSpecificationExecutor {
}
