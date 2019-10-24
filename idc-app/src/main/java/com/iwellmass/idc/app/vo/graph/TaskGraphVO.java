package com.iwellmass.idc.app.vo.graph;

import com.iwellmass.idc.app.vo.task.TaskNodeVO;
import com.iwellmass.idc.scheduler.model.Task;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TaskGraphVO {

    private List<TaskNodeVO> nodes;

    private List<EdgeVO> edges;

    public TaskGraphVO() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }
}
