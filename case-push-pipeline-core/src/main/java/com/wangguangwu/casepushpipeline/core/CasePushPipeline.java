package com.wangguangwu.casepushpipeline.core;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.context.CasePushHandlerContext;
import com.wangguangwu.casepushpipeline.core.exception.CasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.exception.DefaultCasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
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
     * 处理器上下文列表，使用LinkedList保证顺序
     */
    private final LinkedList<CasePushHandlerContext> handlerContexts = new LinkedList<>();

    /**
     * 异常处理器
     */
    private CasePushExceptionHandler exceptionHandler;

    /**
     * 构造函数
     */
    public CasePushPipeline() {
        this(null);
    }

    /**
     * 构造函数
     *
     * @param exceptionHandler 异常处理器
     */
    public CasePushPipeline(CasePushExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler != null ? exceptionHandler : new DefaultCasePushExceptionHandler();
    }

    /**
     * 注册处理器（添加到链表末尾）
     *
     * @param handler 处理器
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline register(CasePushHandler handler) {
        if (handler != null) {
            handlerContexts.addLast(new CasePushHandlerContext(handler));
            log.info("注册处理器: {}", handler.name());
        }
        return this;
    }
    
    /**
     * 在链表头部添加处理器
     *
     * @param handler 处理器
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline registerFirst(CasePushHandler handler) {
        if (handler != null) {
            handlerContexts.addFirst(new CasePushHandlerContext(handler));
            log.info("在链表头部注册处理器: {}", handler.name());
        }
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
    
    /**
     * 在指定处理器之后添加新处理器
     *
     * @param existingHandlerName 已存在的处理器名称
     * @param newHandler 新处理器
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline registerAfter(String existingHandlerName, CasePushHandler newHandler) {
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

    /**
     * 注册多个处理器
     *
     * @param handlers 处理器列表
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline registerAll(List<CasePushHandler> handlers) {
        if (handlers != null && !handlers.isEmpty()) {
            for (CasePushHandler handler : handlers) {
                register(handler);
            }
        }
        return this;
    }

    /**
     * 设置异常处理器
     *
     * @param exceptionHandler 异常处理器
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline setExceptionHandler(CasePushExceptionHandler exceptionHandler) {
        if (exceptionHandler != null) {
            this.exceptionHandler = exceptionHandler;
        }
        return this;
    }

    /**
     * 初始化责任链
     * 由于使用LinkedList，处理器的顺序已经由添加顺序确定，不需要额外排序
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline init() {
        log.info("责任链初始化完成，共有{}个处理器", handlerContexts.size());
        
        // 打印处理器顺序
        if (!handlerContexts.isEmpty()) {
            log.info(getHandlerOrderInfo());
        }
        return this;
    }

    /**
     * 获取处理器执行顺序
     *
     * @return 处理器执行顺序的字符串表示
     */
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
        
        String caseId = context.getCaseId();
        log.info("开始执行责任链, 案件ID: {}", caseId);
        long start = System.currentTimeMillis();
        
        try {
            executeHandlers(context);
            
            log.info("责任链执行完成, 案件ID: {}, 耗时: {}ms", 
                    caseId, 
                    System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("责任链执行异常, 案件ID: {}", caseId, e);
            throw e;
        }
    }
    
    /**
     * 执行所有处理器
     *
     * @param context 案件上下文
     */
    private void executeHandlers(CaseContext context) {
        // 按顺序执行处理器
        for (CasePushHandlerContext handlerContext : handlerContexts) {
            try {
                handlerContext.invoke(context);
            } catch (Exception e) {
                handleException(handlerContext.getHandler(), context, e);
                break;
            }
        }
    }
    
    /**
     * 处理异常
     *
     * @param handler 处理器
     * @param context 案件上下文
     * @param e 异常
     */
    private void handleException(CasePushHandler handler, CaseContext context, Exception e) {
        // 先让处理器自己尝试处理异常
        boolean handled = handler.handleException(context, e);
        
        // 如果处理器没有处理异常，则交给全局异常处理器
        if (!handled) {
            log.error("处理器[{}]执行异常且未自行处理, 案件ID: {}", 
                    handler.name(), 
                    context.getCaseId(), 
                    e);
            exceptionHandler.handle(context, e);
        } else {
            log.info("处理器[{}]执行异常但已自行处理, 案件ID: {}", 
                    handler.name(), 
                    context.getCaseId());
        }
    }
}
