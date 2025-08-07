<template>
  <div class="p-2">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">项目草稿箱</span>
          <el-button type="primary" @click="$router.push('/osc/projectCreate')">
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
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 草稿列表 -->
      <el-table v-loading="loading" :data="draftList">
        <el-table-column label="项目名称" align="center" prop="projectName" />
        <el-table-column label="项目描述" align="center" prop="description" :show-overflow-tooltip="true" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleEdit(scope.row)">继续编辑</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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
  </div>
</template>

<script setup name="ProjectDraft" lang="ts">
import { listProject, delProject } from '@/api/osc/project';
import { ProjectVO, ProjectQuery } from '@/api/osc/project/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const loading = ref(false);
const total = ref(0);
const draftList = ref<ProjectVO[]>([]);

const queryParams = ref<ProjectQuery>({
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  status: '0' // 只查询草稿状态的项目
});

/** 查询草稿列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await listProject(queryParams.value);
    draftList.value = res.rows;
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

/** 编辑按钮操作 */
const handleEdit = (row: ProjectVO) => {
  proxy?.$router.push({
    path: '/osc/projectCreate',
    query: { projectId: row.projectId }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row: ProjectVO) => {
  try {
    await proxy?.$modal.confirm('是否确认删除该草稿？');
    await delProject(row.projectId);
    proxy?.$modal.msgSuccess('删除成功');
    await getList();
  } catch (error) {
    console.error('删除失败:', error);
  }
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
</style>