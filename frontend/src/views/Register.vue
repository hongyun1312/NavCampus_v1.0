<template>
  <div class="auth-page">
    <!-- 背景图片层 -->
    <div class="auth-bg"></div>
    <!-- 半透明遮罩层，增强文字可读性 -->
    <div class="auth-overlay"></div>

    <!-- 注册卡片 -->
    <el-card class="auth-card">
      <!-- 品牌区域 -->
      <div class="auth-brand">
        <div class="auth-logo">🏫</div>
        <h2 class="auth-title">注册新账号</h2>
        <p class="auth-subtitle">加入 NavCampus 智慧校园导航平台</p>
      </div>

      <!-- 注册表单 -->
      <el-form :model="form" label-width="80px" @submit.prevent="submit" class="auth-form">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit" :loading="loading" class="auth-submit-btn">
            注 册
          </el-button>
          <el-button link @click="goLogin" class="auth-register-btn">
            已有账号？返回登录 ->
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 注册页面：用户输入用户名和密码，调用后端 /api/auth/signup 注册账号。
 * 注册成功后跳转至登录页面。
 */
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const router = useRouter()
const form = reactive({ username: '', password: '' })
const loading = ref(false)

/** 提交注册表单 */
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

/** 返回登录页面 */
function goLogin() {
  router.push({ name: 'login' })
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

/* 注册卡片 */
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
