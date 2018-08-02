package com.iwellmass.idc.service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.mapper.IdcTaskHistoryMapper;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobQuery;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class JobInstanceService {

    @Inject
    private IdcTaskHistoryMapper idcTaskHistoryMapper;

    public PageData<List<JobInstance>>findTaskInstanceByCondition(JobQuery query, Pager pager){
        Pager pager1=new Pager();
        pager1.setPage(pager.getTo());
        pager1.setLimit(pager.getLimit());
        List<JobInstance> allTaskInstance = idcTaskHistoryMapper.findAllTaskInstanceByCondition(query);
        List<JobInstance> taskInstance = idcTaskHistoryMapper.findTaskInstanceByCondition(query, pager1);
        return new PageData(allTaskInstance.size(),allTaskInstance,taskInstance);
    }

}
