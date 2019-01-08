package com.iwellmass.idc.app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobKey;

@Repository
public interface JobDependencyRepository extends CrudRepository<JobDependency, Integer>{

	@Query("SELECT D FROM JobDependency D WHERE D.srcJobId = ?1 AND D.srcJobGroup = ?2")
	List<JobDependency> findDependencies(String jobId, String jobGroup);

	
	@Query("SELECT D FROM JobDependency D")
	public List<JobDependency> findAll();


	@Modifying
	@Query("DELETE FROM JobDependency WHERE srcJobId = ?1 AND srcJobGroup = ?2")
	void cleanJobDependencies(String jobId, String jobGroup);

	@Modifying
	@Query("DELETE FROM JobDependency WHERE (srcJobId = :#{#jk.jobId} AND srcJobGroup = :#{#jk.jobGroup})")
	void deleteByJob(@Param("jk") JobKey jobPk);
	
}
