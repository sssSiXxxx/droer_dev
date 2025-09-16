<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="申请类型" prop="applicationType">
              <el-select v-model="queryParams.applicationType" placeholder="请选择申请类型" clearable>
                <el-option label="个人项目" value="personal" />
                <el-option label="社区项目" value="community" />
              </el-select>
            </el-form-item>
            <el-form-item label="申请状态" prop="applicationStatus">
              <el-select v-model="queryParams.applicationStatus" placeholder="请选择申请状态" clearable>
                <el-option label="草稿" value="draft" />
                <el-option label="待审核" value="pending" />
                <el-option label="已通过" value="approved" />
                <el-option label="已拒绝" value="rejected" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              <el-button type="info" icon="Back" @click="goBack">返回申请</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <!-- 专业申请记录列表 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="header-title">我的申请记录</span>
          <div class="header-actions">
            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="applicationRecordsList"
        stripe
        border
        style="width: 100%"
        class="application-records-table"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />

        <el-table-column prop="projectName" label="项目名称" min-width="200" align="center" show-overflow-tooltip>
          <template #default="scope">
            <div class="project-name-cell">
              <el-link type="primary" @click="handleViewProject(scope.row)" :underline="false">
                <el-icon class="mr-1"><View /></el-icon>
                {{ scope.row.projectName }}
              </el-link>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="applicationType" label="申请类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.applicationType === 'personal' ? 'success' : 'warning'" size="small">
              {{ scope.row.applicationType === 'personal' ? '个人项目' : '社区项目' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="applicationStatus" label="申请状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getApplicationStatusType(scope.row.applicationStatus)" size="small">
              {{ getApplicationStatusLabel(scope.row.applicationStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="申请时间" width="180" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}
          </template>
        </el-table-column>

        <el-table-column prop="repositoryUrl" label="仓库地址" min-width="250" align="center" show-overflow-tooltip>
          <template #default="scope">
            <el-link type="primary" :href="scope.row.repositoryUrl" target="_blank" :underline="false">
              {{ scope.row.repositoryUrl }}
            </el-link>
          </template>
        </el-table-column>

        <el-table-column prop="auditOpinion" label="审核意见" min-width="200" align="center" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.auditOpinion" class="audit-opinion">
              {{ scope.row.auditOpinion }}
            </span>
            <span v-else class="no-audit-opinion">-</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="scope">
            <el-tooltip content="查看详情" placement="top">
              <el-button
                type="primary"
                link
                size="small"
                icon="View"
                @click="handleViewProject(scope.row)"
              >详情</el-button>
            </el-tooltip>

            <el-tooltip v-if="scope.row.applicationStatus === 'rejected'" content="重新申请" placement="top">
              <el-button
                type="success"
                link
                size="small"
                icon="RefreshRight"
                @click="handleReapply(scope.row)"
              >重申</el-button>
            </el-tooltip>

            <el-tooltip v-if="scope.row.applicationStatus === 'draft'" content="继续编辑" placement="top">
              <el-button
                type="warning"
                link
                size="small"
                icon="Edit"
                @click="handleEdit(scope.row)"
              >编辑</el-button>
            </el-tooltip>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="您还没有提交过孵化申请" />
        </template>
      </el-table>

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

<script setup name="ApplicationRecords" lang="ts">
import { getApplicationRecords } from '@/api/osc/project';
import { ProjectQuery } from '@/api/osc/project/types';
import { View, RefreshRight, Edit } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/modules/user';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const router = useRouter();
const userStore = useUserStore();

const applicationRecordsList = ref<any[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();

const initQueryParams: ProjectQuery = {
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  status: undefined,
  description: undefined,
  userId: undefined,
  createBy: undefined,
  applicationType: undefined,
  applicationStatus: undefined,
  orderByColumn: undefined,
  isAsc: undefined,
  params: {}
};

const data = reactive<{ queryParams: ProjectQuery }>({
  queryParams: { ...initQueryParams }
});

const { queryParams } = toRefs(data);

/** 查询个人申请记录列表 */
const getList = async () => {
  loading.value = true;
  try {
    // 使用专门的申请记录API，能获取到审核意见
    const params = {
      createBy: userStore.userId,
      projectName: queryParams.value.projectName,
      applicationType: queryParams.value.applicationType,
      applicationStatus: queryParams.value.applicationStatus,
      pageNum: queryParams.value.pageNum,
      pageSize: queryParams.value.pageSize
    };
    console.log('查询参数:', params);

    const res = await getApplicationRecords(params);
    console.log('查询结果:', res);
    
    if (res.code === 200) {
      applicationRecordsList.value = res.rows;
      total.value = res.total;
    } else {
      proxy?.$modal.msgError(res.msg || '查询失败');
    }

    if (!applicationRecordsList.value || applicationRecordsList.value.length === 0) {
      if (queryParams.value.pageNum === 1) {
        proxy?.$modal.msgInfo('您还没有提交过孵化申请');
      }
    }
  } catch (error) {
    console.error('查询失败:', error);
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
  proxy?.$refs['queryFormRef'].resetFields();
  handleQuery();
};

/** 返回申请页面 */
const goBack = () => {
  router.push('/osc/projectCreate');
};

/** 获取申请状态标签 */
const getApplicationStatusLabel = (status: string) => {
  const statusMap = {
    'draft': '草稿',
    'pending': '待审核',
    'approved': '已通过',
    'rejected': '已拒绝'
  } as Record<string, string>;
  return statusMap[status] || status;
};

/** 获取申请状态类型 */
const getApplicationStatusType = (status: string) => {
  const typeMap = {
    'draft': 'info',
    'pending': 'warning',
    'approved': 'success',
    'rejected': 'danger'
  } as Record<string, 'success' | 'warning' | 'info' | 'primary' | 'danger'>;
  return (typeMap[status] || 'info') as 'success' | 'warning' | 'info' | 'primary' | 'danger';
};

/** 查看项目详情 */
const handleViewProject = async (row: any) => {
  try {
    // 直接使用申请记录中的项目数据
    const projectData = row;

    // 添加申请类型特有字段的显示
    let specificFields = '';
    if (projectData.applicationType === 'personal') {
      specificFields = `
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: #e8f4fd; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">申请理由：</strong>
          <p style="margin: 5px 0; color: #666;">${projectData.applicationReason || '未填写'}</p>
        </div>
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">预期贡献：</strong>
          <p style="margin: 5px 0; color: #666;">${projectData.contribution || '未填写'}</p>
        </div>
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: #e8f4fd; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">项目现状：</strong>
          <p style="margin: 5px 0; color: #666;">${projectData.currentStatus || '未填写'}</p>
        </div>
      `;
    } else if (projectData.applicationType === 'community') {
      specificFields = `
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: #fef7e6; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">升级理由：</strong>
          <p style="margin: 5px 0; color: #666;">${projectData.upgradeReason || '未填写'}</p>
        </div>
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">社区影响：</strong>
          <p style="margin: 5px 0; color: #666;">${projectData.communityImpact || '未填写'}</p>
        </div>
      `;
    }

    // 显示审核信息（如果有）
    let auditInfo = '';
    if (projectData.auditOpinion || projectData.auditTime) {
      auditInfo = `
        <div style="margin: 20px 0 2px 0; padding: 12px; background-color: #fff7e6; width: 100%; box-sizing: border-box; border-left: 4px solid #faad14;">
          <strong style="color: #333;">审核信息：</strong>
        </div>
        ${projectData.auditOpinion ? `
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">审核意见：</strong>
          <p style="margin: 5px 0; color: #666;">${projectData.auditOpinion}</p>
        </div>
        ` : ''}
        ${projectData.auditTime ? `
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">审核时间：</strong>
          <p style="margin: 5px 0; color: #666;">${proxy.parseTime(projectData.auditTime, '{y}-{m}-{d} {h}:{i}:{s}')}</p>
        </div>
        ` : ''}
      `;
    }

    ElMessageBox.alert(
      `
       <div style="text-align: left; width: 100%; margin: 0; padding: 0;">
         <h3 style="margin: 0 0 20px 0; color: #333; text-align: center; background-color: #fdfde7; padding: 15px; border-radius: 6px;">${projectData.projectName}</h3>

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">申请类型：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.applicationType === 'personal' ? '个人项目加入社区' : '社区项目升级为顶级项目'}</p>
         </div>

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">申请状态：</strong>
           <p style="margin: 5px 0; color: #666;">${getApplicationStatusLabel(projectData.applicationStatus)}</p>
         </div>

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">项目描述：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.description || '暂无描述'}</p>
         </div>

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">代码仓库：</strong>
           <p style="margin: 5px 0;">
             <a href="${projectData.repositoryUrl}" target="_blank" style="color: #409EFF;">${projectData.repositoryUrl}</a>
           </p>
         </div>

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">联系邮箱：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.contactEmail || '未提供'}</p>
         </div>

         ${specificFields}
         ${auditInfo}
       </div>
     `,
      '申请记录详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        customClass: 'project-detail-dialog'
      }
    );
  } catch (error) {
    console.error('获取申请记录详情失败:', error);
    proxy?.$modal.msgError('获取详情失败');
  }
};

/** 重新申请 */
const handleReapply = (row: any) => {
  proxy?.$modal.confirm('确定要基于此项目重新提交申请吗？')
    .then(() => {
      // 跳转到项目创建页面，并带上项目ID用于重新编辑
      router.push({
        path: '/osc/projectCreate',
        query: { reapplyId: row.projectId }
      });
    })
    .catch(() => {});
};

/** 编辑草稿 */
const handleEdit = (row: any) => {
  // 跳转到项目创建页面编辑草稿
  router.push({
    path: '/osc/projectCreate',
    query: { draftId: row.projectId }
  });
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

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.application-records-table {
  .project-name-cell {
    .el-link {
      font-weight: 500;
      display: flex;
      align-items: center;
    }
  }

  .audit-opinion {
    color: #666;
  }

  .no-audit-opinion {
    color: #c0c4cc;
    font-style: italic;
  }
}

/* 表格行悬停效果 */
.application-records-table :deep(.el-table__row:hover > td) {
  background: #f5f7fa !important;
}

/* 表格列超出显示省略号 */
.application-records-table :deep(.el-table__cell) {
  .cell {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
