<template>
  <div class="login">
    <!-- 动态背景 -->
    <div class="background-container">
      <!-- 网格背景 -->
      <div class="code-grid"></div>

      <!-- 光晕效果 -->
      <div class="glow glow-1"></div>
      <div class="glow glow-2"></div>

      <!-- 浮动元素 -->
      <div class="floating-element element-1 circle"></div>
      <div class="floating-element element-2 ring"></div>
      <div class="floating-element element-3 circle"></div>
      <div class="floating-element element-4 ring"></div>
      <div class="floating-element element-5 circle"></div>
      <div class="floating-element element-6 cat-shape"></div>
      <div class="floating-element element-7 cat-shape"></div>

      <!-- 粒子效果 -->
      <div class="particle" style="left: 10%; animation-duration: 8s"></div>
      <div class="particle" style="left: 20%; animation-duration: 12s"></div>
      <div class="particle" style="left: 30%; animation-duration: 10s"></div>
      <div class="particle" style="left: 40%; animation-duration: 14s"></div>
      <div class="particle" style="left: 50%; animation-duration: 9s"></div>
      <div class="particle" style="left: 60%; animation-duration: 11s"></div>
      <div class="particle" style="left: 70%; animation-duration: 13s"></div>
      <div class="particle" style="left: 80%; animation-duration: 15s"></div>
      <div class="particle" style="left: 90%; animation-duration: 7s"></div>

      <!-- 主Logo区域 -->
      <div class="logo-area">
        <div class="main-logo">
          <div class="logo-ring"></div>
        </div>
        <div class="logo-text">Dromara</div>
      </div>
    </div>

    <!-- 登录表单 -->
    <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
      <div class="title-box">
        <h3 class="title">{{ title }}</h3>
        <lang-select />
      </div>
      <el-form-item v-if="tenantEnabled" prop="tenantId">
        <el-select v-model="loginForm.tenantId" filterable :placeholder="proxy.$t('login.selectPlaceholder')" style="width: 100%">
          <el-option v-for="item in tenantList" :key="item.tenantId" :label="item.companyName" :value="item.tenantId"></el-option>
          <template #prefix><svg-icon icon-class="company" class="el-input__icon input-icon" /></template>
        </el-select>
      </el-form-item>
      <el-form-item prop="username">
        <el-input v-model="loginForm.username" type="text" size="large" auto-complete="off" :placeholder="proxy.$t('login.username')">
          <template #prefix><svg-icon icon-class="user" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          size="large"
          auto-complete="off"
          :placeholder="proxy.$t('login.password')"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="password" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item v-if="captchaEnabled" prop="code">
        <el-input
          v-model="loginForm.code"
          size="large"
          auto-complete="off"
          :placeholder="proxy.$t('login.code')"
          style="width: 63%"
          @keyup.enter="handleLogin"
        >
          <template #prefix><svg-icon icon-class="validCode" class="el-input__icon input-icon" /></template>
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" class="login-code-img" @click="getCode" />
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin: 0 0 25px 0">{{ proxy.$t('login.rememberPassword') }}</el-checkbox>
      <el-form-item style="float: right">
        <el-button circle :title="proxy.$t('login.social.wechat')" @click="doSocialLogin('wechat')">
          <svg-icon icon-class="wechat" />
        </el-button>
        <el-button circle :title="proxy.$t('login.social.maxkey')" @click="doSocialLogin('maxkey')">
          <svg-icon icon-class="maxkey" />
        </el-button>
        <el-button circle :title="proxy.$t('login.social.topiam')" @click="doSocialLogin('topiam')">
          <svg-icon icon-class="topiam" />
        </el-button>
        <el-button circle :title="proxy.$t('login.social.gitee')" @click="doSocialLogin('gitee')">
          <svg-icon icon-class="gitee" />
        </el-button>
        <el-button circle :title="proxy.$t('login.social.github')" @click="doSocialLogin('github')">
          <svg-icon icon-class="github" />
        </el-button>
      </el-form-item>
      <el-form-item style="width: 100%">
        <el-button :loading="loading" size="large" type="primary" style="width: 100%" @click.prevent="handleLogin">
          <span v-if="!loading">{{ proxy.$t('login.login') }}</span>
          <span v-else>{{ proxy.$t('login.logging') }}</span>
        </el-button>
        <div v-if="register" style="float: right">
          <router-link class="link-type" :to="'/register'">{{ proxy.$t('login.switchRegisterPage') }}</router-link>
        </div>
      </el-form-item>
    </el-form>

    <!--  底部  -->
    <div class="el-login-footer">
      <span>Copyright © 2025 - Dromara 开源社区 All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { getCodeImg, getTenantList } from '@/api/login';
import { authBinding } from '@/api/system/social/auth';
import { useUserStore } from '@/store/modules/user';
import { LoginData, TenantVO } from '@/api/types';
import { to } from 'await-to-js';
import { HttpStatus } from '@/enums/RespEnum';
import { useI18n } from 'vue-i18n';
import { getConfigKey } from '@/api/system/config';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const title = import.meta.env.VITE_APP_TITLE;
const userStore = useUserStore();
const router = useRouter();
const { t } = useI18n();

const loginForm = ref<LoginData>({
  tenantId: '000000',
  username: 'admin',
  password: 'admin123',
  rememberMe: false,
  code: '',
  uuid: ''
} as LoginData);

const loginRules: ElFormRules = {
  tenantId: [{ required: true, trigger: 'blur', message: t('login.rule.tenantId.required') }],
  username: [{ required: true, trigger: 'blur', message: t('login.rule.username.required') }],
  password: [{ required: true, trigger: 'blur', message: t('login.rule.password.required') }],
  code: [{ required: true, trigger: 'change', message: t('login.rule.code.required') }]
};

const codeUrl = ref('');
const loading = ref(false);
// 验证码开关
const captchaEnabled = ref(true);
// 租户开关
const tenantEnabled = ref(true);

// 注册开关
const register = ref(true);
const redirect = ref('/');
const loginRef = ref<ElFormInstance>();
// 租户列表
const tenantList = ref<TenantVO[]>([]);

watch(
  () => router.currentRoute.value,
  (newRoute: any) => {
    redirect.value = newRoute.query && newRoute.query.redirect && decodeURIComponent(newRoute.query.redirect);
  },
  { immediate: true }
);

const handleLogin = () => {
  loginRef.value?.validate(async (valid: boolean, fields: any) => {
    if (valid) {
      loading.value = true;
      // 勾选了需要记住密码设置在 localStorage 中设置记住用户名和密码
      if (loginForm.value.rememberMe) {
        localStorage.setItem('tenantId', String(loginForm.value.tenantId));
        localStorage.setItem('username', String(loginForm.value.username));
        localStorage.setItem('password', String(loginForm.value.password));
        localStorage.setItem('rememberMe', String(loginForm.value.rememberMe));
      } else {
        // 否则移除
        localStorage.removeItem('tenantId');
        localStorage.removeItem('username');
        localStorage.removeItem('password');
        localStorage.removeItem('rememberMe');
      }
      // 调用action的登录方法
      const [err] = await to(userStore.login(loginForm.value));
      if (!err) {
        const redirectUrl = redirect.value || '/';
        await router.push(redirectUrl);
        loading.value = false;
      } else {
        loading.value = false;
        // 重新获取验证码
        if (captchaEnabled.value) {
          await getCode();
        }
      }
    } else {
      console.log('error submit!', fields);
    }
  });
};

/**
 * 获取验证码
 */
const getCode = async () => {
  const res = await getCodeImg();
  const { data } = res;
  captchaEnabled.value = data.captchaEnabled === undefined ? true : data.captchaEnabled;
  if (captchaEnabled.value) {
    codeUrl.value = 'data:image/gif;base64,' + data.img;
    loginForm.value.uuid = data.uuid;
  }
};

const getLoginData = () => {
  const tenantId = localStorage.getItem('tenantId');
  const username = localStorage.getItem('username');
  const password = localStorage.getItem('password');
  const rememberMe = localStorage.getItem('rememberMe');
  loginForm.value = {
    tenantId: tenantId === null ? String(loginForm.value.tenantId) : tenantId,
    username: username === null ? String(loginForm.value.username) : username,
    password: password === null ? String(loginForm.value.password) : String(password),
    rememberMe: rememberMe === null ? false : Boolean(rememberMe)
  } as LoginData;
};

/**
 * 获取租户列表
 */
const initTenantList = async () => {
  const { data } = await getTenantList(false);
  tenantEnabled.value = data.tenantEnabled === undefined ? true : data.tenantEnabled;
  if (tenantEnabled.value) {
    tenantList.value = data.voList;
    if (tenantList.value != null && tenantList.value.length !== 0) {
      loginForm.value.tenantId = tenantList.value[0].tenantId;
    }
  }
};

/**
 * 第三方登录
 * @param type
 */
const doSocialLogin = (type: string) => {
  authBinding(type, loginForm.value.tenantId).then((res: any) => {
    if (res.code === HttpStatus.SUCCESS) {
      // 获取授权地址跳转
      window.location.href = res.data;
    } else {
      ElMessage.error(res.msg);
    }
  });
};

onMounted(() => {
  getCode();
  initTenantList();
  getLoginData();
  // 拉取注册开关
  getConfigKey('sys.account.registerUser').then((res) => {
    register.value = res.data === 'true';
  });
});
</script>

<style lang="scss" scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #87ceeb 0%, #4a90e2 25%, #6b8fc1 75%, #5a7ba8 100%);
  position: relative;
  font-family: 'Arial', sans-serif;
}

.background-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

/* 动态几何图形 */
.floating-element {
  position: absolute;
  opacity: 0.1;
  animation: float 8s ease-in-out infinite;
}

.circle {
  border-radius: 50%;
  background: linear-gradient(45deg, #ffd700, #ffa500);
}

.ring {
  border-radius: 50%;
  border: 4px solid #ffd700;
  background: transparent;
}

.cat-shape {
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50% 50% 50% 50% / 60% 60% 40% 40%;
  position: relative;
}

.cat-shape::before,
.cat-shape::after {
  content: '';
  position: absolute;
  width: 20px;
  height: 20px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50% 50% 50% 0;
  top: -10px;
}

.cat-shape::before {
  left: 10px;
  transform: rotate(-45deg);
}

.cat-shape::after {
  right: 10px;
  transform: rotate(45deg);
}

/* 位置和大小变化 */
.element-1 {
  top: 10%;
  left: 15%;
  width: 80px;
  height: 80px;
  animation-delay: 0s;
}

.element-2 {
  top: 60%;
  right: 20%;
  width: 120px;
  height: 120px;
  animation-delay: 2s;
}

.element-3 {
  bottom: 20%;
  left: 10%;
  width: 100px;
  height: 100px;
  animation-delay: 4s;
}

.element-4 {
  top: 30%;
  right: 10%;
  width: 60px;
  height: 60px;
  animation-delay: 1s;
}

.element-5 {
  bottom: 40%;
  right: 40%;
  width: 90px;
  height: 90px;
  animation-delay: 3s;
}

.element-6 {
  top: 20%;
  left: 60%;
  animation-delay: 5s;
}

.element-7 {
  bottom: 60%;
  left: 40%;
  animation-delay: 6s;
}

/* 代码网格背景 */
.code-grid {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px), linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 50px 50px;
  opacity: 0.6;
  animation: gridMove 20s linear infinite;
}

/* 光晕效果 */
.glow {
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 215, 0, 0.2) 0%, transparent 70%);
  animation: pulse 4s ease-in-out infinite;
}

.glow-1 {
  top: 20%;
  left: 20%;
  animation-delay: 0s;
}

.glow-2 {
  bottom: 20%;
  right: 20%;
  animation-delay: 2s;
}

/* 粒子效果 */
.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: #ffd700;
  border-radius: 50%;
  opacity: 0.8;
  animation: particle 10s linear infinite;
}

.particle:nth-child(odd) {
  animation-delay: 0s;
}

.particle:nth-child(even) {
  animation-delay: 5s;
}

/* 主Logo区域 */
.logo-area {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  z-index: 2;
  opacity: 0.15;
}

.main-logo {
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50% 50% 50% 50% / 60% 60% 40% 40%;
  position: relative;
  margin: 0 auto 20px;
  animation: logoFloat 6s ease-in-out infinite;
}

.main-logo::before,
.main-logo::after {
  content: '';
  position: absolute;
  width: 60px;
  height: 60px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50% 50% 50% 0;
  top: -30px;
}

.main-logo::before {
  left: 40px;
  transform: rotate(-45deg);
}

.main-logo::after {
  right: 40px;
  transform: rotate(45deg);
}

.logo-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 250px;
  height: 120px;
  border: 8px solid rgba(255, 215, 0, 0.3);
  border-radius: 50%;
  animation: ringRotate 12s linear infinite;
}

.logo-text {
  font-size: 48px;
  font-weight: bold;
  color: rgba(255, 255, 255, 0.2);
  text-shadow: 0 0 20px rgba(255, 215, 0, 0.3);
  letter-spacing: 2px;
}

.title-box {
  display: flex;

  .title {
    margin: 0px auto 30px auto;
    text-align: center;
    color: #707070;
  }

  :deep(.lang-select--style) {
    line-height: 0;
    color: #7483a3;
  }
}

.login-form {
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  width: 400px;
  padding: 25px 25px 5px 25px;
  z-index: 100;
  position: relative;

  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }

  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
}

.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}

.login-code {
  width: 33%;
  height: 40px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial, serif;
  font-size: 12px;
  letter-spacing: 1px;
  z-index: 10;
}

.login-code-img {
  height: 40px;
  padding-left: 12px;
}

/* 动画定义 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.2;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.3;
  }
}

@keyframes gridMove {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 50px);
  }
}

@keyframes particle {
  0% {
    transform: translateY(100vh) translateX(0px);
    opacity: 0;
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.8;
  }
  100% {
    transform: translateY(-100px) translateX(100px);
    opacity: 0;
  }
}

@keyframes logoFloat {
  0%,
  100% {
    transform: translate(-50%, -50%) translateY(0px);
  }
  50% {
    transform: translate(-50%, -50%) translateY(-10px);
  }
}

@keyframes ringRotate {
  0% {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  100% {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-logo {
    width: 120px;
    height: 120px;
  }

  .logo-ring {
    width: 150px;
    height: 75px;
  }

  .logo-text {
    font-size: 24px;
  }

  .login-form {
    width: 350px;
    margin: 0 20px;
  }
}
</style>
