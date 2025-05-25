package com.wangguangwu.casepushpipeline.core.executor;

import com.wangguangwu.casepushpipeline.core.config.PipelineConfig;
import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.context.CasePushHandlerContext;
import com.wangguangwu.casepushpipeline.core.exception.CasePushExceptionHandler;
import com.wangguangwu.casepushpipeline.core.strategy.ExceptionHandlingStrategy;
import com.wangguangwu.casepushpipeline.core.strategy.ExceptionHandlingStrategyFactory;
import com.wangguangwu.casepushpipeline.core.strategy.impl.DefaultExceptionHandlingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 默认责任链执行器实现
 * 负责执行责任链中的处理器并处理异常
 *
 * @author wangguangwu
 */
@Slf4j
public class DefaultPipelineExecutor implements PipelineExecutor {

    /**
     * 异常处理策略
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * 责任链配置
     */
    private final PipelineConfig config;

    /**
     * 执行状态
     */
    private ExecutionStatus status = ExecutionStatus.NOT_STARTED;

    /**
     * 最后一次执行的耗时（毫秒）
     */
    private long lastExecutionTime = 0;

    /**
     * 构造函数
     */
    public DefaultPipelineExecutor() {
        this((ExceptionHandlingStrategy) null, (PipelineConfig) null);
    }

    /**
     * 构造函数
     *
     * @param exceptionHandlingStrategy 异常处理策略
     */
    public DefaultPipelineExecutor(ExceptionHandlingStrategy exceptionHandlingStrategy) {
        this(exceptionHandlingStrategy, PipelineConfig.defaultConfig());
    }

    /**
     * 构造函数
     *
     * @param config 责任链配置
     */
    public DefaultPipelineExecutor(PipelineConfig config) {
        this((ExceptionHandlingStrategy) null, config);
    }

    /**
     * 构造函数
     *
     * @param exceptionHandlingStrategy 异常处理策略
     * @param config                    责任链配置
     */
    public DefaultPipelineExecutor(ExceptionHandlingStrategy exceptionHandlingStrategy, PipelineConfig config) {
        this.config = config != null ? config : PipelineConfig.defaultConfig();
        this.exceptionHandlingStrategy = exceptionHandlingStrategy != null ?
                exceptionHandlingStrategy : new DefaultExceptionHandlingStrategy();
    }

    /**
     * 构造函数
     *
     * @param exceptionHandler 异常处理器
     * @param config           责任链配置
     */
    public DefaultPipelineExecutor(CasePushExceptionHandler exceptionHandler, PipelineConfig config) {
        this.config = config != null ? config : PipelineConfig.defaultConfig();
        this.exceptionHandlingStrategy = exceptionHandler != null ?
                ExceptionHandlingStrategyFactory.createStrategy(this.config, exceptionHandler) :
                new DefaultExceptionHandlingStrategy();
    }

    @Override
    public void execute(List<CasePushHandlerContext> handlerContexts, CaseContext context) {
        if (context == null) {
            log.error("案件上下文不能为空");
            throw new IllegalArgumentException("案件上下文不能为空");
        }

        if (handlerContexts == null || handlerContexts.isEmpty()) {
            log.warn("处理器列表为空，无需执行");
            return;
        }

        String caseId = context.getCaseId();
        if (config.isEnableVerboseLogging()) {
            log.info("开始执行责任链, 案件ID: {}, 异常处理模式: {}",
                    caseId, config.getExceptionHandlingMode());
        } else {
            log.info("开始执行责任链, 案件ID: {}", caseId);
        }

        long start = System.currentTimeMillis();
        status = ExecutionStatus.RUNNING;

        try {
            executeHandlers(handlerContexts, context);
            status = ExecutionStatus.COMPLETED;

            lastExecutionTime = System.currentTimeMillis() - start;
            if (config.isEnablePerformanceMonitoring()) {
                log.info("责任链执行完成, 案件ID: {}, 耗时: {}ms",
                        caseId, lastExecutionTime);
            } else {
                log.info("责任链执行完成, 案件ID: {}", caseId);
            }
        } catch (Exception e) {
            status = ExecutionStatus.FAILED;
            lastExecutionTime = System.currentTimeMillis() - start;

            log.error("责任链执行异常, 案件ID: {}", caseId, e);
            throw e;
        }
    }

    @Override
    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    @Override
    public ExecutionStatus getExecutionStatus() {
        return status;
    }

    @Override
    public PipelineConfig getConfig() {
        return config;
    }

    /**
     * 执行所有处理器
     *
     * @param handlerContexts 处理器上下文列表
     * @param context         案件上下文
     */
    private void executeHandlers(List<CasePushHandlerContext> handlerContexts, CaseContext context) {
        // 按顺序执行处理器
        for (CasePushHandlerContext handlerContext : handlerContexts) {
            try {
                handlerContext.invoke(context);
            } catch (Exception e) {
                boolean shouldContinue = exceptionHandlingStrategy.handleException(
                        handlerContext.getHandler(), context, e);

                // 如果异常处理策略表示不继续执行，则中断责任链
                if (!shouldContinue) {
                    if (config.isEnableVerboseLogging()) {
                        log.info("处理器[{}]执行异常后中断责任链, 案件ID: {}",
                                handlerContext.getHandler().name(), context.getCaseId());
                    }
                    break;
                } else if (config.isEnableVerboseLogging()) {
                    log.info("处理器[{}]执行异常后继续执行责任链, 案件ID: {}",
                            handlerContext.getHandler().name(), context.getCaseId());
                }
            }
        }
    }
}
