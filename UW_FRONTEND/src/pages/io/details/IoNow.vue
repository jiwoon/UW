<!--
  仓口当前任务的操作
-->

<template>
  <div>
    <global-tips :message="tipsComponentMsg" v-if="isTipsShow"/>
    <options/>
    <input type="text" title="scanner" id="material-check" v-model="scanText"
           @blur="setFocus" autofocus="autofocus" autocomplete="off" @keyup.enter="scannerHandler">

    <div class="io-now mt-1 mb-3" v-if="tipsMessage !==''">
      <p class="d-block text-center mt-5">{{tipsMessage}}</p>
    </div>
    <div class="io-now mt-1 mb-3" v-else>
      <div class="row m-3 align-content-start">
        <div class="card bg-light col-12 col-lg-6 col-xl-4 m-2">
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
        <div class="card bg-light col-12 col-lg-5 col-xl-3 m-2">
          <div class="border-light row ml-auto mr-auto mt-4">
            <img src="/static/img/finishedQRCode.png" alt="finished" class="img-style">
          </div>
          <span class="card-text text-center mt-auto">* 扫描此二维码或点击按钮以完成操作</span>
          <button class="btn btn-primary mb-4 mt-auto" @click="setBack">操作完毕</button>
        </div>
      </div>
      <div class="row m-3">
        <div class="card bg-light col-12 col-xl-9 ml-2">
          <div class="row card-body mb-0 pb-1">
            <div class="col">
              <span class="text-center col-form-label">料盘: </span>
            </div>
            <div class="col">
              <span class="card-text text-center">数量: </span>
            </div>
          </div>
          <div class="dropdown-divider"></div>
          <div class="row card-body" v-for="item in taskNowItems.details">
            <div class="col pl-4">
              <p class="card-text">{{item.materialId}}</p>
            </div>
            <div class="col pl-4">
              <p class="card-text">{{item.quantity}}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import Options from './comp/QueryOptions'
  import GlobalTips from './comp/GlobalTips'
  import {axiosPost} from "../../../utils/fetchData";
  import {mapGetters} from 'vuex'
  import {robotBackUrl, taskWindowParkingItems, taskIOUrl} from "../../../config/globalUrl";
  import {currentWindowId} from "../../../store/getters";

  export default {
    name: "IoNow",
    components: {
      Options,
      GlobalTips
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
              materialId: '123',
              quantity: 'asd'
            }
          ]
        },

        scanText: '',
        tipsMessage: '',
        tipsComponentMsg: '',
        isTipsShow: false
      }
    },
    mounted() {
      this.setFocus();
      this.fetchData(this.currentWindowId);

      window.g.PARKING_ITEMS_INTERVAL.push(setInterval(() => {
        this.fetchData(this.currentWindowId)
      }, 1000))
    },
    watch: {},
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
            if (response.data.data) {
              this.taskNowItems = response.data.data;
              this.tipsMessage = ""
            } else {
              this.tipsMessage = "无数据"
            }
          } else if (response.data.result === 412) {
            this.tipsMessage = response.data.data
          }
        })

      },
      /*设置输入框焦点*/
      setFocus: function () {
        if (this.$route.path === '/io/now') {
          document.getElementById('material-check').focus();
        }
      },

      /*扫码集中处理*/
      scannerHandler: function () {
        /*若扫描结果为叉车返回的页面二维码，则调用叉车回库*/
        if (this.scanText === "###finished###") {
          this.setBack()
        } else {
          /*sample: 03.01.0001@1000@1531817296428@A008@范例表@A-1@9@2018-07-17@*/
          /*对比料号是否一致*/
          let tempArray = this.scanText.split("@");
          if (tempArray[0] !== this.taskNowItems.materialNo) {
            this.isTipsShow = true;
            this.tipsComponentMsg = false;
            setTimeout(() => {
              this.isTipsShow = false;
            }, 3000);
            return;
          } else {
            let options = {
              url: taskIOUrl,
              data: {
                packListItemId: this.taskNowItems.id,
                materialId: tempArray[2],
                quantity: tempArray[1]
              }
            };
            axiosPost(options).then(response => {
              if (response.data.result === 200) {
                this.isTipsShow = true;
                this.tipsComponentMsg = true;
                setTimeout(() => {
                  this.isTipsShow = false;
                }, 3000)
              } else {
                this.isTipsShow = true;
                this.tipsComponentMsg = false;
                setTimeout(() => {
                  this.isTipsShow = false;
                }, 3000)
              }
            })
          }

        }
        this.scanText = "";
      },

      setBack: function () {
        let options = {
          url: robotBackUrl,
          data: {
            id: this.taskNowItems.id
          }
        };
        axiosPost(options).then(response => {
          if (response.data.result === 200) {
            this.isTipsShow = true;
            this.tipsComponentMsg = true;
            setTimeout(() => {
              this.isTipsShow = false;
            }, 3000)
          } else {
            this.isTipsShow = true;
            this.tipsComponentMsg = false;
            setTimeout(() => {
              this.isTipsShow = false;
            }, 3000)
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

  .img-style {
    width: 100%;
    height: 100%;
  }

  #material-check {
    opacity: 0;
    height: 0;
    line-height: 0;
    border: none;
    padding: 0;
  }
</style>
