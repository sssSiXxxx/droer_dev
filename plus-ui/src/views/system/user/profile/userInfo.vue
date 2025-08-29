<template>
  <el-form ref="userRef" :model="userForm" :rules="rules" label-width="100px">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="用户昵称" prop="nickName">
          <el-input v-model="userForm.nickName" maxlength="30" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="手机号码" prop="phonenumber">
          <el-input v-model="userForm.phonenumber" maxlength="11" />
        </el-form-item>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" maxlength="50" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="性别">
          <el-radio-group v-model="userForm.sex">
            <el-radio value="0">男</el-radio>
            <el-radio value="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-col>
    </el-row>

    <el-divider content-position="left">开源平台信息</el-divider>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="Gitee账号" prop="giteeAccount">
          <el-input v-model="userForm.giteeAccount" placeholder="请输入Gitee用户名" maxlength="50" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="GitHub账号" prop="githubUsername">
          <el-input v-model="userForm.githubUsername" placeholder="请输入GitHub用户名" maxlength="100" />
        </el-form-item>
      </el-col>
    </el-row>

    <el-divider content-position="left">联系方式</el-divider>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="微信号" prop="wechat">
          <el-input v-model="userForm.wechat" placeholder="请输入微信号" maxlength="50" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="QQ号" prop="qq">
          <el-input v-model="userForm.qq" placeholder="请输入QQ号" maxlength="20" />
        </el-form-item>
      </el-col>
    </el-row>

    <el-form-item label="个人博客" prop="blog">
      <el-input v-model="userForm.blog" placeholder="请输入个人博客链接" maxlength="255" />
    </el-form-item>

    <el-divider content-position="left">职业信息</el-divider>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="公司/组织" prop="company">
          <el-input v-model="userForm.company" placeholder="请输入公司或组织名称" maxlength="100" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="职位" prop="position">
          <el-input v-model="userForm.position" placeholder="请输入职位名称" maxlength="100" />
        </el-form-item>
      </el-col>
    </el-row>
    
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="所在地区" prop="location">
          <el-input v-model="userForm.location" placeholder="请输入所在地区" maxlength="100" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="工作年限" prop="experienceYears">
          <el-select v-model="userForm.experienceYears" placeholder="请选择工作年限">
            <el-option label="应届生" :value="0" />
            <el-option label="1年以内" :value="1" />
            <el-option label="1-3年" :value="2" />
            <el-option label="3-5年" :value="3" />
            <el-option label="5-10年" :value="5" />
            <el-option label="10年以上" :value="10" />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>

    <el-form-item label="技能标签" prop="skills">
      <el-input 
        v-model="userForm.skills" 
        type="textarea" 
        :rows="3"
        placeholder="请输入技能标签，用逗号分隔，如：Java,Vue,Spring Boot,MySQL" 
        maxlength="500" 
      />
    </el-form-item>

    <el-form-item label="个人简介" prop="bio">
      <el-input 
        v-model="userForm.bio" 
        type="textarea" 
        :rows="4"
        placeholder="请简单介绍一下自己..." 
        maxlength="500" 
      />
    </el-form-item>
    
    <el-form-item>
      <el-button type="primary" @click="submit">保存</el-button>
      <el-button type="danger" @click="close">关闭</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { updateUserProfile } from '@/api/system/user';
import { propTypes } from '@/utils/propTypes';

const props = defineProps({
  user: propTypes.any.isRequired
});
const userForm = computed(() => props.user);
const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const userRef = ref<ElFormInstance>();
const rule: ElFormRules = {
  nickName: [{ required: true, message: '用户昵称不能为空', trigger: 'blur' }],
  email: [
    { required: true, message: '邮箱地址不能为空', trigger: 'blur' },
    {
      type: 'email',
      message: '请输入正确的邮箱地址',
      trigger: ['blur', 'change']
    }
  ],
  phonenumber: [
    {
      required: true,
      message: '手机号码不能为空',
      trigger: 'blur'
    },
    { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
};
const rules = ref<ElFormRules>(rule);

/** 提交按钮 */
const submit = () => {
  userRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      await updateUserProfile(props.user);
      proxy?.$modal.msgSuccess('修改成功');
    }
  });
};
/** 关闭按钮 */
const close = () => {
  proxy?.$tab.closePage();
};
</script>
