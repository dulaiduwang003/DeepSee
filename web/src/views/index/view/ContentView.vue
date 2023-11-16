<script setup>


import {InfoFilled, Plus, UserFilled, VideoPause} from "@element-plus/icons-vue";
import IndexTemplateComponent from "@/components/IndexTemplateComponent.vue";
import {nextTick, onMounted, ref} from "vue";
import {ElNotification} from "element-plus";
import ChatRecordComponents from "@/views/index/view/components/ChatRecordComponents.vue";
import InputFormField from "@/views/index/view/components/InputComponents.vue";
import {getChatCache, getDoMain, setChatCache} from "@/utils/Utils";
import store from "@/store";
import {getAiModelList} from "@/api/chat";
import PopoverComponent from "@/views/index/view/components/popoverComponent.vue";
import {getDallTask, getSdParam, pushDallTask, pushSdTask} from "@/api/drawing";

const scrollRef = ref(null);

//是否锁定消息 禁止用户发送
const isInput = ref(false)

//聊天数据
const chatTemplate = ref([])

const inputText = ref('')

const inputRef = ref(null)

const wssAddress = ref(process.env.VUE_APP_WSS + '/chat-api/chat/')

const webSocket = ref(null)

const listener = ref(null)
//聊天数据缓存
const chatCache = ref(null)

onMounted(() => {
  if (store.getters.userInfo) {
    //读取用户缓存
    const data = getChatCache();
    //刷新联想词
    getRandomLenovo()
    if (!data) {
      //如果没有任何数据则需要加载一条新对话
      chatCache.value = {
        //当前聊天坐标
        index: 0,
        data: [
          {
            title: "新主题",
            updateTime: Date.now(),
            content: []
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
  }
})

const aiModelList = ref([])

const sdParameter = ref({
  modelList: [],
  samplerList: [],
  stepsList: []
})
const initData = async () => {
  openListener()
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

const closeListener = () => {
  clearInterval(listener.value);
}

const openListener = () => {
  listener.value = setInterval(async () => {
    const {data} = await getDallTask();
    if (data) {
      // image, response,
      const {status, image, response, prompt} = data
      if (status === 1) {
        chatTemplate.value.push({
          issue: "",
          answer: response,
          isError: true,
          type: "DRAWING_GET",
          isFinish: false,
          sizeList: [],
          extra: {
            image: getDoMain() + image
          }
        })
        closeListener()
      } else if (status === 2) {
        chatTemplate.value.push({
          issue: "",
          answer: "抱歉!(" + prompt.trim() + ")因为某些原因图片绘制失败了!请您稍后重试!",
          isError: true,
          type: "DRAWING_MISTAKE",
          isFinish: false,
          sizeList: []
        })
        setTimeout(() => {
          scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
        }, 800)
        closeListener()
      }
    } else {
      closeListener()
    }
  }, 3000);
}


const imageOnLoad = () => {
  nextTick(() => {
    scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
  });
}

const appendMessage = () => {
  const index = chatCache.value.index;
  const valueElement = chatTemplate.value[chatTemplate.value.length - 1];
  if (valueElement.answer) {
    chatCache.value.data[index].title = valueElement.issue.trim().slice(0, 25);
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

//删除对话
const handleDeleteDialogue = (index) => {
  if (chatCache.value.data.length === 1) {
    ElNotification({
      message: "这已经是最后一条数据了",
      type: "info",
    });
    return
  }
  let i = parseInt(chatCache.value.index);
  if (index !== i) {
    if (index < i) {
      chatCache.value.index = i - 1;
    }
    chatCache.value.data.splice(index, 1);
  } else {
    chatCache.value.data.splice(index, 1);
    chatCache.value.index = 0
  }

  setChatCache(chatCache.value);

}

//选择一个对话
const handleSelectedDialogue = (index) => {
  if (chatCache.value.index !== index) {
    chatCache.value.index = index
    //切换内容
    chatTemplate.value = chatCache.value.data[index].content
    //保存至缓存
    setChatCache(chatCache.value)
  }
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

const issueRaw = ref(require("../../../data/issue.json"))
const dallBuildRaw = ref(require("../../../data/dall_build.json"))
const drawingFinishRaw = ref(require("../../../data/drawing_finish.json"))
const issueNew = ref([])


const onChangeDrawingSize = (parentIndex, childIndex) => {
  chatTemplate.value[parentIndex].sizeList.forEach(s => s.isSelected = false)
  chatTemplate.value[parentIndex].sizeList[childIndex].isSelected = true
}

/**
 * 发布绘图任务
 */
const pushDrawingTask = async (index) => {

  //获取模型坐标
  let drawingIndex = store.getters.userSetting.drawingIndex;
  //获取模型
  let element = store.getters.userSetting.drawingList[drawingIndex];
  if (element.name === 'DALL') {
    //走DALL模型
    //提示词
    let issue = chatTemplate.value[index].issue;
    try {
      await pushDallTask({prompt: issue, size: "1024x1024"});
      chatTemplate.value[index].isFinish = true
      chatTemplate.value[index].answer = getRandomDrawingFinish()
      ElNotification({title: "绘图", message: '任务已提交', type: "success",});
      openListener()
    } catch (e) {
      const issueCache = chatTemplate.value[index].issue;
      chatTemplate.value[index] = {
        issue: issueCache,
        answer: "抱歉!我正在完成您上一个任务,请等我完成上一个绘图任务后给您答复。",
        isError: true,
        type: "DRAWING_MISTAKE",
      }
      ElNotification({title: "绘图", message: '有任务正在执行中', type: "info",});
    }
  } else {
    let val = chatTemplate.value[index];
    chatTemplate.value[index].sd.prompt = chatTemplate.value[index].issue
    val.sizeList.forEach(s => {
      if (s.isSelected) {
        chatTemplate.value[index].sd.width = s.width
        chatTemplate.value[index].sd.height = s.height
      }
    })
    let {images, prompt, width, height, modelName, steps, sampler_index, negative_prompt} = chatTemplate.value[index].sd;
    console.log( chatTemplate.value[index].sd)
    const formData = new FormData();
    formData.append("images",images)
    formData.append("prompt",prompt)
    formData.append("width",width)
    formData.append("height",height)
    formData.append("modelName",modelName)
    formData.append("steps",steps)
    formData.append("sampler_index",sampler_index)
    formData.append("negative_prompt",negative_prompt)
    try {
      await pushSdTask(formData);
      chatTemplate.value[index].isFinish = true
      chatTemplate.value[index].answer = getRandomDrawingFinish()
      ElNotification({title: "绘图", message: '任务已提交', type: "success",});
      openListener()
    } catch (e) {
      const issueCache = chatTemplate.value[index].issue;
      chatTemplate.value[index] = {
        issue: issueCache,
        answer: "抱歉!我正在完成您上一个任务,请等我完成上一个绘图任务后给您答复。",
        isError: true,
        type: "DRAWING_MISTAKE",
      }
      ElNotification({title: "绘图", message: '有任务正在执行中', type: "info",});
    }

  }
}

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


const getRandomDrawingBuild = () => {
  let value = dallBuildRaw.value;
// 随机选择一个字符串
  return value[Math.floor(Math.random() * value.length)];
}

const getRandomDrawingFinish = () => {
  let value = drawingFinishRaw.value;
// 随机选择一个字符串
  return value[Math.floor(Math.random() * value.length)];
}

const onChangeFileTemp = (e, index) => {
  console.log(e)
  if (e.raw.type === 'image/jpg' || e.raw.type === 'image/png' || e.raw.type === 'image/jpeg') {
    if (e.raw.size / 1024 / 1024 > 2) {
      ElNotification({
        title: "MISTAKE",
        message: 'images must not exceed 2 mb in size',
        type: "error",
      });
      return false
    }
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (event) => {
        chatTemplate.value[index].sd.sdUploadTemp = event.target.result
        chatTemplate.value[index].sd.images = e.raw
        resolve(e);
      };
      reader.onerror = (error) => {
        reject(error);
      };
      reader.readAsDataURL(e.raw);
    });
  } else {
    ElNotification({
      title: "MISTAKE",
      message: 'Please upload the correct image',
      type: "error",
    });
    return false
  }
}

/**
 * 触发绘图消息
 */
const handleDrawing = (input) => {

  let userSetting = store.getters.userSetting;

  //获取本次触发的模型
  let obj = userSetting.drawingList[userSetting.drawingIndex];
  //写入模板数据
  chatTemplate.value.push({
    issue: input,
    answer: "",
    isError: false,
    type: "CHAT",
    isFinish: false,
    sizeList: [],
    sd: {}
  })
  //滚动一次
  nextTick(() => {
    scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
  });
  setTimeout(() => {
    let index = chatTemplate.value.length - 1;
    chatTemplate.value[index] = {
      issue: input,
      answer: getRandomDrawingBuild(),
      isError: true,
      type: obj.put,
      isFinish: false,
      sizeList: [
        {
          proportion: '1:1',
          text: '头像',
          image: 'size-1-1.f9b344b9.svg',
          isSelected: true,
          width: 512,
          height: 512
        },
        {
          proportion: '1:2',
          text: '手机壁纸',
          image: 'size-1-2.62c2da58.svg',
          isSelected: false,
          width: 1080,
          height: 2160
        },
        {
          proportion: '3:4',
          text: '文案图',
          image: 'size-3-4.ba364264.svg',
          isSelected: false,
          width: 384,
          height: 512
        },
        {
          proportion: '4:3',
          text: '小红书',
          image: 'size-4-3.a0ec2a1c.svg',
          isSelected: false,
          width: 512,
          height: 384
        },
        {
          proportion: '9:16',
          text: '海报',
          image: 'size-9-16.498b0472.svg',
          isSelected: false,
          width: 768,
          height: 1365
        },
        {
          proportion: '16:9',
          text: '电脑壁纸',
          image: 'size-4-3.a0ec2a1c.svg',
          isSelected: false,
          width: 1980,
          height: 1080
        }
      ],
      sd: {
        images: '',
        prompt: '',
        width: '',
        height: '',
        modelName: '',
        steps: undefined,
        sampler_index: '',
        negative_prompt: '',
        sdUploadTemp: undefined,
      }
    }
    nextTick(() => {
      scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
    });
    isInput.value = false
  }, 1000)
}

/**
 * 触发聊天对话
 * @param input
 */
const handleChat = (input) => {
  //初始化通信
  webSocket.value = new WebSocket(wssAddress.value + store.getters.userSetting.modelIndex + '/web/' + localStorage.getItem("token"));

  const socket = webSocket.value
  //写入模板数据
  chatTemplate.value.push({
    issue: input,
    answer: '',
    isError: false,
    type: 'CHAT',
    isFinish: false,
    sizeList: []
  })
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
    } else {
      //删除
      handleDeleteChat(index)
    }
    appendMessage()
    //重置页面滚动监听
    scrollIsLock.value = false
    //刷新联想词
    getRandomLenovo()
    isInput.value = false
    //清空文本框
    inputRef.value.resetInputValue();
    console.log('WebSocket连接已关闭');
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
    //是否触发绘图关键字
    if (input.startsWith("绘图")) {
      //触发
      handleDrawing(input.substring(2))
    } else {
      //普通消息处理
      handleChat(input)
    }

  }
}


</script>

<template>
  <!--  聊天控件-->
  <div class="body" ref="scrollRef" @wheel="handleScroll">
    <!--    CONTENT-->
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
            <el-avatar :icon="UserFilled" :size="30"
                       :src="store.getters.userInfo.avatarUrl  ? getDoMain()+store.getters.userInfo.avatarUrl    :  require('../../../assets/default_avatar.png')"/>
          </div>
        </div>
        <div class="bot-chat-model">
          <div>
            <el-avatar :size="30" :src="require('../../../assets/logo.svg')"/>
          </div>
          <div class="bot-chat-width" v-if="item.answer || item.type!=='CHAT'">
            <!--            聊天模板-->
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
            <!--     DALL写入模板-->
            <div class="chat-operation-container slide-animation" v-if="item.type==='DALL_PUT'">
              <div class="bot-chat chat-panel">
                {{ item.answer }}
                <div class="drawing-panel" v-if="!item.isFinish">
                  <div class="size-container">
                    <div :class="sizeItem.isSelected?'size-model-selected':'size-model'"
                         v-for="(sizeItem,sizeIndex) in item.sizeList" :key="sizeIndex"
                         @click="onChangeDrawingSize(index,sizeIndex)">
                      <div>
                        <div class="size-logo">
                          <img :src="require('../../../assets/drawing/'+sizeItem.image)" alt=""/>
                        </div>
                        <div class="size-proportion">
                          {{ sizeItem.proportion }}
                        </div>
                        <div class="size-text">{{ sizeItem.text }}</div>
                      </div>
                    </div>

                  </div>
                </div>
                <div class="submit-div" v-if="!item.isFinish">
                  <div>
                    <el-button class="btn-submit" @click="pushDrawingTask(index)">就这样</el-button>
                  </div>
                </div>
              </div>
            </div>
            <!--     SD写入模板-->
            <div class="chat-operation-container slide-animation" v-if="item.type==='SD_PUT'">
              <div class="bot-chat chat-panel">
                please let me know what pictures i need to draw
                <div>
                  <div v-if="!item.isFinish" class="spacing_15px">
                    <div>
                      <div class="d-prompt-flex">
                        <span>反向提示词(可选)</span>
                        <el-popover placement="bottom" effect="dark" title="Reference diagram" :width="300"
                                    trigger="hover" content="从类似于参考图像的图像内容生成">
                          <template #reference>
                            <el-icon>
                              <info-filled/>
                            </el-icon>
                          </template>
                        </el-popover>
                      </div>
                      <div>
                        <el-input
                            placeholder="过滤本次绘图出现的事物(英文更加准确)"
                            type="textarea"
                            rows="5"
                            v-model="item.sd.negative_prompt"
                        ></el-input>
                      </div>
                      <div class="d-prompt-flex">
                        <span>参考图 (可选)</span>
                        <el-popover placement="bottom" effect="dark" title="Reference diagram" :width="300"
                                    trigger="hover" content="从类似于参考图像的图像内容生成">
                          <template #reference>
                            <el-icon>
                              <info-filled/>
                            </el-icon>
                          </template>
                        </el-popover>
                      </div>
                      <div>
                        <el-upload class="upload-file" :auto-upload="false"
                                   :on-change="($event)=>onChangeFileTemp($event,index)"
                                   :show-file-list="false">
                          <img v-if="item.sd.sdUploadTemp" :src="item.sd.sdUploadTemp"/>
                          <el-icon v-else>
                            <Plus/>
                          </el-icon>
                        </el-upload>
                      </div>
                      <div class="d-prompt-flex">
                        <span>模型选择</span>
                        <el-popover placement="bottom" effect="dark" title="Reference diagram" :width="300"
                                    trigger="hover" content="Generated from image content similar to reference image">
                          <template #reference>
                            <el-icon>
                              <info-filled/>
                            </el-icon>
                          </template>
                        </el-popover>
                      </div>
                      <div>
                        <el-select v-model="item.sd.modelName" size="large">
                          <el-option
                              v-for="(item,index) in sdParameter.modelList"
                              :key="index"
                              :label="item.modelText"
                              :value="item.modelName"
                          />
                        </el-select>
                      </div>
                      <div class="d-prompt-flex">
                        <span>采样方法</span>
                        <el-popover placement="bottom" effect="dark" title="Reference diagram" :width="300"
                                    trigger="hover" content="Generated from image content similar to reference image">
                          <template #reference>
                            <el-icon>
                              <info-filled/>
                            </el-icon>
                          </template>
                        </el-popover>
                      </div>
                      <div>
                        <el-select v-model="item.sd.sampler_index" size="large">
                          <el-option
                              v-for="(item,index) in sdParameter.samplerList"
                              :key="index"
                              :label="item"
                              :value="item"
                          />
                        </el-select>
                      </div>
                      <div class="d-prompt-flex">
                        <span>迭代次数</span>
                        <el-popover placement="bottom" effect="dark" title="Reference diagram" :width="300"
                                    trigger="hover" content="Generated from image content similar to reference image">
                          <template #reference>
                            <el-icon>
                              <info-filled/>
                            </el-icon>
                          </template>
                        </el-popover>
                      </div>
                      <div>
                        <el-select v-model="item.sd.steps" size="large">
                          <el-option
                              v-for="(item,index) in sdParameter.stepsList"
                              :key="index"
                              :label="item"
                              :value="item"
                          />
                        </el-select>
                      </div>
                      <div class="d-prompt-flex">
                        <span>生成图片尺寸</span>
                        <el-popover placement="bottom" effect="dark" title="Reference diagram" :width="300"
                                    trigger="hover" content="Generated from image content similar to reference image">
                          <template #reference>
                            <el-icon>
                              <info-filled/>
                            </el-icon>
                          </template>
                        </el-popover>
                      </div>
                      <div class="size-container">
                        <div :class="sizeItem.isSelected?'size-model-selected':'size-model'"
                             v-for="(sizeItem,sizeIndex) in item.sizeList" :key="sizeIndex"
                             @click="onChangeDrawingSize(index,sizeIndex)">
                          <div>
                            <div class="size-logo">
                              <img :src="require('../../../assets/drawing/'+sizeItem.image)" alt=""/>
                            </div>
                            <div class="size-proportion">
                              {{ sizeItem.proportion }}
                            </div>
                            <div class="size-text">{{ sizeItem.text }}</div>
                          </div>
                        </div>

                      </div>
                    </div>
                  </div>
                </div>
                <div class="submit-div" v-if="!item.isFinish" style="margin-top: 20px">
                  <div>
                    <el-button class="btn-submit" @click="pushDrawingTask(index)">就这样</el-button>
                  </div>
                </div>
              </div>
            </div>
            <!--     结果获取模板-->
            <div class="chat-operation-container slide-animation" v-if="item.type==='DRAWING_GET'">
              <div class="bot-chat chat-panel">
                {{ item.answer }}
                <div>
                  <img class="img-result" @load="imageOnLoad" :src="item.extra.image" alt=""/>
                </div>
              </div>
            </div>
            <!--     绘图错误模板-->
            <div class="chat-operation-container slide-animation" v-if="item.type==='DRAWING_MISTAKE'">
              <div class="bot-chat chat-panel">
                {{ item.answer }}
              </div>
            </div>
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
    <div class="footer" v-if="store.getters.userInfo">
      <!--      联想组件-->
      <div class="association-div">
        <div v-for="(item,index) in issueNew" :key="index" class=" slide-animation">
          {{ item.context }}
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
      @delete-dialogue-event="handleDeleteDialogue"
      @selected-dialogue-event="handleSelectedDialogue"/>
</template>


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
  padding-top: 60px;
  width: 100%;
  max-width: 900px;
  padding-bottom: 140px;
  box-sizing: border-box;
  animation: explainAnimation 0.3s;
}

.footer {
  width: 100%;
  box-sizing: border-box;
  z-index: 1;
  pointer-events: none;
  background: linear-gradient(white, white 25%);
  flex-shrink: 0;
  padding: 0 20px 30px;
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
  background-color: #F3F3FC;

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

.operation-icon {
  display: flex;
  justify-content: space-between;
  align-items: center;

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
  background-color: white;
  padding: 5px 20px;
  font-size: 13px;
  color: #717171;
  border-radius: 5px;
  z-index: 200;
}

.suspend div {
  padding-left: 8px;
}

.chat-operation-container {
  display: flex;
  width: 600px;
  margin-bottom: 40px;

}

.chat-panel {
  background-color: #2f2f2f;
  color: #e6e6e6;
  padding-bottom: 15px;
  width: 470px

}

.drawing-panel {
  width: 200px;
  height: 200px;
  margin-top: 15px
}

.size-container {
  width: 445px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.size-model-selected {
  border-radius: 5px;
  margin: 5px auto;
  width: 130px;
  height: 120px;
  background-color: #3a3a3a;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #8166E7;
}

.size-model {
  border-radius: 5px;
  margin: 5px auto;
  width: 130px;
  height: 120px;
  background-color: #3a3a3a;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #3a3a3a;
}

.size-logo {
  padding-bottom: 5px
}

.size-logo img {
  width: 23px;
  height: 23px
}

.size-proportion {
  padding-bottom: 2px
}

.size-text {
  font-size: 9px;
  font-weight: 500;
  color: #636363
}

.btn-submit {

  color: white;
  width: 100%;
  background-color: #7365FF;
  border: none
}

.btn-submit:hover,
.btn-submit:focus,
.btn-submit:active {
  background-color: #5044c0;
  outline: 0;
  color: white;
}

.submit-div {
  width: 100%;
  margin-top: 75px;
}

.img-result {
  width: 440px;
  margin-top: 10px;
  border-radius: 5px
}

:deep(.el-input__inner) {
  background-color: #3f3e3e;
  color: #a8a8a8;
}

:deep(.el-input__wrapper) {
  background-color: #3f3e3e;
  box-shadow: none;
}

.upload-file {

  border-radius: 8px;
  background-color: #3f3e3e;
  width: 100px;
  height: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.upload-file img {
  width: 100px;
  height: 100px;
}

.spacing_15px {
  margin-top: 15px
}

.d-prompt-flex {
  display: flex;
  align-items: center;
  padding: 10px 0;
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
  background-color: #3f3e3e;
  box-shadow: none;
  color: #a8a8a8;
}
</style>
