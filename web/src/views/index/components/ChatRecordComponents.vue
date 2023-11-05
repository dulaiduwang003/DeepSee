<template>
  <!--  触发控件-->
  <div class="chat-record-div">
    <div class="custom-div">
      <!--      GITHUB-->
      <div class="inner-div">
        <img src="../../../assets/menu/github.svg" class="inner-img" alt="">
      </div>
      <div class="inner-div" @click="chatVisible=!chatVisible">
        <img src="../../../assets/menu/chat.svg" class="inner-img" alt="">
      </div>
      <div class="inner-div">
        <img src="../../../assets/menu/app.svg" class="inner-img" alt="">
      </div>
      <div class="inner-div">
        <img src="../../../assets/menu/setting.svg" class="inner-img" alt="">
      </div>
      <div class="inner-div">
        <img src="../../../assets/menu/miniPrograms.svg" class="inner-img" alt="">
      </div>
    </div>

  </div>
  <el-drawer size="320px" v-model="chatVisible" direction="ltr" style="background-color: #2D2B31;"
  >
    <div class="drawer-logo">
      <img :src="require('../../../assets/logo.svg')" alt="">
    </div>
    <div class="drawer-button">
      <el-button type="primary" class="btn-submit">
        创建新的话题
      </el-button>
    </div>
    <div class="chat-scroll">
      <div class="scroll-body">

      </div>
    </div>
  </el-drawer>
</template>

<script setup>

import {ref} from "vue";

const chatVisible = ref(false)

</script>

<style scoped>
.chat-record-div {
  position: fixed;
  top: 50px;
  left: -4px;
  z-index: 20;
}

.custom-div {
  height: 100vh;
  background-color: #2D2B31;
  width: 60px;
}

.inner-div {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80px;

}

.inner-img {
  width: 25px;
  height: 25px;
}

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

::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}


::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: #2D2B31;
}

::-webkit-scrollbar-track {
  /* box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.1); */
  border-radius: 0;
  background: #2D2B31;
  display: block;
}

:deep(.el-drawer__body) {
  padding: 0px;
}

.drawer-logo {
  display: flex;
  justify-content: center;
  align-items: center
}

.drawer-logo img {
  width: 60px;
  height: 60px
}

.drawer-button {
  text-align: center;
  padding: 50px 25px 20px;
}

.chat-scroll {
  display: flex;
  justify-content: center;
  align-items: center;
  padding-left: 5px
}

.scroll-body {
  min-height: 61vh;
  overflow-y: scroll;
  background-color: #232127;
  width: 280px;
}
</style>
