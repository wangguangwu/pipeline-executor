package com.wangguangwu.casepushpipeline.core.handler.impl;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 理算结果推送处理器
 * 负责处理案件的理算结果计算和推送
 *
 * @author wangguangwu
 */
@Slf4j
public class CompensationHandler implements CasePushHandler {

    @Override
    public String name() {
        return "理赔处理器";
    }

    @Override
    public void handle(CaseContext context) throws Exception {
        log.info("处理理算结果推送, 案件ID: {}", context.getCaseId());
        
        // 检查前置处理器是否已完成
        Boolean imageUploaded = context.getAttribute("imageUploaded", Boolean.class);
        if (imageUploaded == null || !imageUploaded) {
            log.warn("影像资料尚未上传，跳过理算结果推送, 案件ID: {}", context.getCaseId());
            return;
        }
        
        // 检查是否有错误发生
        Boolean errorHandled = context.getAttribute("errorHandled", Boolean.class);
        if (errorHandled != null && errorHandled) {
            log.warn("前序处理器发生错误，跳过理算结果推送, 案件ID: {}, 错误信息: {}", 
                    context.getCaseId(), context.getAttribute("errorMessage"));
            return;
        }
        
        // 模拟理算结果推送逻辑
        TimeUnit.MILLISECONDS.sleep(300);
        
        // 将处理结果放入上下文
        context.setAttribute("compensationProcessed", true);
        context.setAttribute("compensationAmount", 5000.0);
        context.setAttribute("compensationTime", System.currentTimeMillis());
    }
    
    @Override
    public boolean handleException(CaseContext context, Exception e) {
        // 演示处理器自行处理异常
        log.info("理赔处理器自行处理异常: {}", e.getMessage());
        
        // 将异常信息记录到上下文中
        context.setAttribute("compensationError", true);
        context.setAttribute("compensationErrorMessage", e.getMessage());
        context.setAttribute("compensationErrorTime", System.currentTimeMillis());
        
        // 返回true表示已处理异常，不需要交给全局异常处理器
        return true;
    }
}
