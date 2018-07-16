<template>
  <div class="edit-panel">
    <div class="edit-panel-container form-row flex-column justify-content-between">
      <div class="form-row">
        <div class="form-group mb-0">
          <h3>更新任务状态：</h3>
        </div>
      </div>
      <div class="form-row" v-if="originState === '0' || originState === '1' || originState === '2'">
        <label for="examine-select" class="col-form-label">状态更改:</label>
        <select id="examine-select" class="custom-select"
                v-model="thisState">
          <option :value="originState" disabled>请选择</option>
          <option value="1" v-if="originState === '0'">通过审核</option>
          <option value="2" v-if="originState === '1'">开始任务</option>
          <option value="3">作废任务</option>
        </select>
      </div>
      <!--<div class="form-row" v-else-if="originState === '1'">-->
        <!--<label for="status-select" class="col-form-label">状态更改:</label>-->
        <!--<select id="status-select" class="custom-select"-->
                <!--v-model="thisState" >-->
          <!--<option :value="originState" disabled>请选择</option>-->
          <!--<option value="2">开始任务</option>-->
          <!--<option value="3">作废任务</option>-->
        <!--</select>-->
      <!--</div>-->
      <div class="form-row" v-else>
        <label for="status-else-select" class="col-form-label">状态更改:</label>
        <select id="status-else-select" class="custom-select"
                v-model="thisState" disabled>
          <option :value="originState" disabled>无法操作</option>
        </select>
      </div>
      <div class="form-row" v-if="windowShow === '2' && originState === '1'">
        <label for="window-select" class="col-form-label">仓口选择:</label>
        <select id="window-select" class="custom-select" v-model="windowVal" >
          <option value="" disabled>请选择</option>
          <option v-for="item in window" :value="item.id">{{item.id}}</option>
        </select>
      </div>
      <div class="dropdown-divider"></div>
      <div class="form-row justify-content-around">
        <button class="btn btn-secondary col mr-1 text-white" @click="closeEditPanel">取消</button>
        <button class="btn btn-primary col ml-1 text-white" @click="submitUpdate" :disabled="originState > 2">提交</button>
      </div>
    </div>
  </div>
</template>

<script>
  import eventBus from '@/utils/eventBus';
  import {taskUrl, taskWindowsUrl} from "../../../../../config/globalUrl";
  import {axiosPost} from "../../../../../utils/fetchData";
  import {errHandler} from "../../../../../utils/errorHandler";

  export default {
    name: "EditStatus",
    props: ['editData'],
    data() {
      return {
        originState: '',
        thisState: '',
        thisData: {},
        isPending: false,
        windowShow: '',
        window: [],
        windowVal: ''
      }
    },
    mounted() {
      this.thisData.id = this.editData.id;
      this.originState = this.editData.state.toString();
      this.thisState = this.editData.state.toString();
      // if (this.originState === 1) {
      //   this.windowShow = 1;
      // }
      axiosPost({url: taskWindowsUrl}).then(res => {
        this.window = res.data.data
      })
    },
    watch: {
      thisState: function (val) {
        console.log(val + typeof val);
        this.windowShow = val
      }
    },
    methods: {
      closeEditPanel: function () {
        eventBus.$emit('closeTaskStatusPanel');
      },
      submitUpdate: function () {
        if (!this.isPending) {
          this.isPending = true;
          if (this.thisState > 0 && (this.thisState !== this.originState)) {
            let statusUrl;
            switch (this.thisState) {
              case '1':
                statusUrl = '/pass';
                break;
              case '2':
                statusUrl = '/start';
                break;
              case '3':
                statusUrl = '/cancel';
                break;
            }
            let options = {
              url: taskUrl + statusUrl,
              data: {
                id: this.thisData.id
              }
            };
            if (this.thisState === '2') {
              if (this.windowVal === "") {
                alert("请选择仓口");
                this.isPending = false;
                return;
              }
              options.data.window = this.windowVal
            }
            axiosPost(options).then(res => {
              this.isPending = false;
              if (res.data.result === 200) {
                alert('设置成功');
                this.windowShow = '';
                this.thisData = {};
                let tempUrl = this.$route.path;
                this.$router.replace('/_empty');
                this.$router.replace(tempUrl)
              } else {
                errHandler(res.data.result)
              }
            }).catch(err => {
              alert(err)
            })
          }
        }
      }
    }
  }
</script>

<style scoped>
  .edit-panel {
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

  .edit-panel-container {
    background: #ffffff;
    min-height: 220px;
    max-width: 600px;
    z-index: 102;
    border-radius: 10px;
    box-shadow: 3px 3px 20px 1px #bbb;
    padding: 30px 60px 10px 60px;
  }
</style>
