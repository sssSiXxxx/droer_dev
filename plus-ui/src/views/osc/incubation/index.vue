<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span class="card-title">项目孵化流程管理</span>
          <el-button type="primary" @click="handleAddIncubation" v-hasPermi="['osc:incubation:add']">
            <el-icon><Plus /></el-icon>
            新增孵化申请
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px" class="search-form">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="申请类型" prop="applicationType">
          <el-select v-model="queryParams.applicationType" placeholder="请选择申请类型" clearable>
            <el-option label="个人项目加入社区" value="personal_join" />
            <el-option label="社区项目升级" value="community_upgrade" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前阶段" prop="currentStage">
          <el-select v-model="queryParams.currentStage" placeholder="请选择当前阶段" clearable>
            <el-option label="申请提交" value="submitted" />
            <el-option label="初审中" value="initial_review" />
            <el-option label="技术评估" value="tech_review" />
            <el-option label="社区投票" value="community_vote" />
            <el-option label="孵化中" value="incubating" />
            <el-option label="毕业准备" value="graduation_prep" />
            <el-option label="已毕业" value="graduated" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 孵化流程看板 -->
      <div class="incubation-kanban">
        <div class="stage-column" v-for="stage in incubationStages" :key="stage.key">
          <div class="stage-header">
            <h3>{{ stage.label }}</h3>
            <el-badge :value="getStageCount(stage.key)" class="item" />
          </div>
          
          <div class="stage-content">
            <div 
              v-for="item in getStageItems(stage.key)" 
              :key="item.id" 
              class="incubation-item"
              :class="{ 'overdue': isOverdue(item) }"
            >
              <div class="item-header">
                <h4>{{ item.projectName }}</h4>
                <el-dropdown @command="handleItemAction">
                  <el-button type="text" class="action-btn">
                    <el-icon><MoreFilled /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :command="{action: 'view', item}">查看详情</el-dropdown-item>
                      <el-dropdown-item :command="{action: 'edit', item}">编辑</el-dropdown-item>
                      <el-dropdown-item :command="{action: 'next', item}" v-if="canMoveNext(item)">推进下一阶段</el-dropdown-item>
                      <el-dropdown-item :command="{action: 'reject', item}" v-if="canReject(item)">拒绝申请</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
              
              <div class="item-content">
                <p class="project-desc">{{ item.description }}</p>
                <div class="item-meta">
                  <div class="applicant">
                    <el-avatar :size="24" :src="item.applicantAvatar">
                      {{ item.applicantName?.charAt(0) }}
                    </el-avatar>
                    <span>{{ item.applicantName }}</span>
                  </div>
                  <div class="dates">
                    <div class="apply-date">申请: {{ formatDate(item.applyTime) }}</div>
                    <div class="deadline" v-if="item.expectedDeadline">
                      期限: {{ formatDate(item.expectedDeadline) }}
                    </div>
                  </div>
                </div>
                
                <div class="progress-info" v-if="item.progress">
                  <el-progress :percentage="item.progress" :stroke-width="6" />
                </div>
                
                <div class="tags">
                  <el-tag size="small" :type="getApplicationTypeColor(item.applicationType)">
                    {{ getApplicationTypeLabel(item.applicationType) }}
                  </el-tag>
                  <el-tag size="small" v-if="item.priority" :type="getPriorityColor(item.priority)">
                    {{ item.priority }}
                  </el-tag>
                </div>
              </div>
            </div>
            
            <!-- 空状态 -->
            <div v-if="getStageItems(stage.key).length === 0" class="empty-stage">
              <el-empty description="暂无项目" :image-size="60" />
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 孵化申请详情对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="800px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="form.projectName" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请类型" prop="applicationType">
              <el-select v-model="form.applicationType" placeholder="请选择申请类型">
                <el-option label="个人项目加入社区" value="personal_join" />
                <el-option label="社区项目升级" value="community_upgrade" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="项目描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入项目描述" />
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请人" prop="applicantName">
              <el-input v-model="form.applicantName" placeholder="请输入申请人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="contactInfo">
              <el-input v-model="form.contactInfo" placeholder="请输入联系方式" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="代码仓库" prop="repositoryUrl">
              <el-input v-model="form.repositoryUrl" placeholder="请输入代码仓库地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目网站" prop="websiteUrl">
              <el-input v-model="form.websiteUrl" placeholder="请输入项目网站地址" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="技术亮点" prop="techHighlights">
          <el-input v-model="form.techHighlights" type="textarea" :rows="2" placeholder="请描述项目的技术亮点" />
        </el-form-item>
        
        <el-form-item label="社区贡献" prop="communityContribution">
          <el-input v-model="form.communityContribution" type="textarea" :rows="2" placeholder="请描述对社区的贡献" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProjectIncubation" lang="ts">
import { ElMessageBox } from 'element-plus';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

// 孵化阶段定义
const incubationStages = ref([
  { key: 'submitted', label: '申请提交', color: '#909399' },
  { key: 'initial_review', label: '初审中', color: '#E6A23C' },
  { key: 'tech_review', label: '技术评估', color: '#409EFF' },
  { key: 'community_vote', label: '社区投票', color: '#9C27B0' },
  { key: 'incubating', label: '孵化中', color: '#FF9800' },
  { key: 'graduation_prep', label: '毕业准备', color: '#4CAF50' },
  { key: 'graduated', label: '已毕业', color: '#67C23A' },
  { key: 'rejected', label: '已拒绝', color: '#F56C6C' }
]);

// 模拟数据
const incubationItems = ref([
  {
    id: 1,
    projectName: 'AI图像处理工具',
    description: '基于深度学习的图像处理和识别工具库',
    applicationType: 'personal_join',
    applicantName: '张三',
    applicantAvatar: '',
    contactInfo: 'zhangsan@example.com',
    repositoryUrl: 'https://github.com/zhangsan/ai-image-tool',
    websiteUrl: 'https://ai-image-tool.com',
    currentStage: 'tech_review',
    applyTime: '2025-08-20',
    expectedDeadline: '2025-09-20',
    progress: 65,
    priority: '高优先级',
    techHighlights: '采用最新的Transformer架构，支持多种图像格式',
    communityContribution: '计划贡献核心算法和完整文档'
  },
  {
    id: 2,
    projectName: 'microservice-framework',
    description: '轻量级微服务开发框架',
    applicationType: 'community_upgrade',
    applicantName: '李四',
    applicantAvatar: '',
    contactInfo: 'lisi@example.com',
    repositoryUrl: 'https://github.com/community/microservice-framework',
    currentStage: 'incubating',
    applyTime: '2025-07-15',
    expectedDeadline: '2025-10-15',
    progress: 40,
    priority: '中优先级'
  }
]);

const queryParams = ref({
  projectName: '',
  applicationType: '',
  currentStage: ''
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref();

const form = ref({
  projectName: '',
  applicationType: '',
  description: '',
  applicantName: '',
  contactInfo: '',
  repositoryUrl: '',
  websiteUrl: '',
  techHighlights: '',
  communityContribution: ''
});

const rules = ref({
  projectName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
  applicationType: [{ required: true, message: '申请类型不能为空', trigger: 'change' }],
  description: [{ required: true, message: '项目描述不能为空', trigger: 'blur' }],
  applicantName: [{ required: true, message: '申请人不能为空', trigger: 'blur' }]
});

/** 获取各阶段的项目数量 */
const getStageCount = (stage: string) => {
  return incubationItems.value.filter(item => item.currentStage === stage).length;
};

/** 获取各阶段的项目列表 */
const getStageItems = (stage: string) => {
  return incubationItems.value.filter(item => item.currentStage === stage);
};

/** 判断是否超期 */
const isOverdue = (item: any) => {
  if (!item.expectedDeadline) return false;
  return new Date(item.expectedDeadline) < new Date();
};

/** 格式化日期 */
const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString();
};

/** 获取申请类型颜色 */
const getApplicationTypeColor = (type: string) => {
  return type === 'personal_join' ? 'success' : 'warning';
};

/** 获取申请类型标签 */
const getApplicationTypeLabel = (type: string) => {
  return type === 'personal_join' ? '个人项目' : '社区升级';
};

/** 获取优先级颜色 */
const getPriorityColor = (priority: string) => {
  const colorMap = {
    '高优先级': 'danger',
    '中优先级': 'warning',
    '低优先级': 'info'
  };
  return colorMap[priority] || 'info';
};

/** 判断是否可以推进下一阶段 */
const canMoveNext = (item: any) => {
  return !['graduated', 'rejected'].includes(item.currentStage);
};

/** 判断是否可以拒绝 */
const canReject = (item: any) => {
  return !['graduated', 'rejected'].includes(item.currentStage);
};

/** 处理项目操作 */
const handleItemAction = (command: any) => {
  const { action, item } = command;
  
  switch (action) {
    case 'view':
      handleViewDetails(item);
      break;
    case 'edit':
      handleEditItem(item);
      break;
    case 'next':
      handleMoveNext(item);
      break;
    case 'reject':
      handleRejectItem(item);
      break;
  }
};

/** 查看详情 */
const handleViewDetails = (item: any) => {
  ElMessageBox.alert(
    `<div style="text-align: left;">
      <h3>${item.projectName}</h3>
      <p><strong>描述:</strong> ${item.description}</p>
      <p><strong>申请人:</strong> ${item.applicantName}</p>
      <p><strong>申请时间:</strong> ${formatDate(item.applyTime)}</p>
      <p><strong>当前阶段:</strong> ${getStageLabel(item.currentStage)}</p>
      <p><strong>技术亮点:</strong> ${item.techHighlights || '暂无'}</p>
      <p><strong>社区贡献:</strong> ${item.communityContribution || '暂无'}</p>
    </div>`,
    '项目详情',
    { dangerouslyUseHTMLString: true }
  );
};

/** 获取阶段标签 */
const getStageLabel = (stage: string) => {
  const stageObj = incubationStages.value.find(s => s.key === stage);
  return stageObj?.label || stage;
};

/** 编辑项目 */
const handleEditItem = (item: any) => {
  Object.assign(form.value, item);
  dialogTitle.value = '编辑孵化申请';
  dialogVisible.value = true;
};

/** 推进下一阶段 */
const handleMoveNext = async (item: any) => {
  const stageIndex = incubationStages.value.findIndex(s => s.key === item.currentStage);
  if (stageIndex >= 0 && stageIndex < incubationStages.value.length - 2) {
    const nextStage = incubationStages.value[stageIndex + 1];
    await proxy?.$modal.confirm(`确认将项目"${item.projectName}"推进到"${nextStage.label}"阶段吗？`);
    item.currentStage = nextStage.key;
    proxy?.$modal.msgSuccess('阶段推进成功');
  }
};

/** 拒绝申请 */
const handleRejectItem = async (item: any) => {
  await proxy?.$modal.confirm(`确认拒绝项目"${item.projectName}"的申请吗？`);
  item.currentStage = 'rejected';
  proxy?.$modal.msgSuccess('申请已拒绝');
};

/** 新增孵化申请 */
const handleAddIncubation = () => {
  form.value = {
    projectName: '',
    applicationType: '',
    description: '',
    applicantName: '',
    contactInfo: '',
    repositoryUrl: '',
    websiteUrl: '',
    techHighlights: '',
    communityContribution: ''
  };
  dialogTitle.value = '新增孵化申请';
  dialogVisible.value = true;
};

/** 搜索 */
const handleQuery = () => {
  // 实现搜索逻辑
  console.log('搜索条件:', queryParams.value);
};

/** 重置 */
const resetQuery = () => {
  queryParams.value = {
    projectName: '',
    applicationType: '',
    currentStage: ''
  };
};

/** 提交表单 */
const submitForm = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      // 实现保存逻辑
      proxy?.$modal.msgSuccess('保存成功');
      dialogVisible.value = false;
    }
  });
};

/** 取消 */
const cancel = () => {
  dialogVisible.value = false;
};
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
}

.search-form {
  margin-bottom: 20px;
}

.incubation-kanban {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.stage-column {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  min-height: 400px;
}

.stage-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e4e7ed;
}

.stage-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.stage-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.incubation-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border-left: 4px solid #409eff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.incubation-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.incubation-item.overdue {
  border-left-color: #f56c6c;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.item-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.action-btn {
  padding: 4px;
  color: #909399;
}

.project-desc {
  font-size: 13px;
  color: #606266;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.item-meta {
  margin-bottom: 12px;
}

.applicant {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.applicant span {
  font-size: 13px;
  color: #606266;
}

.dates {
  font-size: 12px;
  color: #909399;
}

.deadline {
  margin-top: 4px;
}

.progress-info {
  margin: 12px 0;
}

.tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.empty-stage {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.dialog-footer {
  text-align: right;
}
</style>