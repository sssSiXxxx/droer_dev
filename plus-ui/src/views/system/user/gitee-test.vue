<template>
  <div class="p-4">
    <h1>Gitee API 测试页面</h1>

    <el-card class="mb-4">
      <template #header>
        <span>Gitee 用户信息测试</span>
      </template>

      <el-form :inline="true">
        <el-form-item label="Gitee用户名">
          <el-input v-model="giteeUsername" placeholder="请输入Gitee用户名" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="testGiteeUserInfo" :loading="loading"> 获取用户信息 </el-button>
        </el-form-item>
      </el-form>

      <div v-if="userInfo" class="mt-4">
        <h3>用户信息:</h3>
        <pre>{{ JSON.stringify(userInfo, null, 2) }}</pre>
      </div>
    </el-card>

    <el-card class="mb-4">
      <template #header>
        <span>贡献记录测试</span>
      </template>

      <el-button type="success" @click="testContributions" :loading="loadingContributions"> 获取贡献记录 </el-button>

      <div v-if="contributions.length > 0" class="mt-4">
        <h3>贡献记录 ({{ contributions.length }} 条):</h3>
        <div v-for="contribution in contributions.slice(0, 10)" :key="contribution.id" class="mb-2 p-2 border rounded">
          <div class="text-sm"><strong>类型:</strong> {{ contribution.type }}</div>
          <div class="text-sm"><strong>仓库:</strong> {{ contribution.repo?.name }}</div>
          <div class="text-sm"><strong>时间:</strong> {{ formatDate(contribution.created_at) }}</div>
        </div>
      </div>
    </el-card>

    <el-card>
      <template #header>
        <span>贡献日历测试</span>
      </template>

      <ContributionCalendar :contributions="calendarData" :days-count="90" @day-click="handleDayClick" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { getGiteeUserInfo, getGiteeContributions } from '@/api/gitee';
import ContributionCalendar from '@/components/ContributionCalendar.vue';

const giteeUsername = ref('dromara');
const userInfo = ref(null);
const contributions = ref([]);
const loading = ref(false);
const loadingContributions = ref(false);

const testGiteeUserInfo = async () => {
  if (!giteeUsername.value) {
    ElMessage.error('请输入Gitee用户名');
    return;
  }

  loading.value = true;
  try {
    const response = await getGiteeUserInfo(giteeUsername.value);
    userInfo.value = response;
    ElMessage.success('获取用户信息成功');
  } catch (error) {
    console.error('获取用户信息失败:', error);
    ElMessage.error('获取用户信息失败: ' + (error.message || error));
  } finally {
    loading.value = false;
  }
};

const testContributions = async () => {
  if (!giteeUsername.value) {
    ElMessage.error('请输入Gitee用户名');
    return;
  }

  loadingContributions.value = true;
  try {
    const response = await getGiteeContributions(giteeUsername.value, {
      per_page: 50
    });
    contributions.value = response;
    ElMessage.success('获取贡献记录成功');
  } catch (error) {
    console.error('获取贡献记录失败:', error);
    ElMessage.error('获取贡献记录失败: ' + (error.message || error));
  } finally {
    loadingContributions.value = false;
  }
};

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN');
};

// 生成测试日历数据
const calendarData = computed(() => {
  const data = [];
  const today = new Date();

  for (let i = 0; i < 90; i++) {
    const date = new Date(today);
    date.setDate(today.getDate() - i);
    const dateStr = date.toISOString().split('T')[0];

    // 随机生成贡献数
    const count = Math.floor(Math.random() * 10);

    data.push({
      date: dateStr,
      count,
      level: 0
    });
  }

  return data;
});

const handleDayClick = (day: any) => {
  ElMessage.info(`${day.date}: ${day.count} 次贡献`);
};
</script>
