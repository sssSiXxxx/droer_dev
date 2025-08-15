import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ContributionVO, ContributionForm, ContributionQuery } from '@/api/osc/contribution/types';

/**
 * 查询贡献统计列表
 * @param query
 * @returns {*}
 */

export const listContribution = (query?: ContributionQuery): AxiosPromise<ContributionVO[]> => {
  return request({
    url: '/osc/contribution/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询贡献统计详细
 * @param contributionId
 */
export const getContribution = (contributionId: string | number): AxiosPromise<ContributionVO> => {
  return request({
    url: '/osc/contribution/' + contributionId,
    method: 'get'
  });
};

/**
 * 新增贡献统计
 * @param data
 */
export const addContribution = (data: ContributionForm) => {
  return request({
    url: '/osc/contribution',
    method: 'post',
    data: data
  });
};

/**
 * 修改贡献统计
 * @param data
 */
export const updateContribution = (data: ContributionForm) => {
  return request({
    url: '/osc/contribution',
    method: 'put',
    data: data
  });
};

/**
 * 删除贡献统计
 * @param contributionId
 */
export const delContribution = (contributionId: string | number | Array<string | number>) => {
  return request({
    url: '/osc/contribution/' + contributionId,
    method: 'delete'
  });
};
