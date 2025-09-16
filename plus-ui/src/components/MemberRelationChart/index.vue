<template>
  <div class="member-relation-chart">
    <div class="chart-container" ref="chartContainer">
      <div class="project-center">
        <div class="project-node">
          <el-avatar :size="60" :src="projectAvatar">
            {{ projectInitial }}
          </el-avatar>
          <div class="project-info">
            <div class="project-name">{{ projectName }}</div>
            <div class="member-count">{{ totalMembers }}人</div>
          </div>
        </div>
      </div>

      <!-- 角色环 -->
      <div v-for="(roleGroup, index) in roleGroups" :key="roleGroup.roleCode" class="role-ring" :style="getRingStyle(index)">
        <div class="role-label">
          <el-tag :color="roleGroup.color" effect="dark" size="small">
            {{ roleGroup.roleName }}
          </el-tag>
        </div>

        <!-- 成员节点 -->
        <div
          v-for="(member, memberIndex) in roleGroup.members"
          :key="member.memberId"
          class="member-node"
          :style="getMemberNodeStyle(index, memberIndex, roleGroup.members.length)"
          :title="`${member.memberName} - ${member.memberEmail}`"
          @click="$emit('memberClick', member)"
        >
          <el-avatar :size="32" :src="getMemberAvatar(member)">
            {{ getMemberInitial(member) }}
          </el-avatar>
          <div class="member-name">{{ member.memberName }}</div>

          <!-- 连接线 -->
          <div class="connection-line" :style="getConnectionLineStyle(index, memberIndex, roleGroup.members.length)"></div>
        </div>
      </div>
    </div>

    <!-- 图例 -->
    <div class="chart-legend">
      <div class="legend-title">角色说明</div>
      <div class="legend-items">
        <div v-for="roleGroup in roleGroups" :key="roleGroup.roleCode" class="legend-item">
          <div class="legend-color" :style="{ backgroundColor: roleGroup.color }"></div>
          <span>{{ roleGroup.roleName }} ({{ roleGroup.memberCount }})</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="MemberRelationChart" lang="ts">
interface Member {
  memberId: number;
  memberName: string;
  memberEmail: string;
  role: string;
  isActive: string;
  contributionScore: number;
}

interface RoleGroup {
  roleCode: string;
  roleName: string;
  memberCount: number;
  members: Member[];
  color: string;
}

interface Props {
  projectName: string;
  projectCode?: string;
  roleGroups: RoleGroup[];
  totalMembers: number;
}

const props = withDefaults(defineProps<Props>(), {
  projectCode: '',
  roleGroups: () => [],
  totalMembers: 0
});

const emit = defineEmits<{
  memberClick: [member: Member];
}>();

const chartContainer = ref();

// 计算属性
const projectAvatar = computed(() => {
  return `https://api.dicebear.com/7.x/initials/svg?seed=${props.projectName}`;
});

const projectInitial = computed(() => {
  return props.projectName ? props.projectName.charAt(0).toUpperCase() : 'P';
});

// 获取环的样式
function getRingStyle(ringIndex: number) {
  const radius = 120 + ringIndex * 80; // 基础半径 + 每环增加80px
  return {
    width: `${radius * 2}px`,
    height: `${radius * 2}px`,
    position: 'absolute',
    left: '50%',
    top: '50%',
    transform: 'translate(-50%, -50%)',
    borderRadius: '50%',
    border: `2px dashed ${props.roleGroups[ringIndex]?.color || '#ddd'}`,
    opacity: 0.3
  };
}

// 获取成员节点样式
function getMemberNodeStyle(ringIndex: number, memberIndex: number, totalMembers: number) {
  const radius = 120 + ringIndex * 80;
  const angle = (memberIndex / totalMembers) * 2 * Math.PI;
  const x = Math.cos(angle) * radius;
  const y = Math.sin(angle) * radius;

  return {
    position: 'absolute',
    left: '50%',
    top: '50%',
    transform: `translate(calc(-50% + ${x}px), calc(-50% + ${y}px))`,
    zIndex: 10
  };
}

// 获取连接线样式
function getConnectionLineStyle(ringIndex: number, memberIndex: number, totalMembers: number) {
  const radius = 120 + ringIndex * 80;
  const angle = (memberIndex / totalMembers) * 2 * Math.PI;
  const length = radius - 30; // 连接线长度

  return {
    position: 'absolute',
    left: '50%',
    top: '50%',
    width: `${length}px`,
    height: '2px',
    backgroundColor: props.roleGroups[ringIndex]?.color || '#ddd',
    transformOrigin: 'left center',
    transform: `translate(-${length}px, -50%) rotate(${angle}rad)`,
    opacity: 0.6,
    zIndex: 1
  };
}

// 获取成员头像
function getMemberAvatar(member: Member) {
  return `https://api.dicebear.com/7.x/initials/svg?seed=${member.memberName}`;
}

// 获取成员姓名首字母
function getMemberInitial(member: Member) {
  return member.memberName ? member.memberName.charAt(0).toUpperCase() : '?';
}
</script>

<style scoped>
.member-relation-chart {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.chart-container {
  position: relative;
  width: 600px;
  height: 600px;
  margin: 40px auto;
}

.project-center {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 20;
}

.project-node {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border: 3px solid #409eff;
}

.project-info {
  text-align: center;
}

.project-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.member-count {
  font-size: 12px;
  color: #909399;
}

.role-ring {
  pointer-events: none;
}

.role-label {
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 15;
}

.member-node {
  cursor: pointer;
  transition: all 0.3s ease;
  pointer-events: auto;
}

.member-node:hover {
  transform: translate(calc(-50% + var(--x, 0px)), calc(-50% + var(--y, 0px))) scale(1.1);
  z-index: 25 !important;
}

.member-node .el-avatar {
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.member-name {
  font-size: 10px;
  color: #606266;
  text-align: center;
  margin-top: 4px;
  white-space: nowrap;
  max-width: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
}

.connection-line {
  pointer-events: none;
}

.chart-legend {
  background: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  min-width: 200px;
}

.legend-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.legend-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #606266;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chart-container {
    width: 400px;
    height: 400px;
  }

  .member-node {
    transform: scale(0.8);
  }

  .project-node {
    transform: scale(0.9);
  }
}
</style>
