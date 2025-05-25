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
 * 异常处理示例
 * 演示责任链中的异常处理机制
 *
 * @author wangguangwu
 */
@Slf4j
public class ExceptionHandlingDemo {

    public static void main(String[] args) {
        log.info("开始执行异常处理示例");
        
        // 创建责任链实例，使用自定义异常处理器
        CasePushPipeline pipeline = new CasePushPipeline(new CustomAlertExceptionHandler());
        
        // 注册处理器，注意ErrorHandler会在ImageUploadHandler之后执行
        pipeline.register(new ReportHandler())
                .register(new ImageUploadHandler())
                .register(new ErrorHandler())
                .register(new CompensationHandler());
        
        // 初始化责任链
        pipeline.init();
        
        // 创建案件上下文
        CaseContext context = new CaseContext("CASE-20250525-ERROR");
        
        try {
            // 执行责任链
            pipeline.execute(context);
        } catch (Exception e) {
            log.error("责任链执行过程中发生异常: {}", e.getMessage());
        }
        
        // 输出处理结果
        log.info("案件处理完成，结果信息：");
        log.info("案件ID: {}", context.getCaseId());
        log.info("报案处理时间: {}", context.getAttribute("reportTime"));
        log.info("影像数量: {}", context.getAttribute("imageCount"));
        
        // 由于异常发生在ErrorHandler，理赔处理器不会执行
        Object compensationAmount = context.getAttribute("compensationAmount");
        log.info("理赔金额: {}", compensationAmount != null ? compensationAmount : "未处理");
    }
}
