<template>
  <div class="admin-container">
    <el-container>
      <el-aside width="200px">
        <el-menu
          default-active="1"
          class="el-menu-vertical-demo"
          router
        >
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/data">
            <el-icon><DataLine /></el-icon>
            <span>系统数据</span>
          </el-menu-item>
          <el-menu-item index="/admin/notifications">
            <el-icon><Bell /></el-icon>
            <span>通知发布</span>
          </el-menu-item>
          <el-menu-item index="/">
            <el-icon><Back /></el-icon>
            <span>返回前台</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { User, DataLine, Bell, Back } from '@element-plus/icons-vue'
</script>

<style scoped>
.admin-container {
  height: 100vh;
  display: flex;
}
.el-container {
  height: 100%;
  width: 100%;
}
.el-aside {
  background-color: #f5f7fa;
  border-right: 1px solid #e6e6e6;
}
</style>
