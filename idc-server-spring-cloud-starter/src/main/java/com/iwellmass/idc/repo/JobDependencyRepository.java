package com.iwellmass.idc.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobDependencyPK;

@Repository
public interface JobDependencyRepository extends CrudRepository<JobDependency, JobDependencyPK>{

	@Query("SELECT D FROM JobDependency D WHERE D.taskId = ?1 AND D.groupId = ?2")
	List<JobDependency> findDependencies(String taskId, String groupId);

}
