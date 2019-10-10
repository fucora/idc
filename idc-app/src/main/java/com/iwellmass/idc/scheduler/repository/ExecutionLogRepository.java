package com.iwellmass.idc.scheduler.repository;

import com.iwellmass.idc.executor.IDCJobEvent;
import com.iwellmass.idc.scheduler.model.ExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ExecutionLogRepository extends CrudRepository<ExecutionLog, Long>, JpaSpecificationExecutor<ExecutionLog> {

    default void log(String jobId, String message, Object... args) {
        ExecutionLog log = ExecutionLog.createLog(jobId, message, args);
        save(log);
    }

    @Modifying
    @Transactional
    void deleteByJobId(String jobId);

    List<ExecutionLog> findAllByJobId(String jobId);
}
