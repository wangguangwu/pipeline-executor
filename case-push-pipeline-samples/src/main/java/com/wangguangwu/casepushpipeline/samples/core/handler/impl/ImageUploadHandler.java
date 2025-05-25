package com.wangguangwu.casepushpipeline.samples.core.handler.impl;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.CasePushHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 图片上传处理器
 * 用于模拟上传案件相关图片
 *
 * @author wangguangwu
 */
@Slf4j
public class ImageUploadHandler implements CasePushHandler {

    @Override
    public String name() {
        return "图片上传处理器";
    }

    @Override
    public void handle(CaseContext context) {
        String caseId = context.getCaseId();
        log.info("图片上传处理器开始处理案件: {}", caseId);
        
        // 模拟图片上传逻辑
        context.setAttribute("imageUploaded", true);
        context.setAttribute("imageCount", 3);
        context.setAttribute("imageUploadTime", System.currentTimeMillis());
        
        log.info("图片上传处理器处理完成: {}", caseId);
    }
}
