package com.wangguangwu.casepushpipeline.core.executor;

import com.wangguangwu.casepushpipeline.core.config.PipelineConfig;
import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.context.CasePushHandlerContext;

import java.util.List;

/**
 * 责任链执行器接口
 * 负责执行责任链中的处理器
 *
 * @author wangguangwu
 */
public interface PipelineExecutor {

    /**
     * 执行责任链
     *
     * @param handlerContexts 处理器上下文列表
     * @param context 案件上下文
     */
    void execute(List<CasePushHandlerContext> handlerContexts, CaseContext context);
    
    /**
     * 获取最后一次执行的耗时（毫秒）
     *
     * @return 执行耗时
     */
    long getLastExecutionTime();
    
    /**
     * 获取执行状态
     *
     * @return 执行状态
     */
    ExecutionStatus getExecutionStatus();
    
    /**
     * 获取责任链配置
     *
     * @return 责任链配置
     */
    PipelineConfig getConfig();
    
    /**
     * 执行状态枚举
     */
    enum ExecutionStatus {
        /**
         * 未执行
         */
        NOT_STARTED,
        
        /**
         * 执行中
         */
        RUNNING,
        
        /**
         * 执行完成
         */
        COMPLETED,
        
        /**
         * 执行异常
         */
        FAILED
    }
}
