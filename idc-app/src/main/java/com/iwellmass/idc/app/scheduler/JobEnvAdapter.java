package com.iwellmass.idc.app.scheduler;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.JobEnv;
import com.iwellmass.idc.model.CronType;
import lombok.Data;

import java.util.List;

@Data
public class JobEnvAdapter implements JobEnv {

    Integer instanceId;
    String jobId;
    String jobName;
    String loadDate;
    CronType scheduleType;
    List<ExecParam> parameter;
    String dispatchType;
    Long shouldFireTime;
    Long prevFireTime;
    String taskId;
    String contentType;
}
