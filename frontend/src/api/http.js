/**
 * Axios 封装：设置基础地址、JWT 拦截与自动刷新。
 */
import axios from 'axios'
import { auth } from '../store/auth'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8080',
  timeout: 10000
})

// 请求拦截：添加 JWT
http.interceptors.request.use((config) => {
  const token = auth.getToken()
  if (token) config.headers['Authorization'] = `Bearer ${token}`
  return config
})

let isRefreshing = false
let pendingRequests = []
http.interceptors.response.use(
  (res) => res,
  async (error) => {
    const { response, config } = error || {}
    if (response && response.status === 401 && !config.__retry && !config.url.includes('/api/auth/signin') && !config.url.includes('/api/auth/signup') && !config.url.includes('/api/auth/refresh')) {
      if (!isRefreshing) {
        isRefreshing = true
        try {
          const refreshRes = await http.post('/api/auth/refresh')
          auth.setToken(refreshRes.data.token)
          pendingRequests.forEach((cb) => cb())
          pendingRequests = []
        } catch {
          // 刷新失败：拒绝所有挂起请求并回到登录
          pendingRequests.forEach((cb) => cb(new Error('refresh_failed')))
          pendingRequests = []
          auth.clear()
          window.location.hash = '#/login'
        } finally {
          isRefreshing = false
        }
      }
      return new Promise((resolve) => {
        pendingRequests.push((err) => {
          if (err) return resolve(Promise.reject(error))
          config.__retry = true
          resolve(http(config))
        })
      })
    }
    return Promise.reject(error)
  }
)

export default http
