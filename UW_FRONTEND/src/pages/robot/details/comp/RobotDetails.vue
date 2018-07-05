<template>
  <div class="main-details  mt-1 mb-3">
    <div class="row">
      <div class="card col-12 col-sm-5 col-lg-3 col-xl-2  card-board justify-content-between" v-for="item in robotData">
        <img class="card-img-top" src="/static/img/robot.jpg">
        <div class="card-body row pb-0 pt-1">
          <p class="card-text col">ID: {{item.id}}</p>
          <p class="card-text col">状态: {{robotStatus(item.status)}}</p>
        </div>
        <div class="card-body row pb-0 pt-1">
          <p class="card-text col">X: {{item.x}}</p>
          <p class="card-text col">Y: {{item.y}}</p>
        </div>
        <div class="card-body row align-items-center" style="padding: 0 2rem;">
          <span class="d-block" style="width: 20%;">电量:</span>
          <div class="progress" style="width: 80%;">
            <div class="progress-bar progress-bar-striped progress-bar-animated"
                 :class="item.battery < 30 ? 'bg-danger' : ''" role="progressbar" :style="{width : item.battery + '%'}">
              {{item.battery}} %
            </div>
          </div>
        </div>
        <div class="dropdown-divider"></div>
        <div class="card-body form-row justify-content-around">
          <a class="btn btn-secondary col mr-1 text-white" @click="setEnable(item.id, 0)">停用</a>
          <a class="btn btn-primary col ml-1 text-white" @click="setEnable(item.id, 1)">启用</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import {mapActions} from 'vuex'
  import {axiosPost} from "../../../../utils/fetchData";
  import {robotSelectUrl, robotSwitchUrl} from "../../../../config/globalUrl";
  import {errHandler} from "../../../../utils/errorHandler";

  export default {
    name: "RobotDetails",
    data() {
      return {
        robotData: [],
        isPending: false
      }
    },
    watch: {
      $route: function (route) {
        this.setLoading(true);
        if (route.query.filter) {
          let options = {
            url: robotSelectUrl,
            data: {
              pageNo: 1,
              pageSize: 99999,
              filter: route.query.filter
            }
          };
          this.fetchData(options)
        } else {
          let options = {
            url: robotSelectUrl,
            data: {
              pageNo: 1,
              pageSize: 99999
            }
          };
          this.fetchData(options)
        }
      }
    },
    mounted() {
    },
    created() {
      let options = {
        url: robotSelectUrl,
        data: {
          pageNo: 1,
          pageSize: 99999
        }
      };
      this.fetchData(options)
    },
    methods: {
      ...mapActions(['setLoading']),
      fetchData: function (options) {
        if (!this.isPending) {
          this.isPending = true;
          axiosPost(options).then(response => {
            this.setLoading(false);
            this.isPending = false;
            if (response.data.result === 200) {
              this.robotData = response.data.data.list;
            } else {
              errHandler(response.data.result)
            }
          }).catch(err => {
            this.isPending = false;
            this.setLoading(false);
            console.log(JSON.stringify(err));
            alert('请求超时，请刷新重试')
          })
        }
      },
      robotStatus: function (status) {
        switch (status) {
          case 0:
            return "闲置";
          case 1:
            return "忙碌";
          case 3:
            return "错误";
          case 4:
            return "充电";
        }
      },
      setEnable: function (id, enabled) {
        if (!this.isPending) {
          this.isPending = true;
          let options = {
            url: robotSwitchUrl,
            data: {
              id: id,
              enabled: enabled
            }
          };
          axiosPost(options).then(response => {
            if (response.data.result === 200) {
              this.isPending = false;
              alert((enabled === 0 ? "停用" : "启用") + "成功")
            }

          }).catch(err => {
            this.isPending = false;
            console.log(JSON.stringify(err));
            alert('请求超时，请刷新重试')
          })
        }
      }
    }
  }
</script>

<style scoped>
  .main-details {
    background: #fff;
    border: 1px solid #eeeeee;
    border-radius: 8px;
    padding: 10px;
    min-height: 500px;
  }

  .card-board {
    margin: 20px;
  }
</style>
