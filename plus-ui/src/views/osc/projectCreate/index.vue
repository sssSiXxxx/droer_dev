<template>
  <div class="project-create">
    <!-- 顶部导航栏 -->
    <div class="page-header">
      <div class="header-title">
        <el-icon class="mr-2"><Plus /></el-icon>
        <span>创建项目</span>
      </div>
      <div class="header-actions">
        <el-button-group>
          <el-button type="success" @click="showTemplateDialog">
            <el-icon><Files /></el-icon>
            使用模板
          </el-button>
          <el-button type="info" @click="goMyProjects">
            <el-icon><List /></el-icon>
            我的创建
          </el-button>
          <el-button type="primary" @click="goDraftBox">
            <el-icon><Document /></el-icon>
            草稿箱
          </el-button>
        </el-button-group>
      </div>
      
      <!-- 模板选择对话框 -->
      <template-select
        v-model="templateDialogVisible"
        @select="handleTemplateSelect"
      />
    </div>

    <!-- 主要内容区 -->
    <div class="main-content">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="project-form"
        :hide-required-asterisk="false"
      >
        <!-- 基本信息卡片 -->
        <el-card class="form-card mb-4">
          <template #header>
            <div class="card-header">
              <el-icon><InfoFilled /></el-icon>
              <span>基本信息</span>
            </div>
          </template>
          
          <el-form-item label="项目名称" prop="projectName" class="mb-4">
            <el-input 
              v-model="form.projectName" 
              placeholder="请输入项目名称"
              :maxlength="50"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="项目描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="4"
              placeholder="请简要描述项目的主要功能和特点"
              :maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-card>

        <!-- 技术信息卡片 -->
        <el-card class="form-card mb-4">
          <template #header>
            <div class="card-header">
              <el-icon><Monitor /></el-icon>
              <span>技术信息</span>
            </div>
          </template>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="技术栈" prop="techStack">
                <el-select
                  v-model="form.techStack"
                  multiple
                  filterable
                  collapse-tags
                  collapse-tags-tooltip
                  placeholder="请选择技术栈"
                  class="w-full"
                  :filter-method="filterTechStack"
                >
                  <el-option
                    v-for="dict in filteredTechStack"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="编程语言" prop="programmingLanguage">
                <el-select
                  v-model="form.programmingLanguage"
                  multiple
                  filterable
                  collapse-tags
                  collapse-tags-tooltip
                  placeholder="请选择编程语言"
                  class="w-full"
                  :filter-method="filterProgrammingLanguage"
                >
                  <el-option
                    v-for="dict in filteredProgrammingLanguage"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 仓库信息卡片 -->
        <el-card class="form-card mb-4">
          <template #header>
            <div class="card-header">
              <el-icon><Link /></el-icon>
              <span>仓库信息</span>
            </div>
          </template>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="代码仓库" prop="repositoryUrl">
                <el-input 
                  v-model="form.repositoryUrl" 
                  placeholder="请输入代码仓库地址"
                  class="url-input"
                >
                  <template #append>
                    <el-button @click="() => openUrl(form.repositoryUrl)">
                      <el-icon><Link /></el-icon>
                    </el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="项目网站" prop="websiteUrl">
                <el-input 
                  v-model="form.websiteUrl" 
                  placeholder="请输入项目网站地址"
                  class="url-input"
                >
                  <template #append>
                    <el-button @click="() => openUrl(form.websiteUrl)">
                      <el-icon><Link /></el-icon>
                    </el-button>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>

        <!-- 其他信息卡片 -->
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <el-icon><More /></el-icon>
              <span>其他信息</span>
            </div>
          </template>

          <el-row :gutter="20" class="mb-4">
            <el-col :span="12">
              <el-form-item label="联系方式" prop="contactInfo">
                <el-input 
                  v-model="form.contactInfo" 
                  placeholder="请输入联系方式"
                  :maxlength="100"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="版本信息" prop="versionInfo">
                <el-input 
                  v-model="form.versionInfo" 
                  placeholder="请输入版本信息"
                  :maxlength="50"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="项目Logo" prop="logoUrl">
            <image-upload 
              v-model="form.logoUrl" 
              :limit="1"
              class="logo-uploader"
              tip="建议上传正方形图片，大小不超过2MB"
            />
          </el-form-item>

          <el-form-item label="备注" prop="remark">
            <el-input 
              v-model="form.remark" 
              type="textarea" 
              placeholder="请输入备注信息"
              :rows="3"
              :maxlength="200"
              show-word-limit
            />
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

<script setup name="ProjectCreate" lang="ts">
import { ProjectForm } from '@/api/osc/project/types';
import { addProject, getProject, updateProject } from '@/api/osc/project';
import { getCurrentInstance, ref, onMounted, onBeforeUnmount, toRefs } from 'vue';
import { Plus, List, Document, InfoFilled, Monitor, Link, More, Check, DocumentAdd, Timer, Files } from '@element-plus/icons-vue';
import TemplateSelect from './components/TemplateSelect.vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/store/modules/user';
import type { FormInstance as ElFormInstance } from 'element-plus';

const { proxy } = getCurrentInstance() as any;

// 模板相关
const templateDialogVisible = ref(false);
const showTemplateDialog = () => {
  templateDialogVisible.value = true;
};

const handleTemplateSelect = (template: any) => {
  // 使用模板配置填充表单
  form.value = {
    ...form.value,
    description: template.config.description,
    techStack: template.config.techStack,
    programmingLanguage: template.config.programmingLanguage
  };
  proxy?.$modal.msgSuccess(`已成功应用"${template.name}"模板`);
  
  // 更新过滤后的数组，确保选项正确显示
  filteredTechStack.value = osc_project_tech.value;
  filteredProgrammingLanguage.value = prolanDict.value;
};

// 自动保存相关
const autoSaveTimer = ref<ReturnType<typeof setInterval>>();
const isAutoSaving = ref(false);
const lastSaveTime = ref<string>('');

// 分别加载两个字典
const { osc_project_tech } = toRefs<any>(proxy?.useDict('osc_project_tech'));
const prolanDict = ref([]);
const filteredProgrammingLanguage = ref<any[]>([]);
const programmingLanguageSearchKeyword = ref('');
const filteredTechStack = ref<any[]>([]);
const techStackSearchKeyword = ref('');

// 过滤编程语言选项
const filterProgrammingLanguage = (query: string) => {
  if (query) {
    filteredProgrammingLanguage.value = prolanDict.value.filter(item => 
      item.label.toLowerCase().includes(query.toLowerCase())
    );
  } else {
    filteredProgrammingLanguage.value = prolanDict.value;
  }
};

// 过滤技术栈选项
const filterTechStack = (query: string) => {
  if (query) {
    filteredTechStack.value = osc_project_tech.value.filter(item => 
      item.label.toLowerCase().includes(query.toLowerCase())
    );
  } else {
    filteredTechStack.value = osc_project_tech.value;
  }
};

// 手动获取编程语言字典数据
const loadProlanDict = async () => {
  try {
    const res = await proxy?.$http.get('/system/dict/data/type/osc_project_prolan');
    console.log('编程语言字典数据响应:', res);
    if (res?.data && res.data.length > 0) {
      prolanDict.value = res.data;
    } else {
      // 备用的编程语言列表
      prolanDict.value = [
        { label: 'Java', value: '1' },
        { label: 'Python', value: '2' },
        { label: 'Go', value: '3' },
        { label: 'C', value: '4' },
        { label: 'C++', value: '5' },
        { label: 'JavaScript', value: '6' },
        { label: 'Vue', value: '7' },
        { label: 'PHP', value: '8' },
        { label: 'Swift', value: '9' },
        { label: 'Kotlin', value: '10' },
        { label: 'TypeScript', value: '11' },
        { label: 'Rust', value: '12' },
        { label: 'Scala', value: '13' },
        { label: 'Perl', value: '14' },
        { label: 'Lua', value: '15' },
        { label: 'R', value: '16' },
        { label: 'Shell', value: '17' },
        { label: 'MATLAB', value: '18' },
        { label: 'HTML', value: '19' }
      ];
      console.log('使用备用编程语言列表');
    }
  } catch (error) {
    console.error('获取编程语言字典数据失败:', error);
    // 出错时也使用备用列表
    prolanDict.value = [
      { label: 'Java', value: 'Java' },
      { label: 'JavaScript', value: 'JavaScript' },
      { label: 'TypeScript', value: 'TypeScript' },
      { label: 'Python', value: 'Python' },
      { label: 'Go', value: 'Go' },
      { label: 'C++', value: 'C++' },
      { label: 'C#', value: 'C#' },
      { label: 'PHP', value: 'PHP' },
      { label: 'Ruby', value: 'Ruby' },
      { label: 'Swift', value: 'Swift' },
      { label: 'Kotlin', value: 'Kotlin' },
      { label: 'Rust', value: 'Rust' },
      { label: 'Scala', value: 'Scala' },
      { label: 'Dart', value: 'Dart' },
      { label: 'HTML/CSS', value: 'HTML/CSS' },
      { label: 'Shell', value: 'Shell' },
      { label: 'SQL', value: 'SQL' },
      { label: 'R', value: 'R' },
      { label: 'Lua', value: 'Lua' },
      { label: 'Perl', value: 'Perl' },
      { label: 'Groovy', value: 'Groovy' },
      { label: 'Objective-C', value: 'Objective-C' },
      { label: 'Haskell', value: 'Haskell' },
      { label: 'Clojure', value: 'Clojure' },
      { label: 'Elixir', value: 'Elixir' }
    ];
    console.log('使用备用编程语言列表');
  }
};

const formRef = ref<ElFormInstance>();
const form = ref<ProjectForm>({
  projectName: undefined,
  description: undefined,
  repositoryUrl: undefined,
  websiteUrl: undefined,
  logoUrl: undefined,
  status: '0',
  techStack: [],
  programmingLanguage: [],
  contactInfo: undefined,
  versionInfo: undefined,
  remark: undefined
});

// 提交审核时的验证规则
const submitRules = {
  projectName: [
    { required: true, message: '项目名称不能为空', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '项目描述不能为空', trigger: 'blur' }
  ],
  repositoryUrl: [
    { required: true, message: '代码仓库不能为空', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
  ],
  websiteUrl: [
    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
  ]
};

// 保存草稿时的验证规则（只验证URL格式）
const draftRules = {
  repositoryUrl: [
    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
  ],
  websiteUrl: [
    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
  ]
};

// 根据操作类型动态设置验证规则
const rules = ref<any>(submitRules);

/** 自动保存功能 */
const autoSave = async () => {
  if (isAutoSaving.value) return;

  const userId = proxy?.useUserStore().userId;
  if (!userId) return;

  isAutoSaving.value = true;
  try {
    // 转换多选值为字符串
    const submitData: any = {
      ...form.value,
      techStack: form.value.techStack?.join(','),
      programmingLanguage: form.value.programmingLanguage?.join(','),
      status: '0', // 0: 草稿
      createBy: userId
    };

    await addProject(submitData);
    lastSaveTime.value = new Date().toLocaleTimeString();
    proxy?.$message({
      type: 'success',
      message: `草稿已自动保存 (${lastSaveTime.value})`,
      duration: 2000
    });
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
  // 获取当前用户ID
  const userId = proxy?.useUserStore().userId;
  if (!userId) {
    proxy?.$modal.msgError('获取用户信息失败');
    return;
  }

  // 根据操作类型设置验证规则
  rules.value = isSubmitAudit ? submitRules : draftRules;

  try {
    // 进行表单验证
    await formRef.value?.validate();

    // 转换多选值为字符串
    const submitData: any = {
      ...form.value,
      techStack: form.value.techStack?.join(','),
      programmingLanguage: form.value.programmingLanguage?.join(','),
      status: isSubmitAudit ? '1' : '0', // 1: 待审核, 0: 草稿
      createBy: userId
    };

    await addProject(submitData);

    if (isSubmitAudit) {
      proxy?.$modal.msgSuccess('提交成功，等待审核');
      // 跳转到待审核列表
      proxy?.$router.push({
        path: '/osc/project',
        query: {
          status: '1',
          createBy: userId
        }
      });
    } else {
      proxy?.$modal.msgSuccess('草稿保存成功');
      // 跳转到草稿箱
      proxy?.$router.push({
        path: '/osc/project',
        query: {
          status: '0',
          createBy: userId
        }
      });
    }
  } catch (error) {
    if (error instanceof Error) {
      console.error(isSubmitAudit ? '提交失败:' : '保存草稿失败:', error);
      proxy?.$modal.msgError(isSubmitAudit ? '提交失败' : '保存草稿失败');
    }
  } finally {
    // 恢复为默认的验证规则
    rules.value = submitRules;
  }
};

/** 取消按钮 */
const cancel = () => {
  proxy?.$router.push('/osc/project');
};

/** 跳转到草稿箱 */
const goDraftBox = () => {
  const userId = proxy?.useUserStore().userId;
  if (!userId) {
    proxy?.$modal.msgError('获取用户信息失败');
    return;
  }
  
  // 如果表单已修改，提示保存
  if (formRef.value?.isModified) {
    proxy?.$modal.confirm('表单已修改，是否保存草稿？').then(() => {
      submitForm(false).then(() => {
        proxy?.$router.push({
          path: '/osc/projectDraft',
          query: { createBy: userId }
        });
      });
    }).catch(() => {
      proxy?.$router.push({
        path: '/osc/projectDraft',
        query: { createBy: userId }
      });
    });
  } else {
    proxy?.$router.push({
      path: '/osc/projectDraft',
      query: { createBy: userId }
    });
  }
};

/** 跳转到我的创建 */
const goMyProjects = () => {
  const userId = proxy?.useUserStore().userId;
  if (!userId) {
    proxy?.$modal.msgError('获取用户信息失败');
    return;
  }
  
  // 如果表单已修改，提示保存
  if (formRef.value?.isModified) {
    proxy?.$modal.confirm('表单已修改，是否保存草稿？').then(() => {
      submitForm(false).then(() => {
        proxy?.$router.push({
          path: '/osc/myProject',
          query: { createBy: userId }
        });
      });
    }).catch(() => {
      proxy?.$router.push({
        path: '/osc/myProject',
        query: { createBy: userId }
      });
    });
  } else {
    proxy?.$router.push({
      path: '/osc/myProject',
      query: { createBy: userId }
    });
  }
};

/** 获取项目信息 */
const getInfo = async (projectId: string) => {
  try {
    const res = await getProject(projectId);
    form.value = {
      ...res.data,
      techStack: res.data.techStack?.split(',') || [],
      programmingLanguage: res.data.programmingLanguage?.split(',') || []
    };
  } catch (error) {
    console.error('获取项目信息失败:', error);
  }
};

/** 打开URL */
const openUrl = (url?: string) => {
  if (url) {
    window.open(url, '_blank');
  }
};

// 如果是编辑模式，获取项目信息
const route = useRoute();
onMounted(async () => {
  await loadProlanDict();
  // 初始化过滤后的数组
  filteredProgrammingLanguage.value = prolanDict.value;
  filteredTechStack.value = osc_project_tech.value;
  
  const projectId = route.query.projectId;
  if (projectId) {
    getInfo(projectId as string);
  }

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

.url-input {
  :deep(.el-input-group__append) {
    padding: 0;
    
    .el-button {
      margin: 0;
      border: none;
      height: 32px;
      width: 40px;
      border-radius: 0;
      padding: 8px;
    }
  }
}

.logo-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration);

    &:hover {
      border-color: var(--el-color-primary);
    }
  }
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
</style>