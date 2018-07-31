<!--表单查看页面的条件过滤栏-->

<template>
  <div class="options-area">
    <div class="form-row">
      <div class="row no-gutters pl-3 pr-3">
        <div class="form-group col pr-3">
          <label for="window-list">选择窗口</label>
          <select v-model="thisWindow" id="window-list" v-for="item in windowsList" @change="setWindow">
            <option :value="item">{{item}}</option>
          </select>
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
          this.windowsList = response.data.data
        }
      });

      /*如果有缓存仓口id的话给select标签赋值*/
      if (this.currentWindowId !== "") {
        this.thisWindow = this.currentWindowId
      } else {
        this.setCurrentWindow(this.windowsList[0]);
        this.thisWindow = this.currentWindowId
      }
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
        let path = this.$route.path;
        this.$router.replace('_empty');
        this.$router.push(path)
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
