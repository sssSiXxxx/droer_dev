<template>
  <div class="app-container">
    <!-- 搜索工具栏 -->
    <el-card class="mb-4">
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="项目" prop="projectId">
          <el-select 
            v-model="queryParams.projectId" 
            placeholder="请选择项目" 
            clearable 
            filterable
            remote
            :remote-method="handleProjectSearch"
            style="width: 240px" 
            @change="handleQuery"
          >
            <el-option 
              v-for="item in filteredProjectOptions" 
              :key="item.projectId" 
              :label="item.projectName" 
              :value="item.projectId"
            >
              <div class="project-option">
                <div class="project-name">{{ item.projectName }}</div>
                <div class="project-desc" v-if="item.description" :title="item.description">
                  {{ item.description.length > 30 ? item.description.substring(0, 30) + '...' : item.description }}
                </div>
              </div>
            </el-option>
            <template #empty>
              <div class="empty-option">
                <el-empty description="暂无项目数据" :image-size="60">
                  <div class="empty-actions">
                    <el-button type="primary" size="small" @click="loadProjects">重新加载</el-button>
                    <el-button type="success" size="small" @click="goToProjectManagement">去创建项目</el-button>
                  </div>
                </el-empty>
              </div>
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="文档类型" prop="fileType">
          <el-select v-model="queryParams.fileType" placeholder="请选择文档类型" clearable style="width: 200px" @change="handleQuery">
            <el-option v-for="dict in fileTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件名" prop="fileName">
          <el-input v-model="queryParams.fileName" placeholder="请输入文件名" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="上传时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 文件列表卡片 -->
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="card-title">文档列表</span>
          <div class="right-content">
            <el-radio-group v-model="viewMode" size="small" class="mr-4">
              <el-radio-button label="grid">
                <el-icon><Grid /></el-icon>
              </el-radio-button>
              <el-radio-button label="table">
                <el-icon><List /></el-icon>
              </el-radio-button>
            </el-radio-group>
            <el-button type="primary" plain icon="Upload" @click="handleUpload" v-hasPermi="['system:oss:upload']"> 上传文档 </el-button>
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:oss:remove']">
              删除
            </el-button>
          </div>
        </div>
      </template>

      <!-- 网格视图 -->
      <template v-if="viewMode === 'grid'">
        <el-row :gutter="20">
          <el-col :span="6" v-for="item in fileList" :key="item.ossId" class="mb-4">
            <el-card
              :body-style="{ padding: '0px' }"
              shadow="hover"
              class="file-card"
              :class="{ selected: selection.includes(item.ossId) }"
              @click="toggleSelection(item)"
            >
              <!-- 文件预览 -->
              <div class="file-preview">
                <template v-if="isImage(item.fileName)">
                  <el-image 
                    :src="item.url" 
                    fit="cover" 
                    :preview-src-list="[item.url]"
                    :initial-index="0"
                    @error="handleImageError"
                    @load="handleImageLoad"
                  >
                    <template #error>
                      <div class="image-error">
                        <el-icon :size="40"><Picture /></el-icon>
                        <span>图片加载失败</span>
                      </div>
                    </template>
                    <template #placeholder>
                      <div class="image-loading">
                        <el-icon :size="40" class="is-loading"><Loading /></el-icon>
                        <span>加载中...</span>
                      </div>
                    </template>
                  </el-image>
                </template>
                <template v-else>
                  <div class="file-icon">
                    <el-icon :size="40">
                      <Document v-if="isDocument(item.fileName)" />
                      <VideoPlay v-else-if="isVideo(item.fileName)" />
                      <Headset v-else-if="isAudio(item.fileName)" />
                      <Files v-else />
                    </el-icon>
                  </div>
                </template>
              </div>
              <!-- 文件信息 -->
              <div class="file-info">
                <div class="file-name" :title="item.fileName">
                  {{ getDisplayFileName(item) }}
                </div>
                <div class="file-project" v-if="item.projectName">
                  <el-tag size="small" type="info">{{ item.projectName }}</el-tag>
                </div>
                <div class="file-meta">
                  <span>{{ formatSize(item.size) }}</span>
                  <span>{{ parseTime(item.createTime) }}</span>
                </div>
                <!-- 操作按钮 -->
                <div class="file-actions">
                  <el-button link type="primary" icon="Download" @click.stop="handleDownload(item)" v-hasPermi="['system:oss:download']">
                    下载
                  </el-button>
                  <el-button link type="danger" icon="Delete" @click.stop="handleDelete(item)" v-hasPermi="['system:oss:remove']"> 删除 </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </template>

      <!-- 表格视图 -->
      <template v-else>
        <el-table v-loading="loading" :data="fileList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="文件名" align="center" prop="fileName" min-width="200">
            <template #default="{ row }">
              <div class="file-name-cell">
                <el-icon :size="20" class="mr-2">
                  <Document v-if="isDocument(row.fileName)" />
                  <VideoPlay v-else-if="isVideo(row.fileName)" />
                  <Headset v-else-if="isAudio(row.fileName)" />
                  <Files v-else />
                </el-icon>
                <span :title="row.fileName">{{ getDisplayFileName(row) }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所属项目" align="center" prop="projectName" min-width="120">
            <template #default="{ row }">
              <span v-if="row.projectName">{{ row.projectName }}</span>
              <span v-else class="text-muted">未分配项目</span>
            </template>
          </el-table-column>
          <el-table-column label="文档类型" align="center" prop="fileType" min-width="100">
            <template #default="{ row }">
              <dict-tag :options="fileTypeOptions" :value="row.fileType" />
            </template>
          </el-table-column>
          <el-table-column label="文件大小" align="center" prop="size" min-width="100">
            <template #default="{ row }">
              {{ formatSize(row.size) }}
            </template>
          </el-table-column>
          <el-table-column label="上传时间" align="center" prop="createTime" width="180">
            <template #default="{ row }">
              <span>{{ parseTime(row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
            <template #default="{ row }">
              <el-button link type="primary" icon="Download" @click="handleDownload(row)" v-hasPermi="['system:oss:download']"> 下载 </el-button>
              <el-button link type="danger" icon="Delete" @click="handleDelete(row)" v-hasPermi="['system:oss:remove']"> 删除 </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </template>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="600px" append-to-body>
      <el-form ref="uploadFormRef" :model="upload.form" :rules="upload.rules" label-width="100px">
        <el-form-item label="所属项目" prop="projectId">
          <el-select 
            v-model="upload.form.projectId" 
            placeholder="请选择项目" 
            style="width: 100%"
            filterable
            clearable
            remote
            :remote-method="handleProjectSearch"
            :loading="projectSearchLoading"
            @change="handleProjectChange"
          >
            <el-option 
              v-for="item in filteredProjectOptions" 
              :key="item.projectId" 
              :label="item.projectName" 
              :value="item.projectId"
            >
              <div class="project-option">
                <div class="project-name">{{ item.projectName }}</div>
                <div class="project-desc" v-if="item.description">{{ item.description }}</div>
              </div>
            </el-option>
            <template #empty>
              <div class="empty-option">
                <el-empty description="暂无项目数据" :image-size="60">
                  <div class="empty-actions">
                    <el-button type="primary" size="small" @click="loadProjects">重新加载</el-button>
                    <el-button type="success" size="small" @click="goToProjectManagement">去创建项目</el-button>
                  </div>
                </el-empty>
              </div>
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="文档类型" prop="fileType">
          <el-select v-model="upload.form.fileType" placeholder="请选择文档类型" style="width: 100%">
            <el-option v-for="dict in fileTypeOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件上传" prop="file">
          <el-upload
            ref="uploadRef"
            :action="upload.url"
            :headers="upload.headers"
            :file-list="upload.fileList"
            :data="upload.form"
            :limit="upload.limit"
            :on-progress="handleFileUploadProgress"
            :on-success="handleFileSuccess"
            :on-error="handleFileError"
            :on-exceed="handleFileExceed"
            :before-upload="handleBeforeUpload"
            :on-remove="handleFileRemove"
            multiple
            :show-file-list="true"
            drag
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip text-center">
                <span>支持任意类型文件，且不超过 {{ upload.fileSize }}MB</span>
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitUpload">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Oss" lang="ts">
import { listOss, delOss } from '@/api/system/oss';
import { getToken } from '@/utils/auth';
import { listProjectForOss } from '@/api/osc/project';
import { parseTime } from '@/utils/ruoyi';
import { getCurrentInstance, ref, onMounted, ComponentInternalInstance } from 'vue';
import { Document, Grid, List, Upload, UploadFilled, VideoPlay, Headset, Files, Picture, Loading } from '@element-plus/icons-vue';
import router from '@/router';

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

// 遮罩层
const loading = ref(false);
// 选中数组
const ids = ref<Array<string | number>>([]);
// 非单个禁用
const single = ref(true);
// 非多个禁用
const multiple = ref(true);
// 总条数
const total = ref(0);
// 查看模式：grid/table
const viewMode = ref('grid');
// 选中的文件ID
const selection = ref<Array<string | number>>([]);

// 项目选项
const projectOptions = ref([]);
// 过滤后的项目选项
const filteredProjectOptions = ref([]);
// 项目搜索加载状态
const projectSearchLoading = ref(false);

// 文档类型选项
const fileTypeOptions = [
  { label: 'Logo图片', value: 'logo' },
  { label: '需求文档', value: 'requirement' },
  { label: '帮助文档', value: 'help' },
  { label: '设计文档', value: 'design' },
  { label: '接口文档', value: 'api' },
  { label: '其他文档', value: 'other' }
];

// 弹出层标题
const title = ref('');
// 是否显示弹出层
const open = ref(false);
// 日期范围
const dateRange = ref<Array<string>>([]);
// 文件列表
const fileList = ref([]);

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  fileName: undefined,
  originalName: undefined,
  projectId: undefined,
  fileType: undefined
});

// 上传参数
const upload = ref({
  // 是否显示弹出层
  open: false,
  // 弹出层标题
  title: '上传文件',
  // 是否禁用上传
  isUploading: false,
  // 设置上传的请求头部
  get headers() {
    return { 
      Authorization: 'Bearer ' + getToken(),
      clientid: import.meta.env.VITE_APP_CLIENT_ID
    };
  },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + '/resource/oss/upload',
  // 上传的文件列表
  fileList: [],
  // 上传文件大小限制
  fileSize: 5,
  // 限制上传数量
  limit: 10,
  // 表单参数
  form: {
    projectId: undefined,
    fileType: undefined
  },
  // 表单校验
  rules: {
    projectId: [{ required: true, message: '请选择所属项目', trigger: 'change' }],
    fileType: [{ required: true, message: '请选择文档类型', trigger: 'change' }]
  }
});

/** 查询OSS列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await listOss(proxy?.addDateRange(queryParams.value, dateRange.value));
    fileList.value = res.rows;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

/** 加载项目列表 */
const loadProjects = async () => {
  try {
    projectSearchLoading.value = true;
    console.log('开始加载项目列表...');
    const res = await listProjectForOss({ pageNum: 1, pageSize: 1000 });
    console.log('项目列表响应:', res);
    
    // 检查响应结构
    console.log('完整响应对象:', res);
    console.log('res.data:', res?.data);
    console.log('res.data.rows:', res?.data?.rows);
    console.log('res.data.rows类型:', typeof res?.data?.rows);
    console.log('res.data.rows是否为数组:', Array.isArray(res?.data?.rows));
    
    // 检查响应结构 - 尝试多种可能的数据结构
    if (res && res.data && res.data.rows && Array.isArray(res.data.rows)) {
      // 标准响应格式：{data: {code: 200, msg: "success", rows: [...], total: 10}}
      projectOptions.value = res.data.rows;
      console.log('成功加载项目列表（res.data.rows），数量:', res.data.rows.length);
      console.log('项目列表内容:', res.data.rows);
    } else if (res && res.rows && Array.isArray(res.rows)) {
      // 直接响应格式：{rows: [...], total: 10}
      projectOptions.value = res.rows;
      console.log('成功加载项目列表（res.rows），数量:', res.rows.length);
      console.log('项目列表内容:', res.rows);
    } else if (res && res.data && Array.isArray(res.data)) {
      // 直接数组格式：{data: [...]}
      projectOptions.value = res.data;
      console.log('成功加载项目列表（res.data），数量:', res.data.length);
      console.log('项目列表内容:', res.data);
    } else {
      console.warn('响应数据结构异常:', res);
      console.warn('res.data:', res?.data);
      console.warn('res.rows:', res?.rows);
      projectOptions.value = [];
    }
    
    filteredProjectOptions.value = [...projectOptions.value]; // 初始化过滤后的选项
    console.log('最终projectOptions.value:', projectOptions.value);
    console.log('最终filteredProjectOptions.value:', filteredProjectOptions.value);
    
    // 如果没有数据，显示提示
    if (projectOptions.value.length === 0) {
      console.warn('项目列表为空，显示提示信息');
      proxy?.$modal.msgWarning('暂无项目数据，请先创建项目或联系管理员');
    } else {
      console.log('项目列表加载成功，共', projectOptions.value.length, '个项目');
    }
  } catch (error: any) {
    console.error('获取项目列表失败:', error);
    console.error('错误详情:', {
      message: error.message,
      response: error.response,
      status: error.response?.status,
      data: error.response?.data
    });
    projectOptions.value = [];
    filteredProjectOptions.value = [];
    
    // 根据错误类型显示不同的错误信息
    if (error.response?.status === 401) {
      proxy?.$modal.msgError('权限不足，请重新登录');
    } else if (error.response?.status === 404) {
      proxy?.$modal.msgError('接口不存在，请联系管理员');
    } else if (error.response?.status >= 500) {
      proxy?.$modal.msgError('服务器内部错误，请稍后重试');
    } else {
      proxy?.$modal.msgError('获取项目列表失败，请稍后重试');
    }
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
      filteredProjectOptions.value = projectOptions.value.filter(item => 
        item.projectName.toLowerCase().includes(query.toLowerCase()) ||
        (item.description && item.description.toLowerCase().includes(query.toLowerCase()))
      );
    } finally {
      projectSearchLoading.value = false;
    }
  } else {
    // 如果搜索词为空，显示所有项目
    filteredProjectOptions.value = [...projectOptions.value];
  }
}, 300);

/** 项目过滤变化处理 */
const handleProjectFilterChange = (query: string) => {
  if (query !== '') {
    projectSearchLoading.value = true;
    try {
      // 本地过滤项目
      const filtered = projectOptions.value.filter((item: any) => 
        item.projectName?.toLowerCase().includes(query.toLowerCase()) ||
        item.description?.toLowerCase().includes(query.toLowerCase()) ||
        item.projectCode?.toLowerCase().includes(query.toLowerCase())
      );
      filteredProjectOptions.value = filtered;
    } catch (error) {
      console.error('项目搜索失败:', error);
    } finally {
      projectSearchLoading.value = false;
    }
  } else {
    filteredProjectOptions.value = [...projectOptions.value];
  }
};

/** 项目选择变化处理 */
const handleProjectChange = (projectId: number) => {
  console.log('选择的项目ID:', projectId);
  // 可以在这里添加其他逻辑
};

/** 跳转到项目管理页面 */
const goToProjectManagement = () => {
  router.push('/osc/project');
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  dateRange.value = [];
  (proxy?.$refs['queryRef'] as any)?.resetFields();
  handleQuery();
};

/** 删除按钮操作 */
const handleDelete = async (row?: any) => {
  const ossIds = row?.ossId || ids.value;
  try {
    await proxy?.$modal.confirm('是否确认删除OSS对象存储编号为"' + ossIds + '"的数据项？');
    await delOss(ossIds);
    await getList();
    proxy?.$modal.msgSuccess('删除成功');
  } catch (error) {
    console.error('删除失败:', error);
  }
};

/** 上传按钮操作 */
const handleUpload = () => {
  upload.value.open = true;
  upload.value.fileList = [];
  // 重新加载项目列表，确保数据是最新的
  loadProjects();
};

/** 提交上传文件 */
const submitUpload = async () => {
  (proxy?.$refs['uploadRef'] as any)?.submit();
};

/** 文件上传中处理 */
const handleFileUploadProgress = (event: any, file: any) => {
  upload.value.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response: any, file: any) => {
  upload.value.isUploading = false;
  console.log('文件上传响应:', response);
  console.log('文件信息:', file);
  
  if (response.code === 200) {
    proxy?.$modal.msgSuccess('上传成功');
    upload.value.open = false;
    getList();
  } else {
    console.error('上传失败响应:', response);
    proxy?.$modal.msgError(`上传失败: ${response.msg || '未知错误'}`);
  }
};

/** 文件上传失败处理 */
const handleFileError = (error: any, file: any) => {
  upload.value.isUploading = false;
  console.error('文件上传失败:', error);
  console.error('文件信息:', file);
  
  // 检查是否是Token过期错误
  if (error?.response?.status === 401 || error?.message?.includes('认证失败')) {
    proxy?.$modal.msgError('登录已过期，请重新登录');
    // 跳转到登录页面
    router.push('/login');
  } else {
    proxy?.$modal.msgError(`上传失败: ${error?.message || error || '未知错误'}`);
  }
};

/** 文件删除处理 */
const handleFileRemove = (file: any, fileList: any) => {
  upload.value.fileList = fileList;
};

/** 文件上传超出数量限制处理 */
const handleFileExceed = (files: any, fileList: any) => {
  proxy?.$modal.msgError(`上传文件数量不能超过 ${upload.value.limit} 个!`);
};

/** 上传前处理 */
const handleBeforeUpload = (file: any) => {
  const isLt = file.size / 1024 / 1024 < upload.value.fileSize;
  if (!isLt) {
    proxy?.$modal.msgError(`文件大小不能超过 ${upload.value.fileSize} MB!`);
    return false;
  }
  upload.value.fileList = [file];
  return true;
};

/** 下载按钮操作 */
const handleDownload = (row: any) => {
  proxy?.download('/system/oss/download/' + row.ossId, {}, row.fileName);
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: any) => {
  ids.value = selection.map((item: any) => item.ossId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

/** 网格视图选中切换 */
const toggleSelection = (item: any) => {
  const index = selection.value.indexOf(item.ossId);
  if (index === -1) {
    selection.value.push(item.ossId);
  } else {
    selection.value.splice(index, 1);
  }
};

/** 文件类型判断 */
const isImage = (fileName: string) => {
  return /\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(fileName);
};

const isDocument = (fileName: string) => {
  return /\.(doc|docx|xls|xlsx|ppt|pptx|pdf|txt|md)$/i.test(fileName);
};

const isVideo = (fileName: string) => {
  return /\.(mp4|webm|ogg|mov|avi)$/i.test(fileName);
};

const isAudio = (fileName: string) => {
  return /\.(mp3|wav|ogg|m4a)$/i.test(fileName);
};

/** 获取显示的文件名 */
const getDisplayFileName = (item: any) => {
  // 生成格式：项目名_文件类型
  const projectName = item.projectName || '未分配项目';
  const fileTypeLabel = getFileTypeLabel(item.fileType);
  
  return `${projectName}_${fileTypeLabel}`;
};

/** 获取文件类型的中文标签 */
const getFileTypeLabel = (fileType: string) => {
  switch (fileType) {
    case 'logo': return 'Logo';
    case 'requirement': return '需求文档';
    case 'help': return '帮助文档';
    case 'design': return '设计文档';
    case 'api': return '接口文档';
    case 'other': return '其他文档';
    default: return '文档';
  }
};

/** 格式化文件大小 */
const formatSize = (size: number | null | undefined) => {
  if (!size || size === 0) return '0 B';
  
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let index = 0;
  let fileSize = size;
  
  while (fileSize >= 1024 && index < units.length - 1) {
    fileSize /= 1024;
    index++;
  }
  
  return `${fileSize.toFixed(1)} ${units[index]}`;
};

/** 图片加载失败处理 */
const handleImageError = (e: any) => {
  console.error('图片加载失败:', e);
};

/** 图片加载成功处理 */
const handleImageLoad = (e: any) => {
  console.log('图片加载成功:', e);
};

onMounted(() => {
  getList();
  loadProjects();
});
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}

.mr-4 {
  margin-right: 16px;
}

.mr-2 {
  margin-right: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 500;
}

.right-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.file-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
  height: 280px;
  display: flex;
  flex-direction: column;
  animation: fadeIn 0.5s ease-out;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  }

  &.selected {
    border: 2px solid var(--el-color-primary);
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }

  .file-preview {
    height: 160px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    position: relative;
    overflow: hidden;

    .el-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .file-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--el-text-color-secondary);
      background: rgba(255, 255, 255, 0.8);
      border-radius: 50%;
      width: 80px;
      height: 80px;
      backdrop-filter: blur(10px);
    }

    .image-error, .image-loading {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      color: var(--el-text-color-secondary);
      background: rgba(255, 255, 255, 0.8);
      border-radius: 8px;
      padding: 20px;
      width: 100%;
      height: 100%;
      backdrop-filter: blur(10px);

      .is-loading {
        animation: spin 1s linear infinite;
      }
    }
  }

  .file-info {
    padding: 16px;
    flex: 1;
    display: flex;
    flex-direction: column;

    .file-name {
      font-size: 14px;
      font-weight: 600;
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: var(--el-text-color-primary);
      line-height: 1.4;
    }

    .file-project {
      margin-bottom: 8px;
    }

    .file-meta {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: var(--el-text-color-secondary);
      margin-bottom: 12px;
      flex-wrap: wrap;
      gap: 4px;
    }

    .file-actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
      margin-top: auto;
    }
  }
}

.file-name-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  
  .el-icon {
    margin-right: 8px;
  }
  
  span {
    max-width: 150px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

:deep(.el-upload-dragger) {
  width: 100%;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  .el-icon--upload {
    font-size: 48px;
    color: var(--el-color-primary);
    margin-bottom: 16px;
  }

  .el-upload__text {
    font-size: 16px;
    color: var(--el-text-color-regular);

    em {
      color: var(--el-color-primary);
      font-style: normal;
    }
  }
}

:deep(.el-upload__tip) {
  margin-top: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.project-option {
  .project-name {
    font-weight: 500;
    color: var(--el-text-color-primary);
    font-size: 14px;
    margin-bottom: 2px;
  }
  
  .project-desc {
    font-size: 11px;
    color: var(--el-text-color-secondary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    line-height: 1.2;
  }
}

.empty-option {
  padding: 20px;
  text-align: center;
}

.empty-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
  margin-top: 12px;
}

.text-muted {
  color: var(--el-text-color-secondary);
  font-style: italic;
}
</style>
