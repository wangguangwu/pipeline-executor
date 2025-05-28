package com.wangguangwu.pipelineexecutor.core.registry;

import com.wangguangwu.pipelineexecutor.spi.handler.Handler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理器注册表，用于管理和缓存处理器实例
 * <p>
 * 特性：
 * 1. 线程安全：使用 ConcurrentHashMap 保证线程安全
 * 2. 单例缓存：每个处理器类只创建一个实例
 * 3. 延迟初始化：处理器在首次访问时创建
 * </p>
 *
 * @author wangguangwu
 */
public class HandlerRegistry {

    /**
     * 处理器实例缓存
     * Key: 处理器类
     * Value: 处理器实例
     */
    private final Map<Class<? extends Handler>, Handler> instanceCache = new ConcurrentHashMap<>();

    /**
     * 获取或创建处理器实例
     *
     * @param handlerClass 处理器类
     * @param <T>          处理器类型
     * @return 处理器实例
     * @throws IllegalArgumentException 如果 handlerClass 为 null
     * @throws IllegalStateException    如果处理器实例化失败
     */
    @SuppressWarnings("unchecked")
    public <T extends Handler> T getOrCreateHandler(Class<T> handlerClass) {
        Objects.requireNonNull(handlerClass, "Handler class cannot be null");

        return (T) instanceCache.computeIfAbsent(handlerClass, clazz -> {
            try {
                // 使用 getDeclaredConstructor 支持非公共构造方法
                return clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        String.format("No accessible no-arg constructor found for handler: %s",
                                clazz.getName()), e);
            } catch (InstantiationException e) {
                throw new IllegalStateException(
                        String.format("Cannot instantiate abstract class: %s",
                                clazz.getName()), e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                        String.format("Cannot access constructor of: %s",
                                clazz.getName()), e);
            } catch (Exception e) {
                throw new IllegalStateException(
                        String.format("Failed to create handler instance: %s",
                                clazz.getName()), e);
            }
        });
    }

    /**
     * 清理所有缓存的处理器实例
     */
    public void clear() {
        instanceCache.clear();
    }

    /**
     * 获取当前缓存的处理器数量
     *
     * @return 缓存的处理器数量
     */
    public int size() {
        return instanceCache.size();
    }
}