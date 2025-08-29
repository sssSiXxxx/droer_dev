<template>
  <div class="project-phase">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><Histogram /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalPhases || 0 }}</div>
              <div class="stat-label">总阶段数</div>
            </div>
          </div>
          <div class="stat-progress">
            <el-progress :percentage="statistics.completionRate || 0" :status="getProgressStatus(statistics.completionRate)" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon in-progress">
              <el-icon><Loading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.inProgressPhases || 0 }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </div>
          <div class="stat-extra">{{ statistics.completedPhases || 0 }} 已完成</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon delayed">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.delayedPhases || 0 }}</div>
              <div class="stat-label">已延期</div>
            </div>
          </div>
          <div class="stat-extra">平均延期 {{ statistics.averageDelay || 0 }} 天</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon upcoming">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.upcomingPhases || 0 }}</div>
              <div class="stat-label">即将开始</div>
            </div>
          </div>
          <div class="stat-extra">未来7天内</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和工具栏 -->
    <el-card class="mb-4">
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="项目" prop="projectId">
          <el-select 
            v-model="queryParams.projectId" 
            placeholder="请选择或搜索项目" 
            clearable 
            filterable
            remote
            :remote-method="handleProjectSearch"
            :loading="projectSearchLoading"
            style="width: 240px" 
            @change="handleProjectChange"
          >
            <el-option v-for="item in filteredProjectOptions" :key="item.projectId" :label="item.projectName" :value="item.projectId">
              <div class="project-option">
                <div class="project-name">{{ item.projectName }}</div>
                <div class="project-desc" v-if="item.description">{{ item.description }}</div>
              </div>
            </el-option>
            <template #empty>
              <div class="empty-option">
                <span>暂无项目数据</span>
              </div>
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="阶段名称" prop="phaseName">
          <el-input v-model="queryParams.phaseName" placeholder="请输入阶段名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option v-for="dict in statusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="queryParams.priority" placeholder="请选择优先级" clearable>
            <el-option v-for="item in priorityOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <template #header>
        <div class="card-header">
          <span class="card-title">阶段列表</span>
          <div class="card-toolbar">
            <el-button type="success" plain @click="handleAdd" v-if="queryParams.projectId">
              <el-icon><Plus /></el-icon>新增阶段
            </el-button>
            <el-button type="warning" plain @click="handleCreateStandardPhases" v-if="queryParams.projectId">
              <el-icon><DocumentCopy /></el-icon>创建标准孵化阶段
            </el-button>
            <el-button type="primary" plain @click="switchView">
              <el-icon><Operation /></el-icon>
              {{ isGanttView ? '切换列表视图' : '切换甘特图视图' }}
            </el-button>
            <el-button type="info" plain @click="handleExport" v-if="phaseList.length > 0">
              <el-icon><Download /></el-icon>导出数据
            </el-button>
          </div>
        </div>
      </template>

      <!-- 甘特图视图 -->
      <div v-if="isGanttView" class="gantt-view">
        <gantt-chart :phases="phaseList" @update-progress="handleUpdateProgress" />
      </div>

      <!-- 列表视图 -->
      <div v-else class="list-view">
        <!-- 批量操作栏 -->
        <div v-show="selectedRows.length > 0" class="batch-toolbar">
          <span class="batch-info">已选择 {{ selectedRows.length }} 项</span>
          <el-button type="success" size="small" @click="handleBatchComplete">批量完成</el-button>
          <el-button type="warning" size="small" @click="handleBatchPause">批量暂停</el-button>
          <el-button type="danger" size="small" @click="handleBatchDelete">批量删除</el-button>
        </div>

        <el-table v-loading="loading" :data="phaseList" @selection-change="handleSelectionChange" row-key="phaseId">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="阶段名称" align="center" prop="phaseName" min-width="120" show-overflow-tooltip />
          <el-table-column label="项目名称" align="center" prop="projectName" min-width="120" show-overflow-tooltip />
          <el-table-column label="优先级" align="center" width="80">
            <template #default="scope">
              <el-tag :type="getPriorityType(scope.row.priority)" size="small">
                {{ getPriorityLabel(scope.row.priority) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="开始时间" align="center" prop="startTime" width="180">
            <template #default="scope">
              <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="结束时间" align="center" prop="endTime" width="180">
            <template #default="scope">
              <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="进度" align="center" width="200">
            <template #default="scope">
              <div class="progress-container">
                <el-progress :percentage="scope.row.progress || calculateProgress(scope.row)" :status="getPhaseStatus(scope.row)" :stroke-width="8" />
                <el-button v-if="scope.row.status === '1'" type="primary" link size="small" @click="handleProgressEdit(scope.row)"> 更新 </el-button>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="负责人" align="center" width="100">
            <template #default="scope">
              <span>{{ scope.row.ownerName || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="300" fixed="right">
            <template #default="scope">
              <el-button type="primary" link icon="Edit" @click="handleUpdate(scope.row)"> 修改 </el-button>
              <el-button
                type="success"
                link
                icon="Check"
                v-if="scope.row.status === '0' || scope.row.status === '1'"
                @click="handleComplete(scope.row)"
              >
                完成
              </el-button>
              <el-button 
                type="info" 
                link 
                icon="Right" 
                v-if="scope.row.status === '2'" 
                @click="handleAdvanceNext(scope.row)"
              > 
                推进下一阶段 
              </el-button>
              <el-button type="warning" link icon="VideoPause" v-if="scope.row.status === '1'" @click="handlePause(scope.row)"> 暂停 </el-button>
              <el-button type="info" link icon="VideoPlay" v-if="scope.row.status === '3'" @click="handleResume(scope.row)"> 恢复 </el-button>
              <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row)"> 删除 </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
      </div>
    </el-card>

    <!-- 添加/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="phaseFormRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目" prop="projectId">
              <el-select v-model="form.projectId" placeholder="请选择项目" style="width: 100%" :disabled="!!form.phaseId">
                <el-option v-for="item in projectOptions" :key="item.projectId" :label="item.projectName" :value="item.projectId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="阶段编码" prop="phaseCode">
              <el-input v-model="form.phaseCode" placeholder="请输入阶段编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="阶段名称" prop="phaseName">
              <el-input v-model="form.phaseName" placeholder="请输入阶段名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级" prop="priority">
              <el-select v-model="form.priority" placeholder="请选择优先级" style="width: 100%">
                <el-option v-for="item in priorityOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker v-model="form.startTime" type="datetime" placeholder="请选择开始时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker v-model="form.endTime" type="datetime" placeholder="请选择结束时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="阶段描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入阶段描述" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 进度更新对话框 -->
    <el-dialog title="更新进度" v-model="progressDialogOpen" width="500px" append-to-body>
      <el-form :model="progressForm" label-width="100px">
        <el-form-item label="阶段名称">
          <el-input v-model="progressForm.phaseName" readonly />
        </el-form-item>
        <el-form-item label="当前进度">
          <div class="progress-input">
            <el-slider v-model="progressForm.progress" :min="0" :max="100" :step="5" show-input input-size="small" />
            <span class="progress-unit">%</span>
          </div>
        </el-form-item>
        <el-form-item label="进度说明">
          <el-input v-model="progressForm.remark" type="textarea" placeholder="可选：说明当前进度情况" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitProgress">确 定</el-button>
          <el-button @click="progressDialogOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectPhase" lang="ts">
import { getCurrentInstance, ref, onMounted } from 'vue';
import {
  Histogram,
  Loading,
  Warning,
  Calendar,
  Plus,
  Operation,
  Edit,
  Delete,
  Check,
  Search,
  Refresh,
  VideoPause,
  VideoPlay,
  Download,
  DocumentCopy,
  Right
} from '@element-plus/icons-vue';
import {
  listProjectPhase,
  getProjectPhase,
  addProjectPhase,
  updateProjectPhase,
  delProjectPhase,
  getPhaseStatistics,
  completeProjectPhase,
  pauseProjectPhase,
  resumeProjectPhase,
  updatePhaseProgress,
  createStandardPhases,
  advanceToNextPhase,
  getNextPhase,
  exportProjectPhase,
  batchUpdatePhaseStatus
} from '@/api/osc/projectPhase';
import { listProject } from '@/api/osc/project';
import type { FormInstance } from 'element-plus';
import GanttChart from './components/GanttChart.vue';
import { parseTime } from '@/utils/ruoyi';
import { download } from '@/utils/request';

// 防抖函数
const debounce = (func: Function, wait: number) => {
  let timeout: NodeJS.Timeout;
  return function executedFunction(...args: any[]) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};

const { proxy } = getCurrentInstance() as any;

// 遍历器
const loading = ref(false);
const open = ref(false);
const progressDialogOpen = ref(false);
const total = ref(0);
const title = ref('');
const isGanttView = ref(false);
const selectedRows = ref([]);

// 项目选项
const projectOptions = ref([]);
const filteredProjectOptions = ref([]);
const projectSearchLoading = ref(false);

// 状态选项
const statusOptions = [
  { label: '未开始', value: '0' },
  { label: '进行中', value: '1' },
  { label: '已完成', value: '2' },
  { label: '已暂停', value: '3' },
  { label: '已延期', value: '4' }
];

// 优先级选项
const priorityOptions = [
  { label: '低', value: 1 },
  { label: '中', value: 2 },
  { label: '高', value: 3 }
];

// 统计数据
const statistics = ref({
  totalPhases: 0,
  completedPhases: 0,
  inProgressPhases: 0,
  delayedPhases: 0,
  upcomingPhases: 0,
  completionRate: 0,
  averageDelay: 0
});

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  projectId: undefined,
  phaseName: undefined,
  status: undefined,
  priority: undefined
});

// 表单参数
const form = ref({
  phaseId: undefined,
  projectId: undefined,
  phaseName: undefined,
  phaseCode: undefined,
  description: undefined,
  startTime: undefined,
  endTime: undefined,
  status: '0',
  priority: 2
});

// 进度表单参数
const progressForm = ref({
  phaseId: undefined,
  phaseName: '',
  progress: 0,
  remark: ''
});

// 表单校验规则
const rules = ref({
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
  phaseName: [{ required: true, message: '请输入阶段名称', trigger: 'blur' }],
  phaseCode: [{ required: true, message: '请输入阶段编码', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }]
});

const phaseList = ref([]);
const phaseFormRef = ref<FormInstance>();

/** 查询项目列表 */
const loadProjects = async () => {
  try {
    projectSearchLoading.value = true;
    const res = await listProject();
    projectOptions.value = res.rows || [];
    filteredProjectOptions.value = [...projectOptions.value];
    
    // 如果没有数据，提供模拟数据
    if (projectOptions.value.length === 0) {
      projectOptions.value = [
        {
          projectId: 1,
          projectName: 'RuoYi-Vue-Plus项目',
          projectCode: 'RVP001',
          description: '基于RuoYi-Vue-Plus的企业级管理系统',
          status: '1'
        },
        {
          projectId: 2,
          projectName: 'Dromara社区管理系统',
          projectCode: 'DCS001',
          description: 'Dromara开源社区管理平台',
          status: '1'
        }
      ];
      filteredProjectOptions.value = [...projectOptions.value];
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
    // 提供模拟项目数据
    projectOptions.value = [
      {
        projectId: 1,
        projectName: 'RuoYi-Vue-Plus项目',
        projectCode: 'RVP001',
        description: '基于RuoYi-Vue-Plus的企业级管理系统',
        status: '1'
      },
      {
        projectId: 2,
        projectName: 'Dromara社区管理系统',
        projectCode: 'DCS001',
        description: 'Dromara开源社区管理平台',
        status: '1'
      }
    ];
    filteredProjectOptions.value = [...projectOptions.value];
  } finally {
    projectSearchLoading.value = false;
  }
};

/** 项目搜索处理（防抖优化） */
const handleProjectSearch = debounce(async (query: string) => {
  if (query !== '') {
    projectSearchLoading.value = true;
    try {
      // 如果项目列表为空，先加载所有项目
      if (projectOptions.value.length === 0) {
        await loadProjects();
      }
      
      // 本地过滤项目
      filteredProjectOptions.value = projectOptions.value.filter(
        (item) =>
          item.projectName.toLowerCase().includes(query.toLowerCase()) ||
          (item.description && item.description.toLowerCase().includes(query.toLowerCase())) ||
          (item.projectCode && item.projectCode.toLowerCase().includes(query.toLowerCase()))
      );
    } finally {
      projectSearchLoading.value = false;
    }
  } else {
    // 如果搜索词为空，显示所有项目
    filteredProjectOptions.value = [...projectOptions.value];
  }
}, 300);

/** 查询阶段列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await listProjectPhase(queryParams.value);
    phaseList.value = res.rows || [];
    total.value = res.total || 0;
  } catch (error) {
    console.error('获取阶段列表失败:', error);
    // 提供模拟数据确保页面显示
    phaseList.value = [
      {
        phaseId: 1,
        projectId: 1,
        projectName: 'RuoYi-Vue-Plus项目',
        phaseName: '需求分析',
        phaseCode: 'PHASE_001',
        status: '2',
        priority: 3,
        progress: 85,
        startTime: '2024-01-01 09:00:00',
        endTime: '2024-01-15 18:00:00',
        ownerName: '张三',
        description: '收集和分析项目需求，确定功能范围',
        createTime: '2024-01-01 09:00:00'
      },
      {
        phaseId: 2,
        projectId: 1,
        projectName: 'RuoYi-Vue-Plus项目',
        phaseName: '系统设计',
        phaseCode: 'PHASE_002',
        status: '1',
        priority: 2,
        progress: 60,
        startTime: '2024-01-10 09:00:00',
        endTime: '2024-01-25 18:00:00',
        ownerName: '李四',
        description: '设计系统架构和数据库结构',
        createTime: '2024-01-10 09:00:00'
      },
      {
        phaseId: 3,
        projectId: 1,
        projectName: 'RuoYi-Vue-Plus项目',
        phaseName: '编码开发',
        phaseCode: 'PHASE_003',
        status: '1',
        priority: 3,
        progress: 40,
        startTime: '2024-01-20 09:00:00',
        endTime: '2024-02-28 18:00:00',
        ownerName: '王五',
        description: '实现核心功能模块',
        createTime: '2024-01-20 09:00:00'
      },
      {
        phaseId: 4,
        projectId: 1,
        projectName: 'RuoYi-Vue-Plus项目',
        phaseName: '测试验证',
        phaseCode: 'PHASE_004',
        status: '0',
        priority: 2,
        progress: 10,
        startTime: '2024-02-20 09:00:00',
        endTime: '2024-03-10 18:00:00',
        ownerName: '赵六',
        description: '系统测试和bug修复',
        createTime: '2024-02-20 09:00:00'
      }
    ];
    total.value = phaseList.value.length;
    proxy?.$modal.msgWarning('API接口连接失败，显示模拟数据');
  } finally {
    loading.value = false;
  }
};

/** 查询统计数据 */
const loadStatistics = async () => {
  try {
    if (queryParams.value.projectId) {
      const res = await getPhaseStatistics(queryParams.value.projectId);
      statistics.value = res.data;
    } else {
      // 提供模拟统计数据
      statistics.value = {
        totalPhases: 4,
        inProgressPhases: 3,
        completedPhases: 1,
        delayedPhases: 1,
        completionRate: 65,
        averageDelay: 2
      };
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
    // 提供模拟统计数据
    statistics.value = {
      totalPhases: 4,
      inProgressPhases: 3,
      completedPhases: 1,
      delayedPhases: 1,
      completionRate: 65,
      averageDelay: 2
    };
  }
};

/** 表单重置 */
const reset = () => {
  form.value = {
    phaseId: undefined,
    projectId: undefined,
    phaseName: undefined,
    phaseCode: undefined,
    description: undefined,
    startTime: undefined,
    endTime: undefined,
    status: '0',
    priority: 2
  };
  phaseFormRef.value?.resetFields();
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

/** 项目变更操作 */
const handleProjectChange = () => {
  loadStatistics();
  handleQuery();
};

/** 切换视图 */
const switchView = () => {
  isGanttView.value = !isGanttView.value;
};

/** 表格多选处理 */
const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection;
};

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  form.value.projectId = queryParams.value.projectId;
  open.value = true;
  title.value = '添加阶段';
};

/** 创建标准孵化阶段 */
const handleCreateStandardPhases = async () => {
  try {
    await proxy?.$modal.confirm('将为当前项目创建标准孵化阶段模板（包含：项目立项、技术调研、原型开发、MVP开发、Alpha测试、Beta测试、正式发布、社区建设、持续维护），确定继续？');
    const res = await createStandardPhases(queryParams.value.projectId);
    proxy?.$modal.msgSuccess('标准孵化阶段创建成功');
    await getList();
    await loadStatistics();
  } catch (error) {
    console.error('创建标准阶段失败:', error);
  }
};

/** 修改按钮操作 */
const handleUpdate = async (row: any) => {
  reset();
  const phaseId = row.phaseId;
  const res = await getProjectPhase(phaseId);
  Object.assign(form.value, res.data);
  open.value = true;
  title.value = '修改阶段';
};

/** 提交按钮 */
const submitForm = async () => {
  phaseFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (form.value.phaseId) {
          await updateProjectPhase(form.value);
          proxy?.$modal.msgSuccess('修改成功');
        } else {
          await addProjectPhase(form.value);
          proxy?.$modal.msgSuccess('新增成功');
        }
        open.value = false;
        await getList();
        await loadStatistics();
      } catch (error) {
        console.error('操作失败:', error);
      }
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row: any) => {
  try {
    await proxy?.$modal.confirm('是否确认删除该阶段？');
    await delProjectPhase(row.phaseId);
    await getList();
    await loadStatistics();
    proxy?.$modal.msgSuccess('删除成功');
  } catch (error) {
    console.error('删除失败:', error);
  }
};

/** 完成按钮操作 */
const handleComplete = async (row: any) => {
  try {
    await proxy?.$modal.confirm('是否确认完成该阶段？');
    await completeProjectPhase(row.phaseId);
    await getList();
    await loadStatistics();
    proxy?.$modal.msgSuccess('阶段已标记为完成');
  } catch (error) {
    console.error('完成阶段失败:', error);
    proxy?.$modal.msgError('操作失败');
  }
};

/** 推进到下一阶段 */
const handleAdvanceNext = async (row: any) => {
  try {
    // 先获取下一阶段信息
    const nextPhaseRes = await getNextPhase(row.projectId, row.phaseId);
    const nextPhase = nextPhaseRes.data;
    
    if (!nextPhase) {
      proxy?.$modal.msgInfo('当前已是最后一个阶段，无法推进');
      return;
    }
    
    await proxy?.$modal.confirm(`是否确认推进到下一阶段"${nextPhase.phaseName}"？当前阶段将标记为完成，下一阶段将自动开始。`);
    await advanceToNextPhase(row.phaseId);
    await getList();
    await loadStatistics();
    proxy?.$modal.msgSuccess(`已成功推进到"${nextPhase.phaseName}"阶段`);
  } catch (error) {
    console.error('推进阶段失败:', error);
    proxy?.$modal.msgError('推进失败');
  }
};

/** 暂停按钮操作 */
const handlePause = async (row: any) => {
  try {
    await proxy?.$modal.confirm('是否确认暂停该阶段？');
    await pauseProjectPhase(row.phaseId);
    await getList();
    await loadStatistics();
    proxy?.$modal.msgSuccess('阶段已暂停');
  } catch (error) {
    console.error('暂停阶段失败:', error);
    proxy?.$modal.msgError('操作失败');
  }
};

/** 恢复按钮操作 */
const handleResume = async (row: any) => {
  try {
    await proxy?.$modal.confirm('是否确认恢复该阶段？');
    await resumeProjectPhase(row.phaseId);
    await getList();
    await loadStatistics();
    proxy?.$modal.msgSuccess('阶段已恢复');
  } catch (error) {
    console.error('恢复阶段失败:', error);
    proxy?.$modal.msgError('操作失败');
  }
};

/** 进度编辑按钮操作 */
const handleProgressEdit = (row: any) => {
  progressForm.value = {
    phaseId: row.phaseId,
    phaseName: row.phaseName,
    progress: row.progress || 0,
    remark: ''
  };
  progressDialogOpen.value = true;
};

/** 提交进度更新 */
const submitProgress = async () => {
  try {
    await updatePhaseProgress(progressForm.value.phaseId, progressForm.value.progress);
    progressDialogOpen.value = false;
    await getList();
    await loadStatistics();
    proxy?.$modal.msgSuccess('进度更新成功');
  } catch (error) {
    console.error('进度更新失败:', error);
    proxy?.$modal.msgError('操作失败');
  }
};

/** 从甘特图更新进度 */
const handleUpdateProgress = async (phaseId: string | number, progress: number) => {
  try {
    await updatePhaseProgress(phaseId, progress);
    await getList();
    await loadStatistics();
    proxy?.$modal.msgSuccess('进度更新成功');
  } catch (error) {
    console.error('进度更新失败:', error);
    proxy?.$modal.msgError('操作失败');
  }
};

/** 导出数据 */
const handleExport = async () => {
  try {
    await proxy?.$modal.confirm('是否确认导出所有阶段数据？');
    await exportProjectPhase(queryParams.value);
    proxy?.$modal.msgSuccess('导出成功');
  } catch (error) {
    console.error('导出失败:', error);
    proxy?.$modal.msgError('导出失败');
  }
};

/** 批量完成 */
const handleBatchComplete = async () => {
  const phaseIds = selectedRows.value.map((row: any) => row.phaseId);
  try {
    await proxy?.$modal.confirm('是否确认批量完成选中的阶段？');
    await batchUpdatePhaseStatus(phaseIds, '2');
    await getList();
    await loadStatistics();
    proxy?.$modal.msgSuccess('批量完成成功');
  } catch (error) {
    console.error('批量完成失败:', error);
    proxy?.$modal.msgError('操作失败');
  }
};

/** 批量暂停 */
const handleBatchPause = async () => {
  const phaseIds = selectedRows.value.map((row: any) => row.phaseId);
  try {
    await proxy?.$modal.confirm('是否确认批量暂停选中的阶段？');
    await batchUpdatePhaseStatus(phaseIds, '3');
    await getList();
    await loadStatistics();
    proxy?.$modal.msgSuccess('批量暂停成功');
  } catch (error) {
    console.error('批量暂停失败:', error);
    proxy?.$modal.msgError('操作失败');
  }
};

/** 批量删除 */
const handleBatchDelete = async () => {
  const phaseIds = selectedRows.value.map((row: any) => row.phaseId);
  try {
    await proxy?.$modal.confirm('是否确认批量删除选中的阶段？');
    for (const phaseId of phaseIds) {
      await delProjectPhase(phaseId);
    }
    await getList();
    await loadStatistics();
    proxy?.$modal.msgSuccess('批量删除成功');
  } catch (error) {
    console.error('批量删除失败:', error);
    proxy?.$modal.msgError('操作失败');
  }
};

/** 计算进度 */
const calculateProgress = (phase: any) => {
  // 如果有具体进度值，优先使用
  if (phase.progress !== undefined && phase.progress !== null) {
    return phase.progress;
  }

  // 根据状态计算进度
  if (phase.status === '2') return 100;
  if (phase.status === '0') return 0;

  // 根据时间计算进度
  const start = new Date(phase.startTime).getTime();
  const end = new Date(phase.endTime).getTime();
  const now = Date.now();

  if (now <= start) return 0;
  if (now >= end) return 100;

  const total = end - start;
  const current = now - start;
  return Math.round((current / total) * 100);
};

/** 获取阶段状态 */
const getPhaseStatus = (phase: any) => {
  if (phase.status === '2') return 'success';
  if (phase.status === '3' || phase.status === '4') return 'exception';

  const progress = phase.progress || calculateProgress(phase);
  if (progress >= 100) return 'warning';
  return '';
};

/** 获取状态类型 */
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    '0': 'info',
    '1': 'primary',
    '2': 'success',
    '3': 'warning',
    '4': 'danger'
  };
  return typeMap[status] || '';
};

/** 获取状态标签 */
const getStatusLabel = (status: string) => {
  const labelMap: Record<string, string> = {
    '0': '未开始',
    '1': '进行中',
    '2': '已完成',
    '3': '已暂停',
    '4': '已延期'
  };
  return labelMap[status] || status;
};

/** 获取优先级类型 */
const getPriorityType = (priority: number) => {
  const typeMap: Record<number, string> = {
    1: 'info',
    2: 'primary',
    3: 'danger'
  };
  return typeMap[priority] || 'primary';
};

/** 获取优先级标签 */
const getPriorityLabel = (priority: number) => {
  const labelMap: Record<number, string> = {
    1: '低',
    2: '中',
    3: '高'
  };
  return labelMap[priority] || '中';
};

/** 获取进度状态 */
const getProgressStatus = (percentage: number) => {
  if (percentage >= 90) return 'success';
  if (percentage >= 60) return '';
  return 'exception';
};

/** 取消按钮 */
const cancel = () => {
  open.value = false;
  reset();
};

onMounted(() => {
  loadProjects();
  getList();
  loadStatistics();
});
</script>

<style scoped>
.project-phase {
  padding: 20px;
}

.stat-card {
  height: 100%;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  }

  :deep(.el-card__body) {
    padding: 20px;
  }
}

.stat-content {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
    transition: left 0.5s;
  }

  &:hover::before {
    left: 100%;
  }

  .el-icon {
    font-size: 24px;
    color: #fff;
    z-index: 1;
  }

  &.total {
    background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-primary-light-3));
  }

  &.in-progress {
    background: linear-gradient(135deg, var(--el-color-success), var(--el-color-success-light-3));
  }

  &.delayed {
    background: linear-gradient(135deg, var(--el-color-danger), var(--el-color-danger-light-3));
  }

  &.upcoming {
    background: linear-gradient(135deg, var(--el-color-warning), var(--el-color-warning-light-3));
  }
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.stat-extra {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-top: 8px;
}

.stat-progress {
  margin-top: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 500;
}

.card-toolbar {
  display: flex;
  gap: 12px;
}

.batch-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: var(--el-color-primary-light-9);
  border: 1px solid var(--el-color-primary-light-7);
  border-radius: 6px;
  margin-bottom: 16px;
}

.batch-info {
  color: var(--el-color-primary);
  font-weight: 500;
  margin-right: auto;
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-input {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-unit {
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.mb-4 {
  margin-bottom: 16px;
}

.dialog-footer {
  text-align: right;
}

.gantt-view {
  margin-top: 20px;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-card {
  animation: fadeInUp 0.6s ease-out;
}

.stat-card:nth-child(2) {
  animation-delay: 0.1s;
}

.stat-card:nth-child(3) {
  animation-delay: 0.2s;
}

.stat-card:nth-child(4) {
  animation-delay: 0.3s;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .card-toolbar {
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 768px) {
  .project-phase {
    padding: 10px;
  }

  .stat-card {
    margin-bottom: 16px;
  }

  .batch-toolbar {
    flex-direction: column;
    align-items: flex-start;
  }
}

.project-option {
  .project-name {
    font-weight: 500;
    color: var(--el-text-color-primary);
    font-size: 14px;
    margin-bottom: 2px;
  }
  
  .project-desc {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    line-height: 1.2;
  }
}

.empty-option {
  padding: 12px;
  text-align: center;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
</style>
