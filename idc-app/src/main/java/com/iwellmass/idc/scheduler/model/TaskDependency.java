package com.iwellmass.idc.scheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "idc_task_dependency")
@NoArgsConstructor
@AllArgsConstructor
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("记录id")
    private Long id;

    @Column(name = "name")
    @ApiModelProperty("计划依赖图名称")
    private String name;

    @Column(name = "description")
    @ApiModelProperty("计划依赖描述")
    private String description;

    @Column(name = "principle")
    @ApiModelProperty("计划依赖规则")
    @Enumerated(value = EnumType.STRING)
    private Principle principle;

    @Column(name = "create_time")
    @ApiModelProperty("计划依赖修改日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatetime;
}
