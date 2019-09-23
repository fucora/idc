package com.iwellmass.idc.executor;

import com.iwellmass.idc.ExecuteRequest;
import com.iwellmass.idc.JobEnv;
import com.iwellmass.idc.model.JobInstanceStatus;

public interface IDCJobContext {
    ExecuteRequest getExecuteRequest();

    void complete(CompleteEvent event);

    CompleteEvent newCompleteEvent(JobInstanceStatus status);

    ProgressEvent newProgressEvent();

    StartEvent newStartEvent();

    void fail(Throwable t);

    void progress();

    void start();

    void success();
}
