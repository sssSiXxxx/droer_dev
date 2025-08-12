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
            <el-progress 
              :percentage="statistics.completionRate || 0" 
              :status="getProgressStatus(statistics.completionRate)"
            />
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
          <div class="stat-extra">
            {{ statistics.completedPhases || 0 }} 已完成
          </div>
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
          <div class="stat-extra">
            平均延期 {{ statistics.averageDelay || 0 }} 天
          </div>
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
          <div class="stat-extra">
            未来7天内
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和工具栏 -->
    <el-card class="mb-4">
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="项目" prop="projectId">
          <el-select
            v-model="queryParams.projectId"
            placeholder="请选择项目"
            clearable
            style="width: 240px"
            @change="handleProjectChange"
          >
            <el-option
              v-for="item in projectOptions"
              :key="item.projectId"
              :label="item.projectName"
              :value="item.projectId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="阶段名称" prop="phaseName">
          <el-input
            v-model="queryParams.phaseName"
            placeholder="请输入阶段名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option
              v-for="dict in statusOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
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
            <el-button type="success" plain @click="handleAdd">
              <el-icon><Plus /></el-icon>新增阶段
            </el-button>
            <el-button type="primary" plain @click="switchView">
              <el-icon><Operation /></el-icon>
              {{ isGanttView ? '切换列表视图' : '切换甘特图视图' }}
            </el-button>
          </div>
        </div>
      </template>

      <!-- 甘特图视图 -->
      <div v-if="isGanttView" class="gantt-view">
        <gantt-chart :phases="phaseList" />
      </div>

      <!-- 列表视图 -->
      <div v-else class="list-view">
        <el-table v-loading="loading" :data="phaseList">
          <el-table-column label="阶段名称" align="center" prop="phaseName" min-width="120" />
          <el-table-column label="项目名称" align="center" prop="projectName" min-width="120" />
          <el-table-column label="开始时间" align="center" prop="startTime" width="180">
            <template #default="scope">
              <span>{{ parseTime(scope.row.startTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="结束时间" align="center" prop="endTime" width="180">
            <template #default="scope">
              <span>{{ parseTime(scope.row.endTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="进度" align="center" width="200">
            <template #default="scope">
              <el-progress 
                :percentage="calculateProgress(scope.row)"
                :status="getPhaseStatus(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="300">
            <template #default="scope">
              <el-button type="primary" link icon="Edit" @click="handleUpdate(scope.row)">
                修改
              </el-button>
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
                type="warning" 
                link 
                icon="VideoPause"
                v-if="scope.row.status === '1'"
                @click="handlePause(scope.row)"
              >
                暂停
              </el-button>
              <el-button 
                type="info" 
                link 
                icon="VideoPlay"
                v-if="scope.row.status === '3'"
                @click="handleResume(scope.row)"
              >
                恢复
              </el-button>
              <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total > 0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </el-card>

    <!-- 添加/修改对话框 -->
    <el-dialog :title="title" v-model="open" width="700px" append-to-body>
      <el-form ref="phaseFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="项目" prop="projectId">
          <el-select
            v-model="form.projectId"
            placeholder="请选择项目"
            style="width: 100%"
          >
            <el-option
              v-for="item in projectOptions"
              :key="item.projectId"
              :label="item.projectName"
              :value="item.projectId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="阶段名称" prop="phaseName">
          <el-input v-model="form.phaseName" placeholder="请输入阶段名称" />
        </el-form-item>
        <el-form-item label="阶段编码" prop="phaseCode">
          <el-input v-model="form.phaseCode" placeholder="请输入阶段编码" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="请选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="请选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="阶段描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入阶段描述"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
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
  VideoPlay
} from '@element-plus/icons-vue';
import { listProjectPhase, getProjectPhase, addProjectPhase, updateProjectPhase, delProjectPhase, getPhaseStatistics, completeProjectPhase, pauseProjectPhase, resumeProjectPhase } from '@/api/osc/projectPhase';
import { listProject } from '@/api/osc/project';
import type { FormInstance } from 'element-plus';
import GanttChart from './components/GanttChart.vue';
import { parseTime } from '@/utils/ruoyi';

const { proxy } = getCurrentInstance() as any;

// 遍历器
const loading = ref(false);
const open = ref(false);
const total = ref(0);
const title = ref('');
const isGanttView = ref(false);

// 项目选项
const projectOptions = ref([]);

// 状态选项
const statusOptions = [
  { label: '未开始', value: '0' },
  { label: '进行中', value: '1' },
  { label: '已完成', value: '2' },
  { label: '已延期', value: '3' }
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
  status: undefined
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
  status: '0'
});

// 表单校验规则
const rules = ref({
  projectId: [
    { required: true, message: '请选择项目', trigger: 'change' }
  ],
  phaseName: [
    { required: true, message: '请输入阶段名称', trigger: 'blur' }
  ],
  phaseCode: [
    { required: true, message: '请输入阶段编码', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
});

const phaseList = ref([]);
const phaseFormRef = ref<FormInstance>();

/** 查询项目列表 */
const loadProjects = async () => {
  try {
    const res = await listProject();
    projectOptions.value = res.rows;
  } catch (error) {
    console.error('获取项目列表失败:', error);
  }
};

/** 查询阶段列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await listProjectPhase(queryParams.value);
    phaseList.value = res.rows;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
};

/** 查询统计数据 */
const loadStatistics = async () => {
  if (!queryParams.value.projectId) return;
  try {
    const res = await getPhaseStatistics(queryParams.value.projectId);
    statistics.value = res.data;
  } catch (error) {
    console.error('获取统计数据失败:', error);
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
    status: '0'
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

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  open.value = true;
  title.value = '添加阶段';
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

/** 计算进度 */
const calculateProgress = (phase: any) => {
  if (phase.status === '2') return 100;
  if (phase.status === '0') return 0;
  
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
  if (phase.status === '3') return 'exception';
  
  const progress = calculateProgress(phase);
  if (progress >= 100) return 'warning';
  return '';
};

/** 获取状态类型 */
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    '0': 'info',
    '1': 'primary',
    '2': 'success',
    '3': 'danger'
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
});
</script>

<style scoped>
.project-phase {
  padding: 20px;
}

.stat-card {
  height: 100%;
  
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

  .el-icon {
    font-size: 24px;
    color: #fff;
  }

  &.total {
    background-color: var(--el-color-primary);
  }

  &.in-progress {
    background-color: var(--el-color-success);
  }

  &.delayed {
    background-color: var(--el-color-danger);
  }

  &.upcoming {
    background-color: var(--el-color-warning);
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

.mb-4 {
  margin-bottom: 16px;
}

.dialog-footer {
  text-align: right;
}

.gantt-view {
  margin-top: 20px;
}
</style>