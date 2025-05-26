package com.wangguangwu.pipelineexecutor.core.context;

import com.wangguangwu.pipelineexecutor.core.result.HandlerResult;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管道上下文
 * 存储任务ID和处理器结果
 *
 * @author wangguangwu
 */
@Getter
@ToString
public class PipelineContext {

    /**
     * 任务ID
     */
    private final String taskId;

    /**
     * 处理器结果
     * -- GETTER --
     * 获取所有处理器结果
     */
    @Getter
    private final Map<String, HandlerResult> results = new ConcurrentHashMap<>();

    /**
     * 上下文属性
     */
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param taskId 任务ID
     */
    public PipelineContext(String taskId) {
        if (taskId == null || taskId.isEmpty()) {
            throw new IllegalArgumentException("任务ID不能为空");
        }
        this.taskId = taskId;
    }

    /**
     * 添加处理器结果
     *
     * @param result 处理结果
     */
    public void addResult(HandlerResult result) {
        if (result != null) {
            results.put(result.getHandlerName(), result);
        }
    }

    /**
     * 添加处理器结果
     *
     * @param handlerName 处理器名称
     * @param result      处理结果
     */
    public void addResult(String handlerName, HandlerResult result) {
        results.put(handlerName, result);
    }

    /**
     * 获取处理器结果
     *
     * @param handlerName 处理器名称
     * @return 处理结果
     */
    public HandlerResult getResult(String handlerName) {
        return results.get(handlerName);
    }

    /**
     * 获取处理器结果并转换为指定类型
     *
     * @param handlerName 处理器名称
     * @param clazz       结果类型
     * @param <T>         结果类型
     * @return 处理结果
     */
    @SuppressWarnings("unchecked")
    public <T extends HandlerResult> T getResult(String handlerName, Class<T> clazz) {
        HandlerResult result = results.get(handlerName);
        if (result == null) {
            return null;
        }
        if (clazz.isInstance(result)) {
            return (T) result;
        }
        throw new ClassCastException("结果[" + handlerName + "]不能转换为" + clazz.getName());
    }

    /**
     * 判断处理器是否执行成功
     *
     * @param handlerName 处理器名称
     * @return 是否成功
     */
    public boolean isHandlerSuccess(String handlerName) {
        HandlerResult result = results.get(handlerName);
        return result != null && result.isSuccess();
    }

    /**
     * 获取所有结果名称
     *
     * @return 结果名称集合
     */
    public Set<String> getResultNames() {
        return results.keySet();
    }

    /**
     * 设置属性
     *
     * @param name  属性名
     * @param value 属性值
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * 获取属性
     *
     * @param name 属性名
     * @return 属性值
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * 获取属性并转换为指定类型
     *
     * @param name  属性名
     * @param clazz 属性类型
     * @param <T>   属性类型
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String name, Class<T> clazz) {
        Object value = attributes.get(name);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("属性[" + name + "]不能转换为" + clazz.getName());
    }

    /**
     * 判断是否包含属性
     *
     * @param name 属性名
     * @return 是否包含
     */
    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    /**
     * 移除属性
     *
     * @param name 属性名
     * @return 被移除的属性值
     */
    public Object removeAttribute(String name) {
        return attributes.remove(name);
    }
}
