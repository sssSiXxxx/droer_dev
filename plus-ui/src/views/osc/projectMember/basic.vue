<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>项目成员管理 - 基础测试页面</span>
        </div>
      </template>

      <div class="basic-content">
        <el-alert title="页面状态" description="这是一个基础测试页面，用于验证页面是否能正常显示" type="info" show-icon :closable="false" />

        <el-divider />

        <el-row :gutter="20">
          <el-col :span="12">
            <el-card>
              <template #header>
                <span>基本信息</span>
              </template>
              <p><strong>当前时间:</strong> {{ currentTime }}</p>
              <p><strong>页面状态:</strong> 已加载</p>
              <p><strong>测试按钮:</strong></p>
              <el-button type="primary" @click="testClick">点击测试</el-button>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card>
              <template #header>
                <span>API测试</span>
              </template>
              <el-button @click="testAPI">测试API</el-button>
              <p v-if="apiResult"><strong>结果:</strong> {{ apiResult }}</p>
            </el-card>
          </el-col>
        </el-row>

        <el-divider />

        <el-card>
          <template #header>
            <span>简单表格</span>
          </template>
          <el-table :data="tableData" border style="width: 100%">
            <el-table-column label="ID" prop="id" width="80" />
            <el-table-column label="名称" prop="name" width="120" />
            <el-table-column label="描述" prop="description" />
          </el-table>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup name="ProjectMemberBasic" lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';

const currentTime = ref(new Date().toLocaleString());
const apiResult = ref<string>('');
const tableData = ref([
  { id: 1, name: '测试项目1', description: '这是一个测试项目' },
  { id: 2, name: '测试项目2', description: '这是另一个测试项目' },
  { id: 3, name: '测试项目3', description: '第三个测试项目' }
]);

// 测试点击
function testClick() {
  ElMessage.success('按钮点击成功！');
}

// 测试API
async function testAPI() {
  try {
    apiResult.value = 'API测试成功';
    ElMessage.success('API测试成功');
  } catch (error: any) {
    apiResult.value = `API测试失败: ${error.message}`;
    ElMessage.error('API测试失败');
  }
}

onMounted(() => {
  console.log('基础测试页面已加载');
  ElMessage.info('基础测试页面已加载');
});
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.basic-content {
  margin-top: 20px;
}

.el-divider {
  margin: 20px 0;
}
</style>
