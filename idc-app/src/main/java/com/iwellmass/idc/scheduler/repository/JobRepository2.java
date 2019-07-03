package com.iwellmass.idc.scheduler.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.scheduler.model.AbstractJob;

@Repository
public interface JobRepository2 extends CrudRepository<AbstractJob, String>, JpaSpecificationExecutor<AbstractJob> {
	
}
