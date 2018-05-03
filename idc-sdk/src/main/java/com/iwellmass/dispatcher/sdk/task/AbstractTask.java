package com.iwellmass.dispatcher.sdk.task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.iwellmass.dispatcher.sdk.service.ITaskService;
import com.iwellmass.dispatcher.sdk.util.IExecuteContext;
import com.iwellmass.dispatcher.sdk.util.TaskThreadFactory;

/**
 * 任务模板父类
 * 类及方法及变量都为default权限，仅可包内访问，限制业务方子类调用。
 * 
 * @author Ming.Li
 *
 */
abstract class AbstractTask implements ITaskService {

	ExecutorService executorService;
    
	/**
	 * 根据任务线程数确定是否并发运行
	 * @param ctx
	 * @return
	 */
    final boolean isConcurrent(final IExecuteContext ctx) {
    	
    	if(ctx.getThreadCount() > 1) {
    		synchronized(this) {
    			if(executorService == null) {
    				String className = this.getClass().getSimpleName();    				
    				executorService = Executors.newCachedThreadPool(new TaskThreadFactory(className + "-ExecuteThread")); 
    			}
    			return true;    			
    		}
    	}
    	return false;
    }
    
    /**
     * 获取并发线程数
     * @param ctx
     * @return
     */
    final int getThreadCount(final IExecuteContext ctx) {
		return ctx.getThreadCount();
	}

    /**
     * 等待所有线程执行完成
     * @param latch
     */
	final void latchAwait(final CountDownLatch latch) {
        try {
            latch.await();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }   
}
