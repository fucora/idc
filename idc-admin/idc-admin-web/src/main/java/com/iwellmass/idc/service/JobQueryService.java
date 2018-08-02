package com.iwellmass.idc.service;

import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.mapper.IdcTaskMapper;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobQuery;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class JobQueryService {

    @Inject
    private IdcTaskMapper idcTaskMapper;

    /**
     * 通过条件查询job
     * @param query
     * @return
     */
    public List<Job> findTasksByCondition(JobQuery query, Pager pager){
        Pager pager1=new Pager();
        pager1.setPage(pager.getTo());
        pager1.setLimit(pager.getLimit());
        return idcTaskMapper.findTasksByCondition(query,pager1);
    }

}
