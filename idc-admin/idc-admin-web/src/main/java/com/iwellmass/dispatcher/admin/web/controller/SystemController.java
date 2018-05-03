package com.iwellmass.dispatcher.admin.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.dispatcher.admin.service.IApplicationService;
import com.iwellmass.dispatcher.admin.service.IPermissionService;
import com.iwellmass.dispatcher.admin.service.domain.DataResult;


/**
 * Created by xkwu on 2016/5/10.
 */
@RequestMapping("system")
@Controller
public class SystemController {
    private static Logger logger = LoggerFactory.getLogger(SystemController.class);
    /**
     * The Application service.
     */
    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IPermissionService permissionService;
    /**
     * Gets login user.
     * 获取登录用户的相关信息
     * @return the login user
     */
    @RequestMapping("getLoginUser")
    @ResponseBody
    public DataResult getLoginUser() {
        DataResult result = new DataResult();
        try {
            result.addAttribute("loginUser", applicationService.confirmUserInTable(1));
            result.addAttribute("apps",applicationService.listApplication(1));
            result.addAttribute("isAdmin",permissionService.hasAdminPermission());
        } catch (Exception e) {
            logger.error("获取登录用户的相关信息失败！", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }
}
