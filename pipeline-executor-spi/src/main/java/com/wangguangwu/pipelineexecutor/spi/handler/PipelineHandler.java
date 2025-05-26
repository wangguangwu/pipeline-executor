package com.wangguangwu.pipelineexecutor.spi.handler;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.spi.exception.PipelineException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * 管道处理器接口
 * <p>
 * 定义管道处理单元的核心契约，支持丰富的执行控制策略：
 * 顺序控制、依赖管理、条件执行、重试机制等。
 * 所有扩展方法均提供默认实现，保持向后兼容性。
 *
 * @author wangguangwu
 */
public interface PipelineHandler {

    // ================= 基础方法 =================

    /**
     * 处理器名称（用于日志和监控）
     */
    default String getName() {
        return getClass().getSimpleName();
    }

    /**
     * 核心处理逻辑
     * <p>
     * 实现要求：
     * <ul>
     *   <li>应声明具体异常类型（避免直接抛出Exception）</li>
     *   <li>通过{@link PipelineContext}存取共享数据</li>
     *   <li>避免长时间阻塞线程</li>
     * </ul>
     *
     * @param context 管道执行上下文（不可为null）
     * @throws Exception 处理失败时抛出业务异常
     */
    void handle(PipelineContext context) throws Exception;

    /**
     * 批量处理
     *
     * @param contexts 管道执行上下文（不可为null）
     * @throws Exception 处理失败时抛出业务异常
     */
    default void handleBatch(List<PipelineContext> contexts) throws Exception {
        for (PipelineContext context : contexts) {
            handle(context);
        }
    }

    // ================= 执行控制 =================

    /**
     * 获取执行优先级
     * <p>
     * 排序规则：
     * <ul>
     *   <li>数值越小优先级越高</li>
     *   <li>相同order值的处理器执行顺序不确定</li>
     *   <li>典型取值范围：0（最高）\~100（最低）</li>
     * </ul>
     *
     * @return 优先级数值（默认0）
     */
    default int getOrder() {
        return 0;
    }

    /**
     * 是否启用当前处理器
     * <p>
     * 应用场景：
     * <ul>
     *   <li>根据环境变量动态开关处理器</li>
     *   <li>A/B测试时路由不同处理逻辑</li>
     * </ul>
     *
     * @return false时跳过该处理器（默认true）
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * 是否支持异步执行
     *
     * @return true表示可安全地在异步管道中执行（默认false）
     */
    default boolean isAsyncSupported() {
        return false;
    }

    // ================= 生命周期扩展 =================

    /**
     * 前置处理钩子
     * <p>
     * 在{@link #handle}之前执行，可用于：
     * <ul>
     *   <li>参数校验</li>
     *   <li>资源预加载</li>
     *   <li>执行条件判断</li>
     * </ul>
     *
     * @param context 管道上下文
     * @return false时中断后续执行（默认true）
     */
    default boolean preHandle(PipelineContext context) {
        return true;
    }

    /**
     * 后置处理钩子
     * <p>
     * 无论处理成功与否都会执行，适用于：
     * <ul>
     *   <li>资源清理</li>
     *   <li>执行结果记录</li>
     *   <li>异常转换</li>
     * </ul>
     *
     * @param context 管道上下文
     * @param ex      处理异常（成功时为null）
     */
    default void postHandle(PipelineContext context, Exception ex) {
        // 默认空实现
    }

    // ================= 依赖管理 =================

    /**
     * 获取前置依赖处理器
     * <p>
     * 框架保证：
     * <ul>
     *   <li>所有依赖处理器执行完成后才会执行当前处理器</li>
     *   <li>循环依赖会抛出{@link PipelineException}</li>
     * </ul>
     *
     * @return 依赖的处理器名称集合（默认空集合）
     */
    default Set<String> getDependsOn() {
        return Collections.emptySet();
    }

    /**
     * 获取后置依赖处理器
     * <p>
     * 用于声明当前处理器必须在哪些处理器之前执行
     *
     * @return 被依赖的处理器名称集合（默认空集合）
     */
    default Set<String> getDependentBy() {
        return Collections.emptySet();
    }

    // ================= 高级控制 =================

    /**
     * 执行超时时间（毫秒）
     * <p>
     * 超时处理：
     * <ul>
     *   <li>同步管道：抛出{@link TimeoutException}</li>
     *   <li>异步管道：取消执行并触发超时回调</li>
     * </ul>
     *
     * @return 超时阈值（≤0表示不限制，默认0）
     */
    default long getTimeout() {
        return 0;
    }

    /**
     * 获取重试策略
     * <p>
     * 重试条件：
     * <ul>
     *   <li>仅对指定类型的异常重试</li>
     *   <li>达到最大重试次数后抛出原始异常</li>
     * </ul>
     *
     * @return 重试策略配置（null表示不重试，默认null）
     */
    default RetryPolicy getRetryPolicy() {
        return null;
    }

    /**
     * 重试策略接口
     */
    interface RetryPolicy {
        /**
         * 最大重试次数（包含首次执行）
         */
        int getMaxAttempts();

        /**
         * 重试间隔时间（毫秒）
         */
        long getBackoffPeriod();

        /**
         * 可重试的异常类型
         */
        Class<? extends Throwable>[] retryableExceptions();
    }
}