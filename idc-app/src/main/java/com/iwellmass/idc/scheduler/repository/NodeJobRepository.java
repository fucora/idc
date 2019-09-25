package com.iwellmass.idc.scheduler.repository;

import com.iwellmass.idc.scheduler.model.NodeJob;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeJobRepository extends CrudRepository<NodeJob, String>, JpaSpecificationExecutor<NodeJob> {

	List<NodeJob> findAllByContainer(String container);

	List<NodeJob> findAllByContainerIn(List<String> containers);

	void deleteAllByContainerIn(List<String> containers);

	@Query(nativeQuery = true,value = "select count(*) AS runningJobs from idc_node_job where state = 'ACCEPTED' or state = 'RUNNING' ")
	int countNodeJobsByRunningOrAcceptState();
}
