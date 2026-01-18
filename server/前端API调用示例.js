// 行为管理模块 API 调用示例
// 基于 axios 的封装

import request from '@/utils/request' // 假设你有一个封装好的 request 工具

// ==================== 用户端接口 ====================

/**
 * 获取行为规则列表
 * @param {Object} params - 查询参数
 * @param {Number} params.status - 规则状态：0-禁用，1-启用（可选）
 */
export const getBehaviorRules = (params) => {
  return request({
    url: '/api/behavior/rules',
    method: 'get',
    params
  })
}

/**
 * 获取行为规则详情
 * @param {Number} id - 规则ID
 */
export const getBehaviorRuleDetail = (id) => {
  return request({
    url: `/api/behavior/rules/${id}`,
    method: 'get'
  })
}

/**
 * 提交行为记录
 * @param {Object} data - 行为记录数据
 * @param {String} data.behaviorType - 行为类型
 * @param {Number} data.behaviorValue - 行为数值
 * @param {String} data.description - 行为描述（可选）
 * @param {String} data.proofImage - 证明材料图片URL，多张用逗号分隔（可选）
 */
export const submitBehaviorRecord = (data) => {
  return request({
    url: '/api/behavior/record',
    method: 'post',
    data
  })
}

/**
 * 获取我的行为记录列表
 * @param {Object} params - 查询参数
 * @param {String} params.behaviorType - 行为类型（可选）
 * @param {Number} params.auditStatus - 审核状态：0-待审核，1-通过，2-拒绝（可选）
 * @param {String} params.startTime - 开始时间 yyyy-MM-dd HH:mm:ss（可选）
 * @param {String} params.endTime - 结束时间 yyyy-MM-dd HH:mm:ss（可选）
 * @param {Number} params.pageNum - 页码，默认1
 * @param {Number} params.pageSize - 每页数量，默认10
 */
export const getBehaviorRecords = (params) => {
  return request({
    url: '/api/behavior/record',
    method: 'get',
    params
  })
}

/**
 * 获取行为记录详情
 * @param {Number} id - 记录ID
 */
export const getBehaviorRecordDetail = (id) => {
  return request({
    url: `/api/behavior/record/${id}`,
    method: 'get'
  })
}

/**
 * 删除行为记录（仅待审核状态）
 * @param {Number} id - 记录ID
 */
export const deleteBehaviorRecord = (id) => {
  return request({
    url: `/api/behavior/record/${id}`,
    method: 'delete'
  })
}

/**
 * 获取行为统计数据
 * @param {Object} params - 查询参数
 * @param {String} params.startTime - 开始时间 yyyy-MM-dd HH:mm:ss（可选）
 * @param {String} params.endTime - 结束时间 yyyy-MM-dd HH:mm:ss（可选）
 */
export const getBehaviorStatistics = (params) => {
  return request({
    url: '/api/behavior/statistics',
    method: 'get',
    params
  })
}

// ==================== 管理端接口 ====================

/**
 * 获取待审核行为记录列表（管理员）
 * @param {Object} params - 查询参数
 * @param {Number} params.userId - 用户ID（可选）
 * @param {String} params.behaviorType - 行为类型（可选）
 * @param {String} params.startTime - 开始时间 yyyy-MM-dd HH:mm:ss（可选）
 * @param {String} params.endTime - 结束时间 yyyy-MM-dd HH:mm:ss（可选）
 * @param {Number} params.pageNum - 页码，默认1
 * @param {Number} params.pageSize - 每页数量，默认10
 */
export const getAuditList = (params) => {
  return request({
    url: '/api/behavior/audit/list',
    method: 'get',
    params
  })
}

/**
 * 审核行为记录（管理员）
 * @param {Number} id - 记录ID
 * @param {Object} data - 审核数据
 * @param {Number} data.auditStatus - 审核状态：1-通过，2-拒绝
 * @param {String} data.auditRemark - 审核备注（拒绝时必填）
 */
export const auditBehaviorRecord = (id, data) => {
  return request({
    url: `/api/behavior/audit/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取所有行为记录（管理端）
 * @param {Object} params - 查询参数
 * @param {String} params.username - 用户名，模糊查询（可选）
 * @param {String} params.behaviorType - 行为类型（可选）
 * @param {Number} params.auditStatus - 审核状态：0-待审核，1-通过，2-拒绝（可选）
 * @param {String} params.startTime - 开始时间 yyyy-MM-dd HH:mm:ss（可选）
 * @param {String} params.endTime - 结束时间 yyyy-MM-dd HH:mm:ss（可选）
 * @param {Number} params.pageNum - 页码，默认1
 * @param {Number} params.pageSize - 每页数量，默认10
 */
export const getAdminRecords = (params) => {
  return request({
    url: '/api/behavior/admin/records',
    method: 'get',
    params
  })
}

/**
 * 创建行为规则（管理员）
 * @param {Object} data - 规则数据
 * @param {String} data.behaviorName - 行为名称
 * @param {String} data.behaviorType - 行为类型（英文标识）
 * @param {String} data.unit - 单位
 * @param {Number} data.carbonReductionPerUnit - 每单位减碳量(kg)
 * @param {Number} data.pointsPerUnit - 每单位积分
 * @param {Number} data.minValue - 最小数值限制（可选）
 * @param {Number} data.maxValue - 每日最大数值限制（可选）
 * @param {Number} data.maxPointsPerDay - 每日最大积分限制（可选）
 * @param {Number} data.needProof - 是否需要证明：0-否，1-是
 * @param {Number} data.sortOrder - 排序（可选）
 * @param {String} data.description - 规则说明（可选）
 */
export const createBehaviorRule = (data) => {
  return request({
    url: '/api/behavior/rules',
    method: 'post',
    data
  })
}

/**
 * 更新行为规则（管理员）
 * @param {Number} id - 规则ID
 * @param {Object} data - 规则数据（同创建接口）
 */
export const updateBehaviorRule = (id, data) => {
  return request({
    url: `/api/behavior/rules/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除行为规则（管理员）
 * @param {Number} id - 规则ID
 */
export const deleteBehaviorRule = (id) => {
  return request({
    url: `/api/behavior/rules/${id}`,
    method: 'delete'
  })
}

/**
 * 获取管理员统计数据
 * @param {Object} params - 查询参数
 * @param {String} params.startDate - 开始日期 yyyy-MM-dd（可选）
 * @param {String} params.endDate - 结束日期 yyyy-MM-dd（可选）
 */
export const getAdminStatistics = (params) => {
  return request({
    url: '/api/behavior/admin/statistics',
    method: 'get',
    params
  })
}

// ==================== 使用示例 ====================

// 示例1：用户提交行为记录
async function submitRecord() {
  try {
    const result = await submitBehaviorRecord({
      behaviorType: 'walking',
      behaviorValue: 5.5,
      description: '今天步行上班',
      proofImage: 'https://example.com/proof1.jpg'
    })
    console.log('提交成功', result)
  } catch (error) {
    console.error('提交失败', error)
  }
}

// 示例2：获取行为记录列表
async function getRecords() {
  try {
    const result = await getBehaviorRecords({
      pageNum: 1,
      pageSize: 10,
      auditStatus: 1 // 只查询审核通过的
    })
    console.log('记录列表', result.data)
  } catch (error) {
    console.error('查询失败', error)
  }
}

// 示例3：管理员审核记录
async function auditRecord() {
  try {
    const result = await auditBehaviorRecord(1, {
      auditStatus: 1,
      auditRemark: '审核通过'
    })
    console.log('审核成功', result)
  } catch (error) {
    console.error('审核失败', error)
  }
}

// 示例4：获取统计数据
async function getStatistics() {
  try {
    const result = await getBehaviorStatistics({
      startTime: '2024-01-01 00:00:00',
      endTime: '2024-12-31 23:59:59'
    })
    console.log('统计数据', result.data)
  } catch (error) {
    console.error('查询失败', error)
  }
}

// 示例5：创建行为规则
async function createRule() {
  try {
    const result = await createBehaviorRule({
      behaviorName: '骑行出行',
      behaviorType: 'cycling',
      unit: '公里',
      carbonReductionPerUnit: 0.08,
      pointsPerUnit: 8,
      minValue: 1.0,
      maxValue: 100.0,
      maxPointsPerDay: 300,
      needProof: 1,
      sortOrder: 2,
      description: '骑自行车代替开车出行'
    })
    console.log('创建成功', result)
  } catch (error) {
    console.error('创建失败', error)
  }
}
