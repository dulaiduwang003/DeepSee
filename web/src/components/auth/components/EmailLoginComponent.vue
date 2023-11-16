<script setup>
import {defineEmits, ref} from 'vue';
import {Platform, UserFilled} from "@element-plus/icons-vue";
import {ElMessage, ElNotification} from "element-plus";
import {emailLogin, getCurrentUserInfo} from "@/api/auth";
import {validateEmail} from "@/utils/Utils";
import store from "@/store";


const emits = defineEmits(['change-status-event', 'on-close']);

/**
 * 跳转注册
 */
const callParentOnChangeStatusMethod = (index) => {
  emits('change-status-event', index);
};

//提交表单
const form = ref({
  email: '',
  password: '',
  code: ''
})

//登录加载按钮
const btnLoad = ref(false)

/**
 * 登录
 */
const handleLogin = async () => {
  const {email, password} = form.value;
  if (!email || !password) {
    ElMessage.warning("登录邮箱或密码不能为空");
    return
  }
  if (!validateEmail(email)) {
    ElMessage.warning("登录邮箱地址格式不正确");
    return
  }
  try {
    //按钮动画
    btnLoad.value = true
    //执行登录
    const {data} = await emailLogin({email, password});
    //设置身份令牌
    localStorage.setItem("token", data);
    //获取用户信息数据
    const res = await getCurrentUserInfo();
    store.commit("setUserInfo", res.data);
    ElNotification({
      title: "登录成功",
      message: '欢迎体验TS-GPT',
      type: "success",
    });
    emits('on-close');
    location.reload();
  } catch (e) {
    btnLoad.value = false
  }
}


</script>

<template>
  <el-form ref="formRef" size="large">
    <el-form-item prop="email">
      <el-input
          v-model="form.email"
          type="text"
          clearable
          placeholder="请输入邮箱"
          autocomplete="“off”"
      >
        <template #prefix>
          <el-icon :size="16" color="#909090">
            <UserFilled/>
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          show-password
          autocomplete="“off”"
      >
        <template #prefix>
          <el-icon :size="16" color="#909090">
            <Platform/>
          </el-icon>
        </template>
      </el-input>
      <div class="forgot-password" @click="callParentOnChangeStatusMethod(4)">
        忘记密码
      </div>
    </el-form-item>
    <el-form-item>
      <el-button
          :loading="btnLoad"
          type="primary"
          size="large"
          class="btn-submit"
          @click="handleLogin"
      >
        快速登录
      </el-button>
    </el-form-item>
  </el-form>
  <div class="to-enroll-div">暂无账号?
    <text @click="callParentOnChangeStatusMethod(3)">注册新用户</text>
  </div>
</template>

<style scoped>
.btn-submit {
  width: 100%;
  background-color: #7365FF;
  border: none
}

.btn-submit:hover,
.btn-submit:focus,
.btn-submit:active {
  background-color: #5044c0;
  outline: 0;
}

.to-enroll-div {
  font-size: 12px;
  text-align: center;
  margin-top: 50px;
  cursor: pointer;
}

.to-enroll-div text {
  color: #7365FF;
}

.forgot-password {
  text-align: right;
  width: 100%;
  font-size: 12px;
  text-decoration: underline;
  cursor: pointer;
}


</style>
