package com.wangguangwu.pipelineexecutor.core.registry;

import java.util.List;

/**
 * 处理器顺序注册表接口
 * 用于管理处理器的执行顺序
 *
 * @author wangguangwu
 */
public interface HandlerOrderRegistry {
    
    /**
     * 获取处理器执行顺序列表
     *
     * @return 处理器名称列表，按执行顺序排序
     */
    List<String> getHandlerOrder();
    
    /**
     * 添加处理器到顺序列表末尾
     *
     * @param handlerName 处理器名称
     */
    void addHandler(String handlerName);
    
    /**
     * 在指定处理器之前添加处理器
     *
     * @param targetHandlerName 目标处理器名称
     * @param handlerName 要添加的处理器名称
     */
    void addBefore(String targetHandlerName, String handlerName);
    
    /**
     * 在指定处理器之后添加处理器
     *
     * @param targetHandlerName 目标处理器名称
     * @param handlerName 要添加的处理器名称
     */
    void addAfter(String targetHandlerName, String handlerName);
    
    /**
     * 设置处理器顺序
     *
     * @param handlerNames 处理器名称列表
     */
    void setOrder(List<String> handlerNames);
    
    /**
     * 移除处理器
     *
     * @param handlerName 处理器名称
     */
    void removeHandler(String handlerName);
    
    /**
     * 清空顺序列表
     */
    void clear();
}
