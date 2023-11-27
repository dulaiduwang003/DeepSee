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
      <div class="inner-div" @click="appVisible=!appVisible">
        <img src="../../../assets/menu/app.svg" class="inner-img" alt="">
      </div>
      <div class="inner-div" @click="isSetting=!isSetting">
        <img src="../../../assets/menu/setting.svg" class="inner-img" alt="">
      </div>
      <div class="inner-div">
        <img src="../../../assets/menu/miniPrograms.svg" class="inner-img" alt="">
      </div>
    </div>
  </div>
  <!-- 聊天话题-->
  <el-drawer size="320px" v-model="chatVisible" direction="ltr" style="background-color: #151515;"
  >
    <div class="drawer-button">
      <el-button type="primary" class="btn-submit" @click="handleCreatedDialogue">
        <el-icon size="20px">
          <ChatLineRound/>
        </el-icon>
        <span>创建新的话题</span>
      </el-button>
    </div>
    <div class="chat-scroll">
      <div class="scroll-body">
        <div class="my-style " v-for="(item,index) in props.cache.data" :key="index"
             @click="handleSelectedDialogue(index)">
          <div class="flex-container">
            <div class="flex-item">
              <img :src="require('../../../assets/menu/chat.svg')" alt="" class="image"
                   v-if="index===props.cache.index">
              <div v-else class="image"/>
              <div class="text">{{ item.title }}</div>
            </div>
            <div class="flex-item">
              <div class="delete-icon" @click.stop="handleDeleteDialogue(index)">
                <el-icon>
                  <DeleteFilled/>
                </el-icon>
              </div>
            </div>
          </div>
          <div class="timestamp">
            {{ conversionTime(item.updateTime) }}
          </div>
        </div>
      </div>
    </div>
  </el-drawer>
  <!--  偏好设置-->
  <el-dialog
      v-model="isSetting"
      title="偏好设置"
      width="600px"
      style="background-color: #151515;border-radius: 8px;height: 420px"
  >
    <div class="setting-container">
      <div>
        <div class="setting-row">
          <div>
            显示头像
          </div>
          <div>
            <el-upload
                :headers="requestHeader"
                action="http://localhost:9000/auth-api/user/upload/avatar"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :on-error="handleAvatarError"
                :before-upload="beforeAvatarUpload"
            >
              <img
                  :src="store.getters.userInfo.avatarUrl
                  ?
                  getDoMain()+store.getters.userInfo.avatarUrl
                  :
                  require('../../../assets/default_avatar.png')"
                  alt=""/>
            </el-upload>
          </div>
        </div>
        <div class="setting-row">
          <div>
            对话模型
          </div>
          <div>
            <el-select v-model="modelIndex" class="m-2" size="large" @change="onChangeChatModel">
              <el-option
                  v-for="item in props.model"
                  :key="item.modelIndex"
                  :label="item.modelName"
                  :value="item.modelIndex"
              />
            </el-select>
          </div>
        </div>
        <div class="setting-row">
          <div>
            聊天机器人昵称
          </div>
          <div>
            <el-input size="default" placeholder="请设置聊天机器人昵称" v-model="chatBotName" @change="onChangeChatBotName"/>
          </div>
        </div>
        <div class="setting-row">
          <div>
            绘图机器人昵称
          </div>
          <div>
            <el-input size="default" placeholder="请设置绘图机器人昵称" v-model="drawingBotName" @change="onChangeDrawingBotName"/>
          </div>
        </div>
        <div class="setting-row">
          <div>
            快捷发送
          </div>
          <div>
            <el-select v-model="shortcut" class="m-2" size="large" @change="onChangeShortcut">
              <el-option
                  v-for="(item,index) in shortcutKeyList"
                  :key="index"
                  :label="item.text"
                  :value="index"
              />
            </el-select>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
  <!-- 微应用-->
  <el-drawer size="320px" v-model="appVisible" direction="ltr" style="background-color:  #222222FF;">
    <div class="chat-scroll">
      <div class="scroll-body-100">
        <status-component text="暂未找到任何应用"/>
      </div>
    </div>
    <div class="chat-a">
      <div>
        公共市场
      </div>
      <div>
        创建应用
      </div>
    </div>
  </el-drawer>
</template>

<script setup>

import {defineEmits, defineProps, onMounted, ref} from "vue";
import {ChatLineRound, DeleteFilled} from "@element-plus/icons-vue";
import {conversionTime, getDoMain} from "@/utils/Utils";
import store from "@/store";
import {ElMessage} from "element-plus";
import {getCurrentUserInfo} from "@/api/auth";
import StatusComponent from "@/components/StatusComponent.vue";

const chatVisible = ref(false)

const appVisible = ref(false)

const shortcut = ref(0)

const modelIndex = ref(0)

const props = defineProps({
  cache: {
    type: Object
  },
  model: {
    type: Array
  }
});

const shortcutKeyList = ref([
  {
    text: 'shift + enter'
  },
  {
    text: 'ctrl + enter'
  },
  {
    text: 'alt + enter'
  }
])

const isSetting = ref(false)

const requestHeader = ref({
  Authorization: 'Bearer ' + localStorage.getItem("token")
})

const emits = defineEmits(['created-dialogue-event', 'selected-dialogue-event', 'delete-dialogue-event']);

const chatBotName = ref('')

const drawingBotName = ref('')

onMounted(() => {
  if (store.getters.userInfo) {
    //读取快捷键 以及 模型数据
    let data = store.getters.userSetting;
    modelIndex.value = data.modelIndex
    chatBotName.value = data.chatBotName
    drawingBotName.value = data.drawingBotName
    shortcut.value = data.shortcut
  }
})

//绑定聊天模型
const onChangeChatModel = (e) => {
  //获取原有配置
  let oldData = store.getters.userSetting;
  oldData.modelIndex = e
  store.commit("setUserSetting", oldData);
}

//绑定发送快捷键
const onChangeShortcut = (e) => {
  //获取原有配置
  let oldData = store.getters.userSetting;
  oldData.shortcut = e
  store.commit("setUserSetting", oldData);
}

//绑定CHAT
const onChangeChatBotName = () => {
  let value = chatBotName.value;
  if (value.trim().length > 0) {
    //获取原有配置
    let oldData = store.getters.userSetting;
    oldData.chatBotName = value
    store.commit("setUserSetting", oldData);
  }
}

//绑定DRAWING
const onChangeDrawingBotName = () => {
  let value = drawingBotName.value;
  if (value.trim().length > 0) {
    //获取原有配置
    let oldData = store.getters.userSetting;
    oldData.drawingBotName = value
    store.commit("setUserSetting", oldData);
  }
}

const handleAvatarSuccess = async () => {
  //刷新用户信息
  const res = await getCurrentUserInfo();
  store.commit("setUserInfo", res.data);
  ElMessage.success("个人资料已更新")
}

const handleCreatedDialogue = () => {
  emits('created-dialogue-event');
}
const handleSelectedDialogue = (index) => {
  emits('selected-dialogue-event', index);
}
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg') {
    ElMessage.error('头像必须是JPG格式')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('头像大小不能超过2MB')
    return false
  }
  return true
}

const handleAvatarError = () => {
  ElMessage.error("上传头像失败,请稍后重试")
}

const handleDeleteDialogue = (index) => {
  emits('delete-dialogue-event', index);
}


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
  background-color: #272727;
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
  border: none;
  height: 50px;
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
  background: #151515;
}

::-webkit-scrollbar-track {
  /* box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.1); */
  border-radius: 0;
  background: #151515;
  display: block;
}

:deep(.el-drawer__body) {
  padding: 0;
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
  margin-right: 8px;
}

.chat-scroll {;
  padding-top: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 85%;
  margin-top: 20px;
}

.scroll-body-100 {
  height: 75vh;
  overflow-y: scroll;
  color: white;
  width: 280px;
}


.scroll-body {
  height: 75vh;
  overflow-y: scroll;
  color: white;
  width: 280px;
}

.my-style {
  margin: 10px 0;
  background-color: #202020;
  border-radius: 5px;
  padding: 5px 10px;
  animation: explainAnimation 0.7s;
}


@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

.flex-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.flex-item {
  display: flex;
  align-items: center;
  padding-top: 5px;
}

.image {
  width: 20px;
  height: 20px;
  margin-top: 5px;
}

.text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 200px;
  margin-left: 7px;
  font-size: 14px;
  padding-top: 2px;
  color: #c5c5c5;
}

.timestamp {
  text-align: right;
  font-size: 10px;
  margin-top: 5px;
  color: #a9a9a9;
}

.delete-icon {
  padding-left: 10px;
  width: 30px;
  height: 20px;
}

.setting-container {
//background-color: #3e3e3e; height: 300px; border-radius: 6px
}

.setting-row {
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  border-bottom: 1px solid #202020;
  color: #a9a9a9;

  font-size: 13px;
}

.setting-row img {
  width: 30px;
  height: 30px;
  border-radius: 100%;
}

:deep(.el-input__wrapper) {
  background-color: #151515;
  width: 230px;
  color: white;
}

:deep(.el-select .el-input.is-focus .el-input__wrapper) {
  box-shadow: none;
  border: none;
}

:deep(.el-input__wrapper) {
  box-shadow: none;
  border: none;
  color: white;
}

:deep(.el-input__inner) {
  text-align: right;
  color: #bcbcbc;
  font-size: 14px;
  font-weight: 600;
}

.btn-clear {
  width: 120px;
  height: 30px
}

.chat-a {
  display: flex;
  justify-content: space-between;
  color: #a49aff;
  padding-top: 40px
}

.slide-animation {
  animation: slideEase 0.5s ease-in-out forwards;
}

@keyframes slideEase {
  0% {
    transform: translateX(-100px);
  }

  100% {
    transform: translateX(0);
  }
}

:deep(.el-input__inner) {
  background-color: #151515 !important;
}

:deep(.el-input__wrapper) {
  background-color: #151515 !important;
}
</style>
