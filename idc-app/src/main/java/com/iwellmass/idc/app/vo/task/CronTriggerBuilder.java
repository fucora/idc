package com.iwellmass.idc.app.vo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.app.util.IDCUtils;
import com.iwellmass.idc.model.CronType;

public interface CronTriggerBuilder {

    LocalDate getStartDate();

    LocalDate getEndDate();

    LocalTime getDuetime();

    List<Integer> getDays();

    CronType getCronType();

    default String getExpression() {
        return null;
    }

    default Trigger buildTrigger(TriggerKey key) {

        TriggerBuilder<CronTrigger> builder = TriggerBuilder.newTrigger()
                .withIdentity(key)
                .withSchedule(CronScheduleBuilder.cronSchedule(Utils.isNullOrEmpty(getExpression()) ? toCronExpression() : getExpression()));

        if (getStartDate() != null) {
            builder.startAt(IDCUtils.toDate(LocalDateTime.of(getStartDate(), LocalTime.MIN)));
        }

        if (getEndDate() != null) {
            builder.endAt(IDCUtils.toDate(LocalDateTime.of(getEndDate(), LocalTime.MAX)));
        }
        return builder.build();
    }

    default String toCronExpression() {
        List<Integer> days = getDays();
        LocalTime duetime = getDuetime();

        switch (getCronType()) {
            case MONTHLY: {
                Assert.isFalse(Utils.isNullOrEmpty(days), "月调度配置不能为空");
                boolean isLast = days.stream().filter(i -> i < 0).count() == 1;
                if (isLast && days.size() > 1) {
                    throw new AppException("最后 N 天不能使用组合配置模式");
                }
                return String.format("%s %s %s %s * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(),
                        isLast ? days.get(0) == -1 ? "L" : "L" + (days.get(0) + 1)
                                : String.join(",", days.stream().map(String::valueOf).collect(Collectors.toList())));
            }
            case WEEKLY: {
                throw new UnsupportedOperationException("not supported yet");
            }
            case DAILY:
                return String.format("%s %s %s * * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour());
            case CUSTOMER:
                return getExpression();
            default:
                throw new AppException("接收的调度类型" + Arrays.asList(CronType.values()));
        }
    }

}
