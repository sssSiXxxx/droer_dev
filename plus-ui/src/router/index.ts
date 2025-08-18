import { createWebHistory, createRouter, RouteRecordRaw } from 'vue-router';
/* Layout */
import Layout from '@/layout/index.vue';

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  {
    path: '/social-callback',
    hidden: true,
    component: () => import('@/layout/components/SocialCallback/index.vue')
  },
  {
    path: '/login',
    component: () => import('@/views/login.vue'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register.vue'),
    hidden: true
  },
  {
    path: '/:pathMatch(.*)*',
    component: () => import('@/views/error/404.vue'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401.vue'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: '/index',
        component: () => import('@/views/index.vue'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile',
        component: () => import('@/views/system/user/profile/index.vue'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
];

// 动态路由，基于用户权限动态去加载
export const dynamicRoutes: RouteRecordRaw[] = [
  {
    path: '/system',
    component: Layout,
    name: 'System',
    meta: { title: '系统管理', icon: 'system' },
    children: [
      {
        path: 'user',
        component: () => import('@/views/system/user/index.vue'),
        name: 'User',
        meta: { title: '用户管理', icon: 'user' }
      },
      {
        path: 'user-test',
        component: () => import('@/views/system/user/test.vue'),
        name: 'UserTest',
        meta: { title: '用户管理测试', icon: 'user' }
      },
      {
        path: 'user-simple',
        component: () => import('@/views/system/user/simple.vue'),
        name: 'UserSimple',
        meta: { title: '用户管理简化版', icon: 'user' }
      },
      {
        path: 'user-diagnostic',
        component: () => import('@/views/system/user/diagnostic.vue'),
        name: 'UserDiagnostic',
        meta: { title: '用户管理诊断', icon: 'user' }
      },
      {
        path: 'gitee-test',
        component: () => import('@/views/system/user/gitee-test.vue'),
        name: 'GiteeTest',
        meta: { title: 'Gitee API测试', icon: 'user' }
      },
      {
        path: 'oss',
        component: () => import('@/views/system/oss/index.vue'),
        name: 'Oss',
        meta: { title: '文档管理', icon: 'upload' }
      }
    ]
  },
  {
    path: '/osc',
    component: Layout,
    name: 'OscClient',
    meta: { title: '开源社区', icon: 'github' },
    permissions: ['osc:projectCreate:list'],
    children: [
      {
        path: 'myProject',
        component: () => import('@/views/osc/myProject/index.vue'),
        name: 'MyProjectClient',
        meta: { title: '我的创建', icon: 'list' },
        permissions: ['osc:projectCreate:list']
      },
      {
        path: 'projectCreate',
        component: () => import('@/views/osc/projectCreate/index.vue'),
        name: 'ProjectCreateClient',
        meta: { title: '创建项目', icon: 'edit' },
        hidden: true,
        permissions: ['osc:projectCreate:list']
      },
      {
        path: 'projectDraft',
        component: () => import('@/views/osc/projectDraft/index.vue'),
        name: 'ProjectDraftClient',
        meta: { title: '草稿箱', icon: 'document' },
        hidden: true,
        permissions: ['osc:projectCreate:list']
      },
      {
        path: 'projectPhase',
        component: () => import('@/views/osc/projectPhase/index.vue'),
        name: 'ProjectPhaseClient',
        meta: { title: '进度追踪', icon: 'time' },
        permissions: ['osc:projectPhase:list']
      },
      {
        path: 'projectAudit',
        component: () => import('@/views/osc/projectAudit/index.vue'),
        name: 'ProjectAuditClient',
        meta: { title: '项目审核', icon: 'check' },
        permissions: ['osc:projectAudit:list']
      },
      {
        path: 'project',
        component: () => import('@/views/osc/project/index.vue'),
        name: 'Project',
        meta: { title: '项目管理', icon: 'project' },
        permissions: ['osc:project:list']
      },
      {
        path: 'project/detail/:id',
        component: () => import('@/views/osc/project/detail.vue'),
        name: 'ProjectDetail',
        meta: { title: '项目详情', icon: 'project', hidden: true },
        permissions: ['osc:project:query']
      },
      {
        path: 'projectMember',
        component: () => import('@/views/osc/projectMember/index.vue'),
        name: 'ProjectMember',
        meta: { title: '人员项目管理', icon: 'user' },
        permissions: ['osc:projectMember:list']
      },
      {
        path: 'projectMemberVisualization',
        component: () => import('@/views/osc/projectMember/visualization.vue'),
        name: 'ProjectMemberVisualization',
        meta: { title: '项目人员可视化', icon: 'chart' },
        permissions: ['osc:projectMember:list']
      },
      {
        path: 'projectMember/test',
        component: () => import('@/views/osc/projectMember/test.vue'),
        name: 'ProjectMemberTest',
        meta: { title: '项目成员测试', icon: 'test' },
        hidden: true
      },
      {
        path: 'projectMember/debug',
        component: () => import('@/views/osc/projectMember/debug.vue'),
        name: 'ProjectMemberDebug',
        meta: { title: '项目成员调试', icon: 'debug' },
        hidden: true
      },
      {
        path: 'projectMember/simple',
        component: () => import('@/views/osc/projectMember/simple.vue'),
        name: 'ProjectMemberSimple',
        meta: { title: '项目成员简化测试', icon: 'test' },
        hidden: true
      },
      {
        path: 'projectMember/basic',
        component: () => import('@/views/osc/projectMember/basic.vue'),
        name: 'ProjectMemberBasic',
        meta: { title: '项目成员基础测试', icon: 'test' },
        hidden: true
      }
    ]
  },
  // 项目成员关联路由
  {
    path: '/osc/projectMember',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/osc/projectMember/index.vue'),
        name: 'ProjectMember',
        meta: { title: '项目成员关联', icon: 'user' }
      }
    ]
  }
];

/**
 * 创建路由
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.VITE_APP_CONTEXT_PATH),
  routes: constantRoutes,
  // 刷新时，滚动条位置还原
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    }
    return { top: 0 };
  }
});

export default router;
