package com.wangguangwu.pipelineexecutor.spi;

/**
 * 管道扩展点接口
 * 用于扩展管道功能
 *
 * @author wangguangwu
 */
public interface PipelineExtension {
    
    /**
     * 获取扩展点名称
     *
     * @return 扩展点名称
     */
    String getName();
    
    /**
     * 获取扩展点描述
     *
     * @return 扩展点描述
     */
    String getDescription();
}
