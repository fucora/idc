package com.iwellmass.idc.scheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.common.criteria.Equal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.helpers.MessageFormatter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "idc_execution_log")
@Data
@NoArgsConstructor
public class ExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "job_id")
    private String jobId;

    @Column(name = "time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime time;

    @Column(name = "message", length = 1000)
    private String message;

    @Column(name = "detail", columnDefinition = "LONGTEXT")
    @ApiModelProperty("错误日志,堆栈信息")
    private String detail;

    public ExecutionLog(String jobId, String message, String detail) {
        this.jobId = jobId;
        this.message = message;
        this.detail = detail;
        this.time = LocalDateTime.now();
    }

    public static ExecutionLog createLog(String jobId, String message, Object... args) {
        return createLog(jobId, message, null, args);
    }

    public static ExecutionLog createLog(String jobId, String message, String detail, Object... args) {
        ExecutionLog log = new ExecutionLog(jobId, message, detail);
        log.setJobId(jobId);
        log.setMessage(message);
        if (message != null) {
            if (args == null || args.length == 0) {
                log.setMessage(message);
            } else {
                log.setMessage(MessageFormatter.arrayFormat(message, args).getMessage());
            }
        }
        log.setTime(LocalDateTime.now());
        return log;
    }

}
