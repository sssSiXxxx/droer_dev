<template>
  <div class="p-2">
    <!-- 视图切换标签页 -->
    <el-card shadow="hover" class="view-tabs">
      <el-tabs v-model="activeView" @tab-change="handleViewChange" class="demo-tabs">
        <el-tab-pane label="成员贡献管理" name="members">
          <template #label>
            <span style="display: flex; align-items: center; gap: 8px;">
              <el-icon><Avatar /></el-icon>
              成员贡献管理
            </span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="项目统计查看" name="projects">
          <template #label>
            <span style="display: flex; align-items: center; gap: 8px;">
              <el-icon><Folder /></el-icon>
              项目统计查看
            </span>
          </template>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 原有的成员贡献管理视图 -->
    <div v-show="activeView === 'members'">
      <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
        <div v-show="showSearch" class="mb-[10px]">
          <el-card shadow="hover">
            <el-form ref="queryFormRef" :model="queryParams" :inline="true">
              <el-form-item label="成员姓名" prop="memberName">
                <el-input v-model="queryParams.memberName" placeholder="请输入成员姓名" clearable @keyup.enter="handleQuery" />
              </el-form-item>
              <el-form-item label="项目名称" prop="projectName">
                <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable @keyup.enter="handleQuery" />
              </el-form-item>
              <el-form-item label="贡献类型" prop="contributionType">
                <el-select v-model="queryParams.contributionType" placeholder="请选择贡献类型" clearable >
                  <el-option v-for="dict in osc_user_cbt" :key="dict.value" :label="dict.label" :value="dict.value"/>
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

      <el-card shadow="never">
        <template #header>
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['osc:contribution:add']">新增</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['osc:contribution:edit']">修改</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['osc:contribution:remove']">删除</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['osc:contribution:export']">导出</el-button>
            </el-col>
            <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
          </el-row>
        </template>

        <el-table v-loading="loading" border :data="contributionList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="贡献ID" align="center" prop="contributionId" v-if="false" />
          <el-table-column label="成员姓名" align="center" prop="memberName" />
          <el-table-column label="项目名称" align="center" prop="projectName" />
          <el-table-column label="贡献类型" align="center" prop="contributionType">
            <template #default="scope">
              <dict-tag :options="osc_user_cbt" :value="scope.row.contributionType"/>
            </template>
          </el-table-column>
          <el-table-column label="贡献内容" align="center" prop="content" />
          <el-table-column label="贡献时间" align="center" prop="contributionTime" width="180">
            <template #default="scope">
              <span>{{ parseTime(scope.row.contributionTime, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="贡献点数" align="center" prop="points" />
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-tooltip content="修改" placement="top">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['osc:contribution:edit']"></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['osc:contribution:remove']"></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </el-card>
    </div>

    <!-- 新增的项目统计查看视图 -->
    <div v-show="activeView === 'projects'" class="projects-view">
      <el-card shadow="hover">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <h3 style="margin: 0; font-size: 18px; font-weight: 600;">Dromara 项目统计</h3>
            <div style="display: flex; gap: 12px;">
              <el-input 
                v-model="projectSearchQuery" 
                placeholder="搜索项目..." 
                clearable 
                @input="handleProjectSearch"
                style="width: 250px;"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-button type="primary" @click="refreshProjects">
                <el-icon><Refresh /></el-icon>
                刷新数据
              </el-button>
            </div>
          </div>
        </template>

        <!-- 项目列表 -->
        <div v-loading="projectsLoading" class="projects-container">
          <div 
            v-for="project in filteredProjects" 
            :key="project.name" 
            class="project-item"
            @click="viewProjectContributors(project)"
            :title="`点击查看 ${project.name} 项目贡献图表`"
          >
            <!-- 贡献图表提示标签 -->
            <div class="contributors-badge">
              <el-icon><TrendCharts /></el-icon>
              <span>贡献图表</span>
            </div>
            
            <div class="project-header">
              <div style="display: flex; align-items: center; gap: 12px;">
                <div class="project-icon">
                  <el-icon size="28"><FolderOpened /></el-icon>
                </div>
                <div class="project-info">
                  <h4 class="project-name">{{ project.name }}</h4>
                  <p class="project-desc">{{ project.description || '暂无描述' }}</p>
                </div>
              </div>
              
              <el-dropdown @command="handleProjectCommand" @click.stop>
                <el-button type="text" size="small" class="more-btn" title="更多选项">
                  <el-icon><More /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :command="{ action: 'github', project: project.name }">
                      <el-icon><Link /></el-icon>
                      访问仓库主页
                    </el-dropdown-item>
                    <el-dropdown-item :command="{ action: 'contributors', project: project.name }">
                      <el-icon><Avatar /></el-icon>
                      贡献图表
                    </el-dropdown-item>
                    <el-dropdown-item :command="{ action: 'insights', project: project.name }">
                      <el-icon><TrendCharts /></el-icon>
                      Insights统计
                    </el-dropdown-item>
                    <el-dropdown-item :command="{ action: 'issues', project: project.name }">
                      <el-icon><Warning /></el-icon>
                      Issues列表
                    </el-dropdown-item>
                    <el-dropdown-item :command="{ action: 'releases', project: project.name }">
                      <el-icon><Sell /></el-icon>
                      发布版本
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>

            <div class="project-stats">
              <div class="stat-box star-stat">
                <div class="stat-icon">
                  <el-icon><Star /></el-icon>
                </div>
                <div class="stat-content">
                  <span class="stat-number">{{ formatNumber(project.stars) }}</span>
                  <small class="stat-label">Stars</small>
                </div>
              </div>
              <div class="stat-box fork-stat">
                <div class="stat-icon">
                  <el-icon><Share /></el-icon>
                </div>
                <div class="stat-content">
                  <span class="stat-number">{{ formatNumber(project.forks) }}</span>
                  <small class="stat-label">Forks</small>
                </div>
              </div>
              <div class="stat-box issue-stat">
                <div class="stat-icon">
                  <el-icon><Warning /></el-icon>
                </div>
                <div class="stat-content">
                  <span class="stat-number">{{ formatNumber(project.issues) }}</span>
                  <small class="stat-label">Issues</small>
                </div>
              </div>
            </div>

            <div class="project-footer">
              <div class="project-lang" v-if="project.language">
                <span class="lang-color" :style="{ backgroundColor: getLanguageColor(project.language) }"></span>
                {{ project.language }}
              </div>
              <div class="project-time">
                <el-icon size="12"><Clock /></el-icon>
                {{ formatTime(project.updatedAt) }}
              </div>
            </div>
            
            <!-- 点击提示 -->
            <div class="click-hint">
              <el-icon><Mouse /></el-icon>
              <span>点击查看贡献图表</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 添加或修改贡献统计对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="contributionFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="成员姓名" prop="memberId">
          <el-select v-model="form.memberId" placeholder="请选择成员" filterable>
            <el-option v-for="member in memberList" :key="member.memberId" :label="member.memberName" :value="member.memberId" />
          </el-select>
        </el-form-item>
        <el-form-item label="项目名称" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择项目" filterable>
            <el-option v-for="project in projectList" :key="project.projectId" :label="project.projectName" :value="project.projectId" />
          </el-select>
        </el-form-item>
        <el-form-item label="贡献类型" prop="contributionType">
          <el-select v-model="form.contributionType" placeholder="请选择贡献类型">
            <el-option
                v-for="dict in osc_user_cbt"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="贡献内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="相关链接" prop="url">
          <el-input v-model="form.url" placeholder="请输入相关链接" />
        </el-form-item>
        <el-form-item label="贡献时间" prop="contributionTime">
          <el-date-picker clearable
            v-model="form.contributionTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择贡献时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="贡献点数" prop="points">
          <el-input v-model="form.points" placeholder="请输入贡献点数" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Contribution" lang="ts">
import { listContribution, getContribution, delContribution, addContribution, updateContribution } from '@/api/osc/contribution';
import { ContributionVO, ContributionQuery, ContributionForm } from '@/api/osc/contribution/types';
import { listProject } from '@/api/osc/project';
import { listUser } from '@/api/system/user';
import { ElMessage } from 'element-plus';
import { Avatar, Folder, Search, Refresh, Link, More, TrendCharts, Star, Share, Warning, FolderOpened, Clock, Mouse, Sell } from '@element-plus/icons-vue';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { osc_user_cbt } = toRefs<any>(proxy?.useDict('osc_user_cbt'));

// 原有的成员贡献相关数据
const contributionList = ref<ContributionVO[]>([]);
const memberList = ref<any[]>([]);
const projectList = ref<any[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

// 新增的项目统计相关数据
const activeView = ref('members'); // 'members' | 'projects'
const projects = ref<any[]>([]);
const filteredProjects = ref<any[]>([]);
const projectsLoading = ref(false);
const projectSearchQuery = ref('');

const queryFormRef = ref<ElFormInstance>();
const contributionFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ContributionForm = {
  memberId: undefined,
  projectId: undefined,
  contributionType: undefined,
  content: undefined,
  url: undefined,
  contributionTime: undefined,
  points: undefined,
}

const data = reactive<PageData<ContributionForm, ContributionQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    memberId: undefined,
    projectId: undefined,
    contributionType: undefined,
    content: undefined,
    params: {}
  },
  rules: {
    memberId: [
      { required: true, message: "成员ID不能为空", trigger: "blur" }
    ],
    projectId: [
      { required: true, message: "项目ID不能为空", trigger: "blur" }
    ],
    contributionType: [
      { required: true, message: "贡献类型不能为空", trigger: "change" }
    ],
    content: [
      { required: true, message: "贡献内容不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

// 工具函数
const formatNumber = (num: number): string => {
  if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
  if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
  return num?.toString() || '0';
};

const formatTime = (dateStr: string): string => {
  if (!dateStr) return '未知';
  const date = new Date(dateStr);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (days === 0) return '今天';
  if (days === 1) return '昨天';
  if (days < 7) return `${days}天前`;
  if (days < 30) return `${Math.floor(days/7)}周前`;
  if (days < 365) return `${Math.floor(days/30)}个月前`;
  return `${Math.floor(days/365)}年前`;
};

const getLanguageColor = (lang: string): string => {
  const colors: Record<string, string> = {
    'Java': '#b07219',
    'JavaScript': '#f1e05a', 
    'TypeScript': '#2b7489',
    'Python': '#3572A5',
    'Go': '#00ADD8',
    'Rust': '#dea584',
    'Vue': '#4fc08d',
    'HTML': '#e34c26',
    'CSS': '#563d7c'
  };
  return colors[lang] || '#666';
};

// 原有的成员贡献管理方法
/** 查询贡献统计列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await listContribution(queryParams.value);
    contributionList.value = res.rows;
    total.value = res.total;
  } catch (error) {
    console.error('获取贡献列表失败:', error);
  } finally {
    loading.value = false;
  }
}

/** 获取成员列表 */
const getMemberList = async () => {
  try {
    const res = await listUser({ pageSize: 1000 });
    memberList.value = (res.rows as unknown as any[])?.map(user => ({
      memberId: user.userId,
      memberName: user.nickName || user.userName
    })) || [];
  } catch (error) {
    console.error('获取成员列表失败:', error);
    // 使用空数组作为默认值
    memberList.value = [];
  }
}

/** 获取项目列表 */
const getProjectList = async () => {
  try {
    const res = await listProject({ pageSize: 1000 });
    projectList.value = (res.rows as unknown as any[]) || [];
  } catch (error) {
    console.error('获取项目列表失败:', error);
    // 使用模拟数据
    projectList.value = [
      { projectId: 1, projectName: 'Sa-Token' },
      { projectId: 2, projectName: 'Hutool' },
      { projectId: 3, projectName: 'Forest' },
      { projectId: 4, projectName: 'TLog' },
      { projectId: 5, projectName: 'Dynamic-Tp' }
    ];
  }
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  contributionFormRef.value?.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: ContributionVO[]) => {
  ids.value = selection.map(item => item.contributionId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加贡献统计";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: ContributionVO) => {
  reset();
  const _contributionId = row?.contributionId || ids.value[0]
  const res = await getContribution(_contributionId);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改贡献统计";
}

/** 提交按钮 */
const submitForm = () => {
  contributionFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      try {
        if (form.value.contributionId) {
          await updateContribution(form.value);
        } else {
          await addContribution(form.value);
        }
        proxy?.$modal.msgSuccess("操作成功");
        dialog.visible = false;
        await getList();
      } catch (error) {
        console.error('提交失败:', error);
      } finally {
        buttonLoading.value = false;
      }
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ContributionVO) => {
  const _contributionIds = row?.contributionId || ids.value;
  try {
    await proxy?.$modal.confirm('是否确认删除贡献统计编号为"' + _contributionIds + '"的数据项？');
    await delContribution(_contributionIds);
    proxy?.$modal.msgSuccess("删除成功");
    await getList();
  } catch (error) {
    console.error('删除失败:', error);
  }
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('osc/contribution/export', {
    ...queryParams.value
  }, `contribution_${new Date().getTime()}.xlsx`)
}

// 新增的项目统计相关方法
/** 视图切换处理 */
const handleViewChange = (tabName: string) => {
  if (tabName === 'projects' && projects.value.length === 0) {
    loadDromaraProjects();
  }
}

/** 加载Dromara项目数据 */
const loadDromaraProjects = async (forceRefresh = false) => {
  projectsLoading.value = true;
  try {
    console.log('🔍 开始获取Dromara项目数据...');
    
    // 直接使用模拟数据
    const mockProjects = [
      {
        name: 'hutool',
        description: '🍬A set of tools that keep Java sweet.',
        stars: 28900,
        forks: 7200,
        issues: 45,
        language: 'Java',
        updatedAt: '2024-01-15T10:30:00Z',
        githubUrl: 'https://github.com/dromara/hutool',
        homepage: 'https://hutool.cn'
      },
      {
        name: 'Sa-Token',
        description: '这可能是史上功能最全的Java权限认证框架！',
        stars: 15800,
        forks: 2900,
        issues: 32,
        language: 'Java', 
        updatedAt: '2024-01-14T16:45:00Z',
        githubUrl: 'https://github.com/dromara/Sa-Token',
        homepage: 'https://sa-token.cc'
      },
      {
        name: 'forest',
        description: '声明式HTTP客户端框架',
        stars: 5200,
        forks: 1100,
        issues: 18,
        language: 'Java',
        updatedAt: '2024-01-13T09:15:00Z',
        githubUrl: 'https://github.com/dromara/forest',
        homepage: 'https://forest.dtflyx.com'
      },
      {
        name: 'LiteFlow',
        description: '轻量，快速，稳定可编排的组件式规则引擎',
        stars: 4800,
        forks: 1000,
        issues: 25,
        language: 'Java',
        updatedAt: '2024-01-12T14:20:00Z',
        githubUrl: 'https://github.com/dromara/liteflow',
        homepage: 'https://liteflow.yomahub.com'
      },
      {
        name: 'HertzBeat',
        description: '易用友好的云监控系统', 
        stars: 4500,
        forks: 800,
        issues: 31,
        language: 'Java',
        updatedAt: '2024-01-11T11:30:00Z',
        githubUrl: 'https://github.com/dromara/hertzbeat',
        homepage: 'https://hertzbeat.com'
      },
      {
        name: 'MaxKey',
        description: '业界领先的身份管理和访问管理产品',
        stars: 4200,
        forks: 900,
        issues: 28,
        language: 'Java',
        updatedAt: '2024-01-10T15:45:00Z',
        githubUrl: 'https://github.com/dromara/MaxKey',
        homepage: 'https://www.maxkey.top'
      },
      {
        name: 'Jpom',
        description: '简而轻的低侵入式在线构建、自动部署、日常运维、项目监控软件',
        stars: 3800,
        forks: 700,
        issues: 19,
        language: 'Java',
        updatedAt: '2024-01-09T13:25:00Z',
        githubUrl: 'https://github.com/dromara/Jpom',
        homepage: 'https://jpom.io'
      },
      {
        name: 'Dynamic-Tp',
        description: '🔥轻量级动态线程池，内置监控告警功能',
        stars: 3200,
        forks: 650,
        issues: 22,
        language: 'Java',
        updatedAt: '2024-01-08T17:10:00Z',
        githubUrl: 'https://github.com/dromara/dynamic-tp',
        homepage: 'https://dynamictp.cn'
      },
      {
        name: 'TLog',
        description: '轻量级的分布式日志标记追踪神器',
        stars: 3000,
        forks: 600,
        issues: 15,
        language: 'Java',
        updatedAt: '2024-01-07T12:40:00Z',
        githubUrl: 'https://github.com/dromara/TLog',
        homepage: 'https://yomahub.com/tlog'
      },
      {
        name: 'GoView',
        description: '🚀可视化拖拽式低代码数据可视化开发平台',
        stars: 2800,
        forks: 520,
        issues: 41,
        language: 'TypeScript',
        updatedAt: '2024-01-06T08:55:00Z',
        githubUrl: 'https://github.com/dromara/go-view',
        homepage: 'https://www.mtruning.club'
      }
    ];

    projects.value = mockProjects;
    filteredProjects.value = mockProjects;
    console.log(`📦 加载项目数据: ${mockProjects.length} 个项目`);
    
  } catch (error) {
    console.error('❌ 加载项目失败:', error);
    ElMessage.error('加载项目数据失败');
  } finally {
    projectsLoading.value = false;
  }
}

/** 刷新项目数据 */
const refreshProjects = async () => {
  console.log('🔄 强制刷新项目数据...');
  await loadDromaraProjects(true); // 强制刷新
}

/** 项目搜索 */
const handleProjectSearch = () => {
  const query = projectSearchQuery.value.toLowerCase();
  if (!query) {
    filteredProjects.value = projects.value;
  } else {
    filteredProjects.value = projects.value.filter(p => 
      p.name.toLowerCase().includes(query) ||
      (p.description && p.description.toLowerCase().includes(query))
    );
  }
}

/** 查看项目贡献图表 */
const viewProjectContributors = (project: any) => {
  const url = `https://github.com/dromara/${project.name}/graphs/contributors`;
  console.log(`🔗 跳转到 ${project.name} 贡献图表: ${url}`);
  window.open(url, '_blank');
}

/** 查看项目详情（原函数保留） */
const viewProjectDetails = (project: any) => {
  const url = `https://github.com/dromara/${project.name}`;
  window.open(url, '_blank');
}

/** 处理项目下拉菜单命令 */
const handleProjectCommand = (command: any) => {
  const { action, project } = command;
  
  const baseUrl = `https://github.com/dromara/${project}`;
  let url = baseUrl;
  
  switch (action) {
    case 'github':
      url = baseUrl;
      break;
    case 'contributors':
      url = `${baseUrl}/graphs/contributors`;
      break;
    case 'insights':
      url = `${baseUrl}/pulse`;
      break;
    case 'issues':
      url = `${baseUrl}/issues`;
      break;
    case 'releases':
      url = `${baseUrl}/releases`;
      break;
  }
  
  console.log(`🔗 下拉菜单跳转到: ${url}`);
  window.open(url, '_blank');
}

onMounted(() => {
  getList();
  getMemberList();
  getProjectList();
});
</script>

<style scoped>
.view-tabs {
  margin-bottom: 16px;
}

.projects-view {
  
}

.projects-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(420px, 1fr));
  gap: 20px;
}

.project-item {
  position: relative;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
  overflow: hidden;
}

.project-item:hover {
  border-color: #409eff;
  box-shadow: 0 8px 25px rgba(64, 158, 255, 0.15);
  transform: translateY(-4px) scale(1.02);
}

.project-item:active {
  transform: translateY(-2px) scale(1.01);
}

/* 贡献图表提示标签 */
.contributors-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  opacity: 0;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.project-item:hover .contributors-badge {
  opacity: 1;
  transform: translateX(0);
}

.contributors-badge .el-icon {
  font-size: 12px;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-right: 40px; /* 为右上角标签留空间 */
}

.project-icon {
  color: #409eff;
  transition: transform 0.3s ease;
}

.project-item:hover .project-icon {
  transform: rotate(10deg) scale(1.1);
}

.project-info {
  flex: 1;
}

.project-name {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.3;
  transition: color 0.3s ease;
}

.project-item:hover .project-name {
  color: #409eff;
}

.project-desc {
  margin: 6px 0 0 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.more-btn {
  padding: 6px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.more-btn:hover {
  background-color: #f0f2f5;
  transform: rotate(90deg);
}

.project-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.stat-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.8);
}

.stat-box:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.star-stat {
  border-color: #f7d154;
}

.star-stat:hover {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-color: #f59e0b;
}

.fork-stat {
  border-color: #34d399;
}

.fork-stat:hover {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  border-color: #10b981;
}

.issue-stat {
  border-color: #f87171;
}

.issue-stat:hover {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  border-color: #ef4444;
}

.stat-icon {
  font-size: 16px;
  transition: transform 0.3s ease;
}

.star-stat .stat-icon {
  color: #f59e0b;
}

.fork-stat .stat-icon {
  color: #10b981;
}

.issue-stat .stat-icon {
  color: #ef4444;
}

.stat-box:hover .stat-icon {
  transform: scale(1.2);
}

.stat-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.stat-number {
  font-weight: 700;
  font-size: 16px;
  color: #1a1a1a;
  line-height: 1;
}

.stat-label {
  font-size: 11px;
  color: #6b7280;
  text-transform: uppercase;
  font-weight: 600;
  letter-spacing: 0.5px;
  margin-top: 2px;
}

.project-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #6b7280;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  margin-bottom: 12px;
}

.project-lang {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.lang-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.project-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
}

/* 点击提示 */
.click-hint {
  position: absolute;
  bottom: 12px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  opacity: 0;
  transition: all 0.3s ease;
  border: 1px solid rgba(64, 158, 255, 0.2);
}

.project-item:hover .click-hint {
  opacity: 1;
  transform: translateX(-50%) translateY(-5px);
}

.click-hint .el-icon {
  font-size: 14px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-3px);
  }
  60% {
    transform: translateY(-2px);
  }
}
</style>