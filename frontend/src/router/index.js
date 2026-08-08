/**
 * 应用路由：定义各功能页面与守卫（未登录跳转登录页）。
 * 使用 Hash 模式以支持直接打开 index.html（无需开发服务器历史回退）。
 */
import { createRouter, createWebHashHistory } from 'vue-router'
import { auth } from '../store/auth'

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
  { path: '/register', name: 'register', component: () => import('../views/Register.vue') },
  { path: '/', redirect: { name: 'campus' } },
  { path: '/campus', name: 'campus', component: () => import('../views/Campus3D.vue') },
  { path: '/timetable', name: 'timetable', component: () => import('../views/Timetable.vue') },
  { path: '/study-room', name: 'study-room', component: () => import('../views/StudyRoom.vue') },
  { path: '/dashboard', name: 'dashboard', component: () => import('../views/Dashboard.vue') },
  { path: '/records', name: 'records', component: () => import('../views/Records.vue') },
  { path: '/categories', name: 'categories', component: () => import('../views/Categories.vue') },
  { path: '/accounts', name: 'accounts', component: () => import('../views/Accounts.vue') },
  { path: '/budgets', name: 'budgets', component: () => import('../views/Budgets.vue') },
  { path: '/import', name: 'import', component: () => import('../views/Import.vue') },
  { path: '/notifications', name: 'notifications', component: () => import('../views/Notifications.vue') },
  { path: '/weather', name: 'weather', component: () => import('../views/Weather.vue') },
  { path: '/campus-account', name: 'campus-account', component: () => import('../views/CampusAccount.vue') },
  { path: '/profile', name: 'profile', component: () => import('../views/Profile.vue') },
  { path: '/settings', name: 'settings', component: () => import('../views/Settings.vue') },
  { path: '/preferences', name: 'preferences', component: () => import('../views/Preferences.vue') },
  // Admin Routes
  { 
    path: '/admin', 
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/users' },
      { path: 'users', component: () => import('../views/admin/UserManagement.vue') },
      { path: 'data', component: () => import('../views/admin/DataManagement.vue') },
      { path: 'notifications', component: () => import('../views/admin/NotificationManagement.vue') },
      { path: 'study-room', component: () => import('../views/admin/StudyRoomManagement.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (['login', 'register'].includes(to.name)) return next()
  if (!auth.getToken() || auth.isTokenExpired()) {
    auth.clear()
    return next({ name: 'login' })
  }

  if (to.matched.some(record => record.meta.requiresAdmin)) {
    const user = auth.getUser()
    if (user.role !== 'ADMIN') {
      return next({ name: 'campus' })
    }
  }

  next()
})

export default router
