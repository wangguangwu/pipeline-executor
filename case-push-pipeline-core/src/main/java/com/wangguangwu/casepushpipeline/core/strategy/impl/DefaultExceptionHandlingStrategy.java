package com.wangguangwu.casepushpipeline.core.strategy.impl;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.exception.CasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.exception.DefaultCasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import com.wangguangwu.casepushpipeline.core.strategy.ExceptionHandlingStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认异常处理策略
 * 当处理器发生异常时，中断责任链执行
 *
 * @author wangguangwu
 */
@Slf4j
public class DefaultExceptionHandlingStrategy implements ExceptionHandlingStrategy {

    /**
     * 异常处理器
     */
    private final CasePushExceptionHandler exceptionHandler;

    /**
     * 构造函数
     */
    public DefaultExceptionHandlingStrategy() {
        this(new DefaultCasePushExceptionHandler());
    }

    /**
     * 构造函数
     *
     * @param exceptionHandler 异常处理器
     */
    public DefaultExceptionHandlingStrategy(CasePushExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler != null ? exceptionHandler : new DefaultCasePushExceptionHandler();
    }

    @Override
    public boolean handleException(CasePushHandler handler, CaseContext context, Exception e) {
        // 首先尝试让处理器自行处理异常
        if (handler.handleException(context, e)) {
            log.info("处理器[{}]已自行处理异常, 案件ID: {}", handler.name(), context.getCaseId());
            return true; // 处理器已处理异常，继续执行后续处理器
        }

        // 处理器未处理异常，交给异常处理器
        log.error("处理器[{}]执行异常且未自行处理, 案件ID: {}, 中断责任链执行", handler.name(), context.getCaseId(), e);
        exceptionHandler.handleException(context, e);
        
        // 默认策略：发生异常时中断责任链
        return false;
    }
}
