import request from '@/utils/request';

// 待办事项数据模型
export interface TodoItem {
  id: string;
  title: string;
  description?: string;
  completed: boolean;
  priority: 'low' | 'medium' | 'high' | 'urgent';
  category: string;
  dueDate?: string;
  createdAt: string;
  updatedAt: string;
  tags: string[];
  order: number;
  reminderTime?: string;
  isReminded?: boolean;
}

export interface TodoCategory {
  id: string;
  name: string;
  color: string;
  icon: string;
  order: number;
}

export interface TodoStats {
  total: number;
  completed: number;
  pending: number;
  overdue: number;
  todayDue: number;
  completionRate: number;
}

// 本地存储管理类
class TodoStorage {
  private readonly STORAGE_KEY = 'dromara_todos';
  private readonly CATEGORIES_KEY = 'dromara_todo_categories';

  // 获取所有待办事项
  getTodos(): TodoItem[] {
    try {
      const data = localStorage.getItem(this.STORAGE_KEY);
      return data ? JSON.parse(data) : this.getDefaultTodos();
    } catch (error) {
      console.error('读取待办事项失败:', error);
      return this.getDefaultTodos();
    }
  }

  // 保存待办事项
  saveTodos(todos: TodoItem[]): void {
    try {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(todos));
    } catch (error) {
      console.error('保存待办事项失败:', error);
    }
  }

  // 获取分类
  getCategories(): TodoCategory[] {
    try {
      const data = localStorage.getItem(this.CATEGORIES_KEY);
      return data ? JSON.parse(data) : this.getDefaultCategories();
    } catch (error) {
      console.error('读取分类失败:', error);
      return this.getDefaultCategories();
    }
  }

  // 保存分类
  saveCategories(categories: TodoCategory[]): void {
    try {
      localStorage.setItem(this.CATEGORIES_KEY, JSON.stringify(categories));
    } catch (error) {
      console.error('保存分类失败:', error);
    }
  }

  // 默认待办事项
  private getDefaultTodos(): TodoItem[] {
    return [
      {
        id: '1',
        title: '安排核心团队会议',
        description: '讨论Q4季度规划和项目优先级',
        completed: false,
        priority: 'high',
        category: 'work',
        dueDate: new Date(Date.now() + 2 * 24 * 60 * 60 * 1000).toISOString(),
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        tags: ['会议', '规划'],
        order: 1
      },
      {
        id: '2',
        title: '审核 Hutool 的 PR',
        description: '检查代码质量和单元测试覆盖率',
        completed: false,
        priority: 'medium',
        category: 'development',
        dueDate: new Date(Date.now() + 1 * 24 * 60 * 60 * 1000).toISOString(),
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        tags: ['代码审核', 'Hutool'],
        order: 2
      },
      {
        id: '3',
        title: '更新项目文档',
        description: '更新API文档和使用指南',
        completed: true,
        priority: 'medium',
        category: 'documentation',
        createdAt: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
        updatedAt: new Date().toISOString(),
        tags: ['文档', 'API'],
        order: 3
      },
      {
        id: '4',
        title: '准备月度社区报告',
        description: '整理社区活跃度数据和项目进展',
        completed: false,
        priority: 'medium',
        category: 'community',
        dueDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        tags: ['报告', '社区'],
        order: 4
      },
      {
        id: '5',
        title: '检查系统性能监控',
        description: '检查服务器负载和响应时间',
        completed: false,
        priority: 'high',
        category: 'maintenance',
        dueDate: new Date(Date.now() + 1 * 24 * 60 * 60 * 1000).toISOString(),
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        tags: ['监控', '性能'],
        order: 5
      }
    ];
  }

  // 默认分类
  private getDefaultCategories(): TodoCategory[] {
    return [
      { id: 'work', name: '工作任务', color: '#3b82f6', icon: 'Briefcase', order: 1 },
      { id: 'development', name: '开发任务', color: '#10b981', icon: 'Code', order: 2 },
      { id: 'documentation', name: '文档工作', color: '#f59e0b', icon: 'Document', order: 3 },
      { id: 'community', name: '社区事务', color: '#8b5cf6', icon: 'Users', order: 4 },
      { id: 'maintenance', name: '维护任务', color: '#ef4444', icon: 'Cog', order: 5 },
      { id: 'personal', name: '个人事务', color: '#06b6d4', icon: 'User', order: 6 }
    ];
  }
}

const todoStorage = new TodoStorage();

// API 函数
export const todoApi = {
  // 获取所有待办事项
  async getTodos(): Promise<TodoItem[]> {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(todoStorage.getTodos());
      }, 100);
    });
  },

  // 创建待办事项
  async createTodo(todo: Omit<TodoItem, 'id' | 'createdAt' | 'updatedAt'>): Promise<TodoItem> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const todos = todoStorage.getTodos();
        const newTodo: TodoItem = {
          ...todo,
          id: Date.now().toString(),
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
          order: todos.length + 1
        };
        todos.push(newTodo);
        todoStorage.saveTodos(todos);
        resolve(newTodo);
      }, 100);
    });
  },

  // 更新待办事项
  async updateTodo(id: string, updates: Partial<TodoItem>): Promise<TodoItem> {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const todos = todoStorage.getTodos();
        const index = todos.findIndex(todo => todo.id === id);
        if (index === -1) {
          reject(new Error('待办事项不存在'));
          return;
        }
        
        const updatedTodo = {
          ...todos[index],
          ...updates,
          updatedAt: new Date().toISOString()
        };
        todos[index] = updatedTodo;
        todoStorage.saveTodos(todos);
        resolve(updatedTodo);
      }, 100);
    });
  },

  // 删除待办事项
  async deleteTodo(id: string): Promise<void> {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const todos = todoStorage.getTodos();
        const index = todos.findIndex(todo => todo.id === id);
        if (index === -1) {
          reject(new Error('待办事项不存在'));
          return;
        }
        
        todos.splice(index, 1);
        todoStorage.saveTodos(todos);
        resolve();
      }, 100);
    });
  },

  // 批量更新排序
  async updateOrder(todoIds: string[]): Promise<void> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const todos = todoStorage.getTodos();
        todoIds.forEach((id, index) => {
          const todo = todos.find(t => t.id === id);
          if (todo) {
            todo.order = index + 1;
            todo.updatedAt = new Date().toISOString();
          }
        });
        todoStorage.saveTodos(todos);
        resolve();
      }, 100);
    });
  },

  // 切换完成状态
  async toggleComplete(id: string): Promise<TodoItem> {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        const todos = todoStorage.getTodos();
        const todo = todos.find(t => t.id === id);
        if (!todo) {
          reject(new Error('待办事项不存在'));
          return;
        }
        
        todo.completed = !todo.completed;
        todo.updatedAt = new Date().toISOString();
        todoStorage.saveTodos(todos);
        resolve(todo);
      }, 100);
    });
  },

  // 获取分类
  async getCategories(): Promise<TodoCategory[]> {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(todoStorage.getCategories());
      }, 100);
    });
  },

  // 创建分类
  async createCategory(category: Omit<TodoCategory, 'id'>): Promise<TodoCategory> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const categories = todoStorage.getCategories();
        const newCategory: TodoCategory = {
          ...category,
          id: Date.now().toString()
        };
        categories.push(newCategory);
        todoStorage.saveCategories(categories);
        resolve(newCategory);
      }, 100);
    });
  },

  // 获取统计信息
  async getStats(): Promise<TodoStats> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const todos = todoStorage.getTodos();
        const now = new Date();
        const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
        
        const stats: TodoStats = {
          total: todos.length,
          completed: todos.filter(t => t.completed).length,
          pending: todos.filter(t => !t.completed).length,
          overdue: todos.filter(t => !t.completed && t.dueDate && new Date(t.dueDate) < now).length,
          todayDue: todos.filter(t => !t.completed && t.dueDate && 
            new Date(t.dueDate) >= today && 
            new Date(t.dueDate) < new Date(today.getTime() + 24 * 60 * 60 * 1000)
          ).length,
          completionRate: todos.length > 0 ? (todos.filter(t => t.completed).length / todos.length) * 100 : 0
        };
        
        resolve(stats);
      }, 100);
    });
  },

  // 搜索待办事项
  async searchTodos(query: string, filters?: {
    category?: string;
    priority?: string;
    completed?: boolean;
    overdue?: boolean;
  }): Promise<TodoItem[]> {
    return new Promise((resolve) => {
      setTimeout(() => {
        let todos = todoStorage.getTodos();
        
        // 文本搜索
        if (query.trim()) {
          const searchTerm = query.toLowerCase();
          todos = todos.filter(todo => 
            todo.title.toLowerCase().includes(searchTerm) ||
            todo.description?.toLowerCase().includes(searchTerm) ||
            todo.tags.some(tag => tag.toLowerCase().includes(searchTerm))
          );
        }
        
        // 应用过滤器
        if (filters) {
          if (filters.category) {
            todos = todos.filter(todo => todo.category === filters.category);
          }
          if (filters.priority) {
            todos = todos.filter(todo => todo.priority === filters.priority);
          }
          if (filters.completed !== undefined) {
            todos = todos.filter(todo => todo.completed === filters.completed);
          }
          if (filters.overdue) {
            const now = new Date();
            todos = todos.filter(todo => 
              !todo.completed && todo.dueDate && new Date(todo.dueDate) < now
            );
          }
        }
        
        resolve(todos.sort((a, b) => a.order - b.order));
      }, 100);
    });
  }
};

export default todoApi;