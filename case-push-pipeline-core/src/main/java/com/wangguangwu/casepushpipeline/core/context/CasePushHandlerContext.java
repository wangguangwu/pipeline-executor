package com.wangguangwu.casepushpipeline.core.context;

import com.wangguangwu.casepushpipeline.core.handler.api.CasePushHandler;
import com.wangguangwu.casepushpipeline.core.result.HandlerResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 案件推送处理器上下文
 * 包装处理器并提供执行环境
 *
 * @author wangguangwu
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class CasePushHandlerContext {

    /**
     * 处理器
     */
    private final CasePushHandler handler;

    /**
     * 调用处理器处理案件
     * 记录执行时间并处理异常
     *
     * @param context 案件上下文
     * @throws Exception 处理过程中可能抛出的异常
     */
    public void invoke(CaseContext context) throws Exception {
        long start = System.currentTimeMillis();
        try {
            log.info("开始执行处理器: {}, 案件ID: {}", handler.name(), context.getCaseId());
            
            // 调用处理器并获取结果
            HandlerResult result = handler.handle(context);
            
            // 将结果添加到上下文中
            if (result != null) {
                context.addResult(handler.name(), result);
                log.info("处理器执行完成: {}, 案件ID: {}, 结果: {}, 耗时: {}ms", 
                        handler.name(), 
                        context.getCaseId(),
                        result.isSuccess() ? "成功" : "失败", 
                        System.currentTimeMillis() - start);
            } else {
                log.warn("处理器执行完成但返回结果为空: {}, 案件ID: {}, 耗时: {}ms", 
                        handler.name(), 
                        context.getCaseId(), 
                        System.currentTimeMillis() - start);
            }
        } catch (Exception e) {
            log.error("处理器执行异常: {}, 案件ID: {}, 耗时: {}ms", 
                    handler.name(), 
                    context.getCaseId(), 
                    System.currentTimeMillis() - start, 
                    e);
            throw e;
        }
    }
}
