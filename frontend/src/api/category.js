import request from './request'

// 查询所有分类
export function getCategories(type) {
  return request({
    url: '/api/categories',
    method: 'get',
    params: { type }
  })
}

