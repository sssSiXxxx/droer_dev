import request from '@/utils/request';

// 查询OSS对象存储列表
export function listOss(query?: any) {
  return request({
    url: '/system/oss/list',
    method: 'get',
    params: query
  });
}

// 删除OSS对象存储
export function delOss(ossId: string | number) {
  return request({
    url: `/system/oss/${ossId}`,
    method: 'delete'
  });
}

// 下载OSS对象
export function downloadOss(ossId: string | number) {
  return request({
    url: `/system/oss/download/${ossId}`,
    method: 'get',
    responseType: 'blob'
  });
}
