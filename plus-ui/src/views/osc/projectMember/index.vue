<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              v-hasPermi="['osc:projectMember:add']"
              type="primary"
              plain
              icon="Plus"
              @click="handleAdd"
            >
              新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              v-hasPermi="['osc:projectMember:edit']"
              type="success"
              plain
              icon="Edit"
              :disabled="single"
              @click="handleUpdate"
            >
              修改
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              v-hasPermi="['osc:projectMember:remove']"
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete"
            >
              删除
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              v-hasPermi="['osc:projectMember:export']"
              type="warning"
              plain
              icon="Download"
              @click="handleExport"
            >
              导出
            </el-button>
          </el-col>

          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="Refresh"
              @click="handleQuery"
            >
              刷新
            </el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <!-- 搜索区域 -->
      <el-form
        ref="queryFormRef"
        :model="queryParams"
        :inline="true"
        label-width="68px"
        v-show="showSearch"
      >
        <el-form-item label="项目" prop="projectId">
          <el-select
            v-model="queryParams.projectId"
            placeholder="请选择项目"
            clearable
            style="width: 200px"
            @change="handleQuery"
          >
            <el-option
              v-for="project in projectOptions"
              :key="project.projectId"
              :label="project.projectName"
              :value="project.projectId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="成员" prop="memberId">
          <el-select
            v-model="queryParams.memberId"
            placeholder="请选择成员"
            clearable
            style="width: 200px"
            @change="handleQuery"
          >
            <el-option
              v-for="member in memberOptions"
              :key="member.memberId"
              :label="member.memberName"
              :value="member.memberId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select
            v-model="queryParams.role"
            placeholder="请选择角色"
            clearable
            style="width: 120px"
            @change="handleQuery"
          >
            <el-option
              v-for="dict in osc_project_member_role"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="活跃状态" prop="isActive">
          <el-select
            v-model="queryParams.isActive"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
            @change="handleQuery"
          >
            <el-option
              v-for="dict in sys_yes_no"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="加入时间" prop="joinTime">
          <el-date-picker
            v-model="dateRange"
            style="width: 240px"
            value-format="YYYY-MM-DD"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="projectMemberList"
        @selection-change="handleSelectionChange"
        @sort-change="handleSortChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" width="80" />
        <el-table-column label="项目信息" align="center" min-width="200">
          <template #default="scope">
            <div class="project-info">
              <div class="project-name">{{ scope.row.projectName }}</div>
              <div class="project-code text-gray-500 text-sm">{{ scope.row.projectCode }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="成员信息" align="center" min-width="200">
          <template #default="scope">
            <div class="member-info">
              <el-avatar :size="32" :src="scope.row.memberAvatar" class="mr-2">
                {{ scope.row.memberName?.charAt(0) }}
              </el-avatar>
              <div class="member-details">
                <div class="member-name">{{ scope.row.memberName }}</div>
                <div class="member-email text-gray-500 text-sm">{{ scope.row.memberEmail }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="角色" align="center" prop="role" width="120">
          <template #default="scope">
            <el-tag :type="getRoleTagType(scope.row.role)">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="权限级别" align="center" prop="permissionLevel" width="100">
          <template #default="scope">
            <el-rate
              v-model="scope.row.permissionLevel"
              :max="5"
              disabled
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column label="活跃状态" align="center" prop="isActive" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.isActive"
              active-value="1"
              inactive-value="0"
              @change="handleActiveChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="贡献度评分" align="center" prop="contributionScore" width="120">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.contributionScore"
              :color="getContributionColor(scope.row.contributionScore)"
              :show-text="false"
            />
            <span class="text-sm">{{ scope.row.contributionScore }}/100</span>
          </template>
        </el-table-column>
        <el-table-column label="贡献统计" align="center" width="150">
          <template #default="scope">
            <div class="contribution-stats">
              <div class="stat-item">
                <span class="text-sm text-gray-600">贡献记录:</span>
                <span class="text-sm font-medium">{{ scope.row.contributionCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="text-sm text-gray-600">总点数:</span>
                <span class="text-sm font-medium text-green-600">{{ scope.row.totalPoints || 0 }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="加入时间" align="center" prop="joinTime" width="160" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
          <template #default="scope">
            <el-button
              v-hasPermi="['osc:projectMember:edit']"
              link
              type="primary"
              icon="Edit"
              @click="handleUpdate(scope.row)"
            >
              修改
            </el-button>
            <el-button
              v-hasPermi="['osc:projectMember:edit']"
              link
              type="success"
              icon="User"
              @click="handleRoleChange(scope.row)"
            >
              角色
            </el-button>
            <el-button
              v-hasPermi="['osc:projectMember:remove']"
              link
              type="danger"
              icon="Delete"
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改项目成员关联对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="projectMemberFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目" prop="projectId">
              <el-select
                v-model="form.projectId"
                placeholder="请选择项目"
                style="width: 100%"
                @change="handleProjectChange"
              >
                <el-option
                  v-for="project in projectOptions"
                  :key="project.projectId"
                  :label="project.projectName"
                  :value="project.projectId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成员" prop="memberId">
              <el-select
                v-model="form.memberId"
                placeholder="请选择成员"
                style="width: 100%"
                filterable
                @change="handleMemberChange"
              >
                <el-option
                  v-for="member in memberOptions"
                  :key="member.memberId"
                  :label="member.memberName"
                  :value="member.memberId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select
                v-model="form.role"
                placeholder="请选择角色"
                style="width: 100%"
                @change="handleRoleSelectChange"
              >
                <el-option
                  v-for="dict in osc_project_member_role"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限级别" prop="permissionLevel">
              <el-rate
                v-model="form.permissionLevel"
                :max="5"
                show-score
                text-color="#ff9900"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="活跃状态" prop="isActive">
              <el-radio-group v-model="form.isActive">
                <el-radio
                  v-for="dict in sys_yes_no"
                  :key="dict.value"
                  :value="dict.value"
                >
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="贡献度评分" prop="contributionScore">
              <el-slider
                v-model="form.contributionScore"
                :min="0"
                :max="100"
                :step="1"
                show-input
                input-size="small"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            placeholder="请输入备注"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 角色修改对话框 -->
    <el-dialog title="修改角色" v-model="roleDialogVisible" width="400px" append-to-body>
      <el-form ref="roleFormRef" :model="roleForm" label-width="80px">
        <el-form-item label="当前角色">
          <el-tag :type="getRoleTagType(roleForm.role)">
            {{ getRoleText(roleForm.role) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新角色" prop="newRole">
          <el-select
            v-model="roleForm.newRole"
            placeholder="请选择新角色"
            style="width: 100%"
          >
            <el-option
              v-for="dict in osc_project_member_role"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitRoleChange">确 定</el-button>
          <el-button @click="roleDialogVisible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或
          <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link
              type="primary"
              :underline="false"
              style="font-size: 12px; vertical-align: baseline"
              @click="importTemplate"
            >
              下载模板
            </el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectMember" lang="ts">
import { getCurrentInstance, ref } from 'vue';
import { getToken } from '@/utils/auth';
import {
  listProjectMember,
  getProjectMember,
  delProjectMember,
  addProjectMember,
  updateProjectMember,
  exportProjectMember,
  importProjectMember,
  importTemplate,
  updateMemberRole,
  updateMemberActiveStatus,
  getProjectMembers,
  getMemberProjects
} from '@/api/osc/projectMember';
import { listProject } from '@/api/osc/project';
import { listMember } from '@/api/osc/member';
import { ProjectMemberVO, ProjectMemberForm, ProjectMemberQuery } from '@/api/osc/projectMember/types';
import { ProjectVO } from '@/api/osc/project/types';
import { MemberVO } from '@/api/osc/member/types';

const { proxy } = getCurrentInstance();
const { osc_project_member_role, sys_yes_no } = proxy.useDict('osc_project_member_role', 'sys_yes_no');

const projectMemberList = ref<ProjectMemberVO[]>([]);
const projectOptions = ref<ProjectVO[]>([]);
const memberOptions = ref<MemberVO[]>([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const dateRange = ref<[DateModelType, DateModelType]>();
const roleDialogVisible = ref(false);
const roleForm = ref({
  projectId: undefined as number | undefined,
  memberId: undefined as number | undefined,
  role: '',
  newRole: ''
});

// 查询参数
const queryParams = ref<ProjectMemberQuery>({
  pageNum: 1,
  pageSize: 10,
  projectId: undefined,
  memberId: undefined,
  role: undefined,
  isActive: undefined
});

// 表单参数
const form = ref<ProjectMemberForm>({
  projectId: undefined,
  memberId: undefined,
  role: '0',
  permissionLevel: 1,
  isActive: '1',
  contributionScore: 0
});

// 表单校验规则
const rules = ref({
  projectId: [{ required: true, message: '项目不能为空', trigger: 'change' }],
  memberId: [{ required: true, message: '成员不能为空', trigger: 'change' }],
  role: [{ required: true, message: '角色不能为空', trigger: 'change' }]
});

// 上传参数
const upload = ref({
  open: false,
  title: '',
  isUploading: false,
  updateSupport: false,
  headers: { Authorization: 'Bearer ' + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + '/osc/projectMember/importData'
});

/** 查询项目成员关联列表 */
function getList() {
  loading.value = true;
  listProjectMember(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    projectMemberList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

/** 查询项目列表 */
function getProjectOptions() {
  listProject({ pageSize: 1000 }).then(response => {
    projectOptions.value = response.rows;
  });
}

/** 查询成员列表 */
function getMemberOptions() {
  listMember({ pageSize: 1000 }).then(response => {
    memberOptions.value = response.rows;
  });
}

/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}

/** 表单重置 */
function reset() {
  form.value = {
    projectId: undefined,
    memberId: undefined,
    role: '0',
    permissionLevel: 1,
    isActive: '1',
    contributionScore: 0
  };
  proxy.resetForm('projectMemberFormRef');
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm('queryFormRef');
  handleQuery();
}

/** 多选框选中数据 */
function handleSelectionChange(selection: ProjectMemberVO[]) {
  ids.value = selection.map(item => item.id!);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '添加项目成员关联';
}

/** 修改按钮操作 */
function handleUpdate(row?: ProjectMemberVO) {
  reset();
  const _id = row?.id || ids.value[0];
  getProjectMember(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = '修改项目成员关联';
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs['projectMemberFormRef'].validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != null) {
        updateProjectMember(form.value).then(response => {
          proxy.$modal.msgSuccess('修改成功');
          open.value = false;
          getList();
        });
      } else {
        addProjectMember(form.value).then(response => {
          proxy.$modal.msgSuccess('新增成功');
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row?: ProjectMemberVO) {
  const _ids = row?.id || ids.value;
  proxy.$modal.confirm('是否确认删除项目成员关联编号为"' + _ids + '"的数据项？').then(function() {
    return delProjectMember(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess('删除成功');
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('osc/projectMember/export', {
    ...queryParams.value
  }, `projectMember_${new Date().getTime()}.xlsx`);
}

/** 导入按钮操作 */
function handleImport() {
  upload.value.title = '项目成员关联数据导入';
  upload.value.open = true;
}

/** 下载模板操作 */
function importTemplate() {
  proxy.download('osc/projectMember/importTemplate', {}, `projectMember_template_${new Date().getTime()}.xlsx`);
}

/** 文件上传中处理 */
function handleFileUploadProgress(event: any, file: any) {
  upload.value.isUploading = true;
}

/** 文件上传成功处理 */
function handleFileSuccess(response: any, file: any) {
  upload.value.open = false;
  upload.value.isUploading = false;
  proxy.$refs['uploadRef'].clearFiles();
  proxy.$modal.msgSuccess('导入成功');
  getList();
}

/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs['uploadRef'].submit();
}

/** 角色修改按钮操作 */
function handleRoleChange(row: ProjectMemberVO) {
  roleForm.value = {
    projectId: row.projectId,
    memberId: row.memberId,
    role: row.role!,
    newRole: row.role!
  };
  roleDialogVisible.value = true;
}

/** 提交角色修改 */
function submitRoleChange() {
  updateMemberRole(roleForm.value.projectId!, roleForm.value.memberId!, roleForm.value.newRole).then(response => {
    proxy.$modal.msgSuccess('角色修改成功');
    roleDialogVisible.value = false;
    getList();
  });
}

/** 活跃状态修改 */
function handleActiveChange(row: ProjectMemberVO) {
  updateMemberActiveStatus(row.projectId!, row.memberId!, row.isActive!).then(response => {
    proxy.$modal.msgSuccess('状态修改成功');
  });
}

/** 项目选择变化 */
function handleProjectChange() {
  // 可以在这里添加项目相关的逻辑
}

/** 成员选择变化 */
function handleMemberChange() {
  // 可以在这里添加成员相关的逻辑
}

/** 角色选择变化 */
function handleRoleSelectChange() {
  // 根据角色自动设置权限级别
  const roleLevelMap: { [key: string]: number } = {
    '0': 1, // 普通成员
    '1': 5, // 项目负责人
    '2': 4, // 核心开发者
    '3': 3, // 维护者
    '4': 2  // 贡献者
  };
  if (form.value.role) {
    form.value.permissionLevel = roleLevelMap[form.value.role] || 1;
  }
}

/** 获取角色标签类型 */
function getRoleTagType(role: string) {
  const typeMap: { [key: string]: string } = {
    '0': 'info',    // 普通成员
    '1': 'danger',  // 项目负责人
    '2': 'warning', // 核心开发者
    '3': 'success', // 维护者
    '4': 'primary'  // 贡献者
  };
  return typeMap[role] || 'info';
}

/** 获取角色文本 */
function getRoleText(role: string) {
  const textMap: { [key: string]: string } = {
    '0': '普通成员',
    '1': '项目负责人',
    '2': '核心开发者',
    '3': '维护者',
    '4': '贡献者'
  };
  return textMap[role] || '未知';
}

/** 获取贡献度颜色 */
function getContributionColor(score: number) {
  if (score >= 80) return '#67C23A';
  if (score >= 60) return '#E6A23C';
  if (score >= 40) return '#F56C6C';
  return '#909399';
}

/** 排序变化处理 */
function handleSortChange(column: any) {
  // 处理排序逻辑
}

getList();
getProjectOptions();
getMemberOptions();
</script>

<style scoped>
.project-info {
  text-align: left;
}

.member-info {
  display: flex;
  align-items: center;
  text-align: left;
}

.member-details {
  margin-left: 8px;
}

.contribution-stats {
  text-align: left;
}

.stat-item {
  margin-bottom: 4px;
}

.text-gray-500 {
  color: #6b7280;
}

.text-green-600 {
  color: #059669;
}

.text-sm {
  font-size: 0.875rem;
}

.font-medium {
  font-weight: 500;
}

.mr-2 {
  margin-right: 0.5rem;
}

.mb-8 {
  margin-bottom: 2rem;
}
</style>
