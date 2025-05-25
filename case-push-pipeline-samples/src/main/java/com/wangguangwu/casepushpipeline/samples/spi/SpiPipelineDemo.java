package com.wangguangwu.casepushpipeline.samples.spi;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.exception.CustomAlertExceptionHandler;
import com.wangguangwu.casepushpipeline.spi.SpiCasePushPipeline;
import lombok.extern.slf4j.Slf4j;

/**
 * SPI版本责任链示例
 * 演示如何使用SPI机制的责任链
 *
 * @author wangguangwu
 */
@Slf4j
public class SpiPipelineDemo {

    public static void main(String[] args) {
        log.info("开始执行SPI版本责任链示例");
        
        // 创建SPI责任链实例，使用自定义异常处理器
        SpiCasePushPipeline pipeline = new SpiCasePushPipeline(new CustomAlertExceptionHandler());
        
        // 创建案件上下文
        CaseContext context = new CaseContext("CASE-20250525-SPI");
        
        // 执行责任链
        pipeline.execute(context);
        
        // 输出处理结果
        log.info("案件处理完成，结果信息：");
        log.info("案件ID: {}", context.getCaseId());
        log.info("报案处理时间: {}", context.getAttribute("reportTime"));
        log.info("影像数量: {}", context.getAttribute("imageCount"));
        log.info("理赔金额: {}", context.getAttribute("compensationAmount"));
        log.info("处理框架: {}", context.getAttribute("processedBy"));
    }
}
