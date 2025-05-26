package com.wangguangwu.pipelineexecutor.core.enums;

/**
 * 异常处理模式枚举类
 *
 * @author wangguangwu
 */
public enum ExceptionHandlingModeEnum {

    /**
     * 中断管道
     * 发生异常时中断整个管道的执行
     */
    BREAK_PIPELINE,

    /**
     * 继续执行
     * 发生异常时继续执行后续处理器
     */
    CONTINUE_PIPELINE

}
