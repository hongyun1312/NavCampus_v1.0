<template>
  <div class="app" :style="{ '--theme-color': themeColor }">
    <template v-if="!isAuthPage && !isAdminPage">
      <el-container class="main-layout">
        <!-- ============ 顶部导航栏 ============ -->
        <el-header class="top-header" height="60px">
          <!-- 品牌区域：点击返回校园首页 -->
          <div class="brand-area" @click="$router.push('/campus')">
            <div class="brand-logo">🏫</div>
            <div class="brand-text">
              <span class="brand-name">NavCampus</span>
              <span class="brand-subtitle">智慧校园导航平台</span>
            </div>
          </div>

          <!-- 顶部导航菜单 -->
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
            </el-menu>
          </div>

          <!-- 右侧操作区：管理员入口 + 主题切换 + 用户下拉菜单 -->
          <div class="header-right">
            <el-button
              v-if="isAdmin && !isAdminPage"
              type="danger"
              link
              @click="$router.push('/admin')"
            >
              <el-icon><Setting /></el-icon>&nbsp;管理后台
            </el-button>

            <div class="header-divider" v-if="isAdmin && !isAdminPage"></div>

            <!-- 主题颜色选择器 -->
            <div class="theme-switch-wrapper">
              <el-icon class="theme-icon"><Brush /></el-icon>
              <ThemeSwitcher v-model:color="themeColor" />
            </div>

            <!-- 用户信息下拉菜单 -->
            <el-dropdown trigger="click" @command="handleUserCommand">
              <div class="user-info">
                <el-avatar :size="32" class="user-avatar">{{ userInitial }}</el-avatar>
                <span class="user-name">{{ userName }}</span>
                <el-icon class="arrow-icon"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>&nbsp;个人资料
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>&nbsp;系统设置
                  </el-dropdown-item>
                  <el-dropdown-item command="preferences">
                    <el-icon><Brush /></el-icon>&nbsp;偏好设置
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>&nbsp;退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <el-container class="body-container">
          <!-- ============ 动态侧边栏 ============ -->
          <el-aside width="220px" class="dynamic-sidebar" v-if="showSidebar">

            <!-- 场景工具箱 (仅在3D校园页面显示) -->
            <div v-if="isCampusPage" class="scene-toolbox">
              <div class="sidebar-header">
                <el-icon><Position /></el-icon>
                <span>场景工具箱</span>
              </div>

              <div class="toolbox-card">
                <div class="card-title">图层控制</div>
                <div class="layer-toggles">
                  <el-checkbox v-model="campusState.showCourses" label="今日课程" border size="small" />
                  <el-checkbox v-model="campusState.showSpending" label="消费热点" border size="small" />
                  <el-checkbox v-model="campusState.showWeather" label="天气云图" border size="small" />
                </div>
              </div>

              <div class="toolbox-card">
                <div class="card-title">快速操作</div>
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
              <div class="sidebar-header">
                <el-icon><Wallet /></el-icon>
                <span>校园财务</span>
              </div>
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
              <div class="sidebar-header">
                <el-icon><Reading /></el-icon>
                <span>学习与生活</span>
              </div>
              <el-menu router :default-active="route.path" class="sidebar-menu">
                <el-menu-item index="/timetable"><el-icon><Calendar /></el-icon><span>我的课表</span></el-menu-item>
                <el-menu-item index="/weather"><el-icon><Sunny /></el-icon><span>实时天气</span></el-menu-item>
                <el-menu-item index="/study-room"><el-icon><Reading /></el-icon><span>自习室预约</span></el-menu-item>
              </el-menu>
            </div>

          </el-aside>

          <!-- ============ 主内容区 ============ -->
          <el-main class="main-content">
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </template>

    <!-- 认证页面（登录/注册）和管理员页面：全屏显示，不套用主布局 -->
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
  CreditCard, Collection, Upload, Sunny, Reading,
  ArrowDown, Setting, SwitchButton, User, Brush
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

// 当前登录用户信息（用于顶部导航栏头像与用户名展示）
const currentUser = computed(() => auth.getUser())
const userName = computed(() => currentUser.value?.username || '未登录')
const userInitial = computed(() => {
  const name = userName.value
  return name ? name.charAt(0).toUpperCase() : 'U'
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

// 用户下拉菜单命令处理
function handleUserCommand(command) {
  if (command === 'logout') {
    logout()
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'settings') {
    router.push('/settings')
  } else if (command === 'preferences') {
    router.push('/preferences')
  }
}

</script>

<style>
/* ============ 全局重置 ============ */
body { margin: 0; padding: 0; }

.app {
  --theme-color: #3B82F6;
}

.main-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ============ 顶部导航栏 ============ */
.top-header {
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06), 0 1px 2px rgba(0, 0, 0, 0.04);
  z-index: 100;
  height: 60px !important;
}

/* 品牌区域 */
.brand-area {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  margin-right: 32px;
  transition: opacity 0.2s;
}
.brand-area:hover {
  opacity: 0.85;
}

.brand-logo {
  font-size: 28px;
  line-height: 1;
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-name {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: -0.5px;
}

.brand-subtitle {
  font-size: 11px;
  color: #64748b;
  margin-top: 2px;
}

/* 导航菜单 */
.nav-menu-container {
  flex: 1;
}

.top-menu {
  border-bottom: none !important;
  background: transparent !important;
}

.top-menu .el-menu-item,
.top-menu .el-sub-menu__title {
  font-size: 14px;
  font-weight: 500;
  height: 60px;
  line-height: 60px;
}

/* 右侧操作区 */
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-divider {
  width: 1px;
  height: 24px;
  background: #e2e8f0;
}

.theme-switch-wrapper {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 8px;
  border-radius: 8px;
  background: #f1f5f9;
  transition: background 0.2s;
}
.theme-switch-wrapper:hover {
  background: #e2e8f0;
}
.theme-icon {
  font-size: 14px;
  color: #64748b;
}

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px 4px 4px;
  border-radius: 24px;
  background: #f1f5f9;
  transition: background 0.2s;
}
.user-info:hover {
  background: #e2e8f0;
}

.user-avatar {
  background: var(--theme-color) !important;
  color: #fff !important;
  font-weight: 600;
  font-size: 14px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}

.arrow-icon {
  font-size: 12px;
  color: #64748b;
}

/* ============ 主体区域 ============ */
.body-container {
  flex: 1;
  overflow: hidden;
}

/* 侧边栏 */
.dynamic-sidebar {
  background: #f8fafc;
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  padding: 16px 12px;
  overflow-y: auto;
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 12px;
}

/* 工具箱卡片 */
.toolbox-card {
  background: #fff;
  border-radius: 10px;
  padding: 14px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  border: 1px solid #f1f5f9;
}

.card-title {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 10px;
  font-weight: 500;
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

/* 侧边栏菜单 */
.sidebar-menu {
  border-right: none !important;
  background-color: transparent !important;
}

.sidebar-menu .el-menu-item {
  border-radius: 8px;
  margin-bottom: 4px;
  height: 44px;
  line-height: 44px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #e2e8f0 !important;
}

.sidebar-menu .el-menu-item.is-active {
  background: var(--theme-color) !important;
  color: #fff !important;
}

.sidebar-menu .el-menu-item.is-active .el-icon {
  color: #fff !important;
}

/* 主内容区 */
.main-content {
  padding: 0;
  background: #fff;
  overflow: hidden;
  position: relative;
}

/* ============ Element Plus 覆盖 ============ */
.el-menu--horizontal > .el-menu-item:hover,
.el-menu--horizontal > .el-sub-menu__title:hover {
  background-color: #f1f5f9 !important;
}
</style>