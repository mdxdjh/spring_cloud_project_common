package com.ellis.commons.serializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;

/**
 * @author ellis.luo
 * @date 16/11/16 下午4:45
 * @description JSON序列化
 */
public class JsonSerializer
{
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将对象序列化为JSON字符串
     *
     * @param object
     * @return
     */
    public static String serialize(Object object)
    {
        Writer write = new StringWriter();
        try
        {
            objectMapper.writeValue(write, object);
        }
        catch (Exception e)
        {
            logger.error("Exception when serialize object to json", e);
        }
        return write.toString();
    }

    public static <T> T parse(String str, Class<T> clz)
    {
        try
        {
            return objectMapper.readValue(str == null ? "{}" : str, clz);
        }
        catch (Exception e)
        {
            throw new RuntimeException("json parse to object [" + clz + "] error:" + str, e);
        }
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T deserialize(String json, Class<T> clazz)
    {
        Object object = null;
        try
        {
            object = objectMapper.readValue(json, TypeFactory.rawClass(clazz));
        }
        catch (Exception e)
        {
            logger.error("Exception when serialize object to json", e);
        }
        return (T) object;
    }

    /**
     * 将JSON字符串反序列化为对象
     *
     * @param json
     * @param typeRef
     * @param <T>
     * @return
     */
    public static <T> T deserialize(String json, TypeReference<T> typeRef)
    {
        try
        {
            return (T) objectMapper.readValue(json, typeRef);
        }
        catch (Exception e)
        {
            logger.error("Exception when deserialize json", e);
        }
        return null;
    }

}
