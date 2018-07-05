<template>
  <div class="edit-panel">
    <div class="edit-panel-container form-row flex-column justify-content-between">
      <div class="form-row">
        <div class="form-group mb-0">
          <h3>更新物料类型：</h3>
        </div>
      </div>
      <div class="form-row">
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-id" class="col-form-label">Uid:</label>
          <input type="text" id="material-id" class="form-control"  v-model="thisData.id" disabled>
        </div>
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-no" class="col-form-label">料号:</label>
          <input type="text" id="material-no" class="form-control" v-model="thisData.no">
        </div>
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-area" class="col-form-label">区域:</label>
          <input type="text" id="material-area" class="form-control" v-model="thisData.area">
        </div>
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-row" class="col-form-label">行号:</label>
          <input type="text" id="material-row" class="form-control" v-model="thisData.row">
        </div>
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-col" class="col-form-label">列号:</label>
          <input type="text" id="material-col" class="form-control" v-model="thisData.col">
        </div>
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-height" class="col-form-label">高度:</label>
          <input type="text" id="material-height" class="form-control"  v-model="thisData.height">
        </div>
        <div class="form-row col-4 pl-2 pr-2">
          <label for="material-enabled" class="col-form-label">是否启用:</label>
          <select id="material-enabled" class="custom-select" v-model="thisData.enabled">
            <option value="" disabled>请选择</option>
            <option value="0">禁用</option>
            <option value="1">启用</option>
          </select>
        </div>
      </div>
      <div class="dropdown-divider"></div>
      <div class="form-row justify-content-around">
        <a class="btn btn-secondary col mr-1 text-white" @click="closeEditPanel">取消</a>
        <a class="btn btn-primary col ml-1 text-white" @click="submitUpdate">提交</a>
      </div>
    </div>
  </div>
</template>

<script>
  import eventBus from '@/utils/eventBus';
  import {materialUpdateUrl} from "../../../../../config/globalUrl";
  import {axiosPost} from "../../../../../utils/fetchData";
  import {errHandler} from "../../../../../utils/errorHandler";

  export default {
    name: "EditMaterial",
    props: ['editData'],
    data() {
      return {
        thisData: {
          id: '',
          area: '',
          row: '',
          no: '',
          col: '',
          height: '',
          enabled: ''
        },
        isPending: false
      }
    },
    mounted() {
      this.thisData.id = this.editData.id;
      this.thisData.area = this.editData.area;
      this.thisData.row = this.editData.row;
      this.thisData.no = this.editData.no;
      this.thisData.col = this.editData.col;
      this.thisData.height = this.editData.height;
      this.thisData.enabled = this.editData.enabled;
    },
    methods: {
      closeEditPanel: function () {
        eventBus.$emit('closeEditPanel');
      },
      submitUpdate: function () {
        if (!this.isPending) {
          this.isPending = true;
          for (let item in this.thisData) {
            if (this.thisData[item] === '') {
              alert('内容不能为空');
              return;
            }
          }
          let options = {
            url: materialUpdateUrl,
            data: this.thisData
          };
          axiosPost(options).then(response => {
            this.isPending = false;
            if (response.data.result === 200) {
              alert('更新成功');
              this.closeEditPanel();
              let tempUrl = this.$route.path;
              this.$router.replace('_empty');
              this.$router.replace(tempUrl);
            } else {
              this.isPending = false;
              errHandler(response.data.result);
              this.closeEditPanel()
            }
          })
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
