package com.hotel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JSON 工具類別
 * 基於 tools.jackson (Jackson 3.x) 提供 Java 物件與 JSON 字串之間的雙向轉換功能。
 */
public final class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * 共用的 ObjectMapper 實例（線程安全）
     */
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            // 反序列化時遇到未知屬性不拋出異常
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            // 序列化空物件（無 public 屬性）時不拋出異常
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .build();

    private JsonUtils() {
        // 私有構造函數，防止實例化
    }

    /**
     * 獲取底層 ObjectMapper 實例，供自訂進階操作使用
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    // ==========================================
    // 序列化：Java Object -> JSON String / Bytes
    // ==========================================

    /**
     * 將 Java 物件轉換為 JSON 字串
     *
     * @param object 欲轉換的 Java 物件
     * @return JSON 字串；若輸入為 null 則返回 null
     */
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JacksonException e) {
            log.error("物件轉 JSON 字串失敗: {}", e.getMessage(), e);
            throw new IllegalArgumentException("無法將物件轉換為 JSON 字串", e);
        }
    }

    /**
     * 將 Java 物件轉換為美化格式（Pretty Print）的 JSON 字串
     *
     * @param object 欲轉換的 Java 物件
     * @return 格式化後的 JSON 字串；若輸入為 null 則返回 null
     */
    public static String toPrettyJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JacksonException e) {
            log.error("物件轉美化 JSON 字串失敗: {}", e.getMessage(), e);
            throw new IllegalArgumentException("無法將物件轉換為美化 JSON 字串", e);
        }
    }

    /**
     * 將 Java 物件轉換為 JSON byte 陣列
     *
     * @param object 欲轉換的 Java 物件
     * @return byte 陣列；若輸入為 null 則返回 null
     */
    public static byte[] toJsonBytes(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsBytes(object);
        } catch (JacksonException e) {
            log.error("物件轉 JSON byte 陣列失敗: {}", e.getMessage(), e);
            throw new IllegalArgumentException("無法將物件轉換為 JSON byte 陣列", e);
        }
    }

    // ==========================================
    // 反序列化：JSON String / Bytes -> Java Object
    // ==========================================

    /**
     * 將 JSON 字串解析為指定型別的 Java 物件
     *
     * @param json  JSON 字串
     * @param clazz 目標類別
     * @param <T>   泛型型別
     * @return 目標物件；若 json 為空則返回 null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JacksonException e) {
            log.error("解析 JSON 字串失敗, class={}: {}", clazz.getName(), e.getMessage(), e);
            throw new IllegalArgumentException("JSON 字串轉換失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 將 JSON 字串解析為複雜泛型型別的 Java 物件（例如 List<T>, Map<K, V>）
     *
     * @param json          JSON 字串
     * @param typeReference 型別參照
     * @param <T>           泛型型別
     * @return 目標物件；若 json 為空則返回 null
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (JacksonException e) {
            log.error("解析 JSON 字串失敗, typeRef={}: {}", typeReference.getType().getTypeName(), e.getMessage(), e);
            throw new IllegalArgumentException("JSON 字串轉換失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 將 JSON 字串根據 JavaType 解析為 Java 物件
     *
     * @param json     JSON 字串
     * @param javaType Jackson JavaType
     * @param <T>      泛型型別
     * @return 目標物件；若 json 為空則返回 null
     */
    public static <T> T fromJson(String json, JavaType javaType) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (JacksonException e) {
            log.error("解析 JSON 字串失敗, javaType={}: {}", javaType.toCanonical(), e.getMessage(), e);
            throw new IllegalArgumentException("JSON 字串轉換失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 將 JSON byte 陣列解析為指定型別的 Java 物件
     *
     * @param bytes byte 陣列
     * @param clazz 目標類別
     * @param <T>   泛型型別
     * @return 目標物件；若 bytes 為空則返回 null
     */
    public static <T> T fromJson(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (JacksonException e) {
            log.error("解析 JSON byte 陣列失敗, class={}: {}", clazz.getName(), e.getMessage(), e);
            throw new IllegalArgumentException("JSON byte 陣列轉換失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 將 JSON byte 陣列解析為複雜泛型型別的 Java 物件
     *
     * @param bytes         byte 陣列
     * @param typeReference 型別參照
     * @param <T>           泛型型別
     * @return 目標物件；若 bytes 為空則返回 null
     */
    public static <T> T fromJson(byte[] bytes, TypeReference<T> typeReference) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(bytes, typeReference);
        } catch (JacksonException e) {
            log.error("解析 JSON byte 陣列失敗, typeRef={}: {}", typeReference.getType().getTypeName(), e.getMessage(), e);
            throw new IllegalArgumentException("JSON byte 陣列轉換失敗: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // 集合常用便捷方法
    // ==========================================

    /**
     * 將 JSON 陣列字串解析為指定元素型別的 List
     *
     * @param json         JSON 陣列字串
     * @param elementClass 元素類別
     * @param <T>          泛型型別
     * @return List 集合；若 json 為空則返回空 List
     */
    public static <T> List<T> toList(String json, Class<T> elementClass) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            CollectionType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, elementClass);
            return OBJECT_MAPPER.readValue(json, listType);
        } catch (JacksonException e) {
            log.error("解析 JSON 陣列失敗, elementClass={}: {}", elementClass.getName(), e.getMessage(), e);
            throw new IllegalArgumentException("JSON 轉換為 List 失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 將 JSON 字串解析為通用 Map (Map<String, Object>)
     *
     * @param json JSON 字串
     * @return Map 集合；若 json 為空則返回空 Map
     */
    public static Map<String, Object> toMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JacksonException e) {
            log.error("解析 JSON 為 Map 失敗: {}", e.getMessage(), e);
            throw new IllegalArgumentException("JSON 轉換為 Map 失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 將 JSON 字串解析為指定 Key/Value 型別的 Map
     *
     * @param json       JSON 字串
     * @param keyClass   Key 類別
     * @param valueClass Value 類別
     * @param <K>        Key 泛型型別
     * @param <V>        Value 泛型型別
     * @return Map 集合；若 json 為空則返回空 Map
     */
    public static <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            MapType mapType = OBJECT_MAPPER.getTypeFactory().constructMapType(Map.class, keyClass, valueClass);
            return OBJECT_MAPPER.readValue(json, mapType);
        } catch (JacksonException e) {
            log.error("解析 JSON 為特定型別 Map 失敗: {}", e.getMessage(), e);
            throw new IllegalArgumentException("JSON 轉換為 Map 失敗: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // 物件互相轉換 (convertValue)
    // ==========================================

    /**
     * 將來源物件轉換為目標型別（例如 Map 轉 POJO 或 POJO 轉 POJO）
     *
     * @param fromValue 來源物件
     * @param toValueType 目標型別
     * @param <T> 泛型型別
     * @return 轉換後的目標物件；若來源為 null 則返回 null
     */
    public static <T> T convert(Object fromValue, Class<T> toValueType) {
        if (fromValue == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.convertValue(fromValue, toValueType);
        } catch (IllegalArgumentException e) {
            log.error("物件型別轉換失敗, targetClass={}: {}", toValueType.getName(), e.getMessage(), e);
            throw new IllegalArgumentException("物件型別轉換失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 將來源物件轉換為複雜泛型型別的目標物件
     *
     * @param fromValue 來源物件
     * @param toValueTypeRef 目標型別參照
     * @param <T> 泛型型別
     * @return 轉換後的目標物件；若來源為 null 則返回 null
     */
    public static <T> T convert(Object fromValue, TypeReference<T> toValueTypeRef) {
        if (fromValue == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.convertValue(fromValue, toValueTypeRef);
        } catch (IllegalArgumentException e) {
            log.error("物件型別轉換失敗, typeRef={}: {}", toValueTypeRef.getType().getTypeName(), e.getMessage(), e);
            throw new IllegalArgumentException("物件型別轉換失敗: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // 樹狀節點（Tree Model / JsonNode）
    // ==========================================

    /**
     * 將 JSON 字串解析為 JsonNode 樹狀節點
     *
     * @param json JSON 字串
     * @return JsonNode；若 json 為空則返回 null
     */
    public static JsonNode readTree(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(json);
        } catch (JacksonException e) {
            log.error("解析 JSON 為 JsonNode 失敗: {}", e.getMessage(), e);
            throw new IllegalArgumentException("無法將 JSON 解析為 JsonNode", e);
        }
    }

    /**
     * 將 JSON byte 陣列解析為 JsonNode 樹狀節點
     *
     * @param bytes byte 陣列
     * @return JsonNode；若 bytes 為空則返回 null
     */
    public static JsonNode readTree(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readTree(bytes);
        } catch (JacksonException e) {
            log.error("解析 byte 陣列為 JsonNode 失敗: {}", e.getMessage(), e);
            throw new IllegalArgumentException("無法將 byte 陣列解析為 JsonNode", e);
        }
    }

    /**
     * 將 Java 物件轉換為 JsonNode 樹狀節點
     *
     * @param object Java 物件
     * @return JsonNode；若 object 為 null 則返回 null
     */
    public static JsonNode valueToTree(Object object) {
        if (object == null) {
            return null;
        }
        return OBJECT_MAPPER.valueToTree(object);
    }
}

