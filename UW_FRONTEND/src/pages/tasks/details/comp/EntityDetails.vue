<template>
  <div class="details-panel">
    <div class="form-row justify-content-end">
      <div class="details-panel-container">
        <div class="form-row">
          <div class="form-group mb-0">
            <h3>任务详情：</h3>
          </div>
          <datatable v-bind="$data"/>
        </div>
      </div>
      <div id="cancel-btn" class="ml-2 mt-1" @click="closePanel">
        <icon name="cancel" scale="4" style="color: #fff;"></icon>
      </div>
    </div>
  </div>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex';
  import store from '../../../../store'
  import {axiosPost} from "../../../../utils/fetchData";
  import {taskCheckUrl} from "../../../../config/globalUrl";
  import {errHandler} from "../../../../utils/errorHandler";

  export default {
    name: "EntityDetails",
    data() {
      return {
        fixHeaderAndSetBodyMaxHeight: 550,
        tblStyle: {
          'word-break': 'break-all',
          'table-layout': 'fixed'
        },
        HeaderSettings: false,
        pageSizeOptions: [20, 40, 80],
        data: [],
        columns: [
          {title: '料号', field: 'materialNo', colStyle: {width: '100px'}},
          {title: '套料单条目', field: 'requestQuantity', colStyle: {width: '120px'}},
          {title: '实际变动数目', field: 'actualQuantity', colStyle: {width: '120px'}},
          {title: '操作时间', field: 'finishTime', colStyle: {width: '120px'}}
        ],
        total: 0,
        query: {"limit": 20, "offset": 0},
        isPending: false
      }
    },
    mounted() {
      this.fetchData(store.state.taskDetails)
    },
    methods: {
      ...mapActions(['setTaskActiveState','setTaskData', 'setLoading']),
      init: function () {
        this.data = [];
        this.total = 0;
      },
      fetchData: function (val) {
        if (!this.isPending) {
          this.isPending = true;


          //patch
          if (1) {
            alert('暂不支持任务详情查看');
            this.isPending = false;
            this.setLoading(false);
            this.closePanel();
            return;
          }


          let options = {
            url: taskCheckUrl,
            data: {
              id: val.id,
              type: val.type
            }
          };
          axiosPost(options).then(response => {
            this.isPending = false;
            if (response.data.result === 200) {
              this.data = response.data.data;
              this.total = response.data.data.length;
            } else {
              errHandler(response.data.result)
            }
            this.setLoading(false)
          })
            .catch(err => {
              this.isPending = false;
              console.log(JSON.stringify(err));
              alert('请求超时，请刷新重试')
              this.setLoading(false)
            })
        } else {
          this.setLoading(false)
        }
      },
      closePanel: function () {
        this.setTaskActiveState(false);
        this.setTaskData('')
      }

    }

  }
</script>

<style scoped>
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
    min-height: 220px;
    max-width: 800px;
    z-index: 102;
    border-radius: 10px;
    box-shadow: 3px 3px 20px 1px #bbb;
    padding: 30px 60px 10px 60px;
  }
  #cancel-btn{
    height: 100%;
    cursor: pointer;
  }
</style>
