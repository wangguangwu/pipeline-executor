package com.wangguangwu.casepushpipeline.core.example;

import com.wangguangwu.casepushpipeline.core.CasePushPipeline;
import com.wangguangwu.casepushpipeline.core.config.PipelineConfig;
import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.api.CasePushHandler;
import com.wangguangwu.casepushpipeline.core.handler.impl.CaseValidateHandler;
import com.wangguangwu.casepushpipeline.core.handler.impl.ImageUploadHandler;
import com.wangguangwu.casepushpipeline.core.result.ImageUploadResult;
import com.wangguangwu.casepushpipeline.core.result.ValidateResult;

/**
 * 结果实体类使用示例
 *
 * @author wangguangwu
 */
public class ResultEntityExample {

    public static void main(String[] args) {
        // 创建责任链配置
        PipelineConfig config = PipelineConfig.builder()
                .exceptionHandlingMode(PipelineConfig.ExceptionHandlingMode.CONTINUE_PIPELINE)
                .enablePerformanceMonitoring(true)
                .enableVerboseLogging(true)
                .build();
        
        // 创建责任链
        CasePushPipeline pipeline = new CasePushPipeline(config);
        
        // 注册处理器
        pipeline.addHandler(new CaseValidateHandler());
        pipeline.addHandler(new ImageUploadHandler());
        
        // 初始化责任链
        pipeline.init();
        
        // 创建案件上下文
        CaseContext context = new CaseContext("A123456");
        
        // 设置一些属性用于测试
        context.setAttribute("reportTime", System.currentTimeMillis());
        context.setAttribute("reporterName", "张三");
        context.setAttribute("reporterPhone", "13800138000");
        context.setAttribute("reportProcessed", true);
        
        // 执行责任链
        pipeline.execute(context);
        
        // 获取并处理结果
        processResults(context);
    }
    
    /**
     * 处理责任链执行结果
     *
     * @param context 案件上下文
     */
    private static void processResults(CaseContext context) {
        System.out.println("\n===== 责任链执行结果 =====");
        System.out.println("案件ID: " + context.getCaseId());
        
        // 获取验证处理器结果
        ValidateResult validateResult = context.getResult("案件验证处理器", ValidateResult.class);
        if (validateResult != null) {
            System.out.println("\n--- 验证结果 ---");
            System.out.println("处理器: " + validateResult.getHandlerName());
            System.out.println("是否成功: " + validateResult.isSuccess());
            System.out.println("是否有效: " + validateResult.isValid());
            
            if (!validateResult.isValid() && validateResult.getValidationErrors() != null) {
                System.out.println("验证错误: ");
                validateResult.getValidationErrors().forEach(error -> System.out.println("  - " + error));
            }
        }
        
        // 获取图片上传处理器结果
        ImageUploadResult imageResult = context.getResult("影像上传处理器", ImageUploadResult.class);
        if (imageResult != null) {
            System.out.println("\n--- 图片上传结果 ---");
            System.out.println("处理器: " + imageResult.getHandlerName());
            System.out.println("是否成功: " + imageResult.isSuccess());
            
            if (imageResult.isSuccess()) {
                System.out.println("上传图片数量: " + 
                        (imageResult.getUploadedImageUrls() != null ? imageResult.getUploadedImageUrls().size() : 0));
                
                if (imageResult.getUploadedImageUrls() != null && !imageResult.getUploadedImageUrls().isEmpty()) {
                    System.out.println("上传图片URL: ");
                    imageResult.getUploadedImageUrls().forEach(url -> System.out.println("  - " + url));
                }
                
                if (imageResult.getFailedImages() != null && !imageResult.getFailedImages().isEmpty()) {
                    System.out.println("上传失败图片: ");
                    imageResult.getFailedImages().forEach(img -> System.out.println("  - " + img));
                }
            } else {
                System.out.println("错误码: " + imageResult.getErrorCode());
                System.out.println("错误信息: " + imageResult.getErrorMessage());
            }
        }
        
        // 检查所有处理器是否都成功
        boolean allSuccess = true;
        for (String handlerName : context.getResults().keySet()) {
            if (!context.isHandlerSuccess(handlerName)) {
                allSuccess = false;
                System.out.println("\n处理器 [" + handlerName + "] 执行失败");
            }
        }
        
        System.out.println("\n责任链执行" + (allSuccess ? "全部成功" : "部分失败"));
    }
}
