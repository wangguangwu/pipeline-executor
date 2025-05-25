package com.wangguangwu.case_push_pipeline.core.context;

import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

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
}
