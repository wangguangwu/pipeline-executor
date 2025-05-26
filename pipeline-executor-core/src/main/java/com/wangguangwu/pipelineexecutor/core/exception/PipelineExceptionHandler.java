package com.wangguangwu.pipelineexecutor.core.exception;

import com.wangguangwu.pipelineexecutor.core.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.core.handler.PipelineHandler;

/**
 * 管道异常处理器接口
 *
 * @author wangguangwu
 */
public interface PipelineExceptionHandler {
    
    /**
     * 处理异常
     *
     * @param context   管道上下文
     * @param handler   发生异常的处理器
     * @param exception 异常
     */
    void handleException(PipelineContext context, PipelineHandler handler, Exception exception);
}
