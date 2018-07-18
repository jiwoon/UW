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
      <div class="form-group row align-items-end">
        <div class="btn btn-primary ml-3 mr-4" @click="isUploading = !isUploading">创建任务</div>
      </div>
    </div>
    <div v-if="isUploading" id="upload-window">
      <upload-task></upload-task>
    </div>
  </div>
</template>

<script>
  import UploadTask from './subscomp/UploadTask'
  import eventBus from '@/utils/eventBus'
  import {mapGetters, mapActions} from 'vuex';
  import {taskSelectUrl} from "../../../../config/globalUrl";
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
        '      <label>创建时间  从：</label>\n' +
        '      <datetime v-model="opt.modelFrom" type="datetime" zone="Asia/Shanghai" value-zone="Asia/Shanghai"/>\n' +
        '    </div>\n' +
        '    <div class="form-group col pr-3">\n' +
        '      <label>至：</label>\n' +
        '      <datetime v-model="opt.modelTo" type="datetime" zone="Asia/Shanghai" value-zone="Asia/Shanghai"/>\n' +
        '    </div>\n' +
        '  </div>'

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
      UploadTask
    },
    data() {
      return {
        // pageSize: 2000,
        queryOptions: [
          {
            id: 'state',
            name: '状态',
            model: '',
            type: 'select',
            list: [
              {
                value: '0',
                string: '未审核'
              },
              {
                value: '1',
                string: '未开始'
              },
              {
                value: '2',
                string: '进行中'
              },
              {
                value: '3',
                string: '已完成'
              },
              {
                value: '4',
                string: '已作废'
              }
            ]
          },
          {
            id: 'type',
            name: '类型',
            model: '',
            type: 'select',
            list: [
              {
                value: '0',
                string: '入库'
              },
              {
                value: '1',
                string: '出库'
              },
              {
                value: '2',
                string: '盘点'
              },
              {
                value: '3',
                string: '位置优化'
              }
            ]
          },
          {
            id: 'fileName',
            name: '文件名',
            model: '',
            type: 'text'
          },
          {
            id: 'createTimeString',
            name: '创建时间',
            modelFrom: '',
            modelTo: '',
            type: 'date'
          }
        ],
        copyQueryOptions: [],
        queryString: "",
        isUploading: false
      }
    },
    mounted: function () {
      this.initForm();
      eventBus.$on('closeUploadPanel', () => {
        this.isUploading = false;
      })
    },
    computed: {
    },
    watch: {},
    methods: {
      ...mapActions(['setLoading']),
      initForm: function () {
        this.queryOptions = [
          {
            id: 'state',
            name: '状态',
            model: '',
            type: 'select',
            list: [
              {
                value: '0',
                string: '未审核'
              },
              {
                value: '1',
                string: '未开始'
              },
              {
                value: '2',
                string: '进行中'
              },
              {
                value: '3',
                string: '已完成'
              },
              {
                value: '4',
                string: '已作废'
              }
            ]
          },
          {
            id: 'type',
            name: '类型',
            model: '',
            type: 'select',
            list: [
              {
                value: '0',
                string: '入库'
              },
              {
                value: '1',
                string: '出库'
              },
              {
                value: '2',
                string: '盘点'
              },
              {
                value: '3',
                string: '位置优化'
              }
            ]
          },
          {
            id: 'fileName',
            name: '文件名',
            model: '',
            type: 'text'
          },
          {
            id: 'createTimeString',
            name: '创建时间',
            modelFrom: '',
            modelTo: '',
            type: 'date'
          }
        ]
      },
      createQueryString: function () {
        this.queryString = "";
        this.copyQueryOptions = this.queryOptions.filter((item) => {
          if (!(item.model === "" || item.modelFrom === "" || item.modelTo === "")) {
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
                this.setLoading(false);
                return
              }
            }
          }

        })
      },
      fetchData: function () {
        let options = {
          path: '/tasks',
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

  #upload-window {
    z-index: 100;
  }

  .fade-enter-active, .fade-leave-active {
    transition: opacity .5s;
  }

  .fade-enter, .fade-leave-to {
    opacity: 0;
  }
</style>
