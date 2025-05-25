package com.wangguangwu.casepushpipeline.core.exception.strategy;

import com.wangguangwu.casepushpipeline.core.config.PipelineConfig;
import com.wangguangwu.casepushpipeline.core.exception.CasePushExceptionHandler;

/**
 * 异常处理策略工厂
 * 根据配置创建相应的异常处理策略
 *
 * @author wangguangwu
 */
public class ExceptionHandlingStrategyFactory {

    /**
     * 创建异常处理策略
     *
     * @param config 责任链配置
     * @param exceptionHandler 异常处理器
     * @return 异常处理策略
     */
    public static ExceptionHandlingStrategy createStrategy(PipelineConfig config, CasePushExceptionHandler exceptionHandler) {
        if (config == null) {
            return new DefaultExceptionHandlingStrategy(exceptionHandler);
        }

        // 使用 if 语句替代 switch 表达式，因为只有一个特殊情况需要处理
        if (PipelineConfig.ExceptionHandlingMode.CONTINUE_PIPELINE.equals(config.getExceptionHandlingMode())) {
            return new ContinueExceptionHandlingStrategy(exceptionHandler);
        } else {
            return new DefaultExceptionHandlingStrategy(exceptionHandler);
        }
    }
}
