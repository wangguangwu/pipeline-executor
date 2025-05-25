package com.wangguangwu.casepushpipeline.samples.spi;

import com.wangguangwu.casepushpipeline.spi.config.HandlerOrderConfig;

import java.util.Arrays;
import java.util.List;

/**
 * 默认处理器顺序配置
 * 通过SPI机制加载，用于配置处理器的执行顺序
 *
 * @author wangguangwu
 */
public class DefaultHandlerOrderConfig implements HandlerOrderConfig {
    
    @Override
    public List<String> getHandlerOrder() {
        // 指定处理器的执行顺序
        return Arrays.asList(
                "报案处理器",
                "影像上传处理器",
                "错误处理器",
                "理赔处理器"
        );
    }
    
    @Override
    public int getPriority() {
        // 默认优先级为0
        return 0;
    }
}
