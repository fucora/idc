package com.iwellmass.idc.service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.dispatcher.admin.DDCConfiguration;
import com.iwellmass.dispatcher.admin.service.ITaskService;
import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.dispatcher.thrift.bvo.TaskTypeHelper;
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

    @Inject
    private ITaskService iTaskService;
    
    public JobInstance save(JobInstance instance){
        int insert = jobInstanceMapper.insert(instance);
        if(0==insert||-1==insert){
            throw new AppException("保存失败");
        }
        return instance;
    }

    public PageData<JobInstance>findTaskInstanceByCondition(JobQuery query, Pager pager){
        Pager pager1=new Pager();
        pager1.setPage(pager.getTo());
        pager1.setLimit(pager.getLimit());
        
        if (query != null && query.getContentType() != null	) {
        	query.setContentType(TaskTypeHelper.classNameOf(query.getContentType()));
        }
        
        List<JobInstance> allTaskInstance = idcTaskHistoryMapper.findAllTaskInstanceByCondition(query);
        List<JobInstance> taskInstance = idcTaskHistoryMapper.findTaskInstanceByCondition(query, pager1);
        taskInstance.forEach(t -> {
        	t.setContentType(TaskTypeHelper.contentTypeOf(t.getContentType()));
        });
        return new PageData<JobInstance>(allTaskInstance.size(),taskInstance);
    }

    public List<JobQuery> getAllTypes(){
        List<JobQuery> list = new ArrayList<>();
        List<JobQuery> list1 = new ArrayList<>();
        idcTaskHistoryMapper.findAllTaskInstance().forEach(i -> {
           JobQuery query=new JobQuery();
           if(!(null==i.getContentType()||i.getContentType().equals(""))){
               query.setContentType(i.getContentType());
               list.add(query);
           }
        });
        for (JobQuery type : list) {
            boolean is = list1.stream().anyMatch(t -> t.getContentType().equals(type.getContentType()));
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
            if(!(null==i.getAssignee()||i.getAssignee().equals(""))){
                query.setAssignee(i.getAssignee());
                list.add(query);
            }
        });
        for (JobQuery type : list) {
            boolean is = list1.stream().anyMatch(t -> t.getAssignee().equals(type.getAssignee()));
            if (!is) {
                list1.add(type);
            }
        }
        return list1;
    }

    public void restart(int taskId) throws DDCException {
        iTaskService.executeTask(DDCConfiguration.DEFAULT_APP,taskId);
    }

}
