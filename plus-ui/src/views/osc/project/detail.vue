<template>
  <div class="project-detail">
    <!-- 项目基本信息 -->
    <el-card class="project-info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Document /></el-icon>
          <span>项目基本信息</span>
          <el-button type="primary" @click="router.back()" style="margin-left: auto">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </template>

      <el-row :gutter="24">
        <el-col :span="8">
          <div class="info-item">
            <label>项目序号：</label>
            <span>{{ projectInfo.projectId }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <label>项目名称：</label>
            <span class="project-name">{{ projectInfo.projectName }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <label>项目状态：</label>
            <el-tag :type="getStatusType(projectInfo.status)">
              {{ getStatusLabel(projectInfo.status) }}
            </el-tag>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="24" style="margin-top: 16px">
        <el-col :span="24">
          <div class="info-item">
            <label>项目描述：</label>
            <span>{{ projectInfo.description || '暂无描述' }}</span>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="24" style="margin-top: 16px">
        <el-col :span="12">
          <div class="info-item">
            <label>代码仓库：</label>
            <el-link v-if="projectInfo.repositoryUrl" :href="projectInfo.repositoryUrl" target="_blank" type="primary">
              {{ projectInfo.repositoryUrl }}
            </el-link>
            <span v-else>暂无</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label>项目网站：</label>
            <el-link v-if="projectInfo.websiteUrl" :href="projectInfo.websiteUrl" target="_blank" type="primary">
              {{ projectInfo.websiteUrl }}
            </el-link>
            <span v-else>暂无</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

  </div>
</template>

<script setup name="ProjectDetail" lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Document, ArrowLeft } from '@element-plus/icons-vue';
import { getProject } from '@/api/osc/project';
import { ProjectVO } from '@/api/osc/project/types';

const route = useRoute();
const router = useRouter();

// 响应式数据
const projectInfo = ref<ProjectVO>({});

// 获取项目信息
const getProjectInfo = async () => {
  try {
    const projectId = Number(route.params.id);
    const res = await getProject(projectId);
    projectInfo.value = res.data;
  } catch (error) {
    ElMessage.error('获取项目信息失败');
  }
};


// 获取状态标签
const getStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    '0': '草稿',
    '1': '审核中',
    '2': '进行中',
    '3': '已完成',
    '4': '已暂停',
    '5': '已驳回'
  };
  return statusMap[status] || '未知';
};

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    '0': 'info',
    '1': 'warning',
    '2': 'success',
    '3': 'success',
    '4': 'danger',
    '5': 'danger'
  };
  return typeMap[status] || 'info';
};


onMounted(async () => {
  await getProjectInfo();
});
</script>

<style scoped>
.project-detail {
  padding: 20px;
  background-color: var(--el-bg-color-page);
  min-height: calc(100vh - 84px);
}

.project-info-card {
  border-radius: 8px;
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
}

.card-header .el-icon {
  margin-right: 8px;
  font-size: 18px;
  color: var(--el-color-primary);
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.info-item label {
  font-weight: 500;
  color: var(--el-text-color-regular);
  min-width: 80px;
  margin-right: 8px;
}

.project-name {
  font-weight: 600;
  color: var(--el-color-primary);
}

</style>
