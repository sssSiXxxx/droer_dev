<template>
  <div class="p-2">
    <el-row :gutter="20">
      <!-- 开源社区智能搜索树 -->
      <el-col :lg="4" :xs="24" style="">
        <el-card shadow="hover" class="community-search-panel">
          <template #header>
            <div class="flex items-center justify-between">
              <div class="flex items-center">
                <el-icon class="mr-2 text-primary"><Search /></el-icon>
                <span class="font-medium">信息管理</span>
                <el-badge 
                  v-if="enhancedStats.totalProjects > 0" 
                  :value="enhancedStats.totalProjects" 
                  class="ml-2" 
                  type="info"
                />
              </div>
              <div class="flex items-center space-x-1">
                <el-tooltip content="智能分析" placement="top">
                  <el-button 
                    type="success" 
                    size="small" 
                    @click="showProjectAnalysis" 
                    :icon="TrendCharts" 
                    circle 
                  />
                </el-tooltip>
                <el-tooltip content="高级搜索" placement="top">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="toggleAdvancedSearch" 
                    :icon="isAdvancedSearchVisible ? 'ArrowUp' : 'ArrowDown'" 
                    circle 
                  />
                </el-tooltip>
                <el-tooltip content="刷新项目" placement="top">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="refreshProjectTree" 
                    :icon="Refresh" 
                    :loading="isRefreshing"
                    circle 
                  />
                </el-tooltip>
              </div>
            </div>
          </template>
          
          <!-- 智能搜索框 -->
          <div class="smart-search-section mb-3">
            <el-input 
              v-model="searchKeyword" 
              placeholder="智能搜索项目、技术栈、贡献者..." 
              prefix-icon="Search" 
              clearable 
              @input="handleSmartSearch"
              @keyup.enter="performSearch"
              @focus="showSearchTips = true"
              @blur="hideSearchTips"
            >
              <template #suffix>
                <el-button 
                  v-if="searchKeyword" 
                  type="primary" 
                  size="small" 
                  text 
                  @click="performSearch"
                >
                  搜索
                </el-button>
              </template>
            </el-input>
            
            <!-- 搜索提示 -->
            <div v-if="showSearchTips && !searchKeyword" class="search-tips mt-2">
              <div class="text-xs text-gray-500 mb-1">搜索提示:</div>
              <div class="flex flex-wrap gap-1">
                <el-tag 
                  v-for="tip in searchTips" 
                  :key="tip"
                  size="small"
                  type="info"
                  class="cursor-pointer tip-tag"
                  @click="applySearchTip(tip)"
                >
                  {{ tip }}
                </el-tag>
              </div>
            </div>
            
            <!-- 智能搜索建议 -->
            <div v-if="searchSuggestions.length > 0" class="search-suggestions mt-2">
              <div class="text-xs text-gray-500 mb-1">智能建议:</div>
              <div class="flex flex-wrap gap-1">
                <el-tag 
                  v-for="suggestion in searchSuggestions" 
                  :key="suggestion"
                  size="small"
                  type="success"
                  class="cursor-pointer suggestion-tag"
                  @click="applySuggestion(suggestion)"
                >
                                     <el-icon class="mr-1"><Star /></el-icon>
                  {{ suggestion }}
                </el-tag>
              </div>
            </div>
          </div>
          
          <!-- 高级搜索面板 -->
          <el-collapse-transition>
            <div v-show="isAdvancedSearchVisible" class="advanced-search-panel mb-3">
              <el-divider class="my-3">
                <span class="text-xs">高级搜索</span>
              </el-divider>
              
              <!-- 编程语言筛选 -->
              <div class="filter-group mb-3">
                <div class="filter-label">编程语言:</div>
                <el-select 
                  v-model="advancedFilters.language" 
                  placeholder="选择语言" 
                  clearable 
                  size="small"
                  class="w-full"
                >
                  <el-option 
                    v-for="lang in availableLanguages" 
                    :key="lang" 
                    :label="lang" 
                    :value="lang" 
                  />
                </el-select>
              </div>
              
              <!-- 技术栈筛选 -->
              <div class="filter-group mb-3">
                <div class="filter-label">技术栈:</div>
                <el-select 
                  v-model="advancedFilters.techStack" 
                  placeholder="选择技术栈" 
                  clearable 
                  size="small"
                  class="w-full"
                >
                  <el-option 
                    v-for="tech in availableTechStacks" 
                    :key="tech" 
                    :label="tech" 
                    :value="tech" 
                  />
                </el-select>
              </div>
              
              <!-- 星标数范围 -->
              <div class="filter-group mb-3">
                <div class="filter-label">星标数:</div>
                <el-slider 
                  v-model="advancedFilters.starRange" 
                  range 
                  :min="0" 
                  :max="maxStarCount" 
                  :step="100"
                  show-stops
                  size="small"
                />
                <div class="flex justify-between text-xs text-gray-500 mt-1">
                  <span>{{ advancedFilters.starRange[0] }}</span>
                  <span>{{ advancedFilters.starRange[1] }}</span>
                </div>
              </div>
              
              <!-- 排序方式 -->
              <div class="filter-group mb-3">
                <div class="filter-label">排序:</div>
                <el-radio-group v-model="advancedFilters.sortBy" size="small">
                  <el-radio-button value="stars">星标</el-radio-button>
                  <el-radio-button value="updated">更新</el-radio-button>
                  <el-radio-button value="name">名称</el-radio-button>
                  <el-radio-button value="forks">Fork</el-radio-button>
                </el-radio-group>
              </div>
              
              <!-- 搜索按钮 -->
              <el-button 
                type="primary" 
                size="small" 
                class="w-full" 
                @click="performAdvancedSearch"
                :loading="isSearching"
              >
                <el-icon class="mr-1"><Search /></el-icon>
                高级搜索
              </el-button>
            </div>
          </el-collapse-transition>
          
          <!-- 智能分类标签 -->
          <div class="smart-category-tags mb-3">
            <div class="flex flex-wrap gap-1">
              <el-tag 
                v-for="tag in smartCategoryTags" 
                :key="tag.value"
                :type="tag.type"
                size="small"
                class="cursor-pointer category-tag"
                :class="{ 'active-tag': activeCategory === tag.value }"
                @click="filterByCategory(tag.value)"
              >
                <el-icon class="mr-1" v-if="tag.icon">
                  <component :is="tag.icon" />
                </el-icon>
                {{ tag.label }}
                <el-badge 
                  v-if="tag.count > 0" 
                  :value="tag.count" 
                  class="ml-1" 
                  size="small"
                />
              </el-tag>
            </div>
          </div>
          
          <!-- 项目树 -->
          <div class="project-tree-container">
            <div v-if="isSearching" class="loading-state">
              <el-skeleton :rows="3" animated />
            </div>
            
            <div v-else-if="filteredProjectOptions.length === 0" class="empty-state">
              <el-empty 
                :image-size="60" 
                description="暂无匹配项目"
              >
                <el-button type="primary" size="small" @click="resetFilters">
                  重置筛选
                </el-button>
              </el-empty>
            </div>
            
            <el-tree
              v-else
              ref="projectTreeRef"
              class="enhanced-community-tree"
              node-key="id"
              :data="paginatedProjectOptions"
              :props="{ label: 'label', children: 'children' } as any"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              highlight-current
              :default-expand-all="false"
              @node-click="handleNodeClick"
            >
              <template #default="{ node, data }">
                <div class="enhanced-tree-node-content">
                  <div class="node-header">
                    <div class="flex items-center">
                      <el-avatar 
                        v-if="data.avatar" 
                        :src="data.avatar" 
                        :size="20" 
                        class="mr-2"
                      />
                      <el-icon 
                        v-else 
                        class="mr-2" 
                        :color="getProjectStatusColor(data.status)"
                      >
                        <component :is="getProjectStatusIcon(data.status)" />
                      </el-icon>
                      <span class="project-name">{{ node.label }}</span>
                      <div class="flex items-center ml-auto">
                        <el-tag 
                          v-if="data.language" 
                          size="small" 
                          type="info" 
                          class="language-tag"
                        >
                          {{ data.language }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                  
                  <div v-if="data.description" class="node-description">
                    {{ data.description.length > 50 ? data.description.substring(0, 50) + '...' : data.description }}
                  </div>
                  
                  <div class="node-stats">
                    <div class="flex items-center justify-between text-xs">
                      <div class="flex items-center space-x-2">
                        <span class="stat-item">
                          <el-icon class="mr-1"><Star /></el-icon>
                          {{ data.starCount || 0 }}
                        </span>
                        <span class="stat-item">
                          <el-icon class="mr-1"><Share /></el-icon>
                          {{ data.forkCount || 0 }}
                        </span>
                        <span v-if="data.contributorCount" class="stat-item">
                          <el-icon class="mr-1"><User /></el-icon>
                          {{ data.contributorCount }}
                        </span>
                      </div>
                      <el-tag 
                        :type="getStatusTagType(data.status)" 
                        size="small"
                      >
                        {{ getStatusText(data.status) }}
                      </el-tag>
                    </div>
                  </div>
                  
                  <!-- 项目标签 -->
                  <div v-if="data.tags && data.tags.length > 0" class="node-tags mt-1">
                    <div class="flex flex-wrap gap-1">
                      <el-tag 
                        v-for="tag in data.tags.slice(0, 2)" 
                        :key="tag"
                        size="small"
                        type="info"
                        class="text-xs"
                      >
                        {{ tag }}
                      </el-tag>
                      <el-tag 
                        v-if="data.tags.length > 2" 
                        size="small"
                        type="info"
                        class="text-xs"
                      >
                        +{{ data.tags.length - 2 }}
                      </el-tag>
                    </div>
                  </div>
                </div>
              </template>
            </el-tree>
          </div>
          
          <!-- 分页 -->
          <div v-if="filteredProjectOptions.length > projectPageSize" class="pagination-wrapper mt-3">
            <el-pagination 
              v-model:current-page="currentProjectPage" 
              :page-size="projectPageSize" 
              :total="filteredProjectOptions.length" 
              layout="prev, next" 
              small 
              background
              @current-change="handleProjectPageChange"
            />
          </div>
          
          <!-- 增强统计信息 -->
          <div class="enhanced-stats mt-3 pt-3 border-t border-gray-200">
            <div class="stats-grid">
              <div class="stat-card">
                <div class="stat-value text-green-600">{{ enhancedStats.active }}</div>
                <div class="stat-label">活跃项目</div>
              </div>
              <div class="stat-card">
                <div class="stat-value text-blue-600">{{ enhancedStats.developing }}</div>
                <div class="stat-label">开发中</div>
              </div>
              <div class="stat-card">
                <div class="stat-value text-orange-600">{{ enhancedStats.maintaining }}</div>
                <div class="stat-label">维护中</div>
              </div>
              <div class="stat-card">
                <div class="stat-value text-purple-600">{{ enhancedStats.totalStars }}</div>
                <div class="stat-label">总星标</div>
              </div>
            </div>
            
            <!-- 快速操作 -->
            <div class="quick-actions mt-3">
              <el-button-group size="small">
                <el-button @click="exportProjectList">
                  <el-icon class="mr-1"><Download /></el-icon>
                  导出
                </el-button>
                <el-button @click="showProjectTrends">
                  <el-icon class="mr-1"><TrendCharts /></el-icon>
                  趋势
                </el-button>
                <el-button @click="showProjectRecommendations">
                  <el-icon class="mr-1"><Star /></el-icon>
                  推荐
                </el-button>
              </el-button-group>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :lg="20" :xs="24">
        <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
          <div v-show="showSearch" class="mb-[10px]">
            <el-card shadow="hover">
              <el-form ref="queryFormRef" :model="queryParams" :inline="true">
                <el-form-item label="用户名称" prop="userName">
                  <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item label="用户昵称" prop="nickName">
                  <el-input v-model="queryParams.nickName" placeholder="请输入用户昵称" clearable @keyup.enter="handleQuery" />
                </el-form-item>
                <el-form-item label="手机号码" prop="phonenumber">
                  <el-input v-model="queryParams.phonenumber" placeholder="请输入手机号码" clearable @keyup.enter="handleQuery" />
                </el-form-item>

                <el-form-item label="状态" prop="status">
                  <el-select v-model="queryParams.status" placeholder="用户状态" clearable>
                    <el-option v-for="dict in sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
                  </el-select>
                </el-form-item>
                <el-form-item label="创建时间" style="width: 308px">
                  <el-date-picker
                    v-model="dateRange"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    type="daterange"
                    range-separator="-"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
                  ></el-date-picker>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
                  <el-button icon="Refresh" @click="resetQuery">重置</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </div>
        </transition>

        <el-card shadow="hover">
          <template #header>
            <el-row :gutter="10">
              <el-col :span="1.5">
                <el-button v-has-permi="['system:user:add']" type="primary" plain icon="Plus" @click="handleAdd()">新增</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button v-has-permi="['system:user:edit']" type="success" plain :disabled="single" icon="Edit" @click="handleUpdate()">
                  修改
                </el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button v-has-permi="['system:user:remove']" type="danger" plain :disabled="multiple" icon="Delete" @click="handleDelete()">
                  删除
                </el-button>
              </el-col>
              <el-col :span="1.5">
                <el-dropdown class="mt-[1px]">
                  <el-button plain type="info">
                    更多
                    <el-icon class="el-icon--right"><arrow-down /></el-icon
                  ></el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item icon="Download" @click="importTemplate">下载模板</el-dropdown-item>
                      <!-- 注意 由于el-dropdown-item标签是延迟加载的 所以v-has-permi自定义标签不生效 需要使用v-if调用方法执行 -->
                      <el-dropdown-item v-if="checkPermi(['system:user:import'])" icon="Top" @click="handleImport">导入数据</el-dropdown-item>
                      <el-dropdown-item v-if="checkPermi(['system:user:export'])" icon="Download" @click="handleExport">导出数据</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-col>
              <right-toolbar v-model:show-search="showSearch" :columns="columns" :search="true" @query-table="getList"></right-toolbar>
            </el-row>
          </template>

          <el-table v-loading="loading" border :data="userList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column v-if="columns[0].visible" key="userId" label="用户编号" align="center" prop="userId" />
            <el-table-column v-if="columns[1].visible" key="userName" label="用户名称" align="center" prop="userName" :show-overflow-tooltip="true" />
            <el-table-column v-if="columns[2].visible" key="nickName" label="用户昵称" align="center" prop="nickName" :show-overflow-tooltip="true" />
            <el-table-column v-if="columns[3].visible" key="giteeAccount" label="Gitee账号" align="center" prop="giteeAccount" :show-overflow-tooltip="true" />
            <el-table-column v-if="columns[3].visible" key="githubAccount" label="GitHub账号" align="center" prop="githubAccount" :show-overflow-tooltip="true" />
            <el-table-column v-if="columns[4].visible" key="contributionSummary" label="贡献记录" align="center" width="150">
              <template #default="scope">
                <div class="contribution-summary">
                  <el-tag 
                    v-if="scope.row.contributionCount > 0" 
                    type="success" 
                    size="small"
                    @click="viewUserContributions(scope.row)"
                    style="cursor: pointer;"
                  >
                    {{ scope.row.contributionCount }} 条贡献
                  </el-tag>
                  <el-tag 
                    v-else 
                    type="info" 
                    size="small"
                  >
                    暂无贡献
                  </el-tag>
                  <div v-if="scope.row.totalPoints > 0" class="contribution-points">
                    <span class="text-xs text-green-600">+{{ scope.row.totalPoints }} 点</span>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column v-if="columns[5].visible" key="phonenumber" label="手机号码" align="center" prop="phonenumber" width="120" />
            <el-table-column v-if="columns[5].visible" key="status" label="状态" align="center">
              <template #default="scope">
                <el-switch v-model="scope.row.status" active-value="0" inactive-value="1" @change="handleStatusChange(scope.row)"></el-switch>
              </template>
            </el-table-column>

            <el-table-column v-if="columns[6].visible" label="创建时间" align="center" prop="createTime" width="160">
              <template #default="scope">
                <span>{{ scope.row.createTime }}</span>
              </template>
            </el-table-column>

            <el-table-column label="操作" fixed="right" width="180" class-name="small-padding fixed-width">
              <template #default="scope">
                <el-tooltip v-if="scope.row.userId !== 1" content="修改" placement="top">
                  <el-button v-hasPermi="['system:user:edit']" link type="primary" icon="Edit" @click="handleUpdate(scope.row)"></el-button>
                </el-tooltip>
                <el-tooltip v-if="scope.row.userId !== 1" content="删除" placement="top">
                  <el-button v-hasPermi="['system:user:remove']" link type="primary" icon="Delete" @click="handleDelete(scope.row)"></el-button>
                </el-tooltip>

                <el-tooltip v-if="scope.row.userId !== 1" content="重置密码" placement="top">
                  <el-button v-hasPermi="['system:user:resetPwd']" link type="primary" icon="Key" @click="handleResetPwd(scope.row)"></el-button>
                </el-tooltip>

                <el-tooltip v-if="scope.row.userId !== 1" content="分配角色" placement="top">
                  <el-button v-hasPermi="['system:user:edit']" link type="primary" icon="CircleCheck" @click="handleAuthRole(scope.row)"></el-button>
                </el-tooltip>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="total > 0"
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            :total="total"
            @pagination="getList"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog ref="formDialogRef" v-model="dialog.visible" :title="dialog.title" width="600px" append-to-body @close="closeDialog">
      <el-form ref="userFormRef" :model="form" :rules="rules" label-width="80px">
                 <el-row>
           <el-col :span="12">
             <el-form-item label="用户昵称" prop="nickName">
               <el-input v-model="form.nickName" placeholder="请输入用户昵称" maxlength="30" />
             </el-form-item>
           </el-col>
           <el-col :span="12">
             <el-form-item label="Gitee账号" prop="giteeAccount">
               <el-input 
                 v-model="form.giteeAccount" 
                 placeholder="请输入Gitee用户名" 
                 maxlength="50"
                 prefix-icon="Link"
               >
                 <template #suffix>
                   <el-button 
                     v-if="form.giteeAccount" 
                     type="primary" 
                     size="small" 
                     text 
                     @click="openGiteeProfile"
                   >
                     查看
                   </el-button>
                   <el-button 
                     v-if="form.giteeAccount" 
                     type="success" 
                     size="small" 
                     text 
                     @click="syncGiteeInfo"
                     :loading="isSyncingGitee"
                   >
                     同步
                   </el-button>
                 </template>
               </el-input>
             </el-form-item>
           </el-col>
         </el-row>
         <el-row>
           <el-col :span="12">
             <el-form-item label="GitHub账号" prop="githubAccount">
               <el-input 
                 v-model="form.githubAccount" 
                 placeholder="请输入GitHub用户名" 
                 maxlength="50"
                 prefix-icon="Link"
               >
                 <template #suffix>
                   <el-button 
                     v-if="form.githubAccount" 
                     type="primary" 
                     size="small" 
                     text 
                     @click="openGithubProfile"
                   >
                     查看
                   </el-button>
                 </template>
               </el-input>
             </el-form-item>
           </el-col>
           <el-col :span="12">
             <el-form-item label="所在地" prop="location">
               <el-input v-model="form.location" placeholder="请输入所在地" maxlength="100" />
             </el-form-item>
           </el-col>
         </el-row>
         <el-row>
           <el-col :span="24">
             <el-form-item label="个人简介" prop="bio">
               <el-input
                 v-model="form.bio"
                 type="textarea"
                 :rows="3"
                 placeholder="请简单介绍一下自己..."
                 maxlength="200"
                 show-word-limit
               />
             </el-form-item>
           </el-col>
         </el-row>
         <el-row>
           <el-col :span="12">
             <el-form-item label="技能标签" prop="skills">
               <el-input v-model="form.skills" placeholder="请输入技能标签，用逗号分隔" maxlength="200" />
             </el-form-item>
           </el-col>
           <el-col :span="12">
             <el-form-item label="个人网站" prop="website">
               <el-input v-model="form.website" placeholder="请输入个人网站地址" maxlength="500" />
             </el-form-item>
           </el-col>
         </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户密码" prop="password">
              <el-input v-model="form.password" placeholder="请输入用户密码" type="password" maxlength="20" show-password />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option v-for="dict in sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.postIds" multiple placeholder="请选择">
                <el-option
                  v-for="item in postOptions"
                  :key="item.postId"
                  :label="item.postName"
                  :value="item.postId"
                  :disabled="item.status == '1'"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="roleIds">
              <el-select v-model="form.roleIds" filterable multiple placeholder="请选择">
                <el-option
                  v-for="item in roleOptions"
                  :key="item.roleId"
                  :label="item.roleName"
                  :value="item.roleId"
                  :disabled="item.status == '1'"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
                 <el-row>
           <el-col :span="24">
             <el-form-item label="开源贡献">
               <div class="contribution-panel">
                 <div class="contribution-header flex items-center justify-between">
                   <span class="text-sm font-medium">贡献统计</span>
                   <div class="flex items-center space-x-2">
                     <el-button 
                       type="success" 
                       size="small" 
                       @click="syncGiteeContributions"
                       :loading="isSyncingContributions"
                       :disabled="!form.giteeAccount"
                     >
                       <el-icon class="mr-1"><Refresh /></el-icon>
                       同步Gitee
                     </el-button>
                     <el-button 
                       type="primary" 
                       size="small" 
                       @click="showContributionDialog = true"
                       :disabled="!form.userId"
                     >
                       管理贡献
                     </el-button>
                   </div>
                 </div>
                 
                 <!-- Gitee统计信息 -->
                 <div v-if="giteeStats" class="gitee-stats mb-3">
                   <el-divider class="my-2">
                     <span class="text-xs">Gitee统计</span>
                   </el-divider>
                   <div class="stats-grid">
                     <div class="stat-item">
                       <div class="stat-value text-blue-600">{{ giteeStats.publicRepos || 0 }}</div>
                       <div class="stat-label">公开仓库</div>
                     </div>
                     <div class="stat-item">
                       <div class="stat-value text-green-600">{{ giteeStats.followers || 0 }}</div>
                       <div class="stat-label">关注者</div>
                     </div>
                     <div class="stat-item">
                       <div class="stat-value text-purple-600">{{ giteeStats.following || 0 }}</div>
                       <div class="stat-label">关注中</div>
                     </div>
                     <div class="stat-item">
                       <div class="stat-value text-orange-600">{{ giteeStats.starred || 0 }}</div>
                       <div class="stat-label">星标项目</div>
                     </div>
                   </div>
                 </div>
                 
                                   <div v-if="userContributions.length > 0" class="contribution-content mt-3">
                   <div class="contribution-stats mb-3">
                     <div class="stats-grid">
                       <div class="stat-item">
                         <div class="stat-value text-blue-600">{{ userContributions.length }}</div>
                         <div class="stat-label">总贡献</div>
                       </div>
                       <div class="stat-item">
                         <div class="stat-value text-green-600">{{ totalContributionPoints }}</div>
                         <div class="stat-label">贡献点数</div>
                       </div>
                       <div class="stat-item">
                         <div class="stat-value text-purple-600">{{ activeProjects.length }}</div>
                         <div class="stat-label">参与项目</div>
                       </div>
                     </div>
                   </div>
                   
                   <!-- 贡献日历 -->
                   <div class="contribution-calendar-section mb-3">
                     <ContributionCalendar 
                       :contributions="contributionCalendarData" 
                       :days-count="365"
                       @day-click="handleCalendarDayClick"
                     />
                   </div>
                   
                   <div class="recent-contributions">
                     <div class="text-sm text-gray-600 mb-2">最近贡献:</div>
                     <div class="contribution-list">
                       <div 
                         v-for="contribution in recentContributions" 
                         :key="contribution.contributionId"
                         class="contribution-item"
                       >
                         <div class="flex items-center justify-between">
                           <div class="flex items-center">
                             <el-tag 
                               :type="getContributionTypeTag(contribution.contributionType)" 
                               size="small"
                             >
                               {{ getContributionTypeText(contribution.contributionType) }}
                             </el-tag>
                             <span class="ml-2 text-sm">{{ contribution.projectName }}</span>
                           </div>
                           <div class="flex items-center">
                             <span class="text-xs text-gray-500 mr-2">
                               {{ formatDate(contribution.contributionTime) }}
                             </span>
                             <span class="text-sm font-medium text-green-600">
                               +{{ contribution.points }}
                             </span>
                           </div>
                         </div>
                         <div class="text-xs text-gray-600 mt-1 truncate">
                           {{ contribution.content }}
                         </div>
                         <div v-if="contribution.url" class="text-xs text-blue-500 mt-1">
                           <el-link :href="contribution.url" target="_blank" type="primary">
                             查看详情
                           </el-link>
                         </div>
                       </div>
                     </div>
                   </div>
                 </div>
                 
                 <div v-else class="contribution-empty">
                   <el-empty 
                     :image-size="40" 
                     description="暂无贡献记录"
                   >
                     <div class="flex flex-col items-center space-y-2">
                       <el-button 
                         type="success" 
                         size="small" 
                         @click="syncGiteeContributions"
                         :loading="isSyncingContributions"
                         :disabled="!form.giteeAccount"
                       >
                         <el-icon class="mr-1"><Refresh /></el-icon>
                         同步Gitee贡献
                       </el-button>
                       <el-button 
                         type="primary" 
                         size="small" 
                         @click="showContributionDialog = true"
                         :disabled="!form.userId"
                       >
                         手动添加贡献
                       </el-button>
                     </div>
                   </el-empty>
                 </div>
               </div>
             </el-form-item>
           </el-col>
         </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel()">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog v-model="upload.open" :title="upload.title" width="400px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload">
          <i-ep-upload-filled />
        </el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="text-center el-upload__tip">
            <div class="el-upload__tip"><el-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据</div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" :underline="false" style="font-size: 12px; vertical-align: baseline" @click="importTemplate">下载模板</el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 贡献管理对话框 -->
    <el-dialog 
      v-model="showContributionDialog" 
      title="管理用户贡献" 
      width="800px" 
      append-to-body
    >
      <div class="contribution-management">
        <!-- 添加贡献表单 -->
        <el-card shadow="never" class="mb-4">
          <template #header>
            <span>添加新贡献</span>
          </template>
          
          <el-form 
            ref="contributionFormRef" 
            :model="contributionForm" 
            :rules="contributionRules" 
            label-width="100px"
          >
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="项目" prop="projectId">
                  <el-select v-model="contributionForm.projectId" placeholder="选择项目">
                    <el-option 
                      v-for="project in projectOptions" 
                      :key="project.id" 
                      :label="project.label" 
                      :value="project.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="贡献类型" prop="contributionType">
                  <el-select v-model="contributionForm.contributionType" placeholder="选择类型">
                    <el-option label="代码提交" value="0" />
                    <el-option label="问题修复" value="1" />
                    <el-option label="文档贡献" value="2" />
                    <el-option label="回答问题" value="3" />
                    <el-option label="其他" value="4" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-form-item label="贡献内容" prop="content">
                  <el-input 
                    v-model="contributionForm.content" 
                    type="textarea" 
                    :rows="3" 
                    placeholder="请描述贡献内容"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="贡献点数" prop="points">
                  <el-input-number 
                    v-model="contributionForm.points" 
                    :min="1" 
                    :max="1000" 
                    placeholder="贡献点数"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="相关链接">
                  <el-input 
                    v-model="contributionForm.url" 
                    placeholder="GitHub/Gitee 链接（可选）"
                  />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <el-button type="primary" @click="addContribution">添加贡献</el-button>
                <el-button @click="resetContributionForm">重置</el-button>
              </el-col>
            </el-row>
          </el-form>
        </el-card>
        
        <!-- 贡献列表 -->
        <el-card shadow="never">
          <template #header>
            <div class="flex items-center justify-between">
              <span>贡献列表</span>
              <el-button size="small" @click="refreshContributions">刷新</el-button>
            </div>
          </template>
          
          <el-table :data="userContributions" stripe>
            <el-table-column label="项目" prop="projectName" width="120" />
            <el-table-column label="类型" width="100">
              <template #default="scope">
                <el-tag 
                  :type="getContributionTypeTag(scope.row.contributionType)" 
                  size="small"
                >
                  {{ getContributionTypeText(scope.row.contributionType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="内容" prop="content" show-overflow-tooltip />
            <el-table-column label="点数" prop="points" width="80" align="center" />
            <el-table-column label="时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.contributionTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <el-button 
                  type="primary" 
                  size="small" 
                  text 
                  @click="editContribution(scope.row)"
                >
                  编辑
                </el-button>
                <el-button 
                  type="danger" 
                  size="small" 
                  text 
                  @click="deleteContribution(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
      
      <template #footer>
        <el-button @click="showContributionDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="User" lang="ts">
import api from '@/api/system/user';
import { UserForm, UserQuery, UserVO } from '@/api/system/user/types';
import { DeptTreeVO, DeptVO } from '@/api/system/dept/types';
import { RoleVO } from '@/api/system/role/types';
import { PostQuery, PostVO } from '@/api/system/post/types';
import { treeselect } from '@/api/system/dept';
import { listProjectForOss } from '@/api/osc/project';
import { globalHeaders } from '@/utils/request';
import { to } from 'await-to-js';
import { optionselect } from '@/api/system/post';
import { hasPermi } from '@/directive/permission';
import { checkPermi } from '@/utils/permission';
import { Refresh, CircleCheck, Clock, Warning, CircleClose, Search, ArrowUp, ArrowDown, Star, Share, Download, TrendCharts, User } from '@element-plus/icons-vue';
import { 
  getGiteeUserInfo, 
  getGiteeRepositories, 
  getGiteeContributions, 
  getRepositoryCommits,
  getGiteePullRequests,
  getGiteeIssues,
  getGiteeUserStats,
  syncGiteeUserInfo,
  getLocalContributions,
  addContribution as addGiteeContribution,
  updateContribution as updateGiteeContribution,
  deleteContribution as deleteGiteeContribution,
  getMemberInfo,
  saveMemberInfo
} from '@/api/gitee';
import ContributionCalendar from '@/components/ContributionCalendar.vue';

const router = useRouter();
const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sys_normal_disable, sys_user_sex } = toRefs<any>(proxy?.useDict('sys_normal_disable', 'sys_user_sex'));
const userList = ref<UserVO[]>();
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<number | string>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dateRange = ref<[DateModelType, DateModelType]>(['', '']);
// 增强的变量声明
const searchKeyword = ref('');
const searchSuggestions = ref<string[]>([]);
const searchTips = ref(['Java', 'Vue', 'Spring Boot', 'Element Plus', '热门项目', '最新更新']);
const showSearchTips = ref(false);
const isAdvancedSearchVisible = ref(false);
const isSearching = ref(false);
const isRefreshing = ref(false);
const currentProjectPage = ref(1);
const projectPageSize = ref(20);
const searchHistory = ref<string[]>([]);

// 高级搜索筛选器
const advancedFilters = ref({
  language: '',
  techStack: '',
  starRange: [0, 10000],
  sortBy: 'stars' as 'stars' | 'updated' | 'name' | 'forks',
  sortOrder: 'desc' as 'asc' | 'desc'
});

// 增强的分类标签
const enhancedCategoryTags = ref([
  { label: '全部', value: 'all', type: '' as any, icon: 'Grid', count: 0 },
  { label: '活跃', value: '2', type: 'success' as any, icon: 'CircleCheck', count: 0 },
  { label: '开发中', value: '3', type: 'primary' as any, icon: 'Clock', count: 0 },
  { label: '维护中', value: '4', type: 'warning' as any, icon: 'Warning', count: 0 },
  { label: '热门', value: 'hot', type: 'danger' as any, icon: 'Star', count: 0 }
]);

// 增强的统计数据
const enhancedStats = computed(() => {
  const stats = {
    active: projectOptions.value.filter(p => p.status === '2').length,
    developing: projectOptions.value.filter(p => p.status === '3').length,
    maintaining: projectOptions.value.filter(p => p.status === '4').length,
    totalStars: projectOptions.value.reduce((sum, p) => sum + (p.starCount || 0), 0),
    totalProjects: projectOptions.value.length
  };
  
  // 更新标签计数
  smartCategoryTags.value.forEach(tag => {
    switch(tag.value) {
      case 'all': tag.count = projectOptions.value.length; break;
      case '2': tag.count = stats.active; break;
      case '3': tag.count = stats.developing; break;
      case '4': tag.count = stats.maintaining; break;
      case 'hot': tag.count = projectOptions.value.filter(p => (p.starCount || 0) > 1000).length; break;
    }
  });
  
  return stats;
});

// 可用的编程语言
const availableLanguages = computed(() => {
  const languages = new Set<string>();
  projectOptions.value.forEach(project => {
    if (project.programmingLanguage) {
      if (Array.isArray(project.programmingLanguage)) {
        project.programmingLanguage.forEach(lang => languages.add(lang));
      } else {
        languages.add(project.programmingLanguage);
      }
    }
  });
  return Array.from(languages).sort();
});

// 可用的技术栈
const availableTechStacks = computed(() => {
  const techStacks = new Set<string>();
  projectOptions.value.forEach(project => {
    if (project.techStack) {
      if (Array.isArray(project.techStack)) {
        project.techStack.forEach(tech => techStacks.add(tech));
      } else {
        techStacks.add(project.techStack);
      }
    }
  });
  return Array.from(techStacks).sort();
});

// 最大星标数
const maxStarCount = computed(() => {
  return Math.max(...projectOptions.value.map(p => p.starCount || 0), 10000);
});

const projectName = ref('');
const projectOptions = ref<any[]>([]);
const enabledProjectOptions = ref<any[]>([]);
const deptOptions = ref<DeptTreeVO[]>([]);
const enabledDeptOptions = ref<DeptTreeVO[]>([]);
const activeCategory = ref('all');

// 分类标签
const categoryTags = ref([
  { label: '全部', value: 'all', type: '' as any },
  { label: '活跃', value: '2', type: 'success' as any },
  { label: '开发中', value: '3', type: 'primary' as any },
  { label: '维护中', value: '4', type: 'warning' as any }
]);

// 智能分类标签
const smartCategoryTags = ref([
  { label: '全部', value: 'all', type: '' as any, icon: 'Grid', count: 0 },
  { label: '活跃', value: '2', type: 'success' as any, icon: 'CircleCheck', count: 0 },
  { label: '开发中', value: '3', type: 'primary' as any, icon: 'Clock', count: 0 },
  { label: '维护中', value: '4', type: 'warning' as any, icon: 'Warning', count: 0 },
  { label: '热门', value: 'hot', type: 'danger' as any, icon: 'Star', count: 0 }
]);

// 增强的筛选项目选项
const filteredProjectOptions = computed(() => {
  let filtered = [...projectOptions.value];
  
  // 按分类筛选
  if (activeCategory.value !== 'all') {
    if (activeCategory.value === 'hot') {
      filtered = filtered.filter(project => (project.starCount || 0) > 1000);
    } else {
      filtered = filtered.filter(project => project.status === activeCategory.value);
    }
  }
  
  // 按关键词筛选
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase();
    filtered = filtered.filter(project => {
      const nameMatch = project.label?.toLowerCase().includes(keyword);
      const descMatch = project.description?.toLowerCase().includes(keyword);
      const techMatch = project.techStack?.toLowerCase().includes(keyword);
      const langMatch = project.programmingLanguage?.toLowerCase().includes(keyword);
      return nameMatch || descMatch || techMatch || langMatch;
    });
  }
  
  // 按高级筛选器筛选
  if (advancedFilters.value.language) {
    filtered = filtered.filter(project => {
      if (Array.isArray(project.programmingLanguage)) {
        return project.programmingLanguage.includes(advancedFilters.value.language);
      }
      return project.programmingLanguage === advancedFilters.value.language;
    });
  }

  if (advancedFilters.value.techStack) {
    filtered = filtered.filter(project => {
      if (Array.isArray(project.techStack)) {
        return project.techStack.includes(advancedFilters.value.techStack);
      }
      return project.techStack === advancedFilters.value.techStack;
    });
  }
  
  // 按星标数筛选
  const [minStars, maxStars] = advancedFilters.value.starRange;
  filtered = filtered.filter(project => {
    const stars = project.starCount || 0;
    return stars >= minStars && stars <= maxStars;
  });
  
  // 排序
  filtered.sort((a, b) => {
    let aValue: any, bValue: any;
    
    switch (advancedFilters.value.sortBy) {
      case 'stars':
        aValue = a.starCount || 0;
        bValue = b.starCount || 0;
        break;
      case 'updated':
        aValue = new Date(a.updateTime || 0).getTime();
        bValue = new Date(b.updateTime || 0).getTime();
        break;
      case 'name':
        aValue = a.label?.toLowerCase() || '';
        bValue = b.label?.toLowerCase() || '';
        break;
      case 'forks':
        aValue = a.forkCount || 0;
        bValue = b.forkCount || 0;
        break;
      default:
        aValue = a.starCount || 0;
        bValue = b.starCount || 0;
    }
    
    if (advancedFilters.value.sortOrder === 'asc') {
      return aValue > bValue ? 1 : -1;
    } else {
      return aValue < bValue ? 1 : -1;
    }
  });
  
  return filtered;
});

// 分页的项目选项
const paginatedProjectOptions = computed(() => {
  const start = (currentProjectPage.value - 1) * projectPageSize.value;
  const end = start + projectPageSize.value;
  return filteredProjectOptions.value.slice(start, end);
});

// 智能搜索功能
const handleSmartSearch = debounce(async (value: string) => {
  if (!value.trim()) {
    searchSuggestions.value = [];
    return;
  }
  
  // 生成智能搜索建议
  const suggestions = new Set<string>();
  projectOptions.value.forEach(project => {
    if (project.label?.toLowerCase().includes(value.toLowerCase())) {
      suggestions.add(project.label);
    }
    if (project.techStack?.toLowerCase().includes(value.toLowerCase())) {
      const techs = project.techStack.split(',').map(t => t.trim());
      techs.forEach(tech => {
        if (tech.toLowerCase().includes(value.toLowerCase())) {
          suggestions.add(tech);
        }
      });
    }
    if (project.programmingLanguage?.toLowerCase().includes(value.toLowerCase())) {
      suggestions.add(project.programmingLanguage);
    }
  });
  
  searchSuggestions.value = Array.from(suggestions).slice(0, 5);
}, 300);

// 搜索提示相关函数
const hideSearchTips = () => {
  setTimeout(() => {
    showSearchTips.value = false;
  }, 200);
};

const applySearchTip = (tip: string) => {
  searchKeyword.value = tip;
  performSearch();
};

const applySuggestion = (suggestion: string) => {
  searchKeyword.value = suggestion;
  performSearch();
};

const performSearch = () => {
  if (searchKeyword.value.trim()) {
    // 添加到搜索历史
    if (!searchHistory.value.includes(searchKeyword.value)) {
      searchHistory.value.unshift(searchKeyword.value);
      if (searchHistory.value.length > 10) {
        searchHistory.value = searchHistory.value.slice(0, 10);
      }
    }
  }
  searchSuggestions.value = [];
  currentProjectPage.value = 1;
};

const toggleAdvancedSearch = () => {
  isAdvancedSearchVisible.value = !isAdvancedSearchVisible.value;
};

const performAdvancedSearch = async () => {
  isSearching.value = true;
  try {
    // 这里可以调用后端高级搜索API
    await new Promise(resolve => setTimeout(resolve, 1000)); // 模拟搜索延迟
    currentProjectPage.value = 1;
  } finally {
    isSearching.value = false;
  }
};

const resetFilters = () => {
  searchKeyword.value = '';
  activeCategory.value = 'all';
  advancedFilters.value = {
    language: '',
    techStack: '',
    starRange: [0, maxStarCount.value],
    sortBy: 'stars',
    sortOrder: 'desc'
  };
  currentProjectPage.value = 1;
  isAdvancedSearchVisible.value = false;
};

const handleProjectPageChange = (page: number) => {
  currentProjectPage.value = page;
};

// 状态相关函数
const getProjectStatusIcon = (status: string) => {
  switch (status) {
    case '2': return 'CircleCheck';
    case '3': return 'Clock';
    case '4': return 'Warning';
    default: return 'CircleClose';
  }
};

const getStatusTagType = (status: string) => {
  switch (status) {
    case '2': return 'success';
    case '3': return 'primary';
    case '4': return 'warning';
    default: return 'info';
  }
};

const getStatusText = (status: string) => {
  switch (status) {
    case '2': return '活跃';
    case '3': return '开发中';
    case '4': return '维护中';
    default: return '未知';
  }
};

// 增强的工具函数
const exportProjectList = () => {
  const data = filteredProjectOptions.value.map(project => ({
    项目名称: project.label,
    描述: project.description,
    技术栈: project.techStack,
    编程语言: project.programmingLanguage,
    星标数: project.starCount,
    Fork数: project.forkCount,
    状态: getStatusText(project.status)
  }));
  
  // 转换为CSV格式
  const csv = convertToCSV(data);
  downloadFile(csv, '项目列表.csv', 'text/csv');
  proxy?.$modal.msgSuccess('导出成功');
};

const shareProjectList = async () => {
  const shareData = {
    title: '开源社区项目列表',
    text: `共找到 ${filteredProjectOptions.value.length} 个项目`,
    url: window.location.href
  };
  
  if (navigator.share) {
    try {
      await navigator.share(shareData);
    } catch (err) {
      console.log('分享取消');
    }
  } else {
    // 复制到剪切板
    await navigator.clipboard.writeText(`${shareData.title}: ${shareData.url}`);
    proxy?.$modal.msgSuccess('链接已复制到剪切板');
  }
};

const addToFavorites = () => {
  const favorites = JSON.parse(localStorage.getItem('project-favorites') || '[]');
  const currentSearch = {
    keyword: searchKeyword.value,
    category: activeCategory.value,
    filters: { ...advancedFilters.value },
    timestamp: Date.now()
  };
  
  favorites.unshift(currentSearch);
  if (favorites.length > 20) {
    favorites.splice(20);
  }
  
  localStorage.setItem('project-favorites', JSON.stringify(favorites));
  proxy?.$modal.msgSuccess('已添加到收藏');
};

// 工具函数
function convertToCSV(data: any[]) {
  if (!data.length) return '';
  
  const headers = Object.keys(data[0]).join(',');
  const rows = data.map(row => 
    Object.values(row).map(value => `"${value}"`).join(',')
  ).join('\n');
  
  return headers + '\n' + rows;
}

function downloadFile(content: string, filename: string, contentType: string) {
  const blob = new Blob([content], { type: contentType });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

function debounce(func: Function, wait: number) {
  let timeout: NodeJS.Timeout;
  return function executedFunction(...args: any[]) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}
// 贡献相关变量
const showContributionDialog = ref(false);
const userContributions = ref<any[]>([]);
const contributionForm = ref({
  projectId: '',
  contributionType: '',
  content: '',
  points: 10,
  url: ''
});
const contributionFormRef = ref<ElFormInstance>();

// Gitee相关变量
const giteeStats = ref<any>(null);
const isSyncingGitee = ref(false);
const isSyncingContributions = ref(false);
const memberInfo = ref<any>(null);

const contributionRules = {
  projectId: [{ required: true, message: '请选择项目', trigger: 'change' }],
  contributionType: [{ required: true, message: '请选择贡献类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入贡献内容', trigger: 'blur' }],
  points: [{ required: true, message: '请输入贡献点数', trigger: 'blur' }]
};

// 计算属性
const totalContributionPoints = computed(() => {
  return userContributions.value.reduce((sum, item) => sum + (item.points || 0), 0);
});

const activeProjects = computed(() => {
  const projectIds = new Set(userContributions.value.map(item => item.projectId));
  return Array.from(projectIds).map(id => {
    const project = projectOptions.value.find(p => p.id === id);
    return project ? project.label : `项目${id}`;
  });
});

const recentContributions = computed(() => {
  return userContributions.value
    .sort((a, b) => new Date(b.contributionTime).getTime() - new Date(a.contributionTime).getTime())
    .slice(0, 5);
});

// 贡献日历数据
const contributionCalendarData = computed(() => {
  const calendarData = [];
  const contributionMap = new Map();
  
  // 统计每天的贡献数
  userContributions.value.forEach(contribution => {
    const date = new Date(contribution.contributionTime).toISOString().split('T')[0];
    const count = contributionMap.get(date) || 0;
    contributionMap.set(date, count + 1);
  });
  
  // 转换为日历数据格式
  contributionMap.forEach((count, date) => {
    calendarData.push({
      date,
      count,
      level: 0 // 将在组件中计算
    });
  });
  
  return calendarData;
});

const initPassword = ref<string>('');
// 贡献相关函数
const getContributionTypeTag = (type: string) => {
  switch (type) {
    case '0': return 'primary';
    case '1': return 'success';
    case '2': return 'info';
    case '3': return 'warning';
    case '4': return '';
    default: return 'info';
  }
};

const getContributionTypeText = (type: string) => {
  switch (type) {
    case '0': return '代码提交';
    case '1': return '问题修复';
    case '2': return '文档贡献';
    case '3': return '回答问题';
    case '4': return '其他';
    default: return '未知';
  }
};

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (days === 0) {
    return '今天';
  } else if (days === 1) {
    return '昨天';
  } else if (days < 7) {
    return `${days}天前`;
  } else {
    return date.toLocaleDateString('zh-CN');
  }
};

const openGiteeProfile = () => {
  if (form.value.giteeAccount) {
    const url = `https://gitee.com/${form.value.giteeAccount}`;
    window.open(url, '_blank');
  }
};

// 打开GitHub个人资料页面
const openGithubProfile = () => {
  if (form.value.githubAccount) {
    const url = `https://github.com/${form.value.githubAccount}`;
    window.open(url, '_blank');
  }
};

// 同步Gitee用户信息
const syncGiteeInfo = async () => {
  if (!form.value.giteeAccount) {
    proxy?.$modal.msgError('请先输入Gitee账号');
    return;
  }
  
  isSyncingGitee.value = true;
  try {
    // 获取Gitee用户信息
    const giteeUserInfo = await getGiteeUserInfo(form.value.giteeAccount);
    
    // 同步到本地成员表
    const memberData = {
      userId: form.value.userId,
      memberName: form.value.nickName,
      nickname: form.value.nickName,
      email: form.value.email,
      giteeId: form.value.giteeAccount,
      avatar: giteeUserInfo.data?.avatar_url,
      status: '0'
    };
    
    await saveMemberInfo(memberData);
    
    // 更新Gitee统计信息
    giteeStats.value = {
      publicRepos: giteeUserInfo.data?.public_repos,
      followers: giteeUserInfo.data?.followers,
      following: giteeUserInfo.data?.following,
      starred: giteeUserInfo.data?.starred || 0
    };
    
    proxy?.$modal.msgSuccess('Gitee信息同步成功');
  } catch (error) {
    console.error('同步Gitee信息失败:', error);
    proxy?.$modal.msgError('同步Gitee信息失败: ' + (error instanceof Error ? error.message : String(error)));
  } finally {
    isSyncingGitee.value = false;
  }
};

// 同步Gitee贡献记录
const syncGiteeContributions = async () => {
  if (!form.value.giteeAccount) {
    proxy?.$modal.msgError('请先输入Gitee账号');
    return;
  }
  
  isSyncingContributions.value = true;
  try {
    // 获取Gitee用户事件
    const events = await getGiteeContributions(form.value.giteeAccount, {
      per_page: 100
    });
    
    // 获取用户的仓库列表
    const repos = await getGiteeRepositories(form.value.giteeAccount);
    
    // 处理贡献记录
    const contributions = [];
    
    const eventsData = events.data || [];
    for (const event of eventsData) {
      let contributionType = '4'; // 其他
      let content = '';
      let url = '';
      
      switch (event.type) {
        case 'PushEvent':
          contributionType = '0'; // 代码提交
          content = `提交了 ${event.payload.commits?.length || 0} 个提交到 ${event.repo.name}`;
          url = `https://gitee.com/${event.repo.name}`;
          break;
        case 'IssuesEvent':
          contributionType = '1'; // 问题修复
          content = `${event.payload.action} 了 Issue: ${event.payload.issue?.title}`;
          url = event.payload.issue?.html_url;
          break;
        case 'PullRequestEvent':
          contributionType = '0'; // 代码提交
          content = `${event.payload.action} 了 Pull Request: ${event.payload.pull_request?.title}`;
          url = event.payload.pull_request?.html_url;
          break;
        case 'CreateEvent':
          contributionType = '2'; // 文档贡献
          content = `创建了 ${event.payload.ref_type}: ${event.payload.ref}`;
          url = `https://gitee.com/${event.repo.name}`;
          break;
        case 'ForkEvent':
          contributionType = '2'; // 文档贡献
          content = `Fork 了仓库: ${event.repo.name}`;
          url = `https://gitee.com/${event.repo.name}`;
          break;
      }
      
      if (content) {
        contributions.push({
          contributionId: Date.now() + Math.random(),
          projectId: event.repo.id,
          projectName: event.repo.name,
          contributionType,
          content,
          url,
          points: getContributionPoints(contributionType),
          contributionTime: event.created_at
        });
      }
    }
    
    // 更新本地贡献记录
    userContributions.value = contributions;
    
    proxy?.$modal.msgSuccess(`成功同步 ${contributions.length} 条贡献记录`);
  } catch (error) {
    console.error('同步Gitee贡献记录失败:', error);
    proxy?.$modal.msgError('同步Gitee贡献记录失败: ' + (error instanceof Error ? error.message : String(error)));
  } finally {
    isSyncingContributions.value = false;
  }
};

// 根据贡献类型计算点数
const getContributionPoints = (type: string) => {
  switch (type) {
    case '0': return 50; // 代码提交
    case '1': return 30; // 问题修复
    case '2': return 20; // 文档贡献
    case '3': return 15; // 回答问题
    default: return 10;  // 其他
  }
};

// 处理日历点击
const handleCalendarDayClick = (day: any) => {
  // 过滤出当天的贡献记录
  const dayContributions = userContributions.value.filter(contribution => {
    const contributionDate = new Date(contribution.contributionTime).toISOString().split('T')[0];
    return contributionDate === day.date;
  });
  
  if (dayContributions.length > 0) {
    // 显示当天的贡献详情
    const date = new Date(day.date);
    const formattedDate = date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
    
    let message = `${formattedDate} 的贡献记录:\n\n`;
    dayContributions.forEach((contribution, index) => {
      message += `${index + 1}. ${getContributionTypeText(contribution.contributionType)}: ${contribution.content}\n`;
    });
    
    proxy?.$modal.alert(message, '贡献详情');
  }
};

const refreshContributions = async () => {
  if (!form.value.userId) return;
  
  try {
    // 获取本地贡献记录
    const response = await getLocalContributions(Number(form.value.userId));
    if (response && response.rows) {
      userContributions.value = response.rows;
    } else {
      userContributions.value = [];
    }
  } catch (error) {
    console.error('获取贡献数据失败:', error);
    userContributions.value = [];
  }
};

const addContribution = async () => {
  if (!contributionFormRef.value) return;
  
  contributionFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        // 获取成员信息
        if (!memberInfo.value) {
          const memberResponse = await getMemberInfo(Number(form.value.userId));
          memberInfo.value = memberResponse.data;
        }
        
        // 准备贡献数据
        const contributionData = {
          memberId: memberInfo.value?.memberId || form.value.userId,
          projectId: contributionForm.value.projectId,
          contributionType: contributionForm.value.contributionType,
          content: contributionForm.value.content,
          points: contributionForm.value.points,
          url: contributionForm.value.url,
          contributionTime: new Date().toISOString()
        };
        
        // 调用API添加贡献
        await addGiteeContribution(contributionData);
        
        // 刷新贡献列表
        await refreshContributions();
        
        resetContributionForm();
        proxy?.$modal.msgSuccess('添加贡献成功');
      } catch (error) {
        console.error('添加贡献失败:', error);
        proxy?.$modal.msgError('添加贡献失败: ' + (error instanceof Error ? error.message : String(error)));
      }
    }
  });
};

const resetContributionForm = () => {
  contributionForm.value = {
    projectId: '',
    contributionType: '',
    content: '',
    points: 10,
    url: ''
  };
  contributionFormRef.value?.resetFields();
};

const editContribution = (contribution: any) => {
  console.log('编辑贡献:', contribution);
};

const deleteContribution = async (contribution: any) => {
  try {
    await proxy?.$modal.confirm('确定要删除这条贡献记录吗？');
    
    // 调用API删除贡献
    await deleteGiteeContribution(contribution.contributionId);
    
    // 刷新贡献列表
    await refreshContributions();
    
    proxy?.$modal.msgSuccess('删除成功');
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除贡献失败:', error);
      proxy?.$modal.msgError('删除贡献失败: ' + (error instanceof Error ? error.message : String(error)));
    }
  }
};

const postOptions = ref<PostVO[]>([]);
const roleOptions = ref<RoleVO[]>([]);

/*** 用户导入参数 */
const upload = reactive<ImportOption>({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: '',
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的用户数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: globalHeaders(),
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + '/system/user/importData'
});

// 列显隐信息
const columns = ref<FieldOption[]>([
  { key: 0, label: `用户编号`, visible: false, children: [] },
  { key: 1, label: `用户名称`, visible: true, children: [] },
  { key: 2, label: `用户昵称`, visible: true, children: [] },
  { key: 3, label: `Gitee账号`, visible: true, children: [] },
  { key: 4, label: `贡献记录`, visible: true, children: [] },
  { key: 5, label: `手机号码`, visible: true, children: [] },
  { key: 6, label: `状态`, visible: true, children: [] },
  { key: 7, label: `创建时间`, visible: true, children: [] }
]);

const projectTreeRef = ref<ElTreeInstance>();
const queryFormRef = ref<ElFormInstance>();
const userFormRef = ref<ElFormInstance>();
const uploadRef = ref<ElUploadInstance>();
const formDialogRef = ref<ElDialogInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: UserForm = {
  userId: undefined,
  deptId: undefined,
  userName: '',
  nickName: undefined,
  password: '',
  phonenumber: undefined,
  email: undefined,
  sex: undefined,
  status: '0',
  remark: '',
  postIds: [],
  roleIds: [],
  giteeAccount: ''
};

const initData: PageData<UserForm, UserQuery> = {
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: '',
    phonenumber: '',
    status: '',
    deptId: '',
    roleId: ''
  },
  rules: {
    userName: [
      { required: true, message: '用户名称不能为空', trigger: 'blur' },
      {
        min: 2,
        max: 20,
        message: '用户名称长度必须介于 2 和 20 之间',
        trigger: 'blur'
      }
    ],
    nickName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
    password: [
      { required: true, message: '用户密码不能为空', trigger: 'blur' },
      {
        min: 5,
        max: 20,
        message: '用户密码长度必须介于 5 和 20 之间',
        trigger: 'blur'
      },
      { pattern: /^[^<>"'|\\]+$/, message: '不能包含非法字符：< > " \' \\ |', trigger: 'blur' }
    ],
    email: [
      {
        type: 'email',
        message: '请输入正确的邮箱地址',
        trigger: ['blur', 'change']
      }
    ],
    phonenumber: [
      {
        pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
        message: '请输入正确的手机号码',
        trigger: 'blur'
      }
    ],
    roleIds: [{ required: true, message: '用户角色不能为空', trigger: 'blur' }]
  }
};
const data = reactive<PageData<UserForm, UserQuery>>(initData);

const { queryParams, form, rules } = toRefs<PageData<UserForm, UserQuery>>(data);

/** 通过条件过滤节点  */
const filterNode = (value: string, data: any) => {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
};

/** 根据名称筛选项目树 */
watchEffect(
  () => {
    if (projectTreeRef.value && projectName.value) {
      projectTreeRef.value.filter(projectName.value);
    }
  },
  {
    flush: 'post'
  }
);

// 监听高级筛选器变化
watch(
  () => advancedFilters.value,
  () => {
    currentProjectPage.value = 1;
  },
  { deep: true }
);

// 监听搜索关键词变化
watch(
  () => searchKeyword.value,
  (newValue) => {
    currentProjectPage.value = 1;
    if (newValue) {
      projectTreeRef.value?.filter(newValue);
    }
  }
);

/** 查询用户列表 */
const getList = async () => {
  loading.value = true;
  try {
    const res = await api.listUser(proxy?.addDateRange(queryParams.value, dateRange.value));
    userList.value = res.rows;
    total.value = res.total;
    
    // 为每个用户获取Gitee账号信息和贡献统计
    if (userList.value && userList.value.length > 0) {
      for (const user of userList.value) {
        try {
          const memberInfo = await getMemberInfo(Number(user.userId));
          if (memberInfo && memberInfo.data) {
            user.giteeAccount = memberInfo.data.giteeAccount || '';
            // 获取贡献统计
            const contributions = await getLocalContributions(Number(user.userId));
            if (contributions && contributions.rows) {
              user.contributionCount = contributions.rows.length;
              user.totalPoints = contributions.rows.reduce((sum, item) => sum + (item.points || 0), 0);
            } else {
              user.contributionCount = 0;
              user.totalPoints = 0;
            }
          } else {
            user.giteeAccount = '';
            user.contributionCount = 0;
            user.totalPoints = 0;
          }
        } catch (error) {
          console.warn(`获取用户 ${user.userId} 的信息失败:`, error);
          user.giteeAccount = '';
          user.contributionCount = 0;
          user.totalPoints = 0;
        }
      }
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
    userList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

/** 查看用户贡献详情 */
const viewUserContributions = (user: any) => {
  // 设置当前用户并打开贡献对话框
  form.value = {
    userId: user.userId,
    nickName: user.nickName,
    giteeAccount: user.giteeAccount
  };
  showContributionDialog.value = true;
  refreshContributions();
};

/** 查询项目树结构 */
const getProjectTree = async () => {
  try {
    const res = await listProjectForOss({ pageNum: 1, pageSize: 1000 });
    if (res && res.data && res.data.rows) {
      // 将项目数据转换为树形结构，包含更多信息
      projectOptions.value = res.data.rows.map((project: any) => ({
        id: project.projectId,
        label: project.projectName,
        status: project.status,
        description: project.description,
        techStack: project.techStack,
        programmingLanguage: project.programmingLanguage,
        starCount: project.starCount,
        forkCount: project.forkCount,
        children: []
      }));
      enabledProjectOptions.value = projectOptions.value;
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
    projectOptions.value = [];
    enabledProjectOptions.value = [];
  }
};

/** 刷新项目树 */
const refreshProjectTree = async () => {
  isRefreshing.value = true;
  try {
    await getProjectTree();
    // 重置到第一页
    currentProjectPage.value = 1;
    proxy?.$modal.msgSuccess('项目列表已刷新');
  } finally {
    isRefreshing.value = false;
  }
};

// 项目分析功能
const showProjectAnalysis = () => {
  proxy?.$modal.msg('项目分析功能开发中...');
};

const showProjectTrends = () => {
  proxy?.$modal.msg('项目趋势分析功能开发中...');
};

const showProjectRecommendations = () => {
  proxy?.$modal.msg('项目推荐功能开发中...');
};

/** 按分类过滤 */
const filterByCategory = (category: string) => {
  activeCategory.value = category;
};

/** 获取项目状态颜色 */
const getProjectStatusColor = (status: string) => {
  switch (status) {
    case '2': return '#67C23A'; // 活跃 - 绿色
    case '3': return '#409EFF'; // 开发中 - 蓝色
    case '4': return '#E6A23C'; // 维护中 - 橙色
    default: return '#909399'; // 其他 - 灰色
  }
};

/** 查询部门下拉树结构 */
const getDeptTree = async () => {
  try {
    const res = await api.deptTreeSelect();
    deptOptions.value = res.data;
    enabledDeptOptions.value = filterDisabledDept(res.data);
  } catch (error) {
    console.error('获取部门树失败:', error);
    deptOptions.value = [];
    enabledDeptOptions.value = [];
  }
};

/** 过滤禁用的部门 */
const filterDisabledDept = (deptList: DeptTreeVO[]) => {
  return deptList.filter((dept) => {
    if (dept.disabled) {
      return false;
    }
    if (dept.children && dept.children.length) {
      dept.children = filterDisabledDept(dept.children);
    }
    return true;
  });
};

/** 节点单击事件 */
const handleNodeClick = (data: any) => {
  // 这里可以根据需要设置项目相关的查询条件
  // 暂时保持原有的部门查询逻辑
  queryParams.value.deptId = data.id;
  handleQuery();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

/** 重置按钮操作 */
const resetQuery = () => {
  dateRange.value = ['', ''];
  queryFormRef.value?.resetFields();
  queryParams.value.pageNum = 1;
  queryParams.value.deptId = undefined;
  projectTreeRef.value?.setCurrentKey(undefined);
  handleQuery();
};

/** 删除按钮操作 */
const handleDelete = async (row?: UserVO) => {
  const userIds = row?.userId || ids.value;
  const [err] = await to(proxy?.$modal.confirm('是否确认删除用户编号为"' + userIds + '"的数据项？') as any);
  if (!err) {
    await api.delUser(userIds);
    await getList();
    proxy?.$modal.msgSuccess('删除成功');
  }
};

/** 用户状态修改  */
const handleStatusChange = async (row: UserVO) => {
  const text = row.status === '0' ? '启用' : '停用';
  try {
    await proxy?.$modal.confirm('确认要"' + text + '""' + row.userName + '"用户吗?');
    await api.changeUserStatus(row.userId, row.status);
    proxy?.$modal.msgSuccess(text + '成功');
  } catch (err) {
    row.status = row.status === '0' ? '1' : '0';
  }
};

/** 跳转角色分配 */
const handleAuthRole = (row: UserVO) => {
  const userId = row.userId;
  router.push('/system/user-auth/role/' + userId);
};

/** 重置密码按钮操作 */
const handleResetPwd = async (row: UserVO) => {
  const [err, res] = await to(
    ElMessageBox.prompt('请输入"' + row.userName + '"的新密码', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      closeOnClickModal: false,
      inputPattern: /^.{5,20}$/,
      inputErrorMessage: '用户密码长度必须介于 5 和 20 之间',
      inputValidator: (value) => {
        if (/<|>|"|'|\||\\/.test(value)) {
          return '不能包含非法字符：< > " \' \\ |';
        }
      }
    })
  );
  if (!err && res) {
    await api.resetUserPwd(row.userId, res.value);
    proxy?.$modal.msgSuccess('修改成功，新密码是：' + res.value);
  }
};

/** 选择条数  */
const handleSelectionChange = (selection: UserVO[]) => {
  ids.value = selection.map((item) => item.userId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
};

/** 导入按钮操作 */
const handleImport = () => {
  upload.title = '用户导入';
  upload.open = true;
};

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download(
    'system/user/export',
    {
      ...queryParams.value
    },
    `user_${new Date().getTime()}.xlsx`
  );
};

/** 下载模板操作 */
const importTemplate = () => {
  proxy?.download('system/user/importTemplate', {}, `user_template_${new Date().getTime()}.xlsx`);
};

/**文件上传中处理 */
const handleFileUploadProgress = () => {
  upload.isUploading = true;
};

/** 文件上传成功处理 */
const handleFileSuccess = (response: any, file: UploadFile) => {
  upload.open = false;
  upload.isUploading = false;
  uploadRef.value?.handleRemove(file);
  ElMessageBox.alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + '</div>', '导入结果', {
    dangerouslyUseHTMLString: true
  });
  getList();
};

/** 提交上传文件 */
function submitFileForm() {
  uploadRef.value?.submit();
}

/** 重置操作表单 */
const reset = () => {
  form.value = { ...initFormData };
  userFormRef.value?.resetFields();
};

/** 取消按钮 */
const cancel = () => {
  dialog.visible = false;
  reset();
};

/** 新增按钮操作 */
const handleAdd = async () => {
  reset();
  const { data } = await api.getUser();
  dialog.visible = true;
  dialog.title = '新增用户';
  postOptions.value = data.posts;
  roleOptions.value = data.roles;
  form.value.password = initPassword.value.toString();
};

/** 修改按钮操作 */
const handleUpdate = async (row?: UserForm) => {
  reset();
  const userId = row?.userId || ids.value[0];
  const { data } = await api.getUser(userId);
  dialog.visible = true;
  dialog.title = '修改用户';
  Object.assign(form.value, data.user);
  postOptions.value = data.posts;
  roleOptions.value = data.roles;
  form.value.postIds = data.postIds;
  form.value.roleIds = data.roleIds;
  form.value.password = '';
  
  // 加载用户贡献数据
  await refreshContributions();
  
      // 如果有Gitee账号，尝试加载Gitee信息
    if (form.value.giteeAccount) {
      try {
        const giteeUserInfo = await getGiteeUserInfo(form.value.giteeAccount);
        giteeStats.value = {
          publicRepos: giteeUserInfo.data?.public_repos,
          followers: giteeUserInfo.data?.followers,
          following: giteeUserInfo.data?.following,
          starred: giteeUserInfo.data?.starred || 0
        };
      } catch (error) {
        console.error('获取Gitee信息失败:', error);
      }
    }
  
  // 加载成员信息
  try {
    const memberResponse = await getMemberInfo(Number(form.value.userId));
    memberInfo.value = memberResponse.data;
  } catch (error) {
    console.error('获取成员信息失败:', error);
  }
};

/** 提交按钮 */
const submitForm = () => {
  userFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      form.value.userId ? await api.updateUser(form.value) : await api.addUser(form.value);
      proxy?.$modal.msgSuccess('操作成功');
      dialog.visible = false;
      await getList();
    }
  });
};

/**
 * 关闭用户弹窗
 */
const closeDialog = () => {
  dialog.visible = false;
  resetForm();
};

/**
 * 重置表单
 */
const resetForm = () => {
  userFormRef.value?.resetFields();
  userFormRef.value?.clearValidate();

  form.value.id = undefined;
  form.value.status = '1';
};

onMounted(() => {
  getProjectTree();
  getDeptTree();
  getList();
  proxy?.getConfigKey('sys.user.initPassword').then((response) => {
    initPassword.value = response.data;
  });
  
  // 初始化高级筛选器
  nextTick(() => {
    if (maxStarCount.value > 0) {
      advancedFilters.value.starRange[1] = maxStarCount.value;
    }
  });
});

async function handleDeptChange(value: number | string) {
  const response = await optionselect(value);
  postOptions.value = response.data;
  form.value.postIds = [];
}
</script>

<style scoped>
.community-search-panel {
  height: calc(100vh - 200px);
  overflow-y: auto;
}

.smart-search-section {
  position: relative;
}

.search-tips, .search-suggestions {
  background: var(--el-bg-color-page);
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 8px;
  margin-top: 4px;
}

.tip-tag, .suggestion-tag {
  transition: all 0.2s ease;
}

.tip-tag:hover, .suggestion-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.advanced-search-panel {
  background: var(--el-bg-color-page);
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
  padding: 12px;
}

.filter-group {
  margin-bottom: 12px;
}

.filter-label {
  font-size: 12px;
  color: var(--el-text-color-regular);
  margin-bottom: 4px;
  font-weight: 500;
}

.smart-category-tags {
  margin-bottom: 16px;
}

.category-tag {
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.category-tag::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.category-tag:hover::before {
  left: 100%;
}

.active-tag {
  background-color: var(--el-color-primary) !important;
  color: white !important;
  border-color: var(--el-color-primary) !important;
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.project-tree-container {
  min-height: 300px;
  position: relative;
}

.loading-state, .empty-state {
  padding: 20px;
  text-align: center;
}

.enhanced-community-tree {
  max-height: 400px;
  overflow-y: auto;
}

.enhanced-tree-node-content {
  padding: 8px 0;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.enhanced-tree-node-content:hover {
  background-color: var(--el-color-primary-light-9);
}

.node-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.project-name {
  font-weight: 600;
  color: var(--el-text-color-primary);
  font-size: 14px;
}

.language-tag {
  font-size: 10px;
  padding: 2px 6px;
}

.node-description {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.4;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.node-stats {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}

.stat-item {
  display: flex;
  align-items: center;
  font-size: 11px;
  color: var(--el-text-color-secondary);
}

.node-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.enhanced-stats {
  background: var(--el-bg-color-page);
  border-radius: 6px;
  padding: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 12px;
}

.stat-card {
  text-align: center;
  padding: 8px;
  background: var(--el-bg-color);
  border-radius: 4px;
  border: 1px solid var(--el-border-color-light);
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 11px;
  color: var(--el-text-color-secondary);
}

.quick-actions {
  display: flex;
  justify-content: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 8px 0;
  border-top: 1px solid var(--el-border-color-light);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .community-search-panel {
    height: auto;
    max-height: 500px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .enhanced-tree-node-content {
    padding: 6px 0;
  }
  
  .project-name {
    font-size: 13px;
  }
  
  .node-description {
    font-size: 11px;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.search-tips, .search-suggestions {
  animation: fadeIn 0.3s ease;
}

.category-tag {
  animation: fadeIn 0.2s ease;
}

/* 滚动条样式 */
.enhanced-community-tree::-webkit-scrollbar {
  width: 6px;
}

.enhanced-community-tree::-webkit-scrollbar-track {
  background: var(--el-bg-color-page);
  border-radius: 3px;
}

.enhanced-community-tree::-webkit-scrollbar-thumb {
  background: var(--el-border-color);
  border-radius: 3px;
}

.enhanced-community-tree::-webkit-scrollbar-thumb:hover {
  background: var(--el-border-color-darker);
}

/* 贡献面板样式 */
.contribution-panel {
  background: var(--el-bg-color-page);
  border: 1px solid var(--el-border-color-light);
  border-radius: 6px;
  padding: 12px;
}

.contribution-header {
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.gitee-stats {
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 12px;
}

.gitee-stats .stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
}

.gitee-stats .stat-item {
  text-align: center;
  padding: 6px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 4px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.gitee-stats .stat-value {
  font-size: 14px;
  font-weight: 600;
  line-height: 1;
  margin-bottom: 2px;
}

.gitee-stats .stat-label {
  font-size: 10px;
  color: var(--el-text-color-secondary);
}

.contribution-stats .stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.contribution-stats .stat-item {
  text-align: center;
  padding: 8px;
  background: var(--el-bg-color);
  border-radius: 4px;
  border: 1px solid var(--el-border-color-light);
}

.contribution-stats .stat-value {
  font-size: 16px;
  font-weight: 600;
  line-height: 1;
  margin-bottom: 4px;
}

.contribution-stats .stat-label {
  font-size: 11px;
  color: var(--el-text-color-secondary);
}

.contribution-list {
  max-height: 200px;
  overflow-y: auto;
}

.contribution-item {
  padding: 8px;
  margin-bottom: 8px;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  transition: all 0.2s ease;
}

.contribution-item:hover {
  border-color: var(--el-color-primary-light-7);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.contribution-item:last-child {
  margin-bottom: 0;
}

.contribution-empty {
  text-align: center;
  padding: 20px;
  color: var(--el-text-color-secondary);
}

.contribution-management .el-card {
  margin-bottom: 16px;
}

.contribution-management .el-table {
  font-size: 12px;
}

/* 贡献记录列样式 */
.contribution-summary {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.contribution-points {
  font-size: 10px;
  color: var(--el-color-success);
}

.contribution-summary .el-tag {
  cursor: pointer;
  transition: all 0.2s ease;
}

.contribution-summary .el-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}


.mr-1 {
  margin-right: 4px;
}
</style>
