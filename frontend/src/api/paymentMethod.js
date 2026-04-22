import request from './request'

// 查询所有支付方式
export function getPaymentMethods(type) {
  return request({
    url: '/api/payment-methods',
    method: 'get',
    params: { type }
  })
}


