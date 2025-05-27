package com.wangguangwu.pipelineexecutor.spi.handler;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;

/**
 * @author wangguangwu
 */
public interface Handler {

    void handle(PipelineContext context) throws Exception;

}
