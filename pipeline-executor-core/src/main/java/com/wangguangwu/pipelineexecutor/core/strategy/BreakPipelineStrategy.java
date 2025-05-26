package com.wangguangwu.pipelineexecutor.core.strategy;

import com.wangguangwu.pipelineexecutor.core.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.core.exception.PipelineExceptionHandler;
import com.wangguangwu.pipelineexecutor.core.handler.api.PipelineHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 中断管道策略
 * 发生异常时中断整个管道的执行
 *
 * @author wangguangwu
 */
@Slf4j
public class BreakPipelineStrategy implements ExceptionHandlingStrategy {
    
    @Override
    public boolean handleException(PipelineContext context, PipelineHandler handler,
                                 PipelineExceptionHandler exceptionHandler, Exception exception) {
        log.debug("使用中断管道策略处理异常");
        
        // 先调用处理器自己的异常处理方法
        boolean handled = handler.handleException(context, exception);
        
        // 如果处理器没有处理异常，则交给全局异常处理器
        if (!handled && exceptionHandler != null) {
            exceptionHandler.handleException(context, handler, exception);
        }
        
        // 中断管道，不继续执行后续处理器
        return false;
    }
}
