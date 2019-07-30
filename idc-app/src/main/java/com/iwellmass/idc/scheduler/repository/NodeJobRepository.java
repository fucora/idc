package com.iwellmass.idc.scheduler.repository;

import com.iwellmass.idc.scheduler.model.NodeJob;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeJobRepository extends CrudRepository<NodeJob, String>, JpaSpecificationExecutor<NodeJob> {

	List<NodeJob> findAllByContainer(String container);

}
