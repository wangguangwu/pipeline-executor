package com.wangguangwu.casepushpipeline.core.config;

import lombok.Builder;
import lombok.Data;

/**
 * 责任链配置类
 * 用于配置责任链的行为
 *
 * @author wangguangwu
 */
@Data
@Builder
public class PipelineConfig {

    /**
     * 异常处理模式
     */
    private ExceptionHandlingMode exceptionHandlingMode;
    
    /**
     * 是否启用性能监控
     */
    private boolean enablePerformanceMonitoring;
    
    /**
     * 是否启用详细日志
     */
    private boolean enableVerboseLogging;
    
    /**
     * 创建默认配置
     *
     * @return 默认配置
     */
    public static PipelineConfig defaultConfig() {
        return PipelineConfig.builder()
                .exceptionHandlingMode(ExceptionHandlingMode.BREAK_PIPELINE)
                .enablePerformanceMonitoring(true)
                .enableVerboseLogging(false)
                .build();
    }
    
    /**
     * 异常处理模式枚举
     */
    public enum ExceptionHandlingMode {
        /**
         * 发生异常后中断责任链
         */
        BREAK_PIPELINE,
        
        /**
         * 发生异常后继续执行后续处理器
         */
        CONTINUE_PIPELINE
    }
}
