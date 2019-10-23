package com.iwellmass.idc.app.util;




import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2019/9/27.
 */
public class JsonUtils {


    //配置ObjectMapper对象
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    public static <T> List<T> readValue2List(String content, Class<T> valueType) throws IOException {

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, valueType);
        return objectMapper.readValue(content, javaType)  ;
    }


    public static String toJSon(Object object) throws IOException {

        return objectMapper.writeValueAsString(object);

    }



}
