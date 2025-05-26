package com.wangguangwu.pipelineexecutor.spi.context;

import java.util.concurrent.TimeUnit;

/**
 * 执行控制接口
 *
 * @author wangguangwu
 */
public interface ExecutionControl {

    /**
     * 请求中断执行
     */
    void requestInterrupt();

    /**
     * 检查中断状态
     *
     * @return 被中断返回true
     */
    boolean isInterrupted();

    /**
     * 设置执行超时时间
     *
     * @param timeout 超时时间（>0）
     * @param unit    时间单位（非空）
     */
    void setTimeout(long timeout, TimeUnit unit);

    /**
     * 检查是否超时
     *
     * @return 超时返回true
     */
    boolean isTimeout();

    /**
     * 获取剩余时间
     *
     * @return 剩余时间
     */
    long getRemainingTime();
}
