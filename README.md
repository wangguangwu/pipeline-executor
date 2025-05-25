# 案件处理责任链框架

## 项目概述

本项目实现了一个灵活的案件处理责任链框架，支持多种集成方式，包括：

1. **核心模块（Core）**：基础的责任链实现，纯Java方式
2. **Spring集成模块**：与Spring框架集成，支持自动注入处理器
3. **SPI扩展模块**：基于Java SPI机制，支持动态加载处理器

## 项目结构

项目采用Maven多模块结构：

```
case-push-pipeline/
├── case-push-pipeline-core/        # 核心模块
├── case-push-pipeline-spring/      # Spring集成模块
├── case-push-pipeline-spi/         # SPI扩展模块
└── case-push-pipeline-samples/     # 示例模块
```

## 核心模块

核心模块提供了责任链的基本实现，主要包括：

- `CaseContext`：案件上下文，存储案件ID和处理过程中的属性
- `CasePushHandler`：处理器接口，定义处理器的基本行为
- `CasePushHandlerContext`：处理器上下文，包装处理器并提供执行环境
- `CasePushExceptionHandler`：异常处理器接口，定义异常处理的基本行为
- `DefaultCasePushExceptionHandler`：默认异常处理器实现
- `CasePushPipeline`：责任链核心实现，管理处理器的注册和执行

## Spring集成模块

Spring集成模块提供了与Spring框架的集成，主要包括：

- `SpringCasePushPipeline`：Spring集成的责任链实现，支持自动注入处理器
- `SpringDefaultExceptionHandler`：Spring集成的默认异常处理器
- `CasePipelineAutoConfiguration`：Spring自动配置类，提供默认组件的自动配置

## SPI扩展模块

SPI扩展模块提供了基于Java SPI机制的扩展能力，主要包括：

- `SpiCasePushPipeline`：基于SPI机制的责任链实现，支持动态加载处理器

## 示例模块

示例模块提供了各种集成方式的示例代码：

- `CorePipelineDemo`：核心版本责任链示例
- `SpringPipelineApplication`：Spring版本责任链示例
- `SpiPipelineDemo`：SPI版本责任链示例

## 使用方法

### 核心版本

```java
// 创建责任链实例
CasePushPipeline pipeline = new CasePushPipeline(new CustomAlertExceptionHandler());

// 注册处理器
pipeline.register(new ReportHandler())
        .register(new ImageUploadHandler())
        .register(new CompensationHandler());

// 初始化责任链
pipeline.init();

// 创建案件上下文
CaseContext context = new CaseContext("CASE-001");

// 执行责任链
pipeline.execute(context);
```

### Spring版本

1. 添加依赖

```xml
<dependency>
    <groupId>com.wangguangwu</groupId>
    <artifactId>case-push-pipeline-spring</artifactId>
</dependency>
```

2. 创建处理器并添加`@Component`注解

```java
@Component
public class SpringReportHandler implements CasePushHandler {
    // 实现处理逻辑
}
```

3. 注入并使用责任链

```java
@Autowired
private SpringCasePushPipeline pipeline;

public void process() {
    CaseContext context = new CaseContext("CASE-002");
    pipeline.execute(context);
}
```

### SPI版本

1. 添加依赖

```xml
<dependency>
    <groupId>com.wangguangwu</groupId>
    <artifactId>case-push-pipeline-spi</artifactId>
</dependency>
```

2. 创建处理器实现

```java
public class SpiReportHandler implements CasePushHandler {
    // 实现处理逻辑
}
```

3. 创建SPI配置文件

在`META-INF/services/com.wangguangwu.casepipeline.core.handler.CasePushHandler`文件中添加处理器实现类的全限定名：

```
com.example.SpiReportHandler
```

4. 使用SPI责任链

```java
SpiCasePushPipeline pipeline = new SpiCasePushPipeline();
CaseContext context = new CaseContext("CASE-003");
pipeline.execute(context);
```

## 运行示例

1. 运行核心版本和SPI版本示例：

```bash
java -cp case-push-pipeline-samples/target/case-push-pipeline-samples-1.0.0-SNAPSHOT.jar com.wangguangwu.casepipeline.samples.CasePipelineApplication
```

2. 运行Spring版本示例：

```bash
java -jar case-push-pipeline-samples/target/case-push-pipeline-samples-1.0.0-SNAPSHOT.jar com.wangguangwu.casepipeline.samples.spring.SpringPipelineApplication
# case-push-pipeline
