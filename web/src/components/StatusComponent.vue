<script setup>
import {defineProps} from 'vue';

const props = defineProps({
  size: {
    type: String,
    default: "40px",
  },
  icon: {
    type: String,
    default: "Document",
  },
  text: {
    type: String,
    default: '空数据',
  },
  isLogin: {
    type: Boolean,
    default: false,
  },
  color: {
    type: String,
    default: "#CCCCCC",
  },
  height: {
    type: Number,
    default: 300,
  }
});
</script>

<template>
  <div class="container" :style="{ height: props.height }">
    <div class="content">
      <el-icon :size="props.size" :color="props.color">
        <component :is="props.icon"/>
      </el-icon>
      <div class="text" :style="{ color: props.color }">
        {{ props.text }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.content {
  text-align: center;
  font-size: 13px;
}

.text {
  padding-top: 15px;
  text-align: center;
}
</style>
