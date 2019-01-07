package com.iwellmass.idc.app.util;

import com.iwellmass.common.exception.AppException;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenxiong
 * @email nobita0522@qq.com
 * @date 2019/01/07 17:38
 * @description
 */
public class Util {
    /**
     * 将JPA关联出来的结果通过反射的方式转换成对应的结果
     * 注意关联查询出来的结果中不能有null,可以是"",但不能是null
     * 如果返回的结果中存在枚举类型,因为数据库查询出来是String类型的,需要写一下由String 到 Enum的映射的构造方法.
     *
     * @param contents     从数据查询出来的结果,带转换
     * @param clazz        转换成指定的类型
     * @param defaultClass 当查询结果为空时,默认使用的Class,将决定选择构造器,注意sql书写的顺序,决定第几个参数可能是null,调用时应该提前创建好构造函数,知道哪些字段可能是null
     * @param <T>
     * @return
     */
    public static <T> List<T> castEntity(List<Object[]> contents, Class<T> clazz, Class... defaultClass) {
        List<T> ts = new ArrayList<T>();
        if (contents.size() == 0) {
            // 没有数据
            return ts;
        }
        Object[] columns = contents.get(0);
        Class[] paramsType = new Class[columns.length]; // 记录每个列的类型
        //确定构造方法
        int k = 0;
        for (int i = 0; i < columns.length; i++) {
            if (columns[i] != null) {
                paramsType[i] = columns[i].getClass();
            } else {
                paramsType[i] = defaultClass[k];
                k++;
            }
        }
        Constructor<T> constructor = null; // 得到相关的构造函数
        try {
            constructor = clazz.getConstructor(paramsType);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new AppException("未查找到需要的构造函数");
        }
        for (Object[] o : contents) {
            try {
                ts.add(constructor.newInstance(o));
            } catch (Exception e) {
                e.printStackTrace();
                throw new AppException("创建对象失败");
            }
        }
        return ts;
    }
}
