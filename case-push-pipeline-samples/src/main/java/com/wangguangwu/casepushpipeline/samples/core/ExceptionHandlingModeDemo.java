package com.wangguangwu.casepushpipeline.samples.core;

import com.wangguangwu.casepushpipeline.core.CasePushPipeline;
import com.wangguangwu.casepushpipeline.core.config.PipelineConfig;
import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.samples.core.handler.impl.CompensationHandler;
import com.wangguangwu.casepushpipeline.samples.core.handler.impl.ErrorHandler;
import com.wangguangwu.casepushpipeline.samples.core.handler.impl.ImageUploadHandler;
import com.wangguangwu.casepushpipeline.samples.core.handler.impl.ValidateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 异常处理模式演示
 * 展示如何配置异常处理模式，决定出现异常后是继续执行后续处理器还是中断责任链
 *
 * @author wangguangwu
 */
@Slf4j
public class ExceptionHandlingModeDemo {

    public static void main(String[] args) {
        log.info("===== 开始演示异常处理模式 =====");
        
        // 演示中断模式
        demoBreakPipelineMode();
        
        // 演示继续执行模式
        demoContinuePipelineMode();
        
        log.info("===== 异常处理模式演示结束 =====");
    }
    
    /**
     * 演示中断模式
     * 发生异常后中断责任链执行
     */
    private static void demoBreakPipelineMode() {
        log.info("===== 中断模式演示开始 =====");
        
        // 创建配置，设置异常处理模式为中断责任链
        PipelineConfig config = PipelineConfig.builder()
                .exceptionHandlingMode(PipelineConfig.ExceptionHandlingMode.BREAK_PIPELINE)
                .enableVerboseLogging(true)
                .build();
        
        // 创建责任链
        CasePushPipeline pipeline = new CasePushPipeline(config);
        
        // 注册处理器
        pipeline.register(new ValidateHandler())
                .register(new ErrorHandler())  // 这个处理器会抛出异常
                .register(new ImageUploadHandler())
                .register(new CompensationHandler())
                .init();
        
        log.info("责任链配置: 异常处理模式={}", pipeline.getConfig().getExceptionHandlingMode());
        log.info("处理器顺序: {}", pipeline.getHandlerOrderInfo());
        
        // 创建案件上下文
        CaseContext context = new CaseContext("CASE-001");
        
        try {
            // 执行责任链
            pipeline.execute(context);
        } catch (Exception e) {
            log.error("责任链执行异常", e);
        }
        
        log.info("===== 中断模式演示结束 =====");
    }
    
    /**
     * 演示继续执行模式
     * 发生异常后继续执行后续处理器
     */
    private static void demoContinuePipelineMode() {
        log.info("===== 继续执行模式演示开始 =====");
        
        // 创建配置，设置异常处理模式为继续执行
        PipelineConfig config = PipelineConfig.builder()
                .exceptionHandlingMode(PipelineConfig.ExceptionHandlingMode.CONTINUE_PIPELINE)
                .enableVerboseLogging(true)
                .build();
        
        // 创建责任链
        CasePushPipeline pipeline = new CasePushPipeline(config);
        
        // 注册处理器
        pipeline.register(new ValidateHandler())
                .register(new ErrorHandler())  // 这个处理器会抛出异常
                .register(new ImageUploadHandler())
                .register(new CompensationHandler())
                .init();
        
        log.info("责任链配置: 异常处理模式={}", pipeline.getConfig().getExceptionHandlingMode());
        log.info("处理器顺序: {}", pipeline.getHandlerOrderInfo());
        
        // 创建案件上下文
        CaseContext context = new CaseContext("CASE-002");
        
        try {
            // 执行责任链
            pipeline.execute(context);
        } catch (Exception e) {
            log.error("责任链执行异常", e);
        }
        
        log.info("===== 继续执行模式演示结束 =====");
    }
}
