<script setup>
import {defineEmits, defineProps} from "vue";

const props = defineProps({
  text: {
    type: String,
    default: null
  },
  // -1 错误 0列队 1预览 2成功
  status: {
    type: Number,
    default: 0
  },
  //outcome
  outcome: {
    type: String,
  }
});
const emits = defineEmits(['preview-image-event']);

const previewImage = ()=>{
  if (props.status===2){
    emits('preview-image-event',props.outcome)
  }
}

</script>

<template>
  <div class="container">
    <div v-if="props.status===0">
      请稍等, 这条任务正在列队中 很快将会开始....
    </div>
    <div v-if="props.status===1">
      正在处理这条任务 请耐心等待
    </div>
    <div v-if="props.status===2">
      根据您提供的提示词以及参数。绘制了以下图片
    </div>
    <div v-if="props.status===-1">
      Sorry! 貌似绘图服务出了点问题 请您稍后重试或联系管理员解决...🥲
    </div>
    <div class="result-margin" v-if="props.status!==-1&&props.outcome">
      <img class="img-result" @click="previewImage"
           :src="props.outcome"
           alt=""/>
    </div>
  </div>


</template>

<style scoped>

.container {
  width: 465px;
  min-width: 50px;
  background: #272727;
  box-shadow: 0 5px 7px rgb(0 0 0 / 6%);
  padding: 8px 12px;
  border-radius: 8px;
  margin-left: 10px;
  font-size: 13px;
  color: #bcbcbc;
}

.result-margin {
  margin-top: 5px
}

.img-result {
  width: 439px;

  border-radius: 5px
}
</style>
