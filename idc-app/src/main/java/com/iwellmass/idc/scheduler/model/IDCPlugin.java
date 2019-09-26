//package com.iwellmass.idc.scheduler.model;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.ToString;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.Table;
//import java.time.LocalDateTime;
//
///**
// * @author nobita chen
// * @email nobita0522@qq.com
// * @date 2019/9/26 11:08
// * @description
// */
//@Getter
//@Setter
//@Entity
//@Table(name = "idc_plugin")
//@ToString
//public class IDCPlugin {
//
//    /**
//     * 属性名
//     */
//    @Id
//    @Column(name = "prop_name")
//    private  String propName;
//
//    /**
//     * 属性值
//     */
//    @Column(name = "prop_value")
//    private  String propValue;
//
//    /**
//     * 描述
//     */
//    @Column(name = "description")
//    private  String description;
//
//    /**
//     * 更新时间
//     */
//    @Column(name = "updatetime")
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private LocalDateTime updatetime;
//
//
//
//}
