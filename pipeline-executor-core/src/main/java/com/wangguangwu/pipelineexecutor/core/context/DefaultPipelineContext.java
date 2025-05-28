package com.wangguangwu.pipelineexecutor.core.context;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 默认的管道上下文实现
 * <p>
 * 特性：
 * 1. 线程安全：所有操作都是线程安全的
 * 2. 高性能：使用 ConcurrentHashMap 存储属性
 * 3. 可追踪：包含执行ID和当前处理器名称
 * 4. 可重置：支持重置上下文状态
 * </p>
 *
 * @author wangguangwu
 */
public class DefaultPipelineContext implements PipelineContext {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 属性存储
     */
    private final Map<String, Object> attributes = new ConcurrentHashMap<>(16);

    /**
     * 异常信息
     */
    private volatile Throwable exception;

    /**
     * 是否已中断
     */
    private volatile boolean broken;

    /**
     * 当前处理器名称
     */
    private volatile String currentHandlerName;

    /**
     * 执行ID
     */
    private final String executionId = generateExecutionId();

    @Override
    public void setAttribute(String key, Object value) {
        Objects.requireNonNull(key, "Attribute key cannot be null");
        attributes.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        Objects.requireNonNull(key, "Attribute key cannot be null");
        return attributes.get(key);
    }

    @Override
    public <T> T getAttribute(String key, Class<T> type) {
        Objects.requireNonNull(key, "Attribute key cannot be null");
        Objects.requireNonNull(type, "Attribute type cannot be null");

        Object value = attributes.get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    @Override
    public <T> T computeIfAbsent(String key, Class<T> type, Supplier<? extends T> valueFactory) {
        Objects.requireNonNull(key, "Attribute key cannot be null");
        Objects.requireNonNull(type, "Attribute type cannot be null");
        Objects.requireNonNull(valueFactory, "Value factory cannot be null");

        // 检查是否已存在
        T existing = getAttribute(key, type);
        if (existing != null) {
            return existing;
        }

        // 计算新值
        T newValue = valueFactory.get();
        if (newValue != null) {
            setAttribute(key, newValue);
        }
        return newValue;
    }

    @Override
    public boolean containsKey(String key) {
        Objects.requireNonNull(key, "Attribute key cannot be null");
        return attributes.containsKey(key);
    }

    @Override
    public void setException(Throwable exception) {
        this.exception = Objects.requireNonNull(exception, "Exception cannot be null");
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public boolean hasException() {
        return exception != null;
    }

    @Override
    public void markBroken() {
        this.broken = true;
    }

    @Override
    public boolean isBroken() {
        return broken;
    }

    @Override
    public String currentHandlerName() {
        return currentHandlerName;
    }

    /**
     * 设置当前处理器名称（框架内部使用）
     *
     * @param name 处理器名称
     */
    void setCurrentHandlerName(String name) {
        this.currentHandlerName = name;
    }

    @Override
    public String executionId() {
        return executionId;
    }

    @Override
    public void reset() {
        attributes.clear();
        exception = null;
        broken = false;
        currentHandlerName = null;
    }

    /**
     * 生成执行ID
     *
     * @return 唯一的执行ID
     */
    private static String generateExecutionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public String toString() {
        return "DefaultPipelineContext{" +
                "executionId='" + executionId + '\'' +
                ", currentHandlerName='" + currentHandlerName + '\'' +
                ", broken=" + broken +
                (exception != null ? ", exception=" + exception : "") +
                ", attributesCount=" + attributes.size() +
                '}';
    }
}
