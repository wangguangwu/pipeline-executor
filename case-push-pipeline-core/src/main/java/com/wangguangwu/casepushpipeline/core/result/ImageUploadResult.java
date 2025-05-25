package com.wangguangwu.casepushpipeline.core.result;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 图片上传处理器结果
 *
 * @author wangguangwu
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ImageUploadResult extends HandlerResult {
    
    /**
     * 上传的图片URL列表
     */
    private List<String> uploadedImageUrls;
    
    /**
     * 上传失败的图片名称列表
     */
    private List<String> failedImages;
    
    /**
     * 创建上传成功结果
     *
     * @param handlerName      处理器名称
     * @param uploadedImageUrls 上传成功的图片URL列表
     * @return 上传结果
     */
    public static ImageUploadResult uploadSuccess(String handlerName, List<String> uploadedImageUrls) {
        return ImageUploadResult.builder()
                .handlerName(handlerName)
                .success(true)
                .uploadedImageUrls(uploadedImageUrls)
                .build();
    }
    
    /**
     * 创建部分上传成功结果
     *
     * @param handlerName      处理器名称
     * @param uploadedImageUrls 上传成功的图片URL列表
     * @param failedImages     上传失败的图片名称列表
     * @return 上传结果
     */
    public static ImageUploadResult partialSuccess(String handlerName, List<String> uploadedImageUrls, List<String> failedImages) {
        return ImageUploadResult.builder()
                .handlerName(handlerName)
                .success(true)
                .uploadedImageUrls(uploadedImageUrls)
                .failedImages(failedImages)
                .build();
    }
    
    /**
     * 创建上传失败结果
     *
     * @param handlerName   处理器名称
     * @param errorCode     错误码
     * @param errorMessage  错误信息
     * @param failedImages  上传失败的图片名称列表
     * @return 上传结果
     */
    public static ImageUploadResult uploadFailure(String handlerName, String errorCode, String errorMessage, List<String> failedImages) {
        return ImageUploadResult.builder()
                .handlerName(handlerName)
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .failedImages(failedImages)
                .build();
    }
}
