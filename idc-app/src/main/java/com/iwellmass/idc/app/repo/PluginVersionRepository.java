package com.iwellmass.idc.app.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.quartz.IDCConstants;

@Repository
public interface PluginVersionRepository extends CrudRepository<PluginVersion, String>{

	@Transactional
	public default int increaseInstanceSeqAndGet() {
		increaseInstanceSeqAndGet(IDCConstants.VERSION);
		PluginVersion v = findOne(IDCConstants.VERSION);
		return v.getInstanceSeq();
	}

	@Modifying
	@Query("UPDATE PluginVersion set instanceSeq = instanceSeq + 1 where version = ?1")
	public int increaseInstanceSeqAndGet(String version);

}
