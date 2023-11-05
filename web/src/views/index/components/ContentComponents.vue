<template>
  <!--  聊天控件-->
  <div class="body">
    <!--    CONTENT-->
    <div class="content-body" v-if="chatTemplate.length>0">
      <div v-for="(item,index) in chatTemplate" :key="index">
        <div class="user-chat-model" trigger="hover">
          <el-popover  placement="top-end" :width="160" effect="dark" >
            <p>Are you sure to delete this?</p>
            <template #reference>
              <div class="user-chat">
                {{ item.issue }}
              </div>
            </template>
          </el-popover>
          <div>
            <el-avatar :icon="UserFilled" :size="30"
                       src="https://sea-time.oss-accelerate.aliyuncs.com/avatar/a888316a-01bd-4e1e-a7e5-1e5b8845c868.jpg"/>
          </div>
        </div>
        <div class="bot-chat-model">
          <div>
            <el-avatar :size="30" :src="require('../../../assets/logo.svg')"/>
          </div>
          <div class="bot-chat-width" v-if="item.answer" >
            <el-popover  placement="top-end" :width="160" effect="dark"  trigger="hover">
              <p>Are you sure to delete this?</p>
              <template #reference>
                <div class="bot-chat" :style="{ maxWidth: calculateWidth(item.answer.length) }">
                  <v-md-editor
                      :model-value="item.answer"
                      mode="preview"
                  />
                </div>
              </template>
            </el-popover>
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
    <!--    DEFAULT-->
    <div v-else>
      <index-template-component/>
    </div>
    <!--   INPUT-->
    <div class="footer">
      <!--      联想组件-->
      <div class="association-div">
        <div>
          "这首歌有什么特别之处吗?"
        </div>
        <div>
          "你喜欢蔡依林吗?"
        </div>
      </div>
      <div class="footer-bar">
        <InputFormField
            @update:inputText="inputText = $event"
            ref="inputRef"
            :aiLoading="isInput"
            @onSubmit="handleSendMessage"
        />
      </div>
    </div>
  </div>

</template>

<script setup>

import InputFormField from "@/views/index/components/InputComponents.vue";
import {UserFilled} from "@element-plus/icons-vue";
import IndexTemplateComponent from "@/components/IndexTemplateComponent.vue";
import {ref} from "vue";

//是否锁定消息 禁止用户发送
const isInput = ref(false)

//聊天数据
const chatTemplate = ref([])

const inputText = ref('')

const inputRef = ref(null)

const wssAddress = ref(process.env.VUE_APP_WSS + '/chat-api/chat/1/web/' + localStorage.getItem("token"))


/**
 * 动态拓展BOT 大小
 * @param length
 * @returns {string}
 */
const calculateWidth = (length) => {
  let width = 100 + length * 16;
  if (width >= 820) {
    width = 820
  }
  return `${width}px`;
}

const messageQueue = []; // 消息队列
let isDisplaying = false; // 是否正在显示消息

//强制单子输出
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

/**
 * 发送消息
 */
const handleSendMessage = () => {
  let input = inputText.value;
  if (input && isInput.value === false) {
    isInput.value = true
    //初始化通信
    const socket = new WebSocket(wssAddress.value);
    //写入模板数据
    chatTemplate.value.push({
      issue: input,
      answer: '',
      isError: false,
      isOperationUser: false,
      isOperationBot: false
    })
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
      isInput.value = false
      //清空文本框
      inputRef.value.resetInputValue();
      console.log('WebSocket连接已关闭');
    };

  }


}


</script>

<style scoped>

:deep(.vuepress-markdown-body:not(.custom)) {
  padding: 0;
}

:deep(.vuepress-markdown-body ) {
  background-color: #f5f5f5;
}

.body {
  scroll-behavior: smooth;
  width: 100%;
  height: 83vh;
  box-sizing: border-box;
  flex-direction: column;
  flex: 1;
  align-items: center;
  padding: 0 20px 140px;
  display: flex;
  overflow: auto;
}


.content-body {
  padding-top: 30px;
  width: 100%;
  max-width: 900px;
  box-sizing: border-box;
}

.footer {
  width: 100%;

  box-sizing: border-box;
  z-index: 1;
  pointer-events: none;
  background: linear-gradient(white, white 25%);
  flex-shrink: 0;
  padding: 30px 20px;
  display: flex;
  position: absolute;
  bottom: 0;
  overflow: hidden;
  flex-direction: column;
  align-items: center;
}


.footer-bar {
  min-height: 60px;
  max-width: 800px;
  width: 100%;
  pointer-events: auto;
  background: #ffffff;
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
  color: #909090;
  background-color: rgba(150, 142, 232, 0.11);
  border-radius: 8px;
  font-size: 12px;
  padding: 4px 8px;
  margin-right: 10px;
}

.user-chat-model {
  display: flex;
  justify-content: right;
  align-items: flex-start;
  margin-bottom: 25px
}

.user-chat {
  background-color: #7365FF;
  color: white;
  margin-right: 10px;
  padding: 8px 12px;
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
  background: #f5f5f5;
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
</style>
