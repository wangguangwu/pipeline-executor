package com.wangguangwu.casepushpipeline.core;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.context.CasePushHandlerContext;
import com.wangguangwu.casepushpipeline.core.exception.CasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.exception.DefaultCasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
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
     * 处理器上下文列表
     */
    private final List<CasePushHandlerContext> handlerContexts = new ArrayList<>();

    /**
     * 异常处理器
     */
    private CasePushExceptionHandler exceptionHandler;

    /**
     * 构造函数
     */
    public CasePushPipeline() {
        this.exceptionHandler = new DefaultCasePushExceptionHandler();
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
     * 注册处理器
     *
     * @param handler 处理器
     * @return 当前责任链实例，支持链式调用
     */
    public CasePushPipeline register(CasePushHandler handler) {
        if (handler != null) {
            handlerContexts.add(new CasePushHandlerContext(handler));
            log.info("注册处理器: {}, 顺序: {}", handler.name(), handler.getOrder());
        }
        return this;
    }

    /**
     * 注册多个处理器
     *
     * @param handlers 处理器列表
     */
    public void registerAll(List<CasePushHandler> handlers) {
        if (handlers != null && !handlers.isEmpty()) {
            for (CasePushHandler handler : handlers) {
                register(handler);
            }
        }
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
     * 按照处理器的顺序排序
     */
    public void init() {
        handlerContexts.sort(Comparator.comparingInt(ctx -> ctx.getHandler().getOrder()));
        log.info("责任链初始化完成，共有{}个处理器", handlerContexts.size());
    }

    /**
     * 执行责任链
     *
     * @param context 案件上下文
     */
    public void execute(CaseContext context) {
        log.info("开始执行责任链, 案件ID: {}", context.getCaseId());
        long start = System.currentTimeMillis();
        
        try {
            // 按顺序执行处理器
            for (CasePushHandlerContext handlerContext : handlerContexts) {
                try {
                    handlerContext.invoke(context);
                } catch (Exception e) {
                    // 处理异常
                    log.error("处理器执行异常: {}, 案件ID: {}", 
                            handlerContext.getHandler().name(), 
                            context.getCaseId(), 
                            e);
                    exceptionHandler.handle(context, e);
                    break;
                }
            }
            
            log.info("责任链执行完成, 案件ID: {}, 耗时: {}ms", 
                    context.getCaseId(), 
                    System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("责任链执行异常, 案件ID: {}", context.getCaseId(), e);
            throw new RuntimeException("责任链执行异常", e);
        }
    }
}
