package com.wangguangwu.casepushpipeline.samples.core;

import com.wangguangwu.casepushpipeline.core.CasePushPipeline;
import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.exception.CustomAlertExceptionHandler;
import com.wangguangwu.casepushpipeline.core.handler.impl.CompensationHandler;
import com.wangguangwu.casepushpipeline.core.handler.impl.ErrorHandler;
import com.wangguangwu.casepushpipeline.core.handler.impl.ImageUploadHandler;
import com.wangguangwu.casepushpipeline.core.handler.impl.ReportHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义异常处理示例
 * 演示处理器自行处理异常的机制
 *
 * @author wangguangwu
 */
@Slf4j
public class CustomExceptionHandlingDemo {

    public static void main(String[] args) {
        log.info("开始执行自定义异常处理示例");
        
        // 创建责任链实例，使用自定义异常处理器
        CasePushPipeline pipeline = new CasePushPipeline(new CustomAlertExceptionHandler());
        
        // 注册处理器，按照添加顺序执行
        pipeline.register(new ReportHandler())
                .register(new ImageUploadHandler())
                .register(new ErrorHandler())
                .register(new CompensationHandler());
        
        // 初始化责任链
        pipeline.init();
        
        // 创建案件上下文
        CaseContext context = new CaseContext("CASE-20250525-CUSTOM-ERROR");
        
        try {
            // 执行责任链
            pipeline.execute(context);
        } catch (Exception e) {
            log.error("责任链执行过程中发生未处理的异常: {}", e.getMessage());
        }
        
        // 输出处理结果
        log.info("案件处理完成，结果信息：");
        log.info("案件ID: {}", context.getCaseId());
        log.info("报案处理时间: {}", context.getAttribute("reportTime"));
        log.info("影像数量: {}", context.getAttribute("imageCount"));
        
        // 检查错误处理器是否自行处理了异常
        Boolean errorHandled = context.getAttribute("errorHandled", Boolean.class);
        if (errorHandled != null && errorHandled) {
            log.info("错误处理器自行处理了异常:");
            log.info("错误信息: {}", context.getAttribute("errorMessage"));
            log.info("错误处理时间: {}", context.getAttribute("errorTime"));
        }
        
        // 检查理赔处理器是否执行
        Object compensationAmount = context.getAttribute("compensationAmount");
        log.info("理赔金额: {}", compensationAmount != null ? compensationAmount : "未处理");
    }
}
