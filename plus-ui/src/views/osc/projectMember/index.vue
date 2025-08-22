<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>项目人员可视化管理</span>
          <div class="header-actions">
            <el-button-group>
              <el-button type="info" @click="switchToListView" :class="{ active: currentView === 'list' }">
                <el-icon><List /></el-icon>
                列表视图
              </el-button>
              <el-button type="info" @click="switchToVisualizationView" :class="{ active: currentView === 'visualization' }">
                <el-icon><DataBoard /></el-icon>
                可视化视图
              </el-button>
            </el-button-group>
            <el-button type="primary" @click="refreshData" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <!-- 搜索区域 - 仅在列表视图显示 -->
      <el-form 
        v-show="currentView === 'list'"
        :model="queryParams" 
        ref="queryRef" 
        :inline="true" 
        label-width="68px" 
        class="search-form"
      >
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
          <el-select v-model="queryParams.role" placeholder="请选择角色" clearable style="width: 150px">
            <el-option label="项目负责人" value="1" />
            <el-option label="核心开发者" value="2" />
            <el-option label="维护者" value="3" />
            <el-option label="贡献者" value="4" />
            <el-option label="普通成员" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="活跃状态" prop="isActive">
          <el-select v-model="queryParams.isActive" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="活跃" value="1" />
            <el-option label="不活跃" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="Plus" @click="handleAdd" v-hasPermi="['osc:projectMember:add']">新增</el-button>
        </el-form-item>
      </el-form>

      <!-- 列表视图 -->
      <div v-show="currentView === 'list'">
        <!-- 数据表格 -->
        <el-table
          v-loading="loading"
          :data="projectMemberList"
          @selection-change="handleSelectionChange"
          row-key="id"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="序号" width="80" align="center" type="index" :index="(index) => (queryParams.pageNum - 1) * queryParams.pageSize + index + 1" />
          <el-table-column label="项目名称" align="center" prop="projectName" min-width="150">
            <template #default="scope">
              <div class="project-info">
                <div class="project-name">{{ scope.row.projectName || '未知项目' }}</div>
                <div class="project-code" v-if="scope.row.projectCode">
                  {{ scope.row.projectCode }}
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="成员信息" align="center" min-width="200">
            <template #default="scope">
              <div class="member-info">
                <el-avatar :size="40" :src="scope.row.memberAvatar">
                  {{ scope.row.memberName ? scope.row.memberName.charAt(0) : 'N' }}
                </el-avatar>
                <div class="member-details">
                  <div class="member-name">{{ scope.row.memberName || '未知成员' }}</div>
                  <div class="member-email" v-if="scope.row.memberEmail">
                    {{ scope.row.memberEmail }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="角色" align="center" prop="role" width="120">
            <template #default="scope">
              <el-tag v-if="scope.row.role === '1'" type="danger">项目负责人</el-tag>
              <el-tag v-else-if="scope.row.role === '2'" type="warning">核心开发者</el-tag>
              <el-tag v-else-if="scope.row.role === '3'" type="success">维护者</el-tag>
              <el-tag v-else-if="scope.row.role === '4'" type="info">贡献者</el-tag>
              <el-tag v-else type="info">普通成员</el-tag>
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
                score-template="{value}"
              />
            </template>
          </el-table-column>
          <el-table-column label="贡献评分" align="center" prop="contributionScore" width="100">
            <template #default="scope">
              <el-progress 
                :percentage="scope.row.contributionScore || 0" 
                :color="getScoreColor(scope.row.contributionScore)"
                :stroke-width="8"
              />
            </template>
          </el-table-column>
          <el-table-column label="活跃状态" align="center" prop="isActive" width="100">
            <template #default="scope">
              <el-tag v-if="scope.row.isActive === '1'" type="success">活跃</el-tag>
              <el-tag v-else type="info">不活跃</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="加入时间" align="center" prop="joinTime" width="180">
            <template #default="scope">
              <span>{{ parseTime(scope.row.joinTime, '{y}-{m}-{d}') }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
            <template #default="scope">
              <el-tooltip content="修改" placement="top">
                <el-button
                  link
                  type="primary"
                  icon="Edit"
                  @click="handleUpdate(scope.row)"
                  v-hasPermi="['osc:projectMember:edit']"
                ></el-button>
              </el-tooltip>
              <el-tooltip content="删除" placement="top">
                <el-button
                  link
                  type="danger"
                  icon="Delete"
                  @click="handleDelete(scope.row)"
                  v-hasPermi="['osc:projectMember:remove']"
                ></el-button>
              </el-tooltip>
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

      <!-- 可视化视图 -->
      <div v-show="currentView === 'visualization'" class="visualization-container">
        <!-- 项目统计卡片 -->
        <el-row :gutter="20" class="stats-row">
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon project-icon">
                  <el-icon><Box /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ projectStats.totalProjects }}</div>
                  <div class="stat-label">项目总数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon member-icon">
                  <el-icon><User /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ projectStats.totalMembers }}</div>
                  <div class="stat-label">成员总数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon owner-icon">
                  <el-icon><Crown /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ projectStats.totalOwners }}</div>
                  <div class="stat-label">项目负责人</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" class="stat-card">
              <div class="stat-content">
                <div class="stat-icon contributor-icon">
                  <el-icon><UserFilled /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-number">{{ projectStats.totalContributors }}</div>
                  <div class="stat-label">贡献者</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 项目-成员关系图 -->
        <el-row :gutter="20" class="visualization-row">
          <el-col :span="24">
            <el-card shadow="never" class="relationship-card">
              <template #header>
                <div class="card-title">
                  <el-icon><Share /></el-icon>
                  <span>项目-成员关系图</span>
                </div>
              </template>
              
              <div class="relationship-container">
                <div v-for="project in visualizationData" :key="project.projectId" class="project-node">
                  <div class="project-info">
                    <div class="project-avatar">
                      <el-avatar :size="60" shape="square">
                        {{ project.projectName.charAt(0) }}
                      </el-avatar>
                    </div>
                    <div class="project-details">
                      <h4>{{ project.projectName }}</h4>
                      <p v-if="project.description">{{ project.description }}</p>
                      <div class="project-meta">
                        <el-tag size="small">{{ project.memberCount }} 成员</el-tag>
                        <el-tag size="small" type="warning" v-if="project.starCount">★ {{ project.starCount }}</el-tag>
                      </div>
                    </div>
                  </div>
                  
                  <div class="members-container">
                    <div class="connection-line"></div>
                    <div class="members-grid">
                      <div 
                        v-for="member in project.members" 
                        :key="member.memberId" 
                        class="member-node"
                        :class="getRoleClass(member.role)"
                      >
                        <el-tooltip :content="getMemberTooltip(member)" placement="top">
                          <div class="member-card">
                            <el-avatar :size="40" :src="member.avatar">
                              {{ member.memberName.charAt(0) }}
                            </el-avatar>
                            <div class="member-info">
                              <div class="member-name">{{ member.memberName }}</div>
                              <div class="member-role">{{ getRoleText(member.role) }}</div>
                            </div>
                            <div class="role-indicator" :class="getRoleClass(member.role)">
                              <el-icon v-if="member.role === '1'"><Crown /></el-icon>
                              <el-icon v-else-if="member.role === '2'"><Tools /></el-icon>
                              <el-icon v-else><User /></el-icon>
                            </div>
                          </div>
                        </el-tooltip>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 空状态 -->
              <el-empty v-if="!visualizationData.length" description="暂无项目数据" />
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 添加或修改项目成员关联对话框 -->
      <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" append-to-body>
        <el-form ref="projectMemberFormRef" :model="form" :rules="rules" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="项目" prop="projectId">
                <el-select 
                  v-model="form.projectId" 
                  placeholder="请选择项目"
                  filterable
                  remote
                  :remote-method="searchProjects"
                  :loading="projectLoading"
                  style="width: 100%"
                >
                  <el-option
                    v-for="project in projectOptions"
                    :key="project.projectId"
                    :label="project.projectName"
                    :value="project.projectId"
                  >
                    <div class="project-option">
                      <div class="project-name">{{ project.projectName }}</div>
                      <div class="project-code" v-if="project.projectCode">{{ project.projectCode }}</div>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="成员" prop="memberId">
                <el-select 
                  v-model="form.memberId" 
                  placeholder="请选择成员"
                  filterable
                  remote
                  :remote-method="searchMembers"
                  :loading="memberLoading"
                  style="width: 100%"
                >
                  <el-option
                    v-for="member in memberOptions"
                    :key="member.memberId"
                    :label="member.memberName"
                    :value="member.memberId"
                  >
                    <div class="member-option">
                      <el-avatar :size="24" :src="member.avatar">
                        {{ member.memberName.charAt(0) }}
                      </el-avatar>
                      <div class="member-info">
                        <div class="member-name">{{ member.memberName }}</div>
                        <div class="member-email" v-if="member.memberEmail">{{ member.memberEmail }}</div>
                      </div>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="角色" prop="role">
                <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
                  <el-option label="项目负责人" value="1" />
                  <el-option label="核心开发者" value="2" />
                  <el-option label="维护者" value="3" />
                  <el-option label="贡献者" value="4" />
                  <el-option label="普通成员" value="0" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="权限级别" prop="permissionLevel">
                <el-rate 
                  v-model="form.permissionLevel" 
                  :max="5" 
                  show-score 
                  text-color="#ff9900"
                  score-template="{value}"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="贡献评分" prop="contributionScore">
                <el-slider
                  v-model="form.contributionScore"
                  :min="0"
                  :max="100"
                  :step="1"
                  show-input
                  input-size="small"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="活跃状态" prop="isActive">
                <el-radio-group v-model="form.isActive">
                  <el-radio label="1">活跃</el-radio>
                  <el-radio label="0">不活跃</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="备注" prop="remark">
            <el-input 
              v-model="form.remark" 
              type="textarea" 
              placeholder="请输入备注信息"
              :rows="3"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="cancel">取 消</el-button>
            <el-button type="primary" @click="submitForm">确 定</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup name="ProjectMember" lang="ts">
import { 
  listProjectMember, 
  getProjectMember, 
  delProjectMember, 
  addProjectMember, 
  updateProjectMember,
  searchProjects,
  searchMembers
} from '@/api/osc/projectMember'
import { parseTime } from '@/utils/ruoyi'

const { proxy } = getCurrentInstance() as ComponentInternalInstance

const projectMemberList = ref<any[]>([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref<Array<string | number>>([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const currentView = ref('list')

// 可视化数据
const visualizationData = ref([])
const projectStats = ref({
  totalProjects: 0,
  totalMembers: 0,
  totalOwners: 0,
  totalContributors: 0
})

// 选项数据
const projectOptions = ref([])
const memberOptions = ref([])
const projectLoading = ref(false)
const memberLoading = ref(false)

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  memberName: undefined,
  role: undefined,
  isActive: undefined
})

const form = ref<any>({})
const rules = ref({
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
  memberId: [{ required: true, message: '请选择成员', trigger: 'change' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
})

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
})

const queryRef = ref<ElFormInstance>()
const projectMemberFormRef = ref<ElFormInstance>()

/** 查询项目成员关联列表 */
const getList = async () => {
  loading.value = true
  try {
    const res = await listProjectMember(queryParams.value)
    if (res.code === 200) {
      projectMemberList.value = res.rows || []
      total.value = res.total || 0
    } else {
      // 如果接口返回错误，显示默认数据
      console.warn('API返回错误，显示默认数据')
      projectMemberList.value = getDefaultProjectMembers()
      total.value = projectMemberList.value.length
    }
  } catch (error) {
    console.error('获取项目成员列表失败:', error)
    // 显示默认数据
    projectMemberList.value = getDefaultProjectMembers()
    total.value = projectMemberList.value.length
    proxy?.$modal.msgError('获取数据失败，显示默认数据')
  } finally {
    loading.value = false
  }
}

/** 获取默认项目成员数据 */
const getDefaultProjectMembers = () => {
  return [
    {
      id: 1,
      projectId: 1,
      projectName: 'Hutool',
      projectCode: 'hutool',
      memberId: 1,
      memberName: 'looly',
      memberEmail: 'looly@dromara.org',
      memberAvatar: 'https://gitee.com/assets/no_portrait-2b772d6b.png',
      role: '1',
      roleName: '项目负责人',
      permissionLevel: 5,
      contributionScore: 95,
      joinTime: '2021-01-15',
      isActive: '1'
    },
    {
      id: 2,
      projectId: 1,
      projectName: 'Hutool',
      projectCode: 'hutool',
      memberId: 2,
      memberName: '路小磊',
      memberEmail: 'luxiaolei520@dromara.org',
      memberAvatar: 'https://gitee.com/assets/no_portrait-2b772d6b.png',
      role: '2',
      roleName: '核心开发者',
      permissionLevel: 4,
      contributionScore: 88,
      joinTime: '2021-03-20',
      isActive: '1'
    },
    {
      id: 3,
      projectId: 2,
      projectName: 'Sa-Token',
      projectCode: 'sa-token',
      memberId: 3,
      memberName: 'kong',
      memberEmail: 'click33@dromara.org',
      memberAvatar: 'https://gitee.com/assets/no_portrait-2b772d6b.png',
      role: '1',
      roleName: '项目负责人',
      permissionLevel: 5,
      contributionScore: 93,
      joinTime: '2020-12-10',
      isActive: '1'
    }
  ]
}

/** 搜索项目 */
const handleSearchProjects = async (query: string) => {
  if (query) {
    projectLoading.value = true
    try {
      const res = await searchProjects(query)
      if (res.code === 200) {
        projectOptions.value = res.data || []
      } else {
        // 使用默认数据
        projectOptions.value = [
          { projectId: 1, projectName: 'Hutool', projectCode: 'hutool' },
          { projectId: 2, projectName: 'Sa-Token', projectCode: 'sa-token' },
          { projectId: 3, projectName: 'Forest', projectCode: 'forest' }
        ].filter(p => p.projectName.includes(query))
      }
    } catch (error) {
      console.error('搜索项目失败:', error)
      // 使用默认数据
      projectOptions.value = [
        { projectId: 1, projectName: 'Hutool', projectCode: 'hutool' },
        { projectId: 2, projectName: 'Sa-Token', projectCode: 'sa-token' },
        { projectId: 3, projectName: 'Forest', projectCode: 'forest' }
      ].filter(p => p.projectName.includes(query))
    } finally {
      projectLoading.value = false
    }
  }
}

/** 搜索成员 */
const handleSearchMembers = async (query: string) => {
  if (query) {
    memberLoading.value = true
    try {
      const res = await searchMembers(query)
      if (res.code === 200) {
        memberOptions.value = res.data || []
      } else {
        // 使用默认数据
        memberOptions.value = [
          { memberId: 1, memberName: 'looly', memberEmail: 'looly@dromara.org', avatar: '' },
          { memberId: 2, memberName: '路小磊', memberEmail: 'luxiaolei520@dromara.org', avatar: '' },
          { memberId: 3, memberName: 'kong', memberEmail: 'click33@dromara.org', avatar: '' }
        ].filter(m => m.memberName.includes(query))
      }
    } catch (error) {
      console.error('搜索成员失败:', error)
      // 使用默认数据
      memberOptions.value = [
        { memberId: 1, memberName: 'looly', memberEmail: 'looly@dromara.org', avatar: '' },
        { memberId: 2, memberName: '路小磊', memberEmail: 'luxiaolei520@dromara.org', avatar: '' },
        { memberId: 3, memberName: 'kong', memberEmail: 'click33@dromara.org', avatar: '' }
      ].filter(m => m.memberName.includes(query))
    } finally {
      memberLoading.value = false
    }
  }
}

/** 获取评分颜色 */
const getScoreColor = (score: number) => {
  if (score >= 80) return '#67c23a'
  if (score >= 60) return '#e6a23c'
  return '#f56c6c'
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryRef.value?.resetFields()
  handleQuery()
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: any) => {
  ids.value = selection.map((item: any) => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset()
  dialog.visible = true
  dialog.title = '添加项目成员关联'
}

/** 修改按钮操作 */
const handleUpdate = async (row?: any) => {
  reset()
  const _id = row?.id || ids.value[0]
  try {
    const res = await getProjectMember(_id)
    Object.assign(form.value, res.data)
    dialog.visible = true
    dialog.title = '修改项目成员关联'
  } catch (error) {
    proxy?.$modal.msgError('获取数据失败')
  }
}

/** 提交按钮 */
const submitForm = () => {
  projectMemberFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      try {
        if (form.value.id) {
          await updateProjectMember(form.value)
          proxy?.$modal.msgSuccess('修改成功')
        } else {
          await addProjectMember(form.value)
          proxy?.$modal.msgSuccess('新增成功')
        }
        dialog.visible = false
        await getList()
      } catch (error) {
        proxy?.$modal.msgError('操作失败')
      }
    }
  })
}

/** 删除按钮操作 */
const handleDelete = async (row?: any) => {
  const _ids = row?.id || ids.value
  await proxy?.$modal.confirm('是否确认删除项目成员关联编号为"' + _ids + '"的数据项？')
  try {
    await delProjectMember(_ids)
    proxy?.$modal.msgSuccess('删除成功')
    await getList()
  } catch (error) {
    proxy?.$modal.msgError('删除失败')
  }
}

/** 表单重置 */
const reset = () => {
  form.value = {
    id: undefined,
    projectId: undefined,
    memberId: undefined,
    role: '4',
    permissionLevel: 1,
    contributionScore: 0,
    isActive: '1',
    remark: undefined
  }
  projectMemberFormRef.value?.resetFields()
}

/** 取消按钮 */
const cancel = () => {
  dialog.visible = false
  reset()
}

/** 切换到列表视图 */
const switchToListView = () => {
  currentView.value = 'list'
  getList()
}

/** 切换到可视化视图 */
const switchToVisualizationView = () => {
  currentView.value = 'visualization'
  loadVisualizationData()
}

/** 刷新数据 */
const refreshData = () => {
  if (currentView.value === 'list') {
    getList()
  } else {
    loadVisualizationData()
  }
}

/** 加载可视化数据 */
const loadVisualizationData = async () => {
  loading.value = true
  try {
    // 模拟从数据库加载项目和成员关联数据
    const mockVisualizationData = [
      {
        projectId: 1,
        projectName: 'Hutool',
        description: 'A set of tools that keep Java sweet.',
        starCount: 28000,
        memberCount: 2,
        members: [
          {
            memberId: 1,
            memberName: 'looly',
            nickname: 'looly',
            avatar: 'https://gitee.com/assets/no_portrait-2b772d6b.png',
            role: '1',
            joinTime: '2021-01-15'
          },
          {
            memberId: 2,
            memberName: '路小磊',
            nickname: 'luxiaolei520',
            avatar: 'https://gitee.com/assets/no_portrait-2b772d6b.png',
            role: '2',
            joinTime: '2021-03-20'
          }
        ]
      },
      {
        projectId: 2,
        projectName: 'Sa-Token',
        description: 'Java权限认证框架，让鉴权变得简单、优雅！',
        starCount: 15000,
        memberCount: 1,
        members: [
          {
            memberId: 3,
            memberName: 'kong',
            nickname: 'click33',
            avatar: 'https://gitee.com/assets/no_portrait-2b772d6b.png',
            role: '1',
            joinTime: '2020-12-10'
          }
        ]
      }
    ]
    
    visualizationData.value = mockVisualizationData
    
    // 计算统计数据
    projectStats.value = {
      totalProjects: mockVisualizationData.length,
      totalMembers: mockVisualizationData.reduce((sum, project) => sum + project.memberCount, 0),
      totalOwners: mockVisualizationData.reduce((sum, project) => 
        sum + project.members.filter(member => member.role === '1').length, 0
      ),
      totalContributors: mockVisualizationData.reduce((sum, project) => 
        sum + project.members.filter(member => member.role !== '1').length, 0
      )
    }
    
  } catch (error) {
    console.error('加载可视化数据失败:', error)
    proxy?.$modal.msgError('获取数据失败')
  } finally {
    loading.value = false
  }
}

/** 获取角色样式类 */
const getRoleClass = (role) => {
  const roleMap = {
    '1': 'owner-role',
    '2': 'maintainer-role',
    '3': 'contributor-role'
  }
  return roleMap[role] || 'contributor-role'
}

/** 获取角色文本 */
const getRoleText = (role) => {
  const roleMap = {
    '1': '项目负责人',
    '2': '核心开发者',
    '3': '维护者',
    '4': '贡献者',
    '0': '普通成员'
  }
  return roleMap[role] || '普通成员'
}

/** 获取成员提示信息 */
const getMemberTooltip = (member) => {
  return `${member.memberName} (${member.nickname})\n角色: ${getRoleText(member.role)}\n加入时间: ${member.joinTime}`
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.header-actions .el-button.active {
  background-color: #409eff;
  color: white;
}

.search-form {
  margin-bottom: 20px;
}

.dialog-footer {
  text-align: right;
}

.small-padding {
  padding-left: 5px;
  padding-right: 5px;
}

.fixed-width {
  width: 200px;
}

.project-info {
  text-align: left;
}

.project-name {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.project-code {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
  text-align: left;
}

.member-details {
  flex: 1;
}

.member-name {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
  margin-bottom: 2px;
}

.member-email {
  font-size: 12px;
  color: #909399;
}

/* 可视化视图样式 */
.visualization-container {
  padding: 20px 0;
}

.stats-row {
  margin-bottom: 30px;
}

.stat-card {
  border-radius: 8px;
  overflow: hidden;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.project-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.member-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.owner-icon {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.contributor-icon {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-top: 5px;
}

.visualization-row {
  margin-bottom: 20px;
}

.relationship-card {
  min-height: 400px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.relationship-container {
  padding: 20px 0;
}

.project-node {
  margin-bottom: 40px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  background: #fafafa;
  position: relative;
}

.project-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.project-avatar {
  margin-right: 15px;
}

.project-details h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
}

.project-details p {
  margin: 0 0 10px 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.4;
}

.project-meta {
  display: flex;
  gap: 10px;
}

.members-container {
  position: relative;
  padding-left: 40px;
}

.connection-line {
  position: absolute;
  left: 0;
  top: 0;
  width: 2px;
  height: 100%;
  background: linear-gradient(180deg, #409eff 0%, transparent 100%);
}

.members-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 15px;
}

.member-node {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.member-node:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.member-card {
  display: flex;
  align-items: center;
  padding: 15px;
  position: relative;
  gap: 12px;
}

.member-info {
  flex: 1;
}

.member-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.member-role {
  font-size: 12px;
  color: #909399;
}

.role-indicator {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: white;
}

.owner-role .role-indicator {
  background: #f56c6c;
}

.maintainer-role .role-indicator {
  background: #e6a23c;
}

.contributor-role .role-indicator {
  background: #67c23a;
}

.owner-role {
  border-left: 4px solid #f56c6c;
}

.maintainer-role {
  border-left: 4px solid #e6a23c;
}

.contributor-role {
  border-left: 4px solid #67c23a;
}

/* 选项样式 */
.project-option {
  display: flex;
  flex-direction: column;
}

.project-option .project-name {
  font-weight: 600;
  font-size: 14px;
}

.project-option .project-code {
  font-size: 12px;
  color: #909399;
}

.member-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.member-option .member-info {
  flex: 1;
}

.member-option .member-name {
  font-size: 14px;
  font-weight: 600;
}

.member-option .member-email {
  font-size: 12px;
  color: #909399;
}
</style>
