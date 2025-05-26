package com.wangguangwu.pipelineexecutor.spi.registry;

import com.wangguangwu.pipelineexecutor.spi.handler.PipelineHandler;

import java.util.Collection;
import java.util.List;

/**
 * 管道处理器注册中心接口
 * <p>
 * 负责管理管道处理器的注册、排序和查找，是管道执行流程的核心编排组件。
 * 实现类需保证线程安全，支持动态增删处理器。
 *
 * @author wangguangwu
 */
public interface HandlerRegistry {

    /**
     * 注册单个处理器（自动按order排序）
     * <p>
     * 实现要求：
     * <ul>
     *   <li>若handler为null抛出IllegalArgumentException</li>
     *   <li>若同名处理器已存在，默认覆盖旧处理器</li>
     *   <li>根据{@link PipelineHandler#getOrder()}自动排序</li>
     * </ul>
     *
     * @param handler 处理器实例（不可为null）
     * @throws IllegalArgumentException 参数非法时抛出
     */
    void register(PipelineHandler handler);

    /**
     * 批量注册处理器（自动按order排序）
     * <p>
     * 等效于多次调用{@link #register(PipelineHandler)}，
     * 但提供原子性保证（要么全部成功，要么全部失败）。
     *
     * @param handlers 处理器集合（可为null或空集合，此时无操作）
     */
    default void registerAll(Collection<PipelineHandler> handlers) {
        if (handlers != null) {
            handlers.forEach(this::register);
        }
    }

    /**
     * 获取按order升序排列的处理器列表
     * <p>
     * 返回列表特性：
     * <ul>
     *   <li>返回不可修改的列表副本（防御性编程）</li>
     *   <li>order值越小优先级越高</li>
     *   <li>无处理器时返回空列表（不返回null）</li>
     * </ul>
     *
     * @return 排序后的处理器快照列表
     */
    List<PipelineHandler> getHandlers();

    /**
     * 根据名称查找处理器
     * <p>
     * 命名规则建议：
     * <ul>
     *   <li>推荐使用Spring命名风格（如"userValidationHandler"）</li>
     *   <li>名称应保持全局唯一</li>
     * </ul>
     *
     * @param name 处理器名称（大小写敏感）
     * @return 匹配的处理器实例，未找到时返回null
     */
    PipelineHandler getHandler(String name);

    /**
     * 移除指定名称的处理器
     * <p>
     * 注意：
     * <ul>
     *   <li>如果处理器正在执行，移除操作不会中断执行</li>
     *   <li>名称不存在时返回null</li>
     * </ul>
     *
     * @param name 要移除的处理器名称
     * @return 被移除的处理器实例（未找到时返回null）
     */
    PipelineHandler removeHandler(String name);

    /**
     * 清空所有处理器
     * <p>
     * 实现要求：
     * <ul>
     *   <li>清空后{@link #getHandlers()}应返回空列表</li>
     *   <li>不影响正在执行的处理器</li>
     * </ul>
     */
    void clear();
}