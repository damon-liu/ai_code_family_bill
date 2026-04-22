import { createRouter, createWebHistory } from 'vue-router'
import BillList from '../views/BillList.vue'

const routes = [
  {
    path: '/',
    name: 'BillList',
    component: BillList
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router


