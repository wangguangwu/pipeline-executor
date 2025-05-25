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
    public boolean handleException(CaseContext context, Exception e) {
        // 演示处理器自行处理异常
        log.info("错误处理器自行处理异常: {}", e.getMessage());
        
        // 将异常信息记录到上下文中
        context.setAttribute("errorHandled", true);
        context.setAttribute("errorMessage", e.getMessage());
        context.setAttribute("errorTime", System.currentTimeMillis());
        
        // 返回true表示已处理异常，不需要交给全局异常处理器
        return true;
    }
}