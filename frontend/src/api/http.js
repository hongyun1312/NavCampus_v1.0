/**
 * Axios 封装：设置基础地址、JWT 拦截与自动刷新。
 * 适配后端统一返回格式 R<T> = { code, msg, data }
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

// 响应拦截：解包 R<T> 格式 + 自动刷新 Token
http.interceptors.response.use(
  (res) => {
    // 文件下载等非 JSON 响应直接返回
    const contentType = res.headers['content-type'] || ''
    if (!contentType.includes('application/json')) {
      return res
    }

    // 解包 R<T> 格式
    const body = res.data
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === 200) {
        // 成功：用 data 字段替换整个 body，使后续代码无感
        res.data = body.data
        return res
      } else {
        // 业务失败
        return Promise.reject(new Error(body.msg || '操作失败'))
      }
    }

    // 非 R<T> 格式的 JSON 响应直接返回
    return res
  },
  async (error) => {
    const { response, config } = error || {}

    // 401 自动刷新 Token
    if (response && response.status === 401 && !config.__retry
        && !config.url.includes('/api/auth/signin')
        && !config.url.includes('/api/auth/signup')
        && !config.url.includes('/api/auth/refresh')) {
      if (!isRefreshing) {
        isRefreshing = true
        try {
          const refreshRes = await http.post('/api/auth/refresh')
          auth.setToken(refreshRes.data.token)
          pendingRequests.forEach((cb) => cb())
          pendingRequests = []
        } catch {
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

    // 提取 R<T> 格式的错误信息
    if (response && response.data && typeof response.data === 'object' && 'msg' in response.data) {
      return Promise.reject(new Error(response.data.msg))
    }

    return Promise.reject(error)
  }
)

export default http