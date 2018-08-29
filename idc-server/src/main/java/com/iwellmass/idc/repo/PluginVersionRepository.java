package com.iwellmass.idc.repo;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.PluginVersion;

@Repository
public interface PluginVersionRepository extends CrudRepository<PluginVersion, String>{

	@Transactional
	@Modifying
	@Query("UPDATE PluginInfo set instanceSeq = instanceSeq + 1 where version = ?1")
	public int increaseInstanceSeqAndGet(String version);

}
