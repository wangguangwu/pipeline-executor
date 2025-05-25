package com.wangguangwu.casepipeline.core.exception;

import com.wangguangwu.casepipeline.core.context.CaseContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认异常处理器
 * 当没有自定义异常处理器时使用此默认实现
 *
 * @author wangguangwu
 */
@Slf4j
public class DefaultCasePushExceptionHandler implements CasePushExceptionHandler {

    @Override
    public void handle(CaseContext context, Exception e) {
        log.error("责任链中未处理的异常, 案件ID: {}", context.getCaseId(), e);
    }
}
