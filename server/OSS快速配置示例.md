# OSS快速配置示例

## 配置步骤（5分钟完成）

### 1. 登录阿里云控制台

访问：https://oss.console.aliyun.com/

### 2. 创建Bucket（2分钟）

1. 点击"创建Bucket"
2. 填写信息：
   - Bucket名称：`carbon-system-2026`（必须全局唯一）
   - 地域：选择"华东1（杭州）"
   - 读写权限：选择"公共读"
3. 点击"确定"

### 3. 获取AccessKey（2分钟）

1. 点击右上角头像 → "AccessKey管理"
2. 点击"创建AccessKey"
3. **重要**：立即复制并保存AccessKey ID和AccessKey Secret

### 4. 配置项目（1分钟）

打开 `server/src/main/resources/application.yml`，修改以下配置：

```yaml
aliyun:
  oss:
    enabled: true
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    access-key-id: 你的AccessKey-ID
    access-key-secret: 你的AccessKey-Secret
    bucket-name: carbon-system-2026
    prefix: carbon-system
    custom-domain:
```

### 5. 重启项目

```bash
cd server
mvn spring-boot:run
```

## 完整配置示例

### 示例1：基础配置（推荐）

```yaml
aliyun:
  oss:
    enabled: true
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    access-key-id: LTAI5tXXXXXXXXXXXXXX
    access-key-secret: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    bucket-name: carbon-system-2026
    prefix: carbon-system
    custom-domain:
```

**文件访问URL示例**：
```
https://carbon-system-2026.oss-cn-hangzhou.aliyuncs.com/carbon-system/avatar/2026-01-16/abc123.jpg
```

### 示例2：使用自定义域名

```yaml
aliyun:
  oss:
    enabled: true
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    access-key-id: LTAI5tXXXXXXXXXXXXXX
    access-key-secret: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    bucket-name: carbon-system-2026
    prefix: carbon-system
    custom-domain: cdn.yourdomain.com
```

**文件访问URL示例**：
```
https://cdn.yourdomain.com/carbon-system/avatar/2026-01-16/abc123.jpg
```

### 示例3：暂时使用本地存储

```yaml
aliyun:
  oss:
    enabled: false
```

## 不同地域的Endpoint

| 地域 | Endpoint |
|------|----------|
| 华东1（杭州） | https://oss-cn-hangzhou.aliyuncs.com |
| 华东2（上海） | https://oss-cn-shanghai.aliyuncs.com |
| 华北1（青岛） | https://oss-cn-qingdao.aliyuncs.com |
| 华北2（北京） | https://oss-cn-beijing.aliyuncs.com |
| 华南1（深圳） | https://oss-cn-shenzhen.aliyuncs.com |
| 华南2（广州） | https://oss-cn-guangzhou.aliyuncs.com |
| 西南1（成都） | https://oss-cn-chengdu.aliyuncs.com |

## 测试上传

### 使用Postman测试

1. **登录获取Token**
```
POST http://localhost:8080/api/user/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

2. **上传头像**
```
POST http://localhost:8080/api/upload/avatar
Authorization: Bearer {你的token}
Content-Type: multipart/form-data

file: [选择图片文件]
```

3. **预期响应**
```json
{
  "code": 200,
  "message": "上传成功",
  "data": "https://carbon-system-2026.oss-cn-hangzhou.aliyuncs.com/carbon-system/avatar/2026-01-16/abc123def456.jpg"
}
```

### 使用curl测试

```bash
# 1. 登录
curl -X POST "http://localhost:8080/api/user/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 2. 上传（替换YOUR_TOKEN和图片路径）
curl -X POST "http://localhost:8080/api/upload/avatar" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

## 常见问题快速解决

### Q1: 上传失败，提示"InvalidAccessKeyId"

**解决**：检查application.yml中的access-key-id和access-key-secret是否正确

### Q2: 上传成功但无法访问图片

**解决**：
1. 进入OSS控制台
2. 选择你的Bucket
3. 点击"权限管理" → "读写权限"
4. 设置为"公共读"

### Q3: 跨域问题

**解决**：
1. 进入OSS控制台
2. 选择你的Bucket
3. 点击"权限管理" → "跨域设置"
4. 点击"创建规则"
5. 配置：
   - 来源：`*`
   - 允许Methods：全选
   - 允许Headers：`*`
   - 暴露Headers：`ETag`、`x-oss-request-id`

### Q4: 如何切换回本地存储？

**解决**：在application.yml中设置 `enabled: false`

## 文件存储结构

```
carbon-system/                    # prefix前缀
├── avatar/                       # 头像目录
│   └── 2026-01-16/              # 日期目录
│       ├── abc123.jpg
│       └── def456.png
├── behavior-proof/               # 行为证明材料
│   └── 2026-01-16/
│       └── xyz789.jpg
├── product/                      # 商品图片
│   └── 2026-01-16/
│       └── prd001.jpg
└── activity/                     # 活动图片
    └── 2026-01-16/
        └── act001.jpg
```

## 安全提示

⚠️ **重要**：
1. 不要将AccessKey提交到Git仓库
2. 建议使用RAM用户而不是主账号
3. 定期更换AccessKey
4. 配置Bucket防盗链

## 费用说明

小型项目（1000用户）月费用约：
- 存储费用：1GB × ¥0.12 = ¥0.12
- 流量费用：10GB × ¥0.50 = ¥0.50
- 请求费用：10万次 × ¥0.01 = ¥0.01
- **总计**：约 ¥0.63/月

## 下一步

配置完成后：
1. ✅ 重启项目
2. ✅ 测试上传头像
3. ✅ 在浏览器中访问返回的URL
4. ✅ 确认图片能正常显示

---

**配置遇到问题？** 查看详细文档：[阿里云OSS配置指南.md](./阿里云OSS配置指南.md)
