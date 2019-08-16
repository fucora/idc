package com.iwellmass.idc.app.rpc;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.datafactory.common.vo.TaskDetailVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author nobita chen
 * @email nobita0522@qq.com
 * @date 2019/8/16 16:29
 * @description
 */
@FeignClient(name = "data-factory")
public interface DFTaskService {

    @PostMapping("/task/infos")
    ServiceResult<List<TaskDetailVO>> batchQueryTaskInfo(@RequestBody List<Long> taskIds);
}
