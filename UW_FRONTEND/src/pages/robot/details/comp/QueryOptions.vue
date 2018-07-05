
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
  import eventBus from '@/utils/eventBus'
  import {mapGetters, mapActions} from 'vuex';
  import {robotSelectUrl} from "../../../../config/globalUrl";
  import _ from 'lodash'

  export default {
    name: "QueryOptions",
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
      }
    },
    data() {
      return {
        // pageSize: 2000,
        queryOptions: [
          {
            id: 'status',
            name: '状态',
            model: '',
            type: 'select',
            list: [
              {
                value: '0',
                string: '闲置'
              },
              {
                value: '1',
                string: '忙碌'
              },
              {
                value: '3',
                string: '错误'
              },
              {
                value: '4',
                string: '充电'
              }
            ]
          },
          {
            id: 'enabled',
            name: '是否启用',
            model: '',
            type: 'select',
            list: [
              {
                value: '0',
                string: '停用'
              },
              {
                value: '1',
                string: '启用'
              }
            ]
          }
        ],
        copyQueryOptions: [],
        queryString: ""
      }
    },
    mounted: function () {
      this.initForm();
    },
    computed: {
    },
    watch: {},
    methods: {
      ...mapActions(['setLoading']),
      initForm: function () {
        this.queryOptions.map(item => {
          item.model = "";
        })
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
          path: '/robot',
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
