package com.wangguangwu.pipelineexecutor.core.pipeline;

import com.wangguangwu.pipelineexecutor.core.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.core.exception.DefaultPipelineExceptionHandler;
import com.wangguangwu.pipelineexecutor.core.exception.ExceptionHandlingMode;
import com.wangguangwu.pipelineexecutor.core.exception.PipelineExceptionHandler;
import com.wangguangwu.pipelineexecutor.core.handler.api.PipelineHandler;
import com.wangguangwu.pipelineexecutor.core.registry.DefaultHandlerRegistry;
import com.wangguangwu.pipelineexecutor.core.registry.HandlerRegistry;
import com.wangguangwu.pipelineexecutor.core.spi.HandlerServiceLoader;
import com.wangguangwu.pipelineexecutor.core.strategy.ExceptionHandlingStrategy;
import com.wangguangwu.pipelineexecutor.core.strategy.ExceptionHandlingStrategyFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 管道实现类
 * 负责管理处理器和执行管道
 *
 * @author wangguangwu
 */
@Slf4j
public class Pipeline {
    
    /**
     * 处理器注册表
     */
    private final HandlerRegistry handlerRegistry;
    
    /**
     * 异常处理器
     */
    private final PipelineExceptionHandler exceptionHandler;
    
    /**
     * 异常处理策略
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    
    /**
     * 管道执行器
     */
    private final PipelineExecutor executor;
    
    /**
     * 处理器顺序列表
     */
    private final List<String> handlerOrder = new ArrayList<>();
    
    /**
     * 是否已初始化
     */
    private boolean initialized = false;
    
    /**
     * 默认构造函数
     * 使用默认的异常处理器和中断管道策略
     */
    public Pipeline() {
        this(new DefaultHandlerRegistry(), new DefaultPipelineExceptionHandler(), 
             ExceptionHandlingMode.BREAK_PIPELINE);
    }
    
    /**
     * 构造函数
     *
     * @param handlerRegistry 处理器注册表
     * @param exceptionHandler 异常处理器
     * @param exceptionHandlingMode 异常处理模式
     */
    public Pipeline(HandlerRegistry handlerRegistry, PipelineExceptionHandler exceptionHandler,
                   ExceptionHandlingMode exceptionHandlingMode) {
        this.handlerRegistry = handlerRegistry;
        this.exceptionHandler = exceptionHandler;
        this.exceptionHandlingStrategy = ExceptionHandlingStrategyFactory.createStrategy(exceptionHandlingMode);
        this.executor = new PipelineExecutor(exceptionHandler, exceptionHandlingStrategy);
    }
    
    /**
     * 初始化管道
     * 加载SPI处理器
     *
     * @return 当前管道实例
     */
    public Pipeline init() {
        if (initialized) {
            log.warn("管道已初始化，跳过初始化");
            return this;
        }
        
        // 加载SPI处理器
        List<PipelineHandler> spiHandlers = HandlerServiceLoader.loadHandlers();
        for (PipelineHandler handler : spiHandlers) {
            handlerRegistry.register(handler);
            handlerOrder.add(handler.name());
        }
        
        initialized = true;
        log.debug("管道初始化完成，共加载{}个处理器", handlerOrder.size());
        return this;
    }
    
    /**
     * 注册处理器
     *
     * @param handler 处理器
     * @return 当前管道实例
     */
    public Pipeline register(PipelineHandler handler) {
        handlerRegistry.register(handler);
        if (!handlerOrder.contains(handler.name())) {
            handlerOrder.add(handler.name());
        }
        return this;
    }
    
    /**
     * 设置处理器顺序
     *
     * @param handlerNames 处理器名称列表
     * @return 当前管道实例
     */
    public Pipeline order(List<String> handlerNames) {
        if (handlerNames == null || handlerNames.isEmpty()) {
            return this;
        }
        
        // 验证所有处理器都已注册
        for (String name : handlerNames) {
            if (handlerRegistry.getHandler(name) == null) {
                throw new IllegalArgumentException("处理器未注册: " + name);
            }
        }
        
        // 更新处理器顺序
        handlerOrder.clear();
        handlerOrder.addAll(handlerNames);
        return this;
    }
    
    /**
     * 执行管道
     *
     * @param context 管道上下文
     */
    public void execute(PipelineContext context) {
        if (!initialized) {
            init();
        }
        
        if (context == null) {
            throw new IllegalArgumentException("管道上下文不能为空");
        }
        
        log.debug("开始执行管道，任务ID: {}", context.getTaskId());
        
        // 构建处理器列表
        List<PipelineHandler> handlers = new ArrayList<>();
        for (String name : handlerOrder) {
            PipelineHandler handler = handlerRegistry.getHandler(name);
            if (handler != null) {
                handlers.add(handler);
            } else {
                log.warn("处理器未找到: {}", name);
            }
        }
        
        // 执行处理器链
        executor.execute(handlers, context);
        
        log.debug("管道执行完成，任务ID: {}", context.getTaskId());
    }
    
    /**
     * 查找处理器在顺序列表中的索引
     *
     * @param handlerName 处理器名称
     * @return 索引，如果不存在则返回-1
     */
    private int findHandlerIndex(String handlerName) {
        for (int i = 0; i < handlerOrder.size(); i++) {
            if (handlerOrder.get(i).equals(handlerName)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 在指定处理器之前添加处理器
     *
     * @param targetHandlerName 目标处理器名称
     * @param handler 要添加的处理器
     * @return 当前管道实例
     */
    public Pipeline before(String targetHandlerName, PipelineHandler handler) {
        handlerRegistry.register(handler);
        
        int index = findHandlerIndex(targetHandlerName);
        if (index >= 0) {
            // 如果处理器已在列表中，先移除
            handlerOrder.remove(handler.name());
            // 在目标处理器之前插入
            handlerOrder.add(index, handler.name());
        } else {
            // 目标处理器不存在，添加到末尾
            if (!handlerOrder.contains(handler.name())) {
                handlerOrder.add(handler.name());
            }
        }
        
        return this;
    }
    
    /**
     * 在指定处理器之后添加处理器
     *
     * @param targetHandlerName 目标处理器名称
     * @param handler 要添加的处理器
     * @return 当前管道实例
     */
    public Pipeline after(String targetHandlerName, PipelineHandler handler) {
        handlerRegistry.register(handler);
        
        int index = findHandlerIndex(targetHandlerName);
        if (index >= 0) {
            // 如果处理器已在列表中，先移除
            handlerOrder.remove(handler.name());
            // 在目标处理器之后插入
            handlerOrder.add(index + 1, handler.name());
        } else {
            // 目标处理器不存在，添加到末尾
            if (!handlerOrder.contains(handler.name())) {
                handlerOrder.add(handler.name());
            }
        }
        
        return this;
    }
    
    /**
     * 移除处理器
     *
     * @param handlerName 处理器名称
     * @return 当前管道实例
     */
    public Pipeline remove(String handlerName) {
        handlerRegistry.removeHandler(handlerName);
        handlerOrder.remove(handlerName);
        return this;
    }
    
    /**
     * 清空管道
     *
     * @return 当前管道实例
     */
    public Pipeline clear() {
        handlerRegistry.clear();
        handlerOrder.clear();
        return this;
    }
    
    /**
     * 获取处理器注册表
     *
     * @return 处理器注册表
     */
    public HandlerRegistry getHandlerRegistry() {
        return handlerRegistry;
    }
    
    /**
     * 获取处理器顺序列表
     *
     * @return 处理器顺序列表
     */
    public List<String> getHandlerOrder() {
        return new ArrayList<>(handlerOrder);
    }
}
