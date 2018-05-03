package com.iwellmass.dispatcher.admin.service.impl;

import com.iwellmass.dispatcher.admin.dao.model.DdcApplication;
import com.iwellmass.dispatcher.admin.service.IApplicationService;
import com.iwellmass.dispatcher.admin.service.IPermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by xkwu on 2016/6/22.
 */
@Service
public class PermissionService implements IPermissionService {

    @Value("#{'${ddc.admin.user}'.split(',')}")
    private Integer[] adminUsers;

    @Autowired
    private IApplicationService applicationService;

    @Override
    public boolean hasPermissionInApp(int userId, int appId)  {
        List<DdcApplication> applicationList = applicationService.listApplication(userId);
        if (!CollectionUtils.isEmpty(applicationList)) {
            for (DdcApplication application : applicationList) {
                if (application.getAppId().intValue() == appId) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasAdminPermission() {
//        for(int id : adminUsers){
//            if(id == LoginContext.getLoginContext().getLongId()){
//                return true;
//            }
//        }
//        return false;
        return true;
    }
}
