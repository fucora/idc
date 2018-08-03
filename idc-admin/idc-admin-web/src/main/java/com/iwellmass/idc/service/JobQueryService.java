package com.iwellmass.idc.service;

import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.mapper.IdcTaskMapper;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobQuery;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
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
    public PageData<List<Job>>findTasksByCondition(JobQuery query, Pager pager){
        Pager pager1=new Pager();
        pager1.setPage(pager.getTo());
        pager1.setLimit(pager.getLimit());
        List<Job> allTasks = idcTaskMapper.findAllTasksByCondition(query);
        List<Job> tasks= idcTaskMapper.findTasksByCondition(query, pager1);
        return  new PageData(allTasks.size(),allTasks,tasks);
    }

    public List<Job> findTaskByGroupId(Integer id){
     List<Job> taskByGroupId = idcTaskMapper.findTaskByGroupId(id);
     return taskByGroupId;
    }

    public List<JobQuery> getAllTypes(){
        List<JobQuery> list = new ArrayList<>();
        List<JobQuery> list1 = new ArrayList<>();
        idcTaskMapper.findAllTask().forEach(i -> {
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
       idcTaskMapper.findAllTask().forEach(i -> {
            JobQuery query=new JobQuery();
            query.setAssignee(i.getAssignee());
            list.add(query);
        });
        for (JobQuery query : list) {
            boolean is = list1.stream().anyMatch(t -> t.getAssignee().equals(query.getAssignee()));
            if (!is) {
                list1.add(query);
            }
        }
        return list1;
    }

}
