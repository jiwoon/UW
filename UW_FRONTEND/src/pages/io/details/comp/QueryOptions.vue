<!--表单查看页面的条件过滤栏-->

<template>
  <div class="options-area">
    <div class="form-row">
      <div class="row no-gutters pl-3 pr-3">
        <div class="form-group col pr-3 pl-1">
          <label for="window-list">选择仓口:</label>
          <select v-model="thisWindow" id="window-list" class="custom-select" @change="setWindow">
            <option  v-for="item in windowsList" :value="item.id">{{item.id}}</option>
          </select>
        </div>
        <div class="form-group row align-items-end" v-if="$route.path === '/io/preview'">
          <div class="btn btn-primary ml-3 mr-4" @click="routerReload">刷新数据</div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  import {mapGetters, mapActions} from 'vuex';
  import {taskWindowsUrl} from "../../../../config/globalUrl";
  import {axiosPost} from "../../../../utils/fetchData";
  import {getLogsQuery} from "../../../../config/logsApiConfig";
  import _ from 'lodash'

  export default {
    name: "Options",
    components: {},
    data() {
      return {
        windowsList: [],
        thisWindow: ''
      }
    },
    created() {
      /*组件创建时加载仓口数据*/
      axiosPost({url: taskWindowsUrl}).then(response => {
        if (response.data.result === 200) {
          this.windowsList = response.data.data;

          /*如果有缓存仓口id的话给select标签赋值*/
          if (this.currentWindowId !== "") {
            this.thisWindow = this.currentWindowId
          } else {
            this.setCurrentWindow(this.windowsList[0].id);
            this.thisWindow = this.windowsList[0].id
          }
        }
      });

    },
    mounted: function () {

      /**/
    },
    computed: {
      ...mapGetters([
        'currentWindowId'
      ]),
    },
    watch: {
      logsRouterApi: function (val) {
        this.initForm(val);
      }
    },
    methods: {
      ...mapActions(['setLoading', 'setCurrentWindow']),


      /*设置仓口*/
      setWindow: function () {
        this.setCurrentWindow(this.thisWindow);
      },
      routerReload: function () {
        let tempPath = this.$route.path;
        this.$router.push('_empty');
        this.$router.push(tempPath)
      }
    }
  }
</script>

<style scoped>
  .options-area {
    background: #fff;
    border: 1px solid #eeeeee;
    border-radius: 8px;
    padding: 10px;
  }
</style>
