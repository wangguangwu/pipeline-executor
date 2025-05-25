package com.wangguangwu.casepushpipeline.core.registry;

import com.wangguangwu.casepushpipeline.core.context.CasePushHandlerContext;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 默认处理器注册器实现
 * 使用LinkedList管理处理器顺序
 *
 * @author wangguangwu
 */
@Slf4j
public class DefaultHandlerRegistry implements HandlerRegistry {

    /**
     * 处理器上下文列表，使用LinkedList保证顺序
     */
    private final LinkedList<CasePushHandlerContext> handlerContexts = new LinkedList<>();

    @Override
    public HandlerRegistry register(CasePushHandler handler) {
        if (handler != null) {
            handlerContexts.addLast(new CasePushHandlerContext(handler));
            log.info("注册处理器: {}", handler.name());
        }
        return this;
    }

    @Override
    public HandlerRegistry registerFirst(CasePushHandler handler) {
        if (handler != null) {
            handlerContexts.addFirst(new CasePushHandlerContext(handler));
            log.info("在链表头部注册处理器: {}", handler.name());
        }
        return this;
    }

    @Override
    public HandlerRegistry registerBefore(String existingHandlerName, CasePushHandler newHandler) {
        if (newHandler == null || existingHandlerName == null) {
            return this;
        }
        
        int index = findHandlerIndex(existingHandlerName);
        if (index >= 0) {
            handlerContexts.add(index, new CasePushHandlerContext(newHandler));
            log.info("在处理器[{}]之前注册处理器: {}", existingHandlerName, newHandler.name());
        } else {
            // 如果没找到指定的处理器，则添加到末尾
            log.warn("未找到处理器[{}]，将新处理器[{}]添加到末尾", existingHandlerName, newHandler.name());
            register(newHandler);
        }
        return this;
    }

    @Override
    public HandlerRegistry registerAfter(String existingHandlerName, CasePushHandler newHandler) {
        if (newHandler == null || existingHandlerName == null) {
            return this;
        }
        
        int index = findHandlerIndex(existingHandlerName);
        if (index >= 0) {
            handlerContexts.add(index + 1, new CasePushHandlerContext(newHandler));
            log.info("在处理器[{}]之后注册处理器: {}", existingHandlerName, newHandler.name());
        } else {
            // 如果没找到指定的处理器，则添加到末尾
            log.warn("未找到处理器[{}]，将新处理器[{}]添加到末尾", existingHandlerName, newHandler.name());
            register(newHandler);
        }
        return this;
    }

    @Override
    public HandlerRegistry registerAll(List<CasePushHandler> handlers) {
        if (handlers != null && !handlers.isEmpty()) {
            for (CasePushHandler handler : handlers) {
                register(handler);
            }
        }
        return this;
    }

    @Override
    public List<CasePushHandlerContext> getHandlerContexts() {
        return Collections.unmodifiableList(handlerContexts);
    }

    @Override
    public String getHandlerOrderInfo() {
        if (handlerContexts.isEmpty()) {
            return "暂无处理器";
        }
        
        StringBuilder sb = new StringBuilder("处理器执行顺序: ");
        boolean first = true;
        for (CasePushHandlerContext handlerContext : handlerContexts) {
            if (!first) {
                sb.append(" -> ");
            }
            sb.append(handlerContext.getHandler().name());
            first = false;
        }
        return sb.toString();
    }

    @Override
    public int size() {
        return handlerContexts.size();
    }

    @Override
    public HandlerRegistry init() {
        log.info("责任链初始化完成，共有{}个处理器", handlerContexts.size());
        
        // 打印处理器顺序
        if (!handlerContexts.isEmpty()) {
            log.info(getHandlerOrderInfo());
        }
        return this;
    }
    
    /**
     * 查找处理器在链表中的索引
     *
     * @param handlerName 处理器名称
     * @return 索引，如果未找到则返回-1
     */
    private int findHandlerIndex(String handlerName) {
        for (int i = 0; i < handlerContexts.size(); i++) {
            if (handlerName.equals(handlerContexts.get(i).getHandler().name())) {
                return i;
            }
        }
        return -1;
    }
}
