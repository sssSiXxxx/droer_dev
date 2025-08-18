<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>项目成员管理 - 简化测试页面</span>
          <el-button type="primary" @click="testLoad">测试加载</el-button>
        </div>
      </template>
      
      <div class="test-content">
        <el-alert
          title="页面状态"
          :description="`页面已加载，当前时间: ${currentTime}`"
          type="info"
          show-icon
          :closable="false"
        />
        
        <el-divider />
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>加载状态</span>
              </template>
              <p><strong>Loading:</strong> {{ loading }}</p>
              <p><strong>数据条数:</strong> {{ projectMemberList.length }}</p>
              <p><strong>错误信息:</strong> {{ errorMessage || '无' }}</p>
            </el-card>
          </el-col>
          
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>API测试</span>
              </template>
              <el-button @click="testAPI">测试API连接</el-button>
              <p v-if="apiResult"><strong>API结果:</strong> {{ apiResult }}</p>
            </el-card>
          </el-col>
        </el-row>
        
        <el-divider />
        
        <el-card>
          <template #header>
            <span>数据列表</span>
          </template>
          <el-table
            v-loading="loading"
            :data="projectMemberList"
            border
            style="width: 100%"
          >
            <el-table-column label="ID" prop="id" width="80" />
            <el-table-column label="项目ID" prop="projectId" width="100" />
            <el-table-column label="成员ID" prop="memberId" width="100" />
            <el-table-column label="角色" prop="role" width="120" />
            <el-table-column label="权限级别" prop="permissionLevel" width="100" />
            <el-table-column label="活跃状态" prop="isActive" width="100" />
            <el-table-column label="贡献度评分" prop="contributionScore" width="120" />
            <el-table-column label="加入时间" prop="joinTime" width="180" />
          </el-table>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup name="ProjectMemberSimple" lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { listProjectMember } from '@/api/osc/projectMember';
import { ProjectMemberVO } from '@/api/osc/projectMember/types';

const currentTime = ref(new Date().toLocaleString());
const loading = ref(false);
const projectMemberList = ref<ProjectMemberVO[]>([]);
const errorMessage = ref<string>('');
const apiResult = ref<string>('');

// 测试加载数据
async function testLoad() {
  loading.value = true;
  errorMessage.value = '';
  
  try {
    console.log('开始加载数据...');
    const response = await listProjectMember();
    console.log('API响应:', response);
    
    if (response && response.rows) {
      projectMemberList.value = response.rows;
      ElMessage.success(`数据加载成功，共 ${response.rows.length} 条记录`);
    } else {
      projectMemberList.value = [];
      ElMessage.warning('数据为空');
    }
  } catch (error: any) {
    console.error('数据加载失败:', error);
    errorMessage.value = `数据加载失败: ${error.message}`;
    ElMessage.error('数据加载失败');
  } finally {
    loading.value = false;
  }
}

// 测试API连接
async function testAPI() {
  try {
    const response = await listProjectMember();
    apiResult.value = `成功 - 总条数: ${response.total}`;
    ElMessage.success('API连接正常');
  } catch (error: any) {
    apiResult.value = `失败 - ${error.message}`;
    ElMessage.error('API连接失败');
  }
}

onMounted(() => {
  console.log('简化测试页面已加载');
  // 自动加载数据
  testLoad();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.test-content {
  margin-top: 20px;
}

.el-divider {
  margin: 20px 0;
}
</style>
