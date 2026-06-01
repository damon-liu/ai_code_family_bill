# CLAUDE.md

本文件为 Claude Code（claude.ai/code）提供在此代码库中工作的指导说明。

## 项目概述

家庭记账系统是一个基于Spring Boot + Vue 3的全栈应用，用于帮助家庭记录和管理日常收支账单。系统支持收入/支出记录、分类管理、支付方式管理、统计分析等功能。

## 架构说明

标准工程包结构（严格强制执行）

```
family_bill/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/family/bill/
│   │   │   │   ├── controller/    # 控制器层
│   │   │   │   ├── service/        # 服务层
│   │   │   │   ├── mapper/         # 数据访问层
│   │   │   │   ├── entity/         # 实体类
│   │   │   │   ├── dto/            # 数据传输对象
│   │   │   │   ├── vo/             # 视图对象
│   │   │   │   ├── common/         # 公共类
│   │   │   │   └── config/         # 配置类
│   │   │   └── resources/
│   │   │       ├── mapper/         # MyBatis XML映射文件
│   │   │       └── application.yml  # 应用配置
│   │   └── test/                   # 测试代码
│   └── pom.xml                    # Maven配置
├── frontend/                     # 前端项目
│   ├── src/
│   │   ├── api/                   # API接口
│   │   ├── views/                 # 页面组件
│   │   ├── router/                # 路由配置
│   │   ├── App.vue                # 根组件
│   │   └── main.js                # 入口文件
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
├── database/                      # 数据库脚本
│   └── schema.sql                # 数据库表结构
└── README.md                      # 项目文档
```



## 技术栈

### 后端技术栈

- 框架: Spring Boot 2.7.14
- JDK：1.8
- ORM: MyBatis Plus 3.5.3.1
- 数据库: MySQL 8.0+
- 缓存: Redis
- 连接池: Druid
- 构建工具: Maven
- 日志框架：SLF4J + Lombok @Slf4j
- 测试框架：JUnit 5 + Mockito

### 前端技术栈

- 框架: Vue 3.3.4
- 构建工具: Vite 5.0
- UI组件库: Element Plus 2.4.2
- 路由: Vue Router 4.2.5
- HTTP客户端: Axios 1.6.0
- 日期处理: Day.js 1.11.10

## 后端接口"完成"定义（强制）

新增 / 修改 / 重构后端 Java 文件后，由 `.claude/hooks/run-related-tests.sh`（PostToolUse hook）自动定位并运行对应的 JUnit5 测试类：

- 命名约定：`com.x.y.Foo` → `com.x.y.FooTest`
- 失败反馈：hook 以 exit 2 + stderr 注入 `❌ [auto-test]` 信息到上下文，Claude 必须按 `backend-api-tdd-loop` skill 的步骤 5 修复，直到 hook 不再报错
- **循环上限**：同一测试类连续失败 ≥3 次时，hook 输出 `🛑` 标记并强制中止循环；Claude 必须立刻停止改代码，改为按 skill 步骤 6 向用户汇报失败原因和可选方案
- 未找到测试类：hook 输出 `⚠️` 警告，必须立即补写单元测试
- 禁止行为：删除测试 / 注释 `@Test` / 把测试期望改成符合错误实现的值，让 hook "通过"

只有 hook 真正通过（`✅ [auto-test] ... 通过`），才能向用户汇报接口完成。
