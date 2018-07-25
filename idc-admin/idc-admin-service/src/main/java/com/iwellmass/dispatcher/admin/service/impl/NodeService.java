package com.iwellmass.dispatcher.admin.service.impl;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.Pager;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcNodeMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcNode;
import com.iwellmass.dispatcher.admin.dao.model.DdcNodeExample;
import com.iwellmass.dispatcher.admin.service.INodeService;
import com.iwellmass.dispatcher.admin.service.aspect.DdcPermission;
import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.entry.DDCException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NodeService implements INodeService {

    private static final int lostInterval = 45 * 1000;

    private static final int LOST = 2;
    
    private static final int NOT_READY = 3;

    @Autowired
    private DdcNodeMapper nodeMapper;

    @Override
    @DdcPermission
    public PageData<DdcNode> queryAllNodeTable(int appId, Pager page) {
        DdcNodeExample nodeExample = new DdcNodeExample();
        DdcNodeExample.Criteria nodeCriteria = nodeExample.createCriteria();
        nodeCriteria.andAppIdEqualTo(appId);
        nodeExample.setOrderByClause("NODE_IP ASC, NODE_PORT ASC");

        List<DdcNode> nodes = nodeMapper.selectByExampleWithBLOBs(nodeExample);
        long now = System.currentTimeMillis();
        for (DdcNode node : nodes) {
            if (node.getNodeStatus() == Constants.ENABLED) {
            	if(node.getLastHbTime() == null) {
            		node.setNodeStatus(NOT_READY);
            	} else if(now - node.getLastHbTime().getTime() > lostInterval) {
            		node.setNodeStatus(LOST);
            	}                
            }
        }

        return new PageData<>(nodeMapper.countByExample(nodeExample), nodes);
    }

    @Override
    @DdcPermission
    public List<DdcNode> queryNodeByApplication(int appId) {

        DdcNodeExample nodeExample = new DdcNodeExample();
        DdcNodeExample.Criteria nodeCriteria = nodeExample.createCriteria();
        nodeCriteria.andAppIdEqualTo(appId);
        nodeExample.setOrderByClause("NODE_IP ASC, NODE_PORT ASC");

        return nodeMapper.selectByExample(nodeExample);
    }

    @Override
    @DdcPermission
    public void modifyNodeStatus(int appId, int id, int status) {
        DdcNode node = new DdcNode();
        node.setId(id);
        node.setNodeStatus(status);
        node.setAppId(appId);
        nodeMapper.updateByPrimaryKeySelective(node);
    }

    @Override
    @DdcPermission
    public DdcNode getRencentActiveNode(int appId) {
        DdcNodeExample nodeExample = new DdcNodeExample();
        DdcNodeExample.Criteria nodeCriteria = nodeExample.createCriteria();
        nodeCriteria.andAppIdEqualTo(appId);
        nodeExample.setPage(new Pager(0, 1));
        nodeExample.setOrderByClause("LAST_START_TIME DESC");
        List<DdcNode> nodeList = nodeMapper.selectByExampleWithBLOBs(nodeExample);
        if (CollectionUtils.isEmpty(nodeList)) {
            return null;
        } else {
            return nodeList.get(0);
        }

    }

    @Override
    public void deleteNode(int appId, int nodeId) throws DDCException {
        DdcNodeExample nodeExample = new DdcNodeExample();
        DdcNodeExample.Criteria nodeCriteria = nodeExample.createCriteria();
        nodeCriteria.andAppIdEqualTo(appId);
        nodeCriteria.andIdEqualTo(nodeId);
        List<DdcNode> list = nodeMapper.selectByExample(nodeExample);
        if (list != null && list.size() == 1) {
            DdcNode node = list.get(0);
            if (node.getLastHbTime() == null || System.currentTimeMillis() - node.getLastHbTime().getTime() > 1000 * 60 * 5) {
                nodeMapper.deleteByPrimaryKey(nodeId);
            } else {
                throw new DDCException("只能删除失联超过5分钟的实例");
            }
        } else {
            throw new DDCException("查询数据出现异常");
        }
    }

    @Override
    @DdcPermission
    public Map<String, Integer> nodeInfo(int appId) {
        Map<String, Integer> map = new HashMap<>();

        DdcNodeExample nodeExample = new DdcNodeExample();
        DdcNodeExample.Criteria nodeCriteria = nodeExample.createCriteria();
        nodeCriteria.andAppIdEqualTo(appId);

        int running = 0;
        int stop = 0;
        int lose = 0;
        List<DdcNode> nodeList = nodeMapper.selectByExample(nodeExample);
        long now = System.currentTimeMillis();
        for (DdcNode node : nodeList) {
            if (node.getNodeStatus() == Constants.ENABLED) {
                if (node.getLastHbTime() != null && now - node.getLastHbTime().getTime() > lostInterval) {
                    lose++;
                } else {
                    running++;
                }
            } else {
                stop++;
            }
        }
        map.put("running",running);
        map.put("stop",stop);
        map.put("lose",lose);
        return map;
    }


}
