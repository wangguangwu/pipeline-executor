package com.wangguangwu.pipelineexecutor.spi.context;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * 管道执行上下文接口
 * <p>
 * 上下文对象在处理器之间传递数据和状态，提供以下核心功能：
 * 1. 属性存储：线程安全的键值对存储
 * 2. 异常处理：记录和处理执行过程中的异常
 * 3. 流程控制：支持中断执行流程
 * 4. 执行追踪：提供执行ID和当前处理器信息
 * </p>
 *
 * @author wangguangwu
 */
public interface PipelineContext extends Serializable {

    // ================= 属性存储 =================

    /**
     * 设置上下文属性
     *
     * @param key   属性键
     * @param value 属性值（支持 null）
     * @throws NullPointerException 如果 key 为 null
     */
    void setAttribute(String key, Object value);

    /**
     * 获取上下文属性（类型不校验）
     *
     * @param key 属性键（非空）
     * @return 属性值（可能为 null）
     * @throws NullPointerException 如果 key 为 null
     */
    Object getAttribute(String key);

    /**
     * 获取类型安全的属性值
     *
     * @param key  属性键（非空）
     * @param type 目标类型（非空）
     * @param <T>  属性值类型
     * @return 值或 null（类型不匹配或值不存在时返回 null）
     * @throws NullPointerException 如果 key 或 type 为 null
     */
    <T> T getAttribute(String key, Class<T> type);

    /**
     * 获取属性值或默认值
     *
     * @param key          属性键（非空）
     * @param type         目标类型（非空）
     * @param defaultValue 默认值（可空）
     * @param <T>          属性值类型
     * @return 属性值（如果存在且类型匹配）或默认值
     * @throws NullPointerException 如果 key 或 type 为 null
     */
    default <T> T getAttributeOrDefault(String key, Class<T> type, T defaultValue) {
        Objects.requireNonNull(key, "Attribute key cannot be null");
        Objects.requireNonNull(type, "Attribute type cannot be null");

        T value = getAttribute(key, type);
        return value != null ? value : defaultValue;
    }

    /**
     * 计算并设置属性值（如果键不存在）
     *
     * @param key          属性键（非空）
     * @param type         值类型
     * @param valueFactory 值工厂（非空）
     * @param <T>          属性值类型
     * @return 当前值（已存在）或新计算的值
     * @throws NullPointerException 如果 key 或 valueFactory 为 null
     */
    default <T> T computeIfAbsent(String key, Class<T> type, Supplier<? extends T> valueFactory) {
        Objects.requireNonNull(key, "Attribute key cannot be null");
        Objects.requireNonNull(valueFactory, "Value factory cannot be null");

        T value = getAttribute(key, type);
        if (value == null) {
            value = valueFactory.get();
            if (value != null) {
                setAttribute(key, value);
            }
        }
        return value;
    }

    /**
     * 检查是否包含指定键
     *
     * @param key 属性键（非空）
     * @return 如果包含指定键则返回 true
     * @throws NullPointerException 如果 key 为 null
     */
    boolean containsKey(String key);

    // ================= 异常处理 =================

    /**
     * 设置处理过程中的异常
     *
     * @param exception 异常实例（非空）
     * @throws NullPointerException 如果 exception 为 null
     */
    void setException(Throwable exception);

    /**
     * 获取当前异常
     *
     * @return 当前异常（未发生异常时返回 null）
     */
    Throwable getException();

    /**
     * 检查是否存在异常
     *
     * @return 如果存在异常则返回 true
     */
    default boolean hasException() {
        return getException() != null;
    }

    // ================= 流程控制 =================

    /**
     * 标记流程中断（终止后续 Handler 执行）
     */
    void markBroken();

    /**
     * 检查流程是否已中断
     *
     * @return 如果流程已中断则返回 true
     */
    boolean isBroken();

    // ================= 执行元数据 =================

    /**
     * 获取当前执行的 Handler 名称
     * <p>
     * 注意：此值由框架自动设置，不应手动修改
     * </p>
     *
     * @return 当前处理器名称（可能为 null）
     */
    String currentHandlerName();

    /**
     * 获取执行链唯一标识
     * <p>
     * 此ID在上下文创建时生成，可用于日志追踪和调试
     * </p>
     *
     * @return 执行ID（非空）
     */
    String executionId();

    // ================= 上下文重置 =================

    /**
     * 重置上下文状态
     * <p>
     * 重置操作包括：
     * 1. 清空所有属性
     * 2. 清除异常状态
     * 3. 重置中断状态
     * 4. 清空当前处理器名称
     * </p>
     */
    void reset();

}
