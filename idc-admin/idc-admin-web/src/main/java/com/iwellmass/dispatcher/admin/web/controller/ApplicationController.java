package com.iwellmass.dispatcher.admin.web.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.dispatcher.admin.dao.Page;
import com.iwellmass.dispatcher.admin.dao.model.DdcApplication;
import com.iwellmass.dispatcher.admin.dao.model.DdcUser;
import com.iwellmass.dispatcher.admin.service.IApplicationService;
import com.iwellmass.dispatcher.admin.service.domain.DataResult;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

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
    public DataResult createOrUpdateApplication(DdcApplication application) {
        DataResult result = new DataResult();
        try {
            if (application.getAppId() == null) {
                applicationService.createApplication(application);
            } else {
                applicationService.updateApplication(application.getAppId(), application);
            }
        } catch (Exception e) {
            logger.error("创建或者更新应用失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
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
    public DataResult deleteApplication(int appId) {
        DataResult result = new DataResult();
        try {
            applicationService.deleteApplication(appId);
        } catch (Exception e) {
            logger.error("删除应用失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/listAppTable", method = RequestMethod.POST)
    @ResponseBody
    public TableDataResult listAppTable(Page page) {
        TableDataResult result = new TableDataResult();
        try {
            result = applicationService.listApplicationTable(page);
        } catch (Exception e) {
            logger.error("获取应用列表失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/listAppUser", method = RequestMethod.POST)
    @ResponseBody
    public TableDataResult listAppUser(int appId, Page page) {
        TableDataResult result = new TableDataResult();
        try {
            result = applicationService.listAppUser(appId, page);
        } catch (Exception e) {
            logger.error("获取当前应用下用户列表失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/addAppUser", method = RequestMethod.POST)
    @ResponseBody
    DataResult addAppUser(int appId, DdcUser ddcUser) {
        DataResult result = new DataResult();
        try {
            applicationService.addAppUser(appId, ddcUser);
        } catch (Exception e) {
            logger.error("在应用中添加用户失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/updateAppUser", method = RequestMethod.POST)
    @ResponseBody
    public DataResult updateAppUser(DdcUser ddcUser) {
        DataResult result = new DataResult();
        try {
            applicationService.updateAppUser(ddcUser);
        } catch (Exception e) {
            logger.error("更新用户信息失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/delAppUser", method = RequestMethod.POST)
    @ResponseBody
    public DataResult deleteAppUser(int appId, int userId) {
        DataResult result = new DataResult();
        try {
            applicationService.deleteAppUser(appId, userId);
        } catch (Exception e) {
            logger.error("删除用户信息失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
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
    public DataResult querUserAppInfo(int appId) {
        DataResult result = new DataResult();
        try {
            result.addAttribute("list",applicationService.queryAllUser());
            result.addAttribute("userApp",applicationService.listUserApplication(appId));
        } catch (Exception e) {
            logger.error("查询用户信息失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/modifyUserAppInfo", method = RequestMethod.POST)
    @ResponseBody
    public DataResult modifyUserAppInfo(int appId, int[] deleteItems,int[] addItems) {
        DataResult result = new DataResult();
        try {
            applicationService.modifyUserAppInfo(appId, deleteItems, addItems);
        } catch (Exception e) {
            logger.error("查询用户信息失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/enableAlarm", method = RequestMethod.POST)
    @ResponseBody
    public DataResult enableAlarm(int appId,boolean status) {
        DataResult result = new DataResult();
        try {
            applicationService.enableAlarm(appId,status);
        } catch (Exception e) {
            logger.error("删除用户信息失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }
}
