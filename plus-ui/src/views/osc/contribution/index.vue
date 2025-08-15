<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="成员姓名" prop="memberName">
              <el-input v-model="queryParams.memberName" placeholder="请输入成员姓名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="贡献类型" prop="contributionType">
              <el-select v-model="queryParams.contributionType" placeholder="请选择贡献类型" clearable >
                <el-option v-for="dict in osc_user_cbt" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['osc:contribution:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['osc:contribution:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['osc:contribution:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['osc:contribution:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="contributionList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="贡献ID" align="center" prop="contributionId" v-if="false" />
        <el-table-column label="成员姓名" align="center" prop="memberName" />
        <el-table-column label="项目名称" align="center" prop="projectName" />
        <el-table-column label="贡献类型" align="center" prop="contributionType">
          <template #default="scope">
            <dict-tag :options="osc_user_cbt" :value="scope.row.contributionType"/>
          </template>
        </el-table-column>
        <el-table-column label="贡献内容" align="center" prop="content" />
        <el-table-column label="贡献时间" align="center" prop="contributionTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.contributionTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="贡献点数" align="center" prop="points" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['osc:contribution:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['osc:contribution:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改贡献统计对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="contributionFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="成员姓名" prop="memberId">
          <el-select v-model="form.memberId" placeholder="请选择成员" filterable>
            <el-option v-for="member in memberList" :key="member.memberId" :label="member.memberName" :value="member.memberId" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目名称" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择项目" filterable>
            <el-option v-for="project in projectList" :key="project.projectId" :label="project.projectName" :value="project.projectId" />
          </el-select>
        </el-form-item>
        <el-form-item label="贡献类型" prop="contributionType">
          <el-select v-model="form.contributionType" placeholder="请选择贡献类型">
            <el-option
                v-for="dict in osc_user_cbt"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="贡献内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="相关链接" prop="url">
          <el-input v-model="form.url" placeholder="请输入相关链接" />
        </el-form-item>
        <el-form-item label="贡献时间" prop="contributionTime">
          <el-date-picker clearable
            v-model="form.contributionTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择贡献时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="贡献点数" prop="points">
          <el-input v-model="form.points" placeholder="请输入贡献点数" />
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

<script setup name="Contribution" lang="ts">
import { listContribution, getContribution, delContribution, addContribution, updateContribution } from '@/api/osc/contribution';
import { ContributionVO, ContributionQuery, ContributionForm } from '@/api/osc/contribution/types';
import { listProject } from '@/api/osc/project';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { osc_user_cbt } = toRefs<any>(proxy?.useDict('osc_user_cbt'));

const contributionList = ref<ContributionVO[]>([]);
const memberList = ref<any[]>([]);
const projectList = ref<any[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const contributionFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ContributionForm = {
  memberId: undefined,
  projectId: undefined,
  contributionType: undefined,
  content: undefined,
  url: undefined,
  contributionTime: undefined,
  points: undefined,
}
const data = reactive<PageData<ContributionForm, ContributionQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    memberId: undefined,
    projectId: undefined,
    contributionType: undefined,
    content: undefined,
    params: {
    }
  },
  rules: {
    memberId: [
      { required: true, message: "成员ID不能为空", trigger: "blur" }
    ],
    projectId: [
      { required: true, message: "项目ID不能为空", trigger: "blur" }
    ],
    contributionType: [
      { required: true, message: "贡献类型不能为空", trigger: "change" }
    ],
    content: [
      { required: true, message: "贡献内容不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询贡献统计列表 */
const getList = async () => {
  loading.value = true;
  const res = await listContribution(queryParams.value);
  contributionList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 获取成员列表 */
const getMemberList = async () => {
  try {
    // 暂时使用模拟数据，后续可以替换为真实的API调用
    memberList.value = [
      { memberId: 1, memberName: '张三' },
      { memberId: 2, memberName: '李四' },
      { memberId: 3, memberName: '王五' },
      { memberId: 4, memberName: '赵六' },
      { memberId: 5, memberName: '钱七' }
    ];
  } catch (error) {
    console.error('获取成员列表失败:', error);
  }
}

/** 获取项目列表 */
const getProjectList = async () => {
  try {
    const res = await listProject({ pageSize: 1000 });
    projectList.value = (res.rows as unknown as any[]) || [];
  } catch (error) {
    console.error('获取项目列表失败:', error);
    // 使用模拟数据
    projectList.value = [
      { projectId: 1, projectName: 'Sa-Token' },
      { projectId: 2, projectName: 'Hutool' },
      { projectId: 3, projectName: 'Forest' },
      { projectId: 4, projectName: 'TLog' },
      { projectId: 5, projectName: 'Dynamic-Tp' }
    ];
  }
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  contributionFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: ContributionVO[]) => {
  ids.value = selection.map(item => item.contributionId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加贡献统计";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: ContributionVO) => {
  reset();
  const _contributionId = row?.contributionId || ids.value[0]
  const res = await getContribution(_contributionId);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改贡献统计";
}

/** 提交按钮 */
const submitForm = () => {
  contributionFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.contributionId) {
        await updateContribution(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addContribution(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ContributionVO) => {
  const _contributionIds = row?.contributionId || ids.value;
  await proxy?.$modal.confirm('是否确认删除贡献统计编号为"' + _contributionIds + '"的数据项？').finally(() => loading.value = false);
  await delContribution(_contributionIds);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('osc/contribution/export', {
    ...queryParams.value
  }, `contribution_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
  getMemberList();
  getProjectList();
});
</script>
