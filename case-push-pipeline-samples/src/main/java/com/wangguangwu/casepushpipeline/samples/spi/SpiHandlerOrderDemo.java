package com.wangguangwu.casepushpipeline.samples.spi;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.spi.SpiCasePushPipeline;
import lombok.extern.slf4j.Slf4j;

/**
 * SPI处理器顺序示例
 * 演示如何通过SPI机制指定处理器的执行顺序
 *
 * @author wangguangwu
 */
@Slf4j
public class SpiHandlerOrderDemo {

    public static void main(String[] args) {
        log.info("开始执行SPI处理器顺序示例");
        
        // 创建SPI责任链实例
        // 它会自动加载SPI处理器和处理器顺序配置
        SpiCasePushPipeline pipeline = new SpiCasePushPipeline();
        
        // 创建案件上下文
        CaseContext context = new CaseContext("CASE-20250525-SPI-ORDER");
        
        try {
            // 执行责任链
            pipeline.execute(context);
        } catch (Exception e) {
            log.error("责任链执行过程中发生异常: {}", e.getMessage());
        }
        
        // 输出处理结果
        log.info("案件处理完成，结果信息：");
        log.info("案件ID: {}", context.getCaseId());
        
        // 获取处理结果
        Object reportTime = context.getAttribute("reportTime");
        Object imageCount = context.getAttribute("imageCount");
        Object compensationAmount = context.getAttribute("compensationAmount");
        
        log.info("报案处理时间: {}", reportTime != null ? reportTime : "未处理");
        log.info("影像数量: {}", imageCount != null ? imageCount : "未处理");
        log.info("理赔金额: {}", compensationAmount != null ? compensationAmount : "未处理");
    }
}
