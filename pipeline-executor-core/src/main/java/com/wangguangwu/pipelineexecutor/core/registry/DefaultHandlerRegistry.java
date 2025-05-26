package com.wangguangwu.pipelineexecutor.core.registry;

import com.wangguangwu.pipelineexecutor.core.handler.api.PipelineHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认处理器注册表实现
 *
 * @author wangguangwu
 */
@Slf4j
public class DefaultHandlerRegistry implements HandlerRegistry {
    
    /**
     * 处理器映射
     */
    private final Map<String, PipelineHandler> handlers = new ConcurrentHashMap<>();
    
    @Override
    public void register(PipelineHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("处理器不能为空");
        }
        String name = handler.name();
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("处理器名称不能为空");
        }
        
        handlers.put(name, handler);
        log.debug("注册处理器: {}", name);
    }
    
    @Override
    public List<PipelineHandler> getHandlers() {
        return new ArrayList<>(handlers.values());
    }
    
    @Override
    public PipelineHandler getHandler(String name) {
        return handlers.get(name);
    }
    
    @Override
    public PipelineHandler removeHandler(String name) {
        PipelineHandler handler = handlers.remove(name);
        if (handler != null) {
            log.debug("移除处理器: {}", name);
        }
        return handler;
    }
    
    @Override
    public void clear() {
        handlers.clear();
        log.debug("清空所有处理器");
    }
}
