<script setup>
//快捷键数组
import {onMounted, ref} from "vue";
import store from "@/store";
import {getAiModelList} from "@/api/chat";
import {ElMessage} from "element-plus";
//快捷键配置列表
const shortcutList = ref([
  {
    text: 'enter'
  },
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
//模型坐标
const modelIndex = ref(0)
//机器人坐标
const chatBotName = ref('')
//快捷键坐标
const shortcutIndex = ref(0)
//获取模型列表
const modelList = ref([])
//输出速率
const outputRate = ref(0)
//上下文
const memorySize = ref(0)
//函数
const rowSize = ref(4)

/**
 * 更新快捷键
 * @param e
 */
const onChangeShortcut = (e) => {
  //获取原有配置
  let oldData = store.getters.userSetting;
  oldData.shortcutIndex = e
  store.commit("setUserSetting", oldData);
}

/**
 * 切换模型
 * @param e
 */
const onChangeAiModel = (e) => {
  //获取原有配置
  let oldData = store.getters.userSetting;
  oldData.modelIndex = e
  store.commit("setUserSetting", oldData);
}


/**
 * 更新BOT昵称
 */
const onBlurChatBotName = () => {
  let value = chatBotName.value;
  if (value.trim().length > 0) {
    //获取原有配置
    let oldData = store.getters.userSetting;
    oldData.chatBotName = value
    store.commit("setUserSetting", oldData);
  } else {
    ElMessage({
      type: 'info',
      message: 'BOT昵称不能为空'
    })
  }
}


/**
 * 更新速率
 */
const onBlurOutputRate = () => {
  let value = outputRate.value;
  if (value && value > 0) {
    //获取原有配置
    let oldData = store.getters.userSetting;
    oldData.outputRate = value
    store.commit("setUserSetting", oldData);
  } else {
    ElMessage({
      type: 'info',
      message: '回复速率至少需要大于0'
    })
  }
}
/**
 * 更新记忆
 */
const onBlurRowSize = () => {
  let value = rowSize.value;
  if (value && value > 0) {
    //获取原有配置
    let oldData = store.getters.userSetting;
    oldData.rowSize = value
    store.commit("setUserSetting", oldData);
  } else {
    ElMessage({
      type: 'info',
      message: '记忆至少需要大于0'
    })
  }
}

/**
 * 更新记忆
 */
const onBlurMemory = () => {
  let value = memorySize.value;
  if (value && value > 0) {
    //获取原有配置
    let oldData = store.getters.userSetting;
    oldData.memorySize = value
    store.commit("setUserSetting", oldData);
  } else {
    ElMessage({
      type: 'info',
      message: '上下文至少大于0'
    })
  }
}


onMounted(async () => {
  if (store.getters.userInfo) {
    //读取个人设置信息
    let userSetting = store.getters.userSetting;
    modelIndex.value = userSetting.modelIndex
    chatBotName.value = userSetting.chatBotName
    shortcutIndex.value = userSetting.shortcutIndex
    outputRate.value = userSetting.outputRate
    memorySize.value = userSetting.memorySize
    rowSize.value = userSetting.rowSize
    //获取模型列表
    const {data} = await getAiModelList();
    modelList.value = data
  }
})
</script>

<template>
  <div class="appearance-title">
    聊天配置
  </div>
  <div class="appearance-body">
    <div class="div-width">
      <div class="div-row">
        <div>
          快捷发送
        </div>
        <div>
          <el-select class="input-width" v-model="shortcutIndex" @change="onChangeShortcut">
            <el-option
                v-for="(item,index) in shortcutList"
                :key="index"
                :label="item.text"
                :value="index"
            />
          </el-select>
        </div>
      </div>
    </div>
    <div class="div-width">
      <div class="div-row">
        <div>
          对话模型
        </div>
        <div>
          <el-select class="input-width" v-model="modelIndex" @change="onChangeAiModel">
            <el-option
                v-for="item in modelList"
                :key="item.modelIndex"
                :label="item.modelName"
                :value="item.modelIndex"
            />
          </el-select>
        </div>
      </div>
    </div>
    <div class="div-width">
      <div class="div-row">
        <div>
          BOT昵称
        </div>
        <div>
          <el-input class="input-width" placeholder="请设置BOT昵称" v-model="chatBotName" @blur="onBlurChatBotName"/>
        </div>
      </div>
    </div>
    <div class="div-width">
      <div class="div-row">
        <div>
          上下文压缩阈值
        </div>
        <div>
          <el-input class="input-width" placeholder="请设置上下文压缩阈值" type="number" v-model="memorySize"
                    @blur="onBlurMemory"/>
        </div>
      </div>
    </div>
    <div class="div-width">
      <div class="div-row">
        <div>
          记忆行数
        </div>
        <div>
          <el-input class="input-width" placeholder="请设置记忆行数" type="number" v-model="rowSize"
                    @blur="onBlurRowSize"/>
        </div>
      </div>
    </div>
    <div class="div-width">
      <div class="div-row">
        <div>
          对话输出速率
        </div>
        <div>
          <el-input class="input-width" placeholder="请设置回复速率" type="number" v-model="outputRate"
                    @blur="onBlurOutputRate"/>
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
:deep(.el-textarea__inner) {
  height: 130px;
  background-color: #F2F2F2;
  box-shadow: none !important;
  resize: none !important;
  outline: none !important;

}
</style>
