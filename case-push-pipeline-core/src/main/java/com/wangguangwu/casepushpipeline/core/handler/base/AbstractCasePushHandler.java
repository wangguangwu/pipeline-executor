package com.wangguangwu.casepushpipeline.core.handler.base;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.api.CasePushHandler;
import com.wangguangwu.casepushpipeline.core.result.HandlerResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象处理器基类
 * 提供基础实现，子类只需要实现核心业务逻辑
 *
 * @author wangguangwu
 */
@Slf4j
public abstract class AbstractCasePushHandler implements CasePushHandler {

    @Override
    public HandlerResult handle(CaseContext context) throws Exception {
        try {
            // 前置处理
            beforeHandle(context);
            
            // 核心业务逻辑
            HandlerResult result = doHandle(context);
            
            // 后置处理
            afterHandle(context, result);
            
            return result;
        } catch (Exception e) {
            log.error("处理器[{}]执行异常, 案件ID: {}", name(), context.getCaseId(), e);
            
            // 尝试处理异常并返回结果
            HandlerResult result = handleExceptionWithResult(context, e);
            if (result != null) {
                return result;
            }
            
            // 无法处理异常，继续抛出
            throw e;
        }
    }
    
    /**
     * 核心业务逻辑
     * 子类必须实现此方法
     *
     * @param context 案件上下文
     * @return 处理结果
     * @throws Exception 处理过程中可能抛出的异常
     */
    protected abstract HandlerResult doHandle(CaseContext context) throws Exception;
    
    /**
     * 前置处理
     * 在核心业务逻辑执行前调用
     *
     * @param context 案件上下文
     */
    protected void beforeHandle(CaseContext context) {
        // 默认空实现，子类可以覆盖
    }
    
    /**
     * 后置处理
     * 在核心业务逻辑执行后调用
     *
     * @param context 案件上下文
     * @param result  处理结果
     */
    protected void afterHandle(CaseContext context, HandlerResult result) {
        // 默认空实现，子类可以覆盖
    }
    
    /**
     * 处理异常并返回结果
     * 子类可以覆盖此方法来处理异常并返回结果
     *
     * @param context 案件上下文
     * @param e       异常
     * @return 处理结果，如果返回null表示无法处理异常
     */
    protected HandlerResult handleExceptionWithResult(CaseContext context, Exception e) {
        // 默认返回null，表示无法处理异常
        return null;
    }
}
