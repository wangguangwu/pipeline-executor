package com.wangguangwu.pipelineexecutor.core.spi;

import com.wangguangwu.pipelineexecutor.core.handler.api.PipelineHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 处理器SPI服务加载器
 *
 * @author wangguangwu
 */
@Slf4j
public class HandlerServiceLoader {
    
    private HandlerServiceLoader() {
        // 工具类，禁止实例化
    }
    
    /**
     * 加载所有可用的处理器实现
     *
     * @return 处理器列表
     */
    public static List<PipelineHandler> loadHandlers() {
        List<PipelineHandler> handlers = new ArrayList<>();
        
        try {
            log.debug("开始加载SPI处理器实现");
            ServiceLoader<PipelineHandler> serviceLoader = ServiceLoader.load(PipelineHandler.class);
            
            for (PipelineHandler handler : serviceLoader) {
                handlers.add(handler);
                log.debug("加载SPI处理器: {}", handler.name());
            }
            
            log.debug("成功加载{}个SPI处理器", handlers.size());
        } catch (Exception e) {
            log.error("加载SPI处理器失败", e);
        }
        
        return handlers;
    }
}
