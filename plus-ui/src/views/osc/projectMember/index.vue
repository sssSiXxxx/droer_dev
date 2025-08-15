<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="成员名称" prop="memberName">
              <el-input v-model="queryParams.memberName" placeholder="请输入成员名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="项目角色" prop="role">
              <el-input v-model="queryParams.role" placeholder="请输入项目角色" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="加入时间" prop="joinTime">
              <el-date-picker clearable
                v-model="queryParams.joinTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择加入时间"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['osc:projectMember:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['osc:projectMember:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['osc:projectMember:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['osc:projectMember:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="projectMemberList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" v-if="false" />
        <el-table-column label="项目名称" align="center" prop="projectName" />
        <el-table-column label="成员名称" align="center" prop="memberName" />
        <el-table-column label="项目角色" align="center" prop="role" />
        <el-table-column label="加入时间" align="center" prop="joinTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.joinTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['osc:projectMember:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['osc:projectMember:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改人员项目管理对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="projectMemberFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="项目名称" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择项目" filterable>
            <el-option v-for="project in projectList" :key="project.projectId" :label="project.projectName" :value="project.projectId" />
          </el-select>
        </el-form-item>
        <el-form-item label="成员名称" prop="memberId">
          <el-select v-model="form.memberId" placeholder="请选择成员" filterable>
            <el-option v-for="member in memberList" :key="member.memberId" :label="member.memberName" :value="member.memberId" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目角色" prop="role">
          <el-input v-model="form.role" placeholder="请输入项目角色" />
        </el-form-item>
        <el-form-item label="加入时间" prop="joinTime">
          <el-date-picker clearable
            v-model="form.joinTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择加入时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectMember" lang="ts">
import { listProjectMember, getProjectMember, delProjectMember, addProjectMember, updateProjectMember } from '@/api/osc/projectMember';
import { ProjectMemberVO, ProjectMemberQuery, ProjectMemberForm } from '@/api/osc/projectMember/types';
import { listProject } from '@/api/osc/project';
import { listMember } from '@/api/osc/member';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const projectMemberList = ref<ProjectMemberVO[]>([]);
const projectList = ref<any[]>([]);
const memberList = ref<any[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const projectMemberFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ProjectMemberForm = {
  projectId: undefined,
  memberId: undefined,
  role: undefined,
  joinTime: undefined,
}
const data = reactive<PageData<ProjectMemberForm, ProjectMemberQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    projectId: undefined,
    memberId: undefined,
    role: undefined,
    joinTime: undefined,
    params: {
    }
  },
  rules: {
    projectId: [
      { required: true, message: "项目名称不能为空", trigger: "blur" }
    ],
    memberId: [
      { required: true, message: "成员名称不能为空", trigger: "blur" }
    ],
    role: [
      { required: true, message: "项目角色不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询人员项目管理列表 */
const getList = async () => {
  loading.value = true;
  const res = await listProjectMember(queryParams.value);
  projectMemberList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 加载项目列表 */
const loadProjectList = async () => {
  const res = await listProject({ pageNum: 1, pageSize: 1000 });
  projectList.value = res.data.rows || [];
}

/** 加载成员列表 */
const loadMemberList = async () => {
  const res = await listMember({ pageNum: 1, pageSize: 1000 });
  memberList.value = res.data.rows || [];
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  projectMemberFormRef.value?.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: ProjectMemberVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加人员项目管理";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: ProjectMemberVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getProjectMember(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改人员项目管理";
}

/** 提交按钮 */
const submitForm = () => {
  projectMemberFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateProjectMember(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addProjectMember(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ProjectMemberVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除人员项目管理编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delProjectMember(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('osc/projectMember/export', {
    ...queryParams.value
  }, `projectMember_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
  loadProjectList();
  loadMemberList();
});
</script>
