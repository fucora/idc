package com.iwellmass.ddc.server.dag;

import java.util.Collection;

/**
 * Description goes here.
 *
 * @author Lu Gan
 * @email lu.gan@dmall.com
 * @date 4/20/16
 */
public class TaskWithDependencies {

    private Integer taskId;

    private Collection<Integer> dependencies;

    public TaskWithDependencies(Integer taskId, Collection<Integer> dependencies) {
        this.taskId = taskId;
        this.dependencies = dependencies;
    }

    public Collection<Integer> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Collection<Integer> dependencies) {
        this.dependencies = dependencies;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}
