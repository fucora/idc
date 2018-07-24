package com.iwellmass.dispatcher.admin.web.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.Pager;
import com.iwellmass.dispatcher.admin.dao.model.DdcApplication;
import com.iwellmass.dispatcher.admin.dao.model.DdcUser;
import com.iwellmass.dispatcher.admin.service.IApplicationService;

@Controller
@RequestMapping("application")
public class ApplicationController {

    private static Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    private IApplicationService applicationService;


    /**
     * Create or update application data result.
     * 创建或者编辑更新应用
     *
     * @param application the application
     * @return the data result
     */
    @RequestMapping(value = "/createOrUpdateApplication", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult createOrUpdateApplication(DdcApplication application) {
        ServiceResult result = new ServiceResult();
        try {
            if (application.getAppId() == null) {
                applicationService.createApplication(application);
            } else {
                applicationService.updateApplication(application.getAppId(), application);
            }
        } catch (Exception e) {
            logger.error("创建或者更新应用失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

    /**
     * Delete application data result.
     * 删除应用
     * @param appId the app id
     * @return the data result
     */
    @RequestMapping(value = "/deleteApp", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult deleteApplication(int appId) {
        ServiceResult result = new ServiceResult();
        try {
            applicationService.deleteApplication(appId);
        } catch (Exception e) {
            logger.error("删除应用失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/listAppTable", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult listAppTable(Pager page) {
        ServiceResult result = new ServiceResult();
        try {
            result = applicationService.listApplicationTable(page);
        } catch (Exception e) {
            logger.error("获取应用列表失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/listAppUser", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult listAppUser(int appId, Pager page) {
        ServiceResult result = new ServiceResult();
        try {
            result = applicationService.listAppUser(appId, page);
        } catch (Exception e) {
            logger.error("获取当前应用下用户列表失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/addAppUser", method = RequestMethod.POST)
    @ResponseBody
    ServiceResult addAppUser(int appId, DdcUser ddcUser) {
        ServiceResult result = new ServiceResult();
        try {
            applicationService.addAppUser(appId, ddcUser);
        } catch (Exception e) {
            logger.error("在应用中添加用户失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/updateAppUser", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult updateAppUser(DdcUser ddcUser) {
        ServiceResult result = new ServiceResult();
        try {
            applicationService.updateAppUser(ddcUser);
        } catch (Exception e) {
            logger.error("更新用户信息失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/delAppUser", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult deleteAppUser(int appId, int userId) {
        ServiceResult result = new ServiceResult();
        try {
            applicationService.deleteAppUser(appId, userId);
        } catch (Exception e) {
            logger.error("删除用户信息失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

/*    @RequestMapping(value = "/queryErpUser", method = RequestMethod.POST)
    @ResponseBody
    public Object queryErpUser(int erpId) {
        return IErpUserInfoDubboService.getUserById(erpId);
    }*/

    @RequestMapping(value = "/querUserAppInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult querUserAppInfo(int appId) {
        ServiceResult result = new ServiceResult();
        try {
            result.setResult(applicationService.queryAllUser());
            result.setResult(applicationService.listUserApplication(appId));
        } catch (Exception e) {
            logger.error("查询用户信息失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/modifyUserAppInfo", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult modifyUserAppInfo(int appId, int[] deleteItems,int[] addItems) {
        ServiceResult result = new ServiceResult();
        try {
            applicationService.modifyUserAppInfo(appId, deleteItems, addItems);
        } catch (Exception e) {
            logger.error("查询用户信息失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/enableAlarm", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult enableAlarm(int appId,boolean status) {
        ServiceResult result = new ServiceResult();
        try {
            applicationService.enableAlarm(appId,status);
        } catch (Exception e) {
            logger.error("删除用户信息失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }
}
