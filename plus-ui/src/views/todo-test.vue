<template>
  <div class="todo-test-page">
    <div class="page-header">
      <h1>待办事项功能测试页面</h1>
      <p>测试完整的待办事项管理功能</p>
    </div>

    <div class="test-sections">
      <!-- 完整管理器测试 -->
      <div class="test-section">
        <h2>1. 完整待办事项管理器</h2>
        <p>测试增删改查、拖拽排序、分类筛选等功能</p>
        <div class="manager-container">
          <TodoManager />
        </div>
      </div>

      <!-- 侧边栏组件测试 -->
      <div class="test-section">
        <h2>2. 侧边栏组件</h2>
        <p>测试快速添加、状态切换、拖拽排序功能</p>
        <div class="sidebar-container">
          <TodoSidebar />
        </div>
      </div>

      <!-- 通知功能测试 -->
      <div class="test-section">
        <h2>3. 通知功能测试</h2>
        <p>测试桌面通知、浏览器通知、提醒设置</p>
        <div class="notification-controls">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-card>
                <template #header>
                  <span>权限状态</span>
                </template>
                <div class="status-display">
                  <el-tag :type="getPermissionType(permission)">
                    {{ getPermissionText(permission) }}
                  </el-tag>
                  <el-button v-if="permission !== 'granted'" @click="requestNotificationPermission" size="small" type="primary"> 请求权限 </el-button>
                </div>
              </el-card>
            </el-col>

            <el-col :span="8">
              <el-card>
                <template #header>
                  <span>提醒设置</span>
                </template>
                <div class="settings-controls">
                  <el-switch v-model="settings.enabled" @change="updateNotificationSettings" />
                  <span>启用提醒</span>

                  <el-input-number v-model="settings.beforeMinutes" :min="1" :max="1440" size="small" @change="updateNotificationSettings" />
                  <span>分钟前提醒</span>
                </div>
              </el-card>
            </el-col>

            <el-col :span="8">
              <el-card>
                <template #header>
                  <span>测试操作</span>
                </template>
                <div class="test-controls">
                  <el-button @click="testNotification" type="success" size="small"> 测试通知 </el-button>
                  <el-button @click="checkReminders" type="info" size="small"> 检查提醒 </el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- API功能测试 -->
      <div class="test-section">
        <h2>4. API功能测试</h2>
        <p>测试待办事项数据管理API</p>
        <div class="api-controls">
          <el-row :gutter="16">
            <el-col :span="6">
              <el-button @click="testCreateTodo" type="primary" :loading="apiLoading"> 创建测试待办 </el-button>
            </el-col>
            <el-col :span="6">
              <el-button @click="testUpdateTodo" type="warning" :loading="apiLoading"> 更新第一个待办 </el-button>
            </el-col>
            <el-col :span="6">
              <el-button @click="testSearchTodo" type="info" :loading="apiLoading"> 搜索测试 </el-button>
            </el-col>
            <el-col :span="6">
              <el-button @click="testGetStats" type="success" :loading="apiLoading"> 获取统计 </el-button>
            </el-col>
          </el-row>

          <!-- API测试结果展示 -->
          <div v-if="apiResult" class="api-result">
            <h4>API测试结果：</h4>
            <pre>{{ JSON.stringify(apiResult, null, 2) }}</pre>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';
import TodoManager from '@/components/TodoManager.vue';
import TodoSidebar from '@/components/TodoSidebar.vue';
import { useTodoNotifications } from '@/utils/todoNotification';
import { todoApi, type TodoItem } from '@/api/todo';

// 通知功能测试
const { permission, settings, requestPermission, updateSettings, checkNow, testNotification } = useTodoNotifications();

// API测试
const apiLoading = ref(false);
const apiResult = ref<any>(null);

// 通知权限管理
const requestNotificationPermission = async () => {
  try {
    await requestPermission();
    ElMessage.success('权限请求完成');
  } catch (error) {
    ElMessage.error('权限请求失败');
  }
};

const updateNotificationSettings = () => {
  updateSettings(settings);
  ElMessage.success('设置已更新');
};

const checkReminders = async () => {
  try {
    await checkNow();
  } catch (error) {
    ElMessage.error('检查提醒失败');
  }
};

// API功能测试
const testCreateTodo = async () => {
  apiLoading.value = true;
  try {
    const result = await todoApi.createTodo({
      title: `测试待办事项 ${Date.now()}`,
      description: '这是一个测试创建的待办事项',
      category: 'work',
      priority: 'medium',
      tags: ['测试', 'API'],
      completed: false,
      order: 0
    });
    apiResult.value = result;
    ElMessage.success('创建成功');
  } catch (error) {
    ElMessage.error('创建失败');
    apiResult.value = { error: error.message };
  } finally {
    apiLoading.value = false;
  }
};

const testUpdateTodo = async () => {
  apiLoading.value = true;
  try {
    const todos = await todoApi.getTodos();
    if (todos.length > 0) {
      const firstTodo = todos[0];
      const result = await todoApi.updateTodo(firstTodo.id, {
        title: `${firstTodo.title} (已更新)`,
        priority: 'high'
      });
      apiResult.value = result;
      ElMessage.success('更新成功');
    } else {
      ElMessage.warning('没有待办事项可更新');
    }
  } catch (error) {
    ElMessage.error('更新失败');
    apiResult.value = { error: error.message };
  } finally {
    apiLoading.value = false;
  }
};

const testSearchTodo = async () => {
  apiLoading.value = true;
  try {
    const result = await todoApi.searchTodos('测试', {
      category: 'work',
      completed: false
    });
    apiResult.value = result;
    ElMessage.success(`搜索完成，找到 ${result.length} 个结果`);
  } catch (error) {
    ElMessage.error('搜索失败');
    apiResult.value = { error: error.message };
  } finally {
    apiLoading.value = false;
  }
};

const testGetStats = async () => {
  apiLoading.value = true;
  try {
    const result = await todoApi.getStats();
    apiResult.value = result;
    ElMessage.success('统计获取成功');
  } catch (error) {
    ElMessage.error('统计获取失败');
    apiResult.value = { error: error.message };
  } finally {
    apiLoading.value = false;
  }
};

// 工具函数
const getPermissionType = (perm: string) => {
  switch (perm) {
    case 'granted':
      return 'success';
    case 'denied':
      return 'danger';
    default:
      return 'warning';
  }
};

const getPermissionText = (perm: string) => {
  switch (perm) {
    case 'granted':
      return '已授权';
    case 'denied':
      return '已拒绝';
    default:
      return '未设置';
  }
};
</script>

<style scoped>
.todo-test-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-header h1 {
  color: #1f2937;
  margin-bottom: 8px;
}

.page-header p {
  color: #6b7280;
  font-size: 16px;
}

.test-sections {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.test-section {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.test-section h2 {
  color: #1f2937;
  margin-bottom: 8px;
  font-size: 20px;
}

.test-section p {
  color: #6b7280;
  margin-bottom: 20px;
}

.manager-container {
  height: 600px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.sidebar-container {
  height: 500px;
  max-width: 400px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.notification-controls {
  margin-top: 16px;
}

.status-display {
  display: flex;
  align-items: center;
  gap: 12px;
}

.settings-controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.settings-controls span {
  font-size: 12px;
  color: #6b7280;
}

.test-controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.api-controls {
  margin-top: 16px;
}

.api-result {
  margin-top: 20px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.api-result h4 {
  margin-bottom: 12px;
  color: #1f2937;
}

.api-result pre {
  background: #1f2937;
  color: #f8fafc;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  font-size: 12px;
  max-height: 300px;
}
</style>
