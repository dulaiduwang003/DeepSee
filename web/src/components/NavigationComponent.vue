<template>
  <div class="container">
    <div class="flex-wrapper">
      <div class="flex-wrapper-model">
        <!--  logo-->
        <div class="logo-wrapper">
          <img alt="" class="logo" src="../assets/logo.svg">
          <span>SUPER AI</span>
        </div>
        <!--  功能按键-->
        <div class="right-layout" v-if="store.getters.userInfo">
          <!--可用币控件-->
          <div class="conversation-div">
            <div class="conversation-text">
              TS 币: 9999
            </div>
            <div class="conversation-icon">
              <img alt="" src="../assets/images/public/recharge.svg">
            </div>
          </div>
          <el-avatar :size="30"
                     :src="store.getters.userInfo.avatar"/>
          <!--    更多-->
          <div class="more-div">
            <el-dropdown ref="dropdown" trigger="contextmenu">
              <img alt="" src="../assets/images/public/more.svg" @click="handleClickMore">
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>GitHub</el-dropdown-item>
                  <el-dropdown-item>设置</el-dropdown-item>
                  <el-dropdown-item>控制台</el-dropdown-item>
                  <el-dropdown-item>公告</el-dropdown-item>
                  <el-dropdown-item>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div class="btn-join-login" @click="authDialogIsVisible=true" v-else>
          登录
        </div>
      </div>
    </div>
  </div>
  <auth-dialog-component :is-visible="authDialogIsVisible" @close-dialog-event="handleCloseDialog"/>
</template>
<script setup>

import {ref} from "vue";
import store from "@/store";
import AuthDialogComponent from "@/components/auth/AuthDialogComponent.vue";

const dropdown = ref();

//登录框可见性
const authDialogIsVisible = ref(false)

const handleClickMore = () => {
  dropdown.value.handleOpen()
};

const handleCloseDialog = () => {
  authDialogIsVisible.value = false
}

</script>

<style scoped>
.container {
  position: fixed;
  top: 0;
  z-index: 2;
  width: 100%;

}

.flex-wrapper {
  width: 100%;
  padding: 5px 0;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #ffffff;
  box-shadow: 0 4px 4px rgba(218, 209, 209, 0.25);
}

.flex-wrapper-model {
  padding: 5px 8px;
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;

}


.right-layout {
  display: flex;
  justify-content: space-between;
  align-items: center
}

.conversation-div {
  margin-right: 20px;
  background-color: #F2F3F5;
  font-size: 13px;
  border-radius: 20px;
  padding: 0 5px;
  display: flex;
  justify-content: space-between;
  align-items: center
}

.conversation-text {
  padding: 0 10px;
  font-family: SmileySans, serif;
}

.conversation-icon {
  padding-top: 5px
}

.conversation-icon img {
  width: 25px;
  height: 25px
}

.logo {
  width: 35px;
  height: 35px
}

.more-div {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-left: 20px
}

.more-div img {
  width: 23px;
  height: 23px;
  cursor: pointer;
}


:deep(.el-dropdown-menu__item:hover, .el-dropdown-menu__item:focus ) {
  background: rgba(140, 223, 216, 0.15) !important;
  box-shadow: none !important;
  color: #7365FF !important;
}

.logo-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: SmileySans, serif;
}

.logo-wrapper span {
  padding-left: 10px;
  font-size: 20px
}

.btn-join-login {
  width: 80px;
  height: 34px;
  background: #7365FF;
  border-radius: 6px;
  padding: 0 10px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
}

</style>
