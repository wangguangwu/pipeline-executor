package com.wangguangwu.casepushpipeline.core.handler.impl;

import com.wangguangwu.casepushpipeline.core.context.CaseContext;
import com.wangguangwu.casepushpipeline.core.handler.base.AbstractCasePushHandler;
import com.wangguangwu.casepushpipeline.core.result.HandlerResult;
import com.wangguangwu.casepushpipeline.core.result.ValidateResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 案件验证处理器
 * 负责验证案件数据的完整性和合法性
 *
 * @author wangguangwu
 */
@Slf4j
public class CaseValidateHandler extends AbstractCasePushHandler {

    @Override
    public String name() {
        return "案件验证处理器";
    }

    @Override
    protected HandlerResult doHandle(CaseContext context) throws Exception {
        log.info("验证案件数据, 案件ID: {}", context.getCaseId());
        
        // 模拟验证逻辑
        List<String> validationErrors = new ArrayList<>();
        
        // 检查案件ID格式
        if (!isValidCaseId(context.getCaseId())) {
            validationErrors.add("案件ID格式不正确");
        }
        
        // 检查必要属性
        if (context.getAttribute("reportTime") == null) {
            validationErrors.add("报案时间不能为空");
        }
        
        if (context.getAttribute("reporterName") == null) {
            validationErrors.add("报案人姓名不能为空");
        }
        
        if (context.getAttribute("reporterPhone") == null) {
            validationErrors.add("报案人电话不能为空");
        }
        
        // 如果有验证错误，返回验证失败结果
        if (!validationErrors.isEmpty()) {
            log.warn("案件验证失败, 案件ID: {}, 错误数量: {}", context.getCaseId(), validationErrors.size());
            return ValidateResult.validFailure(name(), validationErrors);
        }
        
        // 验证通过，设置标记并返回成功结果
        context.setAttribute("validated", true);
        context.setAttribute("validationTime", System.currentTimeMillis());
        
        log.info("案件验证通过, 案件ID: {}", context.getCaseId());
        return ValidateResult.validSuccess(name());
    }
    
    /**
     * 验证案件ID格式
     *
     * @param caseId 案件ID
     * @return 是否有效
     */
    private boolean isValidCaseId(String caseId) {
        // 简单示例：案件ID必须以字母开头，后跟数字，总长度至少6位
        return caseId != null && caseId.matches("[A-Za-z][A-Za-z0-9]{5,}");
    }
    
    @Override
    protected HandlerResult handleExceptionWithResult(CaseContext context, Exception e) {
        log.error("案件验证过程中发生异常, 案件ID: {}", context.getCaseId(), e);
        List<String> errors = new ArrayList<>();
        errors.add("验证过程异常: " + e.getMessage());
        return ValidateResult.validFailure(name(), errors);
    }
}
