package com.wangguangwu.casepushpipeline.core.result;

import lombok.Builder;
import lombok.Data;

/**
 * 处理器结果基类
 * 所有处理器的处理结果都应该继承此类
 *
 * @author wangguangwu
 */
@Data
@Builder
public class HandlerResult {
    
    /**
     * 处理器名称
     */
    private String handlerName;
    
    /**
     * 是否处理成功
     */
    private boolean success;
    
    /**
     * 错误码
     */
    private String errorCode;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建成功结果
     *
     * @param handlerName 处理器名称
     * @return 处理结果
     */
    public static HandlerResult success(String handlerName) {
        return HandlerResult.builder()
                .handlerName(handlerName)
                .success(true)
                .build();
    }
    
    /**
     * 创建失败结果
     *
     * @param handlerName   处理器名称
     * @param errorCode     错误码
     * @param errorMessage  错误信息
     * @return 处理结果
     */
    public static HandlerResult failure(String handlerName, String errorCode, String errorMessage) {
        return HandlerResult.builder()
                .handlerName(handlerName)
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}
