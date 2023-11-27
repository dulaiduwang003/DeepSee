<script setup>
import {defineEmits, defineProps, onMounted, ref, watch} from "vue";
import {ArrowDownBold, ArrowUpBold, InfoFilled, Plus} from "@element-plus/icons-vue";
import {getSdParam} from "@/api/drawing";
import {ElMessage, ElNotification} from "element-plus";

const props = defineProps({
  display: {
    type: Boolean,
    default: false,
  },
  directives: {
    type: Array
  }

})


const isSdSenior = ref(false)


const directives = ref([])

const isSdDisplay = ref(false)
const sizeList = ref([
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
    text: '小红书',
    image: 'size-3-4.ba364264.svg',
    isSelected: false,
    width: 384,
    height: 512
  },
  {
    proportion: '4:3',
    text: '文案图',
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
])
const emits = defineEmits(['clear', 'push-sd-task-event']);

const sdParameter = ref({
  modelList: [],
  samplerList: [],
  steps: {
    min: 0,
    max: 0
  }
})

onMounted(() => {
  initData()
})
const initData = async () => {
  let {data} = await getSdParam();
  if (data) {
    const {modelList, samplerList, steps} = data;
    sdParameter.value.modelList = modelList
    sdParameter.value.samplerList = samplerList
    sdParameter.value.steps.min = steps.min
    sdParameter.value.steps.max = steps.max
    //初始化默认值
    //模型
    sdForm.value.modelName = sdParameter.value.modelList[0].modelName
    //采样
    sdForm.value.sampler_index = sdParameter.value.samplerList[0]

  }
}

const onChangeFileTemp = (e) => {
  if (e.raw.type === 'image/jpg' || e.raw.type === 'image/png' || e.raw.type === 'image/jpeg') {
    if (e.raw.size / 1024 / 1024 > 2) {
      ElNotification({
        title: "MISTAKE",
        message: '图像大小不得超过2MB',
        type: "warning",
      });
      return false
    }
    new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (event) => {
        sdForm.value.images = event.target.result
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


watch(
    () => props.directives,
    (newValue) => {
      directives.value = newValue;
    },
    {
      immediate: true,
    }
);



const onChangeDrawingSize = (index) => {
  sizeList.value.forEach(s => s.isSelected = false)
  sizeList.value[index].isSelected = true

  let {width, height} = sizeList.value[index];
  sdForm.value.width = width
  sdForm.value.height = height
}

const onHandleClickRow = (data) => {
  if (data === "/sd-text2img") {
    isSdDisplay.value = true
    emits('clear')
  }
}

const sdForm = ref({
  images: '',
  prompt: '',
  width: 512,
  height: 512,
  modelName: '',
  steps: 10,
  sampler_index: '',
  negative_prompt: ''
})
const onHandleSubmitSdTask = async () => {
  let value = sdForm.value;
  if (!value.prompt) {
    ElMessage.info("提示词不能为空");
    return
  }
  emits('push-sd-task-event', value)
  isSdDisplay.value = false
}


</script>

<template>
  <div class="panel-container" v-if="props.display">
    <div class="data-row" v-for="(item,index) in props.directives " :key="index"
         @click="onHandleClickRow(item.trigger)">
      <div>
        <img :src="require('../../../assets/drawing/directives/'+item.icon)" alt=""/>
      </div>
      <div class="data-detail">
        <div class="directives">{{ item.trigger }}</div>
        <div class="detail">{{ item.detail }}</div>
      </div>
    </div>
  </div>
  <el-dialog
      v-model="isSdDisplay"
      title="Stable Diffusion"
      width="650px"
      style="background-color: #282828;border-radius: 8px;">
    <div>
      <div class="sd-flex">
        <div>
          <div class="d-prompt-flex">
            <span>提示词</span>
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
                placeholder="绘图提示词"
                type="textarea"
                rows="1"
                v-model="sdForm.prompt"
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
                       :on-change="($event)=>onChangeFileTemp($event)"
                       :show-file-list="false">
              <img v-if="sdForm.images" :src="sdForm.images" alt=""/>
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
            <el-select v-model="sdForm.modelName" size="large">
              <el-option
                  v-for="(item,index) in sdParameter.modelList"
                  :key="index"
                  :label="item.modelText"
                  :value="item.modelName"
              />
            </el-select>
          </div>
        </div>
        <div>
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
                 v-for="(sizeItem,index) in sizeList" :key="index"
                 @click="onChangeDrawingSize(index)">
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
      <div class="advanced-options" @click="isSdSenior=!isSdSenior">
        <el-icon v-if="!isSdSenior">
          <ArrowDownBold/>
        </el-icon>
        <el-icon v-else>
          <ArrowUpBold/>
        </el-icon>
        <div style="padding-left: 5px">高级选项</div>
      </div>
      <div v-if="isSdSenior" class="scale-animation">
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
              rows="1"
              v-model="sdForm.negative_prompt"
          ></el-input>
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
          <el-select v-model="sdForm.sampler_index" size="large">
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
          <el-slider v-model="sdForm.steps" :min="sdParameter.steps.min" :max="sdParameter.steps.max"/>
        </div>
      </div>
    </div>
    <div class="submit-div">
      <div>
        <el-button class="btn-submit" @click="onHandleSubmitSdTask">就这样</el-button>
      </div>
    </div>
  </el-dialog>

</template>

<style scoped>
.scale-animation {
  animation: explainAnimation 0.3s;
}

.panel-container {
  position: absolute;
  background-color: #252525;
  z-index: 99999999;
  width: 800px;
  bottom: 120px;
  border-radius: 6px;
  padding: 8px;
  animation: explainAnimation 0.3s;
  color: white;
}


.data-row {
  display: flex;
  justify-content: left;
  align-items: center;
  margin: 8px 0
}

.data-row:hover {
  transform: scale(1.1);
  transition: transform 0.2s ease-in-out;
  background-color: #1a1a1a;
  border-radius: 8px;
  padding-top: 8px;
  cursor: pointer;
}

.data-row img {
  width: 35px;
  height: 35px;
  border-radius: 100%
}

.data-row > div {
  padding-left: 10px;

}

.directives {
  font-size: 13px;
  font-weight: 550;
  color: #d4d4d4;
}

.detail {
  font-size: 10px;
  color: #d3d3d3
}

.data-detail {
  padding-bottom: 5px
}

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

.size-container {
  width: 330px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-top: 10px;
}

.size-model-selected {
  border-radius: 5px;
  margin: 5px auto;
  width: 100px;
  height: 90px;
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
  width: 100px;
  height: 90px;
  background-color: #3e3e3e;
  text-align: center;
  display: flex;
  justify-content: center;
  align-items: center;
  border: 1px solid #3e3e3e;
}

.size-logo {
  padding-bottom: 5px
}

.size-logo img {
  width: 23px;
  height: 23px
}


.size-proportion {
  padding-bottom: 2px;
  font-size: 14px;
  font-weight: 550;
  color: #a2a2a2
}

.size-text {
  font-size: 11px;

  color: #c3c3c3
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

:deep(.el-textarea__inner) {
  background-color: #3f3e3e;
  box-shadow: none;
  color: #a8a8a8;
}

.submit-div div {
  margin-top: 10px;
  margin-left: 5px;
  margin-right: 5px
}

.submit-div {
  width: 100%;
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

.upload-file {

  border-radius: 8px;
  background-color: #222222FF;
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

:deep(.el-slider__button) {
  background-color: #6b5df8;
  border: none;
}

:deep(.el-slider__bar) {
  background-color: #6b5df8;
}

:deep(.el-slider__runway ) {
  background-color: #222222FF;
}

.advanced-options {
  color: #cacaca;
  text-align: right;
  padding-top: 10px;
  display: flex;
  align-items: center;
  justify-content: right
}

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

.sd-flex {
  display: flex;
  justify-content: space-between;
  align-items: flex-start
}
</style>
