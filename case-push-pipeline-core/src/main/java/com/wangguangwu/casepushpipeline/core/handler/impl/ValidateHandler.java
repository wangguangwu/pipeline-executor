package com.wangguangwu.casepushpipeline.core.handler.impl;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 验证处理器
 * 用于验证案件数据是否符合要求
 *
 * @author wangguangwu
 */
@Slf4j
public class ValidateHandler implements CasePushHandler {

    @Override
    public String name() {
        return "ValidateHandler";
    }

    @Override
    public void handle(CaseContext context) {
        String caseId = context.getCaseId();
        log.info("验证处理器开始处理案件: {}", caseId);
        
        // 模拟验证逻辑
        context.setAttribute("validated", true);
        
        log.info("验证处理器处理完成: {}", caseId);
    }
}
