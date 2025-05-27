package com.wangguangwu.pipelineexecutor.core.registry;

import com.wangguangwu.pipelineexecutor.spi.handler.Handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wangguangwu
 */
public class HandlerRegistry {

    private final Map<Class<? extends Handler>, Handler> instanceCache = new ConcurrentHashMap<>();

    public Handler getOrCreateHandler(Class<? extends Handler> clazz) {
        return instanceCache.computeIfAbsent(clazz, k -> {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException("无法创建 Handler 实例: " + clazz.getName(), e);
            }
        });
    }
}