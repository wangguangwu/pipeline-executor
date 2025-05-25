package com.wangguangwu.casepushpipeline.core.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 验证处理器结果
 *
 * @author wangguangwu
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ValidateResult extends HandlerResult {
    
    /**
     * 验证是否通过
     */
    private boolean valid;
    
    /**
     * 验证错误信息列表
     */
    private List<String> validationErrors;
    
    /**
     * 创建验证成功结果
     *
     * @param handlerName 处理器名称
     * @return 验证结果
     */
    public static ValidateResult validSuccess(String handlerName) {
        return ValidateResult.builder()
                .handlerName(handlerName)
                .success(true)
                .valid(true)
                .build();
    }
    
    /**
     * 创建验证失败结果
     *
     * @param handlerName      处理器名称
     * @param validationErrors 验证错误信息列表
     * @return 验证结果
     */
    public static ValidateResult validFailure(String handlerName, List<String> validationErrors) {
        return ValidateResult.builder()
                .handlerName(handlerName)
                .success(true)
                .valid(false)
                .validationErrors(validationErrors)
                .build();
    }
}
