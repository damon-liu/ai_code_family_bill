# 家庭记账系统

## 项目简介

家庭记账系统是一个基于Spring Boot + Vue 3的全栈应用，用于帮助家庭记录和管理日常收支账单。系统支持收入/支出记录、分类管理、支付方式管理、统计分析等功能。

## 技术栈

### 后端
- **框架**: Spring Boot 2.7.14
- **ORM**: MyBatis Plus 3.5.3.1
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **连接池**: Druid
- **构建工具**: Maven
- **API文档**: Swagger/OpenAPI 3.0

### 前端
- **框架**: Vue 3.3.4
- **构建工具**: Vite 5.0
- **UI组件库**: Element Plus 2.4.2
- **路由**: Vue Router 4.2.5
- **HTTP客户端**: Axios 1.6.0
- **日期处理**: Day.js 1.11.10

## 项目结构

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

## 功能特性

### 账单管理
- ✅ 账单的增删改查
- ✅ 账单信息包含：收款方、金额、交易时间、支付方式、交易号
- ✅ 支出账单金额为负数，收入金额为正数
- ✅ 支持按时间、类型、支付方式、分类筛选查询
- ✅ 分页查询账单列表

### 分类管理
- ✅ 支持收入和支出分类
- ✅ 分类的查询和管理

### 支付方式管理
- ✅ 支付方式的查询和管理
- ✅ 支持支出方式、收入方式、通用方式

### 统计分析
- ✅ 总收入统计
- ✅ 总支出统计
- ✅ 净收入计算（收入-支出）
- ✅ 按分类统计
- ✅ 按支付方式统计

### 性能优化
- ✅ Redis缓存支持
- ✅ 数据库连接池优化
- ✅ 支持高并发访问

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+

### 数据库初始化

1. 创建MySQL数据库：
```sql
CREATE DATABASE family_bill DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库脚本：
```bash
mysql -u root -p family_bill < database/schema.sql
```

或者直接在MySQL客户端中执行 `database/schema.sql` 文件。

### 后端启动

1. 修改数据库配置（`backend/src/main/resources/application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/family_bill?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: your_redis_password  # 如果有密码
```

2. 启动Redis服务

3. 编译并启动后端：
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 `http://127.0.0.1:8090` 启动

4. 访问Swagger API文档：
```
http://127.0.0.1:8090/swagger-ui/index.html
```

### 前端启动

1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

前端服务将在 `http://localhost:3000` 启动

3. 构建生产版本：
```bash
npm run build
```

## API接口文档

### 基础信息

- **Base URL**: `http://127.0.0.1:8090`
- **API前缀**: `/api`
- **数据格式**: JSON
- **字符编码**: UTF-8

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1699000000000
}
```

### 账单管理接口

#### 1. 分页查询账单列表

- **URL**: `GET /api/bills`
- **参数**:
  - `pageNum` (Integer, 可选): 页码，默认1
  - `pageSize` (Integer, 可选): 每页大小，默认10
  - `startTime` (String, 可选): 开始时间，格式：YYYY-MM-DD HH:mm:ss
  - `endTime` (String, 可选): 结束时间，格式：YYYY-MM-DD HH:mm:ss
  - `type` (Integer, 可选): 类型，1-支出，2-收入
  - `paymentMethodId` (Long, 可选): 支付方式ID
  - `categoryId` (Long, 可选): 分类ID

- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "payee": "超市",
        "amount": -100.00,
        "transactionTime": "2024-01-01 10:00:00",
        "paymentMethodId": 1,
        "paymentMethodName": "微信",
        "transactionNo": "WX20240101100000",
        "categoryId": 3,
        "categoryName": "购物",
        "remark": "日常用品",
        "createTime": "2024-01-01 10:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

#### 2. 根据ID查询账单

- **URL**: `GET /api/bills/{id}`
- **参数**: `id` (Long, 路径参数)

#### 3. 新增账单

- **URL**: `POST /api/bills`
- **请求体**:
```json
{
  "payee": "超市",
  "amount": -100.00,
  "transactionTime": "2024-01-01 10:00:00",
  "paymentMethodId": 1,
  "transactionNo": "WX20240101100000",
  "categoryId": 3,
  "remark": "日常用品"
}
```

#### 4. 更新账单

- **URL**: `PUT /api/bills/{id}`
- **请求体**: 同新增账单

#### 5. 删除账单

- **URL**: `DELETE /api/bills/{id}`

#### 6. 获取统计信息

- **URL**: `GET /api/bills/statistics`
- **参数**:
  - `startTime` (String, 可选): 开始时间
  - `endTime` (String, 可选): 结束时间

- **响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalIncome": 5000.00,
    "totalExpense": 3000.00,
    "netIncome": 2000.00,
    "categoryStatistics": [
      {
        "categoryId": 3,
        "categoryName": "购物",
        "amount": 1500.00,
        "type": 1
      }
    ],
    "paymentMethodStatistics": [
      {
        "paymentMethodId": 1,
        "paymentMethodName": "微信",
        "amount": 2000.00
      }
    ]
  }
}
```

### 支付方式接口

#### 查询所有支付方式

- **URL**: `GET /api/payment-methods`
- **参数**:
  - `type` (Integer, 可选): 类型，1-支出方式，2-收入方式，3-通用

### 分类接口

#### 查询所有分类

- **URL**: `GET /api/categories`
- **参数**:
  - `type` (Integer, 可选): 类型，1-支出，2-收入

## 数据库设计

### 表结构

#### 1. payment_method (支付方式表)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| name | VARCHAR(50) | 支付方式名称 |
| icon | VARCHAR(100) | 图标URL |
| type | TINYINT | 类型：1-支出方式，2-收入方式，3-通用 |
| sort_order | INT | 排序顺序 |
| status | TINYINT | 状态：1-启用，0-禁用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 2. bill_category (账单分类表)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| name | VARCHAR(50) | 分类名称 |
| icon | VARCHAR(100) | 图标URL |
| type | TINYINT | 类型：1-支出，2-收入 |
| parent_id | BIGINT | 父分类ID |
| sort_order | INT | 排序顺序 |
| status | TINYINT | 状态：1-启用，0-禁用 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### 3. bill (账单表)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键ID |
| payee | VARCHAR(100) | 收款方 |
| amount | DECIMAL(10,2) | 金额（支出为负数，收入为正数） |
| transaction_time | DATETIME | 交易时间 |
| payment_method_id | BIGINT | 支付方式ID |
| transaction_no | VARCHAR(100) | 交易号 |
| category_id | BIGINT | 分类ID |
| remark | VARCHAR(500) | 备注 |
| user_id | BIGINT | 用户ID（预留） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

## 开发规范

### 后端规范

1. **包结构**: 严格按照controller、service、mapper、entity分层
2. **命名规范**: 
   - 类名：大驼峰（PascalCase）
   - 方法名、变量名：小驼峰（camelCase）
   - 常量：全大写下划线分隔（UPPER_SNAKE_CASE）
3. **接口风格**: RESTful API设计
4. **异常处理**: 统一使用GlobalExceptionHandler处理
5. **缓存策略**: 使用Redis缓存查询结果，更新时清除相关缓存

### 前端规范

1. **组件命名**: 大驼峰（PascalCase）
2. **文件命名**: 小驼峰（camelCase）
3. **API调用**: 统一使用axios封装
4. **状态管理**: 使用Vue 3 Composition API
5. **代码风格**: 遵循Vue 3官方风格指南

## 部署说明

### 后端部署

1. 打包项目：
```bash
cd backend
mvn clean package -DskipTests
```

2. 运行jar包：
```bash
java -jar target/family-bill-backend-1.0.0.jar
```

### 前端部署

1. 构建项目：
```bash
cd frontend
npm run build
```

2. 将 `dist` 目录部署到Nginx或其他Web服务器

3. Nginx配置示例：
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /path/to/frontend/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://127.0.0.1:8090;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 常见问题

### 1. 数据库连接失败

- 检查MySQL服务是否启动
- 检查数据库配置是否正确
- 检查数据库用户权限

### 2. Redis连接失败

- 检查Redis服务是否启动
- 检查Redis配置是否正确
- 如果Redis有密码，确保配置文件中设置了密码

### 3. 前端接口调用失败

- 检查后端服务是否启动
- 检查CORS配置是否正确
- 检查API地址是否正确

## 后续优化方向

- [ ] 用户认证和授权（JWT）
- [ ] 多用户支持
- [ ] 数据导出功能（Excel、PDF）
- [ ] 图表可视化（ECharts）
- [ ] 移动端适配
- [ ] 账单提醒功能
- [ ] 预算管理功能
- [ ] 数据备份和恢复

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎提交Issue或Pull Request。


