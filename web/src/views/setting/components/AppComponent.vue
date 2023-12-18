<script setup>
//快捷键数组
import {onMounted, ref} from "vue";
import store from "@/store";

const isPreset = ref(false)

const presetWordIssue = ref('')

const presetWordAnswer = ref('')

/**
 * 更新持久化
 * @param e
 */
const onChangeIsPreset = (e) => {
  let oldData = store.getters.userSetting;
  oldData.isPreset = e
  store.commit("setUserSetting", oldData);
}

/**
 * 更新预设词 (问题)
 */

const onBlurPresetWordIssue = () => {
  let value = presetWordIssue.value;
  console.log("问题")
  console.log(value)
  //获取原有配置
  let oldData = store.getters.userSetting;
  if (value.trim()) {
    oldData.presetWordIssue = value
  } else {
    oldData.presetWordIssue = ''
  }
  store.commit("setUserSetting", oldData);
}

const onBlurPresetWordAnswer = () => {
  let value = presetWordAnswer.value;
  //获取原有配置
  let oldData = store.getters.userSetting;
  if (value.trim()) {
    oldData.presetWordAnswer = value
  } else {
    oldData.presetWordAnswer = ''
  }
  store.commit("setUserSetting", oldData);
}

onMounted(async () => {
  if (store.getters.userInfo) {
    //读取个人设置信息
    let userSetting = await store.getters.userSetting;
    presetWordIssue.value = userSetting.presetWordIssue
    presetWordAnswer.value = userSetting.presetWordAnswer
    isPreset.value = userSetting.isPreset
  }
})
</script>

<template>
  <div class="appearance-title">
    提示词
  </div>
  <div class="appearance-body">
    <div class="div-width">
      <div class="div-row input-textarea">
        <div>
          预设词(问题)
        </div>
        <div>
          <el-input class="input-width"
                    @blur="onBlurPresetWordIssue"
                    v-model="presetWordIssue"
                    type="textarea"
                    placeholder="前置预设词(可选)"/>
        </div>
      </div>
    </div>
    <div class="div-width">
      <div class="div-row input-textarea">
        <div>
          预设词(回答)
        </div>
        <div>
          <el-input class="input-width"
                    @blur="onBlurPresetWordAnswer"
                    v-model="presetWordAnswer"
                    type="textarea"
                    placeholder="前置预设词(可选)"/>
        </div>
      </div>
    </div>
    <div class="div-width">
      <div class="div-row">
        <div>
          启用
        </div>
        <div>
          <el-switch v-model="isPreset"
                     @change="onChangeIsPreset"
                     style="
                     --el-switch-on-color: #993bff;
                     --el-switch-off-color: #ff4949"
          />
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
  margin-bottom: 30px;
}

.input-width {
  width: 160px;
}

.div-width {
  width: 100%
}

:deep(.el-textarea__inner) {
  height: 130px;
  background-color: #F2F2F2;
  box-shadow: none !important;
  resize: none !important;
  outline: none !important;

}

.div-row {
  padding-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  font-size: 14px;
  border-bottom: 1px solid #f3f3f3;
  margin-bottom: 15px;
  color: #8b8b8b;
}

.input-textarea {
  align-items: flex-start
}
</style>
