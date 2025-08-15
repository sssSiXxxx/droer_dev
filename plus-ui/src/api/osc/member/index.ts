import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { MemberVO, MemberForm, MemberQuery } from './types';

// 定义分页响应类型
interface TableDataInfo<T> {
  rows: T[];
  total: number;
  code: number;
  msg: string;
}

export const listMember = (query?: MemberQuery): AxiosPromise<TableDataInfo<MemberVO>> => {
  return request({
    url: '/osc/member/list',
    method: 'get',
    params: query
  });
};

export const getMember = (id: number | string) => {
  return request.get({ url: `/osc/member/${id}` })
}

export const createMember = (data: MemberForm) => {
  return request.post({ url: '/osc/member', data })
}

export const updateMember = (data: MemberForm) => {
  return request.put({ url: '/osc/member', data })
}

export const deleteMember = (id: Array<number | string>) => {
  return request.delete({ url: `/osc/member/${id.join(',')}` })
}

export const downloadMemberTemplate = () => {
  return request.download({ url: '/osc/member/template' })
}

export const exportMember = (params) => {
  return request.download({ url: '/osc/member/export', params })
}

export const uploadMemberFile = (data: FormData) => {
  return request.upload({ url: '/osc/member/importData', data })
}
