package com.wangguangwu.casepushpipeline.core.handler.impl;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

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
        TimeUnit.MILLISECONDS.sleep(100);

        // 将处理结果放入上下文
        context.setAttribute("reportTime", System.currentTimeMillis());
        context.setAttribute("reportProcessed", true);
    }
}
