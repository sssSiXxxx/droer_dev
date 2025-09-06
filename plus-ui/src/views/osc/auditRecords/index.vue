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
            <el-form-item label="审核状态" prop="applicationStatus">
              <el-select v-model="queryParams.applicationStatus" placeholder="请选择审核状态" clearable>
                <el-option label="已通过" value="approved" />
                <el-option label="已拒绝" value="rejected" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              <el-button type="info" icon="Back" @click="goBack">返回审核</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <!-- 审核记录列表 -->
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div class="header-actions">
            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="auditRecordsList"
        stripe
        border
        header-align="center"
        style="width: 100%"
        class="audit-records-table"
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

        <el-table-column prop="applicationStatus" label="审核结果" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getApplicationStatusType(scope.row.applicationStatus)" size="small">
              {{ getApplicationStatusLabel(scope.row.applicationStatus) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="auditOpinion" label="审核意见" min-width="200" align="center" show-overflow-tooltip />

        <el-table-column prop="auditTime" label="审核时间" width="180" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.auditTime, '{y}-{m}-{d} {h}:{i}') }}
          </template>
        </el-table-column>

        <el-table-column prop="repositoryUrl" label="仓库地址" min-width="250" align="center" show-overflow-tooltip>
          <template #default="scope">
            <el-link type="primary" :href="scope.row.repositoryUrl" target="_blank" :underline="false">
              {{ scope.row.repositoryUrl }}
            </el-link>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center" fixed="right">
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
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无审核记录" />
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

<script setup name="AuditRecords" lang="ts">
import { listProjectAudit } from '@/api/osc/projectAudit';
import { getProject } from '@/api/osc/project';
import { ProjectAuditQuery } from '@/api/osc/projectAudit/types';
import { View } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const router = useRouter();

const auditRecordsList = ref<any[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();

const initQueryParams: ProjectAuditQuery = {
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  auditStatus: undefined,
  auditUser: undefined,
  params: {}
};

const data = reactive<{ queryParams: ProjectAuditQuery }>({
  queryParams: { ...initQueryParams }
});

const { queryParams } = toRefs(data);

/** 查询审核记录列表 */
const getList = async () => {
  loading.value = true;
  try {
    // 只查询已处理的审核记录（已通过或已拒绝）
    const searchParams = {
      ...queryParams.value,
      applicationStatus: queryParams.value.applicationStatus || 'approved,rejected'
    };
    console.log('查询参数:', searchParams);

    const res = await listProjectAudit(searchParams);
    console.log('查询结果:', res);
    auditRecordsList.value = res.rows;
    total.value = res.total;

    if (!auditRecordsList.value || auditRecordsList.value.length === 0) {
      proxy?.$modal.msgInfo('暂无审核记录');
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

/** 返回审核页面 */
const goBack = () => {
  router.push('/osc/projectAudit');
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
    // 直接使用审核记录中的项目数据
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
      `;
    } else if (projectData.applicationType === 'community') {
      specificFields = `
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: #fef7e6; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">升级理由：</strong>
          <p style="margin: 5px 0; color: #666;">${projectData.upgradeReason || '未填写'}</p>
        </div>
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
           <strong style="color: #333;">项目描述：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.description || '暂无描述'}</p>
         </div>

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">代码仓库：</strong>
           <p style="margin: 5px 0;">
             <a href="${projectData.repositoryUrl}" target="_blank" style="color: #409EFF;">${projectData.repositoryUrl}</a>
           </p>
         </div>

         ${specificFields}

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">审核意见：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.auditOpinion || '无'}</p>
         </div>

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">审核时间：</strong>
           <p style="margin: 5px 0; color: #666;">${proxy.parseTime(projectData.auditTime, '{y}-{m}-{d} {h}:{i}:{s}')}</p>
         </div>
       </div>
     `,
      '审核记录详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        customClass: 'project-detail-dialog'
      }
    );
  } catch (error) {
    console.error('获取审核记录详情失败:', error);
    proxy?.$modal.msgError('获取详情失败');
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

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.audit-records-table {
  .project-name-cell {
    .el-link {
      font-weight: 500;
      display: flex;
      align-items: center;
    }
  }
}

/* 表格行悬停效果 */
.audit-records-table :deep(.el-table__row:hover > td) {
  background: #f5f7fa !important;
}

/* 审核意见列超出显示省略号 */
.audit-records-table :deep(.el-table__cell) {
  .cell {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
