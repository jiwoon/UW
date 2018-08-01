<template>
  <div class="main-details  mt-1 mb-3">
    <div class="batch-control">
      <div class="form-row pr-3 pl-3 justify-content-start">
        <div class="col-auto">
          <div class="btn btn-primary btn-sm" @click="selectAll">{{checkedData.length !== robotData.length ? '全选' :
            '取消全选'}}
          </div>
          <!--setEnabled(array, [与欲设置启停数值相反])-->
          <div class="btn btn-secondary btn-sm" @click="setEnable(checkedData.toString(), 2)">禁用所选</div>
          <div class="btn btn-primary btn-sm" @click="setEnable(checkedData.toString(), 1)">启用所选</div>
        </div>
        <div id="vertical-divider" class="ml-3 mr-3"></div>

        <div class="col-auto">
          <div class="btn btn-primary btn-sm" @click="setPause">{{robotData[0].pause === true ? '运行所有叉车' : '暂停所有叉车'}}
          </div>
        </div>
      </div>

    </div>
    <div class="dropdown-divider"></div>
    <div class="row" v-if="robotData.length > 0">

      <!--当warn/error时改变边框及内部字体，vue模板数组语法-->
      <div class="card col-12 col-sm-5 col-lg-3 col-xl-2
        card-board justify-content-between no-select"
           :class="[item.warn === 255 ? '' : 'border-warning text-warning shadow-warning',
           item.error === 255 ? '' : 'border-danger text-danger shadow-danger']"
           v-for="item in robotData"
           @click="toggleSelected(item.id)" >


          <div class="position-absolute mt-3 ml-2">
            <icon :name="checkedData.indexOf(item.id) === -1  ? 'check-empty' : 'check' " scale="3.4"></icon>
            <label :for="item.id + '-checkbox'" @click.stop></label>
            <input type="checkbox" class="checkbox d-none" :value="item.id" :id="item.id + '-checkbox'"
                   v-model="checkedData" >
          </div>
          <div class="message-tips card-body mt-5 row">
            <div class="danger-tips row align-items-center pl-3 pr-3" v-if="item.error !== 255">
              <icon name="danger" scale="2.6"></icon>
              <div class="tips-msg ml-3">
                <p class="m-0">{{item.errorString}}</p>
              </div>
            </div>
            <div class="warning-tips row align-items-center pl-3 pr-3" v-if="item.warn !== 255">
              <icon name="warning" scale="2.6"></icon>
              <div class="tips-msg ml-3">
                <p class="m-0">{{item.warnString}}</p>
              </div>
            </div>
          </div>
          <img class="card-img-top" src="/static/img/robot.jpg">
          <div class="card-body row pb-0 pt-1">
            <p class="card-text col">ID: {{item.id}}</p>
          </div>
          <!--<div class="card-body row pb-0 pt-1">-->
          <!--<p class="card-text col">警告状态: {{item.warnString}}</p>-->
          <!--<p class="card-text col">错误状态: {{item.errorString}}</p>-->
          <!--</div>-->
          <!--<div class="card-body row pb-3 pt-0">-->
          <!--<p class="card-text col">运行状态: {{item.pauseString}}</p>-->
          <!--<p class="card-text col">启停状态: {{item.enabledString}}</p>-->
          <!--</div>-->
          <div class="card-body row pb-0 pt-1">
            <p class="card-text col">X: {{item.x}}</p>
            <p class="card-text col">Y: {{item.y}}</p>
          </div>
          <div class="card-body row align-items-center" style="padding: 0 2rem;">
            <span class="d-block" style="width: 20%;">电量:</span>
            <div class="progress" style="width: 80%;">
              <div class="progress-bar progress-bar-striped progress-bar-animated"
                   :class="item.battery < 30 ? 'bg-danger' : ''" role="progressbar"
                   :style="{width : item.battery + '%'}">
                {{item.battery}} %
              </div>
            </div>
          </div>
          <div class="dropdown-divider"></div>
          <div class="card-body form-row justify-content-around">
            <a class="btn col mr-1 text-white" :class="item.enabled === 2 ? 'btn-secondary' : 'btn-primary'"
               @click.stop="setEnable(item.id, item.enabled)">{{item.enabled === 2 ? '点击禁用' : '点击启用'}}</a>
          </div>


      </div>

    </div>
    <div class="row" v-else>
      无结果
    </div>
  </div>
</template>

<script>
  import {mapActions} from 'vuex'
  import {axiosPost} from "../../../../utils/fetchData";
  import {robotSelectUrl, robotSwitchUrl, robotPauseUrl} from "../../../../config/globalUrl";
  import {errHandler} from "../../../../utils/errorHandler";

  export default {
    name: "RobotDetails",
    data() {
      return {
        robotData: [
          {
            id:'123',
            pause: false
          }
        ],
        isPending: false,
        checkedData: []
      }
    },
    watch: {
      /*监听路由是否来自于过滤搜索栏的请求，其请求会带filter的query字段，进行叉车数据查询*/
      $route: function (route) {
        this.setLoading(true);
        if (route.query.filter) {
          let options = {
            url: robotSelectUrl,
            data: {
              filter: route.query.filter
            }
          };
          this.fetchData(options)
        } else {
          let options = {
            url: robotSelectUrl,
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
      };
      this.fetchData(options);

      /*全局定时器id数组，在static/js/config.js内声明, 供src/router/index.js全局路由访问时清除定时器*/
      window.g.ROBOT_INTERVAL.push(setInterval(() => {
        this.fetchData(options)
      }, 3000))
    },
    methods: {
      ...mapActions(['setLoading']),

      /*获取叉车数据*/
      fetchData: function (options) {
        if (!this.isPending) {
          this.isPending = true;
          axiosPost(options).then(response => {
            this.setLoading(false);
            this.isPending = false;
            if (response.data.result === 200) {
              this.robotData = response.data.data;
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

      /*设置启停， 1停止 2启用  根据当前缓存叉车状态取反*/
      setEnable: function (id, enabled) {
        let thisEnabled = enabled === 2 ? 1 : 2;

        if (!this.isPending && id !== "") {
          this.isPending = true;
          let options = {
            url: robotSwitchUrl,
            data: {
              id: id,
              enabled: thisEnabled
            }
          };
          axiosPost(options).then(response => {
            if (response.data.result === 200) {
              this.isPending = false;
              alert((thisEnabled === 1 ? "停用" : "启用") + "成功");
              // let path = this.$route.path;
              // this.$router.replace('_empty');
              // this.$router.push(path);
            }

          }).catch(err => {
            this.isPending = false;
            console.log(JSON.stringify(err));
            alert('请求超时，请刷新重试')
          })
        }
      },

      /*全选、取消全选  vue checkbox数组 v-model绑定数组，根据数组内容选中*/
      selectAll: function () {
        if (this.checkedData.length === 0) {
          this.checkedData = this.robotData.map((item) => {
            return item.id;
          })
        } else {
          this.checkedData = []
        }
      },
      setPause: function () {
        /*根据叉车现有状态进行启停操作，暂停状态所有叉车相同*/
        let thisPause = this.robotData[0].pause === true ? 1 : 0;
        if (!this.isPending) {
          this.isPending = true;
          let options = {
            url: robotPauseUrl,
            data: {
              pause: thisPause
            }
          };
          axiosPost(options).then(response => {
            if (response.data.result === 200) {
              this.isPending = false;
              alert((thisPause === 0 ? "暂停" : "启用") + "成功");
              //let path = this.$route.path;
              // this.$router.replace('_empty');
              // this.$router.push(path);
            }

          }).catch(err => {
            this.isPending = false;
            console.log(JSON.stringify(err));
            alert('请求超时，请刷新重试')
          })
        }
      },
      /*toggleSelected(id) 切换checkbox状态*/
      toggleSelected: function (id) {
        let index = this.checkedData.indexOf(id);
        if (index > -1) {
         this.checkedData.splice(index,1)
        } else {
          this.checkedData.push(id)
        }
      }
    }
  }
</script>

<style scoped>
  .fade-enter-active, .fade-leave-active {
    transition: opacity .5s;
  }

  .fade-enter, .fade-leave-to {
    opacity: 0;
  }

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

  .card-board:hover, .card-board:active{
    box-shadow: 0 0 4px #007bff !important;
  }

  #vertical-divider {
    width: 2px;
    border-left: 1px solid #e9ecef;
  }

  .shadow-warning {
    box-shadow: 0 0 10px #ffebaf;
  }

  .shadow-danger {
    box-shadow: 0 0 10px #dc3545;
  }

  .message-tips {
    position: absolute;
    width: 100%;
  }

  .warning-tips, .danger-tips {
    position: relative;
    height: 50px;
    width: 100%;
    margin: 0.1em 0.25em;
    border: 1px solid #e9ecef;
    border-radius: 8px;
    background-color: #fff;
    opacity: 0.7;
  }

  .warning-tips {
    border: 1px solid #ffebaf;
    background-color: #ffebaf;
  }

  .danger-tips {
    border: 1px solid #dc3545;
    background-color: #dc3545;
  }

  .tips-msg {
    line-height: 24px;
    color: #ffffff;
    font-size: 20px;
  }

  .no-select {
    user-select: none;
  }
</style>
