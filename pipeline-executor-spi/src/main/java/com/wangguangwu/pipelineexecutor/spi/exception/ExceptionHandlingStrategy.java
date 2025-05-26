package com.wangguangwu.pipelineexecutor.spi.exception;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.spi.handler.PipelineHandler;
import lombok.NonNull;

/**
 * 异常处理策略接口
 * 定义管道执行过程中异常的处理策略
 *
 * @author wangguangwu
 */
public interface ExceptionHandlingStrategy {

    /**
     * 处理执行过程中的异常
     *
     * @param context 当前管道上下文（非空）
     * @param ex      发生的异常（非空）
     * @param handler 发生异常的处理器（非空，使用空对象模式）
     * @return 处理结果枚举（非空）
     * @throws NullPointerException 如果 context、ex 或 handler 为 null
     */
    ExceptionHandlingResult handleException(
            @NonNull PipelineContext context,
            @NonNull Exception ex,
            @NonNull PipelineHandler handler
    );
}
