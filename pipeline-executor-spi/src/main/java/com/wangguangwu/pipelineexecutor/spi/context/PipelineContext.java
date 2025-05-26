package com.wangguangwu.pipelineexecutor.spi.context;

/**
 * 管道上下文基础接口（线程安全）
 * <p>
 * 生命周期状态转换：
 * CREATED → ACTIVE → CLOSED
 * 不可逆状态转换，关闭后不可重新激活
 * </p>
 *
 * @author wangguangwu
 */
public interface PipelineContext extends AutoCloseable {


    // ================= 基础信息方法 =================

    /**
     * 获取上下文唯一标识（线程安全）
     *
     * @return 全局唯一的上下文ID（格式：UUIDv4）
     * @implNote 实现应保证此方法无阻塞
     */
    String getContextId();

    /**
     * 获取上下文创建时间戳（线程安全）
     *
     * @return 毫秒级时间戳（System.currentTimeMillis()）
     * @implNote 时间戳应在对象构造时固化
     */
    long getCreateTime();

    // ================= 状态检查方法 =================

    /**
     * 获取当前上下文状态（线程安全）
     *
     * @return 不可变的状态枚举值
     * @implSpec 实现必须保证状态读写的原子性
     */
    ContextState getState();

    /**
     * 检查是否处于活跃状态（线程安全）
     *
     * @return 当状态为ACTIVE时返回true
     * @see #getState()
     */
    default boolean isActive() {
        return getState() == ContextState.ACTIVE;
    }

    /**
     * 检查是否已关闭（线程安全）
     *
     * @return 当状态为CLOSED时返回true
     * @see #getState()
     */
    default boolean isClosed() {
        return getState() == ContextState.CLOSED;
    }

    // ================= 生命周期控制 =================

    /**
     * 激活上下文（线程安全）
     *
     * @throws IllegalStateException 如果当前状态不是CREATED
     * @implNote 典型实现应使用CAS保证状态转换原子性
     */
    void activate();

    /**
     * 关闭并释放资源（线程安全）
     *
     * @throws IllegalStateException 如果重复关闭
     * @implSpec 实现必须保证幂等性（多次调用close()不产生副作用）
     */
    @Override
    void close();

    /**
     * 上下文状态枚举
     */
    enum ContextState {
        /**
         * 初始创建状态（可执行初始化操作）
         */
        CREATED,

        /**
         * 运行中状态（可执行业务操作）
         */
        ACTIVE,

        /**
         * 已关闭状态（仅允许资源清理）
         */
        CLOSED
    }
}