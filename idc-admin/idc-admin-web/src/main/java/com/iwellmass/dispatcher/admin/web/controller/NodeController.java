package com.iwellmass.dispatcher.admin.web.controller;

import com.iwellmass.dispatcher.admin.dao.Page;
import com.iwellmass.dispatcher.admin.dao.model.DdcNode;
import com.iwellmass.dispatcher.admin.service.INodeService;
import com.iwellmass.dispatcher.admin.service.domain.DataResult;
import com.iwellmass.dispatcher.admin.service.domain.TableDataResult;

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
    public TableDataResult queryAllNode(int appId, Page page) {

        TableDataResult result = new TableDataResult();
        try {
            result = nodeService.queryAllNodeTable(appId, page);
        } catch (Exception e) {
            logger.error("获取实例失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/queryNodeByApp", method = RequestMethod.GET)
    public List<DdcNode> queryNodeByApplication(int appId) {

        return nodeService.queryNodeByApplication(appId);
    }

    @RequestMapping(value = "/modifyNodeStatus")
    @ResponseBody
    public DataResult modifyNodeStatus(int appId, int id, int status) {
        DataResult result = new DataResult();
        try {
            nodeService.modifyNodeStatus(appId, id, status);
        } catch (Exception e) {
            logger.error("改变实例状态失败", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "getRencentActiveNode", method = RequestMethod.POST)
    @ResponseBody
    public DataResult getRencentActiveNode(int appId) {
        DataResult result = new DataResult();
        try {
            result.addAttribute("node", nodeService.getRencentActiveNode(appId));
        } catch (Exception e) {
            logger.error("获取最近活动node失败！！！", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/deleteNode", method = RequestMethod.POST)
    @ResponseBody public DataResult deleteNode(DdcNode node) {
        DataResult result = new DataResult();
        try {
            nodeService.deleteNode(node.getAppId(),node.getId());
        } catch (Exception e) {
            logger.error("删除实例失败！！！", e);
            result.setStatusCode(DataResult.STATUS_CODE.FAILURE);
            result.setMsg(e.getMessage());
        }
        return result;
    }
}
