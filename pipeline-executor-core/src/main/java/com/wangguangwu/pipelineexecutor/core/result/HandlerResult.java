package com.wangguangwu.pipelineexecutor.core.result;

import lombok.Getter;
import lombok.ToString;

/**
 * 处理器执行结果
 *
 * @author wangguangwu
 */
@Getter
@ToString
public class HandlerResult {
    
    /**
     * 处理器名称
     */
    private final String handlerName;
    
    /**
     * 是否成功
     */
    private final boolean success;
    
    /**
     * 错误消息
     */
    private final String errorMessage;
    
    /**
     * 创建成功结果
     *
     * @param handlerName 处理器名称
     * @return 处理结果
     */
    public static HandlerResult success(String handlerName) {
        return new HandlerResult(handlerName, true, null);
    }
    
    /**
     * 创建失败结果
     *
     * @param handlerName  处理器名称
     * @param errorMessage 错误消息
     * @return 处理结果
     */
    public static HandlerResult failure(String handlerName, String errorMessage) {
        return new HandlerResult(handlerName, false, errorMessage);
    }
    
    /**
     * 构造函数
     *
     * @param handlerName  处理器名称
     * @param success      是否成功
     * @param errorMessage 错误消息
     */
    protected HandlerResult(String handlerName, boolean success, String errorMessage) {
        this.handlerName = handlerName;
        this.success = success;
        this.errorMessage = errorMessage;
    }
}
