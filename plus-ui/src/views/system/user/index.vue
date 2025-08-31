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
              <el-tag v-for="tag in scope.row.identityTags.split(',')" :key="tag" size="small" type="primary" class="identity-tag">
                {{ tag }}
              </el-tag>
            </div>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>
        <el-table-column label="个人邮箱" align="center" prop="email" :show-overflow-tooltip="true" />
        <el-table-column label="拥有项目" align="center" min-width="180">
          <template #default="scope">
            <div class="owned-projects">
              <!-- 目前显示暂无项目，后续需要后端实现关联查询 -->
              <span class="text-gray-400">暂无项目</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
        <el-table-column label="加入时间" align="center" prop="createTime" width="160" />
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
              <el-input v-model="form.ownedProjects" placeholder="请输入拥有项目数" maxlength="10" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="身份标签" prop="identityTags">
              <el-input v-model="form.identityTags" placeholder="请输入身份标签，多个标签用逗号分隔" maxlength="200" />
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
  ownedProjects: ''
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
    roleIds: [{ required: false, message: '用户角色不能为空', trigger: 'blur' }]
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
};

/** 修改按钮操作 */
const handleUpdate = async (row?: UserForm) => {
  reset();
  const userId = row?.userId || ids.value[0];
  const { data } = await api.getUser(userId);
  dialog.visible = true;
  dialog.title = '修改用户';
  Object.assign(form.value, data.user);
  postOptions.value = data.posts;
  roleOptions.value = data.roles;
  form.value.postIds = data.postIds;
  form.value.roleIds = data.roleIds;
  form.value.password = '';
};

/** 提交按钮 */
const submitForm = () => {
  userFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      form.value.userId ? await api.updateUser(form.value) : await api.addUser(form.value);
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
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
  gap: 6px;
  justify-content: center;
  align-items: center;
}

.project-link {
  font-size: 12px;
  margin: 2px;
  padding: 2px 6px;
  border-radius: 8px;
  background: #f0f9ff;
  border: 1px solid #e1f5fe;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.2s ease;
}

.project-link:hover {
  background: #e3f2fd;
  transform: translateY(-1px);
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
  margin: 1px;
  font-size: 11px;
  border-radius: 10px;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.more-tag {
  margin: 1px;
  font-size: 11px;
  border-radius: 10px;
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

.project-option {
  .project-name {
    font-weight: 500;
    color: var(--el-text-color-primary);
    font-size: 14px;
    margin-bottom: 2px;
  }
  
  .project-desc {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    line-height: 1.2;
  }
}

.empty-option {
  padding: 12px;
  text-align: center;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
</style>
