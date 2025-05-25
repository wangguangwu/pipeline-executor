package com.wangguangwu.casepushpipeline.core.handler.impl;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.base.AbstractCasePushHandler;
import com.wangguangwu.casepushpipeline.core.result.HandlerResult;
import com.wangguangwu.casepushpipeline.core.result.ImageUploadResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 影像资料上传处理器
 * 负责处理案件的影像资料上传
 *
 * @author wangguangwu
 */
@Slf4j
public class ImageUploadHandler extends AbstractCasePushHandler {

    @Override
    public String name() {
        return "影像上传处理器";
    }

    @Override
    protected HandlerResult doHandle(CaseContext context) throws Exception {
        log.info("处理影像资料推送, 案件ID: {}", context.getCaseId());
        
        // 检查前置处理器是否已完成
        Boolean reportProcessed = context.getAttribute("reportProcessed", Boolean.class);
        if (reportProcessed == null || !reportProcessed) {
            log.warn("报案信息尚未处理，跳过影像资料上传, 案件ID: {}", context.getCaseId());
            return ImageUploadResult.uploadFailure(
                    name(),
                    "PREREQUISITE_NOT_MET",
                    "报案信息尚未处理",
                    new ArrayList<>()
            );
        }
        
        // 模拟影像上传逻辑
        TimeUnit.MILLISECONDS.sleep(200);
        
        // 模拟上传成功的图片URL列表
        List<String> uploadedImageUrls = Arrays.asList(
                "https://example.com/images/case_" + context.getCaseId() + "_1.jpg",
                "https://example.com/images/case_" + context.getCaseId() + "_2.jpg",
                "https://example.com/images/case_" + context.getCaseId() + "_3.jpg",
                "https://example.com/images/case_" + context.getCaseId() + "_4.jpg",
                "https://example.com/images/case_" + context.getCaseId() + "_5.jpg"
        );
        
        // 模拟部分上传失败的情况
        List<String> failedImages = new ArrayList<>();
        if (context.getCaseId().endsWith("9")) {
            failedImages.add("image6.jpg");
            failedImages.add("image7.jpg");
            return ImageUploadResult.partialSuccess(name(), uploadedImageUrls, failedImages);
        }

        // 将处理结果放入上下文（兼容旧版本）
        context.setAttribute("imageUploaded", true);
        context.setAttribute("imageCount", uploadedImageUrls.size());
        context.setAttribute("imageUploadTime", System.currentTimeMillis());
        
        // 返回上传成功结果
        return ImageUploadResult.uploadSuccess(name(), uploadedImageUrls);
    }
    
    @Override
    protected HandlerResult handleExceptionWithResult(CaseContext context, Exception e) {
        // 处理异常并返回结果
        log.error("影像上传异常，案件ID: {}", context.getCaseId(), e);
        List<String> failedImages = Arrays.asList("所有图片");
        return ImageUploadResult.uploadFailure(
                name(),
                "UPLOAD_EXCEPTION",
                "影像上传过程中发生异常: " + e.getMessage(),
                failedImages
        );
    }
}
