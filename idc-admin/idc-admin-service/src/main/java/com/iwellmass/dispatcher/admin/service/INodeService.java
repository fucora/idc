package com.iwellmass.dispatcher.admin.service;

import java.util.List;
import java.util.Map;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.util.PageData;
import com.iwellmass.dispatcher.admin.dao.IDCPager;
import com.iwellmass.dispatcher.admin.dao.model.DdcNode;
import com.iwellmass.dispatcher.common.entry.DDCException;


/**
 * The interface Node service.
 */
public interface INodeService {

    /**
     * Query all node table table data result.
     *
     * @param appId the app id
     * @param page  the page
     * @return the table data result
     */
	PageData<DdcNode> queryAllNodeTable(int appId, IDCPager page);


    /**
     * Query node by application list.
     *
     * @param appId the app id
     * @return the list
     */
    List<DdcNode> queryNodeByApplication(int appId);

    /**
     * Modify node status.
     *
     * @param appId  the app id
     * @param id     the id
     * @param status the status
     */
    void modifyNodeStatus(int appId,int id, int status);

    /**
     * Gets rencent active node.
     * 最近活动节点
     * @param appId the app id
     * @return the rencent active node
     */
    DdcNode getRencentActiveNode(int appId);

    /**
     * Delete node.
     * 删除节点
     * @param appId  the app id
     * @param nodeId the node id
     * @throws DDCException the ddc exception
     */
    void deleteNode(int appId,int nodeId) throws DDCException;


    /**
     * Node info map.
     * 应用下的实例信息（运行中、停用、失联）统计
     * @param appId the app id
     * @return the map
     */
    Map<String,Integer> nodeInfo(int appId);
}
