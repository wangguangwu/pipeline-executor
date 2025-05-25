package com.wangguangwu.casepushpipeline.spring.config;

import com.wangguangwu.casepushpipeline.spring.exception.SpringDefaultExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 案件处理责任链自动配置类
 * 提供默认组件的自动配置
 *
 * @author wangguangwu
 */
@Configuration
public class CasePipelineAutoConfiguration {

    /**
     * 默认异常处理器
     * 当没有自定义异常处理器时使用此默认实现
     *
     * @return 默认异常处理器
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringDefaultExceptionHandler defaultCasePushExceptionHandler() {
        return new SpringDefaultExceptionHandler();
    }
}
