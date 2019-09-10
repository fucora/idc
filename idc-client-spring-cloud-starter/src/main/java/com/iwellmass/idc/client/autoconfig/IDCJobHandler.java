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
                        CompleteEvent event = CompleteEvent.failureEvent(context.executeRequest.getNodeJobId())
                                .setMessage("任务 {} 执行异常: {}", cause.getMessage())
                                .setStackTraceElements(cause.getStackTrace())
                                .setEndTime(LocalDateTime.now());
                        context.complete(event);
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

            if (state == COMPLETE) {
                LOGGER.warn("[{}] job already complete {}", event.getNodeJobId());
                return;
            }

            LOGGER.info("[{}] 任务 '{}' 执行完毕, 执行结果: {}", event.getNodeJobId(),
                    executeRequest.getTaskName(),
                    event.getFinalStatus());

            try {
                idcStatusService.fireCompleteEvent(event);
                state = COMPLETE;
            } catch (Throwable e) {
                state = NOTIFY_ERROR;
                LOGGER.error("发送事件失败, EVENT: {}", event, e);
            }
        }


        public CompleteEvent newCompleteEvent(JobInstanceStatus status) {
            if (status == JobInstanceStatus.FINISHED) {
                return CompleteEvent.successEvent(executeRequest.getNodeJobId());
            } else if (status == JobInstanceStatus.FAILED) {
                return CompleteEvent.failureEvent(executeRequest.getNodeJobId());
            } else {
                return CompleteEvent.failureEvent(executeRequest.getNodeJobId()).setFinalStatus(status);
            }
        }

        public ProgressEvent newProgressEvent() {
            return ProgressEvent.newEvent(executeRequest.getNodeJobId());
        }

        public StartEvent newStartEvent() {
            return StartEvent.newEvent(executeRequest.getNodeJobId());
        }

        @Override
        public void fail(RuntimeException e) {
            if (executeRequest != null && executeRequest.getNodeJobId() != null) {
                CompleteEvent event = CompleteEvent.failureEvent(executeRequest.getNodeJobId())
                        .setMessage("任务执行异常: {}", e.getMessage())
                        .setStackTraceElements(e.getStackTrace())
                        .setEndTime(LocalDateTime.now());
                complete(event);
            }
        }
    }
}