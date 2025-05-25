package com.wangguangwu.case_push_pipeline.samples.spi.handler;

import com.wangguangwu.case_push_pipeline.core.context.CaseContext;
import com.wangguangwu.case_push_pipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * SPI机制的理算结果推送处理器
 * 负责处理案件的理算结果计算和推送
 *
 * @author wangguangwu
 */
@Slf4j
public class SpiCompensationHandler implements CasePushHandler {

    @Override
    public String name() {
        return "SPI理算结果推送处理器";
    }

    @Override
    public void handle(CaseContext context) throws Exception {
        log.info("SPI处理理算结果推送, 案件ID: {}", context.getCaseId());
        
        // 检查前置处理器是否已完成
        Boolean imageUploaded = context.getAttribute("imageUploaded", Boolean.class);
        if (imageUploaded == null || !imageUploaded) {
            log.warn("影像资料尚未上传，跳过理算结果推送, 案件ID: {}", context.getCaseId());
            return;
        }
        
        // 模拟理算结果推送逻辑
        Thread.sleep(300); // 模拟处理耗时
        
        // 将处理结果放入上下文
        context.setAttribute("compensationProcessed", true);
        context.setAttribute("compensationAmount", 10000.0);
        context.setAttribute("compensationTime", System.currentTimeMillis());
        context.setAttribute("processedBy", "SPI");
    }
    
    @Override
    public int getOrder() {
        return 3; // 理算结果处理器优先级第三
    }
}
