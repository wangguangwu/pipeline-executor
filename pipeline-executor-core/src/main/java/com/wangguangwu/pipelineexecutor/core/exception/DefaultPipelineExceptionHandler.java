package com.wangguangwu.pipelineexecutor.core.exception;

import com.wangguangwu.pipelineexecutor.core.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.core.handler.api.PipelineHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认管道异常处理器
 *
 * @author wangguangwu
 */
@Slf4j
public class DefaultPipelineExceptionHandler implements PipelineExceptionHandler {
    
    @Override
    public void handleException(PipelineContext context, PipelineHandler handler, Exception exception) {
        log.error("处理器[{}]执行异常, 任务ID: {}, 异常信息: {}", 
                handler.name(), context.getTaskId(), exception.getMessage(), exception);
    }
}
