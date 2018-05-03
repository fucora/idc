package com.iwellmass.dispatcher.admin.service.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.iwellmass.dispatcher.admin.service.IPermissionService;

/**
 * Created by xkwu on 2016/6/2.
 */
@Configuration
@EnableAspectJAutoProxy
@Aspect
public class PermissionAspect {

    @Autowired
    private IPermissionService permissionService;

    @Around(value = "@annotation(ddcPermisson) && args(appId,..)")
    public Object beforeAdvice(ProceedingJoinPoint pjp, DdcPermission ddcPermisson, int appId) throws Throwable {
//        if(!permissionService.hasPermissionInApp(LoginContext.getLoginContext().getLongId(), appId)){
//            throw new RuntimeException("无权限进行该操作");
//        }
        return pjp.proceed();
    }

    @Before(value = "@annotation(com.iwellmass.dispatcher.admin.service.aspect.DdcAdminPermission)")
    public void adminPermissionAdvice() throws Throwable {
        if(!permissionService.hasAdminPermission()){
             throw new RuntimeException("需管理员权限进行该操作");
        }
    }
}
