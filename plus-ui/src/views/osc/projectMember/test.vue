<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>项目成员管理测试页面</span>
          <el-button type="primary" @click="testAPI">测试API</el-button>
        </div>
      </template>
      
      <div class="test-content">
        <el-alert
          title="页面加载状态"
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
                <span>API测试结果</span>
              </template>
              <div v-if="apiResult">
                <p><strong>状态:</strong> {{ apiResult.status }}</p>
                <p><strong>数据条数:</strong> {{ apiResult.data?.rows?.length || 0 }}</p>
                <p><strong>总条数:</strong> {{ apiResult.data?.total || 0 }}</p>
              </div>
              <div v-else>
                <p>暂无测试结果</p>
              </div>
            </el-card>
          </el-col>
          
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>错误信息</span>
              </template>
              <div v-if="errorMessage">
                <el-alert
                  :title="errorMessage"
                  type="error"
                  show-icon
                  :closable="false"
                />
              </div>
              <div v-else>
                <p>暂无错误信息</p>
              </div>
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
            <el-table-column label="角色" prop="role" width="120">
              <template #default="scope">
                <el-tag :type="getRoleTagType(scope.row.role)">
                  {{ getRoleText(scope.row.role) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="权限级别" prop="permissionLevel" width="100" />
            <el-table-column label="活跃状态" prop="isActive" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isActive === '1' ? 'success' : 'info'">
                  {{ scope.row.isActive === '1' ? '活跃' : '非活跃' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="贡献度评分" prop="contributionScore" width="120" />
            <el-table-column label="加入时间" prop="joinTime" width="180" />
          </el-table>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup name="ProjectMemberTest" lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { listProjectMember } from '@/api/osc/projectMember';
import { ProjectMemberVO } from '@/api/osc/projectMember/types';

const currentTime = ref(new Date().toLocaleString());
const loading = ref(false);
const projectMemberList = ref<ProjectMemberVO[]>([]);
const apiResult = ref<any>(null);
const errorMessage = ref<string>('');

// 获取角色标签类型
function getRoleTagType(role: string) {
  const typeMap: { [key: string]: string } = {
    '0': 'info',    // 普通成员
    '1': 'danger',  // 项目负责人
    '2': 'warning', // 核心开发者
    '3': 'success', // 维护者
    '4': 'primary'  // 贡献者
  };
  return typeMap[role] || 'info';
}

// 获取角色文本
function getRoleText(role: string) {
  const textMap: { [key: string]: string } = {
    '0': '普通成员',
    '1': '项目负责人',
    '2': '核心开发者',
    '3': '维护者',
    '4': '贡献者'
  };
  return textMap[role] || '未知';
}

// 测试API
async function testAPI() {
  loading.value = true;
  errorMessage.value = '';
  
  try {
    console.log('开始测试API...');
    const response = await listProjectMember();
    console.log('API响应:', response);
    
    apiResult.value = {
      status: '成功',
      data: response
    };
    
    projectMemberList.value = response.rows || [];
    
    ElMessage.success('API测试成功');
  } catch (error: any) {
    console.error('API测试失败:', error);
    
    apiResult.value = {
      status: '失败',
      error: error.message
    };
    
    errorMessage.value = `API调用失败: ${error.message}`;
    
    ElMessage.error('API测试失败');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  console.log('测试页面已加载');
  // 自动测试API
  testAPI();
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
