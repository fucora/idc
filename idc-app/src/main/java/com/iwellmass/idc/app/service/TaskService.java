package com.iwellmass.idc.app.service;

import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.app.vo.TaskQueryVO;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskCreateVO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Inject
    private TaskRepository taskRepository;

    public List<Task> query(TaskQueryVO taskQueryVO){
        List<Task> tasks = taskRepository.findAll(new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (taskQueryVO.getTaskName() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("taskName"), taskQueryVO.getTaskName()));
                }
                if (taskQueryVO.getTeskType() != null){
                    predicates.add(criteriaBuilder.equal(root.get("taskType"), taskQueryVO.getTeskType()));
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        return tasks;
    }

    public Task item(String taskId) throws Exception {
        Optional<Task> optionalTask = taskRepository.findByTaskId(taskId);
        if (!optionalTask.isPresent()){
            // 不存在
            throw new Exception("未查找到指定任务!");
        }
        return optionalTask.get();
    }

    public Task save(TaskCreateVO taskCreateVO) {
        return taskRepository.save(new Task(taskCreateVO));
    }



}
