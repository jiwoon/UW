<template>
  <div class="add-panel">
    <div class="add-panel-container form-row flex-column justify-content-between">
      <div class="form-row">
        <div class="form-group mb-0">
          <h3>添加物料类型：</h3>
        </div>
      </div>
      <div class="form-row">
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-no" class="col-form-label">料号:</label>
          <input type="text" id="material-no" class="form-control" v-model="thisData.no">
          <span class="form-span col"></span>
        </div>
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-area" class="col-form-label">区域:</label>
          <input type="text" id="material-area" class="form-control" v-model="thisData.area"
                 @input="validate('area', '^[0-9]*[1-9][0-9]*$', '请输入正整数区域号')">
          <span class="form-span col">{{warningMsg.areaMsg}}</span>
        </div>
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-row" class="col-form-label">行号:</label>
          <input type="text" id="material-row" class="form-control" v-model="thisData.row"
                 @input="validate('row', '^[0-9]*[1-9][0-9]*$', '请输入正整数行号')">
          <span class="form-span col">{{warningMsg.rowMsg}}</span>
        </div>
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-col" class="col-form-label">列号:</label>
          <input type="text" id="material-col" class="form-control" v-model="thisData.col"
                 @input="validate('col', '^[0-9]*[1-9][0-9]*$', '请输入正整数列号')">
          <span class="form-span col">{{warningMsg.colMsg}}</span>
        </div>
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-height" class="col-form-label">高度:</label>
          <input type="text" id="material-height" class="form-control" v-model="thisData.height"
                 @input="validate('height', '^[0-9]*[1-9][0-9]*$', '请输入正整数高度')">
          <span class="form-span col">{{warningMsg.heightMsg}}</span>
        </div>
      </div>
      <div class="dropdown-divider"></div>
      <div class="form-row justify-content-around">
        <a class="btn btn-secondary col mr-1 text-white" @click="closeAddPanel">取消</a>
        <a class="btn btn-primary col ml-1 text-white" @click="submitAdding">提交</a>
      </div>
    </div>
  </div>
</template>

<script>
  import eventBus from '@/utils/eventBus';
  import {materialAddUrl} from "../../../../../config/globalUrl";
  import {axiosPost} from "../../../../../utils/fetchData";
  import {errHandler} from "../../../../../utils/errorHandler";

  export default {
    name: "AddMaterial",
    data() {
      return {
        thisData: {
          no: '',
          area: '',
          row: '',
          col: '',
          height: ''
        },
        warningMsg: {

        },
        isPending: false
      }
    },
    methods: {
      closeAddPanel: function () {
        eventBus.$emit('closeAddPanel');
      },
      submitAdding: function () {
        if (!this.isPending) {
          for (let i in this.warningMsg) {
            if (this.warningMsg[i] !== "") {
              alert("请输入正确格式！");
              return
            }
          }
          for (let item in this.thisData) {
            if (this.thisData[item] === '') {
              alert('内容不能为空');
              return;
            }
          }
          this.isPending = true;
          let options = {
            url: materialAddUrl,
            data: this.thisData
          };
          axiosPost(options).then(response => {
            this.isPending = false;
            if (response.data.result === 200) {
              alert('添加成功');
              this.closeAddPanel();
              let tempUrl = this.$route.path;
              this.$router.replace('_empty');
              this.$router.replace(tempUrl);
            } else if (response.data.result === 412) {
              alert('请勿添加重复料号')
            } else {
              this.isPending = false;
              errHandler(response.data.result)
            }
          })
        }
      },
      validate: function (type, regx, msg) {
        let reg = new RegExp(regx);
        if (!reg.test(this.thisData[type])) {
          this.warningMsg[type + 'Msg'] = '*' + msg
        } else {
          this.warningMsg[type + 'Msg'] = ""
        }
      }
    }
  }
</script>

<style scoped>
  .add-panel {
    position: fixed;
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 100%;
    left: 0;
    top: 0;
    background: rgba(0, 0, 0, 0.1);
    z-index: 1001;
  }

  .add-panel-container {
    background: #ffffff;
    min-height: 220px;
    max-width: 600px;
    z-index: 1002;
    border-radius: 10px;
    box-shadow: 3px 3px 20px 1px #bbb;
    padding: 30px 60px 10px 60px;
  }
  .form-span {
    display: block;
    height: 20px;
    line-height: 20px;
    font-size: 10px;
    color: darkred;
  }
</style>
