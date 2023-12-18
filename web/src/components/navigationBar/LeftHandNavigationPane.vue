<template>
  <!--菜单栏-->
  <menu-component :menu-list="menuList" />
  <!--视图渲染-->
  <render-component/>
</template>


<script setup>
import {ref} from "vue";
import MenuComponent from "@/components/navigationBar/components/MenuComponent.vue";
import RenderComponent from "@/components/navigationBar/components/RenderComponent.vue";

const menuList = ref([
  {
    iconDefault: 'chat.svg',
    iconActive: 'active/chat-active.svg',
    to: "/"
  },
  {
    iconDefault: 'drawing.svg',
    iconActive: 'active/drawing-active.svg',
    to: "/drawing"
  }, {
    iconDefault: 'app.svg',
    iconActive: 'active/app-active.svg',
    to: "/app"
  }, {
    iconDefault: 'miniPrograms.svg',
    iconActive: 'active/miniPrograms-active.svg',
    to: '/miniPrograms'
  }, {
    iconDefault: 'setting.svg',
    iconActive: 'active/setting-active.svg',
    to: '/setting'
  }
])

</script>

<style scoped>

</style>
