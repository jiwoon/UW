<!--表单查看页面的表单详情 统一配置-->
<template>
  <div class="main-details mt-1 mb-3">
    <datatable
      v-bind="$data"
    ></datatable>
  </div>
</template>

<script>
  import {axiosPost} from "../../../../utils/fetchData";
  import {mapGetters, mapActions} from 'vuex'
  import {logsUrl} from "../../../../config/globalUrl";
  import {errHandler} from "../../../../utils/errorHandler";
  import {getLogsConfig} from "../../../../config/logsApiConfig";

  export default {
    name: "Details",
    components: {},
    data() {
      return {
        fixHeaderAndSetBodyMaxHeight: 650,
        tblStyle: {
          'word-break': 'break-all',
          'table-layout': 'fixed'

        },
        HeaderSettings: false,
        pageSizeOptions: [20, 40, 80, 100],
        data: [],
        //srcData: [],
        columns: [
          {}
        ],
        total: 0,
        query: {"limit": 20, "offset": 0},
        isPending: false,
        thisRouter: '',
        filter: ''
      }
    },
    created() {
      this.init();
      this.thisFetch(this.$route.query);
      this.thisRouter = this.$route.query.page;
    },
    computed: {
      ...mapGetters([
        'logsRouterApi'
      ]),

    },
    watch: {
      $route: function (route) {
        this.init();
        this.setLoading(true);
        if (route.query.data) {
          this.fetchData(route.query);
          this.filter = route.query.data.filter;
          this.thisRouter = route.query.data.table;

        } else {
          let options = {
            url: logsUrl,
            data: {
              table: route.query.page,
              pageNo: 1,
              pageSize: 20,
              descBy: 'time'
            }
          };
          this.fetchData(options);
          this.filter = "";
          this.thisRouter = route.query.page;
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
    mounted: function () {
    },
    methods: {
      ...mapActions(['setLogsRouter', 'setLoading']),
      init: function () {
        this.data = [];
        //this.srcData = [];
        this.columns = [];
        this.total = 0;
        this.thisRouter = '';
        this.query = {"limit": 20, "offset": 0};
        this.setLogsRouter('default');

      },
      thisFetch: function (opt) {
        let options = {
          url: logsUrl,
          data: {
            table: opt.page,
            pageNo: 1,
            pageSize: 20,
            descBy: 'time'
          }
        };
        this.fetchData(options)
      },
      fetchData: function (options) {
        if (!this.isPending) {
          this.isPending = true;
          this.columns = getLogsConfig(options.data.table);
          this.setLogsRouter(options.data.table);
          axiosPost(options).then(response => {
            this.isPending = false;
            if (response.data.result === 200) {
              this.data = response.data.data.list;
              this.data.map((item, index) => {
                item.showId = index + 1 + this.query.offset;
              });
              this.total = response.data.data.totalRow;
              this.setLoading(false);
            } else {
              errHandler(response.data.result)
              this.setLoading(false);
            }
          })
            .catch(err => {
              this.isPending = false;
              console.log(JSON.stringify(err));
              alert('请求超时，请刷新重试')
              this.setLoading(false);
            })
        }
      },
      dataFilter: function () {
        let options = {
          url: logsUrl,
          data: {
            table: this.thisRouter,
            descBy: 'time'
          }
        };
        options.data.pageNo = this.query.offset / this.query.limit + 1;
        options.data.pageSize = this.query.limit;
        if (this.filter !== "") {
          options.data.filter = this.filter
        }
        this.fetchData(options);
        //this.data = this.srcData.slice(this.query.offset, this.query.offset + this.query.limit);
        // this.data.map((item, index) => {
        //   item.showId = index + 1 + this.query.offset;
        // })
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
