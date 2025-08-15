import { ref, reactive } from 'vue';
import { ElNotification, ElMessage } from 'element-plus';
import { todoApi, type TodoItem } from '@/api/todo';

// 通知权限状态
export type NotificationPermission = 'default' | 'granted' | 'denied';

// 提醒设置
export interface ReminderSettings {
  enabled: boolean;
  beforeMinutes: number; // 提前多少分钟提醒
  sound: boolean;
  desktop: boolean;
  browser: boolean;
}

// 通知服务类
class TodoNotificationService {
  private checkInterval: NodeJS.Timeout | null = null;
  private permission: NotificationPermission = 'default';
  private lastCheck = new Date();
  
  private settings = reactive<ReminderSettings>({
    enabled: true,
    beforeMinutes: 30,
    sound: true,
    desktop: true,
    browser: true
  });

  constructor() {
    this.initSettings();
    this.checkPermission();
    this.startPeriodicCheck();
  }

  // 初始化设置
  private initSettings() {
    try {
      const saved = localStorage.getItem('todo_reminder_settings');
      if (saved) {
        Object.assign(this.settings, JSON.parse(saved));
      }
    } catch (error) {
      console.error('加载提醒设置失败:', error);
    }
  }

  // 保存设置
  private saveSettings() {
    try {
      localStorage.setItem('todo_reminder_settings', JSON.stringify(this.settings));
    } catch (error) {
      console.error('保存提醒设置失败:', error);
    }
  }

  // 检查浏览器通知权限
  async checkPermission(): Promise<NotificationPermission> {
    if ('Notification' in window) {
      this.permission = Notification.permission as NotificationPermission;
      return this.permission;
    }
    return 'denied';
  }

  // 请求通知权限
  async requestPermission(): Promise<NotificationPermission> {
    if ('Notification' in window) {
      const permission = await Notification.requestPermission();
      this.permission = permission as NotificationPermission;
      return this.permission;
    }
    return 'denied';
  }

  // 发送桌面通知
  private sendDesktopNotification(title: string, body: string, onClick?: () => void) {
    if (this.permission !== 'granted' || !this.settings.desktop) {
      return;
    }

    try {
      const notification = new Notification(title, {
        body,
        icon: '/favicon.ico',
        badge: '/favicon.ico',
        tag: 'todo-reminder',
        requireInteraction: true,
        silent: !this.settings.sound
      });

      notification.onclick = () => {
        window.focus();
        notification.close();
        if (onClick) onClick();
      };

      // 5秒后自动关闭
      setTimeout(() => {
        notification.close();
      }, 5000);
    } catch (error) {
      console.error('发送桌面通知失败:', error);
    }
  }

  // 发送浏览器内通知
  private sendBrowserNotification(title: string, message: string, type: 'info' | 'warning' | 'error' = 'info') {
    if (!this.settings.browser) return;

    ElNotification({
      title,
      message,
      type,
      duration: 8000,
      position: 'top-right',
      showClose: true
    });
  }

  // 播放提醒音
  private playReminderSound() {
    if (!this.settings.sound) return;

    try {
      // 创建音频上下文来播放提醒音
      const audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
      const oscillator = audioContext.createOscillator();
      const gainNode = audioContext.createGain();

      oscillator.connect(gainNode);
      gainNode.connect(audioContext.destination);

      oscillator.frequency.setValueAtTime(800, audioContext.currentTime);
      oscillator.frequency.setValueAtTime(600, audioContext.currentTime + 0.1);
      oscillator.frequency.setValueAtTime(800, audioContext.currentTime + 0.2);

      gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
      gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.3);

      oscillator.start(audioContext.currentTime);
      oscillator.stop(audioContext.currentTime + 0.3);
    } catch (error) {
      console.error('播放提醒音失败:', error);
    }
  }

  // 检查待办事项提醒
  async checkReminders() {
    if (!this.settings.enabled) return;

    try {
      const todos = await todoApi.getTodos();
      const now = new Date();
      const reminderTime = new Date(now.getTime() + this.settings.beforeMinutes * 60 * 1000);

      // 检查即将到期的待办事项
      const upcomingTodos = todos.filter(todo => {
        if (todo.completed || !todo.dueDate || todo.isReminded) return false;
        
        const dueDate = new Date(todo.dueDate);
        return dueDate <= reminderTime && dueDate > now;
      });

      // 检查已逾期的待办事项
      const overdueTodos = todos.filter(todo => {
        if (todo.completed || !todo.dueDate) return false;
        
        const dueDate = new Date(todo.dueDate);
        return dueDate < now;
      });

      // 发送即将到期提醒
      for (const todo of upcomingTodos) {
        const dueDate = new Date(todo.dueDate!);
        const minutesLeft = Math.floor((dueDate.getTime() - now.getTime()) / (60 * 1000));
        
        const title = '待办事项提醒';
        const message = `"${todo.title}" 将在 ${minutesLeft} 分钟后到期`;
        
        this.sendDesktopNotification(title, message, () => {
          // 点击通知时的处理
          console.log('点击了待办事项提醒');
        });
        
        this.sendBrowserNotification(title, message, 'warning');
        this.playReminderSound();

        // 标记为已提醒
        await todoApi.updateTodo(todo.id, { isReminded: true });
      }

      // 发送逾期提醒（每天只提醒一次）
      if (overdueTodos.length > 0) {
        const today = now.toDateString();
        const lastOverdueNotify = localStorage.getItem('last_overdue_notify');
        
        if (lastOverdueNotify !== today) {
          const title = '待办事项逾期提醒';
          const message = `您有 ${overdueTodos.length} 个待办事项已经逾期`;
          
          this.sendBrowserNotification(title, message, 'error');
          localStorage.setItem('last_overdue_notify', today);
        }
      }

      this.lastCheck = now;
    } catch (error) {
      console.error('检查提醒失败:', error);
    }
  }

  // 开始周期性检查
  private startPeriodicCheck() {
    if (this.checkInterval) {
      clearInterval(this.checkInterval);
    }

    // 每分钟检查一次
    this.checkInterval = setInterval(() => {
      this.checkReminders();
    }, 60 * 1000);

    // 立即检查一次
    setTimeout(() => {
      this.checkReminders();
    }, 1000);
  }

  // 停止周期性检查
  stopPeriodicCheck() {
    if (this.checkInterval) {
      clearInterval(this.checkInterval);
      this.checkInterval = null;
    }
  }

  // 手动触发检查
  async triggerCheck() {
    await this.checkReminders();
    ElMessage.success('已检查待办事项提醒');
  }

  // 获取设置
  getSettings(): ReminderSettings {
    return { ...this.settings };
  }

  // 更新设置
  updateSettings(newSettings: Partial<ReminderSettings>) {
    Object.assign(this.settings, newSettings);
    this.saveSettings();
    
    if (newSettings.enabled === false) {
      this.stopPeriodicCheck();
    } else if (newSettings.enabled === true) {
      this.startPeriodicCheck();
    }
  }

  // 获取权限状态
  getPermission(): NotificationPermission {
    return this.permission;
  }

  // 测试通知
  testNotification() {
    const title = '测试通知';
    const message = '这是一个测试通知，用于验证通知功能是否正常工作。';
    
    this.sendDesktopNotification(title, message);
    this.sendBrowserNotification(title, message, 'info');
    this.playReminderSound();
    
    ElMessage.success('测试通知已发送');
  }

  // 销毁服务
  destroy() {
    this.stopPeriodicCheck();
  }
}

// 创建全局实例
export const todoNotificationService = new TodoNotificationService();

// 提供响应式的设置和状态
export function useTodoNotifications() {
  const permission = ref<NotificationPermission>('default');
  const settings = reactive<ReminderSettings>(todoNotificationService.getSettings());

  // 更新权限状态
  const updatePermission = async () => {
    permission.value = await todoNotificationService.checkPermission();
  };

  // 请求权限
  const requestPermission = async () => {
    permission.value = await todoNotificationService.requestPermission();
    return permission.value;
  };

  // 更新设置
  const updateSettings = (newSettings: Partial<ReminderSettings>) => {
    Object.assign(settings, newSettings);
    todoNotificationService.updateSettings(newSettings);
  };

  // 手动检查
  const checkNow = () => {
    return todoNotificationService.triggerCheck();
  };

  // 测试通知
  const testNotification = () => {
    todoNotificationService.testNotification();
  };

  // 初始化权限状态
  updatePermission();

  return {
    permission,
    settings,
    updatePermission,
    requestPermission,
    updateSettings,
    checkNow,
    testNotification
  };
}

export default todoNotificationService;