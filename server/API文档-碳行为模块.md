# 碳行为模块 API 文档

## 基础信息

- 基础路径：`/api/behavior`
- 所有接口需要在请求头中携带 JWT Token：`Authorization: Bearer {token}`
- 响应格式：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

## 用户端接口

### 1. 获取行为规则列表

**接口地址**：`GET /api/behavior/rules`

**接口描述**：查询所有启用的行为规则

**请求参数**：无

**响应示例**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "behaviorType": "walking",
      "behaviorName": "步行出行",
      "unit": "公里",
      "carbonReductionPerUnit": 0.1200,
      "pointsPerUnit": 10,
      "minValue": 1.00,
      "maxValue": 20.00,
      "maxPointsPerDay": 200,
      "needProof": 0,
      "status": 1,
      "sortOrder": 1,
      "description": "步行代替开车出行，每公里减少0.12千克碳排放，获得10积分"
    }
  ]
}
```

### 2. 获取行为规则详情

**接口地址**：`GET /api/behavior/rules/{id}`

**接口描述**：根据规则ID查询规则详细信息

**路径参数**：
- `id`：规则ID

**响应示例**：同上单个规则对象

### 3. 提交行为记录

**接口地址**：`POST /api/behavior/record`

**接口描述**：用户提交行为记录

**请求体**：
```json
{
  "behaviorType": "walking",
  "behaviorValue": 5.0,
  "description": "今天步行上班5公里",
  "proofImage": "/behavior-proof/2026-01-14/abc123.jpg"
}
```

**字段说明**：
- `behaviorType`：行为类型（必填）
- `behaviorValue`：行为数值（必填，必须大于0）
- `description`：行为描述（可选）
- `proofImage`：证明材料图片URL（如果规则要求需要证明材料则必填）

**响应示例**：
```json
{
  "code": 200,
  "message": "提交成功，等待审核",
  "data": null
}
```

### 4. 获取行为记录列表

**接口地址**：`GET /api/behavior/record`

**接口描述**：分页查询当前用户的行为记录

**请求参数**：
- `behaviorType`：行为类型（可选）
- `auditStatus`：审核状态，0-待审核，1-审核通过，2-审核拒绝（可选）
- `startTime`：开始时间，格式：yyyy-MM-dd HH:mm:ss（可选）
- `endTime`：结束时间，格式：yyyy-MM-dd HH:mm:ss（可选）
- `pageNum`：页码，默认1
- `pageSize`：每页数量，默认10

**响应示例**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 2,
        "username": "zhangsan",
        "nickname": "张三",
        "behaviorType": "walking",
        "behaviorName": "步行出行",
        "behaviorValue": 5.00,
        "carbonReduction": 0.60,
        "points": 50,
        "description": "今天步行上班5公里",
        "proofImage": null,
        "auditStatus": 1,
        "auditStatusText": "审核通过",
        "auditUserId": 1,
        "auditUsername": "admin",
        "auditTime": "2026-01-13 10:30:00",
        "auditRemark": "审核通过",
        "createTime": "2026-01-13 08:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 5. 获取行为记录详情

**接口地址**：`GET /api/behavior/record/{id}`

**接口描述**：根据记录ID查询行为记录详细信息

**路径参数**：
- `id`：记录ID

**响应示例**：同上单个记录对象

### 6. 删除行为记录

**接口地址**：`DELETE /api/behavior/record/{id}`

**接口描述**：删除待审核状态的行为记录

**路径参数**：
- `id`：记录ID

**响应示例**：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 7. 获取行为统计数据

**接口地址**：`GET /api/behavior/statistics`

**接口描述**：统计用户各类行为的次数、总减碳量、总获得积分

**请求参数**：
- `startTime`：开始时间，格式：yyyy-MM-dd HH:mm:ss（可选）
- `endTime`：结束时间，格式：yyyy-MM-dd HH:mm:ss（可选）

**响应示例**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalRecords": 15,
    "totalCarbonReduction": 25.50,
    "totalPoints": 350,
    "typeStatistics": [
      {
        "behaviorType": "walking",
        "behaviorName": "步行出行",
        "count": 8,
        "carbonReduction": 9.60,
        "points": 120
      },
      {
        "behaviorType": "cycling",
        "behaviorName": "骑行出行",
        "count": 5,
        "carbonReduction": 12.00,
        "points": 180
      }
    ]
  }
}
```

### 8. 上传行为证明材料

**接口地址**：`POST /api/behavior/upload/behavior-proof`

**接口描述**：上传行为证明材料图片

**请求类型**：`multipart/form-data`

**请求参数**：
- `file`：图片文件（必填）

**支持格式**：jpg、jpeg、png、gif

**文件大小限制**：5MB

**响应示例**：
```json
{
  "code": 200,
  "message": "上传成功",
  "data": "/behavior-proof/2026-01-14/abc123def456.jpg"
}
```

## 管理端接口

### 9. 获取待审核行为记录列表

**接口地址**：`GET /api/behavior/audit/list`

**接口描述**：管理员查询所有待审核的行为记录

**请求参数**：
- `userId`：用户ID（可选）
- `behaviorType`：行为类型（可选）
- `startTime`：开始时间，格式：yyyy-MM-dd HH:mm:ss（可选）
- `endTime`：结束时间，格式：yyyy-MM-dd HH:mm:ss（可选）
- `pageNum`：页码，默认1
- `pageSize`：每页数量，默认10

**响应示例**：同"获取行为记录列表"

### 10. 审核行为记录

**接口地址**：`PUT /api/behavior/audit/{id}`

**接口描述**：管理员审核行为记录

**路径参数**：
- `id`：记录ID

**请求体**：
```json
{
  "auditStatus": 1,
  "auditRemark": "审核通过"
}
```

**字段说明**：
- `auditStatus`：审核状态，1-审核通过，2-审核拒绝（必填）
- `auditRemark`：审核备注（可选）

**响应示例**：
```json
{
  "code": 200,
  "message": "审核成功",
  "data": null
}
```

### 11. 创建行为规则

**接口地址**：`POST /api/behavior/rules`

**接口描述**：管理员创建新的行为规则

**请求体**：
```json
{
  "behaviorType": "metro",
  "behaviorName": "地铁出行",
  "unit": "次",
  "carbonReductionPerUnit": 3.0000,
  "pointsPerUnit": 25,
  "minValue": 1.00,
  "maxValue": 10.00,
  "maxPointsPerDay": 250,
  "needProof": 0,
  "sortOrder": 10,
  "description": "乘坐地铁代替开车，每次减少3千克碳排放，获得25积分"
}
```

**字段说明**：
- `behaviorType`：行为类型（必填，唯一）
- `behaviorName`：行为名称（必填）
- `unit`：单位（必填）
- `carbonReductionPerUnit`：每单位减碳量（必填，大于0）
- `pointsPerUnit`：每单位积分（必填，大于0）
- `minValue`：最小数值限制（可选）
- `maxValue`：每日最大数值限制（可选）
- `maxPointsPerDay`：每日最大积分限制（可选）
- `needProof`：是否需要证明材料，0-不需要，1-需要（必填）
- `sortOrder`：排序顺序（可选）
- `description`：规则说明（可选）

**响应示例**：
```json
{
  "code": 200,
  "message": "创建成功",
  "data": null
}
```

### 12. 更新行为规则

**接口地址**：`PUT /api/behavior/rules/{id}`

**接口描述**：管理员修改现有行为规则

**路径参数**：
- `id`：规则ID

**请求体**：同"创建行为规则"

**响应示例**：
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 13. 删除行为规则

**接口地址**：`DELETE /api/behavior/rules/{id}`

**接口描述**：管理员删除行为规则（已使用的规则不允许删除）

**路径参数**：
- `id`：规则ID

**响应示例**：
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

## 错误码说明

- `200`：操作成功
- `401`：未授权，请先登录
- `500`：业务异常或系统异常

## 注意事项

1. 所有需要认证的接口都需要在请求头中携带 JWT Token
2. 管理员接口需要用户类型为管理员（userType=1）
3. 时间参数格式统一为：`yyyy-MM-dd HH:mm:ss`
4. 文件上传接口使用 `multipart/form-data` 格式
5. 审核通过后会自动发放积分并更新用户积分和减碳量
6. 只能删除待审核状态的行为记录
7. 已使用的行为规则不允许删除
