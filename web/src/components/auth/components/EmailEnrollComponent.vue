<script setup>

import {Connection, Platform, UserFilled} from "@element-plus/icons-vue";
import {defineEmits, ref} from "vue";
import {validateEmail} from "@/utils/Utils";
import {ElMessage} from "element-plus";
import {emailEnroll, sendEmailCode} from "@/api/auth";

const emits = defineEmits(['change-status-event']);

//获取验证码按钮OBJ
const btnCodeObj = ref({
  text: '获取验证码',
  disabled: false,
})

//验证码倒计时控制器
const countdown = ref(null)

//注册按钮加载
const btnLoad = ref(false)

//提交表单
const form = ref({
  email: '',
  password: '',
  code: ''
})

/**
 * 获取验证码
 * @returns {Promise<void>}
 */
const handleGetCaptcha = async () => {
  const {email} = form.value;
  if (!email) {
    ElMessage.warning("注册邮箱不能为空");
    return
  }
  if (!validateEmail(email)) {
    ElMessage.warning("注册邮箱地址格式不正确");
    return
  }
  let seconds = 120;
  try {
    btnCodeObj.value.text = "正在发送中";
    btnCodeObj.value.disabled = true
    //发送验证码
    await sendEmailCode({email});
    ElMessage.success("邮箱验证码已发送 注意查收");
  } catch (e) {
    btnCodeObj.value.text = "获取验证码";
    btnCodeObj.value.disabled = false
    return
  }
  countdown.value = setInterval(() => {
    if (seconds === 0) {
      clearInterval(countdown.value);
      countdown.value = null;
      btnCodeObj.value.disabled = false;
      btnCodeObj.value.text = "获取验证码";
    } else {
      seconds--;
      btnCodeObj.value.text = `${seconds}` + "后获取";
    }
  }, 1000);


}

/**
 * 注册账号
 * @returns {Promise<void>}
 */
const handleEnroll = async () => {
  const {email, password, code} = form.value;
  if (!email) {
    ElMessage.warning("注册邮箱不能为空");
    return
  }
  if (!password) {
    ElMessage.warning("登录密码不能为空");
    return
  }
  if (!code) {
    ElMessage.warning("注册验证码不能为空");
    return
  }
  if (!validateEmail(email)) {
    ElMessage.warning("注册邮箱地址格式不正确");
    return
  }
  //调用注册API
  btnLoad.value = true
  try {
    await emailEnroll({email, password, code});
    ElMessage.success(email + " 注册成功!");
  } finally {
    btnLoad.value = false
  }
}

/**
 * 跳转至登录
 */
const callParentOnChangeStatusMethod = () => {
  emits('change-status-event', 1);
};

</script>

<template>
  <el-form ref="formRef" size="large">
    <el-form-item prop="email">
      <el-input
          v-model="form.email"
          type="text"
          clearable
          placeholder="请输入注册邮箱"
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
          placeholder="请输入注册密码"
          show-password
          autocomplete="“off”"
      >
        <template #prefix>
          <el-icon :size="16" color="#909090">
            <Platform/>
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="code">
      <el-input
          maxlength="6"
          minlength="6"
          ref="codeRef"
          type="text"
          clearable
          placeholder="请输入验证码"
          autocomplete="“off”"
          v-model="form.code"
      >
        <template #prefix>
          <el-icon :size="16">
            <Connection/>
          </el-icon>
        </template>
        <template #append>
          <div class="code-layout">
            <el-button
                :disabled="btnCodeObj.disabled"
                v-text="btnCodeObj.text"
                @click="handleGetCaptcha"
            />
          </div>
        </template>
      </el-input>
      <div class="forgot-password" @click="callParentOnChangeStatusMethod">
        返回登录
      </div>
    </el-form-item>
    <el-form-item>
      <el-button
          :loading="btnLoad"
          type="primary"
          size="large"
          class="btn-submit"
          @click="handleEnroll"
      >
        快速注册
      </el-button>
    </el-form-item>
  </el-form>
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
  background-color: #7365FF;
  outline: 0;
}


.code-layout {
  padding-left: 10px;
  background: none;
}

.forgot-password {
  text-align: right;
  width: 100%;
  font-size: 12px;
  text-decoration: underline;
  cursor: pointer;

}

</style>
