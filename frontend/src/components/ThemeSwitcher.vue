<template>
  <!-- 主题颜色选择器组件 -->
  <el-color-picker v-model="colorInner" :predefine="presets" @change="updateTheme" />
</template>

<script setup>
/**
 * 主题切换组件：通过 v-model:color 同步颜色。
 * 实时更新 Element Plus 全局 CSS 变量。
 */
import { computed, onMounted, watch } from 'vue'

const props = defineProps({ color: { type: String, default: '#409EFF' } })
const emit = defineEmits(['update:color'])

const colorInner = computed({
  get: () => props.color,
  set: (v) => emit('update:color', v)
})

const presets = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#3F3F46']

// 简单的颜色混合算法，模拟 Element Plus 的 mix
// color: 原色, mixColor: 混合色 (white/black), weight: 权重
const mix = (color1, color2, weight) => {
  weight = Math.max(Math.min(Number(weight), 1), 0)
  const r1 = parseInt(color1.substring(1, 3), 16)
  const g1 = parseInt(color1.substring(3, 5), 16)
  const b1 = parseInt(color1.substring(5, 7), 16)
  const r2 = parseInt(color2.substring(1, 3), 16)
  const g2 = parseInt(color2.substring(3, 5), 16)
  const b2 = parseInt(color2.substring(5, 7), 16)
  const r = Math.round(r1 * (1 - weight) + r2 * weight)
  const g = Math.round(g1 * (1 - weight) + g2 * weight)
  const b = Math.round(b1 * (1 - weight) + b2 * weight)
  const _r = ('0' + (r || 0).toString(16)).slice(-2)
  const _g = ('0' + (g || 0).toString(16)).slice(-2)
  const _b = ('0' + (b || 0).toString(16)).slice(-2)
  return '#' + _r + _g + _b
}

const updateTheme = (val) => {
  if (!val) return
  const el = document.documentElement
  // 主色
  el.style.setProperty('--el-color-primary', val)
  // 生成色阶 light-3, light-5, light-7, light-8, light-9, dark-2
  for (let i = 1; i <= 9; i++) {
    el.style.setProperty(`--el-color-primary-light-${i}`, mix(val, '#ffffff', i * 0.1))
  }
  el.style.setProperty(`--el-color-primary-dark-2`, mix(val, '#000000', 0.2))
}

// 初始加载
onMounted(() => {
  updateTheme(props.color)
})

watch(() => props.color, (newVal) => {
  updateTheme(newVal)
})
</script>

