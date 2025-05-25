package com.wangguangwu.casepipeline.spi;

import com.wangguangwu.casepipeline.core.CasePushPipeline;
import com.wangguangwu.casepipeline.core.context.CaseContext;
import com.wangguangwu.casepipeline.core.exception.CasePushExceptionHandler;
import com.wangguangwu.casepipeline.core.exception.DefaultCasePushExceptionHandler;
import com.wangguangwu.casepipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

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
        
        // 加载并注册SPI处理器
        List<CasePushHandler> handlers = loadHandlers();
        pipeline.registerAll(handlers);
        
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
            log.info("通过SPI加载处理器: {}, 顺序: {}", handler.name(), handler.getOrder());
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
}
