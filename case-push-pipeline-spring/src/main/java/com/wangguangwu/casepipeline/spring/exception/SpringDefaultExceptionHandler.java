package com.wangguangwu.casepipeline.spring.exception;

import com.wangguangwu.casepipeline.core.context.CaseContext;
import com.wangguangwu.casepipeline.core.exception.CasePushExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Spring集成的默认异常处理器
 * 当没有自定义异常处理器时使用此默认实现
 *
 * @author wangguangwu
 */
@Slf4j
@Component
public class SpringDefaultExceptionHandler implements CasePushExceptionHandler {

    @Override
    public void handle(CaseContext context, Exception e) {
        log.error("Spring责任链中未处理的异常, 案件ID: {}", context.getCaseId(), e);
    }
}
