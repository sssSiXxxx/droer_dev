<template>
  <div class="gantt-chart">
    <div class="gantt-header">
      <div class="timeline-header">
        <div v-for="date in timelineHeaders" :key="date" class="timeline-cell">
          {{ date }}
        </div>
      </div>
    </div>
    <div class="gantt-body">
      <div v-for="phase in phases" :key="phase.phaseId" class="gantt-row">
        <div class="phase-info">
          <div class="phase-name">{{ phase.phaseName }}</div>
          <div class="phase-dates">{{ formatDate(phase.startTime) }} - {{ formatDate(phase.endTime) }}</div>
        </div>
        <div class="timeline">
          <div class="phase-bar" :class="getPhaseBarClass(phase)" :style="getPhaseBarStyle(phase)" @click="showPhaseDetail(phase)">
            <div class="progress-bar" :style="{ width: `${calculateProgress(phase)}%` }" />
            <span class="phase-label">{{ phase.phaseName }} ({{ calculateProgress(phase) }}%)</span>
            <el-tooltip v-if="isDelayed(phase)" content="阶段已延期" placement="top">
              <el-icon class="delay-icon"><Warning /></el-icon>
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue';
import { Warning } from '@element-plus/icons-vue';
import dayjs from 'dayjs';

const props = defineProps({
  phases: {
    type: Array,
    required: true,
    default: () => []
  }
});

const emit = defineEmits(['update-progress', 'phase-detail']);

// 计算时间线范围
const timeRange = computed(() => {
  if (!props.phases || props.phases.length === 0) {
    const today = dayjs();
    return {
      start: today.subtract(7, 'day'),
      end: today.add(30, 'day')
    };
  }

  const dates = props.phases.reduce((acc: any, phase: any) => {
    acc.push(dayjs(phase.startTime));
    acc.push(dayjs(phase.endTime));
    return acc;
  }, []);

  const minDate = dayjs.min(dates).subtract(7, 'day');
  const maxDate = dayjs.max(dates).add(7, 'day');

  return {
    start: minDate,
    end: maxDate
  };
});

// 生成时间线表头
const timelineHeaders = computed(() => {
  const headers = [];
  let current = timeRange.value.start;
  while (current.isBefore(timeRange.value.end) || current.isSame(timeRange.value.end)) {
    headers.push(current.format('MM/DD'));
    current = current.add(1, 'day');
  }
  return headers;
});

// 计算阶段进度条样式
const getPhaseBarStyle = (phase: any) => {
  const start = dayjs(phase.startTime);
  const end = dayjs(phase.endTime);
  const totalDays = timelineHeaders.value.length;

  const startOffset = start.diff(timeRange.value.start, 'day');
  const duration = end.diff(start, 'day') + 1;

  return {
    left: `${(startOffset / totalDays) * 100}%`,
    width: `${(duration / totalDays) * 100}%`,
    minWidth: '50px'
  };
};

// 获取阶段进度条类名
const getPhaseBarClass = (phase: any) => {
  const now = dayjs();
  const start = dayjs(phase.startTime);
  const end = dayjs(phase.endTime);

  if (phase.status === '2') return 'completed';
  if (phase.status === '3') return 'delayed';
  if (now.isBefore(start)) return 'not-started';
  if (now.isAfter(end)) return 'overdue';
  return 'in-progress';
};

// 计算阶段完成进度
const calculateProgress = (phase: any) => {
  if (phase.status === '2') return 100;
  if (phase.status === '0') return 0;

  const start = dayjs(phase.startTime);
  const end = dayjs(phase.endTime);
  const now = dayjs();

  if (now.isBefore(start)) return 0;
  if (now.isAfter(end)) return 100;

  const total = end.diff(start, 'day');
  const current = now.diff(start, 'day');
  return Math.round((current / total) * 100);
};

// 格式化日期
const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD');
};

// 检查阶段是否延期
const isDelayed = (phase: any) => {
  const now = dayjs();
  const endTime = dayjs(phase.endTime);
  return phase.status !== '2' && now.isAfter(endTime);
};

// 显示阶段详情
const showPhaseDetail = (phase: any) => {
  emit('phase-detail', phase);
};
</script>

<style scoped>
.gantt-chart {
  width: 100%;
  overflow-x: auto;
  background: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.gantt-header {
  position: sticky;
  top: 0;
  z-index: 1;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-light);
}

.timeline-header {
  display: flex;
  padding-left: 200px;
}

.timeline-cell {
  flex: 0 0 50px;
  padding: 8px 4px;
  text-align: center;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  border-right: 1px solid var(--el-border-color-lighter);
}

.gantt-body {
  position: relative;
}

.gantt-row {
  display: flex;
  height: 60px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.phase-info {
  flex: 0 0 200px;
  padding: 8px 16px;
  background: var(--el-bg-color-overlay);
  border-right: 1px solid var(--el-border-color-light);
}

.phase-name {
  font-weight: 500;
  margin-bottom: 4px;
  color: var(--el-text-color-primary);
}

.phase-dates {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.timeline {
  flex: 1;
  position: relative;
  display: flex;
  padding: 8px 0;
}

.phase-bar {
  position: absolute;
  height: 24px;
  border-radius: 4px;
  margin-top: 10px;
  background: var(--el-color-primary-light-9);
  border: 1px solid var(--el-color-primary);
  overflow: hidden;
}

.phase-bar.completed {
  background: var(--el-color-success-light-9);
  border-color: var(--el-color-success);
}

.phase-bar.delayed {
  background: var(--el-color-danger-light-9);
  border-color: var(--el-color-danger);
}

.phase-bar.not-started {
  background: var(--el-color-info-light-9);
  border-color: var(--el-color-info);
}

.phase-bar.overdue {
  background: var(--el-color-warning-light-9);
  border-color: var(--el-color-warning);
}

.phase-bar.in-progress {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary);
}

.progress-bar {
  height: 100%;
  background: currentColor;
  opacity: 0.2;
  transition: width 0.3s ease;
}

.phase-label {
  position: absolute;
  left: 8px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: calc(100% - 16px);
}

.completed .progress-bar {
  color: var(--el-color-success);
}

.delayed .progress-bar {
  color: var(--el-color-danger);
}

.not-started .progress-bar {
  color: var(--el-color-info);
}

.overdue .progress-bar {
  color: var(--el-color-warning);
}

.in-progress .progress-bar {
  color: var(--el-color-primary);
}
</style>
