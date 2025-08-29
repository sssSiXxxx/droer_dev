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
            <el-option v-for="item in filteredProjectOptions" :key="item.projectId" :label="item.projectName" :value="item.projectId">
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
            <el-button type="primary" plain icon="Upload" @click="handleUpload" v-hasPermi="['system:oss:upload']"> 上传文档 </el-button>
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:oss:remove']">
              删除
            </el-button>
          </div>
        </div>
      </template>

      <!-- 表格视图 -->
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
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="600px" append-to-body>
      <el-form ref="uploadFormRef" :model="upload.form" :rules="upload.rules" label-width="100px">
        <el-form-item label="所属项目" prop="projectId">
          <el-select
            v-model="upload.form.projectId"
            placeholder="请选择项目或输入搜索"
            style="width: 100%"
            filterable
            clearable
            remote
            reserve-keyword
            :remote-method="handleProjectSearch"
            :loading="projectSearchLoading"
            @change="handleProjectChange"
            @visible-change="handleSelectVisibleChange"
            no-data-text="暂无匹配项目"
          >
            <el-option 
              v-for="item in filteredProjectOptions" 
              :key="item.projectId" 
              :label="getProjectLabel(item)" 
              :value="item.projectId"
              class="custom-project-option"
            >
              <div class="project-option-content">
                <div class="project-header">
                  <span class="project-name">{{ item.projectName }}</span>
                </div>
                <div class="project-desc" v-if="item.description" :title="item.description">
                  {{ truncateText(item.description, 50) }}
                </div>
                <div class="project-meta" v-if="item.status || item.createTime">
                  <span v-if="item.status === '1'" class="status-active">• 活跃</span>
                  <span v-else-if="item.status === '0'" class="status-inactive">• 暂停</span>
                  <span v-if="item.createTime" class="create-time">创建于 {{ formatDate(item.createTime) }}</span>
                </div>
              </div>
            </el-option>
            <template #empty>
              <div class="empty-option">
                <div v-if="projectSearchLoading" class="loading-state">
                  <el-icon class="is-loading"><Loading /></el-icon>
                  <span>搜索项目中...</span>
                </div>
                <div v-else-if="filteredProjectOptions.length === 0 && projectOptions.length === 0" class="no-data-state">
                  <el-empty description="暂无项目数据" :image-size="60">
                    <div class="empty-actions">
                      <el-button type="primary" size="small" @click="loadProjects" :loading="projectSearchLoading">
                        <el-icon><Refresh /></el-icon>
                        重新加载
                      </el-button>
                      <el-button type="success" size="small" @click="goToProjectManagement">
                        <el-icon><Plus /></el-icon>
                        创建项目
                      </el-button>
                    </div>
                  </el-empty>
                </div>
                <div v-else class="no-match-state">
                  <el-icon><Search /></el-icon>
                  <span>未找到匹配的项目</span>
                  <el-button type="text" size="small" @click="clearSearch">清除搜索</el-button>
                </div>
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
import { Document, Upload, UploadFilled, VideoPlay, Headset, Files, Picture, Loading, Refresh, Plus, Search } from '@element-plus/icons-vue';
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
  url: import.meta.env.VITE_APP_BASE_API + '/system/oss/upload',
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
    console.log('OSS列表响应:', res);
    console.log('OSS数据行:', res.rows);

    // 调试每个文件的项目信息
    if (res.rows && Array.isArray(res.rows)) {
      res.rows.forEach((item: any, index: number) => {
        console.log(`文件 ${index + 1}:`, {
          fileName: item.fileName,
          projectId: item.projectId,
          projectName: item.projectName,
          hasProjectName: !!item.projectName,
          projectNameType: typeof item.projectName
        });
      });
    }

    fileList.value = res.rows;
    total.value = res.total;
  } catch (error) {
    console.error('获取OSS列表失败:', error);
    // 提供模拟数据确保页面显示
    fileList.value = [
      {
        ossId: 1,
        fileName: 'random-generated-uuid.png',
        originalName: 'project-logo.png',
        projectId: 1,
        projectName: 'RuoYi-Vue-Plus项目',
        fileType: 'logo',
        size: 1024000,
        url: 'https://via.placeholder.com/300x200/409eff/ffffff?text=Logo', // 测试图片URL
        createTime: '2024-01-01 09:00:00'
      },
      {
        ossId: 2,
        fileName: 'random-generated-uuid.pdf',
        originalName: 'requirements.pdf',
        projectId: 2,
        projectName: 'Dromara社区管理系统',
        fileType: 'requirement',
        size: 2048000,
        url: '/static/docs/requirements.pdf',
        createTime: '2024-01-02 10:00:00'
      },
      {
        ossId: 3,
        fileName: 'random-generated-uuid.md',
        originalName: 'api-docs.md',
        projectId: 1,
        projectName: 'RuoYi-Vue-Plus项目',
        fileType: 'api',
        size: 512000,
        url: '/static/docs/api-docs.md',
        createTime: '2024-01-03 14:00:00'
      },
      {
        ossId: 4,
        fileName: 'random-generated-uuid.jpg',
        originalName: 'screenshot.jpg',
        projectId: 2,
        projectName: 'Dromara社区管理系统',
        fileType: 'design',
        size: 3024000,
        url: 'https://via.placeholder.com/400x300/67c23a/ffffff?text=Design', // 测试图片URL
        createTime: '2024-01-04 15:30:00'
      }
    ];
    total.value = fileList.value.length;
    proxy?.$modal.msgWarning('API接口连接失败，显示模拟数据');
  } finally {
    loading.value = false;
  }
};

/** 加载项目列表 - 修复版 */
const loadProjects = async (showLoading = true) => {
  try {
    if (showLoading) {
      projectSearchLoading.value = true;
    }
    console.log('开始加载项目列表...');
    
    // 移除status过滤，加载所有项目
    const res = await listProjectForOss({ 
      pageNum: 1, 
      pageSize: 1000
      // 不再过滤status，加载所有项目
    });
    
    console.log('项目列表响应:', res);

    // 检查响应结构
    let projectData = [];
    if (res && res.data && res.data.rows && Array.isArray(res.data.rows)) {
      projectData = res.data.rows;
      console.log('成功加载项目列表（res.data.rows），数量:', projectData.length);
    } else if (res && res.rows && Array.isArray(res.rows)) {
      projectData = res.rows;
      console.log('成功加载项目列表（res.rows），数量:', projectData.length);
    } else if (res && res.data && Array.isArray(res.data)) {
      projectData = res.data;
      console.log('成功加载项目列表（res.data），数量:', projectData.length);
    } else {
      console.warn('响应数据结构异常:', res);
      projectData = [];
    }

    // 按项目状态和创建时间排序（活跃项目优先，但不过滤非活跃项目）
    projectData.sort((a, b) => {
      // 活跃项目优先显示
      if (a.status === '1' && b.status !== '1') return -1;
      if (a.status !== '1' && b.status === '1') return 1;
      
      // 然后按创建时间倒序（最新的在前）
      const timeA = new Date(a.createTime || 0).getTime();
      const timeB = new Date(b.createTime || 0).getTime();
      return timeB - timeA;
    });

    projectOptions.value = projectData;
    filteredProjectOptions.value = [...projectData];
    
    console.log('最终projectOptions.value:', projectOptions.value);

    // 数据统计和提示
    const activeCount = projectData.filter(p => p.status === '1').length;
    const totalCount = projectData.length;
    
    if (totalCount === 0) {
      console.warn('项目列表为空，显示提示信息');
      if (showLoading) {
        proxy?.$modal.msgWarning('暂无项目数据，请先创建项目或联系管理员');
      }
    } else {
      console.log(`项目列表加载成功，共 ${totalCount} 个项目，其中 ${activeCount} 个活跃`);
      if (showLoading && totalCount > 0) {
        proxy?.$modal.msgSuccess(`成功加载 ${totalCount} 个项目（${activeCount} 个活跃）`);
      }
    }
  } catch (error: any) {
    console.error('获取项目列表失败:', error);
    
    // 更丰富的模拟数据，包含不同状态的项目
    projectOptions.value = [
      {
        projectId: 1,
        projectName: 'RuoYi-Vue-Plus项目',
        projectCode: 'RVP001',
        description: '基于RuoYi-Vue-Plus的企业级管理系统，提供完整的权限管理和业务功能',
        status: '1',
        createTime: '2024-01-15 10:00:00'
      },
      {
        projectId: 2,
        projectName: 'Dromara社区管理系统',
        projectCode: 'DCS001',
        description: 'Dromara开源社区管理平台，支持项目管理、成员管理等功能',
        status: '1',
        createTime: '2024-02-20 14:30:00'
      },
      {
        projectId: 3,
        projectName: '文档管理系统',
        projectCode: 'DMS001',
        description: '企业级文档管理系统，支持文档上传、分类、权限控制',
        status: '1',
        createTime: '2024-03-10 09:15:00'
      },
      {
        projectId: 4,
        projectName: '用户权限管理',
        projectCode: 'UPM001',
        description: '统一的用户权限管理平台，支持RBAC权限模型',
        status: '0', // 包含非活跃项目
        createTime: '2024-01-05 16:20:00'
      },
      {
        projectId: 5,
        projectName: '数据分析平台',
        projectCode: 'DAP001',
        description: '企业数据分析和可视化平台，支持多维度数据展示',
        status: '1',
        createTime: '2024-03-25 11:45:00'
      }
    ];
    filteredProjectOptions.value = [...projectOptions.value];

    // 根据错误类型显示不同的错误信息
    if (error.response?.status === 401) {
      proxy?.$modal.msgError('权限不足，请重新登录');
    } else if (error.response?.status === 404) {
      proxy?.$modal.msgError('接口不存在，请联系管理员');
    } else if (error.response?.status >= 500) {
      proxy?.$modal.msgError('服务器内部错误，请稍后重试');
    } else {
      if (showLoading) {
        proxy?.$modal.msgWarning('获取项目列表失败，使用模拟数据');
      }
    }
  } finally {
    if (showLoading) {
      projectSearchLoading.value = false;
    }
  }
};

/** 项目搜索处理（防抖优化） - 修复版 */
const handleProjectSearch = debounce(async (query: string) => {
  console.log('项目搜索查询:', query);
  
  // 如果查询为空，显示所有项目
  if (!query || query.trim() === '') {
    console.log('空查询，显示所有项目');
    filteredProjectOptions.value = [...projectOptions.value];
    return;
  }
  
  projectSearchLoading.value = true;
  try {
    // 如果项目列表为空，先加载所有项目
    if (projectOptions.value.length === 0) {
      console.log('项目列表为空，先加载所有项目');
      await loadProjects(false);
    }

    // 智能搜索：支持项目名称、项目编码、描述搜索（不区分大小写）
    const searchQuery = query.toLowerCase().trim();
    const filtered = projectOptions.value.filter((item: any) => {
      // 安全获取字段值，防止null或undefined
      const projectName = (item.projectName || '').toString().toLowerCase();
      const projectCode = (item.projectCode || '').toString().toLowerCase();
      const description = (item.description || '').toString().toLowerCase();
      
      // 支持多关键词搜索（用空格分隔）
      const searchKeywords = searchQuery.split(/\s+/).filter(keyword => keyword.length > 0);
      
      // 每个关键词都需要在某个字段中出现
      return searchKeywords.every(keyword => {
        return projectName.includes(keyword) || 
               projectCode.includes(keyword) || 
               description.includes(keyword);
      });
    });

    console.log(`原始项目数量: ${projectOptions.value.length}, 过滤后数量: ${filtered.length}`);

    // 按相关度排序（项目名称匹配 > 项目编码匹配 > 描述匹配）
    filtered.sort((a: any, b: any) => {
      const aName = (a.projectName || '').toString().toLowerCase();
      const bName = (b.projectName || '').toString().toLowerCase();
      const aCode = (a.projectCode || '').toString().toLowerCase();
      const bCode = (b.projectCode || '').toString().toLowerCase();
      
      // 项目名称完全匹配优先级最高
      const aNameExact = aName === searchQuery;
      const bNameExact = bName === searchQuery;
      if (aNameExact && !bNameExact) return -1;
      if (!aNameExact && bNameExact) return 1;
      
      // 项目编码完全匹配次之
      const aCodeExact = aCode === searchQuery;
      const bCodeExact = bCode === searchQuery;
      if (aCodeExact && !bCodeExact) return -1;
      if (!aCodeExact && bCodeExact) return 1;
      
      // 项目名称开头匹配
      const aNameStarts = aName.startsWith(searchQuery);
      const bNameStarts = bName.startsWith(searchQuery);
      if (aNameStarts && !bNameStarts) return -1;
      if (!aNameStarts && bNameStarts) return 1;
      
      // 最后按项目状态和创建时间排序
      if (a.status === '1' && b.status !== '1') return -1;
      if (a.status !== '1' && b.status === '1') return 1;
      
      const timeA = new Date(a.createTime || 0).getTime();
      const timeB = new Date(b.createTime || 0).getTime();
      return timeB - timeA;
    });

    filteredProjectOptions.value = filtered;
    console.log(`项目搜索完成："${query}" 找到 ${filtered.length} 个匹配项目`);
    
    // 输出搜索结果详情以便调试
    if (filtered.length > 0) {
      console.log('搜索结果:', filtered.map(p => ({ 
        name: p.projectName, 
        code: p.projectCode, 
        status: p.status 
      })));
    }
    
  } catch (error) {
    console.error('项目搜索失败:', error);
    proxy?.$modal.msgError('搜索项目失败，请稍后重试');
    // 错误时保持当前的过滤结果，不清空
  } finally {
    projectSearchLoading.value = false;
  }
}, 300);

/** 项目过滤变化处理 */
const handleProjectFilterChange = (query: string) => {
  if (query !== '') {
    projectSearchLoading.value = true;
    try {
      // 本地过滤项目
      const filtered = projectOptions.value.filter(
        (item: any) =>
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
  const selectedProject = projectOptions.value.find(p => p.projectId === projectId);
  if (selectedProject) {
    console.log('选择的项目:', selectedProject.projectName);
    // 可以在这里添加项目选择后的逻辑，比如自动设置文档类型等
  }
};

/** 下拉框显示状态变化处理 */
const handleSelectVisibleChange = async (visible: boolean) => {
  if (visible && projectOptions.value.length === 0) {
    console.log('下拉框打开时自动加载项目列表');
    await loadProjects();
  }
};

/** 获取项目显示标签 */
const getProjectLabel = (item: any) => {
  return item.projectName || item.name || `项目-${item.projectId}`;
};

/** 截断文本 */
const truncateText = (text: string, maxLength: number) => {
  if (!text) return '';
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
};

/** 格式化日期 */
const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.getFullYear() + '-' + String(date.getMonth() + 1).padStart(2, '0') + '-' + String(date.getDate()).padStart(2, '0');
};

/** 清除搜索 */
const clearSearch = () => {
  filteredProjectOptions.value = [...projectOptions.value];
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
  try {
    let confirmMessage = '';
    
    if (row) {
      // 单个文件删除
      const fileName = getDisplayFileName(row);
      confirmMessage = `是否确认删除文件"${fileName}"？`;
    } else {
      // 批量删除
      if (ids.value.length === 0) {
        proxy?.$modal.msgWarning('请选择要删除的文件');
        return;
      }
      
      // 获取选中文件的名称列表
      const selectedFiles = fileList.value
        .filter((item: any) => ids.value.includes(item.ossId))
        .map((item: any) => getDisplayFileName(item))
        .slice(0, 3); // 最多显示3个文件名
      
      if (ids.value.length === 1) {
        confirmMessage = `是否确认删除文件"${selectedFiles[0]}"？`;
      } else if (ids.value.length <= 3) {
        confirmMessage = `是否确认删除以下文件：\n${selectedFiles.join('\n')}`;
      } else {
        confirmMessage = `是否确认删除以下文件：\n${selectedFiles.join('\n')}\n...等共${ids.value.length}个文件？`;
      }
    }
    
    await proxy?.$modal.confirm(confirmMessage);
    
    const ossIds = row?.ossId || ids.value;
    await delOss(ossIds);
    await getList();
    proxy?.$modal.msgSuccess('删除成功');
  } catch (error) {
    console.error('删除失败:', error);
  }
};

/** 上传按钮操作 - 优化版 */
const handleUpload = async () => {
  // 重置表单
  upload.value.open = true;
  upload.value.fileList = [];
  upload.value.form = {
    projectId: undefined,
    fileType: undefined
  };
  
  // 如果项目列表为空，预先加载
  if (projectOptions.value.length === 0) {
    console.log('预先加载项目列表以便用户选择');
    // 使用不显示加载状态的方式预加载
    loadProjects(false).catch(error => {
      console.warn('预加载项目列表失败:', error);
    });
  }
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
const handleFileSuccess = async (response: any, file: any) => {
  upload.value.isUploading = false;
  console.log('文件上传响应:', response);
  console.log('文件信息:', file);

  if (response.code === 200) {
    proxy?.$modal.msgSuccess('上传成功');
    upload.value.open = false;
    // 重置上传表单
    upload.value.fileList = [];
    upload.value.form = {
      projectId: undefined,
      fileType: undefined
    };
    // 刷新文件列表
    await getList();
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
  console.log('下载文件:', row);
  const fileName = row.originalName || row.fileName || '下载文件';
  proxy?.download('/system/oss/download/' + row.ossId, {}, fileName);
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: any) => {
  ids.value = selection.map((item: any) => item.ossId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};


/** 文件类型判断 */
const isImage = (fileName: string) => {
  if (!fileName) return false;
  return /\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(fileName);
};

const isDocument = (fileName: string) => {
  if (!fileName) return false;
  return /\.(doc|docx|xls|xlsx|ppt|pptx|pdf|txt|md)$/i.test(fileName);
};

const isVideo = (fileName: string) => {
  if (!fileName) return false;
  return /\.(mp4|webm|ogg|mov|avi)$/i.test(fileName);
};

const isAudio = (fileName: string) => {
  if (!fileName) return false;
  return /\.(mp3|wav|ogg|m4a)$/i.test(fileName);
};

/** 获取文件URL */
const getFileUrl = (item: any) => {
  // 优先使用item.url，如果不存在则构建默认URL
  if (item.url) {
    // 如果是相对路径，添加基础URL
    if (item.url.startsWith('/')) {
      return import.meta.env.VITE_APP_BASE_API + item.url;
    }
    return item.url;
  }
  
  // 如果没有URL，返回占位符
  return '/static/images/file-placeholder.png';
};

/** 获取显示的文件名 */
const getDisplayFileName = (item: any) => {
  // 优先显示原始文件名，如果没有则显示系统生成的文件名
  return item.originalName || item.fileName || '未知文件';
};

/** 获取文件类型的中文标签 */
const getFileTypeLabel = (fileType: string) => {
  switch (fileType) {
    case 'logo':
      return 'Logo';
    case 'requirement':
      return '需求文档';
    case 'help':
      return '帮助文档';
    case 'design':
      return '设计文档';
    case 'api':
      return '接口文档';
    case 'other':
      return '其他文档';
    default:
      return '文档';
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
  height: 320px;  
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

    .image-error,
    .image-loading {
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
      
      .error-detail {
        font-size: 10px;
        color: var(--el-text-color-placeholder);
        margin-top: 8px;
        text-align: center;
        word-break: break-all;
        max-width: 120px;
      }
    }
  }

  .file-info {
    padding: 16px;
    flex: 1;
    display: flex;
    flex-direction: column;

    .file-name {
      font-size: 13px;
      font-weight: 600;
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      color: var(--el-text-color-primary);
      line-height: 1.4;
      height: 36px;
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

/* ==== 项目下拉框选项样式优化 ==== */
.custom-project-option {
  padding: 0 !important;
}

.project-option-content {
  padding: 12px 16px;
  transition: all 0.2s ease;
  border-radius: 6px;
  margin: 2px 4px;
}

.custom-project-option:hover .project-option-content {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.1);
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.project-name {
  font-weight: 600;
  color: var(--el-text-color-primary);
  font-size: 14px;
  line-height: 1.4;
}


.project-desc {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.4;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  color: var(--el-text-color-placeholder);
  margin-top: 6px;
}

.status-active {
  color: #16a34a;
  font-weight: 500;
}

.status-inactive {
  color: #dc2626;
  font-weight: 500;
}

.create-time {
  font-size: 10px;
  color: var(--el-text-color-placeholder);
}

/* 空状态样式优化 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: var(--el-color-primary);
  font-size: 13px;
  padding: 20px;
}

.loading-state .el-icon {
  font-size: 20px;
  animation: spin 1s linear infinite;
}

.no-data-state {
  padding: 10px;
}

.no-match-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  padding: 20px;
}

.no-match-state .el-icon {
  font-size: 24px;
  color: var(--el-text-color-placeholder);
}

.empty-actions .el-button {
  font-size: 12px;
  padding: 6px 12px;
}

/* 项目选项悬浮效果 */
:deep(.el-select-dropdown__item) {
  padding: 0 !important;
  margin: 2px 0;
  border-radius: 6px;
}

:deep(.el-select-dropdown__item.hover) {
  background-color: transparent !important;
}

/* 下拉框本身的样式优化 */
:deep(.el-select) {
  .el-input__inner {
    border-radius: 6px;
    transition: all 0.2s ease;
  }
  
  .el-input__inner:focus {
    border-color: var(--el-color-primary);
    box-shadow: 0 0 0 2px rgba(var(--el-color-primary-rgb), 0.2);
  }
}

/* 下拉面板样式 */
:deep(.el-select-dropdown) {
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--el-border-color-light);
  margin-top: 4px;
  max-height: 400px;
  overflow-y: auto;
}

/* 搜索时的加载状态 */
:deep(.el-select .el-input.is-focus .el-input__suffix) {
  .el-icon {
    animation: none;
  }
  
  .is-loading {
    animation: spin 1s linear infinite;
  }
}
</style>
