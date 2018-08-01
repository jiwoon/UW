<template>
  <div class="user-options form-row">
    <div class="btn pl-1 pr-1" title="详细" @click="isDetailsShowing = true">
      <icon name="list" scale="1.8"></icon>
    </div>
    <div v-if="isDetailsShowing" id="details-window">
      <div class="details-panel">
        <div class="details-panel-container form-row flex-column justify-content-between">
          <div class="form-row">
            <div class="form-group mb-0">
              <h4>详细信息: ({{row.materialNo}})</h4>
            </div>
          </div>
          <div class="form-row" style="height: 30px;">
            <div class="col pl-2 pr-2">
              <h5 class="text-center m-0">料盘唯一码</h5>
            </div>
            <div class="col pl-2 pr-2">
              <h5 class="text-center m-0">数量</h5>
            </div>
          </div>
          <div class="dropdown-divider"></div>
          <div class="form-row" v-for="item in row.details">
            <div class="col pl-2 pr-2">
              <p class="text-center">{{item.materialId}}</p>
            </div>
            <div class="col pl-2 pr-2">
              <p class="text-center">{{item.quantity}}</p>
            </div>
          </div>
          <div class="form-row" v-if="row.details.length === 0">
            <div class="col pl-2 pr-2">
              <p class="text-center">无数据</p>
            </div>
          </div>
          <div class="dropdown-divider"></div>
          <div class="form-row justify-content-around">
            <a class="btn btn-primary col ml-1 text-white" @click="isDetailsShowing = false">确定</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

  export default {
    name: "SubsOperationOption",
    props: ['row'],
    data() {
      return {
        isDetailsShowing: false
      }
    },
    methods: {

    }
  }
</script>

<style scoped>
  #details-window {
    z-index: 30000;
  }

  .details-panel {
    position: fixed;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 100%;
    left: 0;
    top: 0;
    background: rgba(0, 0, 0, 0.1);
    z-index: 101;
  }

  .details-panel-container {
    background: #ffffff;
    min-height: 260px;
    width: 500px;
    z-index: 102;
    border-radius: 10px;
    box-shadow: 3px 3px 20px 1px #bbb;
    padding: 30px 60px 10px 60px;
  }
  .details-panel-container h3 {

  }
  .details-panel-container p {
    margin: 0;
    line-height: 22px;
  }
  .fade-enter-active, .fade-leave-active {
    transition: opacity .5s;
  }

  .fade-enter, .fade-leave-to {
    opacity: 0;
  }
</style>
