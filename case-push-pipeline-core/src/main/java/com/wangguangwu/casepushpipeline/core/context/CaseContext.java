package com.wangguangwu.casepushpipeline.core.context;

import com.wangguangwu.casepushpipeline.core.result.HandlerResult;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 案件上下文
 * 存储案件ID和处理过程中的属性
 *
 * @author wangguangwu
 */
@Getter
@ToString
public class CaseContext {

    /**
     * 案件ID
     */
    private final String caseId;
    
    /**
     * 案件属性
     */
    private final Map<String, Object> attributes = new HashMap<>();
    
    /**
     * 处理器结果
     */
    private final Map<String, HandlerResult> results = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param caseId 案件ID
     */
    public CaseContext(String caseId) {
        if (caseId == null || caseId.isEmpty()) {
            throw new IllegalArgumentException("案件ID不能为空");
        }
        this.caseId = caseId;
    }

    /**
     * 设置属性
     *
     * @param key   属性键
     * @param value 属性值
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * 获取属性
     *
     * @param key 属性键
     * @return 属性值
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * 获取属性并转换为指定类型
     *
     * @param key   属性键
     * @param clazz 属性值类型
     * @param <T>   属性值类型
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, Class<T> clazz) {
        Object value = attributes.get(key);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        throw new ClassCastException("属性[" + key + "]不能转换为" + clazz.getName());
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
}
