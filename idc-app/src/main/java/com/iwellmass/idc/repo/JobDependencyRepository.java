package com.iwellmass.idc.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobDependencyPK;

@Repository
public interface JobDependencyRepository extends CrudRepository<JobDependency, JobDependencyPK>{

	@Query("SELECT D FROM JobDependency D WHERE D.taskId = ?1 AND D.groupId = ?2")
	List<JobDependency> findDependencies(String taskId, String groupId);

	
	@Query("SELECT D FROM JobDependency D")
	public List<JobDependency> findAll();


	@Modifying
	@Query("DELETE FROM JobDependency WHERE srcTaskId = ?1 AND srcGroupId = ?2")
	void cleanJobDependencies(String taskId, String groupId);
	
}
