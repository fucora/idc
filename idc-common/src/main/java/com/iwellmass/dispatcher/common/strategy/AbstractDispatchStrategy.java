package com.iwellmass.dispatcher.common.strategy;

import java.util.ArrayList;
import java.util.List;

import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.dispatcher.common.model.DdcNode;
import com.iwellmass.dispatcher.common.task.DmallTask;

/**
 * 分配策略抽象类
 * @author duheng
 *
 */
public abstract class AbstractDispatchStrategy {

	public abstract DdcNode select(List<DdcNode> ddcNodes, List<DdcNode> selectedNodes) throws DDCException;
	
	public List<DdcNode> checkNodes(List<DdcNode> ddcNodes, List<DdcNode> selectedNodes) throws DDCException {
		if(ddcNodes == null || ddcNodes.size() == 0) {
			DmallTask.getLogger().error("未找到实例，无法进行任务分派");
			return null;
		} else if(ddcNodes.size() <= selectedNodes.size()) {
			DmallTask.getLogger().error("所有实例都不可用，无法进行任务分派");
			return null;
		}
		//过滤可用的节点
		List<DdcNode> currentNodes = new ArrayList<DdcNode>();
		for(DdcNode ddcNode : ddcNodes) {
			if(!selectedNodes.contains(ddcNode)) {
				currentNodes.add(ddcNode);
			}
		}
		if(currentNodes.size() == 0) {
			return null;
		}
		return currentNodes;
	}
}
