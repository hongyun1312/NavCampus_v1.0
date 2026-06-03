<template>
  <el-row :gutter="12">
    <el-col :span="18">
      <el-card>
        <el-table :data="filtered">
          <el-table-column prop="createdAt" label="时间" width="180" />
          <el-table-column prop="title" label="标题" width="200" />
          <el-table-column prop="content" label="内容" />
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import http from '../api/http'

const list = ref([])
const filtered = computed(() => {
  return [...list.value].sort((a,b) => new Date(b.createdAt) - new Date(a.createdAt))
})

onMounted(async () => {
  const { data } = await http.get('/api/notifications')
  list.value = data
})
</script>
