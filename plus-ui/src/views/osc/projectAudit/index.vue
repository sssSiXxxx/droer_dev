<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="项目状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择项目状态" clearable>
                <el-option label="待审核" value="1" />
                <el-option label="进行中" value="2" />
                <el-option label="已完成" value="3" />
                <el-option label="已暂停" value="4" />
                <el-option label="已驳回" value="5" />
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

    <!-- 项目列表卡片 -->
    <el-empty v-if="!loading && (!projectAuditList || projectAuditList.length === 0)" description="暂无待审核项目" />
    <el-row v-else :gutter="20">
      <el-col :span="8" v-for="item in projectAuditList" :key="item.projectId">
        <el-card class="mb-4 project-card" shadow="hover">
          <template #header>
            <div class="flex justify-between items-center">
              <el-button type="text" class="font-bold text-left hover:text-blue-600 project-name-btn" @click="handleViewProject(item)">
                <el-icon class="mr-1"><View /></el-icon>
                {{ item.projectName }}
              </el-button>
              <div>
                <el-tag :type="getStatusType(item.status)">
                  {{ getStatusLabel(item.status) }}
                </el-tag>
              </div>
            </div>
          </template>
          <!-- 项目信息，使用交替背景色 -->
          <div class="info-row bg-[#f8fdfb]">
            <span class="info-label">技术栈：</span>
            <span>{{ getTechStackLabels(item.techStack).join(', ') }}</span>
          </div>
          <div class="info-row bg-white">
            <span class="info-label">仓库地址：</span>
            <el-link type="primary" :href="item.repositoryUrl" target="_blank" :underline="false">
              {{ item.repositoryUrl }}
            </el-link>
          </div>
          <div class="info-row bg-[#f8fdfb] description-row">
            <span class="info-label">项目描述：</span>
            <span class="description-content">{{ item.description }}</span>
          </div>
          <div class="info-row bg-white">
            <span class="info-label">联系方式：</span>
            <span>{{ item.contactInfo }}</span>
          </div>

          <!-- 审核操作区域 -->
          <div class="mt-4 flex justify-end" v-if="item.status === '1'">
            <el-button type="success" @click="handleQuickAudit(item.projectId, '1')">通过</el-button>
            <el-button type="danger" @click="openRejectDialog(item)">驳回</el-button>
            <el-button type="warning" @click="openAuditDialog(item)">详细审核</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />

    <!-- 驳回对话框 -->
    <el-dialog title="驳回原因" v-model="rejectDialog.visible" width="500px" append-to-body>
      <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="80px">
        <el-form-item label="驳回原因" prop="auditOpinion">
          <el-input type="textarea" v-model="rejectForm.auditOpinion" placeholder="请输入驳回原因" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleReject">确 定</el-button>
          <el-button @click="rejectDialog.visible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详细审核对话框 -->
    <el-dialog title="项目审核" v-model="auditDialog.visible" width="600px" append-to-body>
      <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" label-width="100px">
        <el-form-item label="审核状态" prop="auditStatus">
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio v-for="dict in osc_project_audit_status" :key="dict.value" :value="dict.value">
              {{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="auditOpinion">
          <el-input type="textarea" v-model="auditForm.auditOpinion" placeholder="请输入审核意见" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitAudit">确 定</el-button>
          <el-button @click="auditDialog.visible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectAudit" lang="ts">
import { listProject, getProject } from '@/api/osc/project';
import { auditProject } from '@/api/osc/projectAudit';
import { ProjectVO, ProjectQuery } from '@/api/osc/project/types';
import { View } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { osc_project_audit_status, osc_project_tech, osc_project_prolan } = toRefs<any>(
  proxy?.useDict('osc_project_audit_status', 'osc_project_tech', 'osc_project_prolan')
);

const projectAuditList = ref<ProjectVO[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

// 查询参数
const queryParams = ref<ProjectQuery>({
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  status: '1' // 只查询待审核状态的项目
});

// 驳回对话框
const rejectDialog = reactive({
  visible: false
});

const rejectForm = ref({
  projectId: undefined,
  auditStatus: '2', // 驳回状态
  auditOpinion: ''
});

const rejectRules = {
  auditOpinion: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }]
};

// 审核对话框
const auditDialog = reactive({
  visible: false
});

const auditForm = ref<any>({
  projectId: undefined,
  auditStatus: undefined,
  auditOpinion: undefined
});

const auditRules = {
  auditStatus: [{ required: true, message: '请选择审核状态', trigger: 'change' }],
  auditOpinion: [{ required: true, message: '请输入审核意见', trigger: 'blur' }]
};

/** 查询项目审核列表 */
const getList = async () => {
  loading.value = true;
  try {
    console.log('查询参数:', queryParams.value);
    const res = await listProject(queryParams.value);
    console.log('查询结果:', res);
    projectAuditList.value = res.rows;
    total.value = res.total;

    // 如果没有数据，显示提示信息
    if (!projectAuditList.value || projectAuditList.value.length === 0) {
      proxy?.$modal.msgWarning('暂无待审核项目');
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

/** 一键审核操作 */
const handleQuickAudit = async (projectId: number, status: string) => {
  try {
    await auditProject({
      projectId,
      auditStatus: status,
      auditOpinion: status === '1' ? '审核通过' : '审核不通过'
    });
    proxy?.$modal.msgSuccess('审核成功');
    await getList();
  } catch (error) {
    console.error('审核失败:', error);
  }
};

/** 打开驳回对话框 */
const openRejectDialog = (row: any) => {
  rejectForm.value.projectId = row.projectId;
  rejectForm.value.auditOpinion = '';
  rejectDialog.visible = true;
};

/** 提交驳回 */
const handleReject = () => {
  proxy?.$refs['rejectFormRef'].validate(async (valid: boolean) => {
    if (valid) {
      await handleQuickAudit(rejectForm.value.projectId, '2');
      rejectDialog.visible = false;
    }
  });
};

/** 打开详细审核对话框 */
const openAuditDialog = (row: any) => {
  auditForm.value = {
    projectId: row.projectId,
    auditStatus: undefined,
    auditOpinion: undefined
  };
  auditDialog.visible = true;
};

/** 提交详细审核 */
const submitAudit = () => {
  proxy?.$refs['auditFormRef'].validate(async (valid: boolean) => {
    if (valid) {
      await auditProject(auditForm.value);
      proxy?.$modal.msgSuccess('审核成功');
      auditDialog.visible = false;
      await getList();
    }
  });
};

/** 获取状态标签 */
const getStatusLabel = (status: string) => {
  const statusMap = {
    '0': '草稿',
    '1': '待审核',
    '2': '进行中',
    '3': '已完成',
    '4': '已暂停',
    '5': '已驳回'
  } as Record<string, string>;
  return statusMap[status] || status;
};

/** 获取状态类型 */
const getStatusType = (status: string) => {
  const typeMap = {
    '0': 'info',
    '1': 'warning',
    '2': 'primary',
    '3': 'success',
    '4': 'warning',
    '5': 'danger'
  } as Record<string, 'success' | 'warning' | 'info' | 'primary' | 'danger'>;
  return (typeMap[status] || 'info') as 'success' | 'warning' | 'info' | 'primary' | 'danger';
};

/** 获取技术栈标签 */
const getTechStackLabels = (techStack: string) => {
  if (!techStack) return [];
  const values = techStack.split(',');

  // 创建补充字典，处理缺失的标签
  const supplementDict = [
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

  // 合并字典数据
  const allDict = [...(osc_project_tech.value || []), ...supplementDict];

  return values.map((value) => {
    const trimmedValue = value.trim();
    // 先尝试按value查找
    let dict = allDict.find((item: any) => item.value === trimmedValue);
    // 如果没找到，再尝试按label查找（处理一些特殊值）
    if (!dict) {
      dict = allDict.find((item: any) => item.label === trimmedValue);
    }
    return dict ? dict.label : value;
  });
};

/** 查看项目详情 */
const handleViewProject = async (row: any) => {
  try {
    const response = await getProject(row.projectId);
    const projectData = response.data;

    // 获取技术栈和编程语言的标签
    const techStackText = projectData.techStack ? getTechStackLabels(projectData.techStack).join(', ') : '暂无技术栈信息';

    const programmingLanguageText = projectData.programmingLanguage
      ? projectData.programmingLanguage
          .split(',')
          .map((lang: string) => {
            const trimmedValue = lang.trim();
            // 先尝试按value查找
            let dict = osc_project_prolan.value?.find((item: any) => item.value === trimmedValue);
            // 如果没找到，再尝试按label查找（处理一些特殊值）
            if (!dict) {
              dict = osc_project_prolan.value?.find((item: any) => item.label === trimmedValue);
            }
            return dict ? dict.label : trimmedValue;
          })
          .join(', ')
      : '暂无编程语言信息';

    // 使用项目列表样式的项目详情
    ElMessageBox.alert(
      `
       <div style="text-align: left; width: 100%; margin: 0; padding: 0;">
         <h3 style="margin: 0 0 20px 0; color: #333; text-align: center; background-color: #fdfde7; padding: 15px; border-radius: 6px;">${projectData.projectName}</h3>
         
         <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">项目描述：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.description || '暂无描述'}</p>
         </div>
         
         <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">技术栈：</strong>
           <p style="margin: 5px 0; color: #666;">${techStackText}</p>
         </div>
         
         <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">编程语言：</strong>
           <p style="margin: 5px 0; color: #666;">${programmingLanguageText}</p>
         </div>
         
         <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">代码仓库：</strong>
           <p style="margin: 5px 0;">
             <a href="${projectData.repositoryUrl}" target="_blank" style="color: #409EFF;">${projectData.repositoryUrl || '暂无仓库地址'}</a>
           </p>
         </div>
         
         <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">项目网站：</strong>
           <p style="margin: 5px 0;">
             <a href="${projectData.websiteUrl}" target="_blank" style="color: #409EFF;">${projectData.websiteUrl || '暂无网站地址'}</a>
           </p>
         </div>
         
         <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">联系方式：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.contactInfo || '暂无联系方式'}</p>
         </div>
         
         <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">项目状态：</strong>
           <p style="margin: 5px 0; color: #666;">${getStatusLabel(projectData.status) || '暂无状态信息'}</p>
         </div>
         
         <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">备注：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.remark || '暂无备注'}</p>
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
  } catch (error) {
    console.error('获取项目详情失败:', error);
    proxy?.$modal.msgError('获取项目详情失败');
  }
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.project-card {
  margin-bottom: 20px;
}

.info-row {
  padding: 8px;
  display: flex;
  align-items: flex-start;
}

.info-label {
  font-weight: bold;
  width: 100px;
  flex-shrink: 0;
}

/* 项目描述特殊样式 */
.description-row {
  align-items: flex-start;
}

.description-content {
  flex: 1;
  word-wrap: break-word;
  word-break: break-all;
  white-space: pre-wrap;
  line-height: 1.5;
  color: #666;
  font-size: 14px;
}

.el-card :deep(.el-card__header) {
  padding: 10px 20px;
}

.project-name-btn {
  color: #409eff !important;
  text-decoration: underline;
  text-decoration-style: dotted;
  text-underline-offset: 2px;
}

.project-name-btn:hover {
  color: #66b1ff !important;
  text-decoration-style: solid;
}
</style>
