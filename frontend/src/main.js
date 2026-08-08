/**
 * 前端入口：挂载 Vue 应用，注册路由与 UI 组件库。
 */
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 引入 Quill 富文本编辑器样式（用于记录备注富文本）
import 'quill/dist/quill.snow.css'
// 全局基础样式重置（盒模型、滚动条等）
import './style.css'
// 全局 UI 风格优化（不改动组件结构/逻辑）
import './styles/ui.css'

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
