<!--物料管理-->
<template>
  <div class="main-details mt-1 mb-3">
    <datatable
      v-bind="$data"
    ></datatable>
  <entity-details v-if="isDetailsActive"/>
  </div>
</template>

<script>
  import {axiosPost} from "../../../../utils/fetchData";
  import {mapGetters, mapActions} from 'vuex'
  import {materialAddUrl, materialCountUrl, materialEntityUrl, materialUpdateUrl} from "../../../../config/globalUrl";
  import {errHandler} from "../../../../utils/errorHandler";
  import OperationOptions from "./subscomp/OperationOptions";
  import EntityDetails from './EntityDetails'
  export default {
    name: "Details",
    components: {
      OperationOptions,
      EntityDetails
    },
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
        columns: [],
        total: 0,
        query: {"limit": 20, "offset": 0},
        isPending: false,
        thisRouter: '',
        filter: '',
      }
    },
    created() {
      this.init();
      let options = {
        url: materialCountUrl,
        data: {
          pageNo: 1,
          pageSize: 20
        }
      };
      this.fetchData(options)
    },
    computed: {
      ...mapGetters([
        'isDetailsActive'
      ]),

    },
    watch: {
      $route: function (route) {
        this.init();
        this.setLoading(true);
        let options = {
          url: materialCountUrl,
          data: {
            pageNo: 1,
            pageSize: 20
          }
        };
        if (route.query.filter) {
          this.filter = route.query.filter;
          options.data.filter = this.filter
        } else {
          this.filter = "";
        }
        this.fetchData(options)

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
      ...mapActions(['setTableRouter', 'setLoading']),
      init: function () {
        this.data = [];
        this.columns = [
          {field: 'showId', title: '序号', colStyle: {'width': '70px'}},
          {field: 'id', title: '序号', visible: false},
          {field: 'no', title: '料号', colStyle: {'width': '120px'}},
          {field: 'area', title: '所在区域', colStyle: {'width': '80px'}},
          {field: 'row', title: '列号', colStyle: {'width': '70px'}},
          {field: 'col', title: '行号', colStyle: {'width': '70px'}},
          {field: 'height', title: '高度', colStyle: {'width': '70px'}},
          {field: 'enabled', title: '可用性', visible: false},
          {field: 'enabledString', title: '是否可用', colStyle: {'width': '70px'}, visible: false},
          {field: 'quantity', title: '数量', colStyle: {'width': '70px'}},
          {title: '操作', tdComp: 'OperationOptions', colStyle: {'width': '80px'} }

        ];
        this.total = 0;
        this.query = {"limit": 20, "offset": 0}
      },
      fetchData: function (options) {
        if (!this.isPending) {
          this.isPending = true;
          axiosPost(options).then(response => {
            this.setLoading(false);
            this.isPending = false;
            if (response.data.result === 200) {
              this.data = response.data.data.list;
              this.data.map((item, index) => {
                item.showId = index + 1 + this.query.offset;
              });
              this.total = response.data.data.totalRow
            } else {
              errHandler(response.data.result)
            }
          })
            .catch(err => {
              this.isPending = false;
              console.log(JSON.stringify(err));
              alert('请求超时，请刷新重试')
            })
        }
      },
      dataFilter: function () {
        let options = {
          url: materialCountUrl,
          data: {
          }
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
