package com.wangguangwu.casepushpipeline.core.exception;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;

/**
 * 案件推送异常处理器接口
 * 处理责任链执行过程中的异常
 *
 * @author wangguangwu
 */
public interface CasePushExceptionHandler {

    /**
     * 处理异常
     *
     * @param context 案件上下文
     * @param e 异常
     */
    void handle(CaseContext context, Exception e);
}
