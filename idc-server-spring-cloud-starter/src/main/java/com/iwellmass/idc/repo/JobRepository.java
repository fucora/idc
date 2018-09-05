package com.iwellmass.idc.repo;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobPK;

@Repository
public interface JobRepository extends CrudRepository<Job, JobPK>, JpaSpecificationExecutor<Job>{

	@Query("SELECT j FROM Job j WHERE j.taskId = ?1 and j.groupId = ?2")
	Job findOne(String taskId, String groupId);

}
