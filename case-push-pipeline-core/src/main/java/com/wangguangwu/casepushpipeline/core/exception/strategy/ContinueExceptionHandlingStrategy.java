package com.wangguangwu.casepushpipeline.core.exception.strategy;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.exception.CasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.exception.DefaultCasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 继续执行的异常处理策略
 * 即使发生异常，也会继续执行后续处理器
 *
 * @author wangguangwu
 */
@Slf4j
public class ContinueExceptionHandlingStrategy implements ExceptionHandlingStrategy {

    /**
     * 全局异常处理器
     */
    private final CasePushExceptionHandler exceptionHandler;

    /**
     * 构造函数
     */
    public ContinueExceptionHandlingStrategy() {
        this(new DefaultCasePushExceptionHandler());
    }

    /**
     * 构造函数
     *
     * @param exceptionHandler 异常处理器
     */
    public ContinueExceptionHandlingStrategy(CasePushExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler != null ? 
                exceptionHandler : new DefaultCasePushExceptionHandler();
    }

    @Override
    public boolean handleException(CasePushHandler handler, CaseContext context, Exception e) {
        // 先让处理器自己尝试处理异常
        boolean handled = handler.handleException(context, e);
        
        // 如果处理器没有处理异常，则交给全局异常处理器
        if (!handled) {
            log.error("处理器[{}]执行异常且未自行处理, 案件ID: {}, 继续执行后续处理器", 
                    handler.name(), 
                    context.getCaseId(), 
                    e);
            exceptionHandler.handle(context, e);
        } else {
            log.info("处理器[{}]执行异常但已自行处理, 案件ID: {}, 继续执行后续处理器", 
                    handler.name(), 
                    context.getCaseId());
        }
        
        // 策略：发生异常后继续执行责任链
        return true;
    }
}
