package com.wangguangwu.casepipeline.samples.core;

import com.wangguangwu.casepipeline.core.CasePushPipeline;
import com.wangguangwu.casepipeline.core.context.CaseContext;
import com.wangguangwu.casepipeline.core.exception.CustomAlertExceptionHandler;
import com.wangguangwu.casepipeline.core.handler.impl.CompensationHandler;
import com.wangguangwu.casepipeline.core.handler.impl.ImageUploadHandler;
import com.wangguangwu.casepipeline.core.handler.impl.ReportHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 核心版本责任链示例
 * 演示如何使用原生Java方式构建和使用责任链
 *
 * @author wangguangwu
 */
@Slf4j
public class CorePipelineDemo {

    public static void main(String[] args) {
        log.info("开始执行核心版本责任链示例");
        
        // 创建责任链实例，使用自定义异常处理器
        CasePushPipeline pipeline = new CasePushPipeline(new CustomAlertExceptionHandler());
        
        // 注册处理器
        pipeline.register(new ReportHandler())
                .register(new ImageUploadHandler())
                .register(new CompensationHandler());
        
        // 初始化责任链
        pipeline.init();
        
        // 创建案件上下文
        CaseContext context = new CaseContext("CASE-20250525-CORE");
        
        // 执行责任链
        pipeline.execute(context);
        
        // 输出处理结果
        log.info("案件处理完成，结果信息：");
        log.info("案件ID: {}", context.getCaseId());
        log.info("报案处理时间: {}", context.getAttribute("reportTime"));
        log.info("影像数量: {}", context.getAttribute("imageCount"));
        log.info("理赔金额: {}", context.getAttribute("compensationAmount"));
    }
}
