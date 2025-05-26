package com.wangguangwu.pipelineexecutor.spi.exception;

import com.wangguangwu.pipelineexecutor.spi.enums.PipelineStatus;
import lombok.Getter;

/**
 * 自定义责任链异常，用于在 Handler 执行过程中抛出异常时携带上下文信息，
 * 如具体的处理器名称以及出错的执行阶段，便于日志记录与问题排查。
 * <p>
 * 推荐通过统一异常处理机制捕获并记录该异常。
 *
 * @author wangguangwu
 */
@Getter
public class PipelineException extends RuntimeException {

    /**
     * 出现异常的 Handler 名称
     * -- GETTER --
     * 获取处理器名称
     */
    private final String handlerName;

    /**
     * 异常发生的阶段，如 before、after、execute、error 等
     * -- GETTER --
     * 获取执行阶段
     */
    private final PipelineStatus pipelineStatus;

    /**
     * 构造函数：携带完整的异常信息
     *
     * @param message     异常消息
     * @param cause       原始异常
     * @param handlerName 处理器名称
     * @param pipelineStatus       执行阶段
     */
    public PipelineException(String message, Throwable cause, String handlerName, PipelineStatus pipelineStatus) {
        super(message, cause);
        this.handlerName = handlerName;
        this.pipelineStatus = pipelineStatus;
    }

    /**
     * 构造函数：无原始异常，仅自定义消息
     *
     * @param message     异常消息
     * @param handlerName 处理器名称
     * @param pipelineStatus       执行阶段
     */
    public PipelineException(String message, String handlerName, PipelineStatus pipelineStatus) {
        super(message);
        this.handlerName = handlerName;
        this.pipelineStatus = pipelineStatus;
    }

    /**
     * 构造函数：无异常消息，仅传入 cause
     *
     * @param cause       原始异常
     * @param handlerName 处理器名称
     * @param pipelineStatus       执行阶段
     */
    public PipelineException(Throwable cause, String handlerName, PipelineStatus pipelineStatus) {
        super(cause);
        this.handlerName = handlerName;
        this.pipelineStatus = pipelineStatus;
    }

    /**
     * 重写 toString 方法，增强调试信息输出
     *
     * @return 异常描述字符串
     */
    @Override
    public String toString() {
        return String.format("PipelineException in handler [%s] during phase [%s]: %s",
                handlerName, pipelineStatus.name(), getMessage());
    }
}
