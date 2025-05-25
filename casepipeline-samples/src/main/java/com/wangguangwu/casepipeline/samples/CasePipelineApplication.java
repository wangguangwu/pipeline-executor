package com.wangguangwu.casepipeline.samples;

import com.wangguangwu.casepipeline.samples.core.CorePipelineDemo;
import com.wangguangwu.casepipeline.samples.spi.SpiPipelineDemo;
import lombok.extern.slf4j.Slf4j;

/**
 * 案件处理责任链主应用程序
 * 用于演示不同类型的责任链实现
 *
 * @author wangguangwu
 */
@Slf4j
public class CasePipelineApplication {

    public static void main(String[] args) {
        log.info("=== 案件处理责任链示例应用启动 ===");
        
        // 演示核心版本责任链
        log.info("\n\n=== 开始演示核心版本责任链 ===");
        CorePipelineDemo.main(args);
        
        // 演示SPI版本责任链
        log.info("\n\n=== 开始演示SPI版本责任链 ===");
        SpiPipelineDemo.main(args);
        
        // 提示如何运行Spring版本责任链
        log.info("\n\n=== Spring版本责任链需要单独运行 ===");
        log.info("请运行 SpringPipelineApplication 类来演示Spring版本责任链");
        
        log.info("\n=== 案件处理责任链示例应用结束 ===");
    }
}
