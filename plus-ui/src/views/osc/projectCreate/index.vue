<template>
  <div class="p-2">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">创建项目</span>
          <div class="header-buttons">
            <el-button type="info" plain @click="goMyProjects">
              <el-icon><List /></el-icon>
              我的创建
            </el-button>
            <el-button type="primary" plain @click="goDraftBox">
              <el-icon><Document /></el-icon>
              草稿箱
            </el-button>
          </div>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="project-form"
      >
        <!-- 项目基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="form.projectName" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
        </el-row>

          <el-form-item label="项目描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="3"
              placeholder="请输入项目描述"
            />
          </el-form-item>

        <!-- 技术信息 -->
        <el-divider content-position="left">技术信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="技术栈" prop="techStack">
              <el-select
                v-model="form.techStack"
                multiple
                filterable
                placeholder="请选择技术栈"
                style="width: 100%"
                :filter-method="filterTechStack"
                remote
                reserve-keyword
              >
                <el-input
                  v-model="techStackSearchKeyword"
                  placeholder="搜索技术栈"
                  class="search-input"
                  @input="filterTechStack(techStackSearchKeyword)"
                  slot="prefix"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
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
                placeholder="请选择编程语言"
                style="width: 100%"
                :filter-method="filterProgrammingLanguage"
                remote
                reserve-keyword
              >
                <el-input
                  v-model="programmingLanguageSearchKeyword"
                  placeholder="搜索编程语言"
                  class="search-input"
                  @input="filterProgrammingLanguage(programmingLanguageSearchKeyword)"
                  slot="prefix"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
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

        <!-- 仓库信息 -->
        <el-divider content-position="left">仓库信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="代码仓库" prop="repositoryUrl">
              <el-input v-model="form.repositoryUrl" placeholder="请输入代码仓库地址">
                <template #append>
                  <el-button @click="() => openUrl(form.repositoryUrl)">访问</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目网站" prop="websiteUrl">
              <el-input v-model="form.websiteUrl" placeholder="请输入项目网站地址">
                <template #append>
                  <el-button @click="() => openUrl(form.websiteUrl)">访问</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 其他信息 -->
        <el-divider content-position="left">其他信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系方式" prop="contactInfo">
              <el-input v-model="form.contactInfo" placeholder="请输入联系方式" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版本信息" prop="versionInfo">
              <el-input v-model="form.versionInfo" placeholder="请输入版本信息" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="项目Logo" prop="logoUrl">
          <image-upload v-model="form.logoUrl" :limit="1" />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm(true)">提交审核</el-button>
          <el-button type="info" @click="submitForm(false)">保存草稿</el-button>
          <el-button @click="cancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup name="ProjectCreate" lang="ts">
import { ProjectForm } from '@/api/osc/project/types';
import { addProject, getProject, updateProject } from '@/api/osc/project';
import { getCurrentInstance, ref, onMounted, toRefs } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/store/modules/user';
import type { FormInstance as ElFormInstance } from 'element-plus';

const { proxy } = getCurrentInstance() as any;

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

const rules = ref<any>({
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
});

/** 提交按钮 */
const submitForm = (isSubmitAudit: boolean) => {
  // 获取当前用户ID
  const userId = proxy?.useUserStore().userId;
  if (!userId) {
    proxy?.$modal.msgError('获取用户信息失败');
    return;
  }

  // 如果是保存草稿，不进行表单验证
  if (!isSubmitAudit) {
    // 转换多选值为字符串
    const submitData: any = {
      ...form.value,
      techStack: form.value.techStack?.join(','),
      programmingLanguage: form.value.programmingLanguage?.join(','),
      status: '0', // 0: 草稿
      createBy: userId // 设置创建者为当前用户
    };
    
    try {
      addProject(submitData).then(() => {
        proxy?.$modal.msgSuccess('草稿保存成功');
        // 跳转到草稿箱（只显示当前用户的草稿）
        proxy?.$router.push({
          path: '/osc/project',
          query: {
            status: '0',
            createBy: userId
          }
        });
      }).catch(error => {
        console.error('保存草稿失败:', error);
        proxy?.$modal.msgError('保存草稿失败');
      });
    } catch (error) {
      console.error('保存草稿失败:', error);
      proxy?.$modal.msgError('保存草稿失败');
    }
    return;
  }

  // 如果是提交审核，进行表单验证
  formRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      // 转换多选值为字符串
      const submitData: any = {
        ...form.value,
        techStack: form.value.techStack?.join(','),
        programmingLanguage: form.value.programmingLanguage?.join(','),
        status: '1', // 1: 待审核
        createBy: userId // 设置创建者为当前用户
      };
      
      try {
        await addProject(submitData);
        proxy?.$modal.msgSuccess('提交成功，等待审核');
        // 跳转到待审核列表
        proxy?.$router.push({
          path: '/osc/project',
          query: {
            status: '1',  // 1: 待审核
            createBy: userId
          }
        });
      } catch (error) {
        console.error('提交失败:', error);
        proxy?.$modal.msgError('提交失败');
      }
    }
  });
};

/** 取消按钮 */
const cancel = () => {
  proxy?.$router.push('/osc/project');
};

/** 跳转到草稿箱 */
const goDraftBox = () => {
  proxy?.$router.push({
    path: '/osc/project',
    query: {
      status: '0',
      createBy: proxy?.useUserStore().userId
    }
  });
};

/** 跳转到我的创建 */
const goMyProjects = () => {
  const userId = proxy?.useUserStore().userId;
  if (!userId) {
    proxy?.$modal.msgError('获取用户信息失败');
    return;
  }

  proxy?.$confirm(
    '请选择要查看的项目状态',
    '我的创建',
    {
      confirmButtonText: '进行中/已完成',
      cancelButtonText: '已驳回',
      distinguishCancelAndClose: true,
      showClose: true,
      closeOnClickModal: false,
      type: 'info',
      center: true
    }
  ).then(() => {
    // 用户点击"进行中/已完成"，查看状态为2和3的项目
    const query = {
      createBy: userId,
      params: JSON.stringify({
        statusList: ['2', '3']
      })
    };
    proxy?.$router.push({
      path: '/osc/project',
      query
    });
  }).catch((action) => {
    if (action === 'cancel') {
      // 用户点击"已驳回"
      proxy?.$router.push({
        path: '/osc/project',
        query: {
          createBy: userId,
          status: '5'
        }
      });
    }
  });
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
});
</script>

<style scoped>
.project-form {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.el-divider {
  margin: 24px 0;
}

:deep(.el-divider__text) {
  font-size: 16px;
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-buttons {
    display: flex;
    gap: 10px;
    
    .el-button {
      margin-left: 0;
    }
  }
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input-group__append) {
  padding: 0;
  .el-button {
    margin: 0;
    border: none;
    height: 100%;
    border-radius: 0;
  }
}
</style>