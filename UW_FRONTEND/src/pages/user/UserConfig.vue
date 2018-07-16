<template>
  <div class="user-config form-row">
    <div class="form-area col">
      <div class="options-area">
        <div class="form-row">
          <div v-for="item in queryOptions" class="row no-gutters pl-3 pr-3">
            <component :opt="item" :is="item.type + '-comp'" :callback="thisFetch"></component>
          </div>
          <div class="form-group row align-items-end">
            <button type="button" class="btn btn-primary ml-3 mr-4" @click="addUser">添加用户</button>
          </div>
          <div class="form-group row align-items-end">
            <div class="btn btn-secondary ml-3 mr-4" @click="initForm">清空条件</div>
          </div>
          <div class="form-group row align-items-end">
            <div class="btn btn-primary ml-3 mr-4" @click="thisFetch">查询</div>
          </div>
        </div>
      </div>
      <user-details/>
      <transition name="fade">
        <div class="add-panel" v-if="isAdding">
          <div class="add-panel-container form-row flex-column justify-content-between">
            <div class="form-row">
              <div class="form-row col-6 pl-2 pr-2">
                <label for="user-name" class="col-form-label">用户名:</label>
                <input type="text" id="user-name" class="form-control" v-model="userData.uid">
              </div>
              <div class="form-row col-6 pl-2 pr-2">
                <label for="user-pwd" class="col-form-label">密码:</label>
                <input type="password" id="user-pwd" class="form-control" v-model="userData.password">
              </div>
              <div class="form-row col-6 pl-2 pr-2">
                <label for="user-des" class="col-form-label">用户描述:</label>
                <input type="text" id="user-des" class="form-control" v-model="userData.name">
              </div>
              <div class="form-row col-6 pl-2 pr-2">
                <label for="access-select" class="col-form-label">用户类型:</label>
                <select id="access-select" class="custom-select" v-model="userData.type">
                  <option value="" disabled selected>请选择</option>
                  <option v-for="item in userTypeList" :value="item.id">{{item.name}}</option>
                </select>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <div class="form-row justify-content-around">
              <a class="btn btn-secondary col mr-1 text-white" @click="isAdding = !isAdding">取消</a>
              <a class="btn btn-primary col ml-1 text-white" @click="addSubmit">提交</a>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
  import UserDetails from './comp/UserDetails'
  import {axiosPost} from "../../utils/fetchData";
  import {mapGetters, mapActions} from 'vuex'
  import {userAddUrl, userTypeUrl} from "../../config/globalUrl";
  import {errHandler} from "../../utils/errorHandler";

  export default {
    name: "UserConfig",
    data() {
      return {
        isAdding: false,
        userData: {
          uid: '',
          name: '',
          password: '',
          type: '',
        },
        isPending: false,
        queryOptions: [
          {
            id: 'uid',
            name: '用户名',
            model: '',
            type: 'text'
          },
          {
            id: 'enabled',
            name: '是否启用',
            model: '',
            type: 'select',
            list: [
              {
                value: false,
                string: '禁用状态'
              },
              {
                value: true,
                string: '启用状态'
              }
            ]
          },
          {
            id: 'type',
            name: '用户类型',
            model: '',
            type: 'select',
            list: []
          }
        ],

        copyQueryOptions: [],
        queryString: ""
      }
    },
    components: {
      'text-comp': {
        props: ['opt', 'callback'],
        template: '<div class="form-group col pr-3"">\n' +
        '           <label :for="opt.id">{{opt.name}}：</label>\n' +
        '           <input type="text" class="form-control" :id="opt.id" v-model="opt.model" @keyup.enter="callback">\n' +
        '          </div>'
      },
      'select-comp': {
        props: ['opt'],
        template: '<div class="row">\n' +
        '      <div class="form-group col pr-3">\n' +
        '        <label :for="opt.id">{{opt.name}}：</label>\n' +
        '        <select :id="opt.id" v-model="opt.model" class="custom-select">\n' +
        '          <option value="" disabled>请选择</option>\n' +
        '          <option :value="item.value"  v-for="item in opt.list">{{item.string}}</option>\n' +
        '        </select>\n' +
        '      </div>\n' +
        '    </div>'
      },
      UserDetails
    },
    computed: {
      ...mapGetters(['userTypeList'])
    },
    mounted() {
      axiosPost({
        url: userTypeUrl
      }).then(response => {
        if (response.data.result === 200) {
          this.setUserTypeList(response.data.data);
          for (let i = 0; i < response.data.data.length; i++) {
            this.queryOptions[2].list.push({
              value: response.data.data[i].id,
              string: response.data.data[i].name
            })
          }
        }
      });
      this.initForm();
    },
    methods: {
      ...mapActions(['setUserTypeList','setLoading']),
      initForm: function () {
        this.queryOptions.map(item => {
          item.model = "";
        })
      },
      addUser: function () {
        this.isAdding = true;
      },
      addSubmit: function () {
        if (!this.isPending) {
          this.isPending = true;
          let user = this.userData;
          if (user.uid !== "" || user.password !== "" || user.type !== "" || user.name !== "") {
            let optData = JSON.parse(JSON.stringify(user));
            let options = {
              url: userAddUrl,
              data: optData
            };
            axiosPost(options).then(response => {
              this.isPending = false;
              if (response.data.result === 200) {
                alert("添加成功");
                this.isAdding = false;
                let tempUrl = this.$route.path;
                this.$router.replace('/_empty');
                this.$router.replace(tempUrl);
              } else if (response.data.result === 412) {
                alert("用户已存在");
              } else {
                errHandler(response.data.result)
              }
            })
              .catch(err => {
                this.isPending = false;
                console.log(JSON.stringify(err));
                alert('请求超时，清刷新重试')
              })
          } else {
            alert("内容不能为空");
            this.isPending = false;
            return;
          }
        }
      },
      createQueryString: function () {
        this.queryString = "";
        this.copyQueryOptions = this.queryOptions.filter((item) => {
          if (!(item.model === "")) {
            return true;
          }
        });

        this.copyQueryOptions.map((item, index) => {
          if (item.type === 'text' || item.type === 'select') {
            if (_.trim(item.model) !== "") {
              if (index === 0) {
                this.queryString += (item.id + "=" + _.trim(item.model))
              } else {
                this.queryString += ("&" + item.id + "=" + _.trim(item.model))
              }

            } else {
              this.setLoading(false)
            }
          }

        })
      },
      fetchData: function () {
        let options = {
          path: '/user',
          query: {}
        };
        if (this.queryString !== "") {
          options.query.filter = this.queryString
        }

        this.$router.replace('_empty');
        this.$router.push(options
          , () => {
            this.setLoading(true);
          })

      },
      thisFetch: function () {
        this.createQueryString();
        this.fetchData()
      }
    }
  }
</script>

<style scoped>
  .user-config {
    position: absolute;
    height: 100%;
    width: 100%;
  }

  .form-area {
    position: relative;
    margin-left: 60px;
    width: 100%;
    padding: 40px 60px;
  }

  .options-area {
    background: #fff;
    border: 1px solid #eeeeee;
    border-radius: 8px;
    padding: 10px;
  }

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
    z-index: 101;
  }

  .add-panel-container {
    background: #ffffff;
    min-height: 220px;
    max-width: 600px;
    z-index: 102;
    border-radius: 10px;
    box-shadow: 3px 3px 20px 1px #bbb;
    padding: 30px 60px 10px 60px;
  }

  .fade-enter-active, .fade-leave-active {
    transition: opacity .5s;
  }

  .fade-enter, .fade-leave-to {
    opacity: 0;
  }
</style>
