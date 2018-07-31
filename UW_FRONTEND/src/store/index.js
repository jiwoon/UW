import Vue from 'vue'
import Vuex from 'vuex'
import * as mutations from './mutations'
import * as actions from './actions'
import * as getters from './getters'

Vue.use(Vuex);

const state = {
  /*登录回传标识 用于请求中验证*/
  token: '',

  /*加载状态 用于控制'@/components/Loading.vue显示'*/
  isLoading: false,

  /*日志页面的路由地址，用于区分各个日志分页*/
  logsRouterApi: 'default',

  /*物料添加、编辑状态，用于控制输入框显示*/
  isMaterialAdding: false,
  isMaterialEditing: false,

  /*物料编辑时的页面行内容以及其副本*/
  editData: [],
  copyData: [],

  /*物料页面行数据 用于查询其详情*/
  materialDetails: '',
  /*用于控制物料详情面板显示*/
  isDetailsActive: false,

  /*任务页面行数据 用于查询其详情*/
  taskDetails: {},
  /*用于控制任务详情面板显示*/
  isTaskDetailsActive: false,

  /*缓存用户类型列表*/
  userTypeList: {},

  /*缓存出入库页面运行仓口ID*/
  currentWindowId: ''
};

const store = new Vuex.Store({
  state,
  getters,
  actions,
  mutations
});

export default store;
