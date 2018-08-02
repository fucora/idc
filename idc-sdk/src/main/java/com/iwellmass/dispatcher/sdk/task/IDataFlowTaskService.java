package com.iwellmass.dispatcher.sdk.task;

import java.util.List;

import com.iwellmass.dispatcher.sdk.util.IExecuteContext;

/**
 * 数据流任务接口。
 * @author Ming.Li
 *
 */
interface IDataFlowTaskService<T> {
    
    /**
     * 获取待处理的数据.
     *
     * @param params 自定义参数
     * @param ctx 任务执行上下文，主要用于缓存任务执行状态
     * @return 待处理的数据集合
     */
    public List<T> fetchData(String params, IExecuteContext ctx);
    
    /**
     * 处理数据
     *
     * @param data 待处理的数据
     * @param ctx 任务执行上下文，主要用于缓存任务执行状态
     * @return 数据是否处理成功
     */
    public boolean processData(List<T> data, IExecuteContext ctx);

    /**
     * 配置是否流式处理数据.
     * 如果流式处理数据, 将循环调用fetchData直到为空.
     * 如果非流式处理数据, 则处理数据完成后作业结束.
     *
     * @return 是否流式处理数据
     */
    public boolean isStreamingProcess();
}
