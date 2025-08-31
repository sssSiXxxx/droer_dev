<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover" class="search-card">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="项目描述" prop="description">
              <el-input v-model="queryParams.description" placeholder="请输入项目描述关键字" clearable />
            </el-form-item>
            <el-form-item label="负责人" prop="userId">
              <el-select v-model="queryParams.userId" placeholder="请选择负责人" clearable filterable @focus="getUserList">
                <el-option v-for="user in userList" :key="user.userId" :label="user.nickName" :value="user.userId" />
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

      <el-table v-loading="loading" border :data="projectList" @selection-change="handleSelectionChange" @sort-change="handleSortChange" class="responsive-table">
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

        <!-- 项目名称和描述 -->
        <el-table-column label="项目信息" align="center" prop="projectName" min-width="280" :show-overflow-tooltip="false">
          <template #default="scope">
            <div class="project-info-cell">
              <div class="project-name">
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
                <span v-else class="project-name-text">{{ scope.row.projectName }}</span>
              </div>
              <div v-if="scope.row.description" class="project-description">
                {{ scope.row.description }}
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 项目负责人 -->
        <el-table-column label="项目负责人" align="center" prop="userId" min-width="120" :show-overflow-tooltip="true">
          <template #default="scope">
            <div class="maintainer-cell">
              <span v-if="scope.row.userId">{{ getUserNickName(scope.row.userId) }}</span>
              <span v-else class="text-gray-400">未设置</span>
            </div>
          </template>
        </el-table-column>

        <!-- Star数 -->
        <el-table-column label="Star数" align="center" prop="starCount" min-width="100" sortable="custom">
          <template #default="scope">
            <div class="star-cell">
              <el-icon class="star-icon"><Star /></el-icon>
              <span>{{ formatNumber(scope.row.starCount) }}</span>
            </div>
          </template>
        </el-table-column>

        <!-- Fork数 -->
        <el-table-column label="Fork数" align="center" prop="forkCount" min-width="100" sortable="custom">
          <template #default="scope">
            <div class="fork-cell">
              <el-icon class="fork-icon"><Share /></el-icon>
              <span>{{ formatNumber(scope.row.forkCount) }}</span>
            </div>
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
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['osc:project:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="贡献统计" placement="top">
              <el-button link type="success" icon="TrendCharts" @click="goToContributors(scope.row)"></el-button>
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
      <el-form ref="projectFormRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" placeholder="请输入项目名称" />
        </el-form-item>

        <el-form-item label="项目描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入项目描述" />
        </el-form-item>

        <el-form-item label="代码仓库" prop="repositoryUrl">
          <el-input v-model="form.repositoryUrl" placeholder="请输入GitHub/Gitee仓库地址" />
        </el-form-item>

        <el-form-item label="项目网站" prop="websiteUrl">
          <el-input v-model="form.websiteUrl" placeholder="请输入项目网址（可选）" />
        </el-form-item>

        <el-form-item label="项目负责人" prop="userId">
          <el-select 
            v-model="form.userId" 
            placeholder="请选择项目负责人" 
            filterable 
            clearable
            style="width: 100%"
            @focus="getUserList"
          >
            <el-option 
              v-for="user in userList" 
              :key="user.userId" 
              :label="user.nickName" 
              :value="user.userId"
            >
              <span>{{ user.nickName }}</span>
              <span style="float: right; color: #999; font-size: 12px">{{ user.userName }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="项目Logo" prop="logoUrl">
          <image-upload v-model="form.logoUrl" />
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
import { listUser } from '@/api/system/user';
import { ProjectVO, ProjectQuery, ProjectForm } from '@/api/osc/project/types';
import { UserVO } from '@/api/system/user/types';
import { useUserStore } from '@/store/modules/user';
import { View, TrendCharts, Star, Share } from '@element-plus/icons-vue';
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
  // 如果为空，返回一个基于真实语言的列表（使用语言名称作为值）
  return [
    { label: 'Java', value: 'Java' },
    { label: 'JavaScript', value: 'JavaScript' },
    { label: 'TypeScript', value: 'TypeScript' },
    { label: 'Vue', value: 'Vue' },
    { label: 'Python', value: 'Python' },
    { label: 'Go', value: 'Go' },
    { label: 'C++', value: 'C++' },
    { label: 'C', value: 'C' },
    { label: 'PHP', value: 'PHP' },
    { label: 'Swift', value: 'Swift' },
    { label: 'Kotlin', value: 'Kotlin' },
    { label: 'Rust', value: 'Rust' },
    { label: 'Scala', value: 'Scala' },
    { label: 'Ruby', value: 'Ruby' },
    { label: 'C#', value: 'C#' },
    { label: 'Dart', value: 'Dart' },
    { label: 'Shell', value: 'Shell' },
    { label: 'MATLAB', value: 'MATLAB' },
    { label: 'HTML', value: 'HTML' },
    { label: 'CSS', value: 'CSS' }
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
const userList = ref<UserVO[]>([]);
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
  userId: undefined,
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
    description: undefined,
    userId: undefined,
    createBy: undefined,
    orderByColumn: undefined,
    isAsc: undefined,
    params: {}
  },
  rules: {
    projectName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
    description: [{ required: true, message: '项目描述不能为空', trigger: 'blur' }],
    repositoryUrl: [{ required: true, message: '代码仓库不能为空', trigger: 'blur' }],
    userId: [{ required: true, message: '项目负责人不能为空', trigger: 'blur' }]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 根据用户ID获取用户昵称 */
const getUserNickName = (userId: number) => {
  const user = userList.value.find(u => u.userId === userId);
  return user ? user.nickName : '未设置';
};

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

/** 格式化数字显示 */
const formatNumber = (num: number | undefined | null) => {
  if (num === undefined || num === null) return '0';
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k';
  }
  return num.toString();
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

/** 获取用户列表 */
const getUserList = async () => {
  try {
    const res = await listUser({ pageNum: 1, pageSize: 1000, status: '0' });
    userList.value = res.rows || [];
  } catch (error) {
    console.error('获取用户列表失败:', error);
    userList.value = [];
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
  projectFormRef.value?.resetFields();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 排序变化处理 */
const handleSortChange = ({ prop, order }: { prop: string; order: string }) => {
  if (prop && order) {
    queryParams.value.orderByColumn = prop;
    queryParams.value.isAsc = order === 'ascending' ? 'asc' : 'desc';
  } else {
    queryParams.value.orderByColumn = undefined;
    queryParams.value.isAsc = undefined;
  }
  handleQuery();
};

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  queryParams.value.projectName = undefined;
  queryParams.value.description = undefined;
  queryParams.value.userId = undefined;
  queryParams.value.createBy = undefined;
  queryParams.value.orderByColumn = undefined;
  queryParams.value.isAsc = undefined;
  queryParams.value.params = {};
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

  dialog.visible = true;
  dialog.title = '修改项目列表';
};

/** 跳转到GitHub贡献统计 */
const goToContributors = (row: ProjectVO) => {
  if (row.repositoryUrl) {
    // 构建GitHub Contributors URL
    let contributorsUrl = row.repositoryUrl;
    if (contributorsUrl.includes('github.com')) {
      contributorsUrl = contributorsUrl.replace(/\.git$/, '') + '/graphs/contributors';
    } else if (contributorsUrl.includes('gitee.com')) {
      contributorsUrl = contributorsUrl.replace(/\.git$/, '') + '/contributors';
    }
    window.open(contributorsUrl, '_blank');
  } else {
    proxy?.$modal.msgWarning('该项目暂无代码仓库地址');
  }
};

/** 提交按钮 */
const submitForm = () => {
  projectFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;

      const submitData = { ...form.value };
      
      console.log('提交数据:', submitData);

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

  // 处理创建者参数
  if (route.query.createBy) {
    queryParams.value.createBy = route.query.createBy as string;
  }

  // 初始化过滤列表
  filteredTechStack.value = techStackDict.value;
  filteredProgrammingLanguage.value = programmingLanguageDict.value;

  // 获取用户列表
  getUserList();

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
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 表格样式 */
.el-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.el-table th {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  font-weight: 600;
  color: #333;
  border: none;
  font-size: 14px;
}

.el-table td {
  padding: 16px 12px;
  border: none;
  border-bottom: 1px solid #f5f5f5;
}

.el-table tbody tr:last-child td {
  border-bottom: none;
}

/* 序号列 */
.serial-number {
  font-weight: 600;
  color: #666;
}

/* 项目信息 */
.project-info-cell {
  text-align: left;
  padding: 8px 0;
}

.project-name {
  font-weight: 600;
  font-size: 15px;
  margin-bottom: 4px;
  line-height: 1.4;
}

.project-name-text {
  color: #333;
}

.project-description {
  font-size: 13px;
  color: #666;
  line-height: 1.4;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-word;
}

/* 项目负责人 */
.maintainer-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  color: #606266;
}

/* Star数 */
.star-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-weight: 500;
}

.star-icon {
  color: #f7ba2a;
  font-size: 16px;
}

/* Fork数 */
.fork-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-weight: 500;
}

.fork-icon {
  color: #67c23a;
  font-size: 16px;
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

.tech-count-tag {
  margin: 2px;
  border-radius: 12px;
  font-size: 11px;
  padding: 2px 6px;
  background-color: #f0f0f0;
  color: #666;
  border: 1px solid #d9d9d9;
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
  background: linear-gradient(135deg, #f8fbff 0%, #f0f9ff 100%) !important;
  transform: translateY(-1px);
  transition: all 0.3s ease;
}

.el-table tbody tr {
  transition: all 0.3s ease;
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
