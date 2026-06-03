<template>
  <div
    class="auth-page"
    style="background-image:
      url('http://www.mbfsr.com/login.jpg');
      background-size: cover; background-position: center;"
  >
    <el-card class="auth-card">
      <h3 class="title">注册</h3>
      <el-form :model="form" label-width="80px" @submit.prevent="submit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item>
          <el-space>
            <el-button type="primary" @click="submit" :loading="loading">注册</el-button>
            <el-button link @click="goLogin">返回登录</el-button>
          </el-space>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 注册页逻辑：调用 /api/auth/signup，成功后跳转登录。
 */
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const router = useRouter()
const form = reactive({ username: '', password: '' })
const loading = ref(false)

async function submit() {
  loading.value = true
  try {
    await http.post('/api/auth/signup', form)
    ElMessage.success('注册成功，请登录')
    router.push({ name: 'login' })
  } catch (e) {
    if (e.response && e.response.data) {
      // 显示后端返回的具体错误信息，例如 "Error: Username is already taken!"
      ElMessage.error(e.response.data)
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}
function goLogin() {
  router.push({ name: 'login' })
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
