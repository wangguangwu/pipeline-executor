package com.wangguangwu.casepipeline.core.exception;

import com.wangguangwu.casepipeline.core.context.CaseContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义告警异常处理器
 * 提供告警通知、异常记录和案件状态更新等功能
 *
 * @author wangguangwu
 */
@Slf4j
public class CustomAlertExceptionHandler implements CasePushExceptionHandler {

    @Override
    public void handle(CaseContext context, Exception e) {
        log.error("案件处理异常，触发告警机制, 案件ID: {}", context.getCaseId(), e);
        
        // 模拟发送告警通知
        sendAlertNotification(context, e);
        
        // 模拟记录异常信息到数据库
        saveExceptionToDatabase(context, e);
        
        // 模拟设置案件状态为异常
        markCaseAsException(context);
    }
    
    /**
     * 发送告警通知
     * 
     * @param context 案件上下文
     * @param e 异常
     */
    private void sendAlertNotification(CaseContext context, Exception e) {
        // 实际项目中可以集成短信、邮件、钉钉等告警通知
        log.info("发送告警通知: 案件[{}]处理异常: {}", context.getCaseId(), e.getMessage());
    }
    
    /**
     * 保存异常信息到数据库
     * 
     * @param context 案件上下文
     * @param e 异常
     */
    private void saveExceptionToDatabase(CaseContext context, Exception e) {
        // 实际项目中可以将异常信息保存到数据库
        log.info("记录异常信息到数据库: 案件[{}], 异常类型: {}", context.getCaseId(), e.getClass().getSimpleName());
    }
    
    /**
     * 标记案件状态为异常
     * 
     * @param context 案件上下文
     */
    private void markCaseAsException(CaseContext context) {
        // 实际项目中可以更新案件状态为异常
        log.info("标记案件[{}]状态为异常", context.getCaseId());
    }
}
