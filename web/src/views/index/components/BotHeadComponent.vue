<script setup>
import {defineProps} from "vue";

const props = defineProps({
  name: {
    type: String
  },
  type: {
    type: String
  },
  date: {
    type: String
  },
  progress: {
    type: Number
  },
});
</script>

<template>
  <div class="container">
    <div class="bot-name">
      {{ props.name }}
    </div>
    <div :class="props.type === 'CHAT' ? 'bot-label' : 'drawing-label'">
      {{ props.type === 'CHAT' ? 'Chat GPT' : 'Stable Diffusion' }}
    </div>
    <div class="bot-date">
      {{ props.date }}
    </div>
    <div class="progress" v-if="props.type !== 'CHAT'&&props.progress!==100">
      -- {{ props.progress }}%
    </div>
  </div>
</template>

<style scoped>

.container {
  display: flex;
  align-items: center;
  color: #dfdfdf;

  margin-left: 10px;
  font-size: 14px;
  margin-bottom: 6px
}

.bot-name {
  font-family: SmileySans, serif;
}

.bot-label {
  background-color: #6b5df8;
  padding: 0 4px;
  border-radius: 3px;
  margin-left: 10px;
  font-size: 11px;
}

.drawing-label {
  background-color: #da54a3;
  padding: 0 4px;
  border-radius: 3px;
  margin-left: 10px;
  font-size: 11px;
}

.bot-date {
  color: #afafaf;
  padding-left: 10px;
  font-size: 12px;
}

.progress {
  padding-left: 20px
}
</style>
