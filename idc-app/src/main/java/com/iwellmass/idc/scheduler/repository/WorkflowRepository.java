package com.iwellmass.idc.scheduler.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.scheduler.model.Workflow;

@Repository
public interface WorkflowRepository extends CrudRepository<Workflow, String>, JpaSpecificationExecutor<Workflow> {
}
