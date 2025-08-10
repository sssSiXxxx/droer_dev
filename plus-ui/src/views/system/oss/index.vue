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
            style="width: 240px"
            @change="handleQuery"
          >
            <el-option
              v-for="item in projectOptions"
              :key="item.projectId"
              :label="item.projectName"
              :value="item.projectId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文档类型" prop="fileType">
          <el-select
            v-model="queryParams.fileType"
            placeholder="请选择文档类型"
            clearable
            style="width: 200px"
            @change="handleQuery"
          >
            <el-option
              v-for="dict in fileTypeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文件名" prop="fileName">
          <el-input
            v-model="queryParams.fileName"
            placeholder="请输入文件名"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
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
            <el-button
              type="primary"
              plain
              icon="Upload"
              @click="handleUpload"
              v-hasPermi="['system:oss:upload']"
            >
              上传文档
            </el-button>
            <el-button
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['system:oss:remove']"
            >
              删除
            </el-button>
          </div>
        </div>
      </template>

      <!-- 网格视图 -->
      <template v-if="viewMode === 'grid'">
        <el-row :gutter="20">
          <el-col
            :span="6"
            v-for="item in fileList"
            :key="item.ossId"
            class="mb-4"
          >
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
                  />
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
                  {{ item.fileName }}
                </div>
                <div class="file-meta">
                  <span>{{ formatSize(item.size) }}</span>
                  <span>{{ parseTime(item.createTime) }}</span>
                </div>
                <!-- 操作按钮 -->
                <div class="file-actions">
                  <el-button
                    link
                    type="primary"
                    icon="Download"
                    @click.stop="handleDownload(item)"
                    v-hasPermi="['system:oss:download']"
                  >
                    下载
                  </el-button>
                  <el-button
                    link
                    type="danger"
                    icon="Delete"
                    @click.stop="handleDelete(item)"
                    v-hasPermi="['system:oss:remove']"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </template>

      <!-- 表格视图 -->
      <template v-else>
        <el-table
          v-loading="loading"
          :data="fileList"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="文件名" align="left" prop="fileName" min-width="200">
            <template #default="{ row }">
              <div class="file-name-cell">
                <el-icon :size="20" class="mr-2">
                  <Document v-if="isDocument(row.fileName)" />
                  <VideoPlay v-else-if="isVideo(row.fileName)" />
                  <Headset v-else-if="isAudio(row.fileName)" />
                  <Files v-else />
                </el-icon>
                <span>{{ row.fileName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="所属项目" align="center" prop="projectName" min-width="120" />
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
          <el-table-column
            label="上传时间"
            align="center"
            prop="createTime"
            width="180"
          >
            <template #default="{ row }">
              <span>{{ parseTime(row.createTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            align="center"
            width="150"
            class-name="small-padding fixed-width"
          >
            <template #default="{ row }">
              <el-button
                link
                type="primary"
                icon="Download"
                @click="handleDownload(row)"
                v-hasPermi="['system:oss:download']"
              >
                下载
              </el-button>
              <el-button
                link
                type="danger"
                icon="Delete"
                @click="handleDelete(row)"
                v-hasPermi="['system:oss:remove']"
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
          >
            <el-option
              v-for="item in projectOptions"
              :key="item.projectId"
              :label="item.projectName"
              :value="item.projectId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文档类型" prop="fileType">
          <el-select
            v-model="upload.form.fileType"
            placeholder="请选择文档类型"
            style="width: 100%"
          >
            <el-option
              v-for="dict in fileTypeOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
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
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
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
import { listProjects } from '@/api/osc/project';
import { getCurrentInstance, ref, onMounted } from 'vue';
import {
  Document,
  Grid,
  List,
  Upload,
  UploadFilled,
  VideoPlay,
  Headset,
  Files
} from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance() as any;

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
  headers: { Authorization: 'Bearer ' + getToken() },
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
    projectId: [
      { required: true, message: '请选择所属项目', trigger: 'change' }
    ],
    fileType: [
      { required: true, message: '请选择文档类型', trigger: 'change' }
    ]
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
    const res = await listProjects();
    projectOptions.value = res.rows;
  } catch (error) {
    console.error('获取项目列表失败:', error);
  }
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  dateRange.value = [];
  proxy?.resetForm('queryRef');
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
};

/** 提交上传文件 */
const submitUpload = async () => {
  proxy?.$refs['uploadRef'].submit();
};

/** 文件上传中处理 */
const handleFileUploadProgress = (event: any, file: any) => {
  upload.value.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response: any, file: any) => {
  upload.value.isUploading = false;
  if (response.code === 200) {
    proxy?.$modal.msgSuccess('上传成功');
    upload.value.open = false;
    getList();
  } else {
    proxy?.$modal.msgError('上传失败');
  }
};

/** 文件上传失败处理 */
const handleFileError = () => {
  upload.value.isUploading = false;
  proxy?.$modal.msgError('上传失败');
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

/** 文件大小格式化 */
const formatSize = (size: number) => {
  if (size < 1024) {
    return size + ' B';
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB';
  } else if (size < 1024 * 1024 * 1024) {
    return (size / (1024 * 1024)).toFixed(2) + ' MB';
  } else {
    return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
  }
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

.file-card {
  cursor: pointer;
  transition: all 0.3s;

  &.selected {
    border-color: var(--el-color-primary);
    box-shadow: 0 0 0 1px var(--el-color-primary);
  }

  .file-preview {
    height: 160px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: var(--el-fill-color-lighter);

    .el-image {
      width: 100%;
      height: 100%;
    }

    .file-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--el-text-color-secondary);
    }
  }

  .file-info {
    padding: 12px;

    .file-name {
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .file-meta {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: var(--el-text-color-secondary);
      margin-bottom: 8px;
    }

    .file-actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
    }
  }
}

.file-name-cell {
  display: flex;
  align-items: center;
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
</style>