<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover" class="search-card">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="项目状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择项目状态" clearable >
                <el-option v-for="dict in osc_project_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
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

    <el-card shadow="never" class="main-card">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['osc:project:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['osc:project:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['osc:project:remove']">删除</el-button>
          </el-col>
                     <el-col :span="1.5">
             <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['osc:project:export']">导出</el-button>
           </el-col>
           <el-col :span="1.5">
             <el-button type="info" plain icon="Upload" @click="handleImport" v-hasPermi="['osc:project:import']">导入</el-button>
           </el-col>
           <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

             <el-table v-loading="loading" border :data="projectList" @selection-change="handleSelectionChange" class="responsive-table">
         <el-table-column type="selection" width="50" align="center" />
         <el-table-column label="序号" align="center" width="60">
           <template #default="scope">
             {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
           </template>
         </el-table-column>
         <el-table-column label="项目ID" align="center" prop="projectId" v-if="false" />
         <el-table-column label="项目名称" align="center" prop="projectName" min-width="150">
           <template #default="scope">
             <el-tooltip
               :content="scope.row.projectName"
               placement="top"
               :show-after="500"
             >
               <div class="project-name-cell">
                 {{ scope.row.projectName }}
               </div>
             </el-tooltip>
           </template>
         </el-table-column>
         <el-table-column label="项目描述" align="center" prop="description" min-width="200">
           <template #default="scope">
             <el-tooltip
               :content="scope.row.description"
               placement="top"
               :show-after="500"
               :hide-after="3000"
             >
               <div class="description-cell">
                 {{ scope.row.description }}
               </div>
             </el-tooltip>
           </template>
         </el-table-column>
         <el-table-column label="代码仓库" align="center" prop="repositoryUrl" min-width="120">
           <template #default="scope">
             <el-link
               v-if="scope.row.repositoryUrl"
               :href="scope.row.repositoryUrl"
               target="_blank"
               type="primary"
               :underline="false"
               class="link-button"
             >
               <el-icon><Link /></el-icon>
               查看仓库
             </el-link>
             <span v-else class="text-gray-400">-</span>
           </template>
         </el-table-column>
         <el-table-column label="项目网站" align="center" prop="websiteUrl" min-width="120">
           <template #default="scope">
             <el-link
               v-if="scope.row.websiteUrl"
               :href="scope.row.websiteUrl"
               target="_blank"
               type="primary"
               :underline="false"
               class="link-button"
             >
               <el-icon><Link /></el-icon>
               访问网站
             </el-link>
             <span v-else class="text-gray-400">-</span>
           </template>
         </el-table-column>
         <el-table-column label="Logo" align="center" prop="logoUrl" width="80">
           <template #default="scope">
             <image-preview :src="scope.row.logoUrlUrl" :width="40" :height="40"/>
           </template>
         </el-table-column>
         <el-table-column label="项目状态" align="center" prop="status" width="90">
           <template #default="scope">
             <dict-tag :options="osc_project_status" :value="scope.row.status"/>
           </template>
         </el-table-column>
         <el-table-column label="备注" align="center" prop="remark" min-width="100">
           <template #default="scope">
             <el-tooltip
               v-if="scope.row.remark"
               :content="scope.row.remark"
               placement="top"
               :show-after="500"
             >
               <div class="remark-cell">
                 {{ scope.row.remark }}
               </div>
             </el-tooltip>
             <span v-else class="text-gray-400">-</span>
           </template>
         </el-table-column>
         <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="100">
           <template #default="scope">
             <el-tooltip content="修改" placement="top">
               <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['osc:project:edit']"></el-button>
             </el-tooltip>
             <el-tooltip content="删除" placement="top">
               <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['osc:project:remove']"></el-button>
             </el-tooltip>
           </template>
         </el-table-column>
       </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改项目列表对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="projectFormRef" :model="form" :rules="rules" label-width="80px" class="dialog-form">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" placeholder="请输入项目名称" />
        </el-form-item>

        <el-form-item label="项目描述" prop="description">
            <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="代码仓库" prop="repositoryUrl">
          <el-input v-model="form.repositoryUrl" placeholder="请输入仓库地址" />
        </el-form-item>
        <el-form-item label="项目网站" prop="websiteUrl">
          <el-input v-model="form.websiteUrl" placeholder="请输入项目网址" />
        </el-form-item>
        <el-form-item label="项目Logo" prop="logoUrl">
          <image-upload v-model="form.logoUrl"/>
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择项目状态" style="width: 100%">
            <el-option
              v-for="dict in osc_project_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
             </template>
     </el-dialog>
     
     <!-- 导入对话框 -->
     <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
       <el-upload
         ref="uploadRef"
         :limit="1"
         accept=".xlsx,.xls"
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
           将文件拖到此处，或<em>点击上传</em>
         </div>
         <template #tip>
           <div class="el-upload__tip">
             <el-checkbox v-model="upload.updateSupport" />
             是否更新已经存在的数据，仅更新模式支持。
             <br>
             <span>仅允许导入xls、xlsx格式文件。</span>
             <br>
             <span>模板下载：<el-button type="text" @click="downloadTemplate">下载模板</el-button></span>
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

<script setup name="Project" lang="ts">
import { listProject, getProject, delProject, addProject, updateProject } from '@/api/osc/project';
import { ProjectVO, ProjectQuery, ProjectForm } from '@/api/osc/project/types';
import { useUserStore } from '@/store/modules/user';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { osc_project_status } = toRefs<any>(proxy?.useDict('osc_project_status'));

const projectList = ref<ProjectVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

// 文件上传相关
const uploadRef = ref();
const upload = reactive({
  // 是否显示弹出层
  open: false,
  // 弹出层标题
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的数据
  updateSupport: false,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + useUserStore().token },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/osc/project/importData"
});

const queryFormRef = ref<ElFormInstance>();
const projectFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ProjectForm = {
  projectId: undefined,
  projectName: undefined,
  projectCode: undefined,
  description: undefined,
  repositoryUrl: undefined,
  websiteUrl: undefined,
  logoUrl: undefined,
  status: undefined,
  remark: undefined
}
const data = reactive<PageData<ProjectForm, ProjectQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    projectName: undefined,
    status: undefined,
    params: {
    }
  },
  rules: {
    projectName: [
      { required: true, message: "项目名称不能为空", trigger: "blur" }
    ],
    description: [
      { required: true, message: "项目描述不能为空", trigger: "blur" }
    ],
    repositoryUrl: [
      { required: true, message: "代码仓库不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "项目状态不能为空", trigger: "change" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询项目列表列表 */
const getList = async () => {
  loading.value = true;
  const res = await listProject(queryParams.value);
  projectList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  projectFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: ProjectVO[]) => {
  ids.value = selection.map(item => item.projectId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加项目列表";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: ProjectVO) => {
  reset();
  const _projectId = row?.projectId || ids.value[0]
  const res = await getProject(_projectId);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改项目列表";
}

/** 提交按钮 */
const submitForm = () => {
  projectFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.projectId) {
        await updateProject(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addProject(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ProjectVO) => {
  const _projectIds = row?.projectId || ids.value;
  await proxy?.$modal.confirm('是否确认删除项目列表编号为"' + _projectIds + '"的数据项？').finally(() => loading.value = false);
  await delProject(_projectIds);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('osc/project/export', {
    ...queryParams.value
  }, `project_${new Date().getTime()}.xlsx`)
}

/** 文件上传中处理 */
const handleFileUploadProgress = (event: any, file: any) => {
  upload.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response: any, file: any) => {
  upload.open = false;
  upload.isUploading = false;
  uploadRef.value.clearFiles();
  proxy?.$modal.msgSuccess("导入成功");
  getList();
};

/** 导入按钮操作 */
const handleImport = () => {
  upload.title = "项目数据导入";
  upload.open = true;
};

/** 下载模板操作 */
const downloadTemplate = () => {
  proxy?.download('osc/project/importTemplate', {
  }, `project_template_${new Date().getTime()}.xlsx`)
};

/** 文件上传中处理 */
const submitFileForm = () => {
  uploadRef.value.submit();
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
/* 响应式表格 */
.responsive-table {
  width: 100%;
  overflow-x: auto;
}

/* 表格容器 */
.el-card {
  margin-bottom: 20px;
}

/* 搜索区域 */
.search-card {
  margin-bottom: 16px;
}

/* 主卡片 */
.main-card {
  border-radius: 8px;
}

/* 表格样式 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.el-table td {
  padding: 12px 8px;
}

/* 序号列 */
.serial-number {
  font-weight: 600;
  color: #666;
}

/* 项目名称 */
.project-name-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  max-width: 100%;
  text-align: center;
  font-weight: 500;
  color: #333;
  transition: color 0.3s ease;
}

.project-name-cell:hover {
  color: #409eff;
}

/* 项目描述 */
.description-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  max-width: 100%;
  text-align: left;
  color: #666;
  line-height: 1.4;
  transition: color 0.3s ease;
}

.description-cell:hover {
  color: #409eff;
}

/* 备注 */
.remark-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  max-width: 100%;
  text-align: left;
  color: #999;
  font-size: 13px;
  transition: color 0.3s ease;
}

.remark-cell:hover {
  color: #409eff;
}

/* 链接按钮 */
.link-button {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
  font-size: 13px;
}

.link-button:hover {
  background-color: #f0f9ff;
  transform: translateY(-1px);
}

/* Logo容器 */
.logo-container {
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 6px;
  overflow: hidden;
}

/* 对话框样式 */
.dialog-form {
  padding: 20px 0;
}

.dialog-form .el-form-item {
  margin-bottom: 20px;
}

.dialog-form .el-input,
.dialog-form .el-select {
  width: 100%;
}

.dialog-form .el-textarea {
  width: 100%;
}

/* 按钮样式 */
.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .el-table {
    font-size: 13px;
  }
  
  .el-table td {
    padding: 8px 6px;
  }
}

@media (max-width: 768px) {
  .el-table {
    font-size: 12px;
  }
  
  .project-name-cell,
  .description-cell,
  .remark-cell {
    max-width: 80px;
  }
  
  .link-button {
    font-size: 12px;
    padding: 2px 6px;
  }
}

/* 表格行悬停效果 */
.el-table tbody tr:hover > td {
  background-color: #f5f7fa !important;
}

/* 状态标签样式 */
.el-tag {
  border-radius: 12px;
  font-size: 12px;
  padding: 2px 8px;
}

/* 操作按钮样式 */
.el-button--link {
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.el-button--link:hover {
  background-color: #f0f9ff;
  transform: translateY(-1px);
}
</style>
