package com.iwellmass.dispatcher.sdk.task;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.sdk.util.IBasicExecuteContext;
import com.iwellmass.dispatcher.sdk.util.IExecuteContext;
import com.iwellmass.dispatcher.sdk.util.ListUtils;

/**
 * 数据流任务模板
 * @author Ming.Li
 *
 * @param <T>
 */
public abstract class AbstractDataFlowTask<T> extends AbstractTask implements IDataFlowTaskService<T> {

	private final static Logger logger = LoggerFactory.getLogger(AbstractDataFlowTask.class);
	
	@Override
	public final void execute(final String params, final IBasicExecuteContext ctx) {
		
		boolean succeed = true;
		boolean concurrency = isConcurrent(ctx);
		
        if (isStreamingProcess()) {
            succeed = executeStreamingTask(params, ctx, concurrency);
        } else {
            succeed = executeOneOffTask(params, ctx, concurrency);
        }
        if (succeed) {
            ctx.executeSucceed();
        } else {
            ctx.executeFailed();
        }
	}
	
	/**
     * 流式处理任务，本次处理完成后如还有数据，继续处理，直到数据处理完成为止
     */
    private boolean executeStreamingTask(final String params, final IBasicExecuteContext ctx, final boolean concurrency) {
    	    	
        try {
            List<T> data = fetchData(params, (IExecuteContext)ctx);
            
            while (data != null && !data.isEmpty()) {
                if (concurrency) {
                    // 并发模式
                    if (!concurrentProcessData(data, ctx)) {
                        return false;
                    }
                } else {
                    // 单线程模式
                    if (!processData(data, (IExecuteContext)ctx)) {
                        return false;
                    }
                }
                data = fetchData(params, (IExecuteContext)ctx);
            }

            return true;
        } catch (Throwable e) {
            logger.error("执行数据流任务出错：", e);
            return false;
        }
    }

    /**
     * 一次性处理任务
     */
    private boolean executeOneOffTask(final String params, final IBasicExecuteContext ctx, final boolean concurrency) {
    	
        try {
            List<T> data = fetchData(params, ctx);
            if (data != null && !data.isEmpty()) {
                if (concurrency) {
                    return concurrentProcessData(data, ctx);
                } else {
                    return processData(data, ctx);
                }
            }
            return true;
        } catch (Throwable e) {
            logger.error("执行数据流任务出错：", e);
            return false;
        }
    }

    /**
     * 并发任务执行
     * @param data
     * @param ctx
     * @return
     */
    private boolean concurrentProcessData(final List<T> data, final IBasicExecuteContext ctx) {

        // 数据切分, 优化线程数，若数据量小于设置的线程数，按数据量开启线程
    	int actualDataSize = data.size();
    	int defaultThreadCount = getThreadCount(ctx);
    	int threadCount = actualDataSize > defaultThreadCount ? defaultThreadCount : actualDataSize;
    	
        List<List<T>> splitedData = ListUtils.partition(data, threadCount);
        
        final CountDownLatch latch = new CountDownLatch(splitedData.size());
        final AtomicBoolean succeed = new AtomicBoolean(true);
        
        for (final List<T> shard : splitedData) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!processData(shard, (IExecuteContext)ctx)) {
                            succeed.set(false);
                        }
                    } catch (Throwable e) {
                        succeed.set(false);
                        logger.error("并发执行数据流任务失败：", e);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        latchAwait(latch);

        return succeed.get();
    }

}
