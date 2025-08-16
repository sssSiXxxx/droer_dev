<template>
  <div class="contribution-calendar">
    <div class="calendar-header">
      <h3 class="text-sm font-medium text-gray-700 mb-2">贡献日历</h3>
      <div class="flex items-center space-x-2 text-xs text-gray-500">
        <span>贡献度</span>
        <div class="flex items-center space-x-1">
          <div 
            v-for="level in 5" 
            :key="level"
            class="w-3 h-3 rounded-sm"
            :class="getLevelClass(level)"
          ></div>
        </div>
        <span>更多</span>
      </div>
    </div>
    
    <div class="calendar-grid">
      <div 
        v-for="(week, weekIndex) in calendarData" 
        :key="weekIndex"
        class="calendar-week"
      >
        <div 
          v-for="(day, dayIndex) in week" 
          :key="dayIndex"
          class="calendar-day"
          :class="getDayClass(day)"
          :title="getDayTooltip(day)"
          @click="handleDayClick(day)"
        ></div>
      </div>
    </div>
    
    <div class="calendar-footer mt-2 text-xs text-gray-500">
      <span>{{ totalContributions }} 次贡献在过去 {{ daysCount }} 天</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface ContributionDay {
  date: string;
  count: number;
  level: number;
}

interface Props {
  contributions: ContributionDay[];
  daysCount?: number;
}

const props = withDefaults(defineProps<Props>(), {
  daysCount: 365
});

const emit = defineEmits<{
  dayClick: [day: ContributionDay];
}>();

// 计算贡献等级
const getContributionLevel = (count: number): number => {
  if (count === 0) return 0;
  if (count <= 3) return 1;
  if (count <= 6) return 2;
  if (count <= 9) return 3;
  if (count <= 12) return 4;
  return 5;
};

// 生成日历数据
const calendarData = computed(() => {
  const weeks = [];
  const today = new Date();
  const startDate = new Date(today);
  startDate.setDate(today.getDate() - props.daysCount + 1);
  
  // 创建贡献数据映射
  const contributionMap = new Map();
  props.contributions.forEach(contribution => {
    contributionMap.set(contribution.date, contribution.count);
  });
  
  let currentDate = new Date(startDate);
  let currentWeek = [];
  
  while (currentDate <= today) {
    const dateStr = currentDate.toISOString().split('T')[0];
    const count = contributionMap.get(dateStr) || 0;
    const level = getContributionLevel(count);
    
    currentWeek.push({
      date: dateStr,
      count,
      level
    });
    
    if (currentWeek.length === 7) {
      weeks.push([...currentWeek]);
      currentWeek = [];
    }
    
    currentDate.setDate(currentDate.getDate() + 1);
  }
  
  // 补充最后一周
  if (currentWeek.length > 0) {
    while (currentWeek.length < 7) {
      currentWeek.push({
        date: '',
        count: 0,
        level: 0
      });
    }
    weeks.push(currentWeek);
  }
  
  return weeks;
});

// 计算总贡献数
const totalContributions = computed(() => {
  return props.contributions.reduce((sum, day) => sum + day.count, 0);
});

// 获取等级样式
const getLevelClass = (level: number) => {
  const classes = {
    0: 'bg-gray-100',
    1: 'bg-green-100',
    2: 'bg-green-200',
    3: 'bg-green-300',
    4: 'bg-green-400',
    5: 'bg-green-500'
  };
  return classes[level as keyof typeof classes] || classes[0];
};

// 获取日期样式
const getDayClass = (day: ContributionDay) => {
  if (!day.date) return 'invisible';
  return [
    'w-3 h-3 rounded-sm cursor-pointer transition-colors',
    getLevelClass(day.level),
    'hover:ring-2 hover:ring-green-300'
  ];
};

// 获取日期提示
const getDayTooltip = (day: ContributionDay) => {
  if (!day.date) return '';
  const date = new Date(day.date);
  const formattedDate = date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
  return `${formattedDate}: ${day.count} 次贡献`;
};

// 处理日期点击
const handleDayClick = (day: ContributionDay) => {
  if (day.date) {
    emit('dayClick', day);
  }
};
</script>

<style scoped>
.contribution-calendar {
  padding: 12px;
  background: var(--el-bg-color);
  border-radius: 6px;
  border: 1px solid var(--el-border-color-light);
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.calendar-grid {
  display: flex;
  gap: 2px;
}

.calendar-week {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.calendar-day {
  transition: all 0.2s ease;
}

.calendar-day:hover {
  transform: scale(1.1);
}

.calendar-footer {
  text-align: center;
}
</style>
