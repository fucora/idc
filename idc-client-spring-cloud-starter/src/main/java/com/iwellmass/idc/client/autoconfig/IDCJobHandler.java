package com.iwellmass.idc.client.autoconfig;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.ExecuteRequest;
import com.iwellmass.idc.executor.*;
import com.iwellmass.idc.model.JobInstanceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 调度器 Rest 接口
 */
public class IDCJobHandler implements IDCJobExecutorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IDCJobHandler.class);

    private static final int RUNNING = 0x01;
    private static final int COMPLETE = 0x02;
    private static final int NOTIFY_ERROR = 0x03;
    private static final int FAIL = 0x04;

    private static final int MAX_RETRY_PUSH_COUNT = 3; // 消息失败的最大推送次数
    private static final long WAIT_TIME_ON_ERROR = 500L;// 失败时下一次通知的等待时间
    private final IDCJob job;

    @Inject
    private IDCStatusService idcStatusService;

    @Inject
    @Named("idc-executor")
    private AsyncTaskExecutor executor;

    public IDCJobHandler(IDCJob job) {
        this.job = job;
    }

    @ResponseBody
    @PostMapping(path = "/execution")
    public ServiceResult<String> doExecute(@RequestBody ExecuteRequest executeRequest) {
        LOGGER.info("[{}] job '{}' accepted, taskId: {}", executeRequest.getNodeJobId(),
                executeRequest.getTaskName(), executeRequest.getTaskId());


        Map<String, String> ps = null;
        List<ExecParam> eps = executeRequest.getParams();
        if (eps != null && !eps.isEmpty()) {
            ps = new HashMap<>();
            for (ExecParam ep : eps) {
                ps.put(ep.getName(), ep.getValue());
            }
        }

        LOGGER.info("[{}] parameter: {}", executeRequest.getNodeJobId(), executeRequest.getTaskName(), ps);
        // safe execute
        execute(executeRequest);
        return ServiceResult.success("任务已提交");
    }

    public void execute(ExecuteRequest executeRequest) {

        ExecutionContextImpl context = new ExecutionContextImpl();
        context.executeRequest = executeRequest;

        CompletableFuture.runAsync(() -> job.execute(context), executor)
                .whenComplete((_void, cause) -> {
                    if (cause != null) {
                        context.fail(cause);
                    } else {
                        context.success();
                    }
                });
    }

    private class ExecutionContextImpl implements IDCJobContext {

        private ExecuteRequest executeRequest;
        private int state = RUNNING; // TODO use CAS

        @Override
        public ExecuteRequest getExecuteRequest() {
            return executeRequest;
        }

        @Override
        public void complete(CompleteEvent event) {
            Objects.requireNonNull(event, "event 不能为空");

            if (state == COMPLETE || state == FAIL) {
                return;
            }

            LOGGER.info("任务[{}], id[{}] 执行完毕, 执行结果: {}",
                    executeRequest.getNodeTaskTaskName(),
                    event.getNodeJobId(),
                    event.getFinalStatus());

            notifyIDCServer(event);
        }

        @Override
        public void fail(Throwable t) {
            if (executeRequest != null && executeRequest.getNodeJobId() != null) {
                CompleteEvent event = CompleteEvent.failureEvent(executeRequest.getNodeJobId(), executeRequest.getNodeTaskTaskName())
                        .setMessage("任务执行异常: {}", t.getMessage())
                        .setThrowable(t)
                        .setEndTime(LocalDateTime.now());
                complete(event);
            }
        }

        @Override
        public void fail(String logs) {
            CompleteEvent event = CompleteEvent.failureEvent(executeRequest.getNodeJobId(), executeRequest.getNodeTaskTaskName())
                    .setMessage("任务执行异常: {}", logs)
                    .setThrowable(new Throwable(logs))
                    .setEndTime(LocalDateTime.now());
            complete(event);
        }

        @Override
        public void success() {
            complete(newCompleteEvent(JobInstanceStatus.FINISHED));
        }

        @Override
        public void progress() {
            progress(null);
        }

        @Override
        public void progress(String msg) {
            ProgressEvent event = ProgressEvent.newEvent(executeRequest.getNodeJobId()).setMessage(msg);
            notifyIDCServer(event);
        }

        @Override
        public void start() {
            StartEvent event = StartEvent.newEvent(executeRequest.getNodeJobId());
            notifyIDCServer(event);
        }

        public CompleteEvent newCompleteEvent(JobInstanceStatus status) {
            if (status == JobInstanceStatus.FINISHED) {
                return CompleteEvent.successEvent(executeRequest.getNodeJobId(), executeRequest.getNodeTaskTaskName());
            } else if (status == JobInstanceStatus.FAILED) {
                return CompleteEvent.failureEvent(executeRequest.getNodeJobId(), executeRequest.getNodeTaskTaskName());
            } else {
                return CompleteEvent.failureEvent(executeRequest.getNodeJobId(), executeRequest.getNodeTaskTaskName()).setFinalStatus(status);
            }
        }

        public ProgressEvent newProgressEvent() {
            return ProgressEvent.newEvent(executeRequest.getNodeJobId());
        }

        public StartEvent newStartEvent() {
            return StartEvent.newEvent(executeRequest.getNodeJobId());
        }

        private synchronized void modifyState(CompleteEvent event) {
            if (event == null) {
                this.state = NOTIFY_ERROR;
                return;
            }
            if (event.getFinalStatus().equals(JobInstanceStatus.FINISHED)) {
                this.state = COMPLETE;
            } else if (event.getFinalStatus().equals(JobInstanceStatus.FAILED)) {
                this.state = FAIL;
            }
        }

        private synchronized void notifyIDCServer(IDCJobEvent event) {
            for (int i = 0; i < MAX_RETRY_PUSH_COUNT; i++) {
                try {
                    if (event instanceof CompleteEvent) {
                        idcStatusService.fireCompleteEvent((CompleteEvent) event);
                        modifyState((CompleteEvent)event);
                    } else if (event instanceof StartEvent) {
                        idcStatusService.fireStartEvent((StartEvent) event);
                    } else if (event instanceof ProgressEvent) {
                        idcStatusService.fireProgressEvent((ProgressEvent) event);
                    }
                    return;
                } catch (Throwable e) {
                    modifyState(null);
                    LOGGER.error("发送事件失败, EVENT: {},第{}次推送", event, i + 1);
                    try {
                        Thread.sleep(WAIT_TIME_ON_ERROR);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

    }
}