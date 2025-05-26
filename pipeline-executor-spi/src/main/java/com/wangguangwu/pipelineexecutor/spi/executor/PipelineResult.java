package com.wangguangwu.pipelineexecutor.spi.executor;

import com.wangguangwu.pipelineexecutor.spi.exception.PipelineException;

import java.util.Optional;

/**
 * 管道执行结果接口（线程安全）
 * <p>
 * 核心职责：
 * 1. 明确标识执行成功/失败
 * 2. 携带类型安全的结果数据
 * 3. 提供基本执行元数据
 *
 * @author wangguangwu
 */
public interface PipelineResult {

    /**
     * 检查执行是否成功
     *
     * @return true表示业务逻辑执行成功
     * @implNote 该方法实现必须保证线程安全
     */
    boolean isSuccess();

    /**
     * 获取错误码（成功时可能为null）
     */
    BasicStatus getErrorCode();

    /**
     * 获取错误消息（成功时可能为null）
     */
    String getErrorMessage();

    /**
     * 获取执行耗时（纳秒）
     */
    long getElapsedNanos();

    /**
     * 获取主结果数据
     *
     * @return 业务数据对象
     * @throws IllegalStateException 当isSuccess()==false时调用
     * @apiNote 成功时可能返回null（需业务方明确允许null值场景）
     */
    <T> T getData(Class<T> type);

    /**
     * 结果状态枚举（精简版）
     */
    enum BasicStatus {
        /**
         * 标识业务逻辑执行成功
         */
        SUCCESS,

        /**
         * 标识执行过程中发生错误
         */
        FAILED
    }
}
