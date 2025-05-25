package com.wangguangwu.casepushpipeline.samples;

import com.wangguangwu.casepushpipeline.samples.core.ExceptionHandlingModeDemo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * 异常处理模式演示应用程序
 *
 * @author wangguangwu
 */
@SpringBootApplication
public class ExceptionHandlingModeDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExceptionHandlingModeDemoApplication.class, args);
    }
    
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            ExceptionHandlingModeDemo.main(args);
        };
    }
}
