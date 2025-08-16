<template>
  <div class="p-4">
    <h1>用户管理界面诊断</h1>
    
    <el-card class="mb-4">
      <template #header>
        <span>路由检查</span>
      </template>
      <p>当前路由: {{ $route.path }}</p>
      <p>路由名称: {{ $route.name }}</p>
      <p>路由参数: {{ $route.params }}</p>
    </el-card>

    <el-card class="mb-4">
      <template #header>
        <span>API检查</span>
      </template>
      <el-button type="primary" @click="testUserAPI">测试用户API</el-button>
      <el-button type="success" @click="testDeptAPI">测试部门API</el-button>
      <el-button type="warning" @click="testProjectAPI">测试项目API</el-button>
      
      <div v-if="apiResults.length > 0" class="mt-4">
        <h3>API测试结果:</h3>
        <div v-for="result in apiResults" :key="result.id" class="mb-2">
          <el-tag :type="result.success ? 'success' : 'danger'">
            {{ result.name }}: {{ result.success ? '成功' : '失败' }}
          </el-tag>
          <p v-if="result.error" class="text-red-500 text-sm">{{ result.error }}</p>
        </div>
      </div>
    </el-card>

    <el-card class="mb-4">
      <template #header>
        <span>组件检查</span>
      </template>
      <p>Element Plus 版本: {{ elementPlusVersion }}</p>
      <p>Vue 版本: {{ vueVersion }}</p>
      <p>当前时间: {{ currentTime }}</p>
    </el-card>

    <el-card>
      <template #header>
        <span>错误日志</span>
      </template>
      <el-button type="danger" @click="clearErrors">清除错误</el-button>
      <div v-if="errors.length > 0" class="mt-4">
        <div v-for="error in errors" :key="error.id" class="mb-2 p-2 bg-red-50 border border-red-200 rounded">
          <p class="text-red-600 font-bold">{{ error.message }}</p>
          <p class="text-red-500 text-sm">{{ error.stack }}</p>
        </div>
      </div>
      <p v-else class="text-green-600">暂无错误</p>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import api from '@/api/system/user';
import { listProjectForOss } from '@/api/osc/project';
import { treeselect } from '@/api/system/dept';

const apiResults = ref<any[]>([]);
const errors = ref<any[]>([]);
const currentTime = ref(new Date().toLocaleString());

const elementPlusVersion = ref('未知');
const vueVersion = ref('未知');

// 获取版本信息
onMounted(() => {
  try {
    // 尝试获取版本信息
    if (window.Vue) {
      vueVersion.value = window.Vue.version || '未知';
    }
    if (window.ElementPlus) {
      elementPlusVersion.value = window.ElementPlus.version || '未知';
    }
  } catch (error) {
    console.error('获取版本信息失败:', error);
  }

  // 设置错误监听
  window.addEventListener('error', (event) => {
    errors.value.push({
      id: Date.now(),
      message: event.message,
      stack: event.error?.stack || '无堆栈信息'
    });
  });

  // 设置未处理的Promise拒绝监听
  window.addEventListener('unhandledrejection', (event) => {
    errors.value.push({
      id: Date.now(),
      message: `Promise拒绝: ${event.reason}`,
      stack: '无堆栈信息'
    });
  });
});

const testUserAPI = async () => {
  try {
    const response = await api.listUser({
      pageNum: 1,
      pageSize: 10
    });
    apiResults.value.push({
      id: Date.now(),
      name: '用户列表API',
      success: true,
      data: response
    });
  } catch (error) {
    apiResults.value.push({
      id: Date.now(),
      name: '用户列表API',
      success: false,
      error: error.message || error
    });
  }
};

const testDeptAPI = async () => {
  try {
    const response = await treeselect();
    apiResults.value.push({
      id: Date.now(),
      name: '部门树API',
      success: true,
      data: response
    });
  } catch (error) {
    apiResults.value.push({
      id: Date.now(),
      name: '部门树API',
      success: false,
      error: error.message || error
    });
  }
};

const testProjectAPI = async () => {
  try {
    const response = await listProjectForOss({
      pageNum: 1,
      pageSize: 10
    });
    apiResults.value.push({
      id: Date.now(),
      name: '项目列表API',
      success: true,
      data: response
    });
  } catch (error) {
    apiResults.value.push({
      id: Date.now(),
      name: '项目列表API',
      success: false,
      error: error.message || error
    });
  }
};

const clearErrors = () => {
  errors.value = [];
};
</script>
