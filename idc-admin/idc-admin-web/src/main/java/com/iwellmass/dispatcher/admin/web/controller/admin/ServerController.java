package com.iwellmass.dispatcher.admin.web.controller.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.service.IServerService;

/**
 * Created by xkwu on 2016/6/20.
 */
@Controller
@RequestMapping("/admin/server")
public class ServerController {
    private static Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private IServerService iServerService;

    @RequestMapping(value = "/listServerTable", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult listServerTable() {
        ServiceResult result = new ServiceResult();
        try {
            result = iServerService.listServerTable();
        } catch (Exception e) {
            logger.error("获取服务器列表失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }
}
