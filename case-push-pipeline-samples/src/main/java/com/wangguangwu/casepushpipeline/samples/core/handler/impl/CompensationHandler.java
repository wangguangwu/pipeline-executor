package com.wangguangwu.casepushpipeline.samples.core.handler.impl;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 补偿处理器
 * 用于在责任链执行过程中出现异常后进行补偿操作
 *
 * @author wangguangwu
 */
@Slf4j
public class CompensationHandler implements CasePushHandler {

    @Override
    public String name() {
        return "补偿处理器";
    }

    @Override
    public void handle(CaseContext context) {
        String caseId = context.getCaseId();
        log.info("补偿处理器开始处理案件: {}", caseId);
        
        // 检查是否有错误发生
        Boolean errorHandled = (Boolean) context.getAttribute("errorHandled");
        if (errorHandled != null && errorHandled) {
            String errorMessage = (String) context.getAttribute("errorMessage");
            log.info("检测到之前处理器发生异常: {}, 开始执行补偿逻辑", errorMessage);
            
            // 执行补偿逻辑
            context.setAttribute("compensated", true);
            context.setAttribute("compensationTime", System.currentTimeMillis());
            log.info("补偿逻辑执行完成");
        } else {
            log.info("未检测到异常，无需执行补偿逻辑");
        }
        
        log.info("补偿处理器处理完成: {}", caseId);
    }
}
