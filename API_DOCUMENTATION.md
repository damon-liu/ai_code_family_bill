# 家庭记账系统 API 接口文档

## 基础信息

- **Base URL**: `http://127.0.0.1:8090`
- **API前缀**: `/api`
- **数据格式**: JSON
- **字符编码**: UTF-8
- **请求方式**: RESTful

## 统一响应格式

所有接口返回统一的JSON格式：

```json
{
  "code": 200,           // 状态码：200-成功，其他-失败
  "message": "操作成功",  // 响应消息
  "data": {},            // 响应数据
  "timestamp": 1699000000000  // 时间戳
}
```

### 状态码说明

- `200`: 操作成功
- `400`: 请求参数错误
- `500`: 服务器内部错误

## 账单管理接口

### 1. 分页查询账单列表

**接口地址**: `GET /api/bills`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页大小，默认10 |
| startTime | String | 否 | 开始时间，格式：YYYY-MM-DD HH:mm:ss |
| endTime | String | 否 | 结束时间，格式：YYYY-MM-DD HH:mm:ss |
| type | Integer | 否 | 类型：1-支出，2-收入 |
| paymentMethodId | Long | 否 | 支付方式ID |
| categoryId | Long | 否 | 分类ID |

**请求示例**:
```
GET /api/bills?pageNum=1&pageSize=10&type=1&startTime=2024-01-01 00:00:00&endTime=2024-01-31 23:59:59
```

**响应示例**:
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
        "transactionTime": "2024-01-01T10:00:00",
        "paymentMethodId": 1,
        "paymentMethodName": "微信",
        "transactionNo": "WX20240101100000",
        "categoryId": 3,
        "categoryName": "购物",
        "remark": "日常用品",
        "createTime": "2024-01-01T10:00:00"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  },
  "timestamp": 1699000000000
}
```

---

### 2. 根据ID查询账单

**接口地址**: `GET /api/bills/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 账单ID |

**请求示例**:
```
GET /api/bills/1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "payee": "超市",
    "amount": -100.00,
    "transactionTime": "2024-01-01T10:00:00",
    "paymentMethodId": 1,
    "paymentMethodName": "微信",
    "transactionNo": "WX20240101100000",
    "categoryId": 3,
    "categoryName": "购物",
    "remark": "日常用品",
    "createTime": "2024-01-01T10:00:00"
  },
  "timestamp": 1699000000000
}
```

---

### 3. 新增账单

**接口地址**: `POST /api/bills`

**请求头**:
```
Content-Type: application/json
```

**请求体**:

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| payee | String | 是 | 收款方 |
| amount | BigDecimal | 是 | 金额（支出为负数，收入为正数） |
| transactionTime | String | 是 | 交易时间，格式：YYYY-MM-DD HH:mm:ss |
| paymentMethodId | Long | 是 | 支付方式ID |
| transactionNo | String | 否 | 交易号 |
| categoryId | Long | 否 | 分类ID |
| remark | String | 否 | 备注 |

**请求示例**:
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

**响应示例**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": 1,
  "timestamp": 1699000000000
}
```

---

### 4. 更新账单

**接口地址**: `PUT /api/bills/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 账单ID |

**请求头**:
```
Content-Type: application/json
```

**请求体**: 同新增账单

**请求示例**:
```
PUT /api/bills/1
Content-Type: application/json

{
  "payee": "超市",
  "amount": -150.00,
  "transactionTime": "2024-01-01 10:00:00",
  "paymentMethodId": 1,
  "transactionNo": "WX20240101100000",
  "categoryId": 3,
  "remark": "日常用品"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null,
  "timestamp": 1699000000000
}
```

---

### 5. 删除账单

**接口地址**: `DELETE /api/bills/{id}`

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 账单ID |

**请求示例**:
```
DELETE /api/bills/1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1699000000000
}
```

---

### 6. 获取统计信息

**接口地址**: `GET /api/bills/statistics`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| startTime | String | 否 | 开始时间，格式：YYYY-MM-DD HH:mm:ss |
| endTime | String | 否 | 结束时间，格式：YYYY-MM-DD HH:mm:ss |

**请求示例**:
```
GET /api/bills/statistics?startTime=2024-01-01 00:00:00&endTime=2024-01-31 23:59:59
```

**响应示例**:
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
      },
      {
        "categoryId": 1,
        "categoryName": "餐饮",
        "amount": 1000.00,
        "type": 1
      }
    ],
    "paymentMethodStatistics": [
      {
        "paymentMethodId": 1,
        "paymentMethodName": "微信",
        "amount": 2000.00
      },
      {
        "paymentMethodId": 2,
        "paymentMethodName": "支付宝",
        "amount": 1500.00
      }
    ]
  },
  "timestamp": 1699000000000
}
```

---

## 支付方式接口

### 查询所有支付方式

**接口地址**: `GET /api/payment-methods`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| type | Integer | 否 | 类型：1-支出方式，2-收入方式，3-通用 |

**请求示例**:
```
GET /api/payment-methods
GET /api/payment-methods?type=1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "微信",
      "icon": "wechat",
      "type": 3,
      "sortOrder": 1,
      "status": 1,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    },
    {
      "id": 2,
      "name": "支付宝",
      "icon": "alipay",
      "type": 3,
      "sortOrder": 2,
      "status": 1,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    }
  ],
  "timestamp": 1699000000000
}
```

---

## 分类接口

### 查询所有分类

**接口地址**: `GET /api/categories`

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| type | Integer | 否 | 类型：1-支出，2-收入 |

**请求示例**:
```
GET /api/categories
GET /api/categories?type=1
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "餐饮",
      "icon": "food",
      "type": 1,
      "parentId": 0,
      "sortOrder": 1,
      "status": 1,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    },
    {
      "id": 2,
      "name": "交通",
      "icon": "transport",
      "type": 1,
      "parentId": 0,
      "sortOrder": 2,
      "status": 1,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    }
  ],
  "timestamp": 1699000000000
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 500 | 服务器内部错误 |

## 注意事项

1. 所有时间字段格式为：`YYYY-MM-DD HH:mm:ss`
2. 金额字段使用 `DECIMAL(10,2)` 类型，支持两位小数
3. 支出金额为负数，收入金额为正数
4. 所有接口支持跨域请求（CORS）
5. 接口响应时间戳为毫秒级Unix时间戳

## Swagger文档

启动后端服务后，可访问Swagger UI查看完整的API文档：

```
http://127.0.0.1:8090/swagger-ui/index.html
```


