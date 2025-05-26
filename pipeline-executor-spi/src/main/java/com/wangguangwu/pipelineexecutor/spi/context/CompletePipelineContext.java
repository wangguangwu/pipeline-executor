package com.wangguangwu.pipelineexecutor.spi.context;

import java.util.Map;

/**
 * 完整上下文组合接口
 *
 * @param <R> 结果类型
 * @author wangguangwu
 */
public interface CompletePipelineContext<R> extends
        PipelineContext,
        AttributeStore,
        TransactionManager,
        ExecutionControl {

    /**
     * 获取上下文创建时的元数据
     *
     * @return 不可变的元数据映射
     */
    Map<String, String> getMetadata();
}

