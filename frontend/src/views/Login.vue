<template>
  <div
    class="auth-page"
    style="background-image:
      url('http://www.mbfsr.com/login.jpg');
      background-size: cover; background-position: center;"
  >
    <el-card class="auth-card">
      <h3 class="title">登录</h3>
      <el-form :model="loginForm" label-width="80px" @submit.prevent="submitLogin">
        <el-form-item label="用户名">
          <el-input v-model="loginForm.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="loginForm.password" type="password" />
        </el-form-item>
        <el-form-item>
          <el-space>
            <el-button type="primary" @click="submitLogin" :loading="loading">登录</el-button>
            <el-button link @click="goRegister">立即注册</el-button>
          </el-space>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../api/http'
import { auth } from '../store/auth'

const router = useRouter()
const loginForm = reactive({ username: '', password: '' })
const loading = ref(false)

async function submitLogin() {
  loading.value = true
  try {
    const { data } = await http.post('/api/auth/signin', loginForm)
    localStorage.setItem('themeColor', '#3F3F46')
    auth.setToken(data.token)
    auth.setUser({ id: data.id, username: data.username, email: data.email, role: data.role })
    router.push({ name: 'campus' })
  } catch (e) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}
function goRegister() {
  router.push({ name: 'register' })
}
</script>

<style>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-size: cover;
  background-position: center;
}
.auth-card {
  width: 520px;
  backdrop-filter: blur(6px);
  background-color: rgba(255,255,255,0.85);
  border-radius: 12px;
}
.title {
  margin-bottom: 12px;
  font-size: 22px;
}
</style>
