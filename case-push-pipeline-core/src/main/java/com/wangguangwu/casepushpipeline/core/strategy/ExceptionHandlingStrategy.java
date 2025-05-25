package com.wangguangwu.casepushpipeline.core.strategy;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;

/**
 * 异常处理策略接口
 * 定义如何处理责任链执行过程中的异常
 *
 * @author wangguangwu
 */
public interface ExceptionHandlingStrategy {

    /**
     * 处理异常
     *
     * @param handler 发生异常的处理器
     * @param context 案件上下文
     * @param e       异常
     * @return 是否继续执行后续处理器
     */
    boolean handleException(CasePushHandler handler, CaseContext context, Exception e);
}
