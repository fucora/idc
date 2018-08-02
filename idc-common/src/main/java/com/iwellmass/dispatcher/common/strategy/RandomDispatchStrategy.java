package com.iwellmass.dispatcher.common.strategy;

import java.util.List;
import java.util.Random;

import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.dispatcher.common.model.DdcNode;

/**
 * 随机分配算法
 * @author duheng
 *
 */
public class RandomDispatchStrategy extends AbstractDispatchStrategy {

	@Override
	public DdcNode select(List<DdcNode> ddcNodes, List<DdcNode> selectedNodes) throws DDCException {
		List<DdcNode> currentNodes = checkNodes(ddcNodes, selectedNodes);
		if(currentNodes != null) {
			return currentNodes.get((new Random()).nextInt(currentNodes.size()));
		} 
		return null;
	}

}
