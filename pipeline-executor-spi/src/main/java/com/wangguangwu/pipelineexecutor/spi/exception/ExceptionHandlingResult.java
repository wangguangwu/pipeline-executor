package com.wangguangwu.pipelineexecutor.spi.exception;

/**
 * @author wangguangwu
 */
public enum ExceptionHandlingResult {

    /**
     * 继续执行后续处理器
     */
    CONTINUE,

    /**
     * 中断执行并抛出原始异常
     */
    FAIL_FAST,

    /**
     * 中断执行并返回默认值
     */
    COMPLETE_WITH_DEFAULT,

    /**
     * 重试当前处理器（需要配合重试策略）
     */
    RETRY;

    public boolean shouldContinue() {
        return this == CONTINUE;
    }

    public boolean shouldRetry() {
        return this == RETRY;
    }
}
