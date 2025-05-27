package com.wangguangwu.pipelineexecutor.spi.context;

/**
 * @author wangguangwu
 */
public interface PipelineContext {
    // region 属性存储

    /**
     * 设置上下文属性
     *
     * @param key   属性键
     * @param value 属性值（支持 null）
     */
    void setAttribute(String key, Object value);

    /**
     * 获取上下文属性（类型不校验）
     *
     * @return 属性值（可能为 null）
     */
    Object getAttribute(String key);

    /**
     * 获取类型安全的属性值
     *
     * @param type 目标类型（非空）
     * @return 值或 null（类型不匹配时返回 null）
     */
    <T> T getAttribute(String key, Class<T> type);

    /**
     * 获取属性值或默认值
     *
     * @param defaultValue 默认值（可空）
     */
    default <T> T getAttributeOrDefault(String key, Class<T> type, T defaultValue) {
        T value = getAttribute(key, type);
        return value != null ? value : defaultValue;
    }
    // endregion

    // region 异常处理

    /**
     * 设置处理过程中的异常
     *
     * @param exception 异常实例（可空，用于清空异常状态）
     */
    void setException(Throwable exception);

    /**
     * 获取当前异常（未发生异常时返回 null）
     */
    Throwable getException();
    // endregion

    // region 流程控制

    /**
     * 标记流程中断（终止后续 Handler 执行）
     */
    void markBroken();

    /**
     * 检查流程是否已中断
     */
    boolean isBroken();
    // endregion

    // region 执行元数据

    /**
     * 获取当前执行的 Handler 名称（由框架自动设置）
     */
    String currentHandlerName();

    /**
     * 获取执行链唯一标识（可用于日志追踪）
     */
    String executionId();
    // endregion

    // region 上下文重置

    /**
     * 重置上下文状态（清空属性、异常、中断状态等）
     */
    void reset();
    // endregion
}
