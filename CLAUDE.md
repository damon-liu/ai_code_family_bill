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


