<template>
  <el-dialog
      v-model="isVisible"
      :show-close="false"
      style="border-radius: 8px"
      align-center
      width="680px"
      @close="onClose"
  >
    <div class="auth-container">
      <div class="auth-image">
        <div>
          <img :src="require('../../assets/images/public/figure.svg')" alt="">
          <div class="figure-text">
            TRY SUPER AI QUICKLY
          </div>
        </div>
      </div>
      <div class="auth-form">
        <!--登录-->
        <div>
          <div class="auth-form-switch">
            <div :class="isStatus===1?'':'switch-active'" @click="onChangeStatus(1)"
                 v-if="isStatus!==3&&isStatus!==4">
              邮箱登录
            </div>
            <div :class="isStatus===2?'':'switch-active'" @click="onChangeStatus(2)"
                 v-if="isStatus!==3&&isStatus!==4">
              微信登录
            </div>
            <div v-if="isStatus===3" class="extra">
              注册账号
            </div>
            <div v-if="isStatus===4" class="extra">
              找回密码
            </div>
          </div>
          <div v-if="isStatus===1">
            <email-login-component @change-status-event="onChangeStatus" @on-close="onClose"/>
          </div>
          <div v-if="isStatus===2">
            <we-chat-login-component/>
          </div>
          <div v-if="isStatus===3">
            <email-enroll-component @change-status-event="onChangeStatus"/>
          </div>
          <div v-if="isStatus===4">
            <email-forget-component @change-status-event="onChangeStatus"/>
          </div>
        </div>
        <!--注册-->
      </div>
    </div>
  </el-dialog>


</template>

<script setup>
// 是否显示登录框
import {defineEmits, defineProps, ref, watch} from "vue";
import EmailLoginComponent from "@/components/auth/components/EmailLoginComponent.vue";
import WeChatLoginComponent from "@/components/auth/components/WeChatLoginComponent.vue";
import EmailEnrollComponent from "@/components/auth/components/EmailEnrollComponent.vue";
import EmailForgetComponent from "@/components/auth/components/EmailForgetComponent.vue";

//1登录 2微信 3注册 4找回密码
const isStatus = ref(1)

const props = defineProps({
  //是否显示
  isVisible: {
    type: Boolean,
    default: false,
  }
});

const isVisible = ref(false)

watch(
    () => props.isVisible,
    (newValue) => {
      isVisible.value = newValue;
    },
    {
      immediate: true,
    }
);



const emits = defineEmits(['close-dialog-event']);


/**
 * 切换SWITCH状态
 * @param index
 */
const onChangeStatus = (index) => {
  if (isStatus.value !== index) {
    isStatus.value = index
  }
}
/**
 * 关闭登录框
 */
const onClose = () => {
  emits('close-dialog-event');
}


</script>

<style scoped>

.auth-container {
  height: 320px;
  display: flex;
  justify-content: space-between;
  padding-right: 10px;

}


.auth-image {
  width: 50%;
  display: flex;
  align-items: center;
  justify-content: center
}

.auth-image img {
  width: 200px;
  height: 200px
}

.auth-form {
  width: 50%;
  padding-left: 20px;
}

.switch-active {
  font-weight: 500;
  color: #7a7a7a;
  cursor: pointer;
}

.auth-form-switch {
  width: 300px;
  padding: 0 70px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  font-weight: 550;
  margin-bottom: 30px;
}

.figure-text {
  text-align: center;
  font-weight: 550;
  font-size: 18px;
  font-family: SmileySans, serif;
}

.extra {
  padding-left: 35px
}
</style>

