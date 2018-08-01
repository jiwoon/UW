<template>
  <div class="tips-wrap">
    <transition name="fade">
      <div class="tips" v-if="message === true">
        <icon name="correct" scale="20" style="color: #30ff4f;"></icon>
        <span class="tips-text" style="color: #30ff4f">操作成功</span>
      </div>
      <div class="tips" v-else-if="message === false">
        <icon name="wrong" scale="20"></icon>
        <span class="tips-text">操作失败</span>
      </div>
    </transition>
  </div>
</template>

<script>
  export default {
    name: "GlobalTips",
    props: ['message']
  }
</script>

<style scoped>
  .tips-wrap {
    z-index: 20000;
    position: fixed;
    display: flex;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    justify-content: center;
    align-items: center;
    background-color: #eee;
    opacity: 0.5;
  }

  .tips {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    border-radius: 10px;
    opacity: 0.8;
    width: 20rem;
    height: 20rem;
  }

  .tips-text {
    display: block;
    margin-top: 0;
    font-size: 2.5rem;
  }
  .fade-enter-active, .fade-leave-active {
    transition: opacity 1s;
  }

  .fade-enter, .fade-leave-to {
    opacity: 0;
  }
</style>
