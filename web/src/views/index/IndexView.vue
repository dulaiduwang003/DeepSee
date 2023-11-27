<template>
  <div class="body">
    <!--  聊天控件-->
    <div class="body" ref="scrollRef" @wheel="handleScroll">
      <!-- 内容-->
      <div class="content-body" v-if="chatTemplate.length>0">
        <div v-for="(item,index) in chatTemplate" :key="index" class="slide-animation">
          <div class="user-chat-model" v-if="item.issue">
            <el-popover placement="top-end" :width="160">
              <popover-component @copy-content-event="handleCopyContent(item.issue)"
                                 @delete-chat-event="handleDeleteChat(index)"/>
              <template #reference>
                <div class="user-chat">
                  {{ item.issue }}
                </div>
              </template>
            </el-popover>
            <div>
              <el-avatar :icon="UserFilled" :size="40"
                         :src="store.getters.userInfo.avatarUrl ? getDoMain()+store.getters.userInfo.avatarUrl :  require('../../assets/default_avatar.png')"/>
            </div>
          </div>
          <div class="bot-chat-model">
            <div>
              <el-avatar :size="40"
                         :src="item.type==='CHAT'?require('../../assets/logo.svg'):require('../../assets/drawing.svg')"/>
            </div>
            <div class="bot-chat-width" v-if="item.answer">
              <!--机器人标签-->
              <bot-head-component :date="item.date" :name="item.name" :type="item.type"
                                  :progress="item.progress?item.progress:undefined"/>
              <!--普通模板-->
              <el-popover placement="top-end" :width="160" v-if="item.type==='CHAT'">
                <popover-component @copy-content-event="handleCopyContent(item.answer)"
                                   @delete-chat-event="handleDeleteChat(index)"/>
                <template #reference>

                  <div class="bot-chat" :style="{ maxWidth: calculateWidth(item.answer.length) }">
                    <v-md-editor
                        :model-value="item.answer"
                        mode="preview"
                        @copy-code-success="handleCopyCode"
                    />
                  </div>
                </template>
              </el-popover>
              <!--操作模板-->
              <outcome-component v-else text="你好" :status="item.status" :outcome="item.outcome"
                                 @preview-image-event="previewImage"/>
            </div>
            <div class="bot-chat" v-else>
              <div class="dot-flex">
                <div class="dot_0"></div>
                <div class="dot_1"></div>
                <div class="dot_2"></div>
                <div class="dot_3"></div>
                <div class="dot_4"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 默认打开模板-->
      <div v-else>
        <index-template-component/>
      </div>
      <!-- 联想组件-->
      <directives-component :display="isCommand" :directives="instructionResult" @clear="rollbackValue"
                            @push-sd-task-event="handlePushSdTask"/>
      <!-- 输入组件-->
      <div class="footer" v-if="store.getters.userInfo">
        <div class="association-div">
          <div v-for="(item,index) in issueNew" :key="index" class=" slide-animation">
            {{ item.context }}
          </div>
        </div>
        <div class="footer-bar">
          <InputFormField
              @update:inputText="$event=>(onChangeInputDirectives($event))"
              ref="inputRef"
              :aiLoading="isInput"
              @onSubmit="handleSendMessage"
          />
        </div>
      </div>
      <!-- 暂停输出-->
      <div class="suspend" v-show="isInput" @click="handleCloseSocket">
        <el-icon :size="16">
          <VideoPause/>
        </el-icon>
        <div>暂停输出</div>
      </div>
    </div>
    <!--  左侧控件-->
    <chat-record-components
        :cache="chatCache"
        :model="aiModelList"
        @created-dialogue-event="handleCreatedDialogue"
        @selected-dialogue-event="handleSelectedDialogue"/>
  </div>
  <!--  图片预览-->
  <el-dialog v-model="previewImageData.dialogTableVisible" style="background-color: transparent" width="45%"
             :show-close="false">
    <el-image style="width: 100%;" :src="previewImageData.image"
              :preview-src-list="[previewImageData.image]"
              fit="contain"/>
    <div style="display: flex;color: #978eff;justify-content: right;font-size: 13px;text-decoration: underline">
      <span style="cursor: pointer"
            @click="downloadImage(previewImageData.image)">下载此图片</span>
    </div>
  </el-dialog>
</template>

<script setup>


import {UserFilled, VideoPause} from "@element-plus/icons-vue";
import IndexTemplateComponent from "@/components/IndexTemplateComponent.vue";
import {nextTick, onBeforeUnmount, onMounted, ref} from "vue";
import {ElNotification} from "element-plus";
import ChatRecordComponents from "@/views/index/components/ChatRecordComponents.vue";
import InputFormField from "@/views/index/components/InputComponents.vue";
import {
  getChatCache,
  getCurrentFormattedTime,
  getDoMain,
  getSdDrawingTaskList,
  setChatCache,
  setSdDrawingTaskList
} from "@/utils/Utils";
import store from "@/store";
import {getAiModelList} from "@/api/chat";
import PopoverComponent from "@/views/index/components/popoverComponent.vue";
import {getSdParam, getSdTask, pushSdTask} from "@/api/drawing";
import directivesComponent from "@/views/index/components/DirectivesComponent.vue";
import OutcomeComponent from "@/views/index/components/OutcomeComponent.vue";
import BotHeadComponent from "@/views/index/components/BotHeadComponent.vue";

const scrollRef = ref(null);


const previewImageData = ref({
  dialogTableVisible: false,
  image: ''
})

const previewImage = (image) => {
  previewImageData.value.image = image
  previewImageData.value.dialogTableVisible = true
}


//是否锁定消息 禁止用户发送
const isInput = ref(false)

//聊天数据
const chatTemplate = ref([])

const inputText = ref('')

const inputRef = ref(null)

const wssAddress = ref(process.env.VUE_APP_WSS + '/chat-api/chat/')

const webSocket = ref(null)

//聊天数据缓存
const chatCache = ref(null)
const randomlyGenerated = () => {
  const result = [];
  for (let i = 0; i < 6; i++) {
    result.push(String.fromCharCode(Math.floor(Math.random() * 26) + 65));
  }
  return result.join('');
}
onMounted(() => {
  if (store.getters.userInfo) {
    //读取用户缓存
    const data = getChatCache();

    //刷新联想词
    getRandomLenovo()
    if (!data) {
      //如果没有任何数据则需要加载一条新对话
      let id = randomlyGenerated();
      chatCache.value = {
        //当前聊天坐标
        index: 0,
        id: id,
        data: [
          {
            title: "新主题",
            updateTime: Date.now(),
            content: [],
            id: id
          }
        ]
      }
      //保存一次缓存数据
      setChatCache(chatCache.value)
    } else {
      //如果存在
      chatCache.value = data
      chatTemplate.value = chatCache.value.data[chatCache.value.index].content
    }
    initData()
    //读取缓存
    const taskList = getSdDrawingTaskList();
    if (taskList) {
      taskList.forEach(t => {
        createdSdDrawingListener(t)
      })
    }
  }
})


onBeforeUnmount(() => {
  sdInterval.value.forEach(s => {
    clearInterval(s);
  })
})

const aiModelList = ref([])

const sdParameter = ref({
  modelList: [],
  samplerList: [],
  stepsList: []
})
const initData = async () => {

  const res1 = await getAiModelList();
  if (res1.data) {
    aiModelList.value = res1.data
  }
  let res2 = await getSdParam();
  if (res2.data) {
    const {modelList, samplerList, stepsList} = res2.data;
    sdParameter.value.modelList = modelList
    sdParameter.value.samplerList = samplerList
    sdParameter.value.stepsList = stepsList
  }
}

const handlePushSdTask = async (form) => {
  chatTemplate.value.push({
    isError: false,
    type: 'SD',
    name: store.getters.userSetting.drawingBotName,
    date: getCurrentFormattedTime(),
    statue: 0,
    progress: 0,
    answer: 'a'
  });
  appendMessage()
  let number = chatTemplate.value.length - 1;
  //这里得到taskId 需要存储在缓存中
  let {data} = await pushSdTask(form);
  //获取本地任务集合
  let sdDrawingTaskList = getSdDrawingTaskList();
  let chatCacheIndex = chatCache.value.index;
  const taskData = {
    taskId: data,
    //记录聊天插入顺序
    index: number,
    //主题ID
    topicId: chatCache.value.data[chatCacheIndex].id
  };
  if (sdDrawingTaskList && sdDrawingTaskList.length > 0) {
    //写入任务
    sdDrawingTaskList.push(taskData)
    //存储任务ID
    setSdDrawingTaskList(sdDrawingTaskList)
  } else {
    setSdDrawingTaskList([taskData])
  }

  createdSdDrawingListener(taskData)

  ElNotification({title: "绘图", message: '任务已提交', type: "success",});
}

const sdTimerList = ref([])

const sdInterval = ref([])

const createdSdDrawingListener = (data) => {
  // 添加任务到定时器列表
  sdTimerList.value.push({
    taskId: data.taskId,
    mark: false
  });

  // 设定定时器
  let interval = setInterval(async () => {
    let index = sdTimerList.value.findIndex(item => item.taskId === data.taskId);

    if (!sdTimerList.value[index].mark) {
      sdTimerList.value[index].mark = true;
      let res = await getSdTask(data.taskId);

      if (res.data) {
        let status = res.data.status;

        chatTemplate.value[data.index].status = status;

        switch (status) {
          case 1: // 实时预览
            if (res.data.extra) {
              const {current_image, progress} = res.data.extra;
              if (current_image) {
                chatTemplate.value[data.index].outcome = 'data:image/png;base64,' + current_image;
                chatTemplate.value[data.index].progress = progress;
                appendMessage();
              }
            }
            break;
          case 2: // 成功
            if (res.data.image) {
              chatTemplate.value[data.index].outcome = getDoMain() + res.data.image;
              chatTemplate.value[data.index].progress = 100;
              clearSdTask(data.taskId);
              clearInterval(interval);
              sdTimerList.value.splice(index, 1);
              appendMessage();
            } else {
              // 如果不是成功状态，回到默认状态或者错误处理逻辑这里就不需要再次清除定时器了，因为只有当状态为2时才清除定时器。
            }
            break;
          case -1: // 失败
            chatTemplate.value[data.index].progress = 0;
            clearSdTask(data.taskId);
            clearInterval(interval);
            sdTimerList.value.splice(index, 1);
            appendMessage();
            break;
          default: // 其他状态处理逻辑，根据实际需要添加。
        }
      } else { // 如果res.data为空，清除定时器并返回。这里不需要再次清除定时器，因为只有在状态为2时才清除定时器。其他状态处理逻辑，根据实际需要添加。
        clearSdTask(data.taskId);
        clearInterval(interval);
        sdTimerList.value.splice(index, 1);
        appendMessage();
      }
    } else { // 如果已经标记过，清除定时器并返回。这里不需要再次清除定时器，因为只有在状态为2时才清除定时器。其他状态处理逻辑，根据实际需要添加。
      clearSdTask(data.taskId);
      clearInterval(interval);
      sdTimerList.value.splice(index, 1);
      appendMessage();
    }
  }, 4000); // 设定定时器间隔为4秒，你可以根据实际需要调整这个值。其他状态处理逻辑，根据实际需要添加。
}

//清除任务缓存
const clearSdTask = (taskId) => {
  //获取任务缓存
  let arr = getSdDrawingTaskList();
  //查找任务对象
  const newArr = arr.filter(item => item.taskId !== taskId);
  setSdDrawingTaskList(newArr)

}

const appendMessage = () => {
  const index = chatCache.value.index;
  const valueElement = chatTemplate.value[chatTemplate.value.length - 1];
  if (valueElement.answer) {
    if (valueElement.issue) {
      chatCache.value.data[index].title = valueElement.issue.trim().slice(0, 25);
    } else {
      chatCache.value.data[index].title = '新增绘画任务'
    }
    chatCache.value.data[index].content = chatTemplate.value;
    chatCache.value.data[index].updateTime = Date.now();
    setChatCache(chatCache.value)
  }
}
//创建新对话
const handleCreatedDialogue = () => {
  chatCache.value.data.unshift({
    title: "新话题",
    updateTime: Date.now(),
    content: [],
  });
  //重置坐标以及内容
  chatCache.value.index = 0
  chatTemplate.value = []
  setChatCache(chatCache.value)
}


//选择一个对话
const handleSelectedDialogue = (index) => {
  if (chatCache.value.index !== index) {
    chatCache.value.index = index
    chatCache.value.id = chatCache.value.data[index].id;
    //切换内容
    chatTemplate.value = chatCache.value.data[index].content
    //保存至缓存
    setChatCache(chatCache.value)
  }
}

const isCommand = ref(false)

const instructionSet = ref([
  {
    "trigger": "/dall-text2img",
    "icon": "dall_text.svg",
    "detail": "输入文本后，使用 GPT 驱动程序输出图像"
  },
  {
    "trigger": "/sd-text2img",
    "icon": "sd.text.svg",
    "detail": "通过输入提示词以及参数 通过 SD 模型生成图像"
  },
  {
    "trigger": "/sd-img2img",
    "icon": "sd_img.svg",
    "detail": "通过输入文本从SD模型生成图像（可以上传参考图像）"
  }
])

const instructionResult = ref([])
const onChangeInputDirectives = (e) => {
  inputText.value = e
  // 检查输入的文本是否等于 "/"
  instructionResult.value = []
  if (inputText.value.length > 0) {
    if (e === "/") {
      isCommand.value = true
      instructionResult.value = instructionSet.value
    } else {
      // 遍历指令集合，查找匹配的指令
      const matchedInstruction = instructionSet.value.filter(instruction => instruction.trigger.startsWith(e))
      if (matchedInstruction) {
        // 如果找到匹配的指令，将其转移到结果数组中
        instructionResult.value = matchedInstruction
      } else {
        // 如果没有找到匹配的指令，将 isCommand 设置为 false
        isCommand.value = false
      }
    }
    isCommand.value = instructionResult.value.length > 0;
  } else {
    isCommand.value = false
  }
  console.log(instructionResult.value)
}
const rollbackValue = () => {
  inputText.value = ''
  inputRef.value.clearInputValue()
}
//暂停输出
const handleCloseSocket = () => {
  if (webSocket.value) {
    messageQueue.length = 0;
    webSocket.value.close();
    webSocket.value = null;
  }
}
/**
 * 动态拓展BOT 大小
 * @param length
 * @returns {string}
 */
const calculateWidth = (length) => {
  let width = length * 12;
  if (width >= 820) {
    width = 820
  }
  return `${width}px`;
}


const messageQueue = []; // 消息队列
let isDisplaying = false; // 是否正在显示消息

//强制单输出
const displayMessages = () => {
  if (isDisplaying) {
    return; // 如果正在显示消息，则直接返回，等待下一次调用
  }
  isDisplaying = true;
  const message = messageQueue.shift(); // 取出队列中的第一个消息
  if (message) {
    let i = 0;

    // eslint-disable-next-line no-inner-declarations
    function displayNextCharacter() {
      const index = message.index;
      const msg = message.msg;
      const character = msg.charAt(i++);
      if (character) {
        chatTemplate.value[index].answer += character;
        appendMessage()
        //滚动页面
        scrollToTheBottom()
        setTimeout(displayNextCharacter, 20);
      } else {
        isDisplaying = false;
        displayMessages(); // 显示下一条消息
      }
    }

    displayNextCharacter();
  } else {
    isDisplaying = false; // 重置标志以便下次能够正确显示消息
  }
}

//复制代码
const handleCopyCode = (code) => {
  navigator.clipboard.writeText(code);
  ElNotification({
    message: "复制成功",
    type: "success",
  });
}


/**
 * 删除聊天
 * @param index
 */
const handleDeleteChat = (index) => {
  chatTemplate.value.splice(index, 1);
}


//复制内容
const handleCopyContent = (data) => {
  navigator.clipboard.writeText(data);
  ElNotification({
    message: "复制成功",
    type: "success",
  });
}

const scrollIsLock = ref(false)

/**
 * 自动滚动
 */
const scrollToTheBottom = () => {
  if (!scrollIsLock.value) {
    nextTick(() => {
      scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
    });
  }
}


const handleScroll = (event) => {
  // 判断滚动方向
  if (event.deltaY < 0) {
    if (!scrollIsLock.value) {
      if (isInput.value) {
        scrollIsLock.value = true
        setTimeout(() => {
          scrollIsLock.value = false;
        }, 8000); // 延迟8秒后将scrollIsLock.value设置为false
      }
    }
  }
}

const issueRaw = ref(require("../../data/issue.json"))

const issueNew = ref([])


const getRandomLenovo = () => {
  let randomItems = [];
  let itemsCopy = [...issueRaw.value]; // 复制数组对象以避免修改原始数据

  while (randomItems.length < 2 && itemsCopy.length > 0) {
    const randomIndex = Math.floor(Math.random() * itemsCopy.length);
    const randomItem = itemsCopy.splice(randomIndex, 1)[0];
    randomItems.push(randomItem);
  }
  issueNew.value = randomItems
}


/**
 * 触发聊天对话
 * @param input
 */
const handleChat = (input) => {
  //初始化通信
  webSocket.value = new WebSocket(wssAddress.value + store.getters.userSetting.modelIndex + '/web/' + localStorage.getItem("token"));

  const socket = webSocket.value

  const chatData = {
    issue: input,
    answer: '',
    isError: false,
    type: 'CHAT',
    name: store.getters.userSetting.chatBotName,
    date: getCurrentFormattedTime()
  }
  //写入模板数据
  chatTemplate.value.push(chatData)
  //滚动一次 注意这里不能使用方法来滚动
  nextTick(() => {
    scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
  });
  const messages = []
  //记录当前聊天数据坐标
  let index = chatTemplate.value.length - 1;
  //转为后端所支持的格式
  chatTemplate.value.forEach((c, itemIndex) => {
    const {isError, issue, answer} = c
    if (!isError) {
      messages.push({
        role: "user",
        content: issue
      })
      // 判断是否为最后一条
      if (itemIndex !== index) {
        messages.push({
          role: "system",
          content: answer
        })
      }
    }
  })

  //将发送数据转为STRING 并建立socket连接
  socket.onopen = () => {
    socket.send(JSON.stringify(messages));
  };
  //接收数据
  socket.onmessage = (event) => {
    //根据坐标写入数据
    messageQueue.push({
      msg: event.data,
      index: index,
    }); // 将接收到的消息存储到队列中
    displayMessages(); // 显示消息
  };
  //监听关闭
  socket.onclose = () => {
    //判断回复是否有一个字? 如果没有则表示用户中断了请求
    let answer = chatTemplate.value[index].answer;
    if (answer.length > 0) {
      //判断是否回复了错误符号 如果时错误符号则给出响应提示  如果数据单个为ø 则表示 错误信息
      if (answer === "ø") {
        //标记为错误信息
        chatTemplate.value[index].isError = true
        chatTemplate.value[index].answer = "🥲 Sorry!,此次对话失败请稍后再试"
      }
      appendMessage()
    } else {
      //删除
      handleDeleteChat(index)
    }
    //重置页面滚动监听
    scrollIsLock.value = false
    //刷新联想词
    getRandomLenovo()
    isInput.value = false
    //清空文本框
    inputRef.value.resetInputValue();
    webSocket.value = null
  };
}


/**
 * 发送聊天消息
 */
const handleSendMessage = () => {
  let input = inputText.value;
  if (input && isInput.value === false) {
    isInput.value = true
    handleChat(input)
  }
}


const downloadImage = (data) => {
  window.open(data, '_blank');
}

</script>


<style scoped>
.body {
  margin: 0 auto;
  padding: 20px 15px;
  min-height: 100%;
  max-width: 1320px;

}

:deep(.vuepress-markdown-body ) {
  background-color: #222222FF;
}

.body {
  scroll-behavior: smooth;
  width: 100%;
  height: 94vh;
  box-sizing: border-box;
  flex-direction: column;
  flex: 1;
  align-items: center;
  padding: 0 20px;
  display: flex;
  overflow: auto;

}


.content-body {
  padding-top: 120px;
  width: 100%;
  max-width: 1100px;
  padding-bottom: 140px;
  box-sizing: border-box;
  animation: explainAnimation 0.3s;
}

.footer {
  width: 100%;
  box-sizing: border-box;
  z-index: 1;
  pointer-events: none;
  background: linear-gradient(rgba(32, 32, 32, 0.04), #202020 25%);
  flex-shrink: 0;
  padding: 0 20px 30px;
  display: flex;
  position: absolute;
  bottom: 0;
//overflow: hidden; flex-direction: column; align-items: center;
}


.footer-bar {
  min-height: 60px;
  max-width: 800px;
  width: 100%;
  pointer-events: auto;
  background: #252525;
  border-radius: 8px;
  box-shadow: 0 3px 7px 2px rgba(2, 2, 2, 0.11);
  display: flex;
  align-items: center;
  animation: footerBarAnimation 0.3s;
}

@keyframes footerBarAnimation {
  from {
    transform: translateY(150%);
  }

  to {
    transform: translateY(0);
  }
}

.association-div {
  width: 800px;
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.association-div div {
  color: #828282;
  background-color: #272727;

  border-radius: 8px;
  font-size: 12px;
  padding: 4px 8px;
  margin-right: 10px;
}

.user-chat-model {
  display: flex;
  justify-content: right;
  align-items: flex-start;
  margin-bottom: 25px;
}

.user-chat {
  background-color: #7365FF;
  color: #e1e1e1;
  margin-right: 10px;
  padding: 8px 12px;
  font-size: 13px;
  border-radius: 8px;
  max-width: 820px;
}

.bot-chat-model {
  display: flex;
  justify-content: left;
  align-items: flex-start;
  margin-bottom: 25px
}

.bot-chat {
  min-width: 50px;
  background: rgb(39, 39, 39);
  box-shadow: 0 5px 7px rgb(0 0 0 / 6%);
  padding: 8px 12px;
  border-radius: 8px;
  margin-left: 10px;

}


.dot-flex {
  display: flex;
  padding: 5px 9px
}


.bot-chat-width {
  width: 100%
}


@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
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

.suspend {
  animation: explainAnimation 0.3s;
  position: fixed;
  bottom: 200px;
  display: flex;
  align-items: center;
  box-shadow: 0 5px 7px rgba(65, 65, 65, 0.22);
  background-color: #222222FF;
  padding: 5px 20px;
  font-size: 13px;
  color: #717171;
  border-radius: 5px;
  z-index: 200;
}

.suspend div {
  padding-left: 8px;
}


.size-logo img {
  width: 23px;
  height: 23px
}


:deep(.el-input__inner) {
  background-color: #222222FF;
  color: #a8a8a8;
}

:deep(.el-input__wrapper) {
  background-color: #222222FF;
  box-shadow: none;
}


.d-prompt-flex span {
  color: #e2e2e2;
  font-size: 13px;
  padding-right: 5px;
}

.submit-div div {
  margin-top: 10px;
  margin-left: 5px;
  margin-right: 5px
}

:deep(.el-textarea__inner) {
  background-color: #222222FF;
  box-shadow: none;
  color: #ffffff;
}

:deep(.vuepress-markdown-body:not(.custom) ) {
  padding: 0;
  color: #b9b7b7;
  background-color: #272727 !important;
  font-size: 13px;
}

:deep(.v-md-editor) {
  background-color: #272727;
  font-size: 13px;
}

:deep(.scrollbar__wrap ) {
  background-color: #272727;
}

:deep(.el-avatar) {
  background-color: #202020 !important;
}

:deep(.el-overlay) {
  background-color: rgba(0, 0, 0, 0.77);
}
</style>

