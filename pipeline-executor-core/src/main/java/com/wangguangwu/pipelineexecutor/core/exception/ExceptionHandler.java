package com.wangguangwu.pipelineexecutor.core.exception;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;

/**
 * @author wangguangwu
 */
public interface ExceptionHandler {
    /**
     * 处理责任链执行过程中的异常
     * @param e       异常对象（非空）
     * @param context 当前上下文（可从中读取执行状态）
     * @return 处理结果指令（继续执行或终止）
     */
    ExceptionHandleResult handle(Throwable e, PipelineContext context);
}
