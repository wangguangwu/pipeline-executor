package com.wangguangwu.pipelineexecutor.spi.pipeline;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.spi.handler.Handler;

/**
 * @author wangguangwu
 */
public interface Pipeline {
    Pipeline addFirst(String name, Class<? extends Handler> handlerClass);
    Pipeline addLast(String name, Class<? extends Handler> handlerClass);
    Pipeline addBefore(String baseName, String name, Class<? extends Handler> handlerClass);
    Pipeline addAfter(String baseName, String name, Class<? extends Handler> handlerClass);
    Pipeline remove(String name);
    Pipeline replace(String name, Class<? extends Handler> handlerClass);
    void execute(PipelineContext context);
}
