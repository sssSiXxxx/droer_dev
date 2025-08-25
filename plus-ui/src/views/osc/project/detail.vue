<template>
  <div class="project-detail">
    <!-- 项目基本信息 -->
    <el-card class="project-info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon><Document /></el-icon>
          <span>项目基本信息</span>
          <el-button type="primary" @click="router.back()" style="margin-left: auto">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </template>

      <el-row :gutter="24">
        <el-col :span="8">
          <div class="info-item">
            <label>项目序号：</label>
            <span>{{ projectInfo.projectId }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <label>项目名称：</label>
            <span class="project-name">{{ projectInfo.projectName }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <label>项目状态：</label>
            <el-tag :type="getStatusType(projectInfo.status)">
              {{ getStatusLabel(projectInfo.status) }}
            </el-tag>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="24" style="margin-top: 16px">
        <el-col :span="24">
          <div class="info-item">
            <label>项目描述：</label>
            <span>{{ projectInfo.description || '暂无描述' }}</span>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="24" style="margin-top: 16px">
        <el-col :span="12">
          <div class="info-item">
            <label>代码仓库：</label>
            <el-link v-if="projectInfo.repositoryUrl" :href="projectInfo.repositoryUrl" target="_blank" type="primary">
              {{ projectInfo.repositoryUrl }}
            </el-link>
            <span v-else>暂无</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label>项目网站：</label>
            <el-link v-if="projectInfo.websiteUrl" :href="projectInfo.websiteUrl" target="_blank" type="primary">
              {{ projectInfo.websiteUrl }}
            </el-link>
            <span v-else>暂无</span>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 成员管理标签页 -->
    <el-card class="member-card" shadow="never" style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <el-icon><User /></el-icon>
          <span>项目成员管理</span>
          <el-button type="primary" @click="handleAddMember" style="margin-left: auto">
            <el-icon><Plus /></el-icon>
            添加成员
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="card">
        <!-- 成员可视化 -->
        <el-tab-pane label="成员可视化" name="visualization">
          <div class="visualization-container">
            <!-- 统计卡片 -->
            <el-row :gutter="16" class="stats-row">
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="stat-content">
                    <div class="stat-number">{{ memberStats.totalMembers || 0 }}</div>
                    <div class="stat-label">总成员数</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="stat-content">
                    <div class="stat-number">{{ memberStats.activeMembers || 0 }}</div>
                    <div class="stat-label">活跃成员</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="stat-content">
                    <div class="stat-number">{{ getLeaderCount() }}</div>
                    <div class="stat-label">项目负责人</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="6">
                <el-card class="stat-card" shadow="hover">
                  <div class="stat-content">
                    <div class="stat-number">{{ getCoreDeveloperCount() }}</div>
                    <div class="stat-label">核心开发者</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>

            <!-- 角色分布图 -->
            <el-row :gutter="16" style="margin-top: 24px">
              <el-col :span="12">
                <el-card class="chart-card" shadow="hover">
                  <template #header>
                    <div class="chart-header">
                      <span>角色分布</span>
                    </div>
                  </template>
                  <div class="chart-container" ref="pieChartRef"></div>
                </el-card>
              </el-col>
              <el-col :span="12">
                <el-card class="chart-card" shadow="hover">
                  <template #header>
                    <div class="chart-header">
                      <span>成员活跃度</span>
                    </div>
                  </template>
                  <div class="chart-container" ref="barChartRef"></div>
                </el-card>
              </el-col>
            </el-row>

            <!-- 成员关联图 -->
            <el-row style="margin-top: 24px">
              <el-col :span="24">
                <el-card class="chart-card" shadow="hover">
                  <template #header>
                    <div class="chart-header">
                      <span>人员关联图</span>
                    </div>
                  </template>
                  <div class="network-container" ref="networkChartRef"></div>
                </el-card>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>

        <!-- 成员列表 -->
        <el-tab-pane label="成员列表" name="list">
          <div class="member-list-container">
            <el-table v-loading="memberLoading" :data="memberList" border>
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column label="成员名称" prop="memberName" min-width="120" />
              <el-table-column label="角色" prop="role" width="120" align="center">
                <template #default="scope">
                  <el-tag :type="getRoleTagType(scope.row.role)">
                    {{ getRoleLabel(scope.row.role) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="权限级别" prop="permissionLevel" width="100" align="center">
                <template #default="scope">
                  <el-rate v-model="scope.row.permissionLevel" disabled :max="5" />
                </template>
              </el-table-column>
              <el-table-column label="活跃状态" prop="isActive" width="100" align="center">
                <template #default="scope">
                  <el-tag :type="scope.row.isActive === '1' ? 'success' : 'info'">
                    {{ scope.row.isActive === '1' ? '活跃' : '非活跃' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="贡献度评分" prop="contributionScore" width="120" align="center">
                <template #default="scope">
                  <el-progress
                    :percentage="scope.row.contributionScore || 0"
                    :color="getContributionColor(scope.row.contributionScore)"
                    :stroke-width="8"
                  />
                </template>
              </el-table-column>
              <el-table-column label="加入时间" prop="joinTime" width="180" align="center">
                <template #default="scope">
                  {{ parseTime(scope.row.joinTime) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="200" align="center">
                <template #default="scope">
                  <el-button type="primary" link @click="handleEditMember(scope.row)">
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-button>
                  <el-button type="danger" link @click="handleRemoveMember(scope.row)">
                    <el-icon><Delete /></el-icon>
                    移除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 添加/编辑成员对话框 -->
    <el-dialog :title="memberDialog.title" v-model="memberDialog.visible" width="500px" @close="resetMemberForm">
      <el-form ref="memberFormRef" :model="memberForm" :rules="memberRules" label-width="100px">
        <el-form-item label="成员" prop="memberId" v-if="memberDialog.type === 'add'">
          <el-select v-model="memberForm.memberId" placeholder="请选择成员" style="width: 100%">
            <el-option v-for="member in availableMembers" :key="member.memberId" :label="member.memberName" :value="member.memberId" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="memberForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="普通成员" value="0" />
            <el-option label="项目负责人" value="1" />
            <el-option label="核心开发者" value="2" />
            <el-option label="维护者" value="3" />
            <el-option label="贡献者" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="活跃状态" prop="isActive">
          <el-radio-group v-model="memberForm.isActive">
            <el-radio label="1">活跃</el-radio>
            <el-radio label="0">非活跃</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="memberForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="memberDialog.visible = false">取 消</el-button>
          <el-button type="primary" @click="submitMemberForm" :loading="memberSubmitLoading"> 确 定 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectDetail" lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Document, User, Plus, Edit, Delete, ArrowLeft } from '@element-plus/icons-vue';
import * as echarts from 'echarts';
import { getProject } from '@/api/osc/project';
import { getProjectMembers, getProjectMemberVisualization, addProjectMember, updateMemberRole, removeMember } from '@/api/osc/projectMember';
import { ProjectVO } from '@/api/osc/project/types';
import { ProjectMemberVO } from '@/api/osc/projectMember/types';
import { parseTime } from '@/utils/ruoyi';

const route = useRoute();
const router = useRouter();

// 响应式数据
const projectInfo = ref<ProjectVO>({});
const memberList = ref<ProjectMemberVO[]>([]);
const memberStats = ref<any>({});
const memberLoading = ref(false);
const activeTab = ref('visualization');

// 图表引用
const pieChartRef = ref<HTMLElement>();
const barChartRef = ref<HTMLElement>();
const networkChartRef = ref<HTMLElement>();

// 成员对话框
const memberDialog = reactive({
  visible: false,
  title: '',
  type: 'add' as 'add' | 'edit'
});

const memberForm = reactive({
  projectId: 0,
  memberId: undefined as number | undefined,
  role: '0',
  isActive: '1',
  remark: ''
});

const memberRules = {
  memberId: [{ required: true, message: '请选择成员', trigger: 'change' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  isActive: [{ required: true, message: '请选择活跃状态', trigger: 'change' }]
};

const memberFormRef = ref();
const memberSubmitLoading = ref(false);
const availableMembers = ref<any[]>([]);

// 获取项目信息
const getProjectInfo = async () => {
  try {
    const projectId = Number(route.params.id);
    const res = await getProject(projectId);
    projectInfo.value = res.data;
    memberForm.projectId = projectId;
  } catch (error) {
    ElMessage.error('获取项目信息失败');
  }
};

// 获取项目成员
const getMembers = async () => {
  memberLoading.value = true;
  try {
    const projectId = Number(route.params.id);
    const res = await getProjectMembers(projectId);
    memberList.value = res.data;
  } catch (error) {
    ElMessage.error('获取项目成员失败');
  } finally {
    memberLoading.value = false;
  }
};

// 获取成员可视化数据
const getVisualizationData = async () => {
  try {
    const projectId = Number(route.params.id);
    const res = await getProjectMemberVisualization(projectId);
    memberStats.value = res.data;

    // 初始化图表
    nextTick(() => {
      initPieChart();
      initBarChart();
      initNetworkChart();
    });
  } catch (error) {
    ElMessage.error('获取可视化数据失败');
  }
};

// 初始化饼图
const initPieChart = () => {
  if (!pieChartRef.value) return;

  const chart = echarts.init(pieChartRef.value);
  const roleData = memberStats.value.roleData || [];

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '角色分布',
        type: 'pie',
        radius: '50%',
        data: roleData.map((item: any) => ({
          name: item.roleName,
          value: item.memberCount,
          itemStyle: { color: item.color }
        }))
      }
    ]
  };

  chart.setOption(option);
};

// 初始化柱状图
const initBarChart = () => {
  if (!barChartRef.value) return;

  const chart = echarts.init(barChartRef.value);
  const roleData = memberStats.value.roleData || [];

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: roleData.map((item: any) => item.roleName)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '成员数量',
        type: 'bar',
        data: roleData.map((item: any) => ({
          value: item.memberCount,
          itemStyle: { color: item.color }
        }))
      }
    ]
  };

  chart.setOption(option);
};

// 初始化网络图
const initNetworkChart = () => {
  if (!networkChartRef.value) return;

  const chart = echarts.init(networkChartRef.value);
  const roleData = memberStats.value.roleData || [];

  // 构建节点和边数据
  const nodes: any[] = [];
  const links: any[] = [];

  // 添加项目中心节点
  nodes.push({
    id: 'project',
    name: projectInfo.value.projectName,
    symbolSize: 50,
    category: 0,
    itemStyle: { color: '#409eff' }
  });

  // 添加成员节点
  roleData.forEach((roleGroup: any) => {
    roleGroup.members.forEach((member: any) => {
      nodes.push({
        id: member.memberId,
        name: member.memberName,
        symbolSize: 30,
        category: parseInt(roleGroup.roleCode) + 1,
        itemStyle: { color: roleGroup.color }
      });

      // 添加与项目的连接
      links.push({
        source: 'project',
        target: member.memberId,
        value: roleGroup.roleName
      });
    });
  });

  const option = {
    tooltip: {
      formatter: function (params: any) {
        if (params.dataType === 'node') {
          return params.data.name;
        }
        return params.data.value;
      }
    },
    legend: {
      data: ['项目', '项目负责人', '核心开发者', '维护者', '贡献者', '普通成员']
    },
    series: [
      {
        type: 'graph',
        layout: 'force',
        data: nodes,
        links: links,
        categories: [{ name: '项目' }, { name: '项目负责人' }, { name: '核心开发者' }, { name: '维护者' }, { name: '贡献者' }, { name: '普通成员' }],
        roam: true,
        label: {
          show: true,
          position: 'right'
        },
        force: {
          repulsion: 100
        }
      }
    ]
  };

  chart.setOption(option);
};

// 获取状态标签
const getStatusLabel = (status: string) => {
  const statusMap: Record<string, string> = {
    '0': '草稿',
    '1': '审核中',
    '2': '进行中',
    '3': '已完成',
    '4': '已暂停',
    '5': '已驳回'
  };
  return statusMap[status] || '未知';
};

// 获取状态类型
const getStatusType = (status: string) => {
  const typeMap: Record<string, string> = {
    '0': 'info',
    '1': 'warning',
    '2': 'success',
    '3': 'success',
    '4': 'danger',
    '5': 'danger'
  };
  return typeMap[status] || 'info';
};

// 获取角色标签
const getRoleLabel = (role: string) => {
  const roleMap: Record<string, string> = {
    '0': '普通成员',
    '1': '项目负责人',
    '2': '核心开发者',
    '3': '维护者',
    '4': '贡献者'
  };
  return roleMap[role] || '未知';
};

// 获取角色标签类型
const getRoleTagType = (role: string) => {
  const typeMap: Record<string, string> = {
    '0': 'info',
    '1': 'danger',
    '2': 'warning',
    '3': 'primary',
    '4': 'success'
  };
  return typeMap[role] || 'info';
};

// 获取贡献度颜色
const getContributionColor = (score: number) => {
  if (score >= 80) return '#67c23a';
  if (score >= 60) return '#e6a23c';
  if (score >= 40) return '#f56c6c';
  return '#909399';
};

// 获取负责人数量
const getLeaderCount = () => {
  return memberList.value.filter((member) => member.role === '1').length;
};

// 获取核心开发者数量
const getCoreDeveloperCount = () => {
  return memberList.value.filter((member) => member.role === '2').length;
};

// 添加成员
const handleAddMember = () => {
  memberDialog.type = 'add';
  memberDialog.title = '添加项目成员';
  memberDialog.visible = true;
};

// 编辑成员
const handleEditMember = (row: ProjectMemberVO) => {
  memberDialog.type = 'edit';
  memberDialog.title = '编辑项目成员';
  memberForm.memberId = row.memberId;
  memberForm.role = row.role || '0';
  memberForm.isActive = row.isActive || '1';
  memberForm.remark = row.remark || '';
  memberDialog.visible = true;
};

// 移除成员
const handleRemoveMember = async (row: ProjectMemberVO) => {
  try {
    await ElMessageBox.confirm('确定要移除该成员吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    await removeMember(Number(route.params.id), row.memberId!);
    ElMessage.success('移除成功');
    await getMembers();
    await getVisualizationData();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('移除失败');
    }
  }
};

// 提交成员表单
const submitMemberForm = async () => {
  try {
    await memberFormRef.value.validate();
    memberSubmitLoading.value = true;

    if (memberDialog.type === 'add') {
      await addProjectMember(memberForm);
      ElMessage.success('添加成功');
    } else {
      await updateMemberRole(memberForm.projectId, memberForm.memberId!, memberForm.role);
      ElMessage.success('更新成功');
    }

    memberDialog.visible = false;
    await getMembers();
    await getVisualizationData();
  } catch (error) {
    ElMessage.error('操作失败');
  } finally {
    memberSubmitLoading.value = false;
  }
};

// 重置成员表单
const resetMemberForm = () => {
  memberForm.memberId = undefined;
  memberForm.role = '0';
  memberForm.isActive = '1';
  memberForm.remark = '';
  memberFormRef.value?.resetFields();
};

onMounted(async () => {
  await getProjectInfo();
  await getMembers();
  await getVisualizationData();
});
</script>

<style scoped>
.project-detail {
  padding: 20px;
  background-color: var(--el-bg-color-page);
  min-height: calc(100vh - 84px);
}

.project-info-card,
.member-card {
  border-radius: 8px;
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
}

.card-header .el-icon {
  margin-right: 8px;
  font-size: 18px;
  color: var(--el-color-primary);
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.info-item label {
  font-weight: 500;
  color: var(--el-text-color-regular);
  min-width: 80px;
  margin-right: 8px;
}

.project-name {
  font-weight: 600;
  color: var(--el-color-primary);
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  text-align: center;
  border-radius: 8px;
}

.stat-content {
  padding: 16px;
}

.stat-number {
  font-size: 32px;
  font-weight: 600;
  color: var(--el-color-primary);
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.chart-card {
  border-radius: 8px;
}

.chart-header {
  font-weight: 500;
  color: var(--el-text-color-primary);
}

.chart-container {
  height: 300px;
  width: 100%;
}

.network-container {
  height: 400px;
  width: 100%;
}

.member-list-container {
  margin-top: 16px;
}

:deep(.el-tabs__header) {
  margin-bottom: 16px;
}

:deep(.el-tabs__item) {
  font-weight: 500;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: var(--el-fill-color-light);
  font-weight: 600;
}

.dialog-footer {
  text-align: right;
}
</style>
