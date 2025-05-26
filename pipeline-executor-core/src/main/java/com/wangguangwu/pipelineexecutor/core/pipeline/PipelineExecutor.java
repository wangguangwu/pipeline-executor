package com.wangguangwu.pipelineexecutor.core.pipeline;

import com.wangguangwu.pipelineexecutor.core.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.core.exception.PipelineExceptionHandler;
import com.wangguangwu.pipelineexecutor.core.handler.PipelineHandler;
import com.wangguangwu.pipelineexecutor.core.strategy.ExceptionHandlingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 管道执行器
 *
 * @author wangguangwu
 */
@Slf4j
public class PipelineExecutor {
    
    /**
     * 异常处理器
     */
    private final PipelineExceptionHandler exceptionHandler;
    
    /**
     * 异常处理策略
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    
    /**
     * 构造函数
     *
     * @param exceptionHandler         异常处理器
     * @param exceptionHandlingStrategy 异常处理策略
     */
    public PipelineExecutor(PipelineExceptionHandler exceptionHandler, 
                          ExceptionHandlingStrategy exceptionHandlingStrategy) {
        this.exceptionHandler = exceptionHandler;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }
    
    /**
     * 执行处理器链
     *
     * @param handlers 处理器列表
     * @param context  管道上下文
     */
    public void execute(List<PipelineHandler> handlers, PipelineContext context) {
        if (handlers == null || handlers.isEmpty()) {
            log.warn("没有可执行的处理器");
            return;
        }
        
        for (PipelineHandler handler : handlers) {
            try {
                log.debug("开始执行处理器: {}, 任务ID: {}", handler.name(), context.getTaskId());
                handler.handle(context);
                log.debug("处理器执行完成: {}, 任务ID: {}", handler.name(), context.getTaskId());
            } catch (Exception e) {
                log.debug("处理器执行异常: {}, 任务ID: {}, 异常信息: {}", 
                        handler.name(), context.getTaskId(), e.getMessage());
                
                // 使用异常处理策略处理异常
                boolean continueExecution = exceptionHandlingStrategy.handleException(
                        context, handler, exceptionHandler, e);
                
                if (!continueExecution) {
                    log.debug("中断管道执行, 任务ID: {}", context.getTaskId());
                    break;
                }
            }
        }
    }
}
