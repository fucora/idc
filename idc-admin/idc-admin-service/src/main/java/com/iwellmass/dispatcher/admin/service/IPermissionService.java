package com.iwellmass.dispatcher.admin.service;

/**
 * Created by xkwu on 2016/6/22.
 */
public interface IPermissionService {
    /**
     * Has permission in app boolean.
     * 判断该用户是否有该应用的权限
     *
     * @param userId the user id
     * @param appId  the app id
     * @return the boolean
     */
     boolean hasPermissionInApp(int userId, int appId);


    boolean hasAdminPermission();
}
