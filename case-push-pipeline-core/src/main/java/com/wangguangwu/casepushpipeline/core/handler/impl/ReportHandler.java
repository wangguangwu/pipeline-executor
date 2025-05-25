package com.wangguangwu.case_push_pipeline.core.handler.impl;

import com.wangguangwu.case_push_pipeline.core.context.CaseContext;
import com.wangguangwu.case_push_pipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 报案处理器
 * 负责处理案件的报案信息
 *
 * @author wangguangwu
 */
@Slf4j
public class ReportHandler implements CasePushHandler {

    @Override
    public String name() {
        return "报案处理器";
    }

    @Override
    public void handle(CaseContext context) throws Exception {
        log.info("处理报案信息, 案件ID: {}", context.getCaseId());
        
        // 模拟报案处理逻辑
        Thread.sleep(100); // 模拟处理耗时
        
        // 将处理结果放入上下文
        context.setAttribute("reportTime", System.currentTimeMillis());
        context.setAttribute("reportProcessed", true);
    }
    
    @Override
    public int getOrder() {
        return 1; // 报案处理器优先级最高
    }
}
