package com.wangguangwu.pipelineexecutor.core.exception;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangguangwu
 */
// core 模块
public class DefaultExceptionHandler implements ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Override
    public ExceptionHandleResult handle(Throwable e, PipelineContext context) {
        // 记录异常日志（包含执行链 ID）
        logger.error("[Pipeline 异常] 执行链: {} | 当前 Handler: {} | 错误: {}",
                context.executionId(),
                context.currentHandlerName(),
                e.getMessage(),
                e);

        // 设置上下文异常状态
        context.setException(e);

        // 默认策略：终止执行
        return ExceptionHandleResult.TERMINATE;
    }
}
