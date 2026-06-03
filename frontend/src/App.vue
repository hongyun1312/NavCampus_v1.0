<template>
  <div class="app" :style="{ '--theme-color': themeColor }">
    <template v-if="!isAuthPage && !isAdminPage">
      <el-container class="main-layout">
        <!-- 顶部导航栏 -->
        <el-header class="top-header">
          <div class="brand-area">
            <span class="logo-text">🏫 数字孪生校园</span>
          </div>
          
          <div class="nav-menu-container">
            <el-menu
              mode="horizontal"
              router
              :default-active="activeTopMenu"
              class="top-menu"
              :ellipsis="false"
            >
              <el-menu-item index="/campus">探索校园</el-menu-item>
              
              <el-sub-menu index="study">
                <template #title>学习与生活</template>
                <el-menu-item index="/timetable">我的课表</el-menu-item>
                <el-menu-item index="/weather">实时天气</el-menu-item>
                <el-menu-item index="/study-room">自习室预约</el-menu-item>
              </el-sub-menu>
              
              <el-menu-item index="/dashboard">校园财务</el-menu-item>
              
              <el-menu-item index="/notifications">校园通知</el-menu-item>
              
              <el-sub-menu index="settings">
                <template #title>设置中心</template>
                <el-menu-item index="/profile">个人资料</el-menu-item>
                <el-menu-item index="/settings">系统设置</el-menu-item>
                <el-menu-item index="/preferences">偏好设置</el-menu-item>
              </el-sub-menu>
            </el-menu>
          </div>

          <div class="header-right">
            <el-button 
              v-if="isAdmin && !isAdminPage" 
              type="danger" 
              link 
              @click="$router.push('/admin')"
              style="margin-right: 12px"
            >
              切换到管理员模式
            </el-button>
            <ThemeSwitcher v-model:color="themeColor" />
            <el-button link @click="logout" style="margin-left: 12px">退出</el-button>
          </div>
        </el-header>

        <el-container class="body-container">
          <!-- 动态侧边栏 -->
          <el-aside width="240px" class="dynamic-sidebar" v-if="showSidebar">
            
            <!-- 场景工具箱 (仅在3D校园显示) -->
            <div v-if="isCampusPage" class="scene-toolbox">
              <div class="sidebar-header">场景工具箱</div>
              
              <div class="toolbox-section">
                <div class="section-title">图层控制</div>
                <div class="layer-toggles">
                  <el-checkbox v-model="campusState.showCourses" label="今日课程" border size="small" />
                  <el-checkbox v-model="campusState.showSpending" label="消费热点" border size="small" />
                  <el-checkbox v-model="campusState.showWeather" label="天气云图" border size="small" />
                </div>
              </div>

              <div class="toolbox-section">
                <div class="section-title">快速操作</div>
                <div class="quick-actions">
                  <el-button @click="triggerAction('quickRecord')" circle title="一键记账">
                    <el-icon><Money /></el-icon>
                  </el-button>
                  <el-button @click="triggerAction('viewSchedule')" circle title="今日课表">
                    <el-icon><Calendar /></el-icon>
                  </el-button>
                </div>
              </div>

            </div>

            <!-- 财务菜单 -->
            <div v-else-if="isFinancePage" class="finance-menu">
              <div class="sidebar-header">校园财务</div>
              <el-menu router :default-active="route.path" class="sidebar-menu">
                <el-menu-item index="/dashboard"><el-icon><Odometer /></el-icon><span>仪表盘</span></el-menu-item>
                <el-menu-item index="/records"><el-icon><List /></el-icon><span>收支明细</span></el-menu-item>
                <el-menu-item index="/budgets"><el-icon><Wallet /></el-icon><span>预算管理</span></el-menu-item>
                <el-menu-item index="/accounts"><el-icon><CreditCard /></el-icon><span>账户资产</span></el-menu-item>
                <el-menu-item index="/categories"><el-icon><Collection /></el-icon><span>分类设置</span></el-menu-item>
                <el-menu-item index="/import"><el-icon><Upload /></el-icon><span>账单导入</span></el-menu-item>
              </el-menu>
            </div>

             <!-- 学习菜单 -->
             <div v-else-if="isStudyPage" class="study-menu">
               <div class="sidebar-header">学习与生活</div>
               <el-menu router :default-active="route.path" class="sidebar-menu">
                 <el-menu-item index="/timetable"><el-icon><Calendar /></el-icon><span>我的课表</span></el-menu-item>
                 <el-menu-item index="/weather"><el-icon><Sunny /></el-icon><span>实时天气</span></el-menu-item>
                 <el-menu-item index="/study-room"><el-icon><Reading /></el-icon><span>自习室预约</span></el-menu-item>
               </el-menu>
             </div>

          </el-aside>

          <!-- 主内容区 -->
          <el-main class="main-content">
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </template>
    
    <template v-else>
      <router-view />
    </template>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ThemeSwitcher from './components/ThemeSwitcher.vue'
import http from './api/http'
import { auth } from './store/auth'
import { campusState } from './store/campus'
import { 
  Money, Calendar, Position, Location, Odometer, List, Wallet, 
  CreditCard, Collection, Upload, Sunny, Reading 
} from '@element-plus/icons-vue'

const themeColor = ref(localStorage.getItem('themeColor') || '#3F3F46')
watch(themeColor, (c) => localStorage.setItem('themeColor', c))
const route = useRoute()
const router = useRouter()
const isAuthPage = computed(() => ['login', 'register'].includes(String(route.name || '')))
const isAdminPage = computed(() => route.path.startsWith('/admin'))
const isAdmin = computed(() => {
  const user = auth.getUser()
  return user && user.role === 'ADMIN'
})

async function logout() {
  try {
    await http.post('/api/auth/signout')
  } catch {}
  auth.clear()
  router.push({ name: 'login' })
}

watch(() => route.name, (n) => {
  if (n && !['login', 'register'].includes(String(n))) {
    const stored = localStorage.getItem('themeColor')
    themeColor.value = stored || '#3F3F46'
  }
})

// --- 导航逻辑 ---

// 判断当前所在的大类
const activeTopMenu = computed(() => {
  const p = route.path
  if (p.startsWith('/campus')) return '/campus'
  if (p.startsWith('/timetable') || p.startsWith('/weather') || p.startsWith('/study-room')) return 'study'
  if (['/dashboard', '/records', '/budgets', '/accounts', '/categories', '/import', '/campus-account'].some(x => p.startsWith(x))) return '/dashboard'
  if (p.startsWith('/notifications')) return '/notifications'
  if (['/profile', '/settings', '/preferences'].some(x => p.startsWith(x))) return 'settings'
  return '/campus'
})

const isCampusPage = computed(() => route.path === '/campus')
const isFinancePage = computed(() => ['/dashboard', '/records', '/budgets', '/accounts', '/categories', '/import'].some(path => route.path.startsWith(path)))
const isStudyPage = computed(() => ['/timetable', '/weather', '/study-room'].some(path => route.path.startsWith(path)))

const showSidebar = computed(() => isCampusPage.value || isFinancePage.value || isStudyPage.value)

function triggerAction(action) {
  campusState.actionTrigger = { type: action, time: Date.now() }
}

</script>

<style>
/* Reset */
body { margin: 0; padding: 0; }

.app {
  --theme-color: #409EFF;
}

.main-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Header */
.top-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  z-index: 100;
}

.brand-area {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-right: 40px;
  display: flex;
  align-items: center;
}
.logo-icon { margin-right: 8px; font-size: 24px; }

.nav-menu-container {
  flex: 1;
}

.top-menu {
  border-bottom: none !important;
}

.header-right {
  display: flex;
  align-items: center;
}

/* Body */
.body-container {
  flex: 1;
  overflow: hidden;
}

/* Sidebar */
.dynamic-sidebar {
  background-color: #f5f7fa;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 16px;
  font-size: 14px;
  font-weight: bold;
  color: #606266;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* Toolbox */
.scene-toolbox {
  padding: 0 16px;
}

.toolbox-section {
  margin-bottom: 24px;
  background: #fff;
  padding: 12px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.section-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
}

.layer-toggles {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.quick-actions {
  display: flex;
  justify-content: space-around;
}

.location-info {
  display: flex;
  align-items: center;
  color: var(--theme-color);
  font-weight: bold;
}
.location-text { margin-left: 8px; }

/* Menus */
.sidebar-menu {
  border-right: none !important;
  background-color: transparent !important;
}

.main-content {
  padding: 0;
  background-color: #fff;
  overflow: hidden; /* Canvas needs this */
  position: relative;
}

/* Overwrite element menu bg */
.el-menu--horizontal > .el-menu-item:hover { background-color: #f0f2f5 !important; }

</style>
