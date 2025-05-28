package com.wangguangwu.pipelineexecutor.core.exception;

/**
 * 异常处理结果枚举
 * <p>
 * 定义了在异常处理过程中可能采取的操作类型。
 * 用于指导管道执行引擎在异常发生后的行为。
 * </p>
 *
 * @author wangguangwu
 * @see ExceptionHandler
 */
public enum ExceptionHandleResult {

    /**
     * 继续执行后续处理器
     * <p>
     * 表示当前异常已被处理，可以继续执行管道中的后续处理器。
     * 适用于非关键性错误或已恢复的异常情况。
     * </p>
     */
    CONTINUE,

    /**
     * 终止当前管道的执行
     * <p>
     * 表示发生严重错误，需要立即终止当前管道的执行。
     * 不会影响其他可能并行执行的管道。
     * </p>
     */
    TERMINATE,

    /**
     * 跳过当前处理器，继续执行下一个
     * <p>
     * 表示当前处理器无法处理当前数据，但希望继续执行后续处理器。
     * 适用于可选处理器或条件处理器。
     * </p>
     */
    SKIP,

    /**
     * 重试当前处理器
     * <p>
     * 表示希望重试当前处理器的执行。
     * 注意：需要配合重试策略使用，避免无限重试。
     * </p>
     */
    RETRY;

    /**
     * 检查是否应该继续执行
     *
     * @return 如果是 CONTINUE 或 SKIP 返回 true
     */
    public boolean shouldContinue() {
        return this == CONTINUE || this == SKIP;
    }

    /**
     * 检查是否应该终止执行
     *
     * @return 如果是 TERMINATE 返回 true
     */
    public boolean shouldTerminate() {
        return this == TERMINATE;
    }

    /**
     * 检查是否应该重试
     *
     * @return 如果是 RETRY 返回 true
     */
    public boolean shouldRetry() {
        return this == RETRY;
    }

    /**
     * 检查是否应该跳过当前处理器
     *
     * @return 如果是 SKIP 返回 true
     */
    public boolean shouldSkip() {
        return this == SKIP;
    }

    /**
     * 从布尔值创建处理结果
     *
     * @param shouldContinue 是否继续执行
     * @return shouldContinue 为 true 时返回 CONTINUE，否则返回 TERMINATE
     */
    public static ExceptionHandleResult fromBoolean(boolean shouldContinue) {
        return shouldContinue ? CONTINUE : TERMINATE;
    }

    /**
     * 如果条件为真则继续，否则终止
     *
     * @param condition 条件
     * @return 条件为真返回 CONTINUE，否则返回 TERMINATE
     */
    public static ExceptionHandleResult continueIf(boolean condition) {
        return fromBoolean(condition);
    }
}