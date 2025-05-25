package com.wangguangwu.casepushpipeline.samples.spi.handler;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * SPI机制的影像资料上传处理器
 * 负责处理案件的影像资料上传
 *
 * @author wangguangwu
 */
@Slf4j
public class SpiImageUploadHandler implements CasePushHandler {

    @Override
    public String name() {
        return "SPI影像资料上传处理器";
    }

    @Override
    public void handle(CaseContext context) throws Exception {
        log.info("SPI处理影像资料推送, 案件ID: {}", context.getCaseId());
        
        // 检查前置处理器是否已完成
        Boolean reportProcessed = context.getAttribute("reportProcessed", Boolean.class);
        if (reportProcessed == null || !reportProcessed) {
            log.warn("报案信息尚未处理，跳过影像资料上传, 案件ID: {}", context.getCaseId());
            return;
        }
        
        // 模拟影像上传逻辑
        Thread.sleep(200); // 模拟处理耗时
        
        // 将处理结果放入上下文
        context.setAttribute("imageUploaded", true);
        context.setAttribute("imageCount", 15);
        context.setAttribute("imageUploadTime", System.currentTimeMillis());
        context.setAttribute("processedBy", "SPI");
    }
    
    @Override
    public int getOrder() {
        return 2; // 影像上传处理器优先级第二
    }
}
