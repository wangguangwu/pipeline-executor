package com.wangguangwu.casepushpipeline.spi;

import com.wangguangwu.casepushpipeline.core.CasePushPipeline;
import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.exception.CasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.exception.DefaultCasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import com.wangguangwu.casepushpipeline.spi.config.HandlerOrderConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 基于SPI机制的案件推送责任链
 * 通过Java SPI机制自动加载处理器
 *
 * @author wangguangwu
 */
@Slf4j
public class SpiCasePushPipeline {

    /**
     * 内部责任链实例
     */
    private final CasePushPipeline pipeline;

    /**
     * 构造函数
     * 自动加载SPI处理器
     */
    public SpiCasePushPipeline() {
        this(null);
    }

    /**
     * 构造函数
     * 自动加载SPI处理器
     *
     * @param exceptionHandler 异常处理器
     */
    public SpiCasePushPipeline(CasePushExceptionHandler exceptionHandler) {
        // 如果没有提供异常处理器，使用默认异常处理器
        if (exceptionHandler == null) {
            exceptionHandler = loadExceptionHandler();
        }

        // 创建内部责任链实例
        pipeline = new CasePushPipeline(exceptionHandler);
        
        // 加载处理器顺序配置
        HandlerOrderConfig orderConfig = loadHandlerOrderConfig();
        
        // 加载SPI处理器
        List<CasePushHandler> handlers = loadHandlers();
        
        // 根据配置的顺序注册处理器
        registerHandlersInOrder(handlers, orderConfig);
        
        // 初始化责任链
        pipeline.init();
        
        log.info("SPI责任链初始化完成，共加载{}个处理器", handlers.size());
    }

    /**
     * 执行责任链
     *
     * @param context 案件上下文
     */
    public void execute(CaseContext context) {
        pipeline.execute(context);
    }

    /**
     * 加载SPI处理器
     *
     * @return 处理器列表
     */
    private List<CasePushHandler> loadHandlers() {
        List<CasePushHandler> handlers = new ArrayList<>();
        
        // 使用Java SPI机制加载处理器
        ServiceLoader<CasePushHandler> serviceLoader = ServiceLoader.load(CasePushHandler.class);
        for (CasePushHandler handler : serviceLoader) {
            handlers.add(handler);
            log.info("通过SPI加载处理器: {}", handler.name());
        }
        
        return handlers;
    }

    /**
     * 加载SPI异常处理器
     * 如果没有找到，则使用默认异常处理器
     *
     * @return 异常处理器
     */
    private CasePushExceptionHandler loadExceptionHandler() {
        // 使用Java SPI机制加载异常处理器
        ServiceLoader<CasePushExceptionHandler> serviceLoader = ServiceLoader.load(CasePushExceptionHandler.class);
        
        // 获取第一个异常处理器
        for (CasePushExceptionHandler handler : serviceLoader) {
            log.info("通过SPI加载异常处理器: {}", handler.getClass().getName());
            return handler;
        }
        
        // 如果没有找到，则使用默认异常处理器
        log.info("未找到SPI异常处理器，使用默认异常处理器");
        return new DefaultCasePushExceptionHandler();
    }
    
    /**
     * 加载处理器顺序配置
     * 如果存在多个配置，选择优先级最高的配置
     *
     * @return 处理器顺序配置
     */
    private HandlerOrderConfig loadHandlerOrderConfig() {
        // 使用Java SPI机制加载处理器顺序配置
        ServiceLoader<HandlerOrderConfig> serviceLoader = ServiceLoader.load(HandlerOrderConfig.class);
        
        HandlerOrderConfig selectedConfig = null;
        
        // 选择优先级最高的配置
        for (HandlerOrderConfig config : serviceLoader) {
            if (selectedConfig == null || config.getPriority() < selectedConfig.getPriority()) {
                selectedConfig = config;
            }
        }
        
        if (selectedConfig != null) {
            log.info("通过SPI加载处理器顺序配置: {}", selectedConfig.getClass().getName());
            return selectedConfig;
        }
        
        // 如果没有找到配置，则返回一个空配置
        log.info("未找到处理器顺序配置，将按照加载顺序执行处理器");
        return new HandlerOrderConfig() {
            @Override
            public List<String> getHandlerOrder() {
                return Collections.emptyList();
            }
        };
    }
    
    /**
     * 根据配置的顺序注册处理器
     *
     * @param handlers 处理器列表
     * @param orderConfig 处理器顺序配置
     */
    private void registerHandlersInOrder(List<CasePushHandler> handlers, HandlerOrderConfig orderConfig) {
        if (handlers.isEmpty()) {
            return;
        }
        
        // 获取配置的处理器顺序
        List<String> handlerOrder = orderConfig.getHandlerOrder();
        
        // 如果没有配置顺序，则按照加载顺序注册
        if (handlerOrder.isEmpty()) {
            pipeline.registerAll(handlers);
            return;
        }
        
        // 创建处理器名称到处理器的映射
        Map<String, CasePushHandler> handlerMap = new HashMap<>();
        for (CasePushHandler handler : handlers) {
            handlerMap.put(handler.name(), handler);
        }
        
        // 按照配置的顺序注册处理器
        for (String handlerName : handlerOrder) {
            CasePushHandler handler = handlerMap.get(handlerName);
            if (handler != null) {
                pipeline.register(handler);
                handlerMap.remove(handlerName);
                log.info("按照配置顺序注册处理器: {}", handlerName);
            } else {
                log.warn("配置的处理器不存在: {}", handlerName);
            }
        }
        
        // 注册剩余的处理器
        for (CasePushHandler handler : handlerMap.values()) {
            pipeline.register(handler);
            log.info("注册未配置顺序的处理器: {}", handler.name());
        }
    }
}
