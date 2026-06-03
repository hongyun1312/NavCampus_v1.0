<template>
  <el-container style="height: 100vh;">
    <el-aside width="200px" style="background-color: #304156;">
      <div class="admin-logo">管理员系统</div>
      <el-menu
        router
        :default-active="$route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        style="border-right: none;"
      >
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/data">
          <el-icon><DataLine /></el-icon>
          <span>数据管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/notifications">
          <el-icon><Bell /></el-icon>
          <span>通知发布</span>
        </el-menu-item>
        <el-menu-item index="/admin/study-room">
          <el-icon><Reading /></el-icon>
          <span>自习室管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container>
      <el-header style="border-bottom: 1px solid #eee; display: flex; align-items: center; justify-content: space-between;">
        <div style="font-weight: bold;">{{ routeName }}</div>
        <div>
           <el-button type="primary" link @click="$router.push('/campus')">返回前台</el-button>
           <el-button type="danger" link @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main style="background-color: #f0f2f5;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, DataLine, Bell, Reading } from '@element-plus/icons-vue'
import http from '../../api/http'
import { auth } from '../../store/auth'

const route = useRoute()
const router = useRouter()
const routeName = computed(() => {
  if (route.path.includes('users')) return '用户管理'
  if (route.path.includes('data')) return '数据管理'
  if (route.path.includes('notifications')) return '通知发布'
  if (route.path.includes('study-room')) return '自习室管理'
  return '管理后台'
})

async function logout() {
  try {
    await http.post('/api/auth/signout')
  } catch {}
  auth.clear()
  router.push({ name: 'login' })
}
</script>

<style scoped>
.admin-logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-weight: bold;
  font-size: 18px;
  border-bottom: 1px solid #1f2d3d;
}
</style>
