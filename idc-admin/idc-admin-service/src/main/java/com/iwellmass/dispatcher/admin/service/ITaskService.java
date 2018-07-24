package com.iwellmass.dispatcher.admin.service;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.Pager;
import com.iwellmass.dispatcher.admin.dao.model.DdcTask;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskUpdateHistory;
import com.iwellmass.dispatcher.common.entry.DDCException;

import java.util.List;
import java.util.Map;

/**
 * Quartz任务操作接口类
 *
 * @author Ming.Li
 */
public interface ITaskService {

    /**
     * 删除任务
     *
     * @param taskId
     * @throws DDCException
     */
    void deleteTask(int appId,int taskId) throws DDCException;

    /**
     * 启用任务
     *
     * @param taskId
     * @throws DDCException
     */
    void enableTask(int appId,int taskId) throws DDCException;

    /**
     * 停用任务
     *
     * @param taskId
     * @throws DDCException
     */
    void disableTask(int appId,int taskId) throws DDCException;

    /**
     * 手动执行任务
     *
     * @param task
     */
    void executeTask(int appId,int taskId) throws DDCException;

    /**
     * 分页查询所有任务
     *
     * @param task
     * @param page
     * @return
     */

    ServiceResult taskTable(int appId, DdcTask task, Pager page);

    /**
     * 创建或修改任务
     *
     * @param task
     * @throws DDCException
     */

    void createOrUpdateTask(int appId, DdcTask task) throws DDCException;

    /**
     * 获取应用下所有流程子任务
     *
     * @param appId
     * @return
     */

    List<DdcTask> listSubTask(int appId);

    /**
     * 保存流程模板
     *
     * @param json
     */
    void saveWorkFlow(String json);

    /**
     * 获取流程模板
     *
     * @param taskId
     * @return
     */
    Object getWorkFlow(int taskId);

    /**
     * Task info map.
     * 应用下任务信息（运行中、停用、最近7天执行任务成功次数、最近7天任务执行失败次数）
     * @param appId the app id
     * @return the map
     */
    Map<String,Integer> taskInfo(int appId);
    /**
     * 分页查询任务的变更记录
     *
     * @param history
     * @param page
     * @return
     */

    ServiceResult taskUpdateHistoryTable(DdcTaskUpdateHistory history, Pager page);
}
