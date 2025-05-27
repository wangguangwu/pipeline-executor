package com.wangguangwu.pipelineexecutor.core.pipeline;

import com.wangguangwu.pipelineexecutor.core.exception.DefaultExceptionHandler;
import com.wangguangwu.pipelineexecutor.core.exception.ExceptionHandleResult;
import com.wangguangwu.pipelineexecutor.core.exception.ExceptionHandler;
import com.wangguangwu.pipelineexecutor.core.registry.HandlerRegistry;
import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;
import com.wangguangwu.pipelineexecutor.spi.handler.Handler;
import com.wangguangwu.pipelineexecutor.spi.pipeline.Pipeline;

import java.util.*;

/**
 * @author wangguangwu
 */
public class DefaultPipeline implements Pipeline {

    private final Map<String, Integer> nameToIndex = new HashMap<>();
    private final List<HandlerWrapper> handlers = new LinkedList<>();
    private final HandlerRegistry registry = new HandlerRegistry();
    private final ExceptionHandler exceptionHandler;

    // 构造方法支持自定义异常处理器
    public DefaultPipeline() {
        this(new DefaultExceptionHandler());
    }

    public DefaultPipeline(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Pipeline addFirst(String name, Class<? extends Handler> handlerClass) {
        if (nameToIndex.containsKey(name)) {
            throw new IllegalArgumentException("Handler name already exists: " + name);
        }
        Handler handler = registry.getOrCreateHandler(handlerClass);
        int index = 0;
        handlers.add(index, new HandlerWrapper(name, handler));
        nameToIndex.put(name, index);
        return this;
    }

    @Override
    public Pipeline addLast(String name, Class<? extends Handler> handlerClass) {
        if (nameToIndex.containsKey(name)) {
            throw new IllegalArgumentException("Handler name already exists: " + name);
        }
        Handler handler = registry.getOrCreateHandler(handlerClass);
        int index = handlers.size();
        handlers.add(index, new HandlerWrapper(name, handler));
        nameToIndex.put(name, index);
        return this;
    }

    @Override
    public Pipeline addBefore(String baseName, String name, Class<? extends Handler> handlerClass) {
        if (nameToIndex.containsKey(name)) {
            throw new IllegalArgumentException("Handler name already exists: " + name);
        }
        if (!nameToIndex.containsKey(baseName)) {
            throw new IllegalArgumentException("Base handler not found: " + baseName);
        }
        Handler handler = registry.getOrCreateHandler(handlerClass);
        int index = nameToIndex.get(baseName);
        handlers.add(index, new HandlerWrapper(name, handler));
        nameToIndex.put(name, index);
        // 更新后续 Handler 的索引
        for (int i = index + 1; i < handlers.size(); i++) {
            String existingName = handlers.get(i).name;
            nameToIndex.put(existingName, i);
        }
        return this;
    }

    @Override
    public Pipeline addAfter(String baseName, String name, Class<? extends Handler> handlerClass) {
        if (nameToIndex.containsKey(name)) {
            throw new IllegalArgumentException("Handler name already exists: " + name);
        }
        if (!nameToIndex.containsKey(baseName)) {
            throw new IllegalArgumentException("Base handler not found: " + baseName);
        }
        Handler handler = registry.getOrCreateHandler(handlerClass);
        int index = nameToIndex.get(baseName) + 1;
        handlers.add(index, new HandlerWrapper(name, handler));
        nameToIndex.put(name, index);
        // 更新后续 Handler 的索引
        for (int i = index + 1; i < handlers.size(); i++) {
            String existingName = handlers.get(i).name;
            nameToIndex.put(existingName, i);
        }
        return this;
    }

    @Override
    public Pipeline remove(String name) {
        if (!nameToIndex.containsKey(name)) {
            throw new IllegalArgumentException("Handler not found: " + name);
        }
        int index = nameToIndex.get(name);
        handlers.remove(index);
        nameToIndex.remove(name);
        // 更新后续 Handler 的索引
        for (int i = index; i < handlers.size(); i++) {
            String existingName = handlers.get(i).name;
            nameToIndex.put(existingName, i);
        }
        return this;
    }

    @Override
    public Pipeline replace(String name, Class<? extends Handler> handlerClass) {
        if (!nameToIndex.containsKey(name)) {
            throw new IllegalArgumentException("Handler not found: " + name);
        }
        Handler oldHandler = handlers.get(nameToIndex.get(name)).handler;
        Handler newHandler = registry.getOrCreateHandler(handlerClass);
        int index = nameToIndex.get(name);
        handlers.set(index, new HandlerWrapper(name, newHandler));
        return this;
    }

    @Override
    public void execute(PipelineContext context) {
        for (HandlerWrapper wrapper : handlers) {
            try {
                wrapper.handler.handle(context);
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

    private static class HandlerWrapper {
        final String name;
        final Handler handler;

        HandlerWrapper(String name, Handler handler) {
            this.name = name;
            this.handler = handler;
        }
    }
}