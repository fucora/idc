CREATE INDEX `IDX_ALARM_DATE` ON `DDC_ALARM_HISTORY` (`ALARM_DATE`, `TASK_ID`, `APP_ID`);

DROP TABLE IF EXISTS DDC_TASK_UPDATE_HISTORY;

/*==============================================================*/
/* Table: DDC_TASK_UPDATE_HISTORY                               */
/*==============================================================*/
CREATE TABLE DDC_TASK_UPDATE_HISTORY
(
   ID                   INT NOT NULL AUTO_INCREMENT COMMENT '编号',
   TASK_ID              INT NOT NULL COMMENT '任务编号',
   UPDATE_DETAIL        LONGTEXT COMMENT '任务修改内容',
   UPDATE_USER          VARCHAR(50) NOT NULL COMMENT '修改人',
   UPDATE_TIME          DATETIME NOT NULL COMMENT '修改时间',
   PRIMARY KEY (ID)
);

/*==============================================================*/
/* Index: IDX_TASK_HISTORY_TASKID                               */
/*==============================================================*/
CREATE INDEX IDX_TASK_HISTORY_TASKID ON DDC_TASK_UPDATE_HISTORY
(
   TASK_ID,
   UPDATE_TIME
);