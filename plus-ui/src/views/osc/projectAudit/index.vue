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
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              <el-button type="info" icon="Document" @click="goToAuditRecords">审核记录</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <!-- 孵化申请列表卡片 -->
    <el-empty v-if="!loading && (!projectAuditList || projectAuditList.length === 0)" description="暂无待审核的孵化申请" />
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
                <el-tag :type="getApplicationStatusType(item.applicationStatus)">
                  {{ getApplicationStatusLabel(item.applicationStatus) }}
                </el-tag>
              </div>
            </div>
          </template>
          <!-- 项目信息，使用交替背景色 -->
          <div class="info-row bg-[#f8fdfb]">
            <span class="info-label">申请类型：</span>
            <el-tag :type="item.applicationType === 'personal' ? 'success' : 'warning'" size="small">
              {{ item.applicationType === 'personal' ? '个人项目' : '社区项目' }}
            </el-tag>
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
            <span class="info-label">开源协议：</span>
            <span>{{ item.license || '未选择' }}</span>
          </div>
          <div class="info-row bg-[#f8fdfb]">
            <span class="info-label">联系邮箱：</span>
            <span>{{ item.contactEmail || '未提供' }}</span>
          </div>

          <!-- 审核操作区域 -->
          <div
              class="mt-4 flex justify-between"
              v-if="item.applicationStatus === 'pending' || item.status === '1'"
          >
            <!-- 左边：审核操作 -->
            <div class="flex gap-3">
              <el-button type="success" @click="handleQuickAudit(item.projectId, '1')">通过</el-button>
              <el-button type="danger" @click="openRejectDialog(item)">驳回</el-button>
            </div>

            <!-- 右边：其他操作 -->
            <div>
              <el-button
                  v-if="item.applicationType === 'personal'"
                  type="primary"
                  @click="openProjectJoinDialog(item)"
              >
                加入项目列表
              </el-button>
              <el-button
                  v-else
                  type="warning"
                  @click="openAuditDialog(item)"
              >
                详细审核
              </el-button>
            </div>
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

    <el-dialog
        title="孵化申请详细审核"
        v-model="auditDialog.visible"
        width="800px"
        append-to-body
    >
      <div class="audit-detail-content">
        <!-- 基本信息 -->
        <div class="info-section">
          <h4>基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="项目名称">
              {{ currentAuditItem.projectName }}
            </el-descriptions-item>
            <el-descriptions-item label="申请类型">
              <el-tag
                  :type="currentAuditItem.applicationType === 'personal' ? 'success' : 'warning'"
                  size="small"
              >
                {{ currentAuditItem.applicationType === 'personal' ? '个人项目' : '社区项目' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="开源协议">
              {{ currentAuditItem.license || '未选择' }}
            </el-descriptions-item>
            <el-descriptions-item label="代码仓库">
              <el-link :href="currentAuditItem.repositoryUrl" target="_blank" type="primary">
                {{ currentAuditItem.repositoryUrl }}
              </el-link>
            </el-descriptions-item>
            <el-descriptions-item label="项目网站" v-if="currentAuditItem.websiteUrl">
              <el-link :href="currentAuditItem.websiteUrl" target="_blank" type="primary">
                {{ currentAuditItem.websiteUrl }}
              </el-link>
            </el-descriptions-item>
            <el-descriptions-item label="项目描述" :span="2">
              <div class="description-text">{{ currentAuditItem.description }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 个人项目特有信息 -->
        <div class="info-section" v-if="currentAuditItem.applicationType === 'personal'">
          <h4>个人项目信息</h4>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="申请理由">
              <div class="description-text">{{ currentAuditItem.applicationReason }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="预期贡献">
              <div class="description-text">{{ currentAuditItem.contribution }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="项目现状">
              <div class="description-text">{{ currentAuditItem.currentStatus }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 社区项目特有信息 -->
        <div class="info-section" v-if="currentAuditItem.applicationType === 'community'">
          <h4>社区项目信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="升级理由" :span="2">
              <div class="description-text">{{ currentAuditItem.upgradeReason }}</div>
            </el-descriptions-item>
            <el-descriptions-item label="社区影响" :span="2">
              <div class="description-text">{{ currentAuditItem.communityImpact }}</div>
            </el-descriptions-item>

            <pre>{{ currentAuditItem }}</pre>

            <el-descriptions-item label="Star 数量">
              {{ currentAuditItem.starCount ?? 0 }}
            </el-descriptions-item>
            <el-descriptions-item label="Fork 数量">
              {{ currentAuditItem.forkCount || 0 }}
            </el-descriptions-item>
            <el-descriptions-item label="Issues 数量">
              {{ currentAuditItem.issuesCount || 0 }}
            </el-descriptions-item>
            <el-descriptions-item label="PR 数量">
              {{ currentAuditItem.prCount || 0 }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 联系信息 -->
        <div class="info-section">
          <h4>联系信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="联系邮箱">
              {{ currentAuditItem.contactEmail || '未提供' }}
            </el-descriptions-item>
            <el-descriptions-item label="联系电话">
              {{ currentAuditItem.contactPhone || '未提供' }}
            </el-descriptions-item>
            <el-descriptions-item label="备注" v-if="currentAuditItem.remarks" :span="2">
              <div class="description-text">{{ currentAuditItem.remarks }}</div>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>

      <el-divider />

      <el-form ref="auditFormRef" :model="auditForm" :rules="auditRules" label-width="100px">
        <el-form-item label="审核状态" prop="auditStatus">
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio value="2">通过</el-radio>
            <el-radio value="3">拒绝</el-radio>
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

    <!-- 个人项目加入对话框 -->
    <el-dialog title="个人项目加入项目列表" v-model="projectJoinDialog.visible" width="600px" append-to-body>
      <div class="join-dialog-content">
        <!-- 项目基本信息 -->
        <div class="project-info-section">
          <h4>项目信息</h4>
          <div class="project-basic-info">
            <div class="info-row">
              <strong>项目名称：</strong>
              <span>{{ currentJoinItem.projectName }}</span>
            </div>
            <div class="info-row">
              <strong>项目描述：</strong>
              <span>{{ currentJoinItem.description }}</span>
            </div>
            <div class="info-row">
              <strong>代码仓库：</strong>
              <el-link :href="currentJoinItem.repositoryUrl" target="_blank" type="primary">
                {{ currentJoinItem.repositoryUrl }}
              </el-link>
            </div>
          </div>
        </div>

        <!-- 加入选择 -->
        <div class="join-selection-section">
          <h4>加入选择</h4>
          <el-radio-group v-model="joinForm.joinOption">
            <el-radio value="approve">
              <div class="option-content">
                <div class="option-title">通过申请并加入项目列表</div>
                <div class="option-desc">审核通过，项目将显示在项目列表中</div>
              </div>
            </el-radio>
            <el-radio value="approve_only">
              <div class="option-content">
                <div class="option-title">仅通过申请</div>
                <div class="option-desc">审核通过，但不加入项目列表</div>
              </div>
            </el-radio>
          </el-radio-group>
        </div>

        <!-- 审核意见 -->
        <div class="opinion-section">
          <h4>审核意见</h4>
          <el-input
            v-model="joinForm.auditOpinion"
            type="textarea"
            :rows="3"
            placeholder="请输入审核意见（可选）"
          />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="handleProjectJoin">确 定</el-button>
          <el-button @click="projectJoinDialog.visible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectAudit" lang="ts">
import { listProject, getProject } from '@/api/osc/project';
import { listProjectAudit, auditProject } from '@/api/osc/projectAudit';
import { ProjectVO, ProjectQuery } from '@/api/osc/project/types';
import { View } from '@element-plus/icons-vue';
import { ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const router = useRouter();

const projectAuditList = ref<ProjectVO[]>([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);

// 批量选择相关
const multipleSelection = ref<ProjectVO[]>([]);

// 查询参数 - 固定为待审核状态
const queryParams = ref<ProjectQuery>({
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  applicationType: undefined,
  applicationStatus: 'pending' // 固定为待审核状态，不允许更改
});

// 驳回对话框
const rejectDialog = reactive({
  visible: false
});

const rejectForm = ref({
  projectId: undefined,
  auditStatus: '3', // 驳回状态
  auditOpinion: ''
});

const rejectRules = {
  auditOpinion: [{ required: true, message: '请输入驳回原因', trigger: 'blur' }]
};

// 审核对话框
const auditDialog = reactive({
  visible: false
});

// 当前审核项目信息
const currentAuditItem = ref<any>({});

const auditForm = ref<any>({
  projectId: undefined,
  auditStatus: undefined,
  auditOpinion: undefined
});

const auditRules = {
  auditStatus: [{ required: true, message: '请选择审核状态', trigger: 'change' }],
  auditOpinion: [{ required: true, message: '请输入审核意见', trigger: 'blur' }]
};

// 个人项目加入对话框
const projectJoinDialog = reactive({
  visible: false
});

const currentJoinItem = ref<any>({});

const joinForm = ref({
  projectId: undefined,
  joinOption: 'approve', // approve: 通过并加入, approve_only: 仅通过
  auditOpinion: ''
});

/** 查询项目审核列表 */
const getList = async () => {
  loading.value = true;
  try {
    // 强制确保只查询待审核状态
    queryParams.value.applicationStatus = 'pending';

    console.log('审核列表查询参数:', queryParams.value);
    console.log('固定查询状态:', queryParams.value.applicationStatus);

    const res = await listProjectAudit(queryParams.value);
    console.log('审核列表查询结果:', res);
    console.log('返回记录数:', res.rows ? res.rows.length : 0);

    // 双重过滤：确保只有待审核状态的记录
    const pendingRows = res.rows ? res.rows.filter(item => {
      const isPending = item.applicationStatus === 'pending';

      console.log(`项目 ${item.projectName}: status=${item.applicationStatus}, isPending=${isPending}`);
      return isPending;
    }) : [];

    projectAuditList.value = pendingRows;
    total.value = pendingRows.length;

    console.log('过滤后待审核记录数:', projectAuditList.value.length);

    // 调试每条记录的状态
    if (projectAuditList.value.length > 0) {
      console.log('待审核列表状态分布:');
      projectAuditList.value.forEach((item, index) => {
        console.log(`记录${index + 1}:`, {
          name: item.projectName,
          applicationStatus: item.applicationStatus,
          applicationType: item.applicationType,
          id: item.projectId
        });
      });
    } else {
      console.log('当前无待审核项目');
    }
  } catch (error) {
    console.error('查询失败:', error);
    proxy?.$modal.msgError('查询审核列表失败');
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
    // 获取当前项目信息，判断是个人项目还是社区项目
    const currentProject = projectAuditList.value.find(item => item.projectId === projectId);

    if (!currentProject) {
      proxy?.$modal.msgError('未找到项目信息');
      return;
    }

    // 审核数据，明确标识社区项目处理方式
    const auditData = {
      projectId,
      auditStatus: status,
      auditOpinion: status === '1' ? '孵化申请审核通过' : '孵化申请审核不通过',
      applicationType: currentProject.applicationType, // 传递申请类型
      // 关键参数：社区项目不创建新记录，只更新状态
      isUpdateOnly: currentProject.applicationType === 'community',
      skipProjectCreation: currentProject.applicationType === 'community'
    };

    console.log('=== 快速审核数据分析 ===');
    console.log('项目ID:', projectId);
    console.log('项目名称:', currentProject.projectName);
    console.log('申请类型:', currentProject.applicationType);
    console.log('审核状态:', status === '1' ? '通过' : '拒绝');
    console.log('是否仅更新状态:', auditData.isUpdateOnly);
    console.log('是否跳过项目创建:', auditData.skipProjectCreation);
    console.log('完整审核数据:', auditData);

    await auditProject(auditData);

    // 审核成功提示
    const statusText = status === '1' ? '通过' : '驳回';
    let message;

    if (currentProject.applicationType === 'community') {
      message = `社区项目升级审核${statusText}成功！项目状态已更新（未创建新项目），记录已存入审核记录`;
    } else {
      message = `个人项目审核${statusText}成功，记录已存入审核记录`;
    }

    proxy?.$modal.msgSuccess(message);

    // 审核完成后重新加载列表，已审核的记录将不再显示在待审核列表中
    await getList();

    // 提示用户可以在审核记录中查看
    setTimeout(() => {
      proxy?.$modal.msgInfo('已审核的记录可在"审核记录"页面查看');
    }, 1500);

  } catch (error) {
    console.error('审核失败:', error);
    proxy?.$modal.msgError('审核失败，请重试');
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
      // 获取当前项目信息
      const currentProject = projectAuditList.value.find(item => item.projectId === rejectForm.value.projectId);

      if (!currentProject) {
        proxy?.$modal.msgError('未找到项目信息');
        return;
      }

      const auditData = {
        projectId: rejectForm.value.projectId,
        auditStatus: '2', // 驳回状态
        auditOpinion: rejectForm.value.auditOpinion,
        applicationType: currentProject.applicationType, // 传递申请类型
        // 驳回时不需要创建新项目，只更新申请状态
        isUpdateOnly: true,
        skipProjectCreation: true
      };

      console.log('=== 驳回审核数据分析 ===');
      console.log('项目ID:', rejectForm.value.projectId);
      console.log('项目名称:', currentProject.projectName);
      console.log('申请类型:', currentProject.applicationType);
      console.log('驳回原因:', rejectForm.value.auditOpinion);
      console.log('完整审核数据:', auditData);

      await auditProject(auditData);
      proxy?.$modal.msgSuccess('驳回成功，记录已存入审核记录');
      rejectDialog.visible = false;

      // 重新加载列表，已驳回的记录将不再显示在待审核列表中
      await getList();

      // 提示用户可以在审核记录中查看
      setTimeout(() => {
        proxy?.$modal.msgInfo('已审核的记录可在"审核记录"页面查看');
      }, 1500);
    }
  });
};

/** 打开详细审核对话框 */
const openAuditDialog = (row: any) => {
  currentAuditItem.value = row;
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
      // 获取当前项目信息
      const currentProject = currentAuditItem.value;

      if (!currentProject) {
        proxy?.$modal.msgError('未找到项目信息');
        return;
      }

      const auditData = {
        ...auditForm.value,
        applicationType: currentProject.applicationType, // 传递申请类型
        // 关键参数：社区项目不创建新记录，只更新状态
        isUpdateOnly: currentProject.applicationType === 'community',
        skipProjectCreation: currentProject.applicationType === 'community'
      };

      console.log('=== 详细审核数据分析 ===');
      console.log('项目ID:', auditForm.value.projectId);
      console.log('项目名称:', currentProject.projectName);
      console.log('申请类型:', currentProject.applicationType);
      console.log('审核状态:', auditForm.value.auditStatus === '2' ? '通过' : '拒绝');
      console.log('是否仅更新状态:', auditData.isUpdateOnly);
      console.log('是否跳过项目创建:', auditData.skipProjectCreation);
      console.log('完整审核数据:', auditData);

      await auditProject(auditData);

      // 根据审核状态给出不同提示
      const statusText = auditForm.value.auditStatus === '2' ? '通过' : '拒绝';
      let message;

      if (currentProject.applicationType === 'community') {
        message = `社区项目升级详细审核${statusText}成功！项目状态已更新（未创建新项目），记录已存入审核记录`;
      } else {
        message = `个人项目详细审核${statusText}成功，记录已存入审核记录`;
      }

      proxy?.$modal.msgSuccess(message);

      auditDialog.visible = false;

      // 重新加载列表，已审核的记录将不再显示在待审核列表中
      await getList();

      // 提示用户可以在审核记录中查看
      setTimeout(() => {
        proxy?.$modal.msgInfo('已审核的记录可在"审核记录"页面查看');
      }, 1500);
    }
  });
};

/** 打开个人项目加入对话框 */
const openProjectJoinDialog = (row: any) => {
  currentJoinItem.value = row;
  joinForm.value = {
    projectId: row.projectId,
    joinOption: 'approve',
    auditOpinion: ''
  };
  projectJoinDialog.visible = true;
};

/** 处理个人项目加入 */
const handleProjectJoin = async () => {
  try {
    const currentProject = currentJoinItem.value;

    if (!currentProject || currentProject.applicationType !== 'personal') {
      proxy?.$modal.msgError('此功能只适用于个人项目申请');
      return;
    }

    const auditData = {
      projectId: joinForm.value.projectId,
      auditStatus: '1', // 通过状态
      auditOpinion: joinForm.value.auditOpinion || '审核通过',
      applicationType: 'personal', // 明确标识为个人项目
      joinProjectList: joinForm.value.joinOption === 'approve', // 是否加入项目列表
      // 个人项目可以创建新项目记录
      isUpdateOnly: false,
      skipProjectCreation: false
    };

    console.log('=== 个人项目加入审核数据分析 ===');
    console.log('项目ID:', joinForm.value.projectId);
    console.log('项目名称:', currentProject.projectName);
    console.log('申请类型:', currentProject.applicationType);
    console.log('是否加入项目列表:', auditData.joinProjectList);
    console.log('完整审核数据:', auditData);

    await auditProject(auditData);

    const joinText = joinForm.value.joinOption === 'approve' ? '并已加入项目列表' : '但未加入项目列表';
    proxy?.$modal.msgSuccess(`个人项目审核通过${joinText}，记录已存入审核记录`);

    projectJoinDialog.visible = false;

    // 重新加载列表，已审核的记录将不再显示在待审核列表中
    await getList();

    // 提示用户可以在审核记录中查看
    setTimeout(() => {
      proxy?.$modal.msgInfo('已审核的记录可在"审核记录"页面查看');
    }, 1500);

  } catch (error) {
    console.error('操作失败:', error);
    proxy?.$modal.msgError('操作失败，请重试');
  }
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
    // 直接使用审核列表中的项目数据，这些数据已经包含了完整信息
    const projectData = row;

    // 添加申请类型特有字段的显示
    let specificFields = '';
    if (projectData.applicationType === 'personal') {
      // 个人项目的特有字段
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
      // 社区项目的特有字段
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

    // 使用孵化申请样式的项目详情
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
           <strong style="color: #333;">开源协议：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.license || '未选择'}</p>
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
             <a href="${projectData.websiteUrl || '#'}" target="_blank" style="color: #409EFF;">${projectData.websiteUrl || '暂无网站地址'}</a>
           </p>
         </div>

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">联系邮箱：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.contactEmail || '未提供'}</p>
         </div>

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">联系电话：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.contactPhone || '未提供'}</p>
         </div>

         ${specificFields}

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">申请状态：</strong>
           <p style="margin: 5px 0; color: #666;">${getApplicationStatusLabel(projectData.applicationStatus) || '暂无状态信息'}</p>
         </div>

         <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
           <strong style="color: #333;">备注信息：</strong>
           <p style="margin: 5px 0; color: #666;">${projectData.remarks || '暂无备注'}</p>
         </div>
       </div>
     `,
      '孵化申请详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        customClass: 'project-detail-dialog'
      }
    );
  } catch (error) {
    console.error('获取孵化申请详情失败:', error);
    proxy?.$modal.msgError('获取申请详情失败');
  }
};

/** 跳转到审核记录页面 */
const goToAuditRecords = () => {
  router.push('/osc/auditRecords');
};

/** 多选框选中数据 */
const handleSelectionChange = () => {
  multipleSelection.value = projectAuditList.value.filter(item => item.selected);
};

/** 删除单个项目 */
const handleDeleteSingle = async (row: ProjectVO) => {
  try {
    await proxy?.$modal.confirm('是否确认删除该孵化申请记录？');
    await delProject(row.projectId!);
    proxy?.$modal.msgSuccess('删除成功');
    await getList();
  } catch (error) {
    console.error('删除失败:', error);
  }
};

/** 批量删除项目 */
const handleBatchDelete = async () => {
  try {
    if (multipleSelection.value.length === 0) {
      proxy?.$modal.msgWarning('请选择要删除的记录');
      return;
    }

    await proxy?.$modal.confirm(`是否确认删除选中的 ${multipleSelection.value.length} 条记录？`);

    const ids = multipleSelection.value.map(item => item.projectId!);
    await delProject(ids);

    proxy?.$modal.msgSuccess('批量删除成功');
    multipleSelection.value = [];
    // 清除选中状态
    projectAuditList.value.forEach(item => item.selected = false);
    await getList();
  } catch (error) {
    console.error('批量删除失败:', error);
    proxy?.$modal.msgError('批量删除失败');
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

/* 审核详情对话框样式 */
.audit-detail-content {
  max-height: 500px;
  overflow-y: auto;
}

.info-section {
  margin-bottom: 24px;
}

.info-section h4 {
  margin: 0 0 12px 0;
  color: #409eff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-item strong {
  color: #606266;
  font-size: 14px;
}

.info-item span {
  color: #303133;
}

.description-text {
  color: #606266;
  line-height: 1.6;
  margin: 0;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
}

/* 卡片选择框样式 */
.card-checkbox {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 10;
}

.project-card {
  position: relative;
}

.audit-actions {
  display: flex;
  gap: 8px;
}

.delete-action {
  display: flex;
  align-items: center;
}

.audit-detail-content {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 10px;
}

.info-section {
  margin-bottom: 20px;
}

.info-section h4 {
  margin-bottom: 12px;
  font-weight: bold;
  color: #333;
}

.description-text {
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>
