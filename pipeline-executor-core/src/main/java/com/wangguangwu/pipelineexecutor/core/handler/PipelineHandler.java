package com.wangguangwu.pipelineexecutor.core.handler;

import com.wangguangwu.pipelineexecutor.core.context.PipelineContext;

/**
 * 管道处理器接口
 *
 * @author wangguangwu
 */
public interface PipelineHandler {
    
    /**
     * 获取处理器名称
     *
     * @return 处理器名称
     */
    String name();
    
    /**
     * 处理任务
     *
     * @param context 管道上下文
     * @throws Exception 处理过程中的异常
     */
    void handle(PipelineContext context) throws Exception;
    
    /**
     * 处理异常
     * 默认不处理异常，交给全局异常处理器
     *
     * @param context   管道上下文
     * @param exception 异常
     * @return 是否已处理异常，true表示已处理，false表示未处理
     */
    default boolean handleException(PipelineContext context, Exception exception) {
        return false;
    }
}
