package com.iwellmass.idc.scheduler.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.scheduler.model.Job;

@Repository
public interface JobRepository extends CrudRepository<Job, String>, JpaSpecificationExecutor<Job> {
	
	
	@Query("SELECT DISTINCT assignee FROM Job WHERE assignee IS NOT NULL")
	List<String> findAllAssignee();

	Optional<Job> findAllByTaskNameAndTaskGroup(String taskName,String taskGroup);

	List<Job> findAllByTaskNameIn(List<String> taskNames);

}
