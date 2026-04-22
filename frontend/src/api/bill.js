import request from './request'

// 分页查询账单列表
export function getBillList(params) {
  return request({
    url: '/api/bills',
    method: 'get',
    params
  })
}

// 根据ID查询账单
export function getBillById(id) {
  return request({
    url: `/api/bills/${id}`,
    method: 'get'
  })
}

// 新增账单
export function createBill(data) {
  return request({
    url: '/api/bills',
    method: 'post',
    data
  })
}

// 更新账单
export function updateBill(id, data) {
  return request({
    url: `/api/bills/${id}`,
    method: 'put',
    data
  })
}

// 删除账单
export function deleteBill(id) {
  return request({
    url: `/api/bills/${id}`,
    method: 'delete'
  })
}

// 获取统计信息
export function getStatistics(params) {
  return request({
    url: '/api/bills/statistics',
    method: 'get',
    params
  })
}

