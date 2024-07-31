package com.mads.ai.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private JsonUtil() {}

    /**
     * 判断对象是否为合法JSON字符串
     *
     * @return boolean
     */
    public static boolean mayBeJson(Object object) {
        if (object == null
                || !String.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        String string = (String) object;
        if (string.isEmpty()) {
            return false;
        }
        char head = string.charAt(0);
        return head == '[' || head == '{';
    }

    /**
     * 判断对象是否为合法JSON Object的字符串
     *
     * @return boolean
     */
    public static boolean mayBeJsonObject(Object object) {
        if (object == null
                || !String.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        String string = (String) object;
        if (string.isEmpty()) {
            return false;
        }
        char head = string.charAt(0);
        return head == '{';
    }

    /**
     * 判断对象是否为合法JSON Array的字符串
     *
     * @return boolean
     */
    public static boolean mayBeJsonArray(Object object) {
        if (object == null
                || !String.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        String string = (String) object;
        if (string.isEmpty()) {
            return false;
        }
        char head = string.charAt(0);
        return head == '[';
    }

    /**
     * 将JSON串转换为对象
     *
     * @param json  JSON串
     * @param clazz clazz 指定的对象类型
     */
    public static <T> Optional<T> toObject(@NotNull String json, Class<T> clazz) {
        try {
            if (Objects.isNull(json)) {
                return Optional.empty();
            }
            return Optional.of(getDefaultObjectMapper().readValue(json, clazz));
        } catch (IOException e) {
            logger.error("failed to parse json string: {} to {}.", json, clazz, e);
            return Optional.empty();
        }
    }

    public static <T> List<T> toObjects(@NotNull List<String> jsons, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        for (String json : jsons) {
            toObject(json, clazz).ifPresent(result::add);
        }
        return result;
    }

    /**
     * 将JSON串转换为对象
     *
     * @param json      JSON串
     * @param reference clazz 指定的对象类型
     */
    public static <T> Optional<T> toObject(@NotNull String json, TypeReference<T> reference) {
        try {
            return Optional.of(getDefaultObjectMapper().readValue(json, reference));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("failed to parse json string: {} to {}.", json, reference, e);
            return Optional.empty();
        }
    }


    /**
     * 将JSON串转换为对象
     *
     * @param json  JSON串
     * @param clazz clazz 指定的对象类型 or null if got a exception
     */
    public static <T> Optional<T> toObject(byte[] json, Class<T> clazz) {
        try {
            return Optional.of(getDefaultObjectMapper().readValue(json, clazz));
        } catch (IOException e) {
            logger.error("failed to parse json string: {} to {}", json, clazz, e);
            return Optional.empty();
        }
    }

    /**
     * 将对象转换为JSON串
     *
     * @return String or null if got a exception
     */
    public static Optional<String> toJson(Object object) {
        try {
            return Optional.of(getDefaultObjectMapper().writeValueAsString(object));
        } catch (JsonProcessingException e) {
            logger.error("failed to convert object: {} to json，{}", object, e);
            return Optional.empty();
        }
    }

    public static <T> List<String> toJsons(List<T> objectList) {
        List<String> result = new ArrayList<>();
        for (T object : objectList) {
            toJson(object).ifPresent(result::add);
        }
        return result;
    }

    public static List<String> getDataFromJsonArray(String targetJson, String fieldName) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isEmpty(targetJson) || StringUtils.isEmpty(fieldName)) {
            return list;
        }
        JsonNode jn;
        try {
            jn = JsonUtil.getDefaultObjectMapper().readTree(targetJson);
            if (null != jn && jn.isArray()) {
                Iterator<JsonNode> iterator = jn.elements();
                while (iterator.hasNext()) {
                    JsonNode item = iterator.next();
                    if (null != item && null != item.get(fieldName)) {
                        String questionTitle = item.get(fieldName).asText();
                        if (StringUtils.isNotEmpty(questionTitle)) {
                            list.add(questionTitle);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("failed to parsing targetJson:{}, filedName:{}", targetJson, fieldName, e);
        }
        return list;
    }

    public static Optional<String> merge(@NotNull String... json) {

        Map<String, Object> merged = new HashMap<>();

        for (String s : json) {
            Optional<Map> tmp = toObject(s, Map.class);
            tmp.ifPresent(merged::putAll);
        }
        return toJson(merged);
    }


    public static class CustomInstantSerializer extends JsonSerializer<Instant> {

        @Override
        public void serialize(Instant o, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeObject(o.toEpochMilli());
        }
    }

    public static class CustomInstantDeserializer extends JsonDeserializer<Instant> {

        @Override
        public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                throws IOException {
            long longValue = jsonParser.getValueAsLong();
            return Instant.ofEpochMilli(longValue);
        }
    }

    //基于静态内部类的延迟初始化单例模式，利用类加载机制完成单利，多线程更安全
    private static class JsonUtilLazyHolder {
        private static final ObjectMapper INSTANCE = createDefaultObjectMapper();
    }

    public static ObjectMapper getDefaultObjectMapper() {
        return JsonUtilLazyHolder.INSTANCE;
    }

    public static ObjectMapper createDefaultObjectMapper() {
        //解决内存bug, https://www.jianshu.com/p/48cc5755dac0
        //https://blog.csdn.net/CaptainLYF/article/details/86238148
//                    JsonFactory jsonFactory = new JsonFactoryBuilder()
//                            .disable(JsonFactory.Feature.USE_THREAD_LOCAL_FOR_BUFFER_RECYCLING)
////                            .disable(JsonFactory.Feature.INTERN_FIELD_NAMES)
//                            .build();

        ObjectMapper objectMapper = new ObjectMapper();
        //把属性名从驼峰式（CamelCase）转为蛇形式（snake_case）。
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        //设置为仅包含非空属性
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        //禁用在反序列化过程中遇到未知属性时抛出异常的功能。
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //禁用将日期序列化为时间戳的功能，通常会将日期序列化为可读的日期字符串。
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        //禁用在序列化空对象时抛出异常的功能。
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        //用于注册自定义的序列化器和反序列化器
        SimpleModule customModule = new SimpleModule();
        customModule.addSerializer(Instant.class, new CustomInstantSerializer());
        customModule.addDeserializer(Instant.class, new CustomInstantDeserializer());
        objectMapper.findAndRegisterModules();
        objectMapper.registerModule(customModule);

        return objectMapper;
    }
}