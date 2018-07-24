package com.iwellmass.dispatcher.admin.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.Pager;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcApplicationMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcNodeMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcUserAlarmMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcUserApplicationMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcUserMapper;
import com.iwellmass.dispatcher.admin.dao.model.DdcApplication;
import com.iwellmass.dispatcher.admin.dao.model.DdcApplicationEx;
import com.iwellmass.dispatcher.admin.dao.model.DdcApplicationExample;
import com.iwellmass.dispatcher.admin.dao.model.DdcNodeExample;
import com.iwellmass.dispatcher.admin.dao.model.DdcTaskExample;
import com.iwellmass.dispatcher.admin.dao.model.DdcUser;
import com.iwellmass.dispatcher.admin.dao.model.DdcUserAlarm;
import com.iwellmass.dispatcher.admin.dao.model.DdcUserAlarmExample;
import com.iwellmass.dispatcher.admin.dao.model.DdcUserApplication;
import com.iwellmass.dispatcher.admin.dao.model.DdcUserApplicationExample;
import com.iwellmass.dispatcher.admin.dao.model.DdcUserExample;
import com.iwellmass.dispatcher.admin.service.IApplicationService;
import com.iwellmass.dispatcher.admin.service.aspect.DdcAdminPermission;
import com.iwellmass.dispatcher.admin.service.aspect.DdcPermission;
import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.dispatcher.common.utils.UUIDUtils;

@Service
public class ApplicationServiceImpl implements IApplicationService {
    private static Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);
    @Autowired
    private DdcApplicationMapper appMapper;

    @Autowired
    private DdcTaskMapper ddcTaskMapper;

    @Autowired
    private DdcNodeMapper nodeMapper;

    @Autowired
    private DdcUserMapper userMapper;

    @Autowired
    private DdcUserApplicationMapper userAppMapper;

    @Autowired
    private DdcUserAlarmMapper userAlarmMapper;


    private LoadingCache<Integer, List<DdcApplication>> userAppListCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES).expireAfterAccess(5, TimeUnit.MINUTES).maximumSize(1000)
            .build(new CacheLoader<Integer, List<DdcApplication>>() {
                @Override
                public List load(Integer integer) throws Exception {
                    return findApplicationByUserId(integer);
                }
            });

    /**
     * Invalidate cache.
     * 更新该用户的cache
     */
    private void invalidateCache() {
        userAppListCache.invalidateAll();
    }


    /**
     * Create application.
     * 创建应用
     *
     * @param application the application
     * @throws DDCException the ddc exception
     */
    @Transactional
    public void createApplication(DdcApplication application) throws DDCException {
        if (StringUtils.isBlank(application.getAppName())) {
            throw new DDCException("应用名称不能为空！");
        }

        if (checkNameExists(application.getAppName())) {
            throw new DDCException("应用名称已经存在！");
        }

        String appKey = UUIDUtils.newUuid();

        Date now = new Date();
        application.setAppStatus(Constants.ENABLED);
        application.setAppKey(appKey);
        application.setCreateUser("admin");
        application.setCreateTime(now);
        application.setUpdateUser("admin");
        application.setUpdateTime(now);

        appMapper.insertSelective(application);

        DdcUserApplication record = new DdcUserApplication();
        record.setUserId(1);
        record.setLoginName("admin");
        record.setAppId(application.getAppId());
        record.setCreateUser("admin");
        record.setCreateTime(now);

        userAppMapper.insertSelective(record);

        //检查user表是否已经有该用户信息，如果没有则从erp拉取数据
        confirmUserInTable(1);

        //创建应用，默认将创建者加入报警
        enableAlarm(application.getAppId(), true);

        invalidateCache();
    }


    /**
     * Confirm user in table.
     * 检查user表是否已经有该用户信息，如果没有则从erp拉取数据
     *
     * @param userId the user id
     * @throws DDCException the ddc exception
     */
    public DdcUser confirmUserInTable(int userId) throws DDCException {
        DdcUserExample ddcUserExample = new DdcUserExample();
        DdcUserExample.Criteria userCriteria = ddcUserExample.createCriteria();
        userCriteria.andUserIdEqualTo(userId);
        List<DdcUser> userList = userMapper.selectByExample(ddcUserExample);
        if (userList == null || userList.size() == 0) {
            DdcUser ddcUser;
            Date now = new Date();
            try {
                ddcUser = new DdcUser();
                ddcUser.setCreateTime(now);
                ddcUser.setCreateUser("admin");
                ddcUser.setUpdateTime(now);
                ddcUser.setUpdateUser("admin");
                ddcUser.setLoginName("admin");
                ddcUser.setUserName("test");
                ddcUser.setUserPhone("");
                ddcUser.setUserEmail("");
                ddcUser.setUserId(userId);

                userMapper.insertSelective(ddcUser);
            } catch (Exception e) {
                logger.error(String.format("从erp获取用户信息失败,用户的erpId为%d", 1), e);
                throw new DDCException("从erp获取用户信息失败！");
            }
            return ddcUser;
        } else {
            return userList.get(0);
        }
    }

    @Override
    public List<DdcUser> queryAllUser() throws DDCException {
        DdcUserExample ddcUserExample = new DdcUserExample();
        List<DdcUser> userList = userMapper.selectByExample(ddcUserExample);
        return userList;
    }


    @Override
    public List<DdcUserApplication> listUserApplication(int appId) {
        DdcUserApplicationExample example = new DdcUserApplicationExample();
        DdcUserApplicationExample.Criteria criteria = example.createCriteria();
        criteria.andAppIdEqualTo(appId);
        criteria.andUserTypeEqualTo(1);//1为普通用户
        return userAppMapper.selectByExample(example);
    }

    @Override
    @DdcPermission
    @Transactional
    public void modifyUserAppInfo(int appId, int[] deleteItems, int[] addItems) throws DDCException {
        for (int userId : deleteItems) {
            deleteAppUser(appId, userId);
        }
        for (int userId : addItems) {
            DdcUserApplicationExample ddcUserApplicationExample = new DdcUserApplicationExample();
            DdcUserApplicationExample.Criteria userApplicationCriteria = ddcUserApplicationExample.createCriteria();
            userApplicationCriteria.andUserIdEqualTo(userId);
            userApplicationCriteria.andAppIdEqualTo(appId);
            userApplicationCriteria.andUserTypeEqualTo(1);
            if (userAppMapper.countByExample(ddcUserApplicationExample) > 0) {
                throw new DDCException("已经保存过该用户");
            }
            DdcUserExample userExample = new DdcUserExample();
            DdcUserExample.Criteria userCriteria = userExample.createCriteria();
            userCriteria.andUserIdEqualTo(userId);
            List<DdcUser> ddcUserList = userMapper.selectByExample(userExample);

            if (!CollectionUtils.isEmpty(ddcUserList)) {
                DdcUser ddcUser = ddcUserList.get(0);
                Date now = new Date();
                DdcUserApplication record = new DdcUserApplication();
                record.setUserId(ddcUser.getUserId());
                record.setAppId(appId);
                record.setUserType(1);
                record.setLoginName(ddcUser.getUserName());
                record.setCreateUser("admin");
                record.setCreateTime(now);

                userAppMapper.insertSelective(record);

                //应用添加人员时，被添加人员默认接受报警
                //应用添加人员时，被添加人员默认接受报警
                DdcUserAlarmExample userAlarmExample = new DdcUserAlarmExample();
                DdcUserAlarmExample.Criteria userAlarmCriteria = userAlarmExample.createCriteria();
                userAlarmCriteria.andAppIdEqualTo(appId);
                userAlarmCriteria.andUserIdEqualTo(ddcUser.getUserId());
                if (userAlarmMapper.countByExample(userAlarmExample) == 0) {
                    DdcUserAlarm userAlarm = new DdcUserAlarm();
                    userAlarm.setAppId(appId);
                    userAlarm.setUserId(ddcUser.getUserId());
                    userAlarm.setLoginName(ddcUser.getUserName());
                    userAlarm.setCreateUser("admin");
                    userAlarm.setCreateTime(new Date());

                    userAlarmMapper.insertSelective(userAlarm);
                }
            }
        }
        invalidateCache();
    }

    @Override
    @DdcPermission
    public void updateApplication(int appId, DdcApplication application) throws DDCException {
        if (checkNameDuplicateWithAppId(application.getAppName(), application.getAppId())) {
            throw new DDCException("应用名称已经存在相同应用名！");
        }
        application.setUpdateUser("admin");
        application.setUpdateTime(new Date());
        appMapper.updateByPrimaryKeySelective(application);
        invalidateCache();
    }

    @Override
    @DdcPermission
    public void enableAlarm(int appId, boolean enableAlarm) throws DDCException {
        if (enableAlarm) {
            DdcUserAlarm userAlarm = new DdcUserAlarm();
            userAlarm.setAppId(appId);
            userAlarm.setUserId(1);
            userAlarm.setLoginName("admin");
            userAlarm.setCreateUser("admin");
            userAlarm.setCreateTime(new Date());

            userAlarmMapper.insertSelective(userAlarm);
        } else {
            DdcUserAlarmExample userAlarmExample = new DdcUserAlarmExample();
            DdcUserAlarmExample.Criteria userAlarmCriteria = userAlarmExample.createCriteria();
            userAlarmCriteria.andAppIdEqualTo(appId);
            userAlarmCriteria.andUserIdEqualTo(1);

            userAlarmMapper.deleteByExample(userAlarmExample);
        }
    }


    @Override
    @DdcPermission
    @Transactional
    public void deleteApplication(int appId) throws DDCException {
        if (hasNode(appId)) {
            throw new DDCException("该应用下还有启用的实例，不能删除！");
        }

        //清空ddc_node表中与该应用有关的数据
        DdcNodeExample ddcNodeExample = new DdcNodeExample();
        DdcNodeExample.Criteria nodeCriteria = ddcNodeExample.createCriteria();
        nodeCriteria.andAppIdEqualTo(appId);
        nodeMapper.deleteByExample(ddcNodeExample);

        //如果task表中还有任务，是不能删除应用
        DdcTaskExample ddcTaskExample = new DdcTaskExample();
        DdcTaskExample.Criteria taskCriteria = ddcTaskExample.createCriteria();
        taskCriteria.andAppIdEqualTo(appId);
        if (ddcTaskMapper.countByExample(ddcTaskExample) > 0) {
            throw new DDCException("该应用下还有创建的任务，不能删除！");
        }

        //删除user_application表中与该应用相关的数据
        DdcUserApplicationExample userApplicationExample = new DdcUserApplicationExample();
        DdcUserApplicationExample.Criteria userApplication = userApplicationExample.createCriteria();
        userApplication.andAppIdEqualTo(appId);
        userAppMapper.deleteByExample(userApplicationExample);

        //删除应用的时候，清除该应用下的报警
        DdcUserAlarmExample userAlarmExample = new DdcUserAlarmExample();
        DdcUserAlarmExample.Criteria userAlarmCriteria = userAlarmExample.createCriteria();
        userAlarmCriteria.andAppIdEqualTo(appId);
        userAlarmMapper.deleteByExample(userAlarmExample);


        appMapper.deleteByPrimaryKey(appId);

        invalidateCache();
    }

    /**
     * Find application by user id list.
     * 通过用户id获取该用户的应用列表
     *
     * @param userId the user id
     * @return the list
     */
    private List<DdcApplication> findApplicationByUserId(int userId) {
        DdcUserApplicationExample userExample = new DdcUserApplicationExample();
        DdcUserApplicationExample.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andUserIdEqualTo(userId);
        List<DdcUserApplication> userApps = userAppMapper.selectByExample(userExample);
        List<Integer> appIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userApps)) {
            for (DdcUserApplication user : userApps) {
                appIds.add(user.getAppId());
            }
        }
        if (appIds.size() == 0) {
            try {
                confirmUserInTable(userId);
            } catch (DDCException e) {
            }
            return new ArrayList<>();
        } else {
            DdcApplicationExample appExample = new DdcApplicationExample();
            DdcApplicationExample.Criteria appCriteria = appExample.createCriteria();
            appCriteria.andAppIdIn(appIds);
            return appMapper.selectByExample(appExample);
        }
    }

    @Override
    public ServiceResult listApplicationTable(Pager page) {
        DdcApplicationEx applicationEx = new DdcApplicationEx();
        applicationEx.setUserId(1);
        applicationEx.setPage(page);
        return new ServiceResult(page, appMapper.selectByExampleEx(applicationEx), appMapper.countByExampleEx(applicationEx));
    }

    @Override
    @DdcAdminPermission
    public ServiceResult listApplicationTable(DdcApplication application, Pager page) {
        DdcApplicationExample appExample = new DdcApplicationExample();
        appExample.setPage(page);
        DdcApplicationExample.Criteria appCriteria = appExample.createCriteria();
        if (!StringUtils.isEmpty(application.getAppName())) {
            appCriteria.andAppNameLike("%" + application.getAppName() + "%");
        }
        return new ServiceResult(page, appMapper.selectByExample(appExample), appMapper.countByExample(appExample));
    }

    @Override
    public List<DdcApplication> listApplication(int userId) {

        try {
            return userAppListCache.get(userId);
        } catch (ExecutionException e) {
            logger.error("缓存获取用户的应用列表出现异常");
            return findApplicationByUserId(userId);
        }

    }


    public boolean checkNameExists(String appName) {

        DdcApplicationExample example = new DdcApplicationExample();
        DdcApplicationExample.Criteria criteria = example.createCriteria();
        criteria.andAppNameEqualTo(appName);

        return appMapper.countByExample(example) > 0;
    }

    private boolean checkNameDuplicateWithAppId(String appName, int appId) {

        DdcApplicationExample example = new DdcApplicationExample();
        DdcApplicationExample.Criteria criteria = example.createCriteria();
        criteria.andAppNameEqualTo(appName);
        criteria.andAppIdNotEqualTo(appId);
        return appMapper.countByExample(example) > 0;
    }


    @Override
    public boolean hasNode(int appId) {

        DdcNodeExample example = new DdcNodeExample();
        DdcNodeExample.Criteria criteria = example.createCriteria();
        criteria.andAppIdEqualTo(appId).andNodeStatusEqualTo(Constants.ENABLED);

        return nodeMapper.countByExample(example) > 0;
    }

    @Override
    @DdcPermission
    @Transactional
    public void addAppUser(int appId, DdcUser ddcUser) throws DDCException {

        DdcUserApplicationExample ddcUserApplicationExample = new DdcUserApplicationExample();
        DdcUserApplicationExample.Criteria userApplicationCriteria = ddcUserApplicationExample.createCriteria();
        userApplicationCriteria.andUserIdEqualTo(ddcUser.getUserId());
        userApplicationCriteria.andAppIdEqualTo(appId);
        if (userAppMapper.countByExample(ddcUserApplicationExample) > 0) {
            throw new DDCException("已经保存过该用户");
        }

        Date now = new Date();
        DdcUserApplication record = new DdcUserApplication();
        record.setUserId(ddcUser.getUserId());
        record.setAppId(appId);
        record.setUserType(1);
        record.setLoginName(ddcUser.getUserName());
        record.setCreateUser("admin");
        record.setCreateTime(now);

        userAppMapper.insertSelective(record);

        DdcUserExample userExample = new DdcUserExample();
        DdcUserExample.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andUserIdEqualTo(ddcUser.getUserId());

        if (userMapper.countByExample(userExample) <= 0) {
            ddcUser.setLoginName(ddcUser.getUserName());
            ddcUser.setCreateTime(now);
            ddcUser.setCreateUser("admin");
            ddcUser.setUpdateTime(now);
            ddcUser.setUpdateUser("admin");
            userMapper.insertSelective(ddcUser);
        }

        //应用添加人员时，被添加人员默认接受报警
        DdcUserAlarm userAlarm = new DdcUserAlarm();
        userAlarm.setAppId(appId);
        userAlarm.setUserId(ddcUser.getUserId());
        userAlarm.setLoginName(ddcUser.getUserName());
        userAlarm.setCreateUser("admin");
        userAlarm.setCreateTime(new Date());

        userAlarmMapper.insertSelective(userAlarm);

        invalidateCache();
    }

    @Override
    @Transactional
    @DdcAdminPermission
    public void joinApplication(int appId) throws DDCException {
        DdcUserApplicationExample ddcUserApplicationExample = new DdcUserApplicationExample();
        DdcUserApplicationExample.Criteria userApplicationCriteria = ddcUserApplicationExample.createCriteria();
        userApplicationCriteria.andUserIdEqualTo(1);
        userApplicationCriteria.andAppIdEqualTo(appId);
        if (userAppMapper.countByExample(ddcUserApplicationExample) > 0) {
            throw new DDCException("已经加入了该应用");
        }

        Date now = new Date();
        DdcUserApplication record = new DdcUserApplication();
        record.setUserId(1);
        record.setAppId(appId);
        record.setUserType(2);
        record.setLoginName("admin");
        record.setCreateUser("admin");
        record.setCreateTime(now);

        userAppMapper.insertSelective(record);

        //检查user表是否已经有该用户信息，如果没有则从erp拉取数据
        confirmUserInTable(1);

        invalidateCache();
    }

    @Override
    @Transactional
    @DdcAdminPermission
    public void leaveApplication(int appId) throws DDCException {
        DdcUserApplicationExample userApplicationExample = new DdcUserApplicationExample();
        DdcUserApplicationExample.Criteria userApplicationCriteria = userApplicationExample.createCriteria();
        userApplicationCriteria.andAppIdEqualTo(appId);
        userApplicationCriteria.andUserIdEqualTo(1);

        userAppMapper.deleteByExample(userApplicationExample);

        //退出应用的时候，还需要删除报警关系
        enableAlarm(appId, false);

        invalidateCache();
    }

    @Override
    public void updateAppUser(DdcUser ddcUser) {

        userMapper.updateByPrimaryKeySelective(ddcUser);
        invalidateCache();
    }

    @Override
    @DdcPermission
    public void deleteAppUser(int appId, int userId) {

        DdcUserApplicationExample example = new DdcUserApplicationExample();
        DdcUserApplicationExample.Criteria criteria = example.createCriteria();
        criteria.andAppIdEqualTo(appId).andUserIdEqualTo(userId).andUserTypeEqualTo(1);

        userAppMapper.deleteByExample(example);

        DdcUserApplicationExample example1 = new DdcUserApplicationExample();
        DdcUserApplicationExample.Criteria criteria1 = example.createCriteria();
        criteria1.andAppIdEqualTo(appId).andUserIdEqualTo(userId).andUserTypeEqualTo(2);
        if (userAppMapper.countByExample(example1) == 0) {
            DdcUserAlarmExample userAlarmExample = new DdcUserAlarmExample();
            DdcUserAlarmExample.Criteria userAlarmCriteria = userAlarmExample.createCriteria();
            userAlarmCriteria.andUserIdEqualTo(userId);
            userAlarmCriteria.andAppIdEqualTo(appId);
            userAlarmMapper.deleteByExample(userAlarmExample);
        }

        invalidateCache();
    }

    @Override
    @DdcPermission
    public ServiceResult listAppUser(int appId, Pager page) {
        DdcUserApplicationExample example = new DdcUserApplicationExample();
        DdcUserApplicationExample.Criteria criteria = example.createCriteria();
        criteria.andAppIdEqualTo(appId);
        criteria.andUserTypeEqualTo(1);//1为普通用户
        List<DdcUserApplication> userApplicationList = userAppMapper.selectByExample(example);
        List<Integer> userIdsList = new ArrayList<>();
        for (DdcUserApplication userApplication : userApplicationList) {
            userIdsList.add(userApplication.getUserId());
        }
        if(userApplicationList.size() == 0){
            return new ServiceResult();
        }
        DdcUserExample userExample = new DdcUserExample();
        DdcUserExample.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andUserIdIn(userIdsList);

        return new ServiceResult(page, userMapper.selectByExample(userExample), userMapper.countByExample(userExample));
    }

}
