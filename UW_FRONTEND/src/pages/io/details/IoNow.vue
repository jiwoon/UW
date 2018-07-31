<!--
  仓口当前任务的操作
-->

<template>
  <div>
    <options/>
    <div class="io-now mt-1 mb-3">
      <div class="row ml-3 mr-3 mt-3">
        <div class="card bg-light col-4">
          <div class="card-body row">
            <span class="col-form-label">任务: </span>
            <p class="card-text form-control">{{taskNowItems.fileName}}</p>
            <span class="col-form-label">料号: </span>
            <p class="card-text form-control">{{taskNowItems.materialNo}}</p>
            <span class="col-form-label">类型: </span>
            <p class="card-text form-control">{{taskNowItems.type}}</p>
          </div>
          <div class="card-body row">
            <div class="col pl-0">
              <span class="col-form-label">计划: </span>
              <p class="card-text form-control">{{taskNowItems.planQuantity}}</p>
            </div>
            <div class="col pr-0">
              <span class="col-form-label">实际: </span>
              <p class="card-text form-control">{{taskNowItems.actualQuantity}}</p>
            </div>
          </div>
        </div>
        <div class="card bg-light col-3 ml-4">
          <div class="border-light row justify-content-center ">
            <img src="/static/img/finishedQRCode.png" alt="finished" class="img-fluid mt-3 mb-3">
          </div>
          <span class="card-text text-center">* 扫描此二维码或点击按钮以完成操作</span>
          <button class="btn btn-primary">操作完毕</button>
        </div>
      </div>
      <div class="row">

      </div>
    </div>
  </div>
</template>

<script>
  import Options from './comp/QueryOptions'
  import {axiosPost} from "../../../utils/fetchData";
  import {mapGetters} from 'vuex'
  import {robotBackUrl, taskWindowParkingItems} from "../../../config/globalUrl";

  export default {
    name: "IoNow",
    components: {
      Options
    },
    data() {

      return {
        /* --item sample--
          "data": {
          "id": 1,
          "fileName": "套料单1",
          "type": "出库",
          "materialNo": "KBG132123",
          "planQuantity": 10000,
          "actualQuantity": 10100,
          "details": [
            {
              "materialId": "29301282",
              "quantity": 2000
            }
          ]
        }
           --sample ends-- */
        taskNowItems: {
          id: '',
          fileName: 'xxx',
          type: 'xx',
          materialNo: 'xxx',
          planQuantity: '123',
          actualQuantity: '123',
          details: [
            {
              materialId: '',
              quantity: ''
            }
          ]
        }
      }
    },
    mounted() {

    },
    computed: {
      ...mapGetters([
        'currentWindowId'
      ]),
    },
    methods: {
      fetchData: function (id) {
        let options = {
          url: taskWindowParkingItems,
          data: {
            id: id
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            this.taskNowItems = response.data.data;
          }
        })

      }
    }
  }
</script>

<style scoped>
  .io-now {
    background: #fff;
    border: 1px solid #eeeeee;
    border-radius: 8px;
    padding: 10px;
    min-height: 500px;
  }
</style>
