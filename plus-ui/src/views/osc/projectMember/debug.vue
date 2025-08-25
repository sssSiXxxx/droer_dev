<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>项目成员管理调试页面</span>
          <el-button type="primary" @click="testLoad">测试加载</el-button>
        </div>
      </template>

      <div class="debug-content">
        <el-alert title="页面状态" :description="`页面已加载，当前时间: ${currentTime}`" type="info" show-icon :closable="false" />

        <el-divider />

        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>加载状态</span>
              </template>
              <p><strong>Loading:</strong> {{ loading }}</p>
              <p><strong>数据条数:</strong> {{ projectMemberList.length }}</p>
              <p><strong>项目选项:</strong> {{ projectOptions.length }}</p>
              <p><strong>成员选项:</strong> {{ memberOptions.length }}</p>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card>
              <template #header>
                <span>错误信息</span>
              </template>
              <div v-if="errorMessage">
                <el-alert :title="errorMessage" type="error" show-icon :closable="false" />
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
          <el-table v-loading="loading" :data="projectMemberList" border style="width: 100%">
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

<script setup name="ProjectMemberDebug" lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { listProjectMember } from '@/api/osc/projectMember';
import { listProject } from '@/api/osc/project';
import { listMember } from '@/api/osc/member';
import { ProjectMemberVO } from '@/api/osc/projectMember/types';
import { ProjectVO } from '@/api/osc/project/types';
import { MemberVO } from '@/api/osc/member/types';

const currentTime = ref(new Date().toLocaleString());
const loading = ref(false);
const projectMemberList = ref<ProjectMemberVO[]>([]);
const projectOptions = ref<ProjectVO[]>([]);
const memberOptions = ref<MemberVO[]>([]);
const errorMessage = ref<string>('');

// 获取角色标签类型
function getRoleTagType(role: string) {
  const typeMap: { [key: string]: string } = {
    '0': 'info', // 普通成员
    '1': 'danger', // 项目负责人
    '2': 'warning', // 核心开发者
    '3': 'success', // 维护者
    '4': 'primary' // 贡献者
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

// 测试加载数据
async function testLoad() {
  loading.value = true;
  errorMessage.value = '';

  try {
    console.log('开始加载数据...');

    // 加载项目成员列表
    const memberResponse = await listProjectMember();
    console.log('项目成员响应:', memberResponse);
    projectMemberList.value = memberResponse.rows || [];

    // 加载项目选项
    const projectResponse = await listProject({ pageSize: 1000 });
    console.log('项目响应:', projectResponse);
    projectOptions.value = projectResponse.rows || [];

    // 加载成员选项
    const memberOptionsResponse = await listMember({ pageSize: 1000 });
    console.log('成员选项响应:', memberOptionsResponse);
    memberOptions.value = memberOptionsResponse.rows || [];

    ElMessage.success('数据加载成功');
  } catch (error: any) {
    console.error('数据加载失败:', error);
    errorMessage.value = `数据加载失败: ${error.message}`;
    ElMessage.error('数据加载失败');
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  console.log('调试页面已加载');
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

.debug-content {
  margin-top: 20px;
}

.el-divider {
  margin: 20px 0;
}
</style>
