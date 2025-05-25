package com.wangguangwu.casepushpipeline.spi.config;

import java.util.List;

/**
 * 处理器顺序配置接口
 * 通过SPI机制加载，用于配置处理器的执行顺序
 *
 * @author wangguangwu
 */
public interface HandlerOrderConfig {
    
    /**
     * 获取处理器顺序配置
     * 返回的列表中的处理器名称将按照顺序执行
     * 不在列表中的处理器将按照注册顺序添加到列表末尾
     *
     * @return 处理器名称列表，按照执行顺序排列
     */
    List<String> getHandlerOrder();
    
    /**
     * 获取配置优先级
     * 数值越小优先级越高，当存在多个配置时，优先级高的配置生效
     *
     * @return 配置优先级
     */
    default int getPriority() {
        return 0;
    }
}
