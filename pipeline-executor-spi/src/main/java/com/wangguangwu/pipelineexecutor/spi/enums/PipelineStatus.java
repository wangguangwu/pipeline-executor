package com.wangguangwu.pipelineexecutor.spi.enums;

/**
 * 表示管道执行状态的枚举
 *
 * @author wangguangwu
 */
public enum PipelineStatus {
    
    /**
     * 管道准备执行
     */
    READY,
    
    /**
     * 管道正在运行
     */
    RUNNING,
    
    /**
     * 管道执行成功完成
     */
    SUCCESS,
    
    /**
     * 管道执行失败
     */
    FAILED,
    
    /**
     * 管道执行超时
     */
    TIMEOUT,
    
    /**
     * 管道执行被取消
     */
    CANCELLED
}
