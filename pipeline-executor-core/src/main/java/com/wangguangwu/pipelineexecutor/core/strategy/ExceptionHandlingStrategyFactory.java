package com.wangguangwu.pipelineexecutor.core.strategy;

import com.wangguangwu.pipelineexecutor.core.exception.ExceptionHandlingMode;

/**
 * 异常处理策略工厂
 *
 * @author wangguangwu
 */
public class ExceptionHandlingStrategyFactory {
    
    /**
     * 创建异常处理策略
     *
     * @param mode 异常处理模式
     * @return 异常处理策略
     */
    public static ExceptionHandlingStrategy createStrategy(ExceptionHandlingMode mode) {
        if (mode == ExceptionHandlingMode.BREAK_PIPELINE) {
            return new BreakPipelineStrategy();
        } else if (mode == ExceptionHandlingMode.CONTINUE_PIPELINE) {
            return new ContinuePipelineStrategy();
        } else {
            throw new IllegalArgumentException("不支持的异常处理模式: " + mode);
        }
    }
}
