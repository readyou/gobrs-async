package com.heytap.ad.osync.core.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();


    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        String s = null;
        try {
            s = obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JsonUtil obj2String error{}", e);
        }
        return s;
    }

    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (str == null || str.length() == 0 || clazz == null) {
            return null;
        }
        T t = null;
        try {
            t = clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            log.error("JsonUtil string2Obj error{}", e);
        }
        return t;
    }


    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
        if (str == null || str.length() == 0 || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (IOException e) {
            return null;
        }
    }


    public static <T> T string2Obj(String str, Class<?> collectionClazz, Class<?>... elementClazzes) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClazz, elementClazzes);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            return null;
        }
    }


    public static JsonUtil.JsonBuilder builder() {
        return new JsonUtil.JsonBuilder();
    }


    public static class JsonBuilder {
        private Map<String, Object> map = new HashMap<>();

        /**
         * Instantiates a new Json builder.
         */
        JsonBuilder() {
        }


        public JsonUtil.JsonBuilder put(String key, Object value) {
            map.put(key, value);
            return this;
        }


        public String build() {

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.writeValueAsString(this.map);
            } catch (JsonProcessingException e) {
                log.error("JsonUtil build error{}", e);
            }
            return "{}";
        }
    }

}
