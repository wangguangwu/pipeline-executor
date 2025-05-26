package com.wangguangwu.pipelineexecutor.core.strategy;

import com.wangguangwu.pipelineexecutor.core.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.core.exception.PipelineExceptionHandler;
import com.wangguangwu.pipelineexecutor.core.handler.api.PipelineHandler;

/**
 * 异常处理策略接口
 *
 * @author wangguangwu
 */
public interface ExceptionHandlingStrategy {
    
    /**
     * 处理异常
     *
     * @param context          管道上下文
     * @param handler          发生异常的处理器
     * @param exceptionHandler 异常处理器
     * @param exception        异常
     * @return 是否继续执行后续处理器
     */
    boolean handleException(PipelineContext context, PipelineHandler handler, 
                           PipelineExceptionHandler exceptionHandler, Exception exception);
}
