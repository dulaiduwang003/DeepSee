<script setup>

</script>

<template>
  <div class="wechat-login">
    <div>
<!--      <img :src="require('../../../assets/test.jpg')" alt="二维码"-->
      />
      <div class="wechat-login-prompt">打开微信扫一扫快速注册/登录</div>
    </div>
  </div>
</template>

<style scoped>


.wechat-login {
  display: flex;
  justify-content: center;
  align-items: center
}

.wechat-login img {
  width: 180px;
  height: 180px
}

.wechat-login-prompt {
  font-size: 12px;
  padding-top: 20px;
  color: #8b8b8b
}
</style>
