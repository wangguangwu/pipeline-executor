package com.wangguangwu.case_push_pipeline.core.handler;

import com.wangguangwu.case_push_pipeline.core.context.CaseContext;

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
     * @throws Exception 处理过程中可能抛出的异常
     */
    void handle(CaseContext context) throws Exception;
    
    /**
     * 获取处理器顺序
     * 数值越小优先级越高，默认为0
     * 
     * @return 处理器顺序值
     */
    default int getOrder() {
        return 0;
    }
}
