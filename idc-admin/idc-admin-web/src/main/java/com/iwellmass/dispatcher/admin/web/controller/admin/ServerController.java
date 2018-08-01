package com.iwellmass.dispatcher.admin.web.controller.admin;

import static com.iwellmass.dispatcher.admin.web.ResultAdapter.asTableDataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.dispatcher.admin.service.IServerService;
import com.iwellmass.dispatcher.admin.service.domain.DataResult;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

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
    public TableDataResult listServerTable() {
        TableDataResult result = new TableDataResult();
        try {
            result = asTableDataResult(iServerService.listServerTable());
        } catch (Exception e) {
            logger.error("获取服务器列表失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }
}
