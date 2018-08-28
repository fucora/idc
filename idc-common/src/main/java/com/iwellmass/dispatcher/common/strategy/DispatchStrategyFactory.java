package com.iwellmass.dispatcher.common.strategy;

import com.iwellmass.common.util.Utils;
import com.iwellmass.dispatcher.common.constants.Constants;

/**
 * 获取派发策略的工厂方法
 * @author duheng
 *
 */
public class DispatchStrategyFactory {

	public static AbstractDispatchStrategy getDispatchStrategy(String strategyType) {
		if(!Utils.isNullOrEmpty(strategyType)) {
			if(strategyType.equals(Constants.STRATEGY_TYPE_RANDOM)) {
				return new RandomDispatchStrategy();
			}
		}
		return null;
	}
}
