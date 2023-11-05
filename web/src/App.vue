<template>
  <NavigationComponent/>
  <main>
    <router-view/>
  </main>

</template>


<script setup>

import {useStore} from "vuex";
import NavigationComponent from "@/components/NavigationComponent.vue";

let store = useStore();
store.commit("initState");
</script>

<style>
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  letter-spacing: 1px;
  color: var(--el-text-color-primary);
  line-height: 1.6;
}

::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}


::-webkit-scrollbar-thumb {
  border-radius: 10px;
  background: white;
}

::-webkit-scrollbar-track {
  /* box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.1); */
  border-radius: 0;
  background: white;
  display: block;
}

@font-face {
  font-family: SmileySans;
  src: url(@/assets/fonts/SmileySans.otf);
  font-display: swap;
}

html, body {
  padding: 0;
  margin: 0;
}

main {
  flex: 1;
  background-color: #ffffff;
}

* {
  box-sizing: border-box;
}

.dot_0,
.dot_1,
.dot_2,
.dot_3 {
  background: rgb(166, 129, 236);
  width: 15px;
  height: 15px;
  border-color: #464646;
  border-radius: 50%;
}

.dot_0 {
  animation: jumpT 1.3s -0.64s linear infinite;
}

.dot_1 {
  animation: jumpT 1.3s -0.32s linear infinite;
}

.dot_2 {
  animation: jumpT 1.3s -0.16s linear infinite;
}

.dot_3 {
  animation: jumpT 1.3s linear infinite;
}

@keyframes jumpT {
  0%,
  80%,
  100% {
    transform: scale(0);
    background-color: #f9f9f9;
  }

  40% {
    transform: scale(1);
    background-color: rgb(186, 156, 241);
  }
}
</style>
