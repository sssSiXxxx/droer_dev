<template>
  <div class="p-2">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">我的创建</span>
          <el-button type="primary" @click="router.push('/osc/projectCreate')">
            <el-icon><Plus /></el-icon>
            创建项目
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="项目名称" prop="projectName">
          <el-input
            v-model="queryParams.projectName"
            placeholder="请输入项目名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择项目状态" clearable style="width: 200px">
            <el-option
              v-for="dict in projectStatusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 项目列表 -->
      <el-table v-loading="loading" :data="projectList">
        <el-table-column label="项目名称" align="center" prop="projectName" min-width="120" />
        <el-table-column label="项目描述" align="center" prop="description" :show-overflow-tooltip="true" min-width="200" />
        <el-table-column label="项目状态" align="center" prop="status" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="技术栈" align="center" prop="techStack" :show-overflow-tooltip="true" min-width="150">
          <template #default="scope">
            <el-tag
              v-for="tech in (scope.row.techStack || '').split(',')"
              :key="tech"
              class="mx-1"
              size="small"
            >
              {{ tech }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="编程语言" align="center" prop="programmingLanguage" :show-overflow-tooltip="true" min-width="150">
          <template #default="scope">
            <el-tag
              v-for="lang in (scope.row.programmingLanguage || '').split(',')"
              :key="lang"
              class="mx-1"
              type="success"
              size="small"
            >
              {{ lang }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后更新时间" align="center" prop="updateTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.updateTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button type="primary" link icon="View" @click="handleView(scope.row)">查看</el-button>
            <el-button 
              v-if="scope.row.status === '5'"
              type="warning" 
              link 
              icon="Edit" 
              @click="handleEdit(scope.row)"
            >修改</el-button>
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

    <!-- 查看对话框 -->
    <el-dialog title="项目详情" v-model="viewDialogVisible" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="项目名称">{{ viewForm.projectName }}</el-descriptions-item>
        <el-descriptions-item label="项目状态">
          <el-tag :type="getStatusType(viewForm.status)">
            {{ getStatusLabel(viewForm.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="项目描述" :span="2">{{ viewForm.description }}</el-descriptions-item>
        <el-descriptions-item label="技术栈" :span="2">
          <el-tag
            v-for="tech in (viewForm.techStack || '').split(',')"
            :key="tech"
            class="mx-1"
          >
            {{ tech }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="编程语言" :span="2">
          <el-tag
            v-for="lang in (viewForm.programmingLanguage || '').split(',')"
            :key="lang"
            class="mx-1"
            type="success"
          >
            {{ lang }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="代码仓库" :span="2">
          <el-link type="primary" :href="viewForm.repositoryUrl" target="_blank">
            {{ viewForm.repositoryUrl }}
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item label="项目网站" :span="2">
          <el-link type="primary" :href="viewForm.websiteUrl" target="_blank">
            {{ viewForm.websiteUrl }}
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ viewForm.contactInfo }}</el-descriptions-item>
        <el-descriptions-item label="版本信息">{{ viewForm.versionInfo }}</el-descriptions-item>
        <el-descriptions-item label="项目Logo" :span="2">
          <el-image
            v-if="viewForm.logoUrl"
            :src="viewForm.logoUrl"
            :preview-src-list="[viewForm.logoUrl]"
            fit="contain"
            style="max-height: 100px"
          />
          <span v-else>暂无Logo</span>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewForm.remark }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(viewForm.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="最后更新时间">{{ parseTime(viewForm.updateTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MyProject" lang="ts">
import { listProject, getProject } from '@/api/osc/project';
import { ProjectVO, ProjectQuery } from '@/api/osc/project/types';
import { getCurrentInstance, ref } from 'vue';
import { useRouter } from 'vue-router';

const { proxy } = getCurrentInstance() as any;
const router = useRouter();

const loading = ref(false);
const total = ref(0);
const projectList = ref<ProjectVO[]>([]);
const viewDialogVisible = ref(false);
const viewForm = ref<ProjectVO>({});

// 项目状态选项
const projectStatusOptions = [
  { label: '进行中', value: '2' },
  { label: '已完成', value: '3' },
  { label: '已驳回', value: '5' }
];

// 查询参数
const queryParams = ref<ProjectQuery>({
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  status: undefined,
  createBy: proxy?.useUserStore().userId,
  params: {
    statusList: ['2', '3', '5'] // 排除草稿和待审核状态
  }
});

/** 查询项目列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await listProject(queryParams.value);
    projectList.value = res.rows;
    total.value = res.total;
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
  proxy?.$refs['queryRef'].resetFields();
  handleQuery();
};

/** 查看按钮操作 */
const handleView = async (row: ProjectVO) => {
  try {
    const res = await getProject(row.projectId);
    viewForm.value = res.data;
    viewDialogVisible.value = true;
  } catch (error) {
    console.error('获取项目详情失败:', error);
  }
};

/** 修改按钮操作 */
const handleEdit = (row: ProjectVO) => {
  router.push({
    path: '/osc/projectCreate',
    query: { projectId: row.projectId }
  });
};

/** 获取状态标签 */
const getStatusLabel = (status: string) => {
  const statusMap = {
    '2': '进行中',
    '3': '已完成',
    '5': '已驳回'
  };
  return statusMap[status] || status;
};

/** 获取状态类型 */
const getStatusType = (status: string) => {
  const typeMap = {
    '2': 'primary',
    '3': 'success',
    '5': 'danger'
  };
  return typeMap[status] || '';
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.mx-1 {
  margin: 0 4px;
}

:deep(.el-descriptions__label) {
  font-weight: bold;
}

.dialog-footer {
  text-align: right;
}
</style>