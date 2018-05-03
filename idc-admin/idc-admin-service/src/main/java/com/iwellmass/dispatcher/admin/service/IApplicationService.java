package com.iwellmass.dispatcher.admin.service;

import com.iwellmass.dispatcher.admin.dao.Page;
import com.iwellmass.dispatcher.admin.dao.model.DdcApplication;
import com.iwellmass.dispatcher.admin.dao.model.DdcUser;
import com.iwellmass.dispatcher.admin.dao.model.DdcUserApplication;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;
import com.iwellmass.dispatcher.common.entry.DDCException;

import java.util.List;


/**
 * The interface Application service.
 */
public interface IApplicationService {

    /**
     * 检查应用名是否已经存在
     *
     * @param appName
     * @return
     */
    boolean checkNameExists(String appName);


    /**
     * 删除应用
     *
     * @param appId
     * @throws DDCException
     */

    void deleteApplication(int appId) throws DDCException;

    /**
     * 分页查看当前用户的应用
     *
     * @param page
     * @return
     */
    TableDataResult listApplicationTable(Page page);

    /**
     * 分页查看所有应用
     *
     * @param page
     * @return
     */
    TableDataResult listApplicationTable(DdcApplication application,Page page);

    /**
     * 获取用户权限范围类的应用列表
     *
     * @return
     */
    List<DdcApplication> listApplication(int userId);


    /**
     * 检查该应用是否已经有实例启动
     *
     * @param appId
     * @return
     */
    boolean hasNode(int appId);

    /**
     * 添加用户
     *
     * @param appId
     * @param ddcUser
     * @throws DDCException
     */

    void addAppUser(int appId, DdcUser ddcUser) throws DDCException;


    /**
     * Join application.
     *  加入应用（需管理员权限）
     * @param appId the app id
     */
    void joinApplication(int appId) throws DDCException;

    /**
     * Leave application.
     * 退出应用（需管理员权限）
     * @param appId the app id
     */
    void leaveApplication(int appId) throws DDCException;

    /**
     * 修改用户信息（电话，邮箱）
     *
     * @param ddcUser
     */
    void updateAppUser(DdcUser ddcUser);

    /**
     * 从应用中删除某个用户
     *
     * @param appId
     * @param userId
     */

    void deleteAppUser(int appId, int userId);

    /**
     * 查看应用下的用户列表
     *
     * @param appId
     * @param page
     * @return
     */

    TableDataResult listAppUser(int appId, Page page);

    /**
     * Create application.
     * 创建应用
     * @param application the application
     * @throws DDCException the ddc exception
     */
    void createApplication(DdcApplication application) throws DDCException;

    /**
     * Update application.
     * 更新应用
     * @param appId       the app id
     * @param application the application
     * @throws DDCException the ddc exception
     */
    void updateApplication(int appId, DdcApplication application) throws DDCException;


    /**
     * Enable alarm.
     * 是否接收应用的报警
     * @param appId  the app id
     * @param enableAlarm the enable alarm
     * @throws DDCException the ddc exception
     */
    void enableAlarm(int appId, boolean enableAlarm) throws DDCException;


    /**
     * Confirm user in table.
     * 检查user表是否已经有该用户信息，如果没有则从erp拉取数据
     * @param userId the user id
     * @throws DDCException the ddc exception
     */
    DdcUser confirmUserInTable(int userId) throws DDCException;

    /**
     * 查询所有用户信息
     * @return
     * @throws DDCException
     */
    List<DdcUser> queryAllUser()  throws DDCException;

    /**
     * 查询给定id应用下的用户
     * @param appId
     * @return
     */
    List<DdcUserApplication> listUserApplication(int appId);

    /**
     * 应用下人员变动
     * @param appId
     * @param deleteItems
     * @param addItems
     * @throws DDCException
     */
    void modifyUserAppInfo(int appId,int[] deleteItems,int[] addItems) throws DDCException;
}
