<template>
  <div>
    <loading v-if="$store.state.isLoading"/>
    <div class="" style="margin-left: 60px;">
      <div class="row main-style" >
        <div class="col router-style mt-3 mb-3">
          <robot-module/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import Loading from '@/components/Loading'
  import RobotModule from './details/RobotModule'
  export default {
    name: "RobotMain",
    components: {
      Loading,
      RobotModule
    }
  }
</script>

<style scoped>
  @media (min-width: 640px) {


    .router-style {
      min-width: 340px;
    }
  }


  .main-style {
    margin: 0;
    height: 100%;
  }
</style>
