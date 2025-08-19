<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>项目人员可视化管理</span>
          <div class="header-actions">
            <el-button type="info" @click="switchToProjectView" :class="{ active: currentView === 'project' }">
              项目视图
            </el-button>
            <el-button type="info" @click="switchToMemberView" :class="{ active: currentView === 'member' }">
              成员视图
            </el-button>
            <el-button type="primary" @click="refreshData" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <!-- 项目视图搜索区域 -->
      <el-form v-if="currentView === 'project'" :model="projectQueryParams" ref="projectQueryRef" :inline="true" label-width="80px" class="search-form">
        <el-form-item label="项目名称" prop="projectName">
          <el-input
            v-model="projectQueryParams.projectName"
            placeholder="请输入项目名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleProjectQuery"
          />
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="projectQueryParams.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="进行中" value="1" />
            <el-option label="已完成" value="2" />
            <el-option label="已暂停" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleProjectQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetProjectQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 成员视图搜索区域 -->
      <el-form v-if="currentView === 'member'" :model="queryParams" ref="queryRef" :inline="true" label-width="68px">
        <el-form-item label="项目名称" prop="projectName">
          <el-input
            v-model="queryParams.projectName"
            placeholder="请输入项目名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="成员名称" prop="memberName">
          <el-input
            v-model="queryParams.memberName"
            placeholder="请输入成员名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="queryParams.role" placeholder="请选择角色" clearable style="width: 200px">
            <el-option label="普通成员" value="0" />
            <el-option label="项目负责人" value="1" />
            <el-option label="核心开发者" value="2" />
            <el-option label="维护者" value="3" />
            <el-option label="贡献者" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="活跃状态" prop="isActive">
          <el-select v-model="queryParams.isActive" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="活跃" value="1" />
            <el-option label="非活跃" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 项目视图操作按钮 -->
      <el-row v-if="currentView === 'project'" :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAddProject"
            v-hasPermi="['osc:project:add']"
          >新增项目</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="View"
            @click="handleBatchManageMembers"
          >批量管理</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExportProjects"
          >导出项目</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getProjectList"></right-toolbar>
      </el-row>

      <!-- 成员视图操作按钮 -->
      <el-row v-if="currentView === 'member'" :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['osc:projectMember:add']"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['osc:projectMember:edit']"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['osc:projectMember:remove']"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['osc:projectMember:export']"
          >导出</el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 项目视图表格 -->
      <el-table 
        v-if="currentView === 'project'"
        v-loading="projectLoading" 
        :data="projectList" 
        class="project-visualization-table"
        :header-cell-style="{ background: '#f8f9fa', color: '#606266' }"
        row-key="id"
      >
        <el-table-column label="序号" type="index" width="60" align="center" :index="getProjectIndex" />
        
        <el-table-column label="项目名称" prop="projectName" min-width="200">
          <template #default="scope">
            <div class="project-info-cell">
              <div class="project-name-main">{{ scope.row.projectName }}</div>
              <div class="project-code-sub">{{ scope.row.projectCode }}</div>
              <div class="project-desc-sub" v-if="scope.row.description">
                {{ scope.row.description.substring(0, 50) }}{{ scope.row.description.length > 50 ? '...' : '' }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="项目负责人" width="140" align="center">
          <template #default="scope">
            <div class="leader-info-cell">
              <el-avatar :size="36" :src="getLeaderAvatar(scope.row.leader)" class="leader-avatar">
                {{ getLeaderInitial(scope.row.leader) }}
              </el-avatar>
              <div class="leader-details">
                <div class="leader-name">{{ scope.row.leader?.memberName || '未分配' }}</div>
                <div class="leader-role" v-if="scope.row.leader">项目负责人</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="人员关联图" min-width="450">
          <template #default="scope">
            <div class="member-visualization-cell">
              <div class="role-groups-container">
                <div 
                  v-for="roleGroup in scope.row.roleGroups" 
                  :key="roleGroup.roleCode"
                  class="role-group-item"
                  :class="`role-${roleGroup.roleCode}`"
                >
                  <div class="role-header-item">
                    <el-tag 
                      :color="roleGroup.color" 
                      effect="dark" 
                      size="small"
                      class="role-tag-item"
                    >
                      {{ roleGroup.roleName }} ({{ roleGroup.memberCount }})
                    </el-tag>
                  </div>
                  <div class="members-avatars">
                    <el-tooltip
                      v-for="member in roleGroup.members.slice(0, 4)" 
                      :key="member.memberId"
                      :content="`${member.memberName} - ${member.memberEmail}`"
                      placement="top"
                    >
                      <el-avatar 
                        :size="28" 
                        :src="getMemberAvatar(member)"
                        class="member-avatar-item"
                        @click="viewMemberContribution(member, scope.row)"
                      >
                        {{ getMemberInitial(member) }}
                      </el-avatar>
                    </el-tooltip>
                    <div 
                      v-if="roleGroup.memberCount > 4" 
                      class="more-members-count"
                      :title="`还有 ${roleGroup.memberCount - 4} 人`"
                      @click="viewAllMembers(scope.row, roleGroup)"
                    >
                      +{{ roleGroup.memberCount - 4 }}
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 统计信息行 -->
              <div class="stats-info-row">
                <el-tooltip content="总人数" placement="top">
                  <el-tag type="info" size="small" class="stat-tag">
                    <el-icon><User /></el-icon>
                    {{ scope.row.totalMembers }}
                  </el-tag>
                </el-tooltip>
                <el-tooltip content="活跃人数" placement="top">
                  <el-tag type="success" size="small" class="stat-tag">
                    <el-icon><CircleCheck /></el-icon>
                    {{ scope.row.activeMembers }}
                  </el-tag>
                </el-tooltip>
                <el-tooltip content="贡献总数" placement="top">
                  <el-tag type="warning" size="small" class="stat-tag">
                    <el-icon><Star /></el-icon>
                    {{ scope.row.totalContributions || 0 }}
                  </el-tag>
                </el-tooltip>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="scope">
            <el-button-group class="action-buttons">
              <el-tooltip content="查看项目详情" placement="top">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="viewProjectDetails(scope.row)"
                >
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="管理项目成员" placement="top">
                <el-button 
                  type="success" 
                  size="small" 
                  @click="manageProjectMembers(scope.row)"
                >
                  <el-icon><User /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="查看关联图表" placement="top">
                <el-button 
                  type="warning" 
                  size="small" 
                  @click="viewProjectChart(scope.row)"
                >
                  <el-icon><TrendCharts /></el-icon>
                </el-button>
              </el-tooltip>
              <el-tooltip content="导出项目数据" placement="top">
                <el-button 
                  type="info" 
                  size="small" 
                  @click="exportProjectData(scope.row)"
                >
                  <el-icon><Download /></el-icon>
                </el-button>
              </el-tooltip>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <!-- 成员视图表格 -->
      <el-table v-if="currentView === 'member'" v-loading="loading" :data="projectMemberList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="ID" align="center" prop="id" width="80" />
        <el-table-column label="项目名称" align="center" prop="projectName" min-width="150" />
        <el-table-column label="项目代码" align="center" prop="projectCode" min-width="120" />
        <el-table-column label="成员名称" align="center" prop="memberName" min-width="120" />
        <el-table-column label="成员邮箱" align="center" prop="memberEmail" min-width="180" />
        <el-table-column label="角色" align="center" prop="roleName" width="120">
          <template #default="scope">
            <el-tag :type="getRoleTagType(scope.row.role)">
              {{ scope.row.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="权限级别" align="center" prop="permissionLevel" width="100">
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
        <el-table-column label="活跃状态" align="center" prop="isActive" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.isActive === '1' ? 'success' : 'info'">
              {{ scope.row.isActive === '1' ? '活跃' : '非活跃' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="贡献度评分" align="center" prop="contributionScore" width="120">
          <template #default="scope">
            <el-progress
              :percentage="scope.row.contributionScore || 0"
              :color="getContributionColor(scope.row.contributionScore)"
            />
          </template>
        </el-table-column>
        <el-table-column label="加入时间" align="center" prop="joinTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.joinTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
          <template #default="scope">
            <el-button
              type="text"
              icon="Edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['osc:projectMember:edit']"
            >修改</el-button>
            <el-button
              type="text"
              icon="Delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['osc:projectMember:remove']"
            >删除</el-button>
            <el-button
              type="text"
              icon="View"
              @click="viewMemberContribution(scope.row)"
            >贡献记录</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 项目视图分页 -->
      <pagination
        v-if="currentView === 'project'"
        v-show="projectTotal > 0"
        :total="projectTotal"
        v-model:page="projectQueryParams.pageNum"
        v-model:limit="projectQueryParams.pageSize"
        @pagination="getProjectList"
      />

      <!-- 成员视图分页 -->
      <pagination
        v-if="currentView === 'member'"
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改项目成员关联对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="projectMemberRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="项目ID" prop="projectId">
          <el-input v-model="form.projectId" placeholder="请输入项目ID" />
        </el-form-item>
        <el-form-item label="成员ID" prop="memberId">
          <el-input v-model="form.memberId" placeholder="请输入成员ID" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="普通成员" value="0" />
            <el-option label="项目负责人" value="1" />
            <el-option label="核心开发者" value="2" />
            <el-option label="维护者" value="3" />
            <el-option label="贡献者" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限级别" prop="permissionLevel">
          <el-input-number v-model="form.permissionLevel" :min="1" :max="5" />
        </el-form-item>
        <el-form-item label="活跃状态" prop="isActive">
          <el-radio-group v-model="form.isActive">
            <el-radio label="1">活跃</el-radio>
            <el-radio label="0">非活跃</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="贡献度评分" prop="contributionScore">
          <el-input-number v-model="form.contributionScore" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 成员贡献记录对话框 -->
    <el-dialog 
      title="成员贡献记录" 
      v-model="contributionDialogVisible" 
      width="80%" 
      append-to-body
      :close-on-click-modal="false"
    >
      <div v-if="currentMember" class="contribution-record-container">
        <!-- 成员基本信息卡片 -->
        <el-card class="member-info-card" shadow="never">
          <template #header>
            <span>成员基本信息</span>
          </template>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="成员姓名">
              <div class="member-name-with-avatar">
                <el-avatar :size="32" :src="getMemberAvatar(currentMember)">
                  {{ getMemberInitial(currentMember) }}
                </el-avatar>
                <span class="member-name-text">{{ currentMember.memberName }}</span>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ currentMember.memberEmail }}</el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag :type="getRoleTagType(currentMember.role)">
                {{ currentMember.roleName }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="权限级别">
              <el-rate
                v-model="currentMember.permissionLevel"
                :max="5"
                disabled
                show-score
                text-color="#ff9900"
              />
            </el-descriptions-item>
            <el-descriptions-item label="活跃状态">
              <el-tag :type="currentMember.isActive === '1' ? 'success' : 'info'">
                {{ currentMember.isActive === '1' ? '活跃' : '非活跃' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="贡献度评分">
              <el-progress
                :percentage="currentMember.contributionScore || 0"
                :color="getContributionColor(currentMember.contributionScore)"
                :stroke-width="8"
              />
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 贡献统计卡片 -->
        <el-card class="contribution-stats-card" shadow="never">
          <template #header>
            <span>贡献统计</span>
          </template>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-value">{{ contributionStats.totalContributions || 0 }}</div>
                <div class="stat-label">总贡献数</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-value">{{ contributionStats.totalPoints || 0 }}</div>
                <div class="stat-label">总贡献点数</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-value">{{ contributionStats.thisMonthContributions || 0 }}</div>
                <div class="stat-label">本月贡献</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-value">{{ contributionStats.lastActivityDays || 0 }}</div>
                <div class="stat-label">最近活跃(天)</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 贡献记录筛选 -->
        <el-card class="contribution-filter-card" shadow="never">
          <el-form :model="contributionQuery" :inline="true" label-width="80px">
            <el-form-item label="贡献类型">
              <el-select v-model="contributionQuery.contributionType" placeholder="请选择类型" clearable style="width: 150px">
                <el-option label="代码提交" value="0" />
                <el-option label="问题修复" value="1" />
                <el-option label="文档贡献" value="2" />
                <el-option label="回答问题" value="3" />
                <el-option label="其他" value="4" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间范围">
              <el-date-picker
                v-model="contributionQuery.dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                style="width: 250px"
                @change="handleDateRangeChange"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchContributions">搜索</el-button>
              <el-button @click="resetContributionQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 贡献记录表格 -->
        <el-card class="contribution-list-card" shadow="never">
          <template #header>
            <span>贡献记录详情</span>
          </template>
          <el-table 
            v-loading="contributionLoading" 
            :data="contributionList" 
            max-height="400"
            :header-cell-style="{ background: '#f8f9fa', color: '#606266' }"
          >
            <el-table-column label="贡献时间" prop="contributionTime" width="180" align="center">
              <template #default="scope">
                {{ parseTime(scope.row.contributionTime, '{y}-{m}-{d} {h}:{i}') }}
              </template>
            </el-table-column>
            <el-table-column label="贡献类型" prop="contributionType" width="120" align="center">
              <template #default="scope">
                <el-tag :type="getContributionTypeTagType(scope.row.contributionType)">
                  {{ getContributionTypeName(scope.row.contributionType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="贡献内容" prop="content" min-width="250">
              <template #default="scope">
                <div class="contribution-content">
                  <div class="content-text">{{ scope.row.content }}</div>
                  <div class="content-url" v-if="scope.row.url">
                    <el-link :href="scope.row.url" target="_blank" type="primary">
                      <el-icon><Link /></el-icon>
                      查看详情
                    </el-link>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="贡献点数" prop="points" width="100" align="center">
              <template #default="scope">
                <el-tag type="warning" size="small">
                  +{{ scope.row.points }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          
          <!-- 贡献记录分页 -->
          <pagination
            v-show="contributionTotal > 0"
            :total="contributionTotal"
            v-model:page="contributionQuery.pageNum"
            v-model:limit="contributionQuery.pageSize"
            @pagination="getContributionList"
            class="contribution-pagination"
          />
        </el-card>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectMember" lang="ts">
import { listProjectMember, getProjectMember, delProjectMember, addProjectMember, updateProjectMember, exportProjectMember } from "@/api/osc/projectMember";
import { listProject } from "@/api/osc/project";
import { listContribution } from "@/api/osc/contribution";
import { parseTime } from "@/utils/ruoyi";

const { proxy } = getCurrentInstance();
const { sys_normal_disable } = proxy.useDict("sys_normal_disable");

// 基础响应式数据
const projectMemberList = ref([]);
const projectList = ref([]);
const open = ref(false);
const loading = ref(true);
const projectLoading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const projectTotal = ref(0);
const title = ref("");

// 视图控制
const currentView = ref('project'); // 'project' or 'member'

// 项目查询参数
const projectQueryParams = ref({
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  status: undefined
});

// 贡献记录相关
const contributionDialogVisible = ref(false);
const currentMember = ref(null);
const currentProject = ref(null);
const contributionLoading = ref(false);
const contributionList = ref([]);
const contributionTotal = ref(0);
const contributionStats = ref({});

const contributionQuery = ref({
  pageNum: 1,
  pageSize: 10,
  contributionType: undefined,
  dateRange: [],
  startDate: undefined,
  endDate: undefined,
  memberId: undefined,
  projectId: undefined
});

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    projectName: undefined,
    memberName: undefined,
    role: undefined,
    isActive: undefined
  },
  rules: {
    projectId: [
      { required: true, message: "项目ID不能为空", trigger: "blur" }
    ],
    memberId: [
      { required: true, message: "成员ID不能为空", trigger: "blur" }
    ],
    role: [
      { required: true, message: "角色不能为空", trigger: "change" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

// 角色配置
const roleConfig = {
  '0': { name: '普通成员', color: '#909399', tagType: 'info' },
  '1': { name: '项目负责人', color: '#f56c6c', tagType: 'danger' },
  '2': { name: '核心开发者', color: '#e6a23c', tagType: 'warning' },
  '3': { name: '维护者', color: '#409eff', tagType: 'primary' },
  '4': { name: '贡献者', color: '#67c23a', tagType: 'success' }
};

/** 切换到项目视图 */
function switchToProjectView() {
  currentView.value = 'project';
  getProjectList();
}

/** 切换到成员视图 */
function switchToMemberView() {
  currentView.value = 'member';
  getList();
}

/** 刷新数据 */
function refreshData() {
  if (currentView.value === 'project') {
    getProjectList();
  } else {
    getList();
  }
}

/** 获取项目序号 */
function getProjectIndex(index) {
  return (projectQueryParams.value.pageNum - 1) * projectQueryParams.value.pageSize + index + 1;
}

/** 查询项目列表 */
async function getProjectList() {
  projectLoading.value = true;
  try {
    const response = await listProject(projectQueryParams.value);
    const projects = response.rows || [];
    
    // 为每个项目获取人员数据和可视化数据
    const projectsWithMembers = await Promise.all(
      projects.map(async (project) => {
        try {
          // 获取项目成员
          const membersResponse = await listProjectMember({ projectId: project.id });
          const members = membersResponse.rows || [];
          
          // 按角色分组
          const roleGroups = {};
          let activeCount = 0;
          let leader = null;
          
          members.forEach(member => {
            if (member.isActive === '1') activeCount++;
            if (member.role === '1') leader = member;
            
            const roleCode = member.role;
            if (!roleGroups[roleCode]) {
              roleGroups[roleCode] = {
                roleCode,
                roleName: roleConfig[roleCode]?.name || '未知角色',
                color: roleConfig[roleCode]?.color || '#909399',
                memberCount: 0,
                members: []
              };
            }
            roleGroups[roleCode].memberCount++;
            roleGroups[roleCode].members.push(member);
          });
          
          return {
            ...project,
            roleGroups: Object.values(roleGroups),
            totalMembers: members.length,
            activeMembers: activeCount,
            leader: leader,
            totalContributions: 0 // 可以从贡献API获取
          };
        } catch (error) {
          console.error(`获取项目 ${project.id} 的人员数据失败:`, error);
          return {
            ...project,
            roleGroups: [],
            totalMembers: 0,
            activeMembers: 0,
            leader: null,
            totalContributions: 0
          };
        }
      })
    );
    
    projectList.value = projectsWithMembers;
    projectTotal.value = response.total;
  } catch (error) {
    console.error('获取项目列表失败:', error);
    proxy.$modal.msgError('获取项目列表失败');
  } finally {
    projectLoading.value = false;
  }
}

/** 查询项目成员关联列表 */
function getList() {
  loading.value = true;
  listProjectMember(queryParams.value).then(response => {
    projectMemberList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: undefined,
    projectId: undefined,
    memberId: undefined,
    role: undefined,
    permissionLevel: 1,
    isActive: "1",
    contributionScore: 0,
    remark: undefined
  };
  proxy.resetForm("projectMemberRef");
}

/** 项目查询按钮操作 */
function handleProjectQuery() {
  projectQueryParams.value.pageNum = 1;
  getProjectList();
}

/** 项目重置按钮操作 */
function resetProjectQuery() {
  proxy.resetForm("projectQueryRef");
  handleProjectQuery();
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

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加项目成员关联";
}

/** 新增项目 */
function handleAddProject() {
  proxy.$router.push('/osc/project/add');
}

/** 批量管理成员 */
function handleBatchManageMembers() {
  proxy.$modal.msgInfo('批量管理功能开发中...');
}

/** 导出项目 */
function handleExportProjects() {
  proxy.download('osc/project/export', {
    ...projectQueryParams.value
  }, `projects_${new Date().getTime()}.xlsx`);
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;
  getProjectMember(_id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改项目成员关联";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["projectMemberRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateProjectMember(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addProjectMember(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除项目成员关联编号为"' + _ids + '"的数据项？').then(function() {
    return delProjectMember(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('osc/projectMember/export', {
    ...queryParams.value
  }, `projectMember_${new Date().getTime()}.xlsx`);
}

/** 项目视图操作方法 */
function viewProjectDetails(row) {
  proxy.$router.push({
    path: '/osc/project/detail',
    query: { id: row.id }
  });
}

function manageProjectMembers(row) {
  proxy.$router.push({
    path: '/osc/projectMember',
    query: { projectId: row.id }
  });
}

function viewProjectChart(row) {
  proxy.$modal.msgInfo('图表功能开发中...');
}

function exportProjectData(row) {
  proxy.download('osc/projectMember/export', {
    projectId: row.id
  }, `project_${row.projectCode}_members_${new Date().getTime()}.xlsx`);
}

function viewAllMembers(project, roleGroup) {
  proxy.$modal.msgInfo(`查看 ${project.projectName} 的所有 ${roleGroup.roleName} 成员`);
}

/** 查看成员贡献记录 */
async function viewMemberContribution(member, project = null) {
  currentMember.value = member;
  currentProject.value = project;
  contributionDialogVisible.value = true;
  
  // 重置查询参数
  contributionQuery.value = {
    pageNum: 1,
    pageSize: 10,
    contributionType: undefined,
    dateRange: [],
    startDate: undefined,
    endDate: undefined,
    memberId: member.memberId || member.id,
    projectId: project?.id
  };
  
  await getContributionList();
  await getContributionStats();
}

/** 获取贡献记录列表 */
async function getContributionList() {
  contributionLoading.value = true;
  try {
    const params = {
      ...contributionQuery.value,
      memberId: currentMember.value?.memberId || currentMember.value?.id
    };
    if (currentProject.value) {
      params.projectId = currentProject.value.id;
    }
    
    const response = await listContribution(params);
    contributionList.value = response.rows || [];
    contributionTotal.value = response.total || 0;
  } catch (error) {
    console.error('获取贡献记录失败:', error);
    proxy.$modal.msgError('获取贡献记录失败');
  } finally {
    contributionLoading.value = false;
  }
}

/** 获取贡献统计 */
async function getContributionStats() {
  try {
    // 模拟统计数据，实际应该调用API
    contributionStats.value = {
      totalContributions: 15,
      totalPoints: 320,
      thisMonthContributions: 5,
      lastActivityDays: 3
    };
  } catch (error) {
    console.error('获取贡献统计失败:', error);
  }
}

/** 搜索贡献记录 */
function searchContributions() {
  contributionQuery.value.pageNum = 1;
  getContributionList();
}

/** 重置贡献查询 */
function resetContributionQuery() {
  contributionQuery.value = {
    pageNum: 1,
    pageSize: 10,
    contributionType: undefined,
    dateRange: [],
    startDate: undefined,
    endDate: undefined,
    memberId: currentMember.value?.memberId || currentMember.value?.id,
    projectId: currentProject.value?.id
  };
  getContributionList();
}

/** 处理日期范围变化 */
function handleDateRangeChange(dates) {
  if (dates && dates.length === 2) {
    contributionQuery.value.startDate = dates[0];
    contributionQuery.value.endDate = dates[1];
  } else {
    contributionQuery.value.startDate = undefined;
    contributionQuery.value.endDate = undefined;
  }
}

/** 获取成员头像 */
function getMemberAvatar(member) {
  if (!member) return '';
  return member.avatar || `https://api.dicebear.com/7.x/initials/svg?seed=${member.memberName}`;
}

/** 获取成员姓名首字母 */
function getMemberInitial(member) {
  if (!member) return '?';
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

/** 获取贡献类型名称 */
function getContributionTypeName(type) {
  const typeMap = {
    '0': '代码提交',
    '1': '问题修复',
    '2': '文档贡献',
    '3': '回答问题',
    '4': '其他'
  };
  return typeMap[type] || '未知';
}

/** 获取贡献类型标签类型 */
function getContributionTypeTagType(type) {
  const typeMap = {
    '0': 'primary',
    '1': 'success',
    '2': 'warning',
    '3': 'info',
    '4': 'danger'
  };
  return typeMap[type] || 'info';
}

// 生命周期
onMounted(() => {
  if (currentView.value === 'project') {
    getProjectList();
  } else {
    getList();
  }
});
</script>

<style scoped>
/* 头部样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.header-actions .el-button.active {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

.search-form {
  margin-bottom: 20px;
}

/* 项目视图表格样式 */
.project-visualization-table {
  margin-bottom: 20px;
}

.project-info-cell {
  text-align: left;
  padding: 8px 0;
}

.project-name-main {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
  margin-bottom: 4px;
}

.project-code-sub {
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

.project-desc-sub {
  font-size: 11px;
  color: #c0c4cc;
  line-height: 1.4;
}

/* 负责人信息样式 */
.leader-info-cell {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.leader-avatar {
  border: 2px solid #f56c6c;
}

.leader-details {
  text-align: center;
}

.leader-name {
  font-size: 12px;
  color: #303133;
  font-weight: 500;
  margin-bottom: 2px;
}

.leader-role {
  font-size: 10px;
  color: #f56c6c;
}

/* 人员关联图样式 */
.member-visualization-cell {
  padding: 12px;
  background: linear-gradient(135deg, #f8f9fa 0%, #f1f3f4 100%);
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.role-groups-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 12px;
}

.role-group-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 140px;
  padding: 8px;
  border-radius: 6px;
  background: rgba(255, 255, 255, 0.7);
  border: 1px solid transparent;
  transition: all 0.3s ease;
}

.role-group-item:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: #e4e7ed;
  transform: translateY(-2px);
}

.role-group-item.role-1 {
  border-left: 3px solid #f56c6c;
}

.role-group-item.role-2 {
  border-left: 3px solid #e6a23c;
}

.role-group-item.role-3 {
  border-left: 3px solid #409eff;
}

.role-group-item.role-4 {
  border-left: 3px solid #67c23a;
}

.role-group-item.role-0 {
  border-left: 3px solid #909399;
}

.role-header-item {
  display: flex;
  align-items: center;
  justify-content: center;
}

.role-tag-item {
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 12px;
  color: white !important;
  border: none;
  font-weight: 500;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.members-avatars {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-wrap: wrap;
  justify-content: center;
}

.member-avatar-item {
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.member-avatar-item:hover {
  transform: scale(1.15);
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.more-members-count {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #e4e7ed 0%, #d3d4d6 100%);
  border-radius: 50%;
  font-size: 10px;
  color: #606266;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.more-members-count:hover {
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  color: white;
  transform: scale(1.1);
}

/* 统计信息样式 */
.stats-info-row {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #f0f2f5;
}

.stat-tag {
  font-size: 11px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border-radius: 12px;
  font-weight: 500;
}

.stat-tag .el-icon {
  font-size: 12px;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 4px;
}

.action-buttons .el-button {
  padding: 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.action-buttons .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 贡献记录对话框样式 */
.contribution-record-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.member-info-card,
.contribution-stats-card,
.contribution-filter-card,
.contribution-list-card {
  margin-bottom: 16px;
}

.member-name-with-avatar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.member-name-text {
  font-weight: 600;
  color: #303133;
}

/* 贡献统计卡片样式 */
.stat-item {
  text-align: center;
  padding: 16px;
  background: linear-gradient(135deg, #f8f9fa 0%, #f1f3f4 100%);
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border-color: #409eff;
  transform: translateY(-2px);
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #606266;
  font-weight: 500;
}

/* 贡献内容样式 */
.contribution-content {
  padding: 4px 0;
}

.content-text {
  color: #303133;
  line-height: 1.5;
  margin-bottom: 4px;
}

.content-url {
  display: flex;
  align-items: center;
  gap: 4px;
}

.content-url .el-link {
  font-size: 12px;
}

.contribution-pagination {
  margin-top: 16px;
}

/* 原有样式保持 */
.project-info {
  text-align: left;
}

.member-info {
  display: flex;
  align-items: center;
  text-align: left;
}

.member-details {
  margin-left: 8px;
}

.text-gray-500 {
  color: #6b7280;
}

.text-sm {
  font-size: 0.875rem;
}

.mr-2 {
  margin-right: 0.5rem;
}

.mb-8 {
  margin-bottom: 2rem;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .role-groups-container {
    flex-direction: column;
    gap: 12px;
  }
  
  .role-group-item {
    min-width: auto;
    width: 100%;
  }
  
  .stats-info-row {
    justify-content: center;
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .member-visualization-cell {
    padding: 8px;
  }
  
  .role-groups-container {
    gap: 8px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 2px;
  }
  
  .header-actions {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
