<template>
  <div class="todo-sidebar">
    <!-- 头部 -->
    <div class="sidebar-header">
      <div class="header-title">
        <el-icon><List /></el-icon>
        <span>待办事项</span>
        <el-badge :value="pendingCount" v-if="pendingCount > 0" />
      </div>

      <div class="header-actions">
        <el-button type="primary" size="small" text @click="showAddDialog = true" :icon="Plus" />
        <el-button size="small" text @click="showFullManager = true" :icon="FullScreen" />
      </div>
    </div>

    <!-- 快速统计 -->
    <div class="quick-stats">
      <div class="stat-item">
        <span class="stat-label">待处理</span>
        <span class="stat-value pending">{{ stats.pending }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">已完成</span>
        <span class="stat-value completed">{{ stats.completed }}</span>
      </div>
      <div class="stat-item" v-if="stats.overdue > 0">
        <span class="stat-label">已逾期</span>
        <span class="stat-value overdue">{{ stats.overdue }}</span>
      </div>
    </div>

    <!-- 待办事项列表 -->
    <div class="todo-list" v-loading="loading">
      <draggable
        :list="todos"
        @end="handleDragEnd"
        @start="handleDragStart"
        item-key="id"
        handle=".drag-handle"
        ghost-class="ghost-item"
        chosen-class="chosen-item"
      >
        <template #item="{ element: todo }">
          <div
            class="todo-item"
            :class="{
              'completed': todo.completed,
              'overdue': isOverdue(todo),
              'high-priority': todo.priority === 'urgent' || todo.priority === 'high'
            }"
          >
            <!-- 拖拽句柄 -->
            <div class="drag-handle" title="拖拽调整顺序" @click="() => console.log('点击拖拽句柄')">
              <el-icon><Rank /></el-icon>
            </div>

            <!-- 复选框 -->
            <div class="todo-checkbox">
              <el-checkbox :model-value="todo.completed" @change="toggleComplete(todo.id)" size="small" />
            </div>

            <!-- 内容 -->
            <div class="todo-content" @click="editTodo(todo)">
              <div class="todo-title">{{ todo.title }}</div>

              <div class="todo-meta">
                <!-- 分类 -->
                <div class="category-badge">
                  <div class="category-dot" :style="{ backgroundColor: getCategoryColor(todo.category) }"></div>
                  <span class="category-name">{{ getCategoryName(todo.category) }}</span>
                </div>

                <!-- 优先级 -->
                <div class="priority-badge" :class="todo.priority">
                  <el-icon>
                    <component :is="getPriorityIcon(todo.priority)" />
                  </el-icon>
                </div>

                <!-- 截止时间 -->
                <div class="due-date" v-if="todo.dueDate">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatDueDate(todo.dueDate) }}</span>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="todo-actions">
              <el-dropdown @command="(cmd) => handleItemAction(cmd, todo)" trigger="click">
                <el-button size="small" text>
                  <el-icon><MoreFilled /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">编辑</el-dropdown-item>
                    <el-dropdown-item command="duplicate">复制</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </template>
      </draggable>

      <!-- 空状态 -->
      <div v-if="todos.length === 0 && !loading" class="empty-state">
        <el-icon><DocumentAdd /></el-icon>
        <p>还没有待办事项</p>
        <el-button size="small" type="primary" @click="showAddDialog = true"> 创建第一个 </el-button>
      </div>
    </div>

    <!-- 底部操作 -->
    <div class="sidebar-footer">
      <el-button size="small" text @click="showCompleted = !showCompleted" :icon="showCompleted ? Hide : View">
        {{ showCompleted ? '隐藏已完成' : '显示已完成' }}
      </el-button>

      <el-button size="small" text @click="clearCompleted" v-if="stats.completed > 0"> 清空已完成 </el-button>
    </div>

    <!-- 快速添加对话框 -->
    <el-dialog v-model="showAddDialog" title="快速添加待办事项" width="400px" @close="resetForm">
      <el-form ref="quickFormRef" :model="quickForm" :rules="quickRules" @keyup.enter="quickAdd">
        <el-form-item prop="title">
          <el-input v-model="quickForm.title" placeholder="输入待办事项标题" maxlength="100" show-word-limit autofocus />
        </el-form-item>

        <el-form-item>
          <el-row :gutter="8">
            <el-col :span="12">
              <el-select v-model="quickForm.category" placeholder="分类" size="small">
                <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
              </el-select>
            </el-col>
            <el-col :span="12">
              <el-select v-model="quickForm.priority" placeholder="优先级" size="small">
                <el-option label="紧急" value="urgent" />
                <el-option label="高" value="high" />
                <el-option label="中" value="medium" />
                <el-option label="低" value="low" />
              </el-select>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item>
          <el-date-picker
            v-model="quickForm.dueDate"
            type="date"
            placeholder="截止日期（可选）"
            size="small"
            style="width: 100%"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DDTHH:mm:ss.sssZ"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false" size="small">取消</el-button>
          <el-button type="primary" @click="quickAdd" :loading="saving" size="small"> 添加 </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 完整管理器对话框 -->
    <el-dialog v-model="showFullManager" title="待办事项管理" width="90%" top="5vh" destroy-on-close :show-close="true" class="full-manager-dialog">
      <TodoManager @refresh="loadData" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue';
import {
  List,
  Plus,
  FullScreen,
  Clock,
  View,
  Hide,
  MoreFilled,
  DocumentAdd,
  Rank,
  Warning,
  ArrowUp,
  Minus,
  ArrowDown
} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus';
import draggable from 'vuedraggable';
import { todoApi, type TodoItem, type TodoCategory, type TodoStats } from '@/api/todo';
import TodoManager from './TodoManager.vue';

// 响应式数据
const loading = ref(false);
const saving = ref(false);
const showAddDialog = ref(false);
const showFullManager = ref(false);
const showCompleted = ref(false);

// 数据
const todos = ref<TodoItem[]>([]);
const categories = ref<TodoCategory[]>([]);
const stats = ref<TodoStats>({
  total: 0,
  completed: 0,
  pending: 0,
  overdue: 0,
  todayDue: 0,
  completionRate: 0
});

// 快速表单
const quickFormRef = ref<FormInstance>();
const quickForm = reactive({
  title: '',
  category: 'work',
  priority: 'medium' as const,
  dueDate: ''
});

const quickRules: FormRules = {
  title: [
    { required: true, message: '请输入待办事项标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
  ]
};

// 计算属性
const sortedTodos = computed({
  get() {
    let filtered = todos.value.filter((todo) => showCompleted.value || !todo.completed);

    // 只按order排序，保持拖拽后的顺序
    return filtered.sort((a, b) => a.order - b.order);
  },
  set(value) {
    todos.value = value;
  }
});

const pendingCount = computed(() => stats.value.pending);

// 方法
const loadData = async () => {
  loading.value = true;
  try {
    const [todosData, categoriesData, statsData] = await Promise.all([todoApi.getTodos(), todoApi.getCategories(), todoApi.getStats()]);

    todos.value = todosData;
    categories.value = categoriesData;
    stats.value = statsData;
  } catch (error) {
    console.error('加载数据失败:', error);
  } finally {
    loading.value = false;
  }
};

const toggleComplete = async (id: string) => {
  try {
    await todoApi.toggleComplete(id);
    await loadData();
    ElMessage.success('状态更新成功');
  } catch (error) {
    ElMessage.error('更新失败');
    console.error('切换完成状态失败:', error);
  }
};

const editTodo = (todo: TodoItem) => {
  // 打开完整的管理器来编辑
  showFullManager.value = true;
};

const handleItemAction = async (command: string, todo: TodoItem) => {
  switch (command) {
    case 'edit':
      editTodo(todo);
      break;

    case 'duplicate':
      try {
        await todoApi.createTodo({
          title: `${todo.title} (副本)`,
          description: todo.description,
          completed: false,
          priority: todo.priority,
          category: todo.category,
          dueDate: todo.dueDate,
          tags: [...todo.tags],
          order: todos.value.length + 1
        });

        await loadData();
        ElMessage.success('待办事项已复制');
      } catch (error) {
        ElMessage.error('复制失败');
        console.error('复制待办事项失败:', error);
      }
      break;

    case 'delete':
      try {
        await ElMessageBox.confirm('确定要删除这个待办事项吗？', '确认删除', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        });

        await todoApi.deleteTodo(todo.id);
        await loadData();
        ElMessage.success('删除成功');
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败');
          console.error('删除待办事项失败:', error);
        }
      }
      break;
  }
};

const handleDragStart = (evt: any) => {
  console.log('开始拖拽:', evt);
  console.log('拖拽的元素:', evt.item);
  console.log('拖拽的索引:', evt.oldIndex);
};

const handleDragEnd = async (evt: any) => {
  console.log('拖拽结束:', evt);
  console.log('旧索引:', evt.oldIndex);
  console.log('新索引:', evt.newIndex);
  console.log('拖拽的元素:', evt.item);
  if (evt.oldIndex === evt.newIndex) return;

  try {
    // 更新所有todo的order值
    todos.value.forEach((todo, index) => {
      todo.order = index + 1;
    });

    // 保存到本地存储
    await todoApi.updateOrder(todos.value.map((todo) => todo.id));
  } catch (error) {
    console.error('更新排序失败:', error);
  }
};

const quickAdd = async () => {
  if (!quickFormRef.value) return;

  try {
    await quickFormRef.value.validate();
    saving.value = true;

    await todoApi.createTodo({
      title: quickForm.title,
      description: '',
      category: quickForm.category,
      priority: quickForm.priority,
      dueDate: quickForm.dueDate || undefined,
      tags: [],
      completed: false,
      order: 0
    });

    await loadData();
    showAddDialog.value = false;
    resetForm();
    ElMessage.success('添加成功');
  } catch (error) {
    if (error) {
      ElMessage.error('添加失败');
      console.error('添加待办事项失败:', error);
    }
  } finally {
    saving.value = false;
  }
};

const resetForm = () => {
  quickForm.title = '';
  quickForm.category = 'work';
  quickForm.priority = 'medium';
  quickForm.dueDate = '';
  quickFormRef.value?.resetFields();
};

const clearCompleted = async () => {
  const completedTodos = todos.value.filter((t) => t.completed);
  if (completedTodos.length === 0) {
    ElMessage.info('没有已完成的待办事项');
    return;
  }

  try {
    await ElMessageBox.confirm(`确定要清空所有 ${completedTodos.length} 个已完成的待办事项吗？`, '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    });

    for (const todo of completedTodos) {
      await todoApi.deleteTodo(todo.id);
    }

    await loadData();
    ElMessage.success('清空已完成项目成功');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败');
      console.error('清空已完成项目失败:', error);
    }
  }
};

// 工具函数
const isOverdue = (todo: TodoItem): boolean => {
  if (!todo.dueDate || todo.completed) return false;
  return new Date(todo.dueDate) < new Date();
};

const getCategoryName = (categoryId: string): string => {
  const category = categories.value.find((c) => c.id === categoryId);
  return category?.name || '未分类';
};

const getCategoryColor = (categoryId: string): string => {
  const category = categories.value.find((c) => c.id === categoryId);
  return category?.color || '#666';
};

const getPriorityIcon = (priority: string) => {
  const map = {
    urgent: Warning,
    high: ArrowUp,
    medium: Minus,
    low: ArrowDown
  };
  return map[priority] || Minus;
};

const formatDueDate = (dateStr: string): string => {
  const date = new Date(dateStr);
  const now = new Date();
  const diffMs = date.getTime() - now.getTime();
  const diffDays = Math.ceil(diffMs / (24 * 60 * 60 * 1000));

  if (diffDays < 0) {
    return `逾期 ${Math.abs(diffDays)} 天`;
  } else if (diffDays === 0) {
    return '今天';
  } else if (diffDays === 1) {
    return '明天';
  } else if (diffDays <= 7) {
    return `${diffDays} 天后`;
  } else {
    return date.toLocaleDateString();
  }
};

// 生命周期
onMounted(() => {
  loadData();
});

// 定期刷新数据
setInterval(
  () => {
    loadData();
  },
  5 * 60 * 1000
); // 每5分钟刷新一次
</script>

<style scoped>
.todo-sidebar {
  height: 100%;
  min-height: 520px;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(180, 228, 217, 0.15);
  border: 1px solid #b4e4d9;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 16px;
  border-bottom: 1px solid #b4e4d9;
  background: linear-gradient(135deg, #b4e4d9 0%, #8fd3c7 100%);
  color: #2a3f54;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 4px;
}

.header-actions .el-button {
  color: #2a3f54;
  border-color: rgba(42, 63, 84, 0.3);
}

.header-actions .el-button:hover {
  background: rgba(42, 63, 84, 0.1);
  border-color: rgba(42, 63, 84, 0.5);
}

.quick-stats {
  display: flex;
  padding: 20px 16px;
  background: #f0f9f6;
  border-bottom: 1px solid #b4e4d9;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 11px;
  color: #6b7280;
  margin-bottom: 2px;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
}

.stat-value.pending {
  color: #8fd3c7;
}

.stat-value.completed {
  color: #10b981;
}

.stat-value.overdue {
  color: #f59e0b;
}

.todo-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
  min-height: 400px;
  position: relative;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 6px;
  margin-bottom: 6px;
  transition: all 0.15s ease;
  cursor: pointer;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.todo-item:hover {
  background: #f9fafb;
  border-color: #d1d5db;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.todo-item.completed {
  opacity: 0.7;
  background: #f3f4f6;
}

.todo-item.overdue:not(.completed) {
  background: #fef3c7;
  border-color: #f59e0b;
}

.todo-item.high-priority:not(.completed) {
  border-left: 3px solid #ef4444;
}

.drag-handle {
  opacity: 0.5;
  transition: opacity 0.15s ease;
  cursor: grab;
  color: #9ca3af;
  font-size: 14px;
  padding: 4px;
  border-radius: 3px;
  user-select: none;
}

.drag-handle:hover,
.todo-item:hover .drag-handle {
  opacity: 0.8;
  color: #6b7280;
}

.drag-handle:active {
  cursor: grabbing;
}

.todo-content {
  flex: 1;
  min-width: 0;
}

.todo-title {
  font-size: 13px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 2px;
  line-height: 1.4;
  word-break: break-word;
}

.todo-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 11px;
  color: #6b7280;
}

.category-badge {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  background: #f3f4f6;
  padding: 1px 5px;
  border-radius: 3px;
  font-size: 10px;
}

.category-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #10b981;
}

.todo-checkbox {
  flex-shrink: 0;
  margin-top: 2px;
  pointer-events: auto;
}

.todo-content {
  flex: 1;
  min-width: 0;
  pointer-events: auto;
}

.todo-title {
  font-size: 15px;
  font-weight: 600;
  color: #111827;
  margin-bottom: 6px;
  line-height: 1.5;
  word-break: break-word;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.todo-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #4b5563;
  font-weight: 500;
}

.category-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
  padding: 3px 8px;
  border-radius: 6px;
  border: 1px solid rgba(16, 185, 129, 0.2);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

.category-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}

.category-name {
  font-size: 10px;
}

.priority-badge {
  width: 16px;
  height: 16px;
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
}

.priority-badge.urgent {
  background: #fef2f2;
  color: #dc2626;
}

.priority-badge.high {
  background: #fff7ed;
  color: #ea580c;
}

.priority-badge.medium {
  background: #fefce8;
  color: #ca8a04;
}

.priority-badge.low {
  background: #f0f9ff;
  color: #0284c7;
}

.due-date {
  display: flex;
  align-items: center;
  gap: 2px;
}

.todo-actions {
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.todo-item:hover .todo-actions {
  opacity: 1;
}

.sidebar-footer {
  padding: 20px 16px;
  border-top: 1px solid #b4e4d9;
  background: #f0f9f6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
}

.empty-state .el-icon {
  font-size: 32px;
  margin-bottom: 12px;
  color: #d1d5db;
}

.empty-state p {
  margin: 0 0 16px 0;
  font-size: 14px;
}

/* 拖拽样式 */
.ghost-item {
  opacity: 0.5;
  background: #e8f5f2;
  transform: rotate(2deg);
}

.chosen-item {
  background: #f0f9f6;
  border-color: #8fd3c7;
}

.drag-item {
  transform: rotate(2deg);
}

:deep(.full-manager-dialog) {
  .el-dialog__body {
    padding: 0;
  }
}

/* 滚动条样式 */
.todo-list::-webkit-scrollbar {
  width: 4px;
}

.todo-list::-webkit-scrollbar-track {
  background: #f0f9f6;
}

.todo-list::-webkit-scrollbar-thumb {
  background: #b4e4d9;
  border-radius: 2px;
}

.todo-list::-webkit-scrollbar-thumb:hover {
  background: #8fd3c7;
}
</style>
