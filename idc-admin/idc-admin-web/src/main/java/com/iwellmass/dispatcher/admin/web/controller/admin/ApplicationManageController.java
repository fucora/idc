package com.iwellmass.dispatcher.admin.web.controller.admin;

import static com.iwellmass.dispatcher.admin.web.ResultAdapter.asTableDataResult;

import com.iwellmass.dispatcher.admin.dao.Page;
import com.iwellmass.dispatcher.admin.dao.model.DdcApplication;
import com.iwellmass.dispatcher.admin.service.IApplicationService;
import com.iwellmass.dispatcher.admin.service.domain.DataResult;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by xkwu on 2016/6/20.
 */
@Controller
@RequestMapping("/admin/application")
public class ApplicationManageController {
    /**
     * The constant logger.
     */
    private static Logger logger = LoggerFactory.getLogger(ApplicationManageController.class);

    /**
     * The Application service.
     */
    @Autowired
    private IApplicationService applicationService;

    /**
     * List app table table data result.
     *  应用管理页面Table数据获取
     * @param application the application
     * @param page        the page
     * @return the table data result
     */
    @RequestMapping(value = "/listAppTable", method = RequestMethod.POST)
    @ResponseBody
    public TableDataResult listAppTable(DdcApplication application,Page page) {
        TableDataResult result = new TableDataResult();
        try {
            result = asTableDataResult(applicationService.listApplicationTable(application,page));
        } catch (Exception e) {
            logger.error("获取应用列表失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    /**
     * Join application data result.
     * 管理员加入某个应用
     * @param appId the app id
     * @return the data result
     */
    @RequestMapping(value = "/joinApplication", method = RequestMethod.POST)
    @ResponseBody
    public DataResult joinApplication(int appId) {
        DataResult result = new DataResult();
        try {
            applicationService.joinApplication(appId);
        } catch (Exception e) {
            logger.error("加入应用失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    /**
     * Join application data result.
     * 管理员退出某个应用
     * @param appId the app id
     * @return the data result
     */
    @RequestMapping(value = "/leaveApplication", method = RequestMethod.POST)
    @ResponseBody
    public DataResult leaveApplication(int appId) {
        DataResult result = new DataResult();
        try {
            applicationService.leaveApplication(appId);
        } catch (Exception e) {
            logger.error("加入应用失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }
}
