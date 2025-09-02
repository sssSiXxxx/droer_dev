<template>
  <div class="p-2">
    <!-- 搜索表单 -->
    <el-card shadow="hover" class="mb-4">
      <el-form ref="queryFormRef" :model="queryParams" :inline="true">
        <el-form-item label="用户名称" prop="userName">
          <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="用户昵称" prop="nickName">
          <el-input v-model="queryParams.nickName" placeholder="请输入用户昵称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="项目" prop="projectId">
          <el-select
            v-model="queryParams.projectId"
            placeholder="请选择或搜索项目"
            clearable
            filterable
            remote
            :remote-method="handleProjectSearch"
            :loading="projectSearchLoading"
            style="width: 200px"
            @change="handleQuery"
          >
            <el-option v-for="item in filteredProjectOptions" :key="item.projectId" :label="item.projectName" :value="item.projectId">
              <div class="project-option">
                <div class="project-name">{{ item.projectName }}</div>
                <div class="project-desc" v-if="item.description">{{ item.description }}</div>
              </div>
            </el-option>
            <template #empty>
              <div class="empty-option">
                <span>暂无项目数据</span>
              </div>
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号码" prop="phonenumber">
          <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd()">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain :disabled="single" icon="Edit" @click="handleUpdate()"> 修改 </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain :disabled="multiple" icon="Delete" @click="handleDelete()"> 删除 </el-button>
          </el-col>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="userList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="序号" width="60" align="center">
          <template #default="scope">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="Gitee用户名" align="center" prop="userName" :show-overflow-tooltip="true" />
        <el-table-column label="用户昵称" align="center" prop="nickName" :show-overflow-tooltip="true" />
        <el-table-column label="身份标签" align="center" prop="identityTags" min-width="120">
          <template #default="scope">
            <div v-if="scope.row.identityTags" class="identity-tags">
              <el-tag v-for="tag in parseIdentityTagsForDisplay(scope.row.identityTags)" :key="tag" size="small" type="primary" class="identity-tag">
                {{ tag }}
              </el-tag>
            </div>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>
        <el-table-column label="个人邮箱" align="center" prop="email" :show-overflow-tooltip="true" />
        <el-table-column label="拥有项目" align="center" min-width="200">
          <template #default="scope">
            <div class="owned-projects">
              <div v-if="scope.row.ownedProjects && scope.row.ownedProjects.length > 0" class="project-list">
                <div
                  v-for="(projectId, index) in scope.row.ownedProjects.slice(0, 3)"
                  :key="projectId"
                  class="project-item"
                >
                  <span
                    class="project-link"
                    @click="goToProjectRepository(projectId)"
                    :title="getProjectDescById(projectId)"
                  >
                    {{ getProjectNameById(projectId) }}
                  </span>
                  <span v-if="index < Math.min(scope.row.ownedProjects.length - 1, 2)" class="project-separator">、</span>
                </div>
                <div v-if="scope.row.ownedProjects.length > 3" class="more-projects">
                  <span class="more-text">等{{ scope.row.ownedProjects.length }}个项目</span>
                </div>
              </div>
              <span v-else class="text-gray-400">暂无项目</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
        <el-table-column label="加入时间" align="center" prop="joinTime" width="160">
          <template #default="scope">
            {{ scope.row.joinTime || scope.row.createTime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="600px" append-to-body>
      <el-form ref="userFormRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户昵称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="Gitee" prop="giteeAccount">
              <el-input v-model="form.giteeAccount" placeholder="请输入Gitee账号" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="GitHub" prop="githubAccount">
              <el-input v-model="form.githubAccount" placeholder="请输入GitHub账号" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="个人简介" prop="bio">
              <el-input v-model="form.bio" type="textarea" placeholder="请输入个人简介" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="个人邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入个人邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="拥有项目" prop="ownedProjects">
              <el-select
                v-model="form.ownedProjects"
                placeholder="请选择项目"
                multiple
                clearable
                filterable
                remote
                :remote-method="handleUserFormProjectSearch"
                :loading="userFormProjectSearchLoading"
                collapse-tags
                collapse-tags-tooltip
                :max-collapse-tags="1"
                popper-class="project-select-dropdown"
              >
                <el-option
                  v-for="item in userFormProjectOptions"
                  :key="item.projectId"
                  :label="item.projectName"
                  :value="item.projectId"
                >
                  {{ item.projectName }}
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="身份标签" prop="identityTags">
              <el-tag
                v-for="tag in dynamicTags"
                :key="tag"
                class="mx-1"
                closable
                :disable-transitions="false"
                @close="handleTagClose(tag)"
              >
                {{ tag }}
              </el-tag>
              <el-input
                v-if="inputVisible"
                ref="inputRef"
                v-model="inputValue"
                class="ml-1 w-20"
                size="small"
                @keyup.enter="handleInputConfirm"
                @blur="handleInputConfirm"
              />
              <el-button v-else class="button-new-tag ml-1" size="small" @click="showInput">
                + 新标签
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户角色">
              <div class="role-display">
                <el-tag
                  v-for="roleId in form.roleIds"
                  :key="roleId"
                  size="small"
                  type="info"
                  class="role-tag"
                >
                  {{ getRoleNameById(roleId) }}
                </el-tag>
                <span v-if="!form.roleIds || form.roleIds.length === 0" class="text-gray-400">暂无角色</span>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户岗位">
              <el-select v-model="form.postIds" multiple placeholder="请选择用户岗位" style="width: 100%">
                <el-option
                  v-for="post in postOptions"
                  :key="post.postId"
                  :label="post.postName"
                  :value="post.postId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="加入时间" prop="joinTime">
              <el-date-picker
                v-model="form.joinTime"
                type="date"
                placeholder="请选择加入Dromara社区时间"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option v-for="dict in sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel()">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="User" lang="ts">
import api from '@/api/system/user';
import { UserForm, UserQuery, UserVO } from '@/api/system/user/types';
import { RoleVO } from '@/api/system/role/types';
import { PostQuery, PostVO } from '@/api/system/post/types';
import { globalHeaders } from '@/utils/request';
import { to } from 'await-to-js';
import { optionselect } from '@/api/system/post';
import { hasPermi } from '@/directive/permission';
import { checkPermi } from '@/utils/permission';
import { listProject } from '@/api/osc/project';

// 防抖函数
const debounce = (func: Function, wait: number) => {
  let timeout: NodeJS.Timeout;
  return function executedFunction(...args: any[]) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable, sys_user_sex } = toRefs<any>(proxy?.useDict('sys_normal_disable', 'sys_user_sex'));

const userList = ref<UserVO[]>();
const loading = ref(true);
const ids = ref<Array<number | string>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const postOptions = ref<PostVO[]>([]);
const roleOptions = ref<RoleVO[]>([]);

// 项目搜索相关
const projectOptions = ref<any[]>([]);
const filteredProjectOptions = ref<any[]>([]);
const projectSearchLoading = ref(false);

// 用户表单中的项目搜索相关
const userFormProjectOptions = ref<any[]>([]);
const userFormProjectSearchLoading = ref(false);

// 身份标签相关
const dynamicTags = ref<string[]>([]);
const inputVisible = ref(false);
const inputValue = ref('');
const inputRef = ref<ElementPlus.InputInstance>();

const queryFormRef = ref<ElFormInstance>();
const userFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: UserForm = {
  userId: undefined,
  deptId: undefined,
  userName: '',
  nickName: undefined,
  password: '',
  phonenumber: undefined,
  email: undefined,
  sex: undefined,
  status: '0',
  remark: '',
  postIds: [],
  roleIds: [],
  giteeAccount: '',
  githubAccount: '',
  bio: '',
  identityTags: '',
  ownedProjects: [],
  joinTime: ''
};

const initData: PageData<UserForm, UserQuery> = {
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: '',
    nickName: '',
    phonenumber: '',
    status: '',
    deptId: '',
    roleId: '',
    projectId: ''
  },
  rules: {
    userName: [
      { required: true, message: '用户名称不能为空', trigger: 'blur' },
      {
        min: 2,
        max: 20,
        message: '用户名称长度必须介于 2 和 20 之间',
        trigger: 'blur'
      }
    ],
    nickName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
    password: [
      { required: true, message: '用户密码不能为空', trigger: 'blur' },
      {
        min: 5,
        max: 20,
        message: '用户密码长度必须介于 5 和 20 之间',
        trigger: 'blur'
      }
    ],
    roleIds: []
  }
};

const data = reactive<PageData<UserForm, UserQuery>>(initData);
const { queryParams, form, rules } = toRefs<PageData<UserForm, UserQuery>>(data);

/** 加载项目列表 */
const loadProjects = async () => {
  try {
    projectSearchLoading.value = true;
    const res = await listProject({ pageNum: 1, pageSize: 1000 });

    // 检查不同的响应结构
    if (res && res.rows && Array.isArray(res.rows)) {
      projectOptions.value = res.rows;
    } else if (res && res.data && Array.isArray(res.data)) {
      projectOptions.value = res.data;
    } else {
      projectOptions.value = [];
    }

    filteredProjectOptions.value = [...projectOptions.value];

    // 如果没有数据，提供模拟数据
    if (projectOptions.value.length === 0) {
      projectOptions.value = [
        {
          projectId: 1,
          projectName: 'RuoYi-Vue-Plus项目',
          projectCode: 'RVP001',
          description: '基于RuoYi-Vue-Plus的企业级管理系统',
          status: '1'
        },
        {
          projectId: 2,
          projectName: 'Dromara社区管理系统',
          projectCode: 'DCS001',
          description: 'Dromara开源社区管理平台',
          status: '1'
        }
      ];
      filteredProjectOptions.value = [...projectOptions.value];
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
    // 提供模拟项目数据
    projectOptions.value = [
      {
        projectId: 1,
        projectName: 'RuoYi-Vue-Plus项目',
        projectCode: 'RVP001',
        description: '基于RuoYi-Vue-Plus的企业级管理系统',
        status: '1'
      },
      {
        projectId: 2,
        projectName: 'Dromara社区管理系统',
        projectCode: 'DCS001',
        description: 'Dromara开源社区管理平台',
        status: '1'
      }
    ];
    filteredProjectOptions.value = [...projectOptions.value];
  } finally {
    projectSearchLoading.value = false;
  }
};

/** 加载用户表单项目列表 */
const loadUserFormProjects = async () => {
  try {
    userFormProjectSearchLoading.value = true;
    const res = await listProject({ pageNum: 1, pageSize: 1000 });

    if (res && res.rows && Array.isArray(res.rows)) {
      userFormProjectOptions.value = res.rows;
    } else if (res && res.data && Array.isArray(res.data)) {
      userFormProjectOptions.value = res.data;
    } else {
      userFormProjectOptions.value = [];
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
    userFormProjectOptions.value = [];
  } finally {
    userFormProjectSearchLoading.value = false;
  }
};

/** 加载用户拥有的项目 */
const loadUserOwnedProjects = async (userId: number | string) => {
  try {
    const res = await listProject({
      pageNum: 1,
      pageSize: 1000,
      userId: userId
    });

    let ownedProjects = [];
    if (res && res.rows && Array.isArray(res.rows)) {
      ownedProjects = res.rows.map(p => p.projectId);
    } else if (res && res.data && Array.isArray(res.data)) {
      ownedProjects = res.data.map(p => p.projectId);
    }

    form.value.ownedProjects = ownedProjects;
  } catch (error) {
    console.error('加载用户项目失败:', error);
    form.value.ownedProjects = [];
  }
};

/** 批量加载所有用户的拥有项目 */
const loadUsersOwnedProjects = async () => {
  try {
    // 并行加载所有用户的项目
    const promises = userList.value?.map(async (user) => {
      const res = await listProject({
        pageNum: 1,
        pageSize: 1000,
        userId: user.userId
      });

      let ownedProjects = [];
      if (res && res.rows && Array.isArray(res.rows)) {
        ownedProjects = res.rows.map(p => p.projectId);
      } else if (res && res.data && Array.isArray(res.data)) {
        ownedProjects = res.data.map(p => p.projectId);
      }

      user.ownedProjects = ownedProjects;
      return user;
    }) || [];

    await Promise.all(promises);
  } catch (error) {
    console.error('批量加载用户项目失败:', error);
  }
};

/** 标签管理相关方法 */
const handleTagClose = (tag: string) => {
  dynamicTags.value.splice(dynamicTags.value.indexOf(tag), 1);
  updateFormIdentityTags();
};

const showInput = () => {
  inputVisible.value = true;
  nextTick(() => {
    inputRef.value?.focus();
  });
};

const handleInputConfirm = () => {
  if (inputValue.value && !dynamicTags.value.includes(inputValue.value)) {
    dynamicTags.value.push(inputValue.value);
    updateFormIdentityTags();
  }
  inputVisible.value = false;
  inputValue.value = '';
};

const updateFormIdentityTags = () => {
  if (dynamicTags.value && dynamicTags.value.length > 0) {
    form.value.identityTags = JSON.stringify(dynamicTags.value);
  } else {
    form.value.identityTags = '';
  }
};

const parseIdentityTags = (identityTags: string) => {
  if (!identityTags || identityTags.trim() === '') {
    dynamicTags.value = [];
    return;
  }

  try {
    const parsed = JSON.parse(identityTags);
    dynamicTags.value = Array.isArray(parsed) ? parsed : [];
  } catch (error) {
    console.warn('解析身份标签失败:', error);
    // 兼容旧的逗号分隔格式
    dynamicTags.value = identityTags.split(',').filter(tag => tag.trim() !== '');
  }
};

/** 解析身份标签用于显示 */
const parseIdentityTagsForDisplay = (identityTags: string): string[] => {
  if (!identityTags || identityTags.trim() === '') return [];

  try {
    const parsed = JSON.parse(identityTags);
    return Array.isArray(parsed) ? parsed : [];
  } catch (error) {
    console.warn('解析身份标签失败:', error);
    // 兼容旧的逗号分隔格式
    return identityTags.split(',').filter(tag => tag.trim() !== '');
  }
};

/** 项目搜索处理（防抖优化） */
const handleProjectSearch = debounce(async (query: string) => {
  if (query !== '') {
    projectSearchLoading.value = true;
    try {
      // 如果项目列表为空，先加载所有项目
      if (projectOptions.value.length === 0) {
        await loadProjects();
      }

      // 本地过滤项目
      filteredProjectOptions.value = projectOptions.value.filter(
        (item) =>
          item.projectName.toLowerCase().includes(query.toLowerCase()) ||
          (item.description && item.description.toLowerCase().includes(query.toLowerCase())) ||
          (item.projectCode && item.projectCode.toLowerCase().includes(query.toLowerCase()))
      );
    } finally {
      projectSearchLoading.value = false;
    }
  } else {
    // 如果搜索词为空，显示所有项目
    filteredProjectOptions.value = [...projectOptions.value];
  }
}, 300);

/** 用户表单中的项目搜索处理 */
const handleUserFormProjectSearch = debounce(async (query: string) => {
  if (query !== '') {
    userFormProjectSearchLoading.value = true;
    try {
      const res = await listProject({
        pageNum: 1,
        pageSize: 100,
        projectName: query
      });

      if (res && res.rows && Array.isArray(res.rows)) {
        userFormProjectOptions.value = res.rows;
      } else if (res && res.data && Array.isArray(res.data)) {
        userFormProjectOptions.value = res.data;
      } else {
        userFormProjectOptions.value = [];
      }
    } catch (error) {
      console.error('搜索项目失败:', error);
      userFormProjectOptions.value = [];
    } finally {
      userFormProjectSearchLoading.value = false;
    }
  } else {
    // 加载所有项目
    await loadUserFormProjects();
  }
}, 300);

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  queryParams.value.pageNum = 1;
  // 重置项目选择
  filteredProjectOptions.value = [...projectOptions.value];
  handleQuery();
};

/** 查询用户列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await api.listUser(queryParams.value);
    userList.value = res.rows;
    total.value = res.total;

    // 为每个用户加载拥有的项目
    if (userList.value && userList.value.length > 0) {
      await loadUsersOwnedProjects();
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    userList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

/** 删除按钮操作 */
const handleDelete = async (row?: UserVO) => {
  const userIds = row?.userId || ids.value;
  const [err] = await to(proxy?.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？') as any);
  if (!err) {
    await api.delUser(userIds);
    await getList();
    proxy?.$modal.msgSuccess('删除成功');
  }
};

/** 用户状态修改  */
const handleStatusChange = async (row: UserVO) => {
  const text = row.status === '0' ? '启用' : '停用';
  try {
    await proxy?.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗?');
    await api.changeUserStatus(row.userId, row.status);
    proxy?.$modal.msgSuccess(text + '成功');
  } catch (err) {
    row.status = row.status === '0' ? '1' : '0';
  }
};

/** 选择条数  */
const handleSelectionChange = (selection: UserVO[]) => {
  ids.value = selection.map((item) => item.userId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 重置操作表单 */
const reset = () => {
  form.value = { ...initFormData };
  dynamicTags.value = [];
  userFormRef.value?.resetFields();
};

/** 取消按钮 */
const cancel = () => {
  dialog.visible = false;
  reset();
};

/** 新增按钮操作 */
const handleAdd = async () => {
  reset();
  const { data } = await api.getUser();
  dialog.visible = true;
  dialog.title = '新增用户';
  postOptions.value = data.posts;
  roleOptions.value = data.roles;
  await loadUserFormProjects();
};

/** 修改按钮操作 */
const handleUpdate = async (row?: UserForm) => {
  reset();
  const userId = row?.userId || ids.value[0];

  // 测试身份标签字段映射
  try {
    const testRes = await proxy.$http.get(`/system/user/testIdentityTags/${userId}`);
    console.log('身份标签测试结果:', testRes.data);
  } catch (error) {
    console.error('测试身份标签失败:', error);
  }

  const { data } = await api.getUser(userId);
  dialog.visible = true;
  dialog.title = '修改用户';
  Object.assign(form.value, data.user);
  postOptions.value = data.posts;
  roleOptions.value = data.roles;
  form.value.postIds = data.postIds;
  form.value.roleIds = data.roleIds;
  form.value.password = '';
  await loadUserFormProjects();

  // 加载用户拥有的项目
  await loadUserOwnedProjects(userId);

  // 调试：检查身份标签数据
  console.log('用户数据:', data.user);
  console.log('身份标签原始数据:', data.user.identityTags);
  console.log('身份标签类型:', typeof data.user.identityTags);

  // 解析身份标签
  parseIdentityTags(form.value.identityTags || '');

  console.log('解析后的动态标签:', dynamicTags.value);
};

/** 根据角色ID获取角色名称 */
const getRoleNameById = (roleId: number) => {
  const role = roleOptions.value.find(r => r.roleId === roleId);
  return role ? role.roleName : `角色${roleId}`;
};

/** 提交按钮 */
const submitForm = () => {
  userFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      // 在提交前更新身份标签
      updateFormIdentityTags();

      // 创建提交数据的副本，避免修改原始表单数据
      const submitData = { ...form.value };

      // 移除角色数据，不允许前端修改角色
      delete submitData.roleIds;

      // 确保数组中的值是数字类型
      if (submitData.postIds && Array.isArray(submitData.postIds)) {
        submitData.postIds = submitData.postIds.map(id => Number(id));
      }

      console.log('提交的表单数据：', submitData);
      console.log('身份标签：', submitData.identityTags);

      try {
        form.value.userId ? await api.updateUser(submitData) : await api.addUser(submitData);
        proxy?.$modal.msgSuccess('操作成功');
        dialog.visible = false;
        await getList();
      } catch (error) {
        console.error('提交失败：', error);
        proxy?.$modal.msgError('提交失败：' + (error.message || '未知错误'));
      }
    }
  });
};

/** 跳转到项目仓库 */
const goToRepository = (project: any) => {
  if (project.repositoryUrl) {
    window.open(project.repositoryUrl, '_blank');
  } else {
    proxy?.$modal.msgWarning('该项目暂无代码仓库地址');
  }
};

/** 根据项目ID获取项目名称 */
const getProjectNameById = (projectId: number) => {
  const project = projectOptions.value.find(p => p.projectId === projectId);
  return project ? project.projectName : `项目${projectId}`;
};

/** 根据项目ID获取项目描述 */
const getProjectDescById = (projectId: number) => {
  const project = projectOptions.value.find(p => p.projectId === projectId);
  return project ? (project.description || project.projectName) : `项目${projectId}`;
};

/** 根据项目ID跳转到项目仓库 */
const goToProjectRepository = (projectId: number) => {
  const project = projectOptions.value.find(p => p.projectId === projectId);
  if (project && project.repositoryUrl) {
    window.open(project.repositoryUrl, '_blank');
  } else if (project && project.websiteUrl) {
    window.open(project.websiteUrl, '_blank');
  } else {
    proxy?.$modal.msgWarning('该项目暂无仓库地址或网站链接');
  }
};

/** 获取用户拥有项目数量 */
const getOwnedProjectsCount = (user: UserVO) => {
  // 这里的ownedProjects应该是从后端查询得到的用户作为负责人的项目列表
  // 目前后端还未实现关联查询，所以返回空数组
  return user.ownedProjects && Array.isArray(user.ownedProjects) ? user.ownedProjects : [];
};

onMounted(() => {
  loadProjects();
  getList();
});
</script>

<style scoped>
.owned-projects {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
}

.project-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  max-width: 190px;
  margin: 0 auto;
  line-height: 1.5;
}

.project-item {
  display: inline-flex;
  align-items: center;
  margin: 2px 0;
}

.project-link {
  color: #409eff;
  font-size: 13px;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s ease;
  border-radius: 3px;
  padding: 1px 3px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
}

.project-link:hover {
  color: #66b1ff;
  background-color: rgba(64, 158, 255, 0.1);
  text-decoration: underline;
}

.project-separator {
  color: #909399;
  font-size: 12px;
  margin: 0 2px;
}

.more-projects {
  margin-top: 2px;
}

.more-text {
  color: #909399;
  font-size: 12px;
}

.identity-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
}

.project-link-small {
  font-size: 11px;
  display: block;
  margin: 2px 0;
  padding: 2px 4px;
  border-radius: 4px;
  background: #f8f9fa;
}

.more-projects {
  max-height: 120px;
  overflow-y: auto;
}

.more-tag {
  margin: 1px;
  font-size: 11px;
  border-radius: 10px;
  cursor: pointer;
}

.owned-projects-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.project-count {
  font-size: 13px;
  color: #606266;
  background: #f0f9ff;
  padding: 2px 8px;
  border-radius: 12px;
  border: 1px solid #e1f5fe;
}

.owned-projects {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
}

.project-tag {
  margin: 2px;
  font-size: 11px;
  border-radius: 12px;
  max-width: 100px;
  min-width: 40px;
  padding: 2px 8px;
  display: inline-block;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.2s ease;
}

.project-tag:hover {
  transform: scale(1.05);
  z-index: 10;
}

.more-tag {
  margin: 2px;
  font-size: 11px;
  border-radius: 12px;
  padding: 2px 6px;
}

.identity-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  justify-content: center;
}

.identity-tag {
  margin: 2px;
  font-size: 12px;
  border-radius: 12px;
}

.text-gray-400 {
  color: #9ca3af;
  font-size: 13px;
}

:deep(.project-select-dropdown) {
  max-width: 300px !important;
  width: auto !important;
}

.button-new-tag {
  height: 24px;
  line-height: 22px;
  padding: 0 8px;
  border-style: dashed;
}

.mx-1 {
  margin-left: 4px;
  margin-right: 4px;
}

.ml-1 {
  margin-left: 4px;
}

.w-20 {
  width: 80px;
}

.role-display {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  min-height: 32px;
  align-items: center;
}

.role-tag {
  margin: 2px;
}
</style>
