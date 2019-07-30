package com.iwellmass.idc.executor;

import com.iwellmass.idc.JobEnv;
import com.iwellmass.idc.model.JobInstanceStatus;

public interface IDCJobContext {
    JobEnv getJobEnv();

    void complete(CompleteEvent event);

    public CompleteEvent newCompleteEvent(JobInstanceStatus status);

    public ProgressEvent newProgressEvent();

    public StartEvent newStartEvent();
}
