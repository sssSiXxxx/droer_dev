<template>
  <div class="p-4">
    <h1>用户管理测试页面</h1>
    <el-card>
      <template #header>
        <span>基本信息</span>
      </template>
      <p>如果您能看到这个页面，说明路由配置正常。</p>
      <el-button type="primary" @click="testAPI">测试API调用</el-button>
      <div v-if="apiResult" class="mt-4">
        <p>API调用结果: {{ apiResult }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import api from '@/api/system/user';

const apiResult = ref('');

const testAPI = async () => {
  try {
    const response = await api.listUser({
      pageNum: 1,
      pageSize: 10
    });
    apiResult.value = `成功获取 ${response.rows?.length || 0} 条用户数据`;
  } catch (error) {
    apiResult.value = `API调用失败: ${error}`;
  }
};
</script>
