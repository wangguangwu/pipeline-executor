package com.wangguangwu.pipelineexecutor.core.spi;

import com.wangguangwu.pipelineexecutor.core.handler.PipelineHandler;
import com.wangguangwu.pipelineexecutor.core.registry.DefaultHandlerOrderRegistry;
import com.wangguangwu.pipelineexecutor.core.registry.HandlerOrderRegistry;
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
    
    /**
     * 加载处理器顺序注册表
     * 如果没有找到自定义实现，则返回默认实现
     *
     * @return 处理器顺序注册表
     */
    public static HandlerOrderRegistry loadHandlerOrderRegistry() {
        HandlerOrderRegistry registry = null;
        
        try {
            log.debug("开始加载SPI处理器顺序注册表实现");
            ServiceLoader<HandlerOrderRegistry> serviceLoader = ServiceLoader.load(HandlerOrderRegistry.class);
            
            for (HandlerOrderRegistry orderRegistry : serviceLoader) {
                registry = orderRegistry;
                log.debug("加载SPI处理器顺序注册表: {}", orderRegistry.getClass().getName());
                break; // 只使用第一个找到的实现
            }
        } catch (Exception e) {
            log.error("加载SPI处理器顺序注册表失败", e);
        }
        
        if (registry == null) {
            log.debug("未找到SPI处理器顺序注册表实现，使用默认实现");
            registry = new DefaultHandlerOrderRegistry();
        }
        
        return registry;
    }
}
