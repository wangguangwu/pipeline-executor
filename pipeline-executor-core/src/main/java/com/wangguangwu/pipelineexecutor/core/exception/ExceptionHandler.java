package com.wangguangwu.pipelineexecutor.core.exception;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;

import java.util.Objects;

/**
 * 异常处理器接口
 * <p>
 * 用于处理管道执行过程中发生的异常，决定后续执行流程。
 * 实现类应该是线程安全的，因为可能会在并发环境下被调用。
 * </p>
 *
 * @author wangguangwu
 * @see DefaultExceptionHandler
 * @see ExceptionHandleResult
 */
public interface ExceptionHandler {

    /**
     * 处理管道执行过程中发生的异常
     * <p>
     * 实现类应该处理异常并返回适当的处理结果，决定是否继续执行后续处理器。
     * 典型的实现可能包括：
     * 1. 记录错误日志
     * 2. 收集错误指标
     * 3. 根据异常类型决定处理策略
     * </p>
     *
     * @param throwable 发生的异常（非空）
     * @param context   当前执行的上下文（非空）
     * @return 处理结果，决定后续执行流程（非空）
     * @throws NullPointerException 如果 throwable 或 context 为 null
     */
    ExceptionHandleResult handle(Throwable throwable, PipelineContext context);

    /**
     * 返回一个组合的异常处理器，先由当前处理器处理，如果不终止则由 after 处理器处理
     *
     * @param after 当前处理器不终止时要调用的处理器（非空）
     * @return 组合的异常处理器
     * @throws NullPointerException 如果 after 为 null
     */
    default ExceptionHandler andThen(ExceptionHandler after) {
        Objects.requireNonNull(after, "After exception handler cannot be null");
        return (t, c) -> {
            ExceptionHandleResult result = handle(t, c);
            return result == ExceptionHandleResult.CONTINUE ? after.handle(t, c) : result;
        };
    }

    /**
     * 创建一个简单的异常处理器，总是返回指定的处理结果
     *
     * @param result 要返回的处理结果（非空）
     * @return 固定返回指定结果的异常处理器
     * @throws NullPointerException 如果 result 为 null
     */
    static ExceptionHandler of(ExceptionHandleResult result) {
        Objects.requireNonNull(result, "Result cannot be null");
        return (t, c) -> result;
    }

    /**
     * 创建一个记录日志的异常处理器
     *
     * @param loggerName 记录器名称
     * @return 记录异常并继续执行的异常处理器
     * @throws NullPointerException 如果 loggerName 为 null
     */
    static ExceptionHandler loggingHandler(String loggerName) {
        Objects.requireNonNull(loggerName, "Logger name cannot be null");
        return (t, c) -> {
            org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(loggerName);
            logger.error("Pipeline execution error in handler: {}", c.currentHandlerName(), t);
            return ExceptionHandleResult.CONTINUE;
        };
    }
}
