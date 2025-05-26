package com.wangguangwu.pipelineexecutor.spi.context;

import java.util.Map;
import java.util.function.Function;

/**
 * 类型安全的属性存储接口
 *
 * @author wangguangwu
 */
public interface AttributeStore {

    /**
     * 获取指定类型的属性值
     *
     * @param key  属性键（非空）
     * @param type 预期返回类型（非空）
     * @param <T>  返回值泛型
     * @return 属性值或null
     * @throws ClassCastException 类型不匹配时抛出
     */
    <T> T getAttribute(String key, Class<T> type);

    /**
     * 设置属性值
     *
     * @param key   属性键（非空）
     * @param value 属性值（可为null表示删除）
     */
    void setAttribute(String key, Object value);

    /**
     * 检查属性是否存在
     *
     * @param key 属性键（非空）
     * @return 存在返回true
     */
    boolean hasAttribute(String key);

    /**
     * 移除指定属性
     *
     * @param key 属性键（非空）
     * @return 被移除的值或null
     */
    Object removeAttribute(String key);

    /**
     * 获取所有属性的不可变视图
     *
     * @return 只读的属性映射
     */
    Map<String, Object> getAllAttributes();

    /**
     * 批量设置属性
     *
     * @param attributes 属性集
     */
    default void putAll(Map<String, Object> attributes) {
        if (attributes != null) {
            attributes.forEach(this::setAttribute);
        }
    }
}

