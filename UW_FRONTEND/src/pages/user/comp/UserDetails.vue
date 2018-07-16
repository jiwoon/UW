<template>
  <div class="main-details mt-1 mb-3">
    <datatable
      v-bind="$data"
    ></datatable>
  </div>
</template>

<script>
  import {userAddUrl, userUpdateUrl, userSelectUrl} from "../../../config/globalUrl";
  import {mapGetters, mapActions} from 'vuex'
  import {axiosPost} from "../../../utils/fetchData";
  import {errHandler} from "../../../utils/errorHandler";
  import UserOperation from "./UserOperation"
  export default {
    name: "UserDetails",
    components: {
      UserOperation
    },
    data() {
      return {
        fixHeaderAndSetBodyMaxHeight: 650,
        tblStyle: {
          'word-break': 'break-all',
          'table-layout': 'fixed'
        },
        HeaderSettings: false,
        pageSizeOptions: [20, 40],
        data: [],
        columns: [
          {field: 'showId', title: '序号', colStyle: {'width': '60px'}},
          {field: 'uid', title: '用户名', colStyle: {'width': '100px'}},
          {field: 'name', title: '用户描述', colStyle: {'width': '100px'}},
          {field: 'type', title: '用户类型', colStyle: {'width': '100px'}, visible: false},
          {field: 'typeString', title: '用户类型', colStyle: {'width': '100px'}},
          {field: 'enabled', title: '是否启用', colStyle: {'width': '100px'}, visible: false},
          {field: 'enabledString', title: '是否启用', colStyle:{'width': '100px'}},
          {title: '操作', tdComp: 'UserOperation', colStyle: {'width': '100px'}}
        ],
        total: 0,
        query: {"limit": 20, "offset": 0},
        isPending: false,
        filter: ''
      }
    },
    created() {
      this.init();
    },
    mounted () {
      this.thisFetch(this.$route.query)

    },
    watch: {
      $route: function (route) {
        this.setLoading(true);
        if (route.query.filter) {
          let options = {
            url: userSelectUrl,
            data: {
              filter: route.query.filter
            }
          };
          this.fetchData(options)
        } else {
          let options = {
            url: userSelectUrl
          };
          this.fetchData(options)
        }
      },
      query: {
        handler(query) {
          this.setLoading(true);
          this.dataFilter(query);
        },
        deep: true
      }
    },
    methods: {
      ...mapActions(['setLoading']),
      init: function () {
        this.data = [];
        this.total = 0;
        this.query = {"limit": 20, "offset": 0}
      },
      thisFetch: function (opt) {
        let options = {
          url: userSelectUrl,
          data: {}
        };
        this.fetchData(options);
      },
      fetchData: function (opt) {
        if (!this.isPending) {
          this.isPending = true;
          axiosPost(opt).then(response => {
            this.isPending = false;
            this.setLoading(false);
            if (response.data.result === 200) {
              this.data = response.data.data.list;
              this.data.map((item, index) => {
                item.showId = index + 1 + this.query.offset;
              });
              this.total = response.data.data.totalRow;
            } else {
              this.isPending = false;
              errHandler(response.data.result)
            }
          })
            .catch(err => {
              this.isPending = false;
              console.log(JSON.stringify(err));
              alert('请求超时，清刷新重试')
            })
        } else {
          this.setLoading(false)
        }
      },
      dataFilter: function () {
        let options = {
          url: userSelectUrl,
          data: {}
        };
        options.data.pageNo = this.query.offset / this.query.limit + 1;
        options.data.pageSize = this.query.limit;
        if (this.filter !== "") {
          options.data.filter = this.filter
        }
        this.fetchData(options);
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
</style>
