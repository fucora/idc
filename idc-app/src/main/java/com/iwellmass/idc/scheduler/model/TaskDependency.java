package com.iwellmass.idc.scheduler.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.app.vo.task.TaskDependencyVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "updatetime")
    @ApiModelProperty("计划依赖修改日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatetime;

    @Fetch(FetchMode.SELECT)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "task_dependency_id", updatable = false)
    private List<TaskDependencyEdge> edges;

    public TaskDependency(TaskDependencyVO taskDependencyVO) {
        this.name = taskDependencyVO.getName();
        this.description = taskDependencyVO.getDescription();
        this.principle = Principle.MONTHLY_2_MONTHLY; // only support monthly2monthly
        this.updatetime = LocalDateTime.now();
    }
}
