package com.wangguangwu.case_push_pipeline.spring;

import com.wangguangwu.case_push_pipeline.core.CasePushPipeline;
import com.wangguangwu.case_push_pipeline.core.context.CaseContext;
import com.wangguangwu.case_push_pipeline.core.context.CasePushHandlerContext;
import com.wangguangwu.case_push_pipeline.core.exception.CasePushExceptionHandler;
import com.wangguangwu.case_push_pipeline.core.handler.CasePushHandler;
import com.wangguangwu.case_push_pipeline.spring.exception.SpringDefaultExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Spring集成的案件推送责任链
 * 自动注入处理器和异常处理器
 *
 * @author wangguangwu
 */
@Slf4j
@Component
public class SpringCasePushPipeline {

    /**
     * 处理器列表
     */
    private final List<CasePushHandlerContext> handlerContexts = new ArrayList<>();

    /**
     * 所有处理器
     */
    @Autowired
    private List<CasePushHandler> handlers;

    /**
     * 异常处理器
     */
    @Autowired(required = false)
    private CasePushExceptionHandler exceptionHandler;

    /**
     * 默认异常处理器
     */
    @Autowired
    private SpringDefaultExceptionHandler defaultExceptionHandler;

    /**
     * 内部责任链实例
     */
    private CasePushPipeline pipeline;

    /**
     * 初始化责任链
     */
    @PostConstruct
    public void init() {
        // 如果没有配置异常处理器，使用默认异常处理器
        if (exceptionHandler == null) {
            exceptionHandler = defaultExceptionHandler;
        }

        // 创建内部责任链实例
        pipeline = new CasePushPipeline(exceptionHandler);
        
        // 注册所有处理器
        pipeline.registerAll(handlers);
        
        // 初始化责任链
        pipeline.init();
        
        log.info("Spring责任链初始化完成，共有{}个处理器", handlers.size());
    }

    /**
     * 执行责任链
     *
     * @param context 案件上下文
     */
    public void execute(CaseContext context) {
        pipeline.execute(context);
    }
}
