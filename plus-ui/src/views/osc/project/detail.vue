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
        <el-col :span="6">
          <div class="info-item">
            <label>项目序号：</label>
            <span>{{ projectInfo.projectId }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="info-item">
            <label>项目名称：</label>
            <span class="project-name">{{ projectInfo.projectName }}</span>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="info-item">
            <label>项目负责人：</label>
            <span class="maintainer">{{ projectInfo.maintainer || '未设置' }}</span>
          </div>
        </el-col>
      </el-row>

      <!-- 项目统计数据 -->
      <el-row :gutter="24" style="margin-top: 16px">
        <el-col :span="6">
          <div class="stat-item">
            <el-icon class="stat-icon star-icon"><Star /></el-icon>
            <div class="stat-content">
              <div class="stat-number">{{ projectInfo.starCount || 0 }}</div>
              <div class="stat-label">Star数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <el-icon class="stat-icon fork-icon"><Share /></el-icon>
            <div class="stat-content">
              <div class="stat-number">{{ projectInfo.forkCount || 0 }}</div>
              <div class="stat-label">Fork数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <el-icon class="stat-icon contributor-icon"><User /></el-icon>
            <div class="stat-content">
              <div class="stat-number">{{ getContributorCount() }}</div>
              <div class="stat-label">贡献者数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <el-icon class="stat-icon issue-icon"><Warning /></el-icon>
            <div class="stat-content">
              <div class="stat-number">{{ projectInfo.issuesCount || 0 }}</div>
              <div class="stat-label">Issues</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="24" style="margin-top: 16px">
        <el-col :span="24">
          <div class="info-item description-item">
            <label>项目描述：</label>
            <div class="description-content">{{ projectInfo.description || '暂无描述' }}</div>
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
import { Document, ArrowLeft, Star, Share, User, Warning } from '@element-plus/icons-vue';
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


// 获取贡献者数量
const getContributorCount = () => {
  if (projectInfo.value.coreContributors) {
    return projectInfo.value.coreContributors.split(',').filter(c => c.trim()).length;
  }
  return 0;
};

// 获取状态标签
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
  align-items: flex-start;
  margin-bottom: 12px;
}

.info-item label {
  font-weight: 500;
  color: var(--el-text-color-regular);
  min-width: 80px;
  margin-right: 8px;
  flex-shrink: 0;
}

.description-item {
  flex-direction: column;
  align-items: flex-start;
}

.description-item label {
  margin-bottom: 8px;
}

.description-content {
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
  color: var(--el-text-color-primary);
}

.project-name {
  font-weight: 600;
  color: var(--el-color-primary);
}

.maintainer {
  font-weight: 500;
  color: var(--el-text-color-primary);
}

/* 统计数据样式 */
.stat-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 8px;
  border: 1px solid #dee2e6;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 24px;
  margin-right: 12px;
}

.star-icon {
  color: #f7ba2a;
}

.fork-icon {
  color: #28a745;
}

.contributor-icon {
  color: #6f42c1;
}

.issue-icon {
  color: #fd7e14;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 20px;
  font-weight: bold;
  color: var(--el-text-color-primary);
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: var(--el-text-color-regular);
  margin-top: 4px;
}

</style>
