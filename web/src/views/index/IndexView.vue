<template>
  <div class="body">
    <content-components/>
    <chat-record-components/>
  </div>
</template>

<script setup>

import ContentComponents from "@/views/index/components/ContentComponents.vue";
import ChatRecordComponents from "@/views/index/components/ChatRecordComponents.vue";
</script>

<style scoped>
.body {
  margin: 0 auto;
  padding: 20px 15px;
  min-height: 100%;
  max-width: 1320px;

}

</style>
