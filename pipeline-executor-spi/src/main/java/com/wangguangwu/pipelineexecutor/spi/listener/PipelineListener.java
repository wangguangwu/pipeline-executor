package com.wangguangwu.pipelineexecutor.spi.listener;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.spi.exception.PipelineException;
import com.wangguangwu.pipelineexecutor.spi.handler.PipelineHandler;

/**
 * 管道执行生命周期监听器接口（线程安全设计）
 * <p>
 * 事件触发顺序保证：
 * <pre>
 * 1. beforePipeline
 * 2. (onHandlerStart → onHandlerComplete)*
 * 3. afterPipeline
 *    ↑______onError（任意阶段出错时中断正常流程）
 * </pre>
 *
 * @author wangguangwu
 */
public interface PipelineListener {

    // ================= 核心管道事件 =================

    /**
     * 管道启动时触发（首个处理器执行前）
     *
     * @param context 可修改的上下文对象
     * @throws PipelineException 可中断管道启动
     */
    default void beforePipeline(PipelineContext context) throws PipelineException {
    }

    /**
     * 管道结束时触发（无论成功失败）
     *
     * @param context      只读上下文对象
     * @param elapsedNanos 总执行时间（纳秒）
     */
    default void afterPipeline(PipelineContext context, long elapsedNanos) {
    }

    // ================= 处理器级别事件 =================

    /**
     * 处理器执行前触发
     *
     * @param context 可修改的上下文对象
     * @param handler 即将执行的处理器
     * @throws PipelineException 可跳过当前处理器
     */
    default void onHandlerStart(PipelineContext context, PipelineHandler handler)
            throws PipelineException {
    }

    /**
     * 处理器成功执行后触发
     *
     * @param context      可修改的上下文对象
     * @param handler      已完成的处理器
     * @param elapsedNanos 当前处理器耗时（纳秒）
     */
    default void onHandlerComplete(
            PipelineContext context,
            PipelineHandler handler,
            long elapsedNanos
    ) {
    }

    // ================= 异常处理 =================

    /**
     * 错误处理（支持策略模式）
     *
     * @param context 可恢复的上下文对象
     * @param handler 发生异常的处理器
     * @param error   原始异常
     * @return 处理策略（枚举值见下方）
     */
    default ErrorStrategy onError(
            PipelineContext context,
            PipelineHandler handler,
            Throwable error
    ) {
        return ErrorStrategy.TERMINATE;
    }

    /**
     * 错误处理策略枚举
     */
    enum ErrorStrategy {
        /**
         * 继续执行后续处理器
         */
        CONTINUE,

        /**
         * 终止整个管道（触发afterPipeline）
         */
        TERMINATE,

        /**
         * 重试当前处理器（最大重试次数由实现类控制）
         */
        RETRY
    }
}
