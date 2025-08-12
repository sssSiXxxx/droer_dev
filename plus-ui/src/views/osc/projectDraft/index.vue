<template>
  <div class="app-container">
    <el-card class="box-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-icon class="header-icon"><Document /></el-icon>
            <span class="title">项目草稿箱</span>
            <el-tag type="info" size="small">{{ total }} 个草稿</el-tag>
          </div>
          <el-button type="primary" @click="() => proxy?.$router.push('/osc/projectCreate')" :icon="Plus">
            创建项目
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" class="search-form">
        <el-form-item label="项目名称" prop="projectName">
          <el-input
            v-model="queryParams.projectName"
            placeholder="请输入项目名称"
            clearable
            style="width: 250px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 草稿列表 -->
      <el-table 
        v-loading="loading" 
        :data="draftList" 
        stripe
        border
        class="draft-table"
      >
        <el-table-column label="项目名称" align="center" prop="projectName" min-width="180">
          <template #default="scope">
            <div class="project-name">
              <el-icon class="project-icon"><Folder /></el-icon>
              <span class="name-text">{{ scope.row.projectName || '未命名项目' }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="项目描述" align="center" prop="description" min-width="250">
          <template #default="scope">
            <div class="description-cell">
              <span class="description-text">{{ scope.row.description || '暂无描述' }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="完成度" align="center" min-width="120">
          <template #default="scope">
            <el-progress 
              :percentage="calculateCompletion(scope.row)" 
              color="#67c23a"
              :stroke-width="8"
              :format="(percentage) => percentage + '%'"
            />
          </template>
        </el-table-column>
        
        <el-table-column label="最后编辑时间" align="center" prop="updateTime" min-width="160">
          <template #default="scope">
            <span>{{ scope.row.updateTime ? parseTime(scope.row.updateTime, '{y}-{m}-{d} {h}:{i}') : '暂无' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="创建时间" align="center" prop="createTime" min-width="160">
          <template #default="scope">
            <span>{{ scope.row.createTime ? parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') : '暂无' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" align="center" min-width="160">
          <template #default="scope">
            <el-button 
              type="primary" 
              link 
              icon="Edit" 
              @click="handleEdit(scope.row)"
              class="action-btn"
            >
              继续编辑
            </el-button>
            <el-button 
              type="danger" 
              link 
              icon="Delete" 
              @click="handleDelete(scope.row)"
              class="action-btn"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <pagination
          v-show="total > 0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup name="ProjectDraft" lang="ts">
import { listProject, delProject } from '@/api/osc/project';
import { ProjectVO, ProjectQuery } from '@/api/osc/project/types';
import { getCurrentInstance, ref, onMounted } from 'vue';
import { useUserStore } from '@/store/modules/user';
import { parseTime } from '@/utils/ruoyi';
import { Plus, Document, Folder, Clock, Calendar } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance() as any;
const userStore = useUserStore();

const loading = ref(false);

/** 计算项目完成度 */
const calculateCompletion = (project: ProjectVO): number => {
  const requiredFields = [
    'projectName',
    'description',
    'repositoryUrl'
  ];

  const optionalFields = [
    'websiteUrl',
    'logoUrl',
    'techStack',
    'programmingLanguage',
    'coreContributors',
    'contactInfo',
    'versionInfo'
  ];

  // 必填字段权重为2，选填字段权重为1
  const requiredWeight = 2;
  const optionalWeight = 1;

  // 计算总权重
  const totalWeight = requiredFields.length * requiredWeight + optionalFields.length * optionalWeight;

  // 计算已填写字段的权重和
  let completedWeight = 0;

  // 检查必填字段
  requiredFields.forEach(field => {
    if ((project as any)[field]) {
      completedWeight += requiredWeight;
    }
  });

  // 检查选填字段
  optionalFields.forEach(field => {
    if ((project as any)[field]) {
      completedWeight += optionalWeight;
    }
  });

  // 计算完成度百分比
  return Math.round((completedWeight / totalWeight) * 100);
};

/** 获取进度条颜色 */
const getProgressColor = (percentage: number) => {
  if (percentage >= 80) {
    return '#67c23a'; // 绿色
  } else if (percentage >= 50) {
    return '#e6a23c'; // 橙色
  } else {
    return '#f56c6c'; // 红色
  }
};

const total = ref(0);
const draftList = ref<ProjectVO[]>([]);

const queryParams = ref<ProjectQuery>({
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  status: '0', // 只查询草稿状态的项目
  createBy: userStore.userId // 只查当前用户
});

/** 查询草稿列表 */
const getList = async () => {
  loading.value = true;
  try {
    // 兜底 createBy
    if (!queryParams.value.createBy) {
      queryParams.value.createBy = userStore.userId;
    }
    console.log('查询参数:', queryParams.value);
    const res = await listProject(queryParams.value);
    console.log('查询结果:', res);
    draftList.value = res.rows || [];
    total.value = res.total || 0;
    
    // 调试时间字段
    if (draftList.value.length > 0) {
      console.log('第一条数据的时间字段:', {
        createTime: draftList.value[0].createTime,
        updateTime: draftList.value[0].updateTime,
        parsedCreateTime: parseTime(draftList.value[0].createTime),
        parsedUpdateTime: parseTime(draftList.value[0].updateTime)
      });
    }
  } catch (error) {
    console.error('查询草稿列表失败:', error);
    const msg = (error && typeof error === 'object' && 'message' in (error as any)) ? (error as any).message : '未知错误';
    proxy?.$modal?.msgError && proxy.$modal.msgError('查询失败：' + msg);
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
  proxy?.$refs['queryRef']?.resetFields?.();
  // 重置后保持 createBy
  queryParams.value.createBy = userStore.userId;
  handleQuery();
};

/** 编辑按钮操作 */
const handleEdit = (row: ProjectVO) => {
  proxy?.$router.push({
    path: '/osc/projectCreate',
    query: { projectId: (row as any).projectId }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row: ProjectVO) => {
  try {
    await proxy?.$modal.confirm('是否确认删除该草稿？');
    await delProject((row as any).projectId);
    proxy?.$modal.msgSuccess('删除成功');
    await getList();
  } catch (error) {
    console.error('删除失败:', error);
  }
};

onMounted(() => {
  if (!queryParams.value.createBy) {
    queryParams.value.createBy = userStore.userId;
  }
  getList();
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-icon {
  margin-right: 8px;
  font-size: 20px;
  color: #409eff;
}

.title {
  font-size: 18px;
  font-weight: bold;
  margin-right: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.draft-table {
  width: 100%;
}

.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.project-icon {
  margin-right: 8px;
  font-size: 18px;
  color: #409eff;
}

.name-text {
  font-weight: bold;
  color: #303133;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.description-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.description-text {
  color: #606266;
  font-size: 14px;
  max-width: 220px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.action-btn {
  padding: 4px 8px;
  margin: 0 2px;
  font-size: 12px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>