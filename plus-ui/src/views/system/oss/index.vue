<template>
  <div class="app-container">
    <!-- 搜索工具栏 -->
    <el-card class="mb-4">
      <el-form :model="queryParams" ref="queryRef" :inline="true">
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
            <div class="file-name-cell" style="display: flex; align-items: center; justify-content: center;">
              <el-icon :size="20" class="mr-2" style="margin-right: 8px;">
                <Document v-if="isDocument(row.fileName)" />
                <VideoPlay v-else-if="isVideo(row.fileName)" />
                <Headset v-else-if="isAudio(row.fileName)" />
                <Files v-else />
              </el-icon>
              <span :title="row.fileName">{{ getDisplayFileName(row) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="文件大小" align="center" prop="size" min-width="100">
          <template #default="{ row }">
            <span v-if="row.size">{{ formatFileSize(row.size) }}</span>
            <span v-else class="text-muted">未知</span>
          </template>
        </el-table-column>
        <el-table-column label="上传者" align="center" prop="createByName" width="120" />
        <el-table-column label="上传时间" align="center" prop="createTime" width="180">
          <template #default="{ row }">
            <span>{{ parseTime(row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
          <template #default="{ row }">
            <el-tooltip content="下载" placement="top">
              <el-button link type="primary" icon="Download" @click="handleDownload(row)" v-hasPermi="['system:oss:download']" />
            </el-tooltip>
            <el-tooltip content="复制链接" placement="top">
              <el-button link type="success" icon="CopyDocument" @click="handleCopy(row)" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="danger" icon="Delete" @click="handleDelete(row)" v-hasPermi="['system:oss:remove']" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 上传文档对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="600px" append-to-body>
      <el-form ref="uploadRef" :model="upload.form" :rules="upload.rules" label-width="80px">
        <el-form-item label="文件上传" prop="file">
          <div class="upload-container">
            <el-upload
            ref="uploadRef"
            :headers="upload.headers"
            :action="upload.url"
            :data="getUploadData"
            :limit="1"
            :on-progress="handleFileUploadProgress"
            :on-success="handleFileSuccess"
            :on-error="handleFileError"
            :on-exceed="handleFileExceed"
            :before-upload="handleBeforeUpload"
            :on-remove="handleFileRemove"
            :on-change="handleFileChange"
            :auto-upload="false"
            :show-file-list="true"
            drag
            class="upload-block"
            >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip">每次只能上传一个文件。</div>
            </el-upload>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleSubmitUpload" :loading="upload.isUploading">确 定</el-button>
          <el-button @click="cancelUpload">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Oss" lang="ts">
import { listOss, delOss, downloadOss } from '@/api/system/oss';
import { getToken } from '@/utils/auth';
import { parseTime } from '@/utils/ruoyi';
import { getCurrentInstance, ref, onMounted, ComponentInternalInstance } from 'vue';
import { Document, Upload, UploadFilled, VideoPlay, Headset, Files, Search, Refresh } from '@element-plus/icons-vue';

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
  originalName: undefined
});

// 上传参数
const upload = ref({
  open: false,
  title: '上传文件',
  isUploading: false,
  uploadId: null as string | null,
  get headers() {
    return {
      Authorization: 'Bearer ' + getToken(),
      clientid: import.meta.env.VITE_APP_CLIENT_ID
    };
  },
  url: import.meta.env.VITE_APP_BASE_API + '/system/oss/upload',
  fileList: [] as any[],
  fileSize: 5,
  limit: 1,
  form: {},
  rules: {}
});

/** 查询OSS列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await listOss(proxy?.addDateRange(queryParams.value, dateRange.value));

    if (res && res.rows !== undefined) {
      fileList.value = res.rows || [];
      total.value = res.total || 0;
    } else {
      console.error('OSS列表响应格式异常:', res);
      fileList.value = [];
      total.value = 0;
      proxy?.$modal.msgError('获取文件列表失败，请检查网络连接');
    }
  } catch (error) {
    console.error('OSS查询异常:', error);
    fileList.value = [];
    total.value = 0;
    proxy?.$modal.msgError('网络异常，请稍后重试');
  } finally {
    loading.value = false;
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
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    fileName: undefined,
    originalName: undefined
  };
  handleQuery();
};

/** 上传按钮操作 */
const handleUpload = () => {
  upload.value.form = {};
  upload.value.open = true;
  upload.value.title = '上传文件';
};

/** 获取上传数据 */
const getUploadData = () => {
  return {};
};

/** 提交上传文件 */
const handleSubmitUpload = () => {
  if (upload.value.fileList.length === 0) {
    proxy?.$modal.msgError('请选择要上传的文件');
    return;
  }

  upload.value.isUploading = true;
  (proxy?.$refs['uploadRef'] as any)?.submit();
};

/** 文件上传中处理 */
const handleFileUploadProgress = (event: any, file: any) => {
  upload.value.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response: any, file: any) => {
  if (response.code === 200) {
    upload.value.open = false;
    upload.value.isUploading = false;
    upload.value.fileList = [];
    getList();
    proxy?.$modal.msgSuccess('上传成功');
  } else {
    proxy?.$modal.msgError(response.msg || '上传失败');
  }
  upload.value.isUploading = false;
};

/** 文件上传失败处理 */
const handleFileError = (error: any) => {
  upload.value.isUploading = false;
  proxy?.$modal.msgError('上传失败，请重试');
};

/** 上传结束处理 */
const handleFileExceed = () => {
  proxy?.$modal.msgError('最多只能上传1个文件');
};

/** 上传前处理 */
const handleBeforeUpload = (file: any) => {
  const isValidSize = file.size / 1024 / 1024 < upload.value.fileSize;
  if (!isValidSize) {
    proxy?.$modal.msgError(`文件大小不能超过${upload.value.fileSize}MB!`);
  }
  return isValidSize;
};

/** 删除文件 */
const handleFileRemove = (file: any) => {
  const index = upload.value.fileList.indexOf(file);
  if (index > -1) {
    upload.value.fileList.splice(index, 1);
  }
};

/** 文件状态改变 */
const handleFileChange = (file: any, fileList: any[]) => {
  upload.value.fileList = fileList;
};

/** 取消上传文件 */
const cancelUpload = () => {
  upload.value.open = false;
  upload.value.fileList = [];
};

/** 多选框选中数据 */
const handleSelectionChange = (selection: any[]) => {
  ids.value = selection.map((item: any) => item.ossId);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

/** 下载操作 */
const handleDownload = (row: any) => {
  loading.value = true;
  downloadOss(row.ossId).then((response: any) => {
    const blob = new Blob([response]);
    const fileName = row.originalName || row.fileName || 'downloaded-file';
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = fileName;
    link.click();
    URL.revokeObjectURL(link.href);
    loading.value = false;
  }).catch((error: any) => {
    console.error('下载失败:', error);
    proxy?.$modal.msgError('下载失败');
    loading.value = false;
  });
};

/** 复制链接 */
const handleCopy = (row: any) => {
  const url = row.url;
  if (navigator.clipboard) {
    navigator.clipboard.writeText(url).then(() => {
      proxy?.$modal.msgSuccess('链接已复制到剪贴板');
    });
  } else {
    const textArea = document.createElement('textarea');
    textArea.value = url;
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand('copy');
    document.body.removeChild(textArea);
    proxy?.$modal.msgSuccess('链接已复制到剪贴板');
  }
};

/** 删除按钮操作 */
const handleDelete = (row?: any) => {
  //获取友好文件名
  const ossIds = row?.ossId || ids.value;
  const ossNames = row?.originalName || row?.fileName || '未知文件';
  proxy?.$modal.confirm(`是否确认删除文件"${ossNames}"？`).then(() => {
    return delOss(ossIds);
  }).then(() => {
    getList();
    proxy?.$modal.msgSuccess('删除成功');
  }).catch(() => {});
};

/** 获取显示的文件名 */
const getDisplayFileName = (row: any) => {
  return row.originalName || row.fileName || '未知文件';
};

/** 格式化文件大小 */
const formatFileSize = (bytes: number) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

/** 判断是否为文档文件 */
const isDocument = (fileName: string) => {
  const docExtensions = ['pdf', 'doc', 'docx', 'txt', 'md', 'xlsx', 'xls', 'ppt', 'pptx'];
  const ext = fileName?.split('.').pop()?.toLowerCase();
  return ext && docExtensions.includes(ext);
};

/** 判断是否为视频文件 */
const isVideo = (fileName: string) => {
  const videoExtensions = ['mp4', 'avi', 'mov', 'wmv', 'flv', 'mkv'];
  const ext = fileName?.split('.').pop()?.toLowerCase();
  return ext && videoExtensions.includes(ext);
};

/** 判断是否为音频文件 */
const isAudio = (fileName: string) => {
  const audioExtensions = ['mp3', 'wav', 'ogg', 'aac', 'flac'];
  const ext = fileName?.split('.').pop()?.toLowerCase();
  return ext && audioExtensions.includes(ext);
};

onMounted(() => {
  getList();
});
</script>

<style lang="scss" scoped>
.app-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
    }
  }

  .file-name-cell {
    display: flex;
    align-items: center;
    .mr-2 {
      margin-right: 8px;
    }
  }

  .upload-container {
    .upload-block {
      width: 100%;
      :deep(.el-upload-dragger) {
        width: 100%;
        height: 120px;
      }
    }
  }

  .text-muted {
    color: #909399;
  }

  :deep(.el-table__body-wrapper) {
    max-height: 600px;
    overflow-y: auto;
  }
}
</style>
