<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>项目人员可视化管理</span>
          <el-button type="primary" @click="refreshData" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="80px" class="search-form">
        <el-form-item label="项目名称" prop="projectName">
          <el-input
            v-model="queryParams.projectName"
            placeholder="请输入项目名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="进行中" value="1" />
            <el-option label="已完成" value="2" />
            <el-option label="已暂停" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 项目列表 -->
      <el-table 
        v-loading="loading" 
        :data="projectList" 
        class="project-table"
        :header-cell-style="{ background: '#f8f9fa', color: '#606266' }"
      >
        <el-table-column label="序号" type="index" width="60" align="center" />
        
        <el-table-column label="项目名称" prop="projectName" min-width="200">
          <template #default="scope">
            <div class="project-info">
              <div class="project-name">{{ scope.row.projectName }}</div>
              <div class="project-code">{{ scope.row.projectCode }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="项目负责人" width="120" align="center">
          <template #default="scope">
            <div class="leader-info">
              <el-avatar :size="32" :src="getLeaderAvatar(scope.row.leader)">
                {{ getLeaderInitial(scope.row.leader) }}
              </el-avatar>
              <div class="leader-name">{{ scope.row.leader?.memberName || '未分配' }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="人员关联图" min-width="400">
          <template #default="scope">
            <div class="member-visualization">
              <div class="role-groups">
                <div 
                  v-for="roleGroup in scope.row.roleGroups" 
                  :key="roleGroup.roleCode"
                  class="role-group"
                  :class="`role-${roleGroup.roleCode}`"
                >
                  <div class="role-header">
                    <el-tag 
                      :color="roleGroup.color" 
                      effect="dark" 
                      size="small"
                      class="role-tag"
                    >
                      {{ roleGroup.roleName }} ({{ roleGroup.memberCount }})
                    </el-tag>
                  </div>
                  <div class="members-container">
                    <div 
                      v-for="member in roleGroup.members.slice(0, 5)" 
                      :key="member.memberId"
                      class="member-avatar"
                      :title="`${member.memberName} - ${member.memberEmail}`"
                    >
                      <el-avatar :size="24" :src="getMemberAvatar(member)">
                        {{ getMemberInitial(member) }}
                      </el-avatar>
                    </div>
                    <div 
                      v-if="roleGroup.memberCount > 5" 
                      class="more-members"
                      :title="`还有 ${roleGroup.memberCount - 5} 人`"
                    >
                      +{{ roleGroup.memberCount - 5 }}
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 统计信息 -->
              <div class="stats-info">
                <el-tooltip content="总人数" placement="top">
                  <el-tag type="info" size="small">
                    <el-icon><User /></el-icon>
                    {{ scope.row.totalMembers }}
                  </el-tag>
                </el-tooltip>
                <el-tooltip content="活跃人数" placement="top">
                  <el-tag type="success" size="small">
                    <el-icon><CircleCheck /></el-icon>
                    {{ scope.row.activeMembers }}
                  </el-tag>
                </el-tooltip>
                <el-tooltip content="查看关联图" placement="top">
                  <el-button 
                    type="primary" 
                    size="small" 
                    circle
                    @click="viewRelationChart(scope.row)"
                  >
                    <el-icon><Share /></el-icon>
                  </el-button>
                </el-tooltip>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button-group>
              <el-button 
                type="primary" 
                size="small" 
                @click="viewDetails(scope.row)"
                title="查看详情"
              >
                <el-icon><View /></el-icon>
              </el-button>
              <el-button 
                type="success" 
                size="small" 
                @click="manageMembers(scope.row)"
                title="管理成员"
              >
                <el-icon><User /></el-icon>
              </el-button>
              <el-button 
                type="warning" 
                size="small" 
                @click="viewChart(scope.row)"
                title="图表分析"
              >
                <el-icon><TrendCharts /></el-icon>
              </el-button>
              <el-button 
                type="info" 
                size="small" 
                @click="exportData(scope.row)"
                title="导出数据"
              >
                <el-icon><Download /></el-icon>
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog title="项目人员详情" v-model="detailVisible" width="80%" append-to-body>
      <div v-if="currentProject" class="project-detail">
        <!-- 项目基本信息 -->
        <el-card class="project-basic-info" shadow="never">
          <template #header>
            <span>项目基本信息</span>
          </template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="项目名称">{{ currentProject.projectName }}</el-descriptions-item>
            <el-descriptions-item label="项目代码">{{ currentProject.projectCode }}</el-descriptions-item>
            <el-descriptions-item label="项目负责人">{{ currentProject.leader?.memberName || '未分配' }}</el-descriptions-item>
            <el-descriptions-item label="总人数">{{ currentProject.totalMembers }}</el-descriptions-item>
            <el-descriptions-item label="活跃人数">{{ currentProject.activeMembers }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ parseTime(currentProject.updateTime) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 角色分布图表 -->
        <el-card class="role-chart" shadow="never">
          <template #header>
            <span>角色分布</span>
          </template>
          <div ref="roleChartRef" style="width: 100%; height: 300px;"></div>
        </el-card>

        <!-- 人员列表 -->
        <el-card class="member-list" shadow="never">
          <template #header>
            <span>人员列表</span>
          </template>
          <el-table :data="currentProjectMembers" max-height="400">
            <el-table-column label="头像" width="80" align="center">
              <template #default="scope">
                <el-avatar :size="40" :src="getMemberAvatar(scope.row)">
                  {{ getMemberInitial(scope.row) }}
                </el-avatar>
              </template>
            </el-table-column>
            <el-table-column label="姓名" prop="memberName" min-width="120" />
            <el-table-column label="邮箱" prop="memberEmail" min-width="180" />
            <el-table-column label="角色" prop="roleName" width="120">
              <template #default="scope">
                <el-tag :type="getRoleTagType(scope.row.role)">
                  {{ scope.row.roleName }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="权限级别" prop="permissionLevel" width="120" align="center">
              <template #default="scope">
                <el-rate
                  v-model="scope.row.permissionLevel"
                  :max="5"
                  disabled
                  show-score
                  text-color="#ff9900"
                />
              </template>
            </el-table-column>
            <el-table-column label="活跃状态" prop="isActive" width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.isActive === '1' ? 'success' : 'info'">
                  {{ scope.row.isActive === '1' ? '活跃' : '非活跃' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="贡献度" prop="contributionScore" width="120" align="center">
              <template #default="scope">
                <el-progress
                  :percentage="scope.row.contributionScore || 0"
                  :color="getContributionColor(scope.row.contributionScore)"
                  :stroke-width="8"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </el-dialog>

    <!-- 图表分析对话框 -->
    <el-dialog title="项目人员图表分析" v-model="chartVisible" width="70%" append-to-body>
      <div v-if="currentProject" class="chart-analysis">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="never">
              <template #header>
                <span>角色分布饼图</span>
              </template>
              <div ref="pieChartRef" style="width: 100%; height: 300px;"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never">
              <template #header>
                <span>贡献度分布</span>
              </template>
              <div ref="barChartRef" style="width: 100%; height: 300px;"></div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <!-- 人员关联图对话框 -->
    <el-dialog title="项目人员关联图" v-model="relationVisible" width="80%" append-to-body>
      <div v-if="currentProject" class="relation-chart-container">
        <MemberRelationChart
          :project-name="currentProject.projectName"
          :project-code="currentProject.projectCode"
          :role-groups="currentProject.roleGroups"
          :total-members="currentProject.totalMembers"
          @member-click="handleMemberClick"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectVisualization" lang="ts">
import { 
  listProjectMember, 
  getProjectMemberVisualization, 
  getProjectMembers 
} from "@/api/osc/projectMember";
import { listProject } from "@/api/osc/project";
import { parseTime } from "@/utils/ruoyi";
import * as echarts from 'echarts';
import MemberRelationChart from '@/components/MemberRelationChart/index.vue';

const { proxy } = getCurrentInstance();

// 响应式数据
const loading = ref(true);
const total = ref(0);
const projectList = ref([]);
const detailVisible = ref(false);
const chartVisible = ref(false);
const relationVisible = ref(false);
const currentProject = ref(null);
const currentProjectMembers = ref([]);

// 图表refs
const roleChartRef = ref();
const pieChartRef = ref();
const barChartRef = ref();

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  status: undefined
});

// 角色配置
const roleConfig = {
  '0': { name: '普通成员', color: '#909399', tagType: 'info' },
  '1': { name: '项目负责人', color: '#f56c6c', tagType: 'danger' },
  '2': { name: '核心开发者', color: '#e6a23c', tagType: 'warning' },
  '3': { name: '维护者', color: '#409eff', tagType: 'primary' },
  '4': { name: '贡献者', color: '#67c23a', tagType: 'success' }
};

/** 查询项目列表 */
async function getList() {
  loading.value = true;
  try {
    const response = await listProject(queryParams.value);
    const projects = response.rows || [];
    
    // 为每个项目获取人员数据
    const projectsWithMembers = await Promise.all(
      projects.map(async (project) => {
        try {
          const visualization = await getProjectMemberVisualization(project.id);
          const members = await getProjectMembers(project.id);
          
          // 处理角色分组数据
          const roleGroups = visualization.data?.roleData || [];
          const processedRoleGroups = roleGroups
            .filter(group => group.memberCount > 0)
            .map(group => ({
              ...group,
              color: roleConfig[group.roleCode]?.color || '#909399'
            }));

          // 找出项目负责人
          const leader = members.data?.find(member => member.role === '1');

          return {
            ...project,
            roleGroups: processedRoleGroups,
            totalMembers: visualization.data?.totalMembers || 0,
            activeMembers: visualization.data?.activeMembers || 0,
            leader: leader
          };
        } catch (error) {
          console.error(`获取项目 ${project.id} 的人员数据失败:`, error);
          return {
            ...project,
            roleGroups: [],
            totalMembers: 0,
            activeMembers: 0,
            leader: null
          };
        }
      })
    );
    
    projectList.value = projectsWithMembers;
    total.value = response.total;
  } catch (error) {
    console.error('获取项目列表失败:', error);
    proxy.$modal.msgError('获取项目列表失败');
  } finally {
    loading.value = false;
  }
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 刷新数据 */
function refreshData() {
  getList();
}

/** 查看详情 */
async function viewDetails(row) {
  currentProject.value = row;
  try {
    const response = await getProjectMembers(row.id);
    currentProjectMembers.value = response.data || [];
    detailVisible.value = true;
    
    // 延迟渲染图表
    nextTick(() => {
      renderRoleChart();
    });
  } catch (error) {
    console.error('获取项目成员详情失败:', error);
    proxy.$modal.msgError('获取项目成员详情失败');
  }
}

/** 管理成员 */
function manageMembers(row) {
  // 跳转到成员管理页面
  proxy.$router.push({
    path: '/osc/projectMember',
    query: { projectId: row.id }
  });
}

/** 查看关联图 */
function viewRelationChart(row) {
  currentProject.value = row;
  relationVisible.value = true;
}

/** 处理成员点击 */
function handleMemberClick(member) {
  proxy.$modal.msgInfo(`点击了成员: ${member.memberName} (${member.memberEmail})`);
  // 这里可以跳转到成员详情页面或执行其他操作
}

/** 查看图表 */
async function viewChart(row) {
  currentProject.value = row;
  try {
    const response = await getProjectMembers(row.id);
    currentProjectMembers.value = response.data || [];
    chartVisible.value = true;
    
    // 延迟渲染图表
    nextTick(() => {
      renderPieChart();
      renderBarChart();
    });
  } catch (error) {
    console.error('获取图表数据失败:', error);
    proxy.$modal.msgError('获取图表数据失败');
  }
}

/** 导出数据 */
function exportData(row) {
  proxy.download('osc/projectMember/export', {
    projectId: row.id
  }, `project_${row.projectCode}_members_${new Date().getTime()}.xlsx`);
}

/** 获取成员头像 */
function getMemberAvatar(member) {
  return member.avatar || `https://api.dicebear.com/7.x/initials/svg?seed=${member.memberName}`;
}

/** 获取成员姓名首字母 */
function getMemberInitial(member) {
  return member.memberName ? member.memberName.charAt(0).toUpperCase() : '?';
}

/** 获取负责人头像 */
function getLeaderAvatar(leader) {
  if (!leader) return '';
  return leader.avatar || `https://api.dicebear.com/7.x/initials/svg?seed=${leader.memberName}`;
}

/** 获取负责人姓名首字母 */
function getLeaderInitial(leader) {
  if (!leader) return '?';
  return leader.memberName ? leader.memberName.charAt(0).toUpperCase() : '?';
}

/** 获取角色标签类型 */
function getRoleTagType(role) {
  return roleConfig[role]?.tagType || 'info';
}

/** 获取贡献度颜色 */
function getContributionColor(score) {
  if (score >= 80) return '#67C23A';
  if (score >= 60) return '#E6A23C';
  if (score >= 40) return '#F56C6C';
  return '#909399';
}

/** 渲染角色分布图表 */
function renderRoleChart() {
  if (!roleChartRef.value || !currentProject.value) return;
  
  const chart = echarts.init(roleChartRef.value);
  const roleGroups = currentProject.value.roleGroups || [];
  
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
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        data: roleGroups.map(group => ({
          value: group.memberCount,
          name: group.roleName,
          itemStyle: { color: group.color }
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };
  
  chart.setOption(option);
}

/** 渲染饼图 */
function renderPieChart() {
  if (!pieChartRef.value || !currentProject.value) return;
  renderRoleChart();
}

/** 渲染柱状图 */
function renderBarChart() {
  if (!barChartRef.value || !currentProjectMembers.value) return;
  
  const chart = echarts.init(barChartRef.value);
  const members = currentProjectMembers.value;
  
  // 按贡献度分段统计
  const scoreRanges = [
    { name: '0-20', min: 0, max: 20, count: 0 },
    { name: '21-40', min: 21, max: 40, count: 0 },
    { name: '41-60', min: 41, max: 60, count: 0 },
    { name: '61-80', min: 61, max: 80, count: 0 },
    { name: '81-100', min: 81, max: 100, count: 0 }
  ];
  
  members.forEach(member => {
    const score = member.contributionScore || 0;
    scoreRanges.forEach(range => {
      if (score >= range.min && score <= range.max) {
        range.count++;
      }
    });
  });
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    xAxis: {
      type: 'category',
      data: scoreRanges.map(range => range.name)
    },
    yAxis: {
      type: 'value',
      name: '人数'
    },
    series: [
      {
        name: '贡献度分布',
        type: 'bar',
        data: scoreRanges.map(range => ({
          value: range.count,
          itemStyle: { color: getContributionColor((range.min + range.max) / 2) }
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  };
  
  chart.setOption(option);
}

// 生命周期
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

.search-form {
  margin-bottom: 20px;
}

.project-table {
  margin-bottom: 20px;
}

.project-info {
  text-align: left;
}

.project-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.project-code {
  font-size: 12px;
  color: #909399;
}

.leader-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.leader-name {
  font-size: 12px;
  color: #606266;
}

.member-visualization {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.role-groups {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 12px;
}

.role-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 120px;
}

.role-header {
  display: flex;
  align-items: center;
}

.role-tag {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  color: white;
  border: none;
}

.members-container {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
}

.member-avatar {
  cursor: pointer;
  transition: transform 0.2s;
}

.member-avatar:hover {
  transform: scale(1.1);
}

.more-members {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: #e4e7ed;
  border-radius: 50%;
  font-size: 10px;
  color: #606266;
  cursor: pointer;
}

.stats-info {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.stats-info .el-tag {
  font-size: 11px;
}

.project-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-analysis {
  min-height: 400px;
}

.relation-chart-container {
  min-height: 600px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.project-basic-info,
.role-chart,
.member-list {
  margin-bottom: 20px;
}

.role-group.role-1 {
  border-left: 3px solid #f56c6c;
  padding-left: 8px;
}

.role-group.role-2 {
  border-left: 3px solid #e6a23c;
  padding-left: 8px;
}

.role-group.role-3 {
  border-left: 3px solid #409eff;
  padding-left: 8px;
}

.role-group.role-4 {
  border-left: 3px solid #67c23a;
  padding-left: 8px;
}

.role-group.role-0 {
  border-left: 3px solid #909399;
  padding-left: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .role-groups {
    flex-direction: column;
  }
  
  .member-visualization {
    padding: 8px;
  }
}
</style>