package com.wangguangwu.casepushpipeline.core.registry;

import com.wangguangwu.casepushpipeline.core.context.CasePushHandlerContext;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;

import java.util.List;

/**
 * 处理器注册器接口
 * 负责管理处理器的注册、排序和查询
 *
 * @author wangguangwu
 */
public interface HandlerRegistry {

    /**
     * 注册处理器（添加到链表末尾）
     *
     * @param handler 处理器
     * @return 当前注册器实例，支持链式调用
     */
    HandlerRegistry register(CasePushHandler handler);
    
    /**
     * 在链表头部添加处理器
     *
     * @param handler 处理器
     * @return 当前注册器实例，支持链式调用
     */
    HandlerRegistry registerFirst(CasePushHandler handler);
    
    /**
     * 在指定处理器之前添加新处理器
     *
     * @param existingHandlerName 已存在的处理器名称
     * @param newHandler 新处理器
     * @return 当前注册器实例，支持链式调用
     */
    HandlerRegistry registerBefore(String existingHandlerName, CasePushHandler newHandler);
    
    /**
     * 在指定处理器之后添加新处理器
     *
     * @param existingHandlerName 已存在的处理器名称
     * @param newHandler 新处理器
     * @return 当前注册器实例，支持链式调用
     */
    HandlerRegistry registerAfter(String existingHandlerName, CasePushHandler newHandler);
    
    /**
     * 注册多个处理器
     *
     * @param handlers 处理器列表
     * @return 当前注册器实例，支持链式调用
     */
    HandlerRegistry registerAll(List<CasePushHandler> handlers);
    
    /**
     * 获取所有处理器上下文
     *
     * @return 处理器上下文列表
     */
    List<CasePushHandlerContext> getHandlerContexts();
    
    /**
     * 获取处理器执行顺序的字符串表示
     *
     * @return 处理器执行顺序
     */
    String getHandlerOrderInfo();
    
    /**
     * 获取处理器数量
     *
     * @return 处理器数量
     */
    int size();
    
    /**
     * 初始化注册器
     *
     * @return 当前注册器实例，支持链式调用
     */
    HandlerRegistry init();
}
