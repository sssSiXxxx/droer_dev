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
              <el-select v-model="queryParams.status" placeholder="请选择项目状态" clearable>
                <el-option v-for="dict in osc_project_status" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="技术栈" prop="techStack">
              <el-select v-model="queryParams.techStack" placeholder="请选择技术栈" clearable>
                <el-option v-for="dict in osc_project_tech" :key="dict.value" :label="dict.label" :value="dict.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="编程语言" prop="programmingLanguage">
              <el-select v-model="queryParams.programmingLanguage" placeholder="请选择编程语言" clearable>
                <el-option v-for="dict in osc_project_prolan" :key="dict.value" :label="dict.label" :value="dict.value" />
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
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['osc:project:remove']"
              >删除</el-button
            >
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
        <template #empty>
          <el-empty :description="loading ? '加载中...' : '暂无数据'" />
        </template>
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="序号" align="center" width="60">
          <template #default="scope">
            {{ (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="项目ID" align="center" prop="projectId" v-if="false" />

        <!-- 项目名称 -->
        <el-table-column label="项目名称" align="center" prop="projectName" min-width="120" :show-overflow-tooltip="true">
          <template #default="scope">
            <div class="project-name-cell">
              <el-link
                v-if="scope.row.websiteUrl"
                :href="scope.row.websiteUrl"
                target="_blank"
                type="primary"
                :underline="false"
                class="project-name-link"
              >
                {{ scope.row.projectName }}
              </el-link>
              <span v-else>{{ scope.row.projectName }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- 项目描述 -->
        <el-table-column label="项目描述" align="center" prop="description" min-width="180" :show-overflow-tooltip="true">
          <template #default="scope">
            <div class="description-cell">
              {{ scope.row.description || '暂无描述' }}
            </div>
          </template>
        </el-table-column>

        <!-- 技术栈 -->
        <el-table-column label="技术栈" align="center" prop="techStack" min-width="150">
          <template #default="scope">
            <div v-if="scope.row.techStack && typeof scope.row.techStack === 'string'" class="tech-stack">
              <el-tag v-for="tech in scope.row.techStack.split(',')" :key="tech" size="small" type="info" class="tech-tag">
                {{ getDictLabel(techStackDict, tech) }}
              </el-tag>
            </div>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>

        <!-- 编程语言 -->
        <el-table-column label="编程语言" align="center" prop="programmingLanguage" min-width="150">
          <template #default="scope">
            <div v-if="scope.row.programmingLanguage && typeof scope.row.programmingLanguage === 'string'" class="programming-language">
              <el-tag v-for="lang in scope.row.programmingLanguage.split(',')" :key="lang" size="small" type="success" class="lang-tag">
                {{ getDictLabel(programmingLanguageDict, lang) }}
              </el-tag>
            </div>
            <span v-else class="text-gray-400">-</span>
          </template>
        </el-table-column>

        <!-- 状态 -->
        <el-table-column label="状态" align="center" prop="status" min-width="80">
          <template #default="scope">
            <dict-tag :options="osc_project_status" :value="scope.row.status" />
          </template>
        </el-table-column>

        <!-- 详细信息 -->
        <el-table-column label="详细信息" align="center" prop="description" min-width="120">
          <template #default="scope">
            <el-button type="primary" link @click="showProjectDetail(scope.row)" class="detail-btn">
              <el-icon><View /></el-icon>
              查看详情
            </el-button>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
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
          <image-upload v-model="form.logoUrl" />
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择项目状态" style="width: 100%">
            <el-option v-for="dict in osc_project_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="技术栈" prop="techStack">
          <el-select
            v-model="form.techStack"
            multiple
            filterable
            placeholder="搜索或选择技术栈"
            style="width: 100%"
            :filter-method="filterTechStack"
            :reserve-keyword="true"
          >
            <el-option v-for="dict in filteredTechStack" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="编程语言" prop="programmingLanguage">
          <el-select
            v-model="form.programmingLanguage"
            multiple
            filterable
            placeholder="搜索或选择编程语言"
            style="width: 100%"
            :filter-method="filterProgrammingLanguage"
            :reserve-keyword="true"
          >
            <el-option v-for="dict in filteredProgrammingLanguage" :key="dict.value" :label="dict.label" :value="dict.value" />
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
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">
            <el-checkbox v-model="upload.updateSupport" />
            是否更新已经存在的数据，仅更新模式支持。
            <br />
            <span>仅允许导入xls、xlsx格式文件。</span>
            <br />
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
import { View } from '@element-plus/icons-vue';
import { useRoute } from 'vue-router';
import { ElMessageBox } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
// 尝试不同的字典类型名称
const { osc_project_status, osc_project_tech, osc_project_prolan } = toRefs<any>(
  proxy?.useDict('osc_project_status', 'osc_project_tech', 'osc_project_prolan')
);

// 如果编程语言字典为空，尝试其他可能的名称
const programmingLanguageDict = computed(() => {
  if (osc_project_prolan.value && osc_project_prolan.value.length > 0) {
    return osc_project_prolan.value;
  }
  // 如果为空，返回一个默认的编程语言列表
  return [
    { label: 'Java', value: '1' },
    { label: 'Python', value: '2' },
    { label: 'Go', value: '3' },
    { label: 'C', value: '4' },
    { label: 'C++', value: '5' },
    { label: 'JavaScript', value: '6' },
    { label: 'Vue', value: '7' },
    { label: 'PHP', value: '8' },
    { label: 'Swift', value: '9' },
    { label: 'Kotlin', value: '10' },
    { label: 'TypeScript', value: '11' },
    { label: 'Rust', value: '12' },
    { label: 'Scala', value: '13' },
    { label: 'Perl', value: '14' },
    { label: 'Lua', value: '15' },
    { label: 'R', value: '16' },
    { label: 'Shell', value: '17' },
    { label: 'MATLAB', value: '18' },
    { label: 'HTML', value: '19' }
  ];
});

// 技术栈字典数据（更新后的完整列表）
const techStackDict = computed(() => {
  // 创建完整的技术栈字典
  const completeDict = [
    { value: '1', label: 'Spring Boot' },
    { value: '2', label: 'Spring Cloud' },
    { value: '3', label: 'Docker' },
    { value: '4', label: 'MyBatis-Plus' },
    { value: '5', label: '微服务架构' },
    { value: '6', label: 'DevOps' },
    { value: '7', label: '云原生技术' },
    { value: '8', label: '云计算技术' },
    { value: '9', label: '分布式系统' },
    { value: '10', label: '数据库技术' },
    { value: '11', label: 'NoSQL' },
    { value: '12', label: 'Elasticsearch' },
    { value: '13', label: 'Apache Kafka' },
    { value: '14', label: 'Redis' },
    { value: '15', label: 'Nginx' },
    { value: '16', label: 'Apache Mesos' },
    { value: '17', label: 'RabbitMQ' },
    { value: '18', label: 'Prometheus' },
    { value: '19', label: 'Grafana' },
    { value: '20', label: 'Netty' },
    { value: '21', label: 'gRPC' },
    { value: '22', label: 'Zookeeper' },
    { value: '23', label: '机器学习' },
    { value: '24', label: '大数据技术' },
    { value: '25', label: 'Hadoop' },
    { value: '26', label: 'Dubbo' },
    { value: '27', label: 'Nacos' },
    { value: '28', label: 'Seata' },
    { value: '29', label: 'Sentinel' },
    { value: '30', label: 'Spring Security' },
    { value: '31', label: 'Sa-Token' },
    { value: '32', label: 'MySQL' },
    { value: '33', label: 'PostgreSQL' },
    { value: '34', label: 'MongoDB' },
    { value: '35', label: 'RocketMQ' },
    { value: '36', label: 'Kubernetes' },
    { value: '37', label: 'Vue 3' },
    { value: '38', label: 'React' },
    { value: '39', label: 'TypeScript' },
    { value: '40', label: 'SkyWalking' },
    { value: '41', label: 'Maven' },
    { value: '42', label: 'Jenkins' },
    { value: '43', label: 'OAuth 2.0' },
    { value: '44', label: 'JWT' },
    { value: '45', label: 'JUnit 5' }
  ];

  // 合并字典数据，确保包含所有标签
  return [...(osc_project_tech.value || []), ...completeDict];
});

// 过滤后的技术栈列表
const filteredTechStack = ref(techStackDict.value);

// 过滤后的编程语言列表
const filteredProgrammingLanguage = ref(programmingLanguageDict.value);

// 技术栈搜索过滤函数
const filterTechStack = (query: string) => {
  if (query === '') {
    filteredTechStack.value = techStackDict.value;
  } else {
    filteredTechStack.value = techStackDict.value.filter((item) => item.label.toLowerCase().includes(query.toLowerCase()));
  }
};

// 编程语言搜索过滤函数
const filterProgrammingLanguage = (query: string) => {
  if (query === '') {
    filteredProgrammingLanguage.value = programmingLanguageDict.value;
  } else {
    filteredProgrammingLanguage.value = programmingLanguageDict.value.filter((item) => item.label.toLowerCase().includes(query.toLowerCase()));
  }
};

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
  title: '',
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的数据
  updateSupport: false,
  // 设置上传的请求头部
  headers: { Authorization: 'Bearer ' + useUserStore().token },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + '/osc/project/importData'
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
  remark: undefined,
  techStack: undefined,
  programmingLanguage: undefined,
  coreContributors: undefined,
  contactInfo: undefined,
  versionInfo: undefined,
  starCount: undefined,
  forkCount: undefined,
  issuesCount: undefined,
  prCount: undefined,
  readmeUrl: undefined,
  wikiUrl: undefined,
  apiDocUrl: undefined,
  lastCommitTime: undefined
};
const data = reactive<PageData<ProjectForm, ProjectQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    projectName: undefined,
    status: undefined,
    techStack: undefined,
    programmingLanguage: undefined,
    createBy: undefined,
    params: {}
  },
  rules: {
    projectName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
    description: [{ required: true, message: '项目描述不能为空', trigger: 'blur' }],
    repositoryUrl: [{ required: true, message: '代码仓库不能为空', trigger: 'blur' }],
    status: [{ required: true, message: '项目状态不能为空', trigger: 'change' }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 根据字典值获取标签 */
const getDictLabel = (dictList: any[], value: string) => {
  const trimmedValue = value.trim();
  // 先尝试按value查找
  let dict = dictList.find((item) => item.value === trimmedValue);
  // 如果没找到，再尝试按label查找（处理一些特殊值）
  if (!dict) {
    dict = dictList.find((item) => item.label === trimmedValue);
  }
  return dict ? dict.label : value;
};

/** 查询项目列表列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await listProject(queryParams.value);
    projectList.value = res.rows;
    total.value = res.total;
  } catch (error) {
    console.error('获取项目列表失败:', error);
    proxy?.$modal.msgError('获取项目列表失败，请稍后重试');
    projectList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
};

/** 表单重置 */
const reset = () => {
  form.value = { ...initFormData };
  // 确保多选字段初始化为数组
  (form.value as any).techStack = [];
  (form.value as any).programmingLanguage = [];
  projectFormRef.value?.resetFields();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  queryParams.value.projectName = undefined;
  queryParams.value.techStack = undefined;
  queryParams.value.programmingLanguage = undefined;
  queryParams.value.params = {
    statusList: ['2', '3', '4'] // 默认显示进行中、已完成和已归档的项目
  };
  queryParams.value.status = undefined;
  queryParams.value.createBy = undefined;
  handleQuery();
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: ProjectVO[]) => {
  ids.value = selection.map((item) => item.projectId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = '添加项目列表';
};

/** 修改按钮操作 */
const handleUpdate = async (row?: ProjectVO) => {
  reset();
  const _projectId = row?.projectId || ids.value[0];
  const res = await getProject(_projectId);
  Object.assign(form.value, res.data);

  // 处理多选字段：将字符串转换为数组
  if (form.value.techStack && typeof form.value.techStack === 'string') {
    (form.value as any).techStack = form.value.techStack.split(',').filter((item: string) => item.trim());
  } else {
    (form.value as any).techStack = [];
  }

  if (form.value.programmingLanguage && typeof form.value.programmingLanguage === 'string') {
    (form.value as any).programmingLanguage = form.value.programmingLanguage.split(',').filter((item: string) => item.trim());
  } else {
    (form.value as any).programmingLanguage = [];
  }

  dialog.visible = true;
  dialog.title = '修改项目列表';
};

/** 提交按钮 */
const submitForm = () => {
  projectFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;

      // 处理多选字段：将数组转换为字符串
      const submitData = { ...form.value };
      if (Array.isArray(submitData.techStack)) {
        submitData.techStack = submitData.techStack.join(',');
      }
      if (Array.isArray(submitData.programmingLanguage)) {
        submitData.programmingLanguage = submitData.programmingLanguage.join(',');
      }

      if (form.value.projectId) {
        await updateProject(submitData).finally(() => (buttonLoading.value = false));
      } else {
        await addProject(submitData).finally(() => (buttonLoading.value = false));
      }
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/** 查看详情按钮操作 */
const handleViewDetail = (row: ProjectVO) => {
  router.push(`/osc/project/detail/${row.projectId}`);
};

/** 删除按钮操作 */
const handleDelete = async (row?: ProjectVO) => {
  const _projectIds = row?.projectId || ids.value;
  await proxy?.$modal.confirm('是否确认删除项目列表编号为"' + _projectIds + '"的数据项？').finally(() => (loading.value = false));
  await delProject(_projectIds);
  proxy?.$modal.msgSuccess('删除成功');
  await getList();
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'osc/project/export',
    {
      ...queryParams.value
    },
    `project_${new Date().getTime()}.xlsx`
  );
};

/** 文件上传中处理 */
const handleFileUploadProgress = (event: any, file: any) => {
  upload.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response: any, file: any) => {
  upload.open = false;
  upload.isUploading = false;
  uploadRef.value.clearFiles();
  proxy?.$modal.msgSuccess('导入成功');
  getList();
};

/** 导入按钮操作 */
const handleImport = () => {
  upload.title = '项目数据导入';
  upload.open = true;
};

/** 下载模板操作 */
const downloadTemplate = () => {
  proxy?.download('osc/project/importTemplate', {}, `project_template_${new Date().getTime()}.xlsx`);
};

/** 文件上传中处理 */
const submitFileForm = () => {
  uploadRef.value.submit();
};

/** 查看项目详情 */
const showProjectDetail = (row: ProjectVO) => {
  // 获取字典标签的辅助函数
  const getDictLabelFromValue = (dictList: any[], value: string) => {
    const dict = dictList.find((item) => item.value === value);
    return dict ? dict.label : value;
  };

  // 处理多选字段的显示
  const formatMultiSelect = (value: string, dictList: any[]) => {
    if (!value) return '暂无信息';
    return value
      .split(',')
      .map((item) => getDictLabelFromValue(dictList, item.trim()))
      .join('、');
  };

  // 创建项目详情对话框
  ElMessageBox.alert(
    `
    <div style="text-align: left;">
      <h3 style="margin-bottom: 20px; color: #333; text-align: center; background-color: #fdfde7; padding: 15px;">${row.projectName}</h3>
      
      <div style="margin-bottom: 2px; padding: 12px; background-color: white;">
        <strong style="color: #333;">项目描述：</strong>
        <p style="margin: 5px 0; color: #666;">${row.description || '暂无描述'}</p>
      </div>
      
      <div style="margin-bottom: 2px; padding: 12px; background-color: #f0f9f0;">
        <strong style="color: #333;">技术栈：</strong>
        <p style="margin: 5px 0; color: #666;">${formatMultiSelect(row.techStack, techStackDict.value)}</p>
      </div>
      
      <div style="margin-bottom: 2px; padding: 12px; background-color: white;">
        <strong style="color: #333;">编程语言：</strong>
        <p style="margin: 5px 0; color: #666;">${formatMultiSelect(row.programmingLanguage, programmingLanguageDict.value)}</p>
      </div>
      
      <div style="margin-bottom: 2px; padding: 12px; background-color: #f0f9f0;">
        <strong style="color: #333;">代码仓库：</strong>
        <p style="margin: 5px 0;">
          <a href="${row.repositoryUrl}" target="_blank" style="color: #409EFF;">${row.repositoryUrl || '暂无仓库地址'}</a>
        </p>
      </div>
      
      <div style="margin-bottom: 2px; padding: 12px; background-color: white;">
        <strong style="color: #333;">项目网站：</strong>
        <p style="margin: 5px 0;">
          <a href="${row.websiteUrl}" target="_blank" style="color: #409EFF;">${row.websiteUrl || '暂无网站地址'}</a>
        </p>
      </div>
      
      <div style="margin-bottom: 2px; padding: 12px; background-color: #f0f9f0;">
        <strong style="color: #333;">项目状态：</strong>
        <p style="margin: 5px 0; color: #666;">${getDictLabelFromValue(osc_project_status.value, row.status) || '暂无状态信息'}</p>
      </div>
      
      <div style="margin-bottom: 2px; padding: 12px; background-color: white;">
        <strong style="color: #333;">备注：</strong>
        <p style="margin: 5px 0; color: #666;">${row.remark || '暂无备注'}</p>
      </div>
    </div>
  `,
    '项目详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定',
      customClass: 'project-detail-dialog'
    }
  );
};

onMounted(() => {
  // 从路由参数中获取查询条件
  const route = useRoute();

  // 处理路由参数
  if (route.query.status) {
    // 设置状态
    queryParams.value.status = route.query.status as string;
    // 如果是草稿状态，清除状态列表
    if (route.query.status === '0') {
      queryParams.value.params = {};
    }
  } else {
    // 默认显示进行中、已完成和已归档的项目
    queryParams.value.params = {
      statusList: ['2', '3', '4']
    };
  }

  // 处理创建者参数
  if (route.query.createBy) {
    queryParams.value.createBy = route.query.createBy as string;
  }

  // 处理状态列表参数
  if (route.query.params) {
    try {
      const params = JSON.parse(route.query.params as string);
      if (params.statusList) {
        queryParams.value.params = params;
      }
    } catch (error) {
      console.error('解析params参数失败:', error);
    }
  }

  // 如果是查看草稿箱，必须带上用户ID
  if (route.query.status === '0') {
    const userId = useUserStore().userId;
    if (userId) {
      queryParams.value.createBy = userId;
    }
  }

  // 初始化过滤列表
  filteredTechStack.value = techStackDict.value;
  filteredProgrammingLanguage.value = programmingLanguageDict.value;

  // 获取列表数据
  handleQuery();
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

/* 项目名称链接 */
.project-name-link {
  font-weight: 500;
  color: #409eff;
  transition: all 0.3s ease;
  text-decoration: none;
}

.project-name-link:hover {
  color: #66b1ff;
  transform: translateY(-1px);
}

/* 详情按钮 */
.detail-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}

/* 技术栈标签 */
.tech-stack {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 4px;
}

.tech-stack .el-tag {
  margin: 2px;
}

.tech-tag {
  margin: 2px;
  border-radius: 12px;
  font-size: 11px;
  padding: 2px 6px;
}

/* 编程语言标签 */
.programming-language {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 4px;
  padding: 4px;
  max-width: 100%;
}

.lang-tag {
  margin: 2px;
  border-radius: 12px;
  font-size: 11px;
  padding: 2px 8px;
  min-width: 40px;
  max-width: none;
  white-space: nowrap;
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

/* 项目详情对话框样式 */
:deep(.project-detail-dialog) {
  .el-message-box__content {
    padding: 0;
  }

  .el-message-box__container {
    margin: 0;
  }

  .el-message-box__message {
    padding: 0;
  }

  .el-message-box {
    padding: 0;
    max-width: 600px;
    width: 90%;
    border-radius: 8px;
    overflow: hidden;
  }

  h3 {
    margin: 0;
    padding: 20px;
    background: #fdfde7;
    border-bottom: 1px solid #f3f3e0;
    font-size: 18px;
    font-weight: 600;
    text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.05);
  }

  strong {
    display: inline-block;
    width: 100px;
  }

  p {
    display: inline-block;
    margin-left: 10px;
  }

  .el-message-box__btns {
    padding: 15px;
    background: #f8fdfb;
    border-top: 1px solid #eee;
  }
}
</style>
