<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>项目成员关联管理</span>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
        <el-form-item label="项目名称" prop="projectName">
          <el-input
            v-model="queryParams.projectName"
            placeholder="请输入项目名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="成员名称" prop="memberName">
          <el-input
            v-model="queryParams.memberName"
            placeholder="请输入成员名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="queryParams.role" placeholder="请选择角色" clearable style="width: 200px">
            <el-option label="普通成员" value="0" />
            <el-option label="项目负责人" value="1" />
            <el-option label="核心开发者" value="2" />
            <el-option label="维护者" value="3" />
            <el-option label="贡献者" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="活跃状态" prop="isActive">
          <el-select v-model="queryParams.isActive" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="活跃" value="1" />
            <el-option label="非活跃" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮区域 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['osc:projectMember:add']"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['osc:projectMember:edit']"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['osc:projectMember:remove']"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['osc:projectMember:export']"
          >导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="projectMemberList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" width="80" />
        <el-table-column label="项目名称" align="center" prop="projectName" min-width="150" />
        <el-table-column label="项目代码" align="center" prop="projectCode" min-width="120" />
        <el-table-column label="成员名称" align="center" prop="memberName" min-width="120" />
        <el-table-column label="成员邮箱" align="center" prop="memberEmail" min-width="180" />
        <el-table-column label="角色" align="center" prop="roleName" width="120">
          <template #default="scope">
            <el-tag :type="getRoleTagType(scope.row.role)">
              {{ scope.row.roleName }}
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
            <el-tag :type="scope.row.isActive === '1' ? 'success' : 'info'">
              {{ scope.row.isActive === '1' ? '活跃' : '非活跃' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="贡献度评分" align="center" prop="contributionScore" width="120">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.contributionScore || 0"
              :color="getContributionColor(scope.row.contributionScore)"
            />
          </template>
        </el-table-column>
        <el-table-column label="加入时间" align="center" prop="joinTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.joinTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
          <template #default="scope">
            <el-button
              type="text"
              icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['osc:projectMember:edit']"
            >修改</el-button>
            <el-button
              type="text"
              icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['osc:projectMember:remove']"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改项目成员关联对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="projectMemberRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="项目ID" prop="projectId">
          <el-input v-model="form.projectId" placeholder="请输入项目ID" />
        </el-form-item>
        <el-form-item label="成员ID" prop="memberId">
          <el-input v-model="form.memberId" placeholder="请输入成员ID" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="普通成员" value="0" />
            <el-option label="项目负责人" value="1" />
            <el-option label="核心开发者" value="2" />
            <el-option label="维护者" value="3" />
            <el-option label="贡献者" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限级别" prop="permissionLevel">
          <el-input-number v-model="form.permissionLevel" :min="1" :max="5" />
        </el-form-item>
        <el-form-item label="活跃状态" prop="isActive">
          <el-radio-group v-model="form.isActive">
            <el-radio label="1">活跃</el-radio>
            <el-radio label="0">非活跃</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="贡献度评分" prop="contributionScore">
          <el-input-number v-model="form.contributionScore" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectMember" lang="ts">
import { listProjectMember, getProjectMember, delProjectMember, addProjectMember, updateProjectMember, exportProjectMember } from "@/api/osc/projectMember";
import { parseTime } from "@/utils/ruoyi";

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

const projectMemberList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    projectName: undefined,
    memberName: undefined,
    role: undefined,
    isActive: undefined
  },
  rules: {
    projectId: [
      { required: true, message: "项目ID不能为空", trigger: "blur" }
    ],
    memberId: [
      { required: true, message: "成员ID不能为空", trigger: "blur" }
    ],
    role: [
      { required: true, message: "角色不能为空", trigger: "change" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询项目成员关联列表 */
function getList() {
  loading.value = true;
  listProjectMember(queryParams.value).then(response => {
    projectMemberList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: undefined,
    projectId: undefined,
    memberId: undefined,
    role: undefined,
    permissionLevel: 1,
    isActive: "1",
    contributionScore: 0,
    remark: undefined
  };
  proxy.resetForm("projectMemberRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加项目成员关联";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;
  getProjectMember(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改项目成员关联";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["projectMemberRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateProjectMember(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addProjectMember(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除项目成员关联编号为"' + _ids + '"的数据项？').then(function() {
    return delProjectMember(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('osc/projectMember/export', {
    ...queryParams.value
  }, `projectMember_${new Date().getTime()}.xlsx`);
}

/** 获取角色标签类型 */
function getRoleTagType(role) {
  const typeMap = {
    '0': 'info',
    '1': 'danger',
    '2': 'warning',
    '3': 'primary',
    '4': 'success'
  };
  return typeMap[role] || 'info';
}

/** 获取贡献度颜色 */
function getContributionColor(score) {
  if (score >= 80) return '#67C23A';
  if (score >= 60) return '#E6A23C';
  if (score >= 40) return '#F56C6C';
  return '#909399';
}

getList();
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

.text-gray-500 {
  color: #6b7280;
}

.text-sm {
  font-size: 0.875rem;
}

.mr-2 {
  margin-right: 0.5rem;
}

.mb-8 {
  margin-bottom: 2rem;
}
</style>
