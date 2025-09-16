import { getToken } from '@/utils/auth';
import { ElNotification } from 'element-plus';
import { useNoticeStore } from '@/store/modules/notice';
import { useEventSource } from '@vueuse/core';
import { watch } from 'vue';

// 初始化
export const initSSE = (url: any) => {
  if (import.meta.env.VITE_APP_SSE === 'false') {
    console.log('SSE is disabled');
    return;
  }

  const token = getToken();
  if (!token) {
    console.warn('No token found, skipping SSE connection');
    return;
  }

  url = url + '?Authorization=Bearer ' + token + '&clientid=' + import.meta.env.VITE_APP_CLIENT_ID;
  console.log('Initializing SSE connection to:', url);
  
  const { data, error, status, close } = useEventSource(url, [], {
    autoReconnect: {
      retries: 10,
      delay: 3000,
      onFailed() {
        console.error('Failed to connect SSE after 10 retries');
      }
    }
  });

  watch(error, (newError) => {
    if (newError) {
      console.error('SSE connection error:', newError);
      // 不要将 error 设为 null，让框架自动处理
    }
  });

  watch(status, (newStatus) => {
    console.log('SSE connection status:', newStatus);
  });

  watch(data, (newData) => {
    if (!newData) return;
    console.log('Received SSE message:', newData);
    
    try {
      useNoticeStore().addNotice({
        message: newData,
        read: false,
        time: new Date().toLocaleString()
      });
      
      ElNotification({
        title: '消息',
        message: newData,
        type: 'success',
        duration: 3000
      });
    } catch (err) {
      console.error('Error processing SSE message:', err);
    }
  });

  // 返回关闭函数以便外部调用
  return close;
};
