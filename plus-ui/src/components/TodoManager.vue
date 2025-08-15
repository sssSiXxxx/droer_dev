<template>
  <div class="todo-manager">
    <!-- 头部工具栏 -->
    <div class="todo-header">
      <div class="header-left">
        <h2 class="title">
          <el-icon><List /></el-icon>
          待办事项
        </h2>
        <div class="stats-bar">
          <el-tag size="small" type="info">{{ stats.total }} 总计</el-tag>
          <el-tag size="small" type="success">{{ stats.completed }} 已完成</el-tag>
          <el-tag size="small" type="warning">{{ stats.pending }} 待处理</el-tag>
          <el-tag v-if="stats.overdue > 0" size="small" type="danger">{{ stats.overdue }} 过期</el-tag>
        </div>
      </div>
      
      <div class="header-actions">
        <el-button 
          type="primary" 
          size="small" 
          @click="showAddDialog = true"
          :icon="Plus"
        >
          新增任务
        </el-button>
        
        <el-dropdown @command="handleBatchAction">
          <el-button size="small">
            批量操作
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="complete-selected">标记完成</el-dropdown-item>
              <el-dropdown-item command="delete-selected">删除选中</el-dropdown-item>
              <el-dropdown-item command="clear-completed">清空已完成</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 搜索和过滤 -->
    <div class="todo-filters">
      <div class="search-section">
        <el-input
          v-model="searchQuery"
          placeholder="搜索待办事项..."
          clearable
          @input="handleSearch"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <div class="filter-section">
        <el-select 
          v-model="filterCategory" 
          placeholder="分类" 
          clearable 
          size="small"
          @change="applyFilters"
        >
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          >
            <div class="category-option">
              <div 
                class="category-color" 
                :style="{ backgroundColor: category.color }"
              ></div>
              <el-icon :style="{ color: category.color }">
                <component :is="category.icon" />
              </el-icon>
              <span>{{ category.name }}</span>
            </div>
          </el-option>
        </el-select>

        <el-select 
          v-model="filterPriority" 
          placeholder="优先级" 
          clearable 
          size="small"
          @change="applyFilters"
        >
          <el-option label="紧急" value="urgent">
            <div class="priority-option urgent">
              <el-icon><Warning /></el-icon>
              <span>紧急</span>
            </div>
          </el-option>
          <el-option label="高" value="high">
            <div class="priority-option high">
              <el-icon><ArrowUp /></el-icon>
              <span>高</span>
            </div>
          </el-option>
          <el-option label="中" value="medium">
            <div class="priority-option medium">
              <el-icon><Minus /></el-icon>
              <span>中</span>
            </div>
          </el-option>
          <el-option label="低" value="low">
            <div class="priority-option low">
              <el-icon><ArrowDown /></el-icon>
              <span>低</span>
            </div>
          </el-option>
        </el-select>

        <el-select 
          v-model="filterStatus" 
          placeholder="状态" 
          clearable 
          size="small"
          @change="applyFilters"
        >
          <el-option label="待处理" value="pending" />
          <el-option label="已完成" value="completed" />
          <el-option label="已过期" value="overdue" />
        </el-select>
      </div>
    </div>

    <!-- 待办事项列表 -->
    <div class="todo-list" v-loading="loading">
      <transition-group name="todo-list" tag="div">
        <div
          v-for="todo in displayedTodos"
          :key="todo.id"
          class="todo-item"
          :class="{
            'completed': todo.completed,
            'overdue': isOverdue(todo),
            'selected': selectedItems.includes(todo.id)
          }"
          @click="selectTodo(todo.id)"
        >
          <div class="todo-checkbox">
            <el-checkbox
              :model-value="todo.completed"
              @change="toggleComplete(todo.id)"
              @click.stop
            />
          </div>

          <div class="todo-priority">
            <div 
              class="priority-indicator" 
              :class="todo.priority"
              :title="getPriorityText(todo.priority)"
            >
              <el-icon>
                <component :is="getPriorityIcon(todo.priority)" />
              </el-icon>
            </div>
          </div>

          <div class="todo-content" @click.stop="editTodo(todo)">
            <div class="todo-title">
              {{ todo.title }}
              <div class="todo-tags" v-if="todo.tags.length > 0">
                <el-tag
                  v-for="tag in todo.tags"
                  :key="tag"
                  size="small"
                  type="info"
                  effect="plain"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>
            
            <div class="todo-description" v-if="todo.description">
              {{ todo.description }}
            </div>
            
            <div class="todo-meta">
              <div class="meta-left">
                <div class="todo-category">
                  <div 
                    class="category-dot" 
                    :style="{ backgroundColor: getCategoryColor(todo.category) }"
                  ></div>
                  <span>{{ getCategoryName(todo.category) }}</span>
                </div>
                
                <div class="todo-time" v-if="todo.dueDate">
                  <el-icon><Clock /></el-icon>
                  <span>{{ formatDueDate(todo.dueDate) }}</span>
                </div>
              </div>
              
              <div class="meta-right">
                <span class="created-time">{{ formatCreatedTime(todo.createdAt) }}</span>
              </div>
            </div>
          </div>

          <div class="todo-actions">
            <el-button-group size="small">
              <el-button @click.stop="editTodo(todo)" :icon="Edit" />
              <el-button @click.stop="duplicateTodo(todo)" :icon="CopyDocument" />
              <el-button 
                @click.stop="deleteTodo(todo.id)" 
                :icon="Delete" 
                type="danger" 
              />
            </el-button-group>
          </div>
        </div>
      </transition-group>

      <!-- 空状态 -->
      <div v-if="displayedTodos.length === 0 && !loading" class="empty-state">
        <el-empty :description="getEmptyDescription()">
          <el-button type="primary" @click="showAddDialog = true">
            创建第一个待办事项
          </el-button>
        </el-empty>
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="showAddDialog"
      :title="editingTodo ? '编辑待办事项' : '新增待办事项'"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="todoFormRef"
        :model="todoForm"
        :rules="todoRules"
        label-width="80px"
      >
        <el-form-item label="标题" prop="title">
          <el-input
            v-model="todoForm.title"
            placeholder="请输入待办事项标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input
            v-model="todoForm.description"
            type="textarea"
            placeholder="请输入详细描述（可选）"
            :rows="3"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="todoForm.category" placeholder="选择分类">
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                >
                  <div class="category-option">
                    <div 
                      class="category-color" 
                      :style="{ backgroundColor: category.color }"
                    ></div>
                    <el-icon :style="{ color: category.color }">
                      <component :is="category.icon" />
                    </el-icon>
                    <span>{{ category.name }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="todoForm.priority" placeholder="选择优先级">
                <el-option label="紧急" value="urgent">
                  <div class="priority-option urgent">
                    <el-icon><Warning /></el-icon>
                    <span>紧急</span>
                  </div>
                </el-option>
                <el-option label="高" value="high">
                  <div class="priority-option high">
                    <el-icon><ArrowUp /></el-icon>
                    <span>高</span>
                  </div>
                </el-option>
                <el-option label="中" value="medium">
                  <div class="priority-option medium">
                    <el-icon><Minus /></el-icon>
                    <span>中</span>
                  </div>
                </el-option>
                <el-option label="低" value="low">
                  <div class="priority-option low">
                    <el-icon><ArrowDown /></el-icon>
                    <span>低</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="截止时间">
          <el-date-picker
            v-model="todoForm.dueDate"
            type="datetime"
            placeholder="选择截止时间（可选）"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss.sssZ"
          />
        </el-form-item>

        <el-form-item label="标签">
          <el-input
            v-model="tagInput"
            placeholder="输入标签，按回车添加"
            @keyup.enter="addTag"
          >
            <template #suffix>
              <el-button 
                size="small" 
                text 
                @click="addTag"
                :disabled="!tagInput.trim()"
              >
                添加
              </el-button>
            </template>
          </el-input>
          <div class="tags-display" v-if="todoForm.tags.length > 0">
            <el-tag
              v-for="(tag, index) in todoForm.tags"
              :key="index"
              closable
              @close="removeTag(index)"
              style="margin: 2px;"
            >
              {{ tag }}
            </el-tag>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="saveTodo"
            :loading="saving"
          >
            {{ editingTodo ? '更新' : '创建' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { 
  List, Plus, Search, Edit, Delete, CopyDocument, Clock, Warning,
  ArrowUp, ArrowDown, Minus, Briefcase, Code, Document, Users, Cog, User
} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus';
import { todoApi, type TodoItem, type TodoCategory, type TodoStats } from '@/api/todo';

// 响应式数据
const loading = ref(false);
const saving = ref(false);
const showAddDialog = ref(false);
const editingTodo = ref<TodoItem | null>(null);
const selectedItems = ref<string[]>([]);

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

// 搜索和过滤
const searchQuery = ref('');
const filterCategory = ref('');
const filterPriority = ref('');
const filterStatus = ref('');

// 表单数据
const todoFormRef = ref<FormInstance>();
const todoForm = reactive({
  title: '',
  description: '',
  category: '',
  priority: 'medium' as const,
  dueDate: '',
  tags: [] as string[]
});

const tagInput = ref('');

// 表单验证规则
const todoRules: FormRules = {
  title: [
    { required: true, message: '请输入待办事项标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ]
};

// 计算属性
const displayedTodos = computed(() => {
  let filtered = todos.value;

  // 应用搜索
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(todo =>
      todo.title.toLowerCase().includes(query) ||
      todo.description?.toLowerCase().includes(query) ||
      todo.tags.some(tag => tag.toLowerCase().includes(query))
    );
  }

  // 应用过滤器
  if (filterCategory.value) {
    filtered = filtered.filter(todo => todo.category === filterCategory.value);
  }

  if (filterPriority.value) {
    filtered = filtered.filter(todo => todo.priority === filterPriority.value);
  }

  if (filterStatus.value) {
    if (filterStatus.value === 'completed') {
      filtered = filtered.filter(todo => todo.completed);
    } else if (filterStatus.value === 'pending') {
      filtered = filtered.filter(todo => !todo.completed);
    } else if (filterStatus.value === 'overdue') {
      filtered = filtered.filter(todo => isOverdue(todo));
    }
  }

  return filtered.sort((a, b) => a.order - b.order);
});

// 方法
const loadData = async () => {
  loading.value = true;
  try {
    const [todosData, categoriesData, statsData] = await Promise.all([
      todoApi.getTodos(),
      todoApi.getCategories(),
      todoApi.getStats()
    ]);
    
    todos.value = todosData;
    categories.value = categoriesData;
    stats.value = statsData;
  } catch (error) {
    ElMessage.error('加载数据失败');
    console.error('加载数据失败:', error);
  } finally {
    loading.value = false;
  }
};

const toggleComplete = async (id: string) => {
  try {
    await todoApi.toggleComplete(id);
    await loadData(); // 重新加载数据和统计
    ElMessage.success('状态更新成功');
  } catch (error) {
    ElMessage.error('更新失败');
    console.error('切换完成状态失败:', error);
  }
};

const editTodo = (todo: TodoItem) => {
  editingTodo.value = todo;
  todoForm.title = todo.title;
  todoForm.description = todo.description || '';
  todoForm.category = todo.category;
  todoForm.priority = todo.priority;
  todoForm.dueDate = todo.dueDate || '';
  todoForm.tags = [...todo.tags];
  showAddDialog.value = true;
};

const duplicateTodo = async (todo: TodoItem) => {
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
};

const deleteTodo = async (id: string) => {
  try {
    await ElMessageBox.confirm('确定要删除这个待办事项吗？', '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    await todoApi.deleteTodo(id);
    await loadData();
    ElMessage.success('删除成功');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
      console.error('删除待办事项失败:', error);
    }
  }
};

const saveTodo = async () => {
  if (!todoFormRef.value) return;
  
  try {
    await todoFormRef.value.validate();
    saving.value = true;
    
    const todoData = {
      title: todoForm.title,
      description: todoForm.description,
      category: todoForm.category,
      priority: todoForm.priority,
      dueDate: todoForm.dueDate || undefined,
      tags: todoForm.tags,
      completed: false,
      order: 0
    };
    
    if (editingTodo.value) {
      await todoApi.updateTodo(editingTodo.value.id, todoData);
      ElMessage.success('更新成功');
    } else {
      await todoApi.createTodo(todoData);
      ElMessage.success('创建成功');
    }
    
    await loadData();
    showAddDialog.value = false;
    resetForm();
  } catch (error) {
    if (error) {
      ElMessage.error('保存失败');
      console.error('保存待办事项失败:', error);
    }
  } finally {
    saving.value = false;
  }
};

const resetForm = () => {
  editingTodo.value = null;
  todoForm.title = '';
  todoForm.description = '';
  todoForm.category = '';
  todoForm.priority = 'medium';
  todoForm.dueDate = '';
  todoForm.tags = [];
  tagInput.value = '';
  todoFormRef.value?.resetFields();
};

const addTag = () => {
  const tag = tagInput.value.trim();
  if (tag && !todoForm.tags.includes(tag)) {
    todoForm.tags.push(tag);
    tagInput.value = '';
  }
};

const removeTag = (index: number) => {
  todoForm.tags.splice(index, 1);
};

const selectTodo = (id: string) => {
  const index = selectedItems.value.indexOf(id);
  if (index > -1) {
    selectedItems.value.splice(index, 1);
  } else {
    selectedItems.value.push(id);
  }
};

const handleBatchAction = async (command: string) => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请先选择要操作的项目');
    return;
  }
  
  try {
    switch (command) {
      case 'complete-selected':
        for (const id of selectedItems.value) {
          const todo = todos.value.find(t => t.id === id);
          if (todo && !todo.completed) {
            await todoApi.toggleComplete(id);
          }
        }
        ElMessage.success('批量完成成功');
        break;
        
      case 'delete-selected':
        await ElMessageBox.confirm(`确定要删除选中的 ${selectedItems.value.length} 个待办事项吗？`, '确认删除', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        for (const id of selectedItems.value) {
          await todoApi.deleteTodo(id);
        }
        ElMessage.success('批量删除成功');
        break;
        
      case 'clear-completed':
        const completedTodos = todos.value.filter(t => t.completed);
        if (completedTodos.length === 0) {
          ElMessage.info('没有已完成的待办事项');
          return;
        }
        
        await ElMessageBox.confirm(`确定要删除所有 ${completedTodos.length} 个已完成的待办事项吗？`, '确认删除', {
          confirmButtonText: '删除',
          cancelButtonText: '取消',
          type: 'warning'
        });
        
        for (const todo of completedTodos) {
          await todoApi.deleteTodo(todo.id);
        }
        ElMessage.success('清空已完成项目成功');
        break;
    }
    
    selectedItems.value = [];
    await loadData();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败');
      console.error('批量操作失败:', error);
    }
  }
};

const handleSearch = () => {
  // 搜索是通过计算属性实现的，这里不需要额外逻辑
};

const applyFilters = () => {
  // 过滤是通过计算属性实现的，这里不需要额外逻辑
};

// 工具函数
const isOverdue = (todo: TodoItem): boolean => {
  if (!todo.dueDate || todo.completed) return false;
  return new Date(todo.dueDate) < new Date();
};

const getCategoryName = (categoryId: string): string => {
  const category = categories.value.find(c => c.id === categoryId);
  return category?.name || '未分类';
};

const getCategoryColor = (categoryId: string): string => {
  const category = categories.value.find(c => c.id === categoryId);
  return category?.color || '#666';
};

const getPriorityText = (priority: string): string => {
  const map = {
    urgent: '紧急',
    high: '高',
    medium: '中',
    low: '低'
  };
  return map[priority] || '中';
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
    return '今天到期';
  } else if (diffDays === 1) {
    return '明天到期';
  } else if (diffDays <= 7) {
    return `${diffDays} 天后到期`;
  } else {
    return date.toLocaleDateString();
  }
};

const formatCreatedTime = (dateStr: string): string => {
  const date = new Date(dateStr);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffDays = Math.floor(diffMs / (24 * 60 * 60 * 1000));
  
  if (diffDays === 0) {
    return '今天创建';
  } else if (diffDays === 1) {
    return '昨天创建';
  } else if (diffDays < 7) {
    return `${diffDays} 天前创建`;
  } else {
    return date.toLocaleDateString();
  }
};

const getEmptyDescription = (): string => {
  if (searchQuery.value || filterCategory.value || filterPriority.value || filterStatus.value) {
    return '没有符合条件的待办事项';
  }
  return '还没有待办事项，创建第一个吧！';
};

// 生命周期
onMounted(() => {
  loadData();
});
</script>

<style scoped>
.todo-manager {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.todo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: white;
  border-bottom: 1px solid #e2e8f0;
}

.header-left .title {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  display: flex;
  align-items: center;
  gap: 8px;
}

.stats-bar {
  display: flex;
  gap: 8px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.todo-filters {
  padding: 16px 20px;
  background: white;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-section {
  flex: 1;
  max-width: 300px;
}

.filter-section {
  display: flex;
  gap: 12px;
}

.todo-list {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.todo-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  margin-bottom: 12px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.todo-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: #3b82f6;
}

.todo-item.completed {
  opacity: 0.7;
  background: #f9fafb;
}

.todo-item.overdue:not(.completed) {
  border-left: 4px solid #ef4444;
  background: #fef2f2;
}

.todo-item.selected {
  background: #eff6ff;
  border-color: #3b82f6;
}

.todo-checkbox {
  flex-shrink: 0;
  margin-top: 2px;
}

.todo-priority {
  flex-shrink: 0;
}

.priority-indicator {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.priority-indicator.urgent {
  background: #fef2f2;
  color: #dc2626;
}

.priority-indicator.high {
  background: #fff7ed;
  color: #ea580c;
}

.priority-indicator.medium {
  background: #fefce8;
  color: #ca8a04;
}

.priority-indicator.low {
  background: #f0f9ff;
  color: #0284c7;
}

.todo-content {
  flex: 1;
  min-width: 0;
}

.todo-title {
  font-size: 16px;
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.todo-tags {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.todo-description {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
  line-height: 1.4;
}

.todo-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #9ca3af;
}

.meta-left {
  display: flex;
  gap: 16px;
  align-items: center;
}

.todo-category {
  display: flex;
  align-items: center;
  gap: 4px;
}

.category-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.todo-time {
  display: flex;
  align-items: center;
  gap: 4px;
}

.todo-actions {
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.todo-item:hover .todo-actions {
  opacity: 1;
}

.category-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.priority-option {
  display: flex;
  align-items: center;
  gap: 6px;
}

.priority-option.urgent {
  color: #dc2626;
}

.priority-option.high {
  color: #ea580c;
}

.priority-option.medium {
  color: #ca8a04;
}

.priority-option.low {
  color: #0284c7;
}

.tags-display {
  margin-top: 8px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.todo-list-enter-active,
.todo-list-leave-active {
  transition: all 0.3s ease;
}

.todo-list-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.todo-list-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .todo-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-actions {
    justify-content: center;
  }
  
  .todo-filters {
    flex-direction: column;
    gap: 12px;
  }
  
  .filter-section {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .todo-item {
    flex-direction: column;
    gap: 8px;
  }
  
  .todo-meta {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .meta-left {
    flex-direction: column;
    gap: 4px;
    align-items: flex-start;
  }
}
</style>