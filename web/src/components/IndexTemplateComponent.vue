<script setup>

import {onMounted, ref} from "vue";

const issueRaw = ref(require("../data/issue.json"))

const issueNew = ref([])

onMounted(() => {
  let randomItems = [];
  let itemsCopy = [...issueRaw.value]; // 复制数组对象以避免修改原始数据

  while (randomItems.length < 3 && itemsCopy.length > 0) {
    const randomIndex = Math.floor(Math.random() * itemsCopy.length);
    const randomItem = itemsCopy.splice(randomIndex, 1)[0];
    randomItems.push(randomItem);
  }
  issueNew.value = randomItems
})


</script>

<template>
  <div class="container">
    <div class="model-flex">
      <div>
        <div>
          <img :src="require('../assets/logo.svg')" class="logo" alt=""/>
        </div>
        <div class="title">
          欢迎使用SUPER AI
        </div>
        <div class="prompt">基于 GPT 驱动</div>
        <div class="grid">
          <div v-for="(item,index) in issueNew" :key="index">
            <div class="grid-padding">
              {{ item.icon }} {{ item.title }}
            </div>
            <div class="container-data">
              {{ item.context }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<style scoped>
.logo {
  width: 60px;
  height: 60px
}

.container-data {
  background-color: #252525;
  padding: 10px 15px;
  border-radius: 8px;
  width: 260px;
  font-size: 13px;
  height: 63px;
  text-align: left;
  color: #cccccc;

}

.container {
  padding-top: 20vh;
  width: 100%;
  box-sizing: border-box;
  animation: explainAnimation 0.3s;
}

.model-flex {
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center
}

.title {
  color: #ffffff;
  font-size: 28px;
  font-weight: 550;
  padding-top: 20px
}

.prompt {
  font-size: 15px;
  color: #dadada;
  font-weight: 550
}

.grid {
  padding-top: 100px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 850px;
}

.grid-padding {
  padding-bottom: 20px;
  color: #ffffff;
  font-weight: 550;
}

@keyframes explainAnimation {
  from {
    transform: scale(0);
  }

  to {
    transform: scale(1);
  }
}

</style>
