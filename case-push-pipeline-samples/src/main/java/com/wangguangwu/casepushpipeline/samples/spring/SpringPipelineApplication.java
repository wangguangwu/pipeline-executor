package com.wangguangwu.case_push_pipeline.samples.spring;

import com.wangguangwu.case_push_pipeline.core.context.CaseContext;
import com.wangguangwu.case_push_pipeline.spring.SpringCasePushPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring版本责任链示例应用
 * 演示如何使用Spring集成的责任链
 *
 * @author wangguangwu
 */
@Slf4j
@SpringBootApplication
@ComponentScan({
        "com.wangguangwu.case_push_pipeline.spring",
        "com.wangguangwu.case_push_pipeline.samples.spring"
})
public class SpringPipelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringPipelineApplication.class, args);
    }

    /**
     * 应用启动后执行责任链示例
     */
    @Bean
    public CommandLineRunner demo(SpringCasePushPipeline pipeline) {
        return args -> {
            log.info("开始执行Spring版本责任链示例");
            
            // 创建案件上下文
            CaseContext context = new CaseContext("CASE-20250525-SPRING");
            
            // 执行责任链
            pipeline.execute(context);
            
            // 输出处理结果
            log.info("案件处理完成，结果信息：");
            log.info("案件ID: {}", context.getCaseId());
            log.info("报案处理时间: {}", context.getAttribute("reportTime"));
            log.info("影像数量: {}", context.getAttribute("imageCount"));
            log.info("理赔金额: {}", context.getAttribute("compensationAmount"));
            log.info("处理框架: {}", context.getAttribute("processedBy"));
        };
    }
}
