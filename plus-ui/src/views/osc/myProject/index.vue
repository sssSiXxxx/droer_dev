<template>
  <div class="p-2">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span class="title">我的创建</span>
          <el-button type="primary" @click="router.push('/osc/projectCreate')">
            <el-icon><Plus /></el-icon>
            创建项目
          </el-button>
        </div>
      </template>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="queryParams.projectName" placeholder="请输入项目名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="项目状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择项目状态" clearable style="width: 200px">
            <el-option v-for="dict in projectStatusOptions" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 项目列表 -->
      <el-table v-loading="loading" :data="projectList">
        <el-table-column label="项目名称" align="center" prop="projectName" min-width="120" />
        <el-table-column label="项目描述" align="center" prop="description" :show-overflow-tooltip="true" min-width="200" />
        <el-table-column label="项目状态" align="center" prop="status" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="技术栈" align="center" prop="techStack" :show-overflow-tooltip="true" min-width="150">
          <template #default="scope">
            <el-tag v-for="tech in getTechStackLabels(scope.row.techStack)" :key="tech" class="mx-1" size="small">
              {{ tech }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="编程语言" align="center" prop="programmingLanguage" :show-overflow-tooltip="true" min-width="150">
          <template #default="scope">
            <el-tag v-for="lang in getProgrammingLanguageLabels(scope.row.programmingLanguage)" :key="lang" class="mx-1" type="success" size="small">
              {{ lang }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最后更新时间" align="center" prop="updateTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.updateTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button type="primary" link icon="View" @click="handleView(scope.row)">查看</el-button>
            <el-button v-if="scope.row.status === '5'" type="warning" link icon="Edit" @click="handleEdit(scope.row)">修改</el-button>
            <el-button v-if="scope.row.status === '5'" type="info" link icon="Refresh" @click="handleResubmit(scope.row)">重新提交</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
  </div>
</template>

<script setup name="MyProject" lang="ts">
import { listProject, getProject, updateProject } from '@/api/osc/project';
import { ProjectVO, ProjectQuery } from '@/api/osc/project/types';
import { getCurrentInstance, ref, watch, onMounted, toRefs, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/modules/user';
import { parseTime } from '@/utils/ruoyi';
import { ElMessageBox } from 'element-plus';

const { proxy } = getCurrentInstance() as any;
const router = useRouter();
const userStore = useUserStore();

const loading = ref(false);
const total = ref(0);
const projectList = ref<ProjectVO[]>([]);

// 使用系统字典数据
const { osc_project_tech, osc_project_prolan } = toRefs<any>(proxy?.useDict('osc_project_tech', 'osc_project_prolan'));

// 技术栈字典数据（带备用方案）
const techStackDict = computed(() => {
  if (osc_project_tech.value && osc_project_tech.value.length > 0) {
    // 合并系统字典和补充数据，确保所有值都有对应标签
    const systemDict = osc_project_tech.value;
    const supplementDict = [
      { label: 'Dubbo', value: '26' },
      { label: 'Nacos', value: '27' },
      { label: 'Seata', value: '28' },
      { label: 'Sentinel', value: '29' },
      { label: 'Spring Security', value: '30' },
      { label: 'Sa-Token', value: '31' },
      { label: 'MySQL', value: '32' },
      { label: 'PostgreSQL', value: '33' },
      { label: 'MongoDB', value: '34' },
      { label: 'RocketMQ', value: '35' },
      { label: 'Kubernetes', value: '36' },
      { label: 'Vue 3', value: '37' },
      { label: 'React', value: '38' },
      { label: 'TypeScript', value: '39' },
      { label: 'SkyWalking', value: '40' },
      { label: 'Maven', value: '41' },
      { label: 'Jenkins', value: '42' },
      { label: 'OAuth 2.0', value: '43' },
      { label: 'JWT', value: '44' },
      { label: 'JUnit 5', value: '45' }
    ];

    // 检查系统字典中是否已包含这些值
    const missingValues = supplementDict.filter((supp) => !systemDict.find((sys) => sys.value === supp.value));

    return [...systemDict, ...missingValues];
  }

  // 如果为空，返回完整的技术栈列表（使用与系统字典一致的结构）
  return [
    { label: 'Spring Boot', value: '1' },
    { label: 'Spring Cloud', value: '2' },
    { label: 'Docker', value: '3' },
    { label: 'MyBatis-Plus', value: '4' },
    { label: 'Microservices', value: '5' },
    { label: 'DevOps', value: '6' },
    { label: 'Cloud Native', value: '7' },
    { label: 'Cloud Computing', value: '8' },
    { label: 'Distributed Systems', value: '9' },
    { label: 'Database', value: '10' },
    { label: 'NoSQL', value: '11' },
    { label: 'Elasticsearch', value: '12' },
    { label: 'Apache Kafka', value: '13' },
    { label: 'Redis', value: '14' },
    { label: 'Nginx', value: '15' },
    { label: 'Apache Mesos', value: '16' },
    { label: 'RabbitMQ', value: '17' },
    { label: 'Prometheus', value: '18' },
    { label: 'Grafana', value: '19' },
    { label: 'Netty', value: '20' },
    { label: 'gRPC', value: '21' },
    { label: 'Zookeeper', value: '22' },
    { label: 'Machine Learning', value: '23' },
    { label: 'Big Data', value: '24' },
    { label: 'Hadoop', value: '25' },
    { label: 'Dubbo', value: '26' },
    { label: 'Nacos', value: '27' },
    { label: 'Seata', value: '28' },
    { label: 'Sentinel', value: '29' },
    { label: 'Spring Security', value: '30' },
    { label: 'Sa-Token', value: '31' },
    { label: 'MySQL', value: '32' },
    { label: 'PostgreSQL', value: '33' },
    { label: 'MongoDB', value: '34' },
    { label: 'RocketMQ', value: '35' },
    { label: 'Kubernetes', value: '36' },
    { label: 'Vue 3', value: '37' },
    { label: 'React', value: '38' },
    { label: 'TypeScript', value: '39' },
    { label: 'SkyWalking', value: '40' },
    { label: 'Maven', value: '41' },
    { label: 'Jenkins', value: '42' },
    { label: 'OAuth 2.0', value: '43' },
    { label: 'JWT', value: '44' },
    { label: 'JUnit 5', value: '45' }
  ];
});

// 编程语言字典数据（带备用方案）
const programmingLanguageDict = computed(() => {
  if (osc_project_prolan.value && osc_project_prolan.value.length > 0) {
    return osc_project_prolan.value;
  }
  // 如果为空，返回完整的编程语言列表（使用与系统字典一致的结构）
  return [
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
});

// 项目状态选项
const projectStatusOptions = [
  { label: '进行中', value: '2' },
  { label: '已完成', value: '3' },
  { label: '已驳回', value: '5' }
];

// 查询参数
const queryParams = ref<ProjectQuery>({
  pageNum: 1,
  pageSize: 10,
  projectName: undefined,
  status: undefined,
  createBy: userStore.userId,
  params: {
    statusList: ['2', '3', '5'] // 包含进行中、已完成、已驳回状态
  }
});

// 保证切换用户时 createBy 始终为最新
watch(
  () => userStore.userId,
  (newUserId) => {
    queryParams.value.createBy = newUserId;
  }
);

/** 获取技术栈标签 */
const getTechStackLabels = (techStack: string) => {
  if (!techStack) return [];
  const values = techStack.split(',');
  return values.map((value) => {
    const trimmedValue = value.trim();
    // 先尝试按value查找
    let dict = techStackDict.value.find((item: any) => item.value === trimmedValue);
    // 如果没找到，再尝试按label查找（处理一些特殊值）
    if (!dict) {
      dict = techStackDict.value.find((item: any) => item.label === trimmedValue);
    }
    return dict ? dict.label : value;
  });
};

/** 获取编程语言标签 */
const getProgrammingLanguageLabels = (programmingLanguage: string) => {
  if (!programmingLanguage) return [];
  const values = programmingLanguage.split(',');
  return values.map((value) => {
    const trimmedValue = value.trim();
    // 先尝试按value查找
    let dict = programmingLanguageDict.value.find((item: any) => item.value === trimmedValue);
    // 如果没找到，再尝试按label查找（处理一些特殊值）
    if (!dict) {
      dict = programmingLanguageDict.value.find((item: any) => item.label === trimmedValue);
    }
    return dict ? dict.label : value;
  });
};

/** 查询项目列表 */
const getList = async () => {
  loading.value = true;
  try {
    if (!queryParams.value.createBy) {
      queryParams.value.createBy = userStore.userId;
    }
    const res = await listProject(queryParams.value);
    projectList.value = res.rows;
    total.value = res.total;
  } finally {
    loading.value = false;
  }
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

/** 查看按钮操作 */
const handleView = async (row: ProjectVO) => {
  try {
    console.log('查看项目:', row);
    const res = await getProject(row.projectId);
    const projectData = res.data;
    console.log('项目数据:', projectData);

    // 获取字典标签的辅助函数
    const getDictLabelFromValue = (dictList: any[], value: string) => {
      // 先尝试按value查找
      let dict = dictList.find((item) => item.value === value);
      // 如果没找到，再尝试按label查找（处理一些特殊值）
      if (!dict) {
        dict = dictList.find((item) => item.label === value);
      }
      return dict ? dict.label : value;
    };

    // 处理多选字段的显示
    const formatMultiSelect = (value: string, dictList: any[]) => {
      if (!value) return '暂无信息';
      return value
        .split(',')
        .map((item) => getDictLabelFromValue(dictList, item.trim()))
        .join('、');
    };

    // 提前计算标签文本
    const techStackText = formatMultiSelect(projectData.techStack, techStackDict.value);
    const programmingLanguageText = formatMultiSelect(projectData.programmingLanguage, programmingLanguageDict.value);

    // 创建项目详情对话框（与项目列表页面一致）
    // 使用项目列表样式的项目详情
    ElMessageBox.alert(
      `
      <div style="text-align: left; width: 100%; margin: 0; padding: 0;">
        <h3 style="margin: 0 0 20px 0; color: #333; text-align: center; background-color: #fdfde7; padding: 15px; border-radius: 6px;">${projectData.projectName}</h3>
        
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">项目描述：</strong>
          <p style="margin: 5px 0; color: #666;">${projectData.description || '暂无描述'}</p>
        </div>
        
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">技术栈：</strong>
          <p style="margin: 5px 0; color: #666;">${techStackText}</p>
        </div>
        
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">编程语言：</strong>
          <p style="margin: 5px 0; color: #666;">${programmingLanguageText}</p>
        </div>
        
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">代码仓库：</strong>
          <p style="margin: 5px 0;">
            <a href="${projectData.repositoryUrl}" target="_blank" style="color: #409EFF;">${projectData.repositoryUrl || '暂无仓库地址'}</a>
          </p>
        </div>
        
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">项目网站：</strong>
          <p style="margin: 5px 0;">
            <a href="${projectData.websiteUrl}" target="_blank" style="color: #409EFF;">${projectData.websiteUrl || '暂无网站地址'}</a>
          </p>
        </div>
        
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">联系方式：</strong>
          <p style="margin: 5px 0; color: #666;">${projectData.contactInfo || '暂无联系方式'}</p>
        </div>
        
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: white; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">项目状态：</strong>
          <p style="margin: 5px 0; color: #666;">${getStatusLabel(projectData.status) || '暂无状态信息'}</p>
        </div>
        
        <div style="margin: 0 0 2px 0; padding: 12px; background-color: #f0f9f0; width: 100%; box-sizing: border-box;">
          <strong style="color: #333;">备注：</strong>
          <p style="margin: 5px 0; color: #666;">${projectData.remark || '暂无备注'}</p>
        </div>
      </div>
    `,
      '项目详情',
      {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定',
        customClass: 'project-detail-dialog'
      }
    );
  } catch (error) {
    console.error('获取项目详情失败:', error);
  }
};

/** 修改按钮操作 */
const handleEdit = (row: ProjectVO) => {
  router.push({
    path: '/osc/projectCreate',
    query: { projectId: row.projectId }
  });
};

/** 重新提交按钮操作 */
const handleResubmit = async (row: ProjectVO) => {
  try {
    await proxy?.$modal.confirm('确认重新提交该项目进行审核吗？');

    // 更新项目状态为待审核
    const result = await updateProject({
      projectId: row.projectId,
      status: '1' // 设置为待审核状态
    });

    if (result) {
      proxy?.$modal.msgSuccess('重新提交成功，项目已进入审核队列');
      getList(); // 刷新列表
    } else {
      proxy?.$modal.msgError('重新提交失败');
    }
  } catch (error) {
    console.error('重新提交失败:', error);
    proxy?.$modal.msgError('重新提交失败');
  }
};

/** 获取状态标签 */
const getStatusLabel = (status: string) => {
  const statusMap = {
    '0': '草稿',
    '1': '待审核',
    '2': '进行中',
    '3': '已完成',
    '4': '已暂停',
    '5': '已驳回'
  } as Record<string, string>;
  return statusMap[status] || status;
};

/** 获取状态类型 */
const getStatusType = (status: string) => {
  const typeMap = {
    '0': 'info',
    '1': 'warning',
    '2': 'primary',
    '3': 'success',
    '4': 'warning',
    '5': 'danger'
  } as Record<string, 'success' | 'warning' | 'info' | 'primary' | 'danger'>;
  return (typeMap[status] || 'info') as 'success' | 'warning' | 'info' | 'primary' | 'danger';
};

onMounted(() => {
  if (!queryParams.value.createBy) {
    queryParams.value.createBy = userStore.userId;
  }
  getList();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.mx-1 {
  margin: 0 4px;
}
</style>
