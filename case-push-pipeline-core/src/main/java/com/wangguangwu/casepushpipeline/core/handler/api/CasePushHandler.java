package com.wangguangwu.casepushpipeline.core.handler.api;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.result.HandlerResult;

/**
 * 案件推送处理器接口
 * 责任链中的每个处理节点需要实现此接口
 *
 * @author wangguangwu
 */
public interface CasePushHandler {

    /**
     * 获取处理器名称
     * 用于在日志和异常信息中标识处理器
     *
     * @return 处理器名称
     */
    String name();

    /**
     * 处理案件
     * 实现此方法完成具体的业务逻辑
     *
     * @param context 案件上下文，包含案件ID和处理过程中的数据
     * @return 处理结果
     * @throws Exception 处理过程中可能抛出的异常
     */
    HandlerResult handle(CaseContext context) throws Exception;
    
    /**
     * 处理异常
     * 当处理器执行过程中发生异常时，会调用此方法进行处理
     * 默认实现为不处理异常，返回false表示需要交给全局异常处理器处理
     *
     * @param context 案件上下文
     * @param e 异常
     * @return 是否已处理异常，true表示已处理，false表示未处理需要交给全局异常处理器
     */
    default boolean handleException(CaseContext context, Exception e) {
        // 默认不处理异常，交给全局异常处理器
        return false;
    }
}
