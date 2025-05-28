package com.wangguangwu.pipelineexecutor.core.exception;

import com.wangguangwu.pipelineexecutor.spi.context.PipelineContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * 默认异常处理器实现
 * <p>
 * 提供灵活的异常处理策略，支持自定义处理逻辑、异常过滤和重试机制。
 * 线程安全，支持并发调用。
 * </p>
 *
 * @author wangguangwu
 * @see ExceptionHandler
 * @see ExceptionHandleResult
 */
@Slf4j
public class DefaultExceptionHandler implements ExceptionHandler {

    // 默认配置
    private static final int DEFAULT_MAX_RETRIES = 0;
    private static final long DEFAULT_RETRY_DELAY_MS = 0L;
    private static final String LOG_FORMAT = """
            [Pipeline 异常] 执行链: {} | 当前 Handler: {} | 错误类型: {} | 错误消息: {}
            Handler 类: {}
            异常堆栈:""";

    // 默认实例
    private static final DefaultExceptionHandler DEFAULT = builder().build();

    private final Predicate<Throwable> exceptionFilter;
    private final BiFunction<Throwable, PipelineContext, ExceptionHandleResult> handler;
    private final RetryPolicy retryPolicy;

    /**
     * 私有构造函数，使用构建器模式创建实例
     *
     * @param builder 构建器实例
     */
    private DefaultExceptionHandler(Builder builder) {
        this.exceptionFilter = builder.exceptionFilter;
        this.handler = builder.handler;
        this.retryPolicy = new RetryPolicy(builder.maxRetries, builder.retryDelayMs);
    }

    /**
     * 处理异常的主要方法
     *
     * @param throwable 发生的异常
     * @param context   管道执行上下文
     * @return 异常处理结果
     * @throws NullPointerException 如果 throwable 或 context 为 null
     */
    @Override
    public ExceptionHandleResult handle(Throwable throwable, PipelineContext context) {
        Objects.requireNonNull(throwable, "Throwable cannot be null");
        Objects.requireNonNull(context, "Context cannot be null");

        try {
            if (!shouldHandle(throwable)) {
                return ExceptionHandleResult.CONTINUE;
            }

            logException(throwable, context);
            return handleWithRetry(throwable, context);
        } catch (Exception e) {
            log.error("[Pipeline 异常] 异常处理器执行失败", e);
            return ExceptionHandleResult.TERMINATE;
        }
    }

    // ========== 静态工厂方法 ==========

    public static DefaultExceptionHandler getDefault() {
        return DEFAULT;
    }

    public static DefaultExceptionHandler simple() {
        return builder().build();
    }

    public static DefaultExceptionHandler ignore(Class<? extends Throwable> exceptionType) {
        Objects.requireNonNull(exceptionType, "Exception type cannot be null");
        return builder()
                .withExceptionFilter(e -> !exceptionType.isInstance(e))
                .build();
    }

    public static DefaultExceptionHandler withRetry(int maxRetries, long delay, TimeUnit unit) {
        return builder()
                .withMaxRetries(maxRetries)
                .withRetryDelay(delay, unit)
                .build();
    }

    public static DefaultExceptionHandler of(
            Predicate<Throwable> filter,
            BiFunction<Throwable, PipelineContext, ExceptionHandleResult> handler) {
        return builder()
                .withExceptionFilter(filter)
                .withHandler(handler)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    // ========== 私有方法 ==========

    private boolean shouldHandle(Throwable throwable) {
        try {
            return exceptionFilter.test(throwable);
        } catch (Exception e) {
            log.warn("异常过滤器执行失败", e);
            return false;
        }
    }

    private ExceptionHandleResult handleWithRetry(Throwable throwable, PipelineContext context) {
        ExceptionHandleResult result = handler.apply(throwable, context);
        return (result == ExceptionHandleResult.RETRY)
                ? retryPolicy.retry(throwable, context, this::handleRetry)
                : result;
    }

    private ExceptionHandleResult handleRetry(Throwable throwable, PipelineContext context) {
        return handler.apply(throwable, context);
    }

    private ExceptionHandleResult defaultHandle(Throwable throwable, PipelineContext context) {
        context.setException(throwable);
        return ExceptionHandleResult.TERMINATE;
    }

    private void logException(Throwable throwable, PipelineContext context) {
        if (!log.isErrorEnabled()) {
            return;
        }

        log.error(LOG_FORMAT,
                context.executionId(),
                context.currentHandlerName(),
                throwable.getClass().getName(),
                throwable.getMessage(),
                context.currentHandlerName(),
                throwable
        );
    }

    // ========== 构建器 ==========

    public static final class Builder {
        private Predicate<Throwable> exceptionFilter = e -> true;
        private BiFunction<Throwable, PipelineContext, ExceptionHandleResult> handler =
                (e, ctx) -> ExceptionHandleResult.TERMINATE;
        private int maxRetries = DEFAULT_MAX_RETRIES;
        private long retryDelayMs = DEFAULT_RETRY_DELAY_MS;

        private Builder() {
            // 私有构造函数
        }

        public Builder withExceptionFilter(Predicate<Throwable> filter) {
            this.exceptionFilter = Objects.requireNonNull(filter, "Filter cannot be null");
            return this;
        }

        public Builder withHandler(BiFunction<Throwable, PipelineContext, ExceptionHandleResult> handler) {
            this.handler = Objects.requireNonNull(handler, "Handler cannot be null");
            return this;
        }

        public Builder withMaxRetries(int maxRetries) {
            if (maxRetries < 0) {
                throw new IllegalArgumentException("Max retries cannot be negative");
            }
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder withRetryDelay(long delay, TimeUnit unit) {
            Objects.requireNonNull(unit, "Time unit cannot be null");
            if (delay < 0) {
                throw new IllegalArgumentException("Delay cannot be negative");
            }
            this.retryDelayMs = unit.toMillis(delay);
            return this;
        }

        public DefaultExceptionHandler build() {
            return new DefaultExceptionHandler(this);
        }
    }

    // ========== 重试策略 ==========

    private record RetryPolicy(int maxRetries, long retryDelayMs) {

        ExceptionHandleResult retry(Throwable throwable,
                                    PipelineContext context,
                                    BiFunction<Throwable, PipelineContext, ExceptionHandleResult> handler) {
            int attempts = 0;
            Throwable lastError = throwable;

            while (attempts < maxRetries) {
                try {
                    if (retryDelayMs > 0) {
                        TimeUnit.MILLISECONDS.sleep(retryDelayMs);
                    }
                    ExceptionHandleResult result = handler.apply(throwable, context);
                    if (result != ExceptionHandleResult.RETRY) {
                        return result;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("[Pipeline 重试] 重试被中断", e);
                    return ExceptionHandleResult.TERMINATE;
                } catch (Exception e) {
                    lastError = e;
                    log.warn("[Pipeline 重试] 第 {} 次重试失败: {}", attempts + 1, e.getMessage());
                }
                attempts++;
            }

            log.error("[Pipeline 重试] 达到最大重试次数 {}，终止执行", maxRetries, lastError);
            context.setException(lastError);
            return ExceptionHandleResult.TERMINATE;
        }
    }
}
