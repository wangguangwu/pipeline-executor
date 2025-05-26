package com.wangguangwu.pipelineexecutor.spi.executor;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.spi.exception.PipelineException;
import com.wangguangwu.pipelineexecutor.spi.listener.PipelineListener;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

/**
 * 全泛型管道执行器接口（线程安全设计）
 * <p>
 * 核心特性：
 * 1. 纯泛型结果类型（无基础类型约束）
 * 2. 同步/异步执行统一抽象
 * 3. 内置超时和中断控制
 * 4. 强类型事件监听
 *
 * @author wangguangwu
 */
public interface PipelineExecutor {

    // ================= 同步执行 =================

    /**
     * 同步执行管道（阻塞当前线程）
     *
     * @param context 管道上下文（非null）
     * @return 执行结果（可能为null）
     * @throws PipelineException 业务逻辑错误
     */
    PipelineResult execute(PipelineContext context) throws PipelineException;

    /**
     * 带超时的同步执行
     *
     * @param timeout 必须 > 0
     * @param unit    时间单位（非null）
     * @throws TimeoutException 超过指定时间未完成
     * @see #execute(PipelineContext)
     */
    PipelineResult execute(
            PipelineContext context,
            long timeout,
            TimeUnit unit
    ) throws PipelineException, TimeoutException;

    // ================= 异步执行 =================

    /**
     * 异步执行（非阻塞）
     *
     * @return CompletableFuture 支持链式调用：
     * <pre>{@code
     * executor.executeAsync(ctx)
     *     .thenApply(result -> process(result)) // 自动推断T类型
     *     .exceptionally(e -> handleError(e));
     * }</pre>
     */
    CompletableFuture<PipelineResult> executeAsync(PipelineContext context);

    /**
     * 带超时的异步执行
     *
     * @param timeout 必须 > 0
     * @implNote 超时后会自动取消异步任务
     */
    CompletableFuture<PipelineResult> executeAsync(
            PipelineContext context,
            long timeout,
            TimeUnit unit
    );

    // ================= 执行控制 =================

    /**
     * 添加类型感知的监听器
     *
     * @param listener 可接收特定类型事件的监听器
     * @param <T>      监听器关注的类型
     */
    <T> void addListener(PipelineListener listener);

    /**
     * 移除监听器
     *
     * @return 是否成功移除
     */
    boolean removeListener(PipelineListener listener);

    // ================= 增强方法 =================

    /**
     * 批量并行执行
     *
     * @param contexts 上下文集合
     * @param <T>      动态结果类型
     * @return 结果流（保持输入顺序）
     */
    @SuppressWarnings("unchecked")
    default <T> Stream<T> executeAll(
            Collection<? extends PipelineContext> contexts
    ) {
        return contexts.parallelStream()
                .map(ctx -> {
                    try {
                        return (T) execute(ctx);
                    } catch (PipelineException e) {
                        throw new CompletionException(e);
                    }
                });
    }
}