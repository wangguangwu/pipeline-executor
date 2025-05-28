package com.wangguangwu.pipelineexecutor.core.pipeline;

import com.wangguangwu.pipelineexecutor.core.exception.DefaultExceptionHandler;
import com.wangguangwu.pipelineexecutor.core.exception.ExceptionHandleResult;
import com.wangguangwu.pipelineexecutor.core.exception.ExceptionHandler;
import com.wangguangwu.pipelineexecutor.core.registry.HandlerRegistry;
import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.spi.handler.Handler;
import com.wangguangwu.pipelineexecutor.spi.pipeline.Pipeline;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 默认管道实现，负责管理处理器的执行流程。
 * <p>
 * 特性：
 * 1. 线程安全：支持并发添加/删除处理器
 * 2. 动态修改：支持运行时动态调整处理器链
 * 3. 异常处理：支持自定义异常处理策略
 * 4. 流程控制：支持中断执行流程
 * </p>
 *
 * @author wangguangwu
 */
public class DefaultPipeline implements Pipeline {

    /**
     * 处理器节点，用于维护处理器实例及其名称
     */
    private record HandlerNode(String name, Handler handler) {

        private HandlerNode(String name, Handler handler) {
            this.name = Objects.requireNonNull(name, "Handler name cannot be null");
            this.handler = Objects.requireNonNull(handler, "Handler cannot be null");
        }

        boolean matches(String name) {
            return this.name.equals(name);
        }
    }

    // 使用 CopyOnWriteArrayList 保证线程安全
    private final List<HandlerNode> handlers = new CopyOnWriteArrayList<>();
    // 名称到索引的快速查找表
    private final Map<String, Integer> nameToIndex = new HashMap<>();
    // 处理器注册表，用于创建处理器实例
    private final HandlerRegistry registry = new HandlerRegistry();
    // 异常处理器
    private final ExceptionHandler exceptionHandler;

    /**
     * 使用默认异常处理器创建管道
     */
    public DefaultPipeline() {
        this(DefaultExceptionHandler.getDefault());
    }

    /**
     * 使用指定的异常处理器创建管道
     *
     * @param exceptionHandler 异常处理器
     * @throws NullPointerException 如果 exceptionHandler 为 null
     */
    public DefaultPipeline(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "ExceptionHandler cannot be null");
    }

    @Override
    public synchronized Pipeline addFirst(String name, Class<? extends Handler> handlerClass) {
        validateHandlerName(name);
        Handler handler = getOrCreateHandler(handlerClass);
        handlers.add(0, new HandlerNode(name, handler));
        rebuildIndex();
        return this;
    }

    @Override
    public synchronized Pipeline addLast(String name, Class<? extends Handler> handlerClass) {
        validateHandlerName(name);
        Handler handler = getOrCreateHandler(handlerClass);
        handlers.add(new HandlerNode(name, handler));
        rebuildIndex();
        return this;
    }

    @Override
    public synchronized Pipeline addBefore(String baseName, String name,
                                           Class<? extends Handler> handlerClass) {
        validateHandlerName(name);
        int index = getHandlerIndex(baseName);
        Handler handler = getOrCreateHandler(handlerClass);
        handlers.add(index, new HandlerNode(name, handler));
        rebuildIndex();
        return this;
    }

    @Override
    public synchronized Pipeline addAfter(String baseName, String name,
                                          Class<? extends Handler> handlerClass) {
        validateHandlerName(name);
        int index = getHandlerIndex(baseName) + 1;
        Handler handler = getOrCreateHandler(handlerClass);
        handlers.add(index, new HandlerNode(name, handler));
        rebuildIndex();
        return this;
    }

    @Override
    public synchronized Pipeline remove(String name) {
        int index = getHandlerIndex(name);
        handlers.remove(index);
        rebuildIndex();
        return this;
    }

    @Override
    public synchronized Pipeline replace(String name, Class<? extends Handler> handlerClass) {
        int index = getHandlerIndex(name);
        Handler newHandler = getOrCreateHandler(handlerClass);
        handlers.set(index, new HandlerNode(name, newHandler));
        return this;
    }

    @Override
    public void execute(PipelineContext context) {
        Objects.requireNonNull(context, "PipelineContext cannot be null");

        for (HandlerNode node : new ArrayList<>(handlers)) {
            try {
                // 执行当前处理器
                node.handler.handle(context);

                // 检查是否需要中断执行
                if (context.isBroken()) {
                    break;
                }
            } catch (Exception e) {
                // 调用异常处理器决定后续行为
                ExceptionHandleResult result = exceptionHandler.handle(e, context);
                if (result == ExceptionHandleResult.TERMINATE) {
                    break;
                }
            }
        }
    }

    // ============== 私有辅助方法 ==============

    /**
     * 获取或创建处理器实例
     */
    private <T extends Handler> T getOrCreateHandler(Class<T> handlerClass) {
        try {
            return registry.getOrCreateHandler(handlerClass);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create handler: " + handlerClass.getName(), e);
        }
    }

    /**
     * 验证处理器名称是否合法
     */
    private void validateHandlerName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Handler name cannot be null or empty");
        }
        if (nameToIndex.containsKey(name)) {
            throw new IllegalArgumentException("Handler name already exists: " + name);
        }
    }

    /**
     * 获取处理器索引
     */
    private int getHandlerIndex(String name) {
        Integer index = nameToIndex.get(name);
        if (index == null) {
            throw new IllegalArgumentException("Handler not found: " + name);
        }
        return index;
    }

    /**
     * 重建名称到索引的映射
     */
    private void rebuildIndex() {
        nameToIndex.clear();
        for (int i = 0; i < handlers.size(); i++) {
            nameToIndex.put(handlers.get(i).name, i);
        }
    }
}