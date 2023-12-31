<script setup>


import store from "@/store";
import {getOssDoMain, getServiceDoMain} from "@/utils/Utils";
import {onMounted, ref} from "vue";
import {getCurrentUserInfo, uploadAvatar, uploadNickName} from "@/api/auth";
import {ElMessage} from "element-plus";

const requestHeader = ref({
  Authorization: 'Bearer ' + localStorage.getItem("token")
})

//用户昵称
const nickName = ref('')
//临时昵称
const nickNameTemp = ref('')

/**
 * 更新用户信息
 * @returns {Promise<void>}
 */
const handleRenewalUserInfo = async () => {
  //刷新用户信息
  const res = await getCurrentUserInfo();
  store.commit("setUserInfo", res.data);
  ElMessage.success("个人资料已更新")
}

/**
 * 上传失败
 * @returns {Promise<void>}
 */
const handleAvatarError = () => {
  ElMessage.error("上传头像失败,请稍后重试")
}

/**
 * 上传头像检查
 * @param rawFile
 * @returns {boolean}
 */
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

/**
 * 更新用户昵称
 */
const handleBlurNickName = async () => {
  if (nickNameTemp.value && nickNameTemp.value !== nickName.value) {
    //更新昵称
    await uploadNickName({nickName: nickNameTemp.value});
    await handleRenewalUserInfo()
    nickName.value = JSON.stringify(nickNameTemp)
  }
}


onMounted(() => {
  if (store.getters.userInfo) {
    let name = store.getters.userInfo.nickName;
    nickName.value = name
    nickNameTemp.value = name
  }
})

</script>

<template>

  <div class="appearance-body">
    <div class="user-info-container">
      <el-upload
          :headers="requestHeader"
          :action="getServiceDoMain()+uploadAvatar()"
          :show-file-list="false"
          :on-success="handleRenewalUserInfo"
          :on-error="handleAvatarError"
          :before-upload="beforeAvatarUpload"
      >
        <img class="user-avatar"
             :src="store.getters.userInfo?getOssDoMain()+ store.getters.userInfo.avatar:require('../../../assets/app/default-avatar.png')"
             alt="">
      </el-upload>
      <div class="user-info">
        <div class="user-nickname">
          <el-input class="nick-name-input" placeholder="填写昵称" v-model="nickNameTemp" maxlength="8"
                    @blur="handleBlurNickName"/>
        </div>
        <div class="user-label-model">
          <div class="user-label">{{ store.getters.userInfo.type === 'ADMIN' ? 'SUPER' : '偶像练习生' }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>

.appearance-title {
  font-size: 14px;
  padding-left: 10px
}

.appearance-body {
  background-color: white;
  margin-top: 10px;
  border-radius: 8px;
  padding: 12px;
  display: flex;
  align-items: center;
  margin-bottom: 30px;

}

.user-info-container {
  display: flex;
  align-items: center
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 100%
}

.user-info {
  padding-left: 20px;
}

.user-nickname {
  display: flex;
  align-items: center;
}

.nick-name-input {
  width: 180px;
}

.nick-name-input >>> .el-input__wrapper {
  box-shadow: none;
}

.nick-name-input >>> .el-input__wrapper > .el-input__inner {
  font-size: 18px;
  font-weight: 550;
}

.btn-upload {
  background-color: #c793ff;
  color: white;
  font-size: 12px
}

.user-label-model {
  display: flex;
  padding-left: 10px
}

.user-label {
  font-size: 13px;
  color: #ffffff;
  padding: 2px 10px;
  background-color: #ff93e1;
  border-radius: 5px;
  margin-top: 10px
}
</style>
