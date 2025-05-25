package com.wangguangwu.casepushpipeline.core.handler.impl;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 错误处理器
 * 用于演示责任链中的异常处理机制
 *
 * @author wangguangwu
 */
@Slf4j
public class ErrorHandler implements CasePushHandler {

    @Override
    public void handle(CaseContext context) {
        log.info("开始执行错误处理器，案件ID: {}", context.getCaseId());
        
        // 模拟处理过程中出现异常
        log.error("处理过程中出现异常");
        throw new RuntimeException("模拟处理过程中出现异常");
    }

    @Override
    public String name() {
        return "错误处理器";
    }

    @Override
    public int getOrder() {
        // 设置为20，在影像上传处理器之后执行
        return 20;
    }
}