package com.iwellmass.dispatcher.admin.service.aspect;

import java.lang.annotation.*;

/**
 * Created by xkwu on 2016/6/22.
 */
@Target({ElementType.PARAMETER,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DdcAdminPermission {
}
