# 版本更新信息

## [2.1.1](http://124.207.118.198:8090/zentao/task-view-350.html)
* 支持调度时的参数设置，支持表达式语法
* 并行控制，支持最大并发数为 5
* 修复了 Task 依赖相关的 Bug
* server 与 client 版本分离

# 系统参数

|变量|变量名|参数类型|描述|
|--|--|--|--|
|idc.shouldFireTime|调度日期|[LocalDateTime](#localdatetime)|无

# 表达式语法

## LocalDateTime

表达式|返回值|描述|示例
--|--|--|--
plusMonths(int d)|LocalDateTime|上或下 d 月|*#ldt.plusMonths(1)* 下月当前时间
plusDays(int d)|LocalDateTime|前或后 d 天|*#ldt.plusDays(-2)* 前两天当前时间
plusWeeks(int d)|LocalDateTime|上或下 d 周|*#ldt.plusWeeks(-1)* 上周当前时间
withDayOfMonth(int d)|LocalDateTime|指定日期为 d|*#ldt.withDayOfMonth(1)* 本月 1 号
format(String fmt)| String | 格式化|*#ldt.format('yyyyMMdd')* 格式化为yyyyMMdd

```java
// loadDate 为调度日期的前 20 天，格式化为  yyyyMMdd
#idc.shouldFireTime.plusDays(-20).format('yyyyMMdd')

// loadDate 为调度日上月最后一天，格式化为 yyyy-MM-dd
#idc.shouldFireTime.withDayOfMonth(1).plusDays(-1).format('yyyy-MM-dd')
```

