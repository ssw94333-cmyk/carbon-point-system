# 阿里云OSS配置指南

## 一、准备工作

### 1. 注册阿里云账号
访问 [阿里云官网](https://www.aliyun.com/) 注册账号

### 2. 开通OSS服务
1. 登录阿里云控制台
2. 搜索"对象存储OSS"
3. 点击"立即开通"
4. 选择按量付费（推荐）或包年包月

## 二、创建Bucket

### 1. 进入OSS控制台
访问：https://oss.console.aliyun.com/

### 2. 创建Bucket
1. 点击"创建Bucket"
2. 填写以下信息：
   - **Bucket名称**：全局唯一，建议：`carbon-point-system-{随机数}`
   - **地域**：选择离你最近的地域（如：华东1-杭州）
   - **存储类型**：标准存储
   - **读写权限**：公共读（允许匿名访问）
   - **服务端加密**：无
   - **实时日志查询**：不开启
3. 点击"确定"创建

### 3. 配置跨域规则（CORS）

1. 进入Bucket管理页面
2. 点击"权限管理" → "跨域设置"
3. 点击"创建规则"
4. 配置如下：
   - **来源**：`*`
   - **允许Methods**：勾选 GET、POST、PUT、DELETE、HEAD
   - **允许Headers**：`*`
   - **暴露Headers**：`ETag`、`x-oss-request-id`
   - **缓存时间**：600
5. 点击"确定"

## 三、获取访问密钥

### 1. 创建AccessKey

1. 点击右上角头像 → "AccessKey管理"
2. 建议创建RAM用户（更安全）：
   - 点击"创建用户"
   - 用户名：`carbon-oss-user`
   - 访问方式：勾选"OpenAPI调用访问"
   - 点击"确定"
   - **重要**：保存AccessKey ID和AccessKey Secret（只显示一次）

### 2. 授权OSS权限

1. 进入RAM控制台
2. 找到刚创建的用户
3. 点击"添加权限"
4. 选择"AliyunOSSFullAccess"（OSS完全访问权限）
5. 点击"确定"

## 四、配置项目

### 1. 修改application.yml

打开 `server/src/main/resources/application.yml`，找到OSS配置部分：

```yaml
aliyun:
  oss:
    # 是否启用OSS（true: 使用OSS存储, false: 使用本地存储）
    enabled: true
    
    # OSS访问端点（Endpoint）
    # 根据你的Bucket所在地域填写
    # 华东1（杭州）：https://oss-cn-hangzhou.aliyuncs.com
    # 华东2（上海）：https://oss-cn-shanghai.aliyuncs.com
    # 华北1（青岛）：https://oss-cn-qingdao.aliyuncs.com
    # 华北2（北京）：https://oss-cn-beijing.aliyuncs.com
    # 华北3（张家口）：https://oss-cn-zhangjiakou.aliyuncs.com
    # 华南1（深圳）：https://oss-cn-shenzhen.aliyuncs.com
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    
    # 访问密钥ID（AccessKey ID）
    # 从第三步获取
    access-key-id: LTAI5tXXXXXXXXXXXXXX
    
    # 访问密钥（AccessKey Secret）
    # 从第三步获取
    access-key-secret: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    
    # 存储空间名称（Bucket Name）
    # 从第二步创建的Bucket名称
    bucket-name: carbon-point-system-123456
    
    # 文件存储目录前缀（可选）
    # 所有文件都会存储在这个目录下
    prefix: carbon-system
    
    # 自定义域名（可选）
    # 如果你配置了CDN加速域名，填写在这里
    # 例如：cdn.yourdomain.com
    custom-domain:
```

### 2. 配置示例

**示例1：使用OSS默认域名**
```yaml
aliyun:
  oss:
    enabled: true
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    access-key-id: LTAI5tXXXXXXXXXXXXXX
    access-key-secret: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    bucket-name: carbon-point-system-123456
    prefix: carbon-system
    custom-domain:
```

**示例2：使用自定义域名**
```yaml
aliyun:
  oss:
    enabled: true
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    access-key-id: LTAI5tXXXXXXXXXXXXXX
    access-key-secret: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
    bucket-name: carbon-point-system-123456
    prefix: carbon-system
    custom-domain: cdn.yourdomain.com
```

**示例3：暂时使用本地存储**
```yaml
aliyun:
  oss:
    enabled: false  # 设置为false，使用本地存储
    # 其他配置可以不填
```

## 五、测试上传

### 1. 启动项目

```bash
cd server
mvn spring-boot:run
```

### 2. 测试上传头像

使用Postman或curl测试：

```bash
curl -X POST "http://localhost:8080/api/upload/avatar" \
  -H "Authorization: Bearer {你的token}" \
  -F "file=@/path/to/your/image.jpg"
```

**预期响应**：
```json
{
  "code": 200,
  "message": "上传成功",
  "data": "https://carbon-point-system-123456.oss-cn-hangzhou.aliyuncs.com/carbon-system/avatar/2026-01-16/abc123def456.jpg"
}
```

### 3. 验证文件

1. 复制返回的URL
2. 在浏览器中打开
3. 应该能看到上传的图片

## 六、文件上传接口

### 1. 上传用户头像

**接口**：`POST /api/upload/avatar`

**权限**：需要登录

**参数**：
- `file`：图片文件（multipart/form-data）

**支持格式**：jpg、jpeg、png、gif、bmp、webp

**文件大小限制**：5MB

**存储路径**：`{prefix}/avatar/{yyyy-MM-dd}/{uuid}.{ext}`

**示例**：
```bash
POST http://localhost:8080/api/upload/avatar
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: [选择图片文件]
```

### 2. 上传行为证明材料

**接口**：`POST /api/upload/behavior-proof`

**存储路径**：`{prefix}/behavior-proof/{yyyy-MM-dd}/{uuid}.{ext}`

### 3. 上传商品图片

**接口**：`POST /api/upload/product-image`

**存储路径**：`{prefix}/product/{yyyy-MM-dd}/{uuid}.{ext}`

### 4. 上传活动图片

**接口**：`POST /api/upload/activity-image`

**存储路径**：`{prefix}/activity/{yyyy-MM-dd}/{uuid}.{ext}`

## 七、常见问题

### Q1: 上传失败，提示"InvalidAccessKeyId"

**原因**：AccessKey ID或AccessKey Secret配置错误

**解决**：
1. 检查application.yml中的配置是否正确
2. 确认AccessKey是否有效
3. 确认RAM用户是否有OSS权限

### Q2: 上传成功但无法访问图片

**原因**：Bucket权限设置问题

**解决**：
1. 进入OSS控制台
2. 选择你的Bucket
3. 点击"权限管理" → "读写权限"
4. 设置为"公共读"

### Q3: 跨域问题

**原因**：未配置CORS规则

**解决**：按照"二、创建Bucket"中的第3步配置CORS规则

### Q4: 文件上传慢

**原因**：
1. Bucket地域选择不当
2. 网络问题

**解决**：
1. 选择离你最近的地域
2. 考虑配置CDN加速

### Q5: 如何配置自定义域名？

**步骤**：
1. 在OSS控制台绑定自定义域名
2. 配置CNAME解析
3. 在application.yml中填写custom-domain
4. 重启项目

### Q6: 如何切换回本地存储？

**方法**：
在application.yml中设置：
```yaml
aliyun:
  oss:
    enabled: false
```

## 八、安全建议

### 1. 使用RAM用户

不要使用主账号的AccessKey，创建专门的RAM用户

### 2. 最小权限原则

只授予必要的OSS权限，不要使用FullAccess

### 3. 定期轮换密钥

建议每3-6个月更换一次AccessKey

### 4. 不要提交密钥到代码仓库

将application.yml添加到.gitignore，或使用环境变量

### 5. 使用HTTPS

确保endpoint使用https://

### 6. 配置防盗链

在OSS控制台配置Referer白名单

## 九、费用说明

### 1. 计费项

- **存储费用**：按存储量计费
- **流量费用**：按下载流量计费
- **请求费用**：按API请求次数计费

### 2. 费用优化

- 使用CDN加速可以降低流量费用
- 定期清理不用的文件
- 使用生命周期规则自动删除过期文件

### 3. 费用预估

小型项目（1000用户）：
- 存储：约1GB = ¥0.12/月
- 流量：约10GB = ¥0.50/月
- 请求：约10万次 = ¥0.01/月
- **总计**：约¥0.63/月

## 十、监控和日志

### 1. 查看使用量

OSS控制台 → 概览 → 使用量统计

### 2. 查看访问日志

OSS控制台 → 日志管理 → 实时日志查询

### 3. 设置报警

OSS控制台 → 监控服务 → 报警规则

## 十一、参考资料

- [阿里云OSS官方文档](https://help.aliyun.com/product/31815.html)
- [OSS Java SDK文档](https://help.aliyun.com/document_detail/32008.html)
- [OSS定价说明](https://www.aliyun.com/price/product#/oss/detail)

---

**配置完成后，记得重启项目！**

如有问题，请查看后端日志或联系技术支持。
