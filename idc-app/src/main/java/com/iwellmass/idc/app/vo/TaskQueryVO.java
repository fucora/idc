package com.iwellmass.idc.app.vo;

import com.iwellmass.common.criteria.CustomCriteria;
import com.iwellmass.common.criteria.Equal;
import com.iwellmass.idc.model.TaskType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskQueryVO {

	@Equal
    private TaskType taskType;

    @CustomCriteria(builder = TaskNameLikeOrIdEqualCriteria.class)
    private String taskName;
}
