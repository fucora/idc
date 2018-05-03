package com.iwellmass.dispatcher.sdk.task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.sdk.util.IBasicExecuteContext;
import com.iwellmass.dispatcher.sdk.util.IExecuteContext;

/**
 * 简单任务模板
 */
public abstract class AbstractSimpleTask extends AbstractTask implements ISimpleTaskService {

	private final static Logger logger = LoggerFactory.getLogger(AbstractSimpleTask.class);
	
	@Override
	public final void execute(final String params, final IBasicExecuteContext ctx) {
		
		if (isConcurrent(ctx)) { //并发执行
			
			int threadCount = getThreadCount(ctx);
            final CountDownLatch latch = new CountDownLatch(threadCount);
            final AtomicBoolean succeed = new AtomicBoolean(true);
            
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!executeTask(params, (IExecuteContext)ctx)) {
                                succeed.set(false);
                            }
                        } catch (Throwable e) {
                            succeed.set(false);
                            logger.error("并发执行任务失败：", e);
                        } finally {
                            latch.countDown();
                        }
                    }
                });
            }
            
            latchAwait(latch);

            if (succeed.get()) {
                ctx.executeSucceed();;
            } else {
                ctx.executeFailed();;
            }
        } else {
            // 单线程模式
            try {
                if (executeTask(params, (IExecuteContext)ctx)) {
                    ctx.executeSucceed();
                } else {
                    ctx.executeFailed();
                }
            } catch (Throwable ex) {
                ctx.executeFailed();
                logger.error("执行任务失败：", ex);                
            }
        }		
	}
}
