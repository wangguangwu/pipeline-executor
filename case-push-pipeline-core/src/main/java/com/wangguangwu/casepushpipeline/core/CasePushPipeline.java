package com.wangguangwu.casepushpipeline.core;

import com.wangguangwu.casepushpipeline.core.config.PipelineConfig;
import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.exception.CasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.exception.DefaultCasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.exception.strategy.ExceptionHandlingStrategy;
import com.wangguangwu.casepushpipeline.core.exception.strategy.ExceptionHandlingStrategyFactory;
import com.wangguangwu.casepushpipeline.core.executor.DefaultPipelineExecutor;
import com.wangguangwu.casepushpipeline.core.executor.PipelineExecutor;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import com.wangguangwu.casepushpipeline.core.registry.DefaultHandlerRegistry;
import com.wangguangwu.casepushpipeline.core.registry.HandlerRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 案件推送责任链
 * 管理处理器的执行顺序和异常处理
 *
 * @author wangguangwu
 */
@Slf4j
public class CasePushPipeline {

    /**
     * 处理器注册器
     */
    private final HandlerRegistry handlerRegistry;
    
    /**
     * 责任链执行器
     */
    private final PipelineExecutor executor;

    /**
     * 构造函数
     */
    public CasePushPipeline() {
        this(null, PipelineConfig.defaultConfig());
    }

    /**
     * 构造函数
     *
     * @param exceptionHandler 异常处理器
     */
    public CasePushPipeline(CasePushExceptionHandler exceptionHandler) {
        this(exceptionHandler, PipelineConfig.defaultConfig());
    }
    
    /**
     * 构造函数
     *
     * @param config 责任链配置
     */
    public CasePushPipeline(PipelineConfig config) {
        this(null, config);
    }
    
    /**
     * 构造函数
     *
     * @param exceptionHandler 异常处理器
     * @param config 责任链配置
     */
    public CasePushPipeline(CasePushExceptionHandler exceptionHandler, PipelineConfig config) {
        CasePushExceptionHandler handler = exceptionHandler != null ? 
                exceptionHandler : new DefaultCasePushExceptionHandler();
        PipelineConfig pipelineConfig = config != null ? config : PipelineConfig.defaultConfig();
        
        this.handlerRegistry = new DefaultHandlerRegistry();
        this.executor = new DefaultPipelineExecutor(handler, pipelineConfig);
    }
    
    /**
     * 构造函数
     *
     * @param handlerRegistry 处理器注册器
     * @param executor 责任链执行器
     */
    public CasePushPipeline(HandlerRegistry handlerRegistry, PipelineExecutor executor) {
        this.handlerRegistry = handlerRegistry != null ? handlerRegistry : new DefaultHandlerRegistry();
        this.executor = executor != null ? executor : new DefaultPipelineExecutor();
    }

    /**
     * 注册处理器（添加到链表末尾）
     *
     * @param handler 处理器
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline register(CasePushHandler handler) {
        handlerRegistry.register(handler);
        return this;
    }
    
    /**
     * 在链表头部添加处理器
     *
     * @param handler 处理器
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline registerFirst(CasePushHandler handler) {
        handlerRegistry.registerFirst(handler);
        return this;
    }
    
    /**
     * 在指定处理器之前添加新处理器
     *
     * @param existingHandlerName 已存在的处理器名称
     * @param newHandler 新处理器
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline registerBefore(String existingHandlerName, CasePushHandler newHandler) {
        handlerRegistry.registerBefore(existingHandlerName, newHandler);
        return this;
    }
    
    /**
     * 在指定处理器之后添加新处理器
     *
     * @param existingHandlerName 已存在的处理器名称
     * @param newHandler 新处理器
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline registerAfter(String existingHandlerName, CasePushHandler newHandler) {
        handlerRegistry.registerAfter(existingHandlerName, newHandler);
        return this;
    }

    /**
     * 注册多个处理器
     *
     * @param handlers 处理器列表
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline registerAll(List<CasePushHandler> handlers) {
        handlerRegistry.registerAll(handlers);
        return this;
    }

    /**
     * 设置异常处理器
     *
     * @param exceptionHandler 异常处理器
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline setExceptionHandler(CasePushExceptionHandler exceptionHandler) {
        if (exceptionHandler != null && executor instanceof DefaultPipelineExecutor) {
            // 创建新的执行器，使用新的异常处理策略
            ExceptionHandlingStrategy strategy = ExceptionHandlingStrategyFactory.createStrategy(
                    getConfig(), exceptionHandler);
            return new CasePushPipeline(this.handlerRegistry, new DefaultPipelineExecutor(strategy, getConfig()));
        }
        return this;
    }
    
    /**
     * 设置异常处理模式
     *
     * @param mode 异常处理模式
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline setExceptionHandlingMode(PipelineConfig.ExceptionHandlingMode mode) {
        if (mode != null && executor instanceof DefaultPipelineExecutor) {
            // 创建新的配置
            PipelineConfig newConfig = PipelineConfig.builder()
                    .exceptionHandlingMode(mode)
                    .enablePerformanceMonitoring(getConfig().isEnablePerformanceMonitoring())
                    .enableVerboseLogging(getConfig().isEnableVerboseLogging())
                    .build();
            
            // 创建新的执行器
            DefaultPipelineExecutor currentExecutor = (DefaultPipelineExecutor) executor;
            return new CasePushPipeline(this.handlerRegistry, new DefaultPipelineExecutor(
                    ExceptionHandlingStrategyFactory.createStrategy(newConfig, null), newConfig));
        }
        return this;
    }

    /**
     * 初始化责任链
     * 
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline init() {
        handlerRegistry.init();
        return this;
    }

    /**
     * 获取处理器执行顺序
     *
     * @return 处理器执行顺序的字符串表示
     */
    public String getHandlerOrderInfo() {
        return handlerRegistry.getHandlerOrderInfo();
    }

    /**
     * 执行责任链
     *
     * @param context 案件上下文
     */
    public void execute(CaseContext context) {
        if (context == null) {
            log.error("案件上下文不能为空");
            throw new IllegalArgumentException("案件上下文不能为空");
        }
        
        executor.execute(handlerRegistry.getHandlerContexts(), context);
    }
    
    /**
     * 获取最后一次执行的耗时（毫秒）
     *
     * @return 执行耗时
     */
    public long getLastExecutionTime() {
        return executor.getLastExecutionTime();
    }
    
    /**
     * 获取执行状态
     *
     * @return 执行状态
     */
    public PipelineExecutor.ExecutionStatus getExecutionStatus() {
        return executor.getExecutionStatus();
    }
    
    /**
     * 获取责任链配置
     *
     * @return 责任链配置
     */
    public PipelineConfig getConfig() {
        return executor.getConfig();
    }
}
