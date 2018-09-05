package com.iwellmass.idc.repo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstancePK;

@Repository
public interface JobInstanceRepository extends CrudRepository<JobInstance, JobInstancePK>, JpaSpecificationExecutor<JobInstance>{
	
	@Query("SELECT u FROM JobInstance u WHERE u.instanceId = ?1")
	JobInstance findOne(Integer id);

	@Transactional
	@Modifying
	default boolean tryUpdate(JobInstance jobInstance) {
		if (updateStatus(jobInstance) > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	@Transactional
	@Modifying
	@Query("UPDATE JobInstance J SET J.status = :#{#p.status} WHERE J.status != :#{#p.status} AND J.instanceId = :#{#p.instanceId}")
	int updateStatus(@Param("p") JobInstance jobInstance);

}
