package com.iwellmass.dispatcher.admin.web.controller;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.Pager;
import com.iwellmass.dispatcher.admin.dao.model.DdcNode;
import com.iwellmass.dispatcher.admin.service.INodeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/node")
public class NodeController {
    private static Logger logger = LoggerFactory.getLogger(NodeController.class);

    @Autowired
    private INodeService nodeService;

    @RequestMapping(value = "/queryAllNodeTable", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult queryAllNode(int appId, Pager page) {

        ServiceResult result = new ServiceResult();
        try {
            result = nodeService.queryAllNodeTable(appId, page);
        } catch (Exception e) {
            logger.error("获取实例失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/queryNodeByApp", method = RequestMethod.GET)
    public List<DdcNode> queryNodeByApplication(int appId) {

        return nodeService.queryNodeByApplication(appId);
    }

    @RequestMapping(value = "/modifyNodeStatus")
    @ResponseBody
    public ServiceResult modifyNodeStatus(int appId, int id, int status) {
        ServiceResult result = new ServiceResult();
        try {
            nodeService.modifyNodeStatus(appId, id, status);
        } catch (Exception e) {
            logger.error("改变实例状态失败", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "getRencentActiveNode", method = RequestMethod.POST)
    @ResponseBody
    public ServiceResult getRencentActiveNode(int appId) {
        ServiceResult result = new ServiceResult();
        try {
            result.setResult(nodeService.getRencentActiveNode(appId));
        } catch (Exception e) {
            logger.error("获取最近活动node失败！！！", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/deleteNode", method = RequestMethod.POST)
    @ResponseBody public ServiceResult deleteNode(DdcNode node) {
        ServiceResult result = new ServiceResult();
        try {
            nodeService.deleteNode(node.getAppId(),node.getId());
        } catch (Exception e) {
            logger.error("删除实例失败！！！", e);
            result.setState(ServiceResult.STATE_APP_EXCEPTION);
            result.setError(e.getMessage());
        }
        return result;
    }
}
