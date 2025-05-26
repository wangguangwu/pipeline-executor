package com.wangguangwu.pipelineexecutor.core.registry;

import com.wangguangwu.pipelineexecutor.core.handler.api.PipelineHandler;

import java.util.List;

/**
 * 处理器注册表接口
 *
 * @author wangguangwu
 */
public interface HandlerRegistry {
    
    /**
     * 注册处理器
     *
     * @param handler 处理器
     */
    void register(PipelineHandler handler);
    
    /**
     * 获取所有处理器
     *
     * @return 处理器列表
     */
    List<PipelineHandler> getHandlers();
    
    /**
     * 根据名称获取处理器
     *
     * @param name 处理器名称
     * @return 处理器
     */
    PipelineHandler getHandler(String name);
    
    /**
     * 移除处理器
     *
     * @param name 处理器名称
     * @return 被移除的处理器
     */
    PipelineHandler removeHandler(String name);
    
    /**
     * 清空处理器
     */
    void clear();
}
