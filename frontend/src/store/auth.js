/**
 * 简易认证存储：管理 JWT 与用户信息。
 * 使用 Vue reactive 实现响应式状态。
 */
import { reactive } from 'vue'

const state = reactive({
  token: localStorage.getItem('token') || '',
  user: JSON.parse(localStorage.getItem('user') || '{}')
})

function decodeBase64Url(input) {
  const base64 = String(input).replace(/-/g, '+').replace(/_/g, '/')
  const pad = base64.length % 4 === 0 ? '' : '='.repeat(4 - (base64.length % 4))
  return atob(base64 + pad)
}

function parseJwtPayload(token) {
  try {
    const parts = String(token || '').split('.')
    if (parts.length !== 3) return null
    const json = decodeBase64Url(parts[1])
    return JSON.parse(json)
  } catch {
    return null
  }
}

export const auth = {
  getToken() {
    return state.token
  },
  setToken(token) {
    state.token = token
    if (token) {
      localStorage.setItem('token', token)
    } else {
      localStorage.removeItem('token')
    }
  },
  setUser(user) {
    state.user = user || {}
    localStorage.setItem('user', JSON.stringify(state.user))
  },
  getUser() {
    return state.user
  },
  getTokenPayload() {
    return parseJwtPayload(state.token)
  },
  isTokenExpired() {
    const payload = parseJwtPayload(state.token)
    const exp = payload && payload.exp
    if (!exp) return false
    return Date.now() >= exp * 1000 - 5000
  },
  clear() {
    state.token = ''
    state.user = {}
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
}
