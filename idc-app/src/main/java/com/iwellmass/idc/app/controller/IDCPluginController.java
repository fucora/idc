//package com.iwellmass.idc.app.controller;
//
//import com.iwellmass.common.ServiceResult;
//import io.swagger.annotations.ApiModelProperty;
//import org.springframework.data.domain.Sort;
//import org.springframework.web.bind.annotation.*;
//
//import javax.inject.Inject;
//import java.time.LocalDateTime;
//
///**
// * @author nobita chen
// * @email nobita0522@qq.com
// * @date 2019/9/26 11:26
// * @description
// */
//@RestController
//@RequestMapping("/plugin")
//public class IDCPluginController {
//
//    static final String OPT_SUCCESS = "操作成功";
//    static final String MAX_CONCURRENT = "scheduler.concurrent.max";
//    static final String CALLBACK_TIMEOUT_OPEN = "scheduler.callbackTimeout.open";
//    static final String CALLBACK_TIMEOUT = "scheduler.callbackTimeout";
//
//    @Inject
//    IDCPluginRepository idcPluginRepository;
//
//    @GetMapping
//    @ApiModelProperty("获取所有的属性")
//    public ServiceResult<List<IDCPlugin>> getAll() {
//        return ServiceResult.success(idcPluginRepository.findAll(Sort.by(Sort.Direction.DESC,"updatetime")));
//    }
//
//    @PostMapping
//    @ApiModelProperty("修改或插入指定属性与属性值")
//    public ServiceResult<String> modify(@RequestBody IDCPlugin idcPlugin) {
//        idcPlugin.setUpdatetime(LocalDateTime.now());
//        idcPluginRepository.save(idcPlugin);
//        return ServiceResult.success(OPT_SUCCESS);
//    }
//
//
//
//
//}
