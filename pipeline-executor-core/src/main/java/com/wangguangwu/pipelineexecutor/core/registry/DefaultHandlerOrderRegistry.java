package com.wangguangwu.pipelineexecutor.core.registry;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认处理器顺序注册表实现
 *
 * @author wangguangwu
 */
@Slf4j
public class DefaultHandlerOrderRegistry implements HandlerOrderRegistry {
    
    /**
     * 处理器顺序列表
     */
    private final List<String> handlerOrder = new ArrayList<>();
    
    @Override
    public List<String> getHandlerOrder() {
        return new ArrayList<>(handlerOrder);
    }
    
    @Override
    public void addHandler(String handlerName) {
        if (handlerName == null || handlerName.isEmpty()) {
            throw new IllegalArgumentException("处理器名称不能为空");
        }
        
        if (!handlerOrder.contains(handlerName)) {
            handlerOrder.add(handlerName);
            log.debug("添加处理器到顺序列表: {}", handlerName);
        }
    }
    
    @Override
    public void addBefore(String targetHandlerName, String handlerName) {
        if (handlerName == null || handlerName.isEmpty()) {
            throw new IllegalArgumentException("处理器名称不能为空");
        }
        
        int index = findHandlerIndex(targetHandlerName);
        if (index >= 0) {
            // 如果处理器已在列表中，先移除
            handlerOrder.remove(handlerName);
            // 在目标处理器之前插入
            handlerOrder.add(index, handlerName);
            log.debug("在处理器{}之前添加处理器: {}", targetHandlerName, handlerName);
        } else {
            // 目标处理器不存在，添加到末尾
            if (!handlerOrder.contains(handlerName)) {
                handlerOrder.add(handlerName);
                log.debug("目标处理器{}不存在，添加处理器到末尾: {}", targetHandlerName, handlerName);
            }
        }
    }
    
    @Override
    public void addAfter(String targetHandlerName, String handlerName) {
        if (handlerName == null || handlerName.isEmpty()) {
            throw new IllegalArgumentException("处理器名称不能为空");
        }
        
        int index = findHandlerIndex(targetHandlerName);
        if (index >= 0) {
            // 如果处理器已在列表中，先移除
            handlerOrder.remove(handlerName);
            // 在目标处理器之后插入
            handlerOrder.add(index + 1, handlerName);
            log.debug("在处理器{}之后添加处理器: {}", targetHandlerName, handlerName);
        } else {
            // 目标处理器不存在，添加到末尾
            if (!handlerOrder.contains(handlerName)) {
                handlerOrder.add(handlerName);
                log.debug("目标处理器{}不存在，添加处理器到末尾: {}", targetHandlerName, handlerName);
            }
        }
    }
    
    @Override
    public void setOrder(List<String> handlerNames) {
        if (handlerNames == null) {
            throw new IllegalArgumentException("处理器名称列表不能为空");
        }
        
        handlerOrder.clear();
        handlerOrder.addAll(handlerNames);
        log.debug("设置处理器顺序: {}", handlerNames);
    }
    
    @Override
    public void removeHandler(String handlerName) {
        boolean removed = handlerOrder.remove(handlerName);
        if (removed) {
            log.debug("从顺序列表中移除处理器: {}", handlerName);
        }
    }
    
    @Override
    public void clear() {
        handlerOrder.clear();
        log.debug("清空处理器顺序列表");
    }
    
    /**
     * 查找处理器在顺序列表中的索引
     *
     * @param handlerName 处理器名称
     * @return 索引，如果不存在则返回-1
     */
    private int findHandlerIndex(String handlerName) {
        for (int i = 0; i < handlerOrder.size(); i++) {
            if (handlerOrder.get(i).equals(handlerName)) {
                return i;
            }
        }
        return -1;
    }
}
