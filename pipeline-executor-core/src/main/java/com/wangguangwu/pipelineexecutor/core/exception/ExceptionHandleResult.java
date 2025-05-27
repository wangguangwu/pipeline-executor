package com.wangguangwu.pipelineexecutor.core.exception;

/**
 * @author wangguangwu
 */
public enum ExceptionHandleResult {
    CONTINUE,  // 继续执行后续 Handler
    TERMINATE  // 终止责任链执行
}