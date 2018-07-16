<template>
  <div class="add-panel">
    <div class="add-panel-container form-row flex-column justify-content-between">
      <div class="form-row">
        <div class="form-group mb-0">
          <h3>创建任务：</h3>
        </div>
      </div>
      <div class="form-row">
        <label for="type-select" class="col-form-label">类型选择:</label>
        <select id="type-select" class="custom-select" v-model="taskType">
          <option value="" disabled>请选择</option>
          <option value="0">入库</option>
          <option value="1">出库</option>
          <!--<option value="2">盘点</option>-->
          <!--<option value="3">位置优化</option>-->
        </select>
      </div>
      <div class="form-row" v-if="taskType < 2">
        <div class="form-row pl-1 pr-1">
          <label for="upload-comp" class="col-form-label">选择文件:</label>
          <input id="upload-comp" type="text" class="form-control" v-model="fileName" onfocus="this.blur()"
                 @click="setUpload">
          <input type="file" id="upload-file-comp"
                 accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
                 class="d-none" ref="taskUpload" @change="uploadFile">
        </div>
      </div>
      <div class="dropdown-divider"></div>
      <div class="form-row justify-content-around">
        <a class="btn btn-secondary col mr-1 text-white" @click="closeUploadPanel">取消</a>
        <a class="btn btn-primary col ml-1 text-white" @click="submitUploading">提交</a>
      </div>
    </div>
  </div>
</template>

<script>
  import eventBus from '@/utils/eventBus';
  import {taskCreateUrl} from "../../../../../config/globalUrl";
  import {axiosPost} from "../../../../../utils/fetchData";
  import {errHandler} from "../../../../../utils/errorHandler";
  import store from '../../../../../store'

  export default {
    name: "UploadTask",
    data() {
      return {
        fileName: '',
        taskType: '',
        isPending: false,
        thisFile: ''
      }
    },
    methods: {
      closeUploadPanel: function () {
        eventBus.$emit('closeUploadPanel');
      },
      setUpload: function () {
        this.$refs.taskUpload.click()
      },
      uploadFile: function (e) {
        console.log(e);
        this.thisFile = e.target.files[0];
        this.fileName = e.target.files[0].name + " 加载成功";
      },
      submitUploading: function () {
        if (!this.isPending) {
          if (this.taskType !== "") {
            this.isPending = true;
            let formData = new FormData();
            if (this.taskType < 2) {
              if (this.thisFile !== "") {
                formData.append('file', this.thisFile);
              } else {
                alert("内容不可为空");
                this.isPending = false;
                return;
              }
            }
            formData.append('type', this.taskType);
            formData.append('#TOKEN#', store.state.token);
            let config = {
              header: {
                'Content-Type': 'multipart/form-data'
              }
            };
            this.$axios.post(taskCreateUrl, formData, config).then(res => {
              if (res.data.result === 200) {
                this.isPending = false;
                alert('添加成功');
                this.closeUploadPanel();
                let tempUrl = this.$route.path;
                this.$router.replace('_empty');
                this.$router.replace(tempUrl);
              } else if (res.data.result === 501) {
                this.isPending = false;
                alert(res.data.data);
              }
              else {
                this.isPending = false;
                errHandler(res.data.result)
              }
            })
          } else {
            alert("选项不能为空");
          }
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

</style>
