# 阿里云OSS集成完成说明

## 已完成的工作

### ✅ 1. 添加依赖

在 `pom.xml` 中添加了阿里云OSS SDK依赖：
```xml
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.17.4</version>
</dependency>
```

### ✅ 2. 创建配置类

**文件**：`OssConfig.java`

支持的配置项：
- `endpoint` - OSS访问端点
- `accessKeyId` - 访问密钥ID
- `accessKeySecret` - 访问密钥
- `bucketName` - 存储空间名称
- `prefix` - 文件存储目录前缀
- `customDomain` - 自定义域名（可选）
- `enabled` - 是否启用OSS

### ✅ 3. 创建OSS工具类

**文件**：`OssUtil.java`

**核心功能**：
- ✅ 文件上传到OSS
- ✅ 文件删除
- ✅ 文件格式验证（jpg、jpeg、png、gif、bmp、webp）
- ✅ 文件大小验证（最大5MB）
- ✅ 自动生成唯一文件名（UUID）
- ✅ 按日期组织目录结构
- ✅ 支持自定义域名
- ✅ 支持本地存储降级

**文件命名规则**：
```
{prefix}/{folder}/{yyyy-MM-dd}/{uuid}.{ext}

示例：
carbon-system/avatar/2026-01-16/abc123def456.jpg
```

### ✅ 4. 创建上传接口

**文件**：`UploadController.java`

**提供的接口**：

| 接口 | 用途 | 存储路径 |
|------|------|----------|
| POST /api/upload/avatar | 上传用户头像 | {prefix}/avatar/{date}/{uuid}.{ext} |
| POST /api/upload/behavior-proof | 上传行为证明材料 | {prefix}/behavior-proof/{date}/{uuid}.{ext} |
| POST /api/upload/product-image | 上传商品图片 | {prefix}/product/{date}/{uuid}.{ext} |
| POST /api/upload/activity-image | 上传活动图片 | {prefix}/activity/{date}/{uuid}.{ext} |

### ✅ 5. 更新配置文件

**文件**：`application.yml`

添加了完整的OSS配置模板，包含详细的注释说明。

### ✅ 6. 更新BehaviorController

移除了旧的上传接口，统一使用 `UploadController`。

### ✅ 7. 创建文档

- ✅ `阿里云OSS配置指南.md` - 详细的配置步骤和说明
- ✅ `OSS快速配置示例.md` - 5分钟快速配置指南
- ✅ `OSS集成完成说明.md` - 本文档

## 使用方式

### 方式1：使用阿里云OSS（推荐）

1. **配置OSS**

编辑 `application.yml`：
```yaml
aliyun:
  oss:
    enabled: true
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    access-key-id: 你的AccessKey-ID
    access-key-secret: 你的AccessKey-Secret
    bucket-name: 你的Bucket名称
    prefix: carbon-system
    custom-domain:
```

2. **重启项目**

3. **测试上传**
```bash
POST http://localhost:8080/api/upload/avatar
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: [选择图片]
```

4. **返回OSS URL**
```json
{
  "code": 200,
  "message": "上传成功",
  "data": "https://your-bucket.oss-cn-hangzhou.aliyuncs.com/carbon-system/avatar/2026-01-16/abc123.jpg"
}
```

### 方式2：使用本地存储

如果暂时不想配置OSS，可以使用本地存储：

```yaml
aliyun:
  oss:
    enabled: false
```

文件将保存到项目的 `uploads/` 目录。

## 功能特性

### 1. 智能降级

如果OSS配置错误或网络问题，会自动降级到本地存储，不影响业务。

### 2. 文件验证

- ✅ 支持的格式：jpg、jpeg、png、gif、bmp、webp
- ✅ 文件大小限制：5MB
- ✅ 自动验证文件类型

### 3. 安全性

- ✅ 文件名使用UUID，防止重复和猜测
- ✅ 按日期组织目录，便于管理
- ✅ 支持自定义域名，可配置CDN加速

### 4. 灵活性

- ✅ 可随时切换OSS和本地存储
- ✅ 支持多个上传接口，按业务分类
- ✅ 支持自定义存储路径前缀

## API使用示例

### 1. 上传用户头像

**场景**：用户修改个人头像

```javascript
// 前端代码示例
const formData = new FormData();
formData.append('file', file);

fetch('http://localhost:8080/api/upload/avatar', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token
  },
  body: formData
})
.then(response => response.json())
.then(data => {
  console.log('头像URL:', data.data);
  // 更新用户信息，保存头像URL到数据库
});
```

### 2. 上传行为证明材料

**场景**：用户提交行为记录时上传证明图片

```javascript
// 1. 先上传图片
const uploadResponse = await fetch('/api/upload/behavior-proof', {
  method: 'POST',
  headers: { 'Authorization': 'Bearer ' + token },
  body: formData
});
const uploadData = await uploadResponse.json();
const imageUrl = uploadData.data;

// 2. 提交行为记录，包含图片URL
await fetch('/api/behavior/record', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    behaviorType: 'garbage_sorting',
    behaviorValue: 1,
    description: '垃圾分类',
    proofImage: imageUrl  // 使用上传返回的URL
  })
});
```

### 3. 上传商品图片

**场景**：管理员添加商品时上传商品图片

```javascript
const formData = new FormData();
formData.append('file', file);

fetch('http://localhost:8080/api/upload/product-image', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + adminToken
  },
  body: formData
})
.then(response => response.json())
.then(data => {
  console.log('商品图片URL:', data.data);
});
```

## 数据库存储

上传成功后，将返回的URL保存到数据库：

```sql
-- 用户表
UPDATE user SET avatar = 'https://xxx.oss-cn-hangzhou.aliyuncs.com/...' WHERE id = 1;

-- 行为记录表
INSERT INTO carbon_behavior_record (proof_image, ...) 
VALUES ('https://xxx.oss-cn-hangzhou.aliyuncs.com/...', ...);

-- 商品表
INSERT INTO product (image, ...) 
VALUES ('https://xxx.oss-cn-hangzhou.aliyuncs.com/...', ...);
```

## 文件管理

### 查看已上传的文件

1. 登录OSS控制台
2. 选择你的Bucket
3. 点击"文件管理"
4. 可以看到所有上传的文件

### 删除文件

```java
// 在代码中调用
ossUtil.deleteFile(fileUrl);
```

### 设置生命周期规则

可以在OSS控制台设置自动删除过期文件，节省存储费用。

## 性能优化建议

### 1. 配置CDN加速

1. 在阿里云CDN控制台添加加速域名
2. 绑定到OSS Bucket
3. 在application.yml中配置custom-domain

### 2. 图片处理

阿里云OSS支持图片处理，可以在URL后添加参数：

```
原图：
https://xxx.oss-cn-hangzhou.aliyuncs.com/avatar/abc.jpg

缩略图（宽度100px）：
https://xxx.oss-cn-hangzhou.aliyuncs.com/avatar/abc.jpg?x-oss-process=image/resize,w_100

圆形裁剪：
https://xxx.oss-cn-hangzhou.aliyuncs.com/avatar/abc.jpg?x-oss-process=image/circle,r_100
```

### 3. 压缩上传

前端可以先压缩图片再上传，减少流量费用。

## 安全建议

### 1. 使用RAM用户

不要使用主账号的AccessKey，创建专门的RAM用户：

1. 进入RAM控制台
2. 创建用户：`carbon-oss-user`
3. 授权：`AliyunOSSFullAccess`
4. 创建AccessKey

### 2. 配置Bucket策略

限制只能上传图片：

```json
{
  "Version": "1",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "oss:PutObject"
      ],
      "Resource": [
        "acs:oss:*:*:your-bucket/*"
      ],
      "Condition": {
        "StringLike": {
          "oss:x-oss-object-type": "image/*"
        }
      }
    }
  ]
}
```

### 3. 配置防盗链

在OSS控制台配置Referer白名单，防止盗链。

### 4. 不要提交密钥

将 `application.yml` 添加到 `.gitignore`：

```gitignore
# 配置文件
application.yml
application-*.yml
```

或使用环境变量：

```yaml
aliyun:
  oss:
    access-key-id: ${OSS_ACCESS_KEY_ID}
    access-key-secret: ${OSS_ACCESS_KEY_SECRET}
```

## 监控和日志

### 查看上传日志

后端日志会记录上传信息：

```
INFO  - 文件上传成功: carbon-system/avatar/2026-01-16/abc123.jpg
ERROR - 文件上传失败: InvalidAccessKeyId
```

### OSS访问日志

在OSS控制台可以查看：
- 访问次数
- 流量统计
- 错误日志

## 故障排查

### 问题1：上传失败

**检查**：
1. OSS配置是否正确
2. AccessKey是否有效
3. Bucket是否存在
4. 网络是否正常

**查看日志**：
```
ERROR - 文件上传失败: InvalidAccessKeyId
```

### 问题2：图片无法访问

**检查**：
1. Bucket权限是否为"公共读"
2. URL是否正确
3. 是否配置了防盗链

### 问题3：跨域问题

**解决**：配置CORS规则（参考配置指南）

## 费用预估

### 小型项目（1000用户）

**月费用**：
- 存储：1GB × ¥0.12 = ¥0.12
- 流量：10GB × ¥0.50 = ¥0.50
- 请求：10万次 × ¥0.01 = ¥0.01
- **总计**：约 ¥0.63/月

### 中型项目（10000用户）

**月费用**：
- 存储：10GB × ¥0.12 = ¥1.20
- 流量：100GB × ¥0.50 = ¥50.00
- 请求：100万次 × ¥0.01 = ¥0.10
- **总计**：约 ¥51.30/月

**优化建议**：配置CDN可以降低流量费用约50%

## 下一步

1. ✅ 配置阿里云OSS（参考：OSS快速配置示例.md）
2. ✅ 重启项目
3. ✅ 测试上传功能
4. ✅ 前端集成上传接口
5. ✅ 配置CDN加速（可选）
6. ✅ 设置监控告警（可选）

## 相关文档

- [阿里云OSS配置指南.md](./阿里云OSS配置指南.md) - 详细配置步骤
- [OSS快速配置示例.md](./OSS快速配置示例.md) - 5分钟快速配置
- [阿里云OSS官方文档](https://help.aliyun.com/product/31815.html)

---

**集成完成！** 🎉

现在你可以：
1. 配置你的OSS信息
2. 重启项目
3. 开始使用文件上传功能

如有问题，请查看相关文档或联系技术支持。
