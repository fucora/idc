package com.iwellmass.idc.service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.mapper.IdcTaskHistoryMapper;
import com.iwellmass.idc.mapper.JobInstanceMapper;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobQuery;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobInstanceService {

    @Inject
    private IdcTaskHistoryMapper idcTaskHistoryMapper;
    
    @Inject
    private JobInstanceMapper jobInstanceMapper;
    
    public JobInstance save(JobInstance instance){
        int insert = jobInstanceMapper.insert(instance);
        if(0==insert||-1==insert){
            throw new AppException("保存失败");
        }
        return instance;
    }

    public PageData<List<JobInstance>>findTaskInstanceByCondition(JobQuery query, Pager pager){
        Pager pager1=new Pager();
        pager1.setPage(pager.getTo());
        pager1.setLimit(pager.getLimit());
        List<JobInstance> allTaskInstance = idcTaskHistoryMapper.findAllTaskInstanceByCondition(query);
        List<JobInstance> taskInstance = idcTaskHistoryMapper.findTaskInstanceByCondition(query, pager1);
        return new PageData(allTaskInstance.size(),taskInstance);
    }

    public List<JobQuery> getAllTypes(){
        List<JobQuery> list = new ArrayList<>();
        List<JobQuery> list1 = new ArrayList<>();
        idcTaskHistoryMapper.findAllTaskInstance().forEach(i -> {
           JobQuery query=new JobQuery();
           query.setTaskType(i.getTaskType());
           list.add(query);
        });
        for (JobQuery type : list) {
            boolean is = list1.stream().anyMatch(t -> t.getTaskType().equals(type.getTaskType()));
            if (!is) {
                list1.add(type);
            }
        }
        return list1;
    }

    public List<JobQuery> getAllAssignee(){
        List<JobQuery> list = new ArrayList<>();
        List<JobQuery> list1 = new ArrayList<>();
        idcTaskHistoryMapper.findAllTaskInstance().forEach(i -> {
            JobQuery query=new JobQuery();
            query.setAssignee(i.getAssignee());
            list.add(query);
        });
        for (JobQuery type : list) {
            boolean is = list1.stream().anyMatch(t -> t.getAssignee().equals(type.getAssignee()));
            if (!is) {
                list1.add(type);
            }
        }
        return list1;
    }

}
