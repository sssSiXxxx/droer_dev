<template>
  <div class="project-create">
    <!-- 顶部导航栏 -->
    <div class="page-header">
      <div class="header-title">
        <el-icon class="mr-2"><Star /></el-icon>
        <span>项目孵化申请</span>
      </div>
      <div class="header-actions">
        <!-- 申请记录按钮 -->
        <el-button type="success" @click="goToApplicationRecords">
          <el-icon><Document /></el-icon>
          申请记录
        </el-button>
        <el-button type="primary" @click="goDraftBox">
          <el-icon><Document /></el-icon>
          草稿箱
        </el-button>
      </div>
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="project-form" :hide-required-asterisk="false">
        <!-- 申请类型选择卡片 -->
        <el-card class="form-card mb-4">
          <template #header>
            <div class="card-header">
              <el-icon><Setting /></el-icon>
              <span>申请类型</span>
            </div>
          </template>

          <el-form-item label="申请类型" prop="applicationType" class="mb-4">
            <el-radio-group v-model="form.applicationType" @change="onApplicationTypeChange">
              <el-radio value="personal">个人项目加入社区</el-radio>
              <el-radio value="community">社区项目升级为顶级项目</el-radio>
            </el-radio-group>
          </el-form-item>

          <div v-if="form.applicationType" class="type-description">
            <el-alert
              :title="getTypeDescription(form.applicationType)"
              type="info"
              :closable="false"
              show-icon
            />
          </div>
        </el-card>
        <!-- 基本信息卡片 -->
        <el-card class="form-card mb-4">
          <template #header>
            <div class="card-header">
              <el-icon><InfoFilled /></el-icon>
              <span>基本信息</span>
            </div>
          </template>

          <el-form-item label="项目名称" prop="projectName" class="mb-4">
            <el-input v-model="form.projectName" placeholder="请输入项目名称" :maxlength="50" show-word-limit />
          </el-form-item>

          <el-form-item label="项目描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="4"
              placeholder="请详细描述项目的功能和特点，让评审委员会更好地了解您的项目"
              :maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="代码仓库" prop="repositoryUrl">
                <el-input v-model="form.repositoryUrl" placeholder="https://github.com/yourname/project">
                  <template #append>
                    <el-button @click="() => openUrl(form.repositoryUrl)" :disabled="!form.repositoryUrl">
                      <el-icon><Link /></el-icon>
                    </el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="项目网站" prop="websiteUrl">
                <el-input v-model="form.websiteUrl" placeholder="https://yourproject.com（可选）">
                  <template #append>
                    <el-button @click="() => openUrl(form.websiteUrl)" :disabled="!form.websiteUrl">
                      <el-icon><Link /></el-icon>
                    </el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20" class="mt-4">
            <el-col :span="12">
              <el-form-item label="开源协议" prop="license">
                <el-select v-model="form.license" placeholder="请选择开源协议" class="w-full">
                  <el-option label="Apache 2.0" value="Apache-2.0" />
                  <el-option label="MIT" value="MIT" />
                  <el-option label="GPL v3" value="GPL-3.0" />
                  <el-option label="BSD 3-Clause" value="BSD-3-Clause" />
                  <el-option label="Mozilla Public License 2.0" value="MPL-2.0" />
                  <el-option label="其他" value="OTHER" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 个人项目特有字段 -->
        <el-card v-if="form.applicationType === 'personal'" class="form-card mb-4">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span>个人项目信息</span>
            </div>
          </template>

          <el-row :gutter="20" style="margin-bottom: 12px;">
            <el-col :span="24">
              <el-form-item label="申请理由" prop="applicationReason">
                <el-input v-model="form.applicationReason" type="textarea" :rows="2"
                          placeholder="请说明为什么希望将此项目加入到开源社区" :maxlength="500" show-word-limit />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="预期贡献" prop="contribution">
                <el-input v-model="form.contribution" type="textarea" :rows="2"
                          placeholder="请描述您计划对社区做出的贡献" :maxlength="300" show-word-limit />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="项目现状" prop="currentStatus">
                <el-input v-model="form.currentStatus" type="textarea" :rows="2"
                          placeholder="请描述项目的当前状态、功能完成度等" :maxlength="300" show-word-limit />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 社区项目特有字段 -->
        <el-card v-if="form.applicationType === 'community'" class="form-card mb-4">
          <template #header>
            <div class="card-header">
              <el-icon><Star /></el-icon>
              <span>社区项目信息</span>
            </div>
          </template>

          <!-- 社区项目选择 -->
          <el-form-item label="选择社区项目" prop="selectedCommunityProject">
            <el-select
              v-model="selectedCommunityProject"
              placeholder="请选择要申请升级的社区项目"
              @change="handleCommunityProjectSelect"
              filterable
              class="w-full"
            >
              <el-option
                v-for="project in communityProjects"
                :key="project.projectId"
                :label="project.projectName"
                :value="project.projectId"
              >
                <div class="project-option">
                  <span class="project-name">{{ project.projectName }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>

          <el-row :gutter="20" style="margin-bottom: 16px;">
            <el-col :span="12">
              <el-form-item label="升级理由" prop="upgradeReason">
                <el-input v-model="form.upgradeReason" type="textarea" :rows="3"
                          placeholder="请说明项目为什么达到了升级为顶级项目的标准" :maxlength="500" show-word-limit />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="社区影响" prop="communityImpact">
                <el-input v-model="form.communityImpact" type="textarea" :rows="3"
                          placeholder="请描述项目对社区的积极影响和贡献" :maxlength="500" show-word-limit />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="6">
              <el-form-item label="Star数量" prop="starCount">
                <el-input-number v-model="form.starCount" :min="0" placeholder="0" class="w-full" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="Fork数量" prop="forkCount">
                <el-input-number v-model="form.forkCount" :min="0" placeholder="0" class="w-full" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="Issues数量" prop="issuesCount">
                <el-input-number v-model="form.issuesCount" :min="0" placeholder="0" class="w-full" />
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="PR数量" prop="prCount">
                <el-input-number v-model="form.prCount" :min="0" placeholder="0" class="w-full" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 联系信息卡片 -->
        <el-card class="form-card mb-4">
          <template #header>
            <div class="card-header">
              <el-icon><Phone /></el-icon>
              <span>联系信息</span>
            </div>
          </template>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系邮箱" prop="contactEmail">
                <el-input v-model="form.contactEmail" placeholder="example@email.com" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 其他信息卡片 -->
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <el-icon><More /></el-icon>
              <span>备注信息</span>
            </div>
          </template>

          <el-form-item label="备注" prop="remarks">
            <el-input v-model="form.remarks" type="textarea" placeholder="其他需要说明的信息" :rows="3" :maxlength="200" show-word-limit />
          </el-form-item>
        </el-card>

        <!-- 底部操作栏 -->
        <div class="form-actions">
          <div class="action-buttons">
            <el-button type="primary" size="large" @click="submitForm(true)">
              <el-icon><Check /></el-icon>
              提交审核
            </el-button>
            <el-button type="info" size="large" @click="submitForm(false)">
              <el-icon><DocumentAdd /></el-icon>
              保存草稿
            </el-button>
            <el-button size="large" @click="cancel">取消</el-button>
          </div>
          <div v-if="lastSaveTime" class="last-save-info">
            <el-icon><Timer /></el-icon>
            上次保存时间: {{ lastSaveTime }}
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup name="IncubationApplication" lang="ts">
import { getCurrentInstance, ref, onMounted, onBeforeUnmount, watch } from 'vue';
import { Star, Document, InfoFilled, Setting, User, Phone, More, Check, DocumentAdd, Timer, Link } from '@element-plus/icons-vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/store/modules/user';
import { getProject, addProject, updateProject, listProject } from '@/api/osc/project';
import type { FormInstance as ElFormInstance } from 'element-plus';
import { ProjectVO } from '@/api/osc/project/types';

const { proxy } = getCurrentInstance() as any;
const userStore = useUserStore();
const route = useRoute();
const router = useRouter();

// 自动保存相关
const autoSaveTimer = ref<ReturnType<typeof setInterval>>();
const isAutoSaving = ref(false);
const lastSaveTime = ref<string>('');
const isSubmitting = ref(false);

const formRef = ref<ElFormInstance>();

// 社区项目相关
const selectedCommunityProject = ref<number>();
const communityProjects = ref<ProjectVO[]>([]);

// 孵化申请表单数据
interface IncubationForm {
  applicationId?: number;
  applicationType?: 'personal' | 'community';
  projectName?: string;
  description?: string;
  repositoryUrl?: string;
  websiteUrl?: string;
  license?: string;
  // 个人项目字段
  applicationReason?: string;
  contribution?: string;
  currentStatus?: string;
  // 社区项目字段
  upgradeReason?: string;
  communityImpact?: string;
  starCount?: number;
  forkCount?: number;
  issuesCount?: number;
  prCount?: number;
  // 联系信息
  contactEmail?: string;
  contactPhone?: string;
  remarks?: string;
  applicationStatus?: 'draft' | 'pending' | 'approved' | 'rejected';
}

const form = ref<IncubationForm>({
  applicationType: undefined,
  projectName: undefined,
  description: undefined,
  repositoryUrl: undefined,
  websiteUrl: undefined,
  license: undefined,
  applicationReason: undefined,
  contribution: undefined,
  currentStatus: undefined,
  upgradeReason: undefined,
  communityImpact: undefined,
  starCount: undefined,
  forkCount: undefined,
  issuesCount: undefined,
  prCount: undefined,
  contactEmail: undefined,
  contactPhone: undefined,
  remarks: undefined,
  applicationStatus: 'draft'
});

// 提交审核时的验证规则
const submitRules = {
  applicationType: [{ required: true, message: '请选择申请类型', trigger: 'change' }],
  projectName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
  projectCode: [{ required: true, message: '项目代码不能为空', trigger: 'blur' }],
  description: [{ required: true, message: '项目描述不能为空', trigger: 'blur' }],
  repositoryUrl: [
    { required: true, message: '代码仓库不能为空', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
  ],
  websiteUrl: [{ type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }],
  license: [{ required: true, message: '请选择开源协议', trigger: 'change' }],
  contactEmail: [
    { required: true, message: '联系邮箱不能为空', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  contactPhone: [{ required: true, message: '联系电话不能为空', trigger: 'blur' }],
  // 个人项目必填字段
  applicationReason: [{ required: true, message: '申请理由不能为空', trigger: 'blur' }],
  contribution: [{ required: true, message: '预期贡献不能为空', trigger: 'blur' }],
  currentStatus: [{ required: true, message: '项目现状不能为空', trigger: 'blur' }],
  // 社区项目必填字段
  upgradeReason: [{ required: true, message: '升级理由不能为空', trigger: 'blur' }],
  communityImpact: [{ required: true, message: '社区影响不能为空', trigger: 'blur' }]
};

// 保存草稿时的验证规则（只验证URL格式和邮箱格式）
const draftRules = {
  repositoryUrl: [{ type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }],
  websiteUrl: [{ type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }],
  contactEmail: [{ type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }]
};

// 根据操作类型动态设置验证规则
const rules = ref<any>(submitRules);

// 申请类型描述
const getTypeDescription = (type: 'personal' | 'community') => {
  if (type === 'personal') {
    return '将您的个人开源项目申请加入到开源社区，获得社区的支持和资源';
  } else {
    return '将已在社区孵化的项目申请升级为顶级项目，获得更高的项目地位和影响力';
  }
};

// 根据申请类型动态获取提交验证规则
const getSubmitRules = () => {
  const baseRules = {
    applicationType: [{ required: true, message: '请选择申请类型', trigger: 'change' }],
    projectName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
    description: [{ required: true, message: '项目描述不能为空', trigger: 'blur' }],
    repositoryUrl: [
      { required: true, message: '代码仓库不能为空', trigger: 'blur' },
      { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
    ],
    websiteUrl: [{ type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }],
    license: [{ required: true, message: '请选择开源协议', trigger: 'change' }],
    contactEmail: [
      { required: true, message: '联系邮箱不能为空', trigger: 'blur' },
      { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
    ],
    contactPhone: [{ required: true, message: '联系电话不能为空', trigger: 'blur' }]
  };

  // 根据申请类型添加特定字段的验证
  if (form.value.applicationType === 'personal') {
    return {
      ...baseRules,
      applicationReason: [{ required: true, message: '申请理由不能为空', trigger: 'blur' }],
      contribution: [{ required: true, message: '预期贡献不能为空', trigger: 'blur' }],
      currentStatus: [{ required: true, message: '项目现状不能为空', trigger: 'blur' }]
    };
  } else if (form.value.applicationType === 'community') {
    return {
      ...baseRules,
      upgradeReason: [{ required: true, message: '升级理由不能为空', trigger: 'blur' }],
      communityImpact: [{ required: true, message: '社区影响不能为空', trigger: 'blur' }]
    };
  }

  return baseRules;
};

// 申请类型改变时的处理
const onApplicationTypeChange = (type: 'personal' | 'community') => {
  // 清空类型特有的字段
  if (type === 'personal') {
    form.value.upgradeReason = undefined;
    form.value.communityImpact = undefined;
    form.value.starCount = undefined;
    form.value.forkCount = undefined;
    form.value.issuesCount = undefined;
    form.value.prCount = undefined;
  } else {
    form.value.applicationReason = undefined;
    form.value.contribution = undefined;
    form.value.currentStatus = undefined;
  }
};

/** 自动保存功能 */
const autoSave = async () => {
  if (isAutoSaving.value || isSubmitting.value) return;

  const userId = userStore.userId;
  if (!userId) return;

  // 检查是否有必要的数据进行保存
  if (!form.value.projectName && !form.value.description && !form.value.applicationType) {
    return;
  }

  isAutoSaving.value = true;
  try {
    // 自动保存固定为草稿状态
    const submitData: any = {
      ...form.value,
      applicationStatus: 'draft', // 自动保存只能是草稿状态
      userId: userId,
      status: '1' // 添加默认项目状态为活跃状态
    };

    console.log('自动保存数据:', {
      projectName: submitData.projectName,
      applicationStatus: submitData.applicationStatus,
      userId: submitData.userId
    });

    const applicationId = route.query.applicationId;
    if (applicationId && form.value.applicationId) {
      // 编辑模式：更新现有申请
      submitData.projectId = applicationId;
      await updateProject(submitData);
      console.log('自动保存更新成功，ID:', applicationId);
    } else {
      // 新建模式：创建新申请
      const result = await addProject(submitData);
      // 如果是首次保存，更新URL以包含applicationId
      if (result && result.data) {
        const newApplicationId = result.data;
        console.log('自动保存新建成功，获得ID:', newApplicationId);
        router.replace({
          path: '/osc/projectCreate',
          query: { applicationId: newApplicationId }
        });
        form.value.applicationId = newApplicationId;
      }
    }

    lastSaveTime.value = new Date().toLocaleTimeString();
    console.log('自动保存成功，时间:', lastSaveTime.value);
  } catch (error) {
    console.error('自动保存失败:', error);
  } finally {
    isAutoSaving.value = false;
  }
};

/** 启动自动保存 */
const startAutoSave = () => {
  if (autoSaveTimer.value) {
    clearInterval(autoSaveTimer.value);
  }
  // 每3分钟自动保存一次
  autoSaveTimer.value = setInterval(autoSave, 3 * 60 * 1000);
};

/** 停止自动保存 */
const stopAutoSave = () => {
  if (autoSaveTimer.value) {
    clearInterval(autoSaveTimer.value);
    autoSaveTimer.value = undefined;
  }
};

/** 提交按钮 */
const submitForm = async (isSubmitAudit: boolean) => {
  // 防止重复提交
  if (isSubmitting.value) {
    return;
  }

  // 获取当前用户ID
  const userId = userStore.userId;
  if (!userId) {
    proxy?.$modal.msgError('获取用户信息失败');
    return;
  }

  // 如果提交审核，先停止自动保存
  if (isSubmitAudit) {
    stopAutoSave();
  }

  // 根据操作类型设置验证规则
  rules.value = isSubmitAudit ? getSubmitRules() : draftRules;

  isSubmitting.value = true;
  try {
    // 进行表单验证
    await formRef.value?.validate();

    // 明确设置applicationStatus状态
    const applicationStatus = isSubmitAudit ? 'pending' : 'draft';
    const submitData: any = {
      ...form.value,
      applicationStatus: applicationStatus,
      userId: userId,
      status: '1' // 添加默认项目状态为活跃状态
    };

    console.log('提交孵化申请数据:', submitData);
    console.log('是否提交审核:', isSubmitAudit);
    console.log('申请状态:', applicationStatus);
    console.log('目标状态应该是:', isSubmitAudit ? 'pending(审核列表)' : 'draft(草稿箱)');

    let result;
    const applicationId = route.query.applicationId;

    if (applicationId && form.value.applicationId) {
      // 编辑模式：更新现有申请
      submitData.projectId = applicationId;
      console.log('编辑模式，更新申请ID:', applicationId);
      result = await updateProject(submitData);
    } else {
      // 新建模式：创建新申请
      console.log('新建模式，创建新孵化申请');
      result = await addProject(submitData);
      // 如果是新建，保存返回的ID
      if (result && result.data) {
        form.value.applicationId = result.data;
      }
    }

    if (isSubmitAudit) {
      // 提交审核成功
      proxy?.$modal.msgSuccess('孵化申请提交成功，等待审核');
      // 跳转到申请记录页面，用户可以查看申请状态
      router.push('/osc/applicationRecords');
      //随后清空表单数据
      form.value = {
        applicationType: undefined,
        projectName: undefined,
        description: undefined,
        repositoryUrl: undefined,
        websiteUrl: undefined,
        license: undefined,
        applicationReason: undefined,
        contribution: undefined,
        currentStatus: undefined,
        upgradeReason: undefined,
        communityImpact: undefined,
        starCount: undefined,
        forkCount: undefined,
        issuesCount: undefined,
        prCount: undefined,
        contactEmail: undefined,
        contactPhone: undefined,
        remarks: undefined,
        applicationStatus: 'draft'
      }
    } else {
      proxy?.$modal.msgSuccess('草稿保存成功');
      console.log('草稿保存成功，即将跳转到草稿箱');
      // 跳转到草稿箱，确保带上用户ID参数
      router.push({
        path: '/osc/projectDraft',
        query: { createBy: userId }
      });
    }
  } catch (error) {
    if (error instanceof Error) {
      console.error(isSubmitAudit ? '提交失败:' : '保存草稿失败:', error);
      proxy?.$modal.msgError(isSubmitAudit ? '提交失败' : '保存草稿失败');
    }
  } finally {
    // 恢复为默认的验证规则
    rules.value = getSubmitRules();
    // 重置提交状态
    isSubmitting.value = false;
  }
};

/** 取消按钮 */
const cancel = () => {
  // 清空表单数据
  form.value = {
    applicationType: undefined,
    projectName: undefined,
    description: undefined,
    repositoryUrl: undefined,
    websiteUrl: undefined,
    license: undefined,
    applicationReason: undefined,
    contribution: undefined,
    currentStatus: undefined,
    upgradeReason: undefined,
    communityImpact: undefined,
    starCount: undefined,
    forkCount: undefined,
    issuesCount: undefined,
    prCount: undefined,
    contactEmail: undefined,
    contactPhone: undefined,
    remarks: undefined,
    applicationStatus: 'draft'
  };

  // 清除表单验证状态
  formRef.value?.clearValidate();

  // 停止自动保存
  stopAutoSave();

  // 跳转回草稿箱或申请记录页面
  const userId = userStore.userId;
  if (userId) {
    router.push({
      path: '/osc/projectDraft',
      query: { createBy: userId }
    });
  } else {
    router.push('/osc/applicationRecords');
  }
};
/** 跳转到申请记录 */
const goToApplicationRecords = () => {
  router.push('/osc/applicationRecords');
};
/** 跳转到草稿箱 */
const goDraftBox = () => {
  const userId = userStore.userId;
  if (!userId) {
    proxy?.$modal.msgError('获取用户信息失败');
    return;
  }
  router.push({
    path: '/osc/projectDraft',
    query: { createBy: userId }
  });
};

/** 打开URL */
const openUrl = (url?: string) => {
  if (url) {
    window.open(url, '_blank');
  }
};

/** 获取社区项目列表 */
const getCommunityProjects = async () => {
  try {
    const res = await listProject({
      status: '2', // 只获取已通过审核的项目
      applicationType: 'personal' // 获取个人项目作为社区项目候选
    });
    communityProjects.value = res.rows || [];
  } catch (error) {
    console.error('获取社区项目失败:', error);
  }
};

/** 处理社区项目选择 */
const handleCommunityProjectSelect = async (projectId: number) => {
  if (!projectId) return;

  try {
    const res = await getProject(projectId);
    if (res.data) {
      // 自动填充基本信息
      form.value.projectName = res.data.projectName;
      form.value.description = res.data.description;
      form.value.repositoryUrl = res.data.repositoryUrl;
      form.value.websiteUrl = res.data.websiteUrl;
      form.value.license = res.data.license;
      form.value.starCount = res.data.starCount;
      form.value.forkCount = res.data.forkCount;
      form.value.issuesCount = res.data.issuesCount;
      form.value.prCount = res.data.prCount;

      proxy?.$modal.msgSuccess('已自动填充项目信息');
    }
  } catch (error) {
    console.error('获取项目信息失败:', error);
    proxy?.$modal.msgError('获取项目信息失败');
  }
};

/** 获取孵化申请信息 */
const getInfo = async (applicationId: string) => {
  try {
    const res = await getProject(applicationId);
    if (res.data) {
      // 填充表单数据
      form.value = {
        applicationId: res.data.projectId,
        applicationType: res.data.applicationType,
        projectName: res.data.projectName,
        description: res.data.description,
        repositoryUrl: res.data.repositoryUrl,
        websiteUrl: res.data.websiteUrl,
        license: res.data.license,
        applicationReason: res.data.applicationReason,
        contribution: res.data.contribution,
        currentStatus: res.data.currentStatus,
        upgradeReason: res.data.upgradeReason,
        communityImpact: res.data.communityImpact,
        starCount: res.data.starCount,
        forkCount: res.data.forkCount,
        issuesCount: res.data.issuesCount,
        prCount: res.data.prCount,
        contactEmail: res.data.contactEmail,
        contactPhone: res.data.contactPhone,
        remarks: res.data.remarks,
        applicationStatus: res.data.applicationStatus || 'draft'
      };
    }
  } catch (error) {
    console.error('获取孵化申请信息失败:', error);
    proxy?.$modal.msgError('获取申请信息失败');
  }
};

onMounted(async () => {
  const applicationId = route.query.applicationId;
  if (applicationId) {
    await getInfo(applicationId as string);
  }

  // 获取社区项目列表
  await getCommunityProjects();

  // 启动自动保存
  startAutoSave();
});

// 组件卸载前停止自动保存
onBeforeUnmount(() => {
  stopAutoSave();
});
</script>

<style scoped>
.project-create {
  padding: 20px;
  background-color: var(--el-bg-color);
  min-height: calc(100vh - 84px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 0 16px;
}

.header-title {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: 600;
  color: var(--el-text-color-primary);

  .el-icon {
    margin-right: 8px;
    font-size: 24px;
  }
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
}

.form-card {
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

  :deep(.el-card__header) {
    padding: 16px 20px;
    border-bottom: 1px solid var(--el-border-color-light);
  }
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);

  .el-icon {
    margin-right: 8px;
    font-size: 18px;
    color: var(--el-color-primary);
  }
}

.type-description {
  margin-top: 16px;
}

:deep(.el-form-item) {
  margin-bottom: 24px;

  &:last-child {
    margin-bottom: 0;
  }
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--el-text-color-regular);
}

:deep(.el-input-number) {
  width: 100%;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 32px;
  padding: 24px;
  background-color: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: 0 -2px 12px 0 rgba(0, 0, 0, 0.05);

  .action-buttons {
    display: flex;
    gap: 12px;

    .el-button {
      padding: 12px 24px;

      .el-icon {
        margin-right: 4px;
      }
    }
  }

  .last-save-info {
    display: flex;
    align-items: center;
    color: var(--el-text-color-secondary);
    font-size: 14px;

    .el-icon {
      margin-right: 4px;
      font-size: 16px;
    }
  }
}

.mb-4 {
  margin-bottom: 16px;
}

.mt-4 {
  margin-top: 16px;
}

.mr-2 {
  margin-right: 8px;
}

.w-full {
  width: 100%;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-card__body) {
  padding: 24px;
}

:deep(.el-radio-group) {
  display: flex;
  flex-direction: row;
  gap: 24px;

  .el-radio {
    margin-right: 24px;

    &:last-child {
      margin-right: 0;
    }
  }
}
</style>
