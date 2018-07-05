<!--表单-->
<template>
  <div>
    <!--<Header/>-->
    <loading v-if="$store.state.isLoading"/>
    <div class="" style="margin-left: 60px;">
      <div class="row main-style" :style="sideBarStyle">
        <div class="col router-style mt-3 mb-3">
          <table-module/>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
  import Header from '../../components/PageHeader'
  import Loading from '@/components/Loading'
  import TableModule from './details/TableModule'
  export default {
    name: "Main",
    components: {
      Header,
      Loading,
      TableModule
    },
    data() {
      return {
        sideBarIsShow: true,
        sideBarStyle: {
          height: '0px'
        }
      }
    },
    mounted: function () {


    },
    methods: {}
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

  .slide-enter-active, .slide-leave-active {
    transform: translate3d(0, 0, 0);
    margin-left: 0;
    transition: all .5s;
  }

  .slide-enter, .slide-leave-to {
    transform: translate3d(0, 0, 0);
    margin-left: -250px;
  }
</style>
