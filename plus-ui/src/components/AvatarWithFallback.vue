<template>
  <div 
    class="avatar-container"
    :class="[
      `avatar-size-${size}`,
      { 'avatar-circle': shape === 'circle', 'avatar-square': shape === 'square' }
    ]"
    :style="containerStyle"
  >
    <!-- 头像图片 -->
    <img 
      v-if="!showFallback && src"
      :src="src"
      :alt="alt || name"
      class="avatar-image"
      @error="handleImageError"
      @load="handleImageLoad"
    />
    
    <!-- 名字首字母 fallback -->
    <div 
      v-else
      class="avatar-fallback"
      :style="fallbackStyle"
    >
      {{ initials }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';

interface Props {
  src?: string;
  name?: string;
  alt?: string;
  size?: number | 'large' | 'default' | 'small';
  shape?: 'circle' | 'square';
  backgroundColor?: string;
  color?: string;
}

const props = withDefaults(defineProps<Props>(), {
  src: '',
  name: '',
  alt: '',
  size: 'default',
  shape: 'circle',
  backgroundColor: '',
  color: '#ffffff'
});

const showFallback = ref(false);

// 计算名字首字母
const initials = computed(() => {
  if (!props.name) return '?';
  
  const words = props.name.trim().split(/\s+/);
  if (words.length === 0) return '?';
  
  if (words.length === 1) {
    // 单个词，取前两个字符
    return words[0].substring(0, 2).toUpperCase();
  } else {
    // 多个词，取每个词的首字母（最多两个）
    return words.slice(0, 2)
      .map(word => word.charAt(0))
      .join('')
      .toUpperCase();
  }
});

// 计算尺寸
const actualSize = computed(() => {
  if (typeof props.size === 'number') {
    return props.size;
  }
  
  const sizeMap = {
    'small': 32,
    'default': 40,
    'large': 64
  };
  
  return sizeMap[props.size] || 40;
});

// 容器样式
const containerStyle = computed(() => ({
  width: `${actualSize.value}px`,
  height: `${actualSize.value}px`,
  minWidth: `${actualSize.value}px`,
  minHeight: `${actualSize.value}px`
}));

// 生成随机背景色（基于名字）
const generateBackgroundColor = (name: string): string => {
  if (!name) return '#6b7280';
  
  const colors = [
    '#ef4444', '#f97316', '#f59e0b', '#eab308', '#84cc16',
    '#22c55e', '#10b981', '#14b8a6', '#06b6d4', '#0ea5e9',
    '#3b82f6', '#6366f1', '#8b5cf6', '#a855f7', '#d946ef',
    '#ec4899', '#f43f5e'
  ];
  
  // 基于名字生成固定的颜色索引
  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash);
  }
  
  const index = Math.abs(hash) % colors.length;
  return colors[index];
};

// fallback 样式
const fallbackStyle = computed(() => ({
  backgroundColor: props.backgroundColor || generateBackgroundColor(props.name),
  color: props.color,
  fontSize: `${actualSize.value * 0.4}px`,
  fontWeight: '600'
}));

// 图片加载错误处理
const handleImageError = () => {
  showFallback.value = true;
};

// 图片加载成功处理
const handleImageLoad = () => {
  showFallback.value = false;
};

// 监听 src 变化，重置状态
watch(() => props.src, (newSrc) => {
  if (!newSrc) {
    showFallback.value = true;
  } else {
    showFallback.value = false;
  }
}, { immediate: true });
</script>

<style scoped>
.avatar-container {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #f3f4f6;
  transition: all 0.3s ease;
}

.avatar-circle {
  border-radius: 50%;
}

.avatar-square {
  border-radius: 6px;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.avatar-image:hover {
  transform: scale(1.05);
}

.avatar-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  letter-spacing: 0.5px;
  user-select: none;
  transition: all 0.3s ease;
}

.avatar-container:hover .avatar-fallback {
  transform: scale(1.05);
}

/* 尺寸样式 */
.avatar-size-small {
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.avatar-size-default {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.avatar-size-large {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-container:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}
</style>