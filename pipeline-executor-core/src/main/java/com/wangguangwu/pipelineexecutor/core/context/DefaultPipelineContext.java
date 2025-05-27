package com.wangguangwu.pipelineexecutor.core.context;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangguangwu
 */
public class DefaultPipelineContext implements PipelineContext {

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private Throwable exception;
    private boolean broken;
    private String currentHandlerName;
    private final String executionId = UUID.randomUUID().toString();

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public <T> T getAttribute(String key, Class<T> type) {
        Object value = attributes.get(key);
        return (value != null && type.isInstance(value)) ? type.cast(value) : null;
    }

    @Override
    public void setException(Throwable exception) {
        this.exception = exception;
    }

    @Override
    public Throwable getException() {
        return exception;
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

    // 框架内部使用（非 API 方法）
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
}
