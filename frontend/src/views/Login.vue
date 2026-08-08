<template>
  <div class="auth-page">
    <!-- 背景图片层 -->
    <div class="auth-bg"></div>
    <!-- 半透明遮罩层，增强文字可读性 -->
    <div class="auth-overlay"></div>

    <!-- 登录卡片 -->
    <el-card class="auth-card">
      <!-- 品牌区域 -->
      <div class="auth-brand">
        <div class="auth-logo">🏫</div>
        <h2 class="auth-title">NavCampus</h2>
        <p class="auth-subtitle">智慧校园导航平台</p>
      </div>

      <!-- 登录表单 -->
      <el-form :model="loginForm" label-width="80px" @submit.prevent="submitLogin" class="auth-form">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitLogin" :loading="loading" class="auth-submit-btn">
            登 录
          </el-button>
          <el-button link @click="goRegister" class="auth-register-btn">
            没有账号？立即注册 →
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 登录页面：用户输入用户名和密码，调用后端 /api/auth/signin 获取 JWT。
 * 登录成功后跳转至校园 3D 主页。
 */
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../api/http'
import { auth } from '../store/auth'

const router = useRouter()
const loginForm = reactive({ username: '', password: '' })
const loading = ref(false)

/** 提交登录表单 */
async function submitLogin() {
  loading.value = true
  try {
    const { data } = await http.post('/api/auth/signin', loginForm)
    localStorage.setItem('themeColor', '#3B82F6')
    auth.setToken(data.token)
    auth.setUser({ id: data.id, username: data.username, email: data.email, role: data.role })
    router.push({ name: 'campus' })
  } catch (e) {
    ElMessage.error('登录失败：' + (e.message || '用户名或密码错误'))
  } finally {
    loading.value = false
  }
}

/** 跳转到注册页面 */
function goRegister() {
  router.push({ name: 'register' })
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 背景图片：使用本地图片 */
.auth-bg {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image: url('/src/views/image/login.jpg');
  background-size: cover;
  background-position: center;
}

/* 半透明遮罩层 */
.auth-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(135deg, rgba(30, 41, 59, 0.6) 0%, rgba(59, 130, 246, 0.4) 100%);
}

/* 登录卡片 */
.auth-card {
  position: relative;
  z-index: 1;
  width: 440px;
  border-radius: 20px;
  border: none;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(20px);
  background: rgba(255, 255, 255, 0.95);
}

/* 品牌区域 */
.auth-brand {
  text-align: center;
  margin-bottom: 28px;
}

.auth-logo {
  font-size: 48px;
  line-height: 1;
  margin-bottom: 12px;
}

.auth-title {
  font-size: 26px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 4px 0;
  letter-spacing: -0.5px;
}

.auth-subtitle {
  font-size: 14px;
  color: #64748b;
  margin: 0;
}

/* 表单 */
.auth-form {
  padding: 0 8px;
}

.auth-submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  margin-bottom: 8px;
}

.auth-register-btn {
  width: 100%;
  text-align: center;
  font-size: 13px;
}
</style>
