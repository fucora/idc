package com.iwellmass.dispatcher.sdk.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 检查内存中是否有重复接收的executeId
 * 
 * @author upton
 *
 */
public class DuplicateExecuteChecker {
    // 缓存最大1000个过去接收到的executeId
    private static final int MAX_SIZE = 1000;
    
    private static final String POUND = "#";

    private static LinkedHashMap<String, Boolean> lru = new LinkedHashMap<String, Boolean>(MAX_SIZE) {
        private static final long serialVersionUID = -4065715505001611671L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Boolean> eldest) {
            return size() > MAX_SIZE;
        }
    };
    
    /**
     * 检查是否有重复的接收的task（任务超时重试时executeId不变，dispatchCount递增）
     * 
     * @param taskId       任务编号
     * @param executeId		任务执行编号
     * @param dispatchCount	任务派发次数
     * @return true ：是重复接收的executeId和dispatchCount，如果参数是null，那么默认为不是重复接收的executeId
     *         false ：不是重复接收的executeId或dispatchCount
     */
    public synchronized static boolean checkExecuteId(int taskId, Long executeId, int dispatchCount) {
        if (executeId == null) {
            return true;
        }

        boolean result = false;
        String key = taskId + POUND + executeId + POUND + dispatchCount;
        
        if (lru.containsKey(key)) {
            result = true;
        } else {
            lru.put(key, Boolean.TRUE);
        }
        return result;
    }
    
    public static void removeExecuteId(int taskId, Long executeId, int dispatchCount) {
    	if (executeId == null) {
            return;
        }

        String key = taskId + POUND + executeId + POUND + dispatchCount;        
        lru.remove(key);
    }
}
