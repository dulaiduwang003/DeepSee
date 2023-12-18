<script setup>


</script>

<template>
  <div>

  </div>
</template>

<style scoped>
div {
  position: relative;
  width: 100px;
  height: 100px;
}

svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100px;
  height: 100px;
}

svg:hover {
  animation: move 1s infinite;
}

@keyframes move {
  0% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(50px, 50px);
  }
  100% {
    transform: translate(0, 0);
  }
}
</style>
