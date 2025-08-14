<template>
  <div class="statistics-cards">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><Histogram /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalPhases }}</div>
              <div class="stat-label">总阶段数</div>
            </div>
          </div>
          <div class="stat-progress">
            <el-progress
              :percentage="statistics.completionRate"
              :format="(val) => val + '%完成'"
              :status="getProgressStatus(statistics.completionRate)"
            />
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon in-progress">
              <el-icon><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.inProgressPhases }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
          <div class="stat-extra">{{ statistics.completedPhases }} 已完成</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon delayed">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.delayedPhases }}</div>
              <div class="stat-label">已延期</div>
            </div>
          </div>
          <div class="stat-extra" v-if="statistics.averageDelay">平均延期 {{ statistics.averageDelay }} 天</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon upcoming">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.upcomingPhases }}</div>
              <div class="stat-label">即将开始</div>
            </div>
          </div>
          <div class="stat-extra">未来7天内</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { Histogram, Loading, Warning, Calendar } from '@element-plus/icons-vue';
import { PhaseStatistics } from '@/api/osc/projectPhase/types';

const props = defineProps<{
  statistics: PhaseStatistics;
}>();

const getProgressStatus = (percentage: number): '' | 'success' | 'exception' | 'warning' => {
  if (percentage >= 90) return 'success';
  if (percentage >= 60) return '';
  return 'exception';
};
</script>

<style scoped>
.statistics-cards {
  margin-bottom: 24px;
}

.stat-card {
  background: var(--el-bg-color);
  border-radius: 8px;

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.stat-content {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;

  .el-icon {
    font-size: 24px;
    color: #fff;
  }

  &.total {
    background-color: var(--el-color-primary);
  }

  &.in-progress {
    background-color: var(--el-color-success);
  }

  &.delayed {
    background-color: var(--el-color-danger);
  }

  &.upcoming {
    background-color: var(--el-color-warning);
  }
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.stat-extra {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-top: 8px;
}

.stat-progress {
  margin-top: 16px;
}
</style>
