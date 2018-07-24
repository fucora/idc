package com.iwellmass.dispatcher.admin.service.impl;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcServerMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcServer;
import com.iwellmass.dispatcher.admin.dao.model.DdcServerExample;
import com.iwellmass.dispatcher.admin.service.IServerService;
import com.iwellmass.dispatcher.admin.service.aspect.DdcAdminPermission;
import com.iwellmass.dispatcher.common.constants.Constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by xkwu on 2016/6/20.
 */
@Service
public class ServerService implements IServerService {

    private static final int lostInterval = 15 * 1000;

    private static final int LOST = 2;

    @Autowired
    private DdcServerMapper ddcServerMapper;

    @Override
    @DdcAdminPermission
    public ServiceResult listServerTable() {

        List<DdcServer> servers = ddcServerMapper.selectByExample(new DdcServerExample());
        long now = System.currentTimeMillis();
        for (DdcServer server : servers) {
            if (server.getStatus() == Constants.ENABLED) {
                if (server.getLastHbTime() != null && now - server.getLastHbTime().getTime() > lostInterval) {
                    server.setStatus(LOST);
                }
            }
        }
        return new ServiceResult(servers);
    }
}
