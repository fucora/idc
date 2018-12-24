package com.iwellmass.idc.app.rpc;

import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.vo.DFTaskLog;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author chenxiong
 * @email nobita0522@qq.com
 * @date 2018/12/24 20:03
 * @description
 */
@FeignClient("data-factory")
public interface DFClient {

    @GetMapping(path = "/task/{instanceId}/jobTaskLog", consumes = MediaType.APPLICATION_JSON_VALUE)
    PageData<DFTaskLog> getLogs(@PathVariable(name = "instanceId") Integer instanceId, Pager pager);
}
