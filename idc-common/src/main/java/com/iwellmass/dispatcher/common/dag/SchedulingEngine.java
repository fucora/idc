package com.iwellmass.dispatcher.common.dag;


import java.util.List;

import com.iwellmass.dispatcher.common.entry.TaskInfoTuple;

/**
 * Description goes here.
 *
 * @author Lu Gan
 * @email lu.gan@dmall.com
 * @date 4/20/16
 */
public interface SchedulingEngine {

    String getInstanceId();

    void start();

    void shutdown();

    void shutdownGracefully();

    boolean isShutdown();

    List<Integer> findSubsequentTasks(TaskInfoTuple tuple);

    List<Integer> findSubsequentTasks(List<TaskInfoTuple> tuples);

    List<Integer> findStartTaskIds(Integer workflowId);

}
