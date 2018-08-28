package com.iwellmass.idc.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.Job;

@Repository
public interface JobRepository extends CrudRepository<Job, Integer>{

	@Query("SELECT j FROM Job j WHERE j.taskId = ?1 and j.groupId = ?2")
	Job findOne(String taskId, String groupId);

}
