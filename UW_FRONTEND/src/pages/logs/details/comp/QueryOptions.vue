<!--表单查看页面的条件过滤栏-->

<template>
  <div class="options-area">
    <div class="form-row">
      <div v-for="item in queryOptions" class="row no-gutters pl-3 pr-3">
        <component :opt="item" :is="item.type + '-comp'" :callback="thisFetch"></component>
      </div>
      <div class="form-group row align-items-end">
        <div class="btn btn-secondary ml-3 mr-4" @click="initForm">清空条件</div>
      </div>
      <div class="form-group row align-items-end">
        <div class="btn btn-primary ml-3 mr-4" @click="thisFetch">查询</div>
      </div>
    </div>
  </div>
</template>

<script>
  import {mapGetters, mapActions} from 'vuex';
  import {logsUrl} from "../../../../config/globalUrl";
  import {axiosPost} from "../../../../utils/fetchData";
  import {getLogsQuery} from "../../../../config/logsApiConfig";
  import {Settings} from 'luxon'
  import {Datetime} from 'vue-datetime'
  import 'vue-datetime/dist/vue-datetime.css'
  import _ from 'lodash'

  export default {
    name: "Options",
    components: {
      'text-comp': {
        props: ['opt', 'callback'],
        template: '<div class="form-group col pr-3"">\n' +
        '           <label :for="opt.id">{{opt.name}}：</label>\n' +
        '           <input type="text" class="form-control" :id="opt.id" v-model="opt.model" @keyup.enter="callback">\n' +
        '          </div>'
      },
      'date-comp': {
        props: ['opt'],
        components: {
          Datetime
        },
        template: '<div class="row">\n' +
        '    <div class="form-group col pr-3">\n' +
        '      <label>时间  从：</label>\n' +
        '      <datetime v-model="opt.modelFrom" type="datetime" zone="Asia/Shanghai" value-zone="Asia/Shanghai" />\n' +
        '    </div>\n' +
        '    <div class="form-group col pr-3">\n' +
        '      <label>至：</label>\n' +
        '      <datetime v-model="opt.modelTo" type="datetime" zone="Asia/Shanghai" value-zone="Asia/Shanghai" />\n' +
        '    </div>\n' +
        '  </div>'

      }
    },
    data() {
      return {
        // pageSize: 2000,
        queryOptions: [],
        copyQueryOptions: [],
        queryString: ""
      }
    },
    mounted: function () {
      Settings.defaultLocale = 'zh-CN';
      if (this.$store.state.logsRouterApi !== 'default') {
        this.initForm()
      }
    },
    computed: {
      ...mapGetters([
        'logsRouterApi'
      ]),
    },
    watch: {
      logsRouterApi: function (val) {
        this.initForm(val);
      }
    },
    methods: {
      ...mapActions(['setLoading']),
      initForm: function () {
        let queryOptions = getLogsQuery(this.logsRouterApi);
        this.queryOptions = JSON.parse(JSON.stringify(queryOptions));
      },
      createQueryString: function () {
        this.queryString = "";
        this.copyQueryOptions = this.queryOptions.filter((item) => {
          if (!(item.model === "" || item.modelFrom === "" || item.modelTo === "")) {
            return true;
          }
        });

        this.copyQueryOptions.map((item, index) => {
          if (item.type === 'text') {
            if (_.trim(item.model) !== "") {
              if (index === 0) {
                this.queryString += (item.id + "=" + _.trim(item.model))
              } else {
                this.queryString += ("&" + item.id + "=" + _.trim(item.model))
              }

            } else {
              this.setLoading(false)
            }
          } else if (item.type === 'date') {
            if (item.modelFrom !== '' && item.modelTo !== '') {
              let tempFrom = item.modelFrom.replace('T', ' ').replace('Z', '');
              let tempTo = item.modelTo.replace('T', ' ').replace('Z', '');
              if (this.compareDate(tempFrom, tempTo) >= 0) {
                if (index === 0) {
                  this.queryString += (item.id + '>=' + tempFrom + '&' + item.id + '<=' + tempTo)
                } else {
                  this.queryString += ('&' + item.id + '>=' + tempFrom + '&' + item.id + '<=' + tempTo)
                }
              } else {
                alert('日期格式错误');
                this.setLoading(false)
              }
            }
          }

        })
      },
      fetchData: function () {
        let options = {
          url: logsUrl,
          data: {
            table: this.$store.state.logsRouterApi,
            pageNo: 1,
            pageSize: 20
          }
        };
        if (this.queryString !== "") {
          options.data.filter = this.queryString
        }
        this.$router.replace('_empty');
        this.$router.push({
          path: '/logs/' + this.$store.state.logsRouterApi,
          query: options
        }, () => {
          this.setLoading(true);
        })

      },
      thisFetch: function () {
        this.createQueryString();
        this.fetchData()
      },
      compareDate: function (dateFrom, dateTo) {
        let compFrom = new Date(dateFrom);
        let compTo = new Date(dateTo);
        return (compTo - compFrom);
      }
    }
  }
</script>

<style scoped>
  .options-area {
    background: #fff;
    border: 1px solid #eeeeee;
    border-radius: 8px;
    padding: 10px;
  }
</style>
